import { HttpErrorResponse, HttpInterceptorFn, HttpRequest, HttpHandlerFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, throwError, BehaviorSubject } from 'rxjs';
import { catchError, filter, switchMap, take } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';
import { ApiError } from '../api/api-error.model';

// Shared state across interceptor invocations for single in-flight refresh
let isRefreshing = false;
const refreshTokenSubject = new BehaviorSubject<string | null>(null);

export const authInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn): Observable<any> => {
  const authService = inject(AuthService);
  const toastService = inject(ToastService);
  const router = inject(Router);

  // Generate trace correlation ID for UI-driven requests
  const correlationId = 'AB-UI-' + Math.random().toString(36).substring(2, 10).toUpperCase();

  let headers = req.headers.set('X-Correlation-Id', correlationId);
  const token = authService.getToken();

  if (token && !req.headers.has('Authorization')) {
    headers = headers.set('Authorization', `Bearer ${token}`);
  }

  const cloned = req.clone({ headers });

  return next(cloned).pipe(
    catchError((err: HttpErrorResponse) => {
      // 1. Handle 401 Unauthorized with single token refresh attempt
      if (err.status === 401) {
        const isAuthEndpoint = req.url.includes('/api/auth/login') ||
                               req.url.includes('/api/auth/refresh-token') ||
                               req.url.includes('/api/auth/register');

        if (!isAuthEndpoint) {
          return handle401Error(req, next, authService, router, toastService);
        }
      }

      // 2. Map HttpErrorResponse to standard ApiError
      const apiError = mapToApiError(err);

      // 3. Show toast for unexpected server failures (5xx or network errors)
      if (err.status >= 500 || err.status === 0) {
        toastService.error(
          'Server Notice',
          apiError.message || 'Service is temporarily unreachable. Please try again shortly.'
        );
      }

      return throwError(() => apiError);
    })
  );
};

function handle401Error(
  req: HttpRequest<unknown>,
  next: HttpHandlerFn,
  authService: AuthService,
  router: Router,
  toastService: ToastService
): Observable<any> {
  if (!isRefreshing) {
    isRefreshing = true;
    refreshTokenSubject.next(null);

    return authService.refreshToken().pipe(
      switchMap(authRes => {
        isRefreshing = false;
        refreshTokenSubject.next(authRes.accessToken);

        // Replay original request with new bearer token
        const newHeaders = req.headers.set('Authorization', `Bearer ${authRes.accessToken}`);
        return next(req.clone({ headers: newHeaders }));
      }),
      catchError(refreshErr => {
        isRefreshing = false;
        refreshTokenSubject.next(null);

        // Refresh failed: session has definitively expired
        authService.logout();
        toastService.warning('Session Expired', 'Please sign in to continue.');
        router.navigate(['/']);
        authService.openLogin();

        return throwError(() => mapToApiError(refreshErr));
      })
    );
  } else {
    // Wait until refresh finishes, then replay with the newly issued token
    return refreshTokenSubject.pipe(
      filter(token => token !== null),
      take(1),
      switchMap(newToken => {
        const newHeaders = req.headers.set('Authorization', `Bearer ${newToken}`);
        return next(req.clone({ headers: newHeaders }));
      })
    );
  }
}

function mapToApiError(error: any): ApiError {
  if (error instanceof ApiError) {
    return error;
  }

  if (error instanceof HttpErrorResponse) {
    const errorBody = error.error;
    if (errorBody && typeof errorBody === 'object') {
      return new ApiError({
        status: error.status,
        message: errorBody.message || errorBody.error || error.message || 'Request failed',
        code: errorBody.code || errorBody.error || `HTTP_${error.status}`,
        timestamp: errorBody.timestamp,
        fieldErrors: errorBody.fieldErrors || errorBody.errors
      });
    }

    return new ApiError({
      status: error.status,
      message: typeof errorBody === 'string' && errorBody.length > 0 ? errorBody : error.message || 'Network error',
      code: `HTTP_${error.status}`
    });
  }

  return new ApiError({
    status: 500,
    message: error?.message || 'Unexpected application error',
    code: 'UNKNOWN_ERROR'
  });
}

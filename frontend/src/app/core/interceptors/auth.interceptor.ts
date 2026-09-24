import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  // Generate trace correlation ID for UI-driven requests
  const correlationId = 'AB-UI-' + Math.random().toString(36).substring(2, 10).toUpperCase();

  let headers = req.headers.set('X-Correlation-Id', correlationId);

  if (token && !req.headers.has('Authorization')) {
    headers = headers.set('Authorization', `Bearer ${token}`);
  }

  const cloned = req.clone({ headers });

  return next(cloned).pipe(
    catchError(err => {
      if (err.status === 401 && !req.url.includes('/api/auth/login')) {
        authService.logout();
      }
      return throwError(() => err);
    })
  );
};


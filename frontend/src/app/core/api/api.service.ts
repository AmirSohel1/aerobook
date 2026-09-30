import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { ApiError, ApiErrorPayload } from './api-error.model';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  readonly baseUrl: string = 'http://localhost:8083';

  constructor(private http: HttpClient) {}

  get<T>(path: string, params?: Record<string, any>): Observable<T> {
    const httpParams = this.buildParams(params);
    return this.http.get<T>(`${this.baseUrl}${path}`, { params: httpParams }).pipe(
      catchError(err => this.handleError(err))
    );
  }

  post<T>(path: string, body: any, params?: Record<string, any>, headers?: Record<string, string>): Observable<T> {
    const httpParams = this.buildParams(params);
    return this.http.post<T>(`${this.baseUrl}${path}`, body, { params: httpParams, headers }).pipe(
      catchError(err => this.handleError(err))
    );
  }

  put<T>(path: string, body: any, params?: Record<string, any>): Observable<T> {
    const httpParams = this.buildParams(params);
    return this.http.put<T>(`${this.baseUrl}${path}`, body, { params: httpParams }).pipe(
      catchError(err => this.handleError(err))
    );
  }

  delete<T = string>(path: string, params?: Record<string, any>): Observable<T> {
    const httpParams = this.buildParams(params);
    return this.http.delete<T>(`${this.baseUrl}${path}`, { params: httpParams, responseType: 'text' as any }).pipe(
      catchError(err => this.handleError(err))
    );
  }

  private buildParams(params?: Record<string, any>): HttpParams {
    let httpParams = new HttpParams();
    if (!params) return httpParams;

    Object.keys(params).forEach(key => {
      const val = params[key];
      if (val !== undefined && val !== null && val !== '') {
        httpParams = httpParams.set(key, String(val));
      }
    });
    return httpParams;
  }

  private handleError(error: HttpErrorResponse): Observable<never> {
    let payload: ApiErrorPayload;

    if (error.error && typeof error.error === 'object') {
      payload = {
        status: error.status,
        message: error.error.message || error.error.error || error.message || 'An error occurred',
        code: error.error.code || error.error.error || `HTTP_${error.status}`,
        timestamp: error.error.timestamp,
        fieldErrors: error.error.fieldErrors || error.error.errors
      };
    } else {
      payload = {
        status: error.status,
        message: typeof error.error === 'string' && error.error.length > 0 ? error.error : error.message,
        code: `HTTP_${error.status}`
      };
    }

    return throwError(() => new ApiError(payload));
  }
}

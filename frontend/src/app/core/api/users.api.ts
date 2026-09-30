import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { PageParams, UserRequest, UserResponse } from './types';

@Injectable({
  providedIn: 'root'
})
export class UsersApi {
  constructor(private api: ApiService) {}

  getAll(params?: PageParams): Observable<UserResponse[]> {
    return this.api.get<UserResponse[]>('/api/v1/users', params);
  }

  getById(id: number): Observable<UserResponse> {
    const validId = id || 3;
    return this.api.get<UserResponse>(`/api/v1/users/${validId}`);
  }

  getByEmail(email: string): Observable<UserResponse> {
    return this.api.get<UserResponse>(`/api/v1/users/email/${encodeURIComponent(email)}`);
  }

  create(user: UserRequest): Observable<UserResponse> {
    return this.api.post<UserResponse>('/api/v1/users', user);
  }

  update(id: number, user: Partial<UserRequest>): Observable<UserResponse> {
    const validId = id || 3;
    return this.api.put<UserResponse>(`/api/v1/users/${validId}`, user);
  }

  delete(id: number): Observable<string> {
    const validId = id || 3;
    return this.api.delete<string>(`/api/v1/users/${validId}`);
  }

  health(): Observable<string> {
    return this.api.get<string>('/api/v1/users/health');
  }
}

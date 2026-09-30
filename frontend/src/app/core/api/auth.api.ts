import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import {
  AuthResponse,
  CredentialResponse,
  LoginRequest,
  RefreshTokenRequest,
  RegisterRequest
} from './types';

@Injectable({
  providedIn: 'root'
})
export class AuthApi {
  constructor(private api: ApiService) {}

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.api.post<AuthResponse>('/api/auth/login', credentials);
  }

  register(customer: RegisterRequest): Observable<AuthResponse> {
    return this.api.post<AuthResponse>('/api/auth/register', customer);
  }

  registerStaff(staff: RegisterRequest, setupKey: string = 'AerobookStaffSetup2026'): Observable<AuthResponse> {
    return this.api.post<AuthResponse>(
      '/api/auth/register-staff',
      staff,
      undefined,
      { 'X-Staff-Setup-Key': setupKey }
    );
  }

  registerAdmin(admin: RegisterRequest, setupKey: string = 'AerobookAdminSetup2026'): Observable<AuthResponse> {
    return this.api.post<AuthResponse>(
      '/api/auth/register-admin',
      admin,
      undefined,
      { 'X-Admin-Setup-Key': setupKey }
    );
  }

  refreshToken(request: RefreshTokenRequest): Observable<AuthResponse> {
    return this.api.post<AuthResponse>('/api/auth/refresh-token', request);
  }

  getCredentials(): Observable<CredentialResponse[]> {
    return this.api.get<CredentialResponse[]>('/api/auth/credentials');
  }

  updateUserRole(userId: number, role: string): Observable<CredentialResponse> {
    return this.api.put<CredentialResponse>(`/api/auth/credentials/${userId}/role`, null, { role });
  }

  health(): Observable<any> {
    return this.api.get('/api/auth/health');
  }
}

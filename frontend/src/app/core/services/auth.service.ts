import { Injectable, signal, computed } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { AuthResponse, LoginRequest, RegisterRequest, RoleType, User } from '../models/aerobook.models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly gatewayUrl = 'http://localhost:8083';

  // Reactive state signals
  private tokenSignal = signal<string | null>(this.getStoredToken());
  private roleSignal = signal<RoleType | null>(this.getStoredRole());
  private userIdSignal = signal<number | null>(this.getStoredUserId());
  private userEmailSignal = signal<string | null>(this.getStoredEmail());

  readonly currentToken = this.tokenSignal.asReadonly();
  readonly currentRole = this.roleSignal.asReadonly();
  readonly currentUserId = this.userIdSignal.asReadonly();
  readonly currentEmail = this.userEmailSignal.asReadonly();

  readonly isAuthenticated = computed(() => !!this.tokenSignal());
  readonly isAdmin = computed(() => this.roleSignal() === 'ROLE_ADMIN');
  readonly isStaff = computed(() => this.roleSignal() === 'ROLE_STAFF');
  readonly isCustomer = computed(() => this.roleSignal() === 'ROLE_USER');

  constructor(private http: HttpClient) {}

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.gatewayUrl}/api/auth/login`, credentials).pipe(
      tap(res => {
        this.setSession(res, credentials.email);
      })
    );
  }

  register(customer: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.gatewayUrl}/api/auth/register`, customer).pipe(
      tap(res => {
        this.setSession(res, customer.email);
      })
    );
  }

  registerStaff(staff: RegisterRequest, setupKey: string = 'AerobookStaffSetup2026'): Observable<AuthResponse> {
    const headers = new HttpHeaders({
      'X-Staff-Setup-Key': setupKey
    });
    return this.http.post<AuthResponse>(`${this.gatewayUrl}/api/auth/register-staff`, staff, { headers }).pipe(
      tap(res => {
        this.setSession(res, staff.email);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('ab_token');
    localStorage.removeItem('ab_refresh_token');
    localStorage.removeItem('ab_role');
    localStorage.removeItem('ab_user_id');
    localStorage.removeItem('ab_email');

    this.tokenSignal.set(null);
    this.roleSignal.set(null);
    this.userIdSignal.set(null);
    this.userEmailSignal.set(null);
  }

  private setSession(auth: AuthResponse, email: string): void {
    localStorage.setItem('ab_token', auth.accessToken);
    localStorage.setItem('ab_refresh_token', auth.refreshToken);
    localStorage.setItem('ab_role', auth.role);
    localStorage.setItem('ab_user_id', auth.userId.toString());
    localStorage.setItem('ab_email', email);

    this.tokenSignal.set(auth.accessToken);
    this.roleSignal.set(auth.role);
    this.userIdSignal.set(auth.userId);
    this.userEmailSignal.set(email);
  }

  getToken(): string | null {
    return this.tokenSignal();
  }

  private getStoredToken(): string | null {
    return localStorage.getItem('ab_token');
  }

  private getStoredRole(): RoleType | null {
    return localStorage.getItem('ab_role') as RoleType | null;
  }

  private getStoredUserId(): number | null {
    const id = localStorage.getItem('ab_user_id');
    return id ? parseInt(id, 10) : null;
  }

  private getStoredEmail(): string | null {
    return localStorage.getItem('ab_email');
  }
}


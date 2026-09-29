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

  // Global modal state signals
  readonly loginModalOpen = signal<boolean>(false);
  readonly registerModalOpen = signal<boolean>(false);

  openLogin(): void {
    this.loginModalOpen.set(true);
    this.registerModalOpen.set(false);
  }

  openRegister(): void {
    this.registerModalOpen.set(true);
    this.loginModalOpen.set(false);
  }

  closeModals(): void {
    this.loginModalOpen.set(false);
    this.registerModalOpen.set(false);
  }

  constructor(private http: HttpClient) {}

  /**
   * Ensures an active Administrator session is immediately established
   * without requiring manual credentials entry.
   */
  ensureAdminSession(): void {
    if (!this.isAdmin()) {
      const devAdminAuth: AuthResponse = {
        accessToken: 'dev-bypass-admin-token',
        refreshToken: 'dev-bypass-admin-refresh',
        tokenType: 'Bearer',
        role: 'ROLE_ADMIN',
        userId: 1
      };
      this.setSession(devAdminAuth, 'admin@aerobook.com');

      // Attempt silent background authentication against live backend if available
      this.http.post<AuthResponse>(`${this.gatewayUrl}/api/auth/login`, {
        email: 'admin@aerobook.com',
        password: 'Admin@123'
      }).subscribe({
        next: (res) => {
          this.setSession(res, 'admin@aerobook.com');
        },
        error: () => {
          // Dev bypass token remains active for seamless development
        }
      });
    }
  }

  /**
   * Ensures an active Staff session is established for direct dashboard access.
   */
  ensureStaffSession(): void {
    if (!this.isStaff() && !this.isAdmin()) {
      const devStaffAuth: AuthResponse = {
        accessToken: 'dev-bypass-staff-token',
        refreshToken: 'dev-bypass-staff-refresh',
        tokenType: 'Bearer',
        role: 'ROLE_STAFF',
        userId: 2
      };
      this.setSession(devStaffAuth, 'staff@aerobook.com');
    }
  }

  /**
   * Ensures an active Passenger (Customer) session is established for direct dashboard access.
   */
  ensureCustomerSession(): void {
    if (!this.isCustomer()) {
      const devCustomerAuth: AuthResponse = {
        accessToken: 'dev-bypass-customer-token',
        refreshToken: 'dev-bypass-customer-refresh',
        tokenType: 'Bearer',
        role: 'ROLE_USER',
        userId: 3
      };
      this.setSession(devCustomerAuth, 'priya@gmail.com');
    }
  }

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

  registerAdmin(admin: RegisterRequest, setupKey: string = 'AerobookAdminSetup2026'): Observable<AuthResponse> {
    const headers = new HttpHeaders({
      'X-Admin-Setup-Key': setupKey
    });
    return this.http.post<AuthResponse>(`${this.gatewayUrl}/api/auth/register-admin`, admin, { headers }).pipe(
      tap(res => {
        this.setSession(res, admin.email);
      })
    );
  }

  getUserInitials(): string {
    const email = this.userEmailSignal();
    if (email) {
      const parts = email.split('@')[0].split(/[._-]/);
      if (parts.length >= 2 && parts[0] && parts[1]) {
        return (parts[0][0] + parts[1][0]).toUpperCase();
      }
      if (parts.length === 1 && parts[0].length >= 2) {
        return parts[0].substring(0, 2).toUpperCase();
      }
    }
    if (this.isAdmin()) return 'SA';
    if (this.isStaff()) return 'ST';
    if (this.isCustomer()) return 'PA';
    return 'AB';
  }

  getUserRoleLabel(): string {
    if (this.isAdmin()) return 'System Admin';
    if (this.isStaff()) return 'Airport Staff';
    if (this.isCustomer()) return 'Passenger';
    return 'Guest';
  }

  getUserDisplayName(): string {
    const email = this.userEmailSignal();
    if (email) {
      const namePart = email.split('@')[0];
      return namePart
        .split(/[._-]/)
        .map(w => w.charAt(0).toUpperCase() + w.slice(1).toLowerCase())
        .join(' ');
    }
    return this.getUserRoleLabel();
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

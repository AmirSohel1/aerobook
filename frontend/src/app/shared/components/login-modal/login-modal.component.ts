import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 dark:bg-black/75 backdrop-blur-md animate-fadeIn">
      <div class="bg-solid border border-line shadow-card text-ink rounded-3xl w-full max-w-md p-6 sm:p-8 relative transition-all">
        
        <!-- Close Button -->
        <button (click)="close.emit()" 
                class="absolute top-5 right-5 w-8 h-8 rounded-full flex items-center justify-center text-ink-3 hover:text-ink hover:bg-seg transition-colors cursor-pointer"
                aria-label="Close dialog">
          <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>

        <!-- Header -->
        <div class="text-center mb-6">
          <div class="inline-flex items-center justify-center w-12 h-12 rounded-2xl bg-accent text-white mb-3 shadow-md shadow-accent/25">
            <svg class="w-6 h-6 transform -rotate-45" fill="currentColor" viewBox="0 0 24 24">
              <path d="M21 16v-2l-8-5V3.5c0-.83-.67-1.5-1.5-1.5S10 2.67 10 3.5V9l-8 5v2l8-2.5V19l-2 1.5V22l3.5-1 3.5 1v-1.5L13 19v-5.5l8 2.5z"/>
            </svg>
          </div>
          <h3 class="text-xl font-extrabold text-ink tracking-tight">Sign In to AeroBook</h3>
          <p class="text-xs text-ink-2 mt-1">Access your flight bookings, airport operations, or admin console</p>
        </div>

        <!-- Role Tab Switcher -->
        <div class="grid grid-cols-3 gap-1 p-1 bg-seg rounded-2xl mb-4 border border-line">
          <button (click)="switchRole('user')" 
                  type="button"
                  [class]="activeTab === 'user' ? 'bg-accent text-white shadow-sm font-bold' : 'text-ink-2 hover:text-ink font-medium'" 
                  class="py-2 text-xs rounded-xl transition-all flex items-center justify-center gap-1 cursor-pointer">
            <span>👤</span> Passenger
          </button>
          <button (click)="switchRole('staff')" 
                  type="button"
                  [class]="activeTab === 'staff' ? 'bg-accent text-white shadow-sm font-bold' : 'text-ink-2 hover:text-ink font-medium'" 
                  class="py-2 text-xs rounded-xl transition-all flex items-center justify-center gap-1 cursor-pointer">
            <span>✈️</span> Staff
          </button>
          <button (click)="switchRole('admin')" 
                  type="button"
                  [class]="activeTab === 'admin' ? 'bg-accent text-white shadow-sm font-bold' : 'text-ink-2 hover:text-ink font-medium'" 
                  class="py-2 text-xs rounded-xl transition-all flex items-center justify-center gap-1 cursor-pointer">
            <span>🛡️</span> Admin
          </button>
        </div>

        <!-- Quick-Fill Demo Bar -->
        <div class="mb-4 p-2.5 rounded-2xl bg-seg/50 border border-line flex items-center justify-between">
          <div class="text-[11px] text-ink-2">
            <span class="font-semibold text-ink">Demo:</span> 
            <span class="font-mono ml-1 text-ink-2">{{ email || 'Click Auto Fill' }}</span>
          </div>
          <button (click)="fillDemoCredentials()" type="button" 
                  class="px-2.5 py-1 text-[11px] font-semibold rounded-lg bg-surface hover:bg-seg text-accent border border-line transition-colors cursor-pointer shadow-sm">
            Auto Fill
          </button>
        </div>

        <!-- Error Alert -->
        @if (errorMessage) {
          <div class="mb-4 p-3 rounded-2xl bg-red-500/10 border border-red-500/30 text-red-600 dark:text-red-400 text-xs flex items-center gap-2">
            <svg class="w-4 h-4 shrink-0 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span>{{ errorMessage }}</span>
          </div>
        }

        <!-- Form -->
        <form (ngSubmit)="handleLogin()" class="space-y-3.5">
          <div>
            <label class="block text-xs font-semibold text-ink-2 mb-1">Email Address</label>
            <input type="email" [(ngModel)]="email" name="email" required autocomplete="email" placeholder="name@example.com"
                   class="w-full px-3.5 py-2.5 rounded-xl bg-surface border border-line text-ink font-medium text-sm focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all placeholder:text-ink-3">
          </div>

          <div>
            <label class="block text-xs font-semibold text-ink-2 mb-1">Password</label>
            <input type="password" [(ngModel)]="password" name="password" required autocomplete="current-password" placeholder="••••••••"
                   class="w-full px-3.5 py-2.5 rounded-xl bg-surface border border-line text-ink font-medium text-sm focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all placeholder:text-ink-3">
          </div>

          <button type="submit" [disabled]="loading"
                  class="w-full py-3 rounded-xl bg-accent hover:opacity-95 text-white text-sm font-bold shadow-md shadow-accent/25 transition-all flex items-center justify-center gap-2 disabled:opacity-50 cursor-pointer mt-2">
            @if (loading) {
              <svg class="w-4 h-4 animate-spin text-white" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <span>Verifying Credentials...</span>
            } @else {
              <span>Sign In as {{ getRoleName() }}</span>
            }
          </button>
        </form>

        <!-- Footer switch -->
        <div class="mt-5 text-center text-xs text-ink-3">
          Don't have an account yet?
          <button (click)="switchToRegister()" class="text-accent hover:underline font-bold ml-1 cursor-pointer">
            Create an account
          </button>
        </div>

      </div>
    </div>
  `
})
export class LoginModalComponent {
  @Output() close = new EventEmitter<void>();
  @Output() openRegister = new EventEmitter<void>();

  activeTab: 'user' | 'staff' | 'admin' = 'user';
  email: string = 'asha.khan@example.com';
  password: string = 'Asha@12345';
  loading: boolean = false;
  errorMessage: string = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  switchRole(role: 'user' | 'staff' | 'admin'): void {
    this.activeTab = role;
    this.errorMessage = '';
    this.fillDemoCredentials();
  }

  fillDemoCredentials(): void {
    if (this.activeTab === 'admin') {
      this.email = 'admin@aerobook.com';
      this.password = 'Admin@12345';
    } else if (this.activeTab === 'staff') {
      this.email = 'staff@aerobook.com';
      this.password = 'Staff@12345';
    } else {
      this.email = 'asha.khan@example.com';
      this.password = 'Asha@12345';
    }
  }

  getRoleName(): string {
    if (this.activeTab === 'admin') return 'Administrator';
    if (this.activeTab === 'staff') return 'Airport Staff';
    return 'Passenger';
  }

  handleLogin(): void {
    this.loading = true;
    this.errorMessage = '';

    this.authService.login({ email: this.email, password: this.password }).subscribe({
      next: (res) => {
        this.loading = false;
        this.close.emit();
        // Route safely to respective role landing page
        if (res.role === 'ROLE_ADMIN') {
          this.router.navigate(['/admin']);
        } else if (res.role === 'ROLE_STAFF') {
          this.router.navigate(['/staff']);
        } else {
          this.router.navigate(['/customer']);
        }
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'Invalid email or password. Please verify credentials.';
      }
    });
  }

  switchToRegister(): void {
    this.close.emit();
    this.openRegister.emit();
  }
}

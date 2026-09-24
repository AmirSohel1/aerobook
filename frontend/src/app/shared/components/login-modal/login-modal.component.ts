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
    <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-md animate-fadeIn">
      <div class="glass-panel w-full max-w-md rounded-2xl p-6 sm:p-8 shadow-2xl border border-slate-700/60 relative">
        
        <!-- Close Button -->
        <button (click)="close.emit()" class="absolute top-5 right-5 text-slate-400 hover:text-white transition-colors">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>

        <!-- Header -->
        <div class="text-center mb-6">
          <div class="inline-flex items-center justify-center w-12 h-12 rounded-xl bg-gradient-to-tr from-brand-600 to-cyan-400 mb-3 shadow-lg shadow-brand-500/25">
            <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1" />
            </svg>
          </div>
          <h3 class="text-xl font-bold text-white">Sign In to AeroBook</h3>
          <p class="text-xs text-slate-400 mt-1">Access your flight bookings, boarding passes, or operations hub</p>
        </div>

        <!-- Role Tab Switcher -->
        <div class="grid grid-cols-3 gap-1.5 p-1 bg-slate-900/90 rounded-xl mb-6 border border-slate-800">
          <button (click)="switchRole('user')" [class]="activeTab === 'user' ? 'bg-brand-600 text-white shadow-sm' : 'text-slate-400 hover:text-slate-200'" class="py-2 text-xs font-semibold rounded-lg transition-all flex items-center justify-center gap-1">
            <span>👤</span> Passenger
          </button>
          <button (click)="switchRole('staff')" [class]="activeTab === 'staff' ? 'bg-amber-600 text-white shadow-sm' : 'text-slate-400 hover:text-slate-200'" class="py-2 text-xs font-semibold rounded-lg transition-all flex items-center justify-center gap-1">
            <span>✈️</span> Staff
          </button>
          <button (click)="switchRole('admin')" [class]="activeTab === 'admin' ? 'bg-purple-600 text-white shadow-sm' : 'text-slate-400 hover:text-slate-200'" class="py-2 text-xs font-semibold rounded-lg transition-all flex items-center justify-center gap-1">
            <span>🛡️</span> Admin
          </button>
        </div>

        <!-- Quick-Fill Demo Bar -->
        <div class="mb-5 p-2.5 rounded-xl bg-slate-900/50 border border-slate-800/80 flex items-center justify-between">
          <div class="text-[11px] text-slate-400">
            <span class="font-medium text-slate-300">Quick Test:</span> Click to auto-fill credentials
          </div>
          <button (click)="fillDemoCredentials()" type="button" class="px-2.5 py-1 text-[11px] font-semibold rounded bg-slate-800 hover:bg-slate-700 text-brand-400 border border-brand-500/30 transition-colors">
            Auto Fill
          </button>
        </div>

        <!-- Error Alert -->
        @if (errorMessage) {
          <div class="mb-4 p-3 rounded-xl bg-red-500/10 border border-red-500/30 text-red-300 text-xs flex items-center gap-2">
            <svg class="w-4 h-4 shrink-0 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span>{{ errorMessage }}</span>
          </div>
        }

        <!-- Form -->
        <form (ngSubmit)="handleLogin()" class="space-y-4">
          <div>
            <label class="block text-xs font-medium text-slate-300 mb-1">Email Address</label>
            <input type="email" [(ngModel)]="email" name="email" required placeholder="name@example.com"
                   class="w-full px-3.5 py-2.5 rounded-xl bg-slate-900/80 border border-slate-700 text-white text-sm focus:outline-none focus:border-brand-500 focus:ring-1 focus:ring-brand-500 transition-colors placeholder:text-slate-500">
          </div>

          <div>
            <label class="block text-xs font-medium text-slate-300 mb-1">Password</label>
            <input type="password" [(ngModel)]="password" name="password" required placeholder="••••••••"
                   class="w-full px-3.5 py-2.5 rounded-xl bg-slate-900/80 border border-slate-700 text-white text-sm focus:outline-none focus:border-brand-500 focus:ring-1 focus:ring-brand-500 transition-colors placeholder:text-slate-500">
          </div>

          <button type="submit" [disabled]="loading"
                  class="w-full py-3 rounded-xl bg-gradient-to-r from-brand-500 to-cyan-500 hover:from-brand-600 hover:to-cyan-600 text-white text-sm font-semibold shadow-lg shadow-brand-500/25 transition-all flex items-center justify-center gap-2 disabled:opacity-50">
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
        <div class="mt-6 text-center text-xs text-slate-400">
          Don't have an account yet?
          <button (click)="switchToRegister()" class="text-brand-400 hover:text-brand-300 font-semibold ml-1">
            Sign up now
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
  email: string = '';
  password: string = '';
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


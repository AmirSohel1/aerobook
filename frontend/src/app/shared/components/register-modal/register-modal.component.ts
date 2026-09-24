import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-slate-950/80 backdrop-blur-md animate-fadeIn">
      <div class="glass-panel w-full max-w-lg rounded-2xl p-6 sm:p-8 shadow-2xl border border-slate-700/60 relative max-h-[90vh] overflow-y-auto">
        
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
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z" />
            </svg>
          </div>
          <h3 class="text-xl font-bold text-white">Create AeroBook Account</h3>
          <p class="text-xs text-slate-400 mt-1">Join the airline ecosystem for flight bookings and airport services</p>
        </div>

        <!-- Account Type Switcher -->
        <div class="grid grid-cols-2 gap-2 p-1 bg-slate-900/90 rounded-xl mb-6 border border-slate-800">
          <button type="button" (click)="isStaff = false" [class]="!isStaff ? 'bg-brand-600 text-white shadow-sm' : 'text-slate-400 hover:text-slate-200'" class="py-2 text-xs font-semibold rounded-lg transition-all flex items-center justify-center gap-1.5">
            <span>👤</span> Passenger Account
          </button>
          <button type="button" (click)="isStaff = true" [class]="isStaff ? 'bg-amber-600 text-white shadow-sm' : 'text-slate-400 hover:text-slate-200'" class="py-2 text-xs font-semibold rounded-lg transition-all flex items-center justify-center gap-1.5">
            <span>✈️</span> Airport Staff Portal
          </button>
        </div>

        <!-- Error Banner -->
        @if (errorMessage) {
          <div class="mb-4 p-3 rounded-xl bg-red-500/10 border border-red-500/30 text-red-300 text-xs flex items-center gap-2">
            <svg class="w-4 h-4 shrink-0 text-red-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span>{{ errorMessage }}</span>
          </div>
        }

        <!-- Registration Form -->
        <form (ngSubmit)="handleRegister()" class="space-y-4">
          
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label class="block text-xs font-medium text-slate-300 mb-1">First Name</label>
              <input type="text" [(ngModel)]="firstName" name="firstName" required placeholder="Asha"
                     class="w-full px-3.5 py-2 rounded-xl bg-slate-900/80 border border-slate-700 text-white text-sm focus:outline-none focus:border-brand-500">
            </div>
            <div>
              <label class="block text-xs font-medium text-slate-300 mb-1">Last Name</label>
              <input type="text" [(ngModel)]="lastName" name="lastName" required placeholder="Khan"
                     class="w-full px-3.5 py-2 rounded-xl bg-slate-900/80 border border-slate-700 text-white text-sm focus:outline-none focus:border-brand-500">
            </div>
          </div>

          <div>
            <label class="block text-xs font-medium text-slate-300 mb-1">Email Address</label>
            <input type="email" [(ngModel)]="email" name="email" required placeholder="asha.khan@example.com"
                   class="w-full px-3.5 py-2 rounded-xl bg-slate-900/80 border border-slate-700 text-white text-sm focus:outline-none focus:border-brand-500">
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label class="block text-xs font-medium text-slate-300 mb-1">Phone Number</label>
              <input type="tel" [(ngModel)]="phoneNumber" name="phoneNumber" placeholder="9876543210"
                     class="w-full px-3.5 py-2 rounded-xl bg-slate-900/80 border border-slate-700 text-white text-sm focus:outline-none focus:border-brand-500">
            </div>
            <div>
              <label class="block text-xs font-medium text-slate-300 mb-1">Date of Birth</label>
              <input type="date" [(ngModel)]="dateOfBirth" name="dateOfBirth"
                     class="w-full px-3.5 py-2 rounded-xl bg-slate-900/80 border border-slate-700 text-white text-sm focus:outline-none focus:border-brand-500">
            </div>
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label class="block text-xs font-medium text-slate-300 mb-1">Nationality</label>
              <input type="text" [(ngModel)]="nationality" name="nationality" placeholder="Indian"
                     class="w-full px-3.5 py-2 rounded-xl bg-slate-900/80 border border-slate-700 text-white text-sm focus:outline-none focus:border-brand-500">
            </div>
            <div>
              <label class="block text-xs font-medium text-slate-300 mb-1">Password</label>
              <input type="password" [(ngModel)]="password" name="password" required placeholder="Asha@12345"
                     class="w-full px-3.5 py-2 rounded-xl bg-slate-900/80 border border-slate-700 text-white text-sm focus:outline-none focus:border-brand-500">
            </div>
          </div>

          <!-- Staff Setup Key Authorization Input -->
          @if (isStaff) {
            <div class="p-3 rounded-xl bg-amber-500/10 border border-amber-500/30">
              <label class="block text-xs font-semibold text-amber-300 mb-1 flex items-center gap-1.5">
                <svg class="w-4 h-4 text-amber-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 7a2 2 0 012 2m4 0a6 6 0 01-7.743 5.743L11 17H9v2H7v2H4a1 1 0 01-1-1v-2.586a1 1 0 01.293-.707l5.964-5.964A6 6 0 1121 9z" />
                </svg>
                Staff Authorization Setup Key
              </label>
              <input type="text" [(ngModel)]="staffSetupKey" name="staffSetupKey" required
                     placeholder="AerobookStaffSetup2026"
                     class="w-full px-3 py-1.5 rounded-lg bg-slate-900 border border-amber-500/40 text-amber-200 text-xs font-mono focus:outline-none focus:border-amber-400">
              <p class="text-[10px] text-amber-400/80 mt-1">Pre-filled with master station key for instant test elevation.</p>
            </div>
          }

          <button type="submit" [disabled]="loading"
                  class="w-full py-3 rounded-xl text-white text-sm font-semibold shadow-lg transition-all flex items-center justify-center gap-2 disabled:opacity-50"
                  [ngClass]="isStaff ? 'bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600 shadow-amber-500/25' : 'bg-gradient-to-r from-brand-500 to-cyan-500 hover:from-brand-600 hover:to-cyan-600 shadow-brand-500/25'">
            @if (loading) {
              <span>Provisioning Account...</span>
            } @else {
              <span>Register as {{ isStaff ? 'Airport Staff' : 'Customer' }}</span>
            }
          </button>
        </form>

        <!-- Footer switch -->
        <div class="mt-6 text-center text-xs text-slate-400">
          Already registered?
          <button (click)="switchToLogin()" class="text-brand-400 hover:text-brand-300 font-semibold ml-1">
            Sign In here
          </button>
        </div>

      </div>
    </div>
  `
})
export class RegisterModalComponent {
  @Output() close = new EventEmitter<void>();
  @Output() openLogin = new EventEmitter<void>();

  isStaff: boolean = false;
  firstName: string = '';
  lastName: string = '';
  email: string = '';
  phoneNumber: string = '9876543210';
  dateOfBirth: string = '1996-05-15';
  nationality: string = 'Indian';
  password: string = 'Asha@12345';
  staffSetupKey: string = 'AerobookStaffSetup2026';

  loading: boolean = false;
  errorMessage: string = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  handleRegister(): void {
    this.loading = true;
    this.errorMessage = '';

    const payload = {
      firstName: this.firstName,
      lastName: this.lastName,
      email: this.email,
      phoneNumber: this.phoneNumber,
      dateOfBirth: this.dateOfBirth,
      nationality: this.nationality,
      password: this.password
    };

    if (this.isStaff) {
      this.authService.registerStaff(payload, this.staffSetupKey).subscribe({
        next: () => {
          this.loading = false;
          this.close.emit();
          this.router.navigate(['/staff']);
        },
        error: (err) => {
          this.loading = false;
          this.errorMessage = err.error?.message || 'Staff registration failed. Verify setup key.';
        }
      });
    } else {
      this.authService.register(payload).subscribe({
        next: () => {
          this.loading = false;
          this.close.emit();
          this.router.navigate(['/customer']);
        },
        error: (err) => {
          this.loading = false;
          this.errorMessage = err.error?.message || 'Customer registration failed.';
        }
      });
    }
  }

  switchToLogin(): void {
    this.close.emit();
    this.openLogin.emit();
  }
}


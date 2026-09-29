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
    <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 dark:bg-black/75 backdrop-blur-md animate-fadeIn">
      <div class="bg-solid border border-line shadow-card text-ink rounded-3xl w-full max-w-lg p-6 sm:p-8 relative max-h-[90vh] overflow-y-auto transition-all">
        
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
          <h3 class="text-xl font-extrabold text-ink tracking-tight">Create AeroBook Account</h3>
          <p class="text-xs text-ink-2 mt-1">Join the airline ecosystem for flight bookings and operations</p>
        </div>

        <!-- Account Type Switcher -->
        <div class="grid grid-cols-3 gap-1 p-1 bg-seg rounded-2xl mb-5 border border-line">
          <button type="button" 
                  (click)="setAccountType('user')" 
                  [class]="accountType === 'user' ? 'bg-accent text-white shadow-sm font-bold' : 'text-ink-2 hover:text-ink font-medium'" 
                  class="py-2 text-xs rounded-xl transition-all flex items-center justify-center gap-1 cursor-pointer">
            <span>👤</span> Passenger
          </button>
          <button type="button" 
                  (click)="setAccountType('staff')" 
                  [class]="accountType === 'staff' ? 'bg-accent text-white shadow-sm font-bold' : 'text-ink-2 hover:text-ink font-medium'" 
                  class="py-2 text-xs rounded-xl transition-all flex items-center justify-center gap-1 cursor-pointer">
            <span>✈️</span> Staff
          </button>
          <button type="button" 
                  (click)="setAccountType('admin')" 
                  [class]="accountType === 'admin' ? 'bg-accent text-white shadow-sm font-bold' : 'text-ink-2 hover:text-ink font-medium'" 
                  class="py-2 text-xs rounded-xl transition-all flex items-center justify-center gap-1 cursor-pointer">
            <span>🛡️</span> Admin
          </button>
        </div>

        <!-- Error Banner -->
        @if (errorMessage) {
          <div class="mb-4 p-3 rounded-2xl bg-red-500/10 border border-red-500/30 text-red-600 dark:text-red-400 text-xs flex items-center gap-2">
            <svg class="w-4 h-4 shrink-0 text-red-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
            <span>{{ errorMessage }}</span>
          </div>
        }

        <!-- Registration Form -->
        <form (ngSubmit)="handleRegister()" class="space-y-3.5">
          
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-3.5">
            <div>
              <label class="block text-xs font-semibold text-ink-2 mb-1">First Name</label>
              <input type="text" [(ngModel)]="firstName" name="firstName" required autocomplete="given-name" placeholder="Asha"
                     class="w-full px-3.5 py-2.5 rounded-xl bg-surface border border-line text-ink font-medium text-sm focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all placeholder:text-ink-3">
            </div>
            <div>
              <label class="block text-xs font-semibold text-ink-2 mb-1">Last Name</label>
              <input type="text" [(ngModel)]="lastName" name="lastName" required autocomplete="family-name" placeholder="Khan"
                     class="w-full px-3.5 py-2.5 rounded-xl bg-surface border border-line text-ink font-medium text-sm focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all placeholder:text-ink-3">
            </div>
          </div>

          <div>
            <label class="block text-xs font-semibold text-ink-2 mb-1">Email Address</label>
            <input type="email" [(ngModel)]="email" name="email" required autocomplete="email" placeholder="asha.khan@example.com"
                   class="w-full px-3.5 py-2.5 rounded-xl bg-surface border border-line text-ink font-medium text-sm focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all placeholder:text-ink-3">
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-2 gap-3.5">
            <div>
              <label class="block text-xs font-semibold text-ink-2 mb-1">Phone Number</label>
              <input type="tel" [(ngModel)]="phoneNumber" name="phoneNumber" autocomplete="tel" placeholder="9876543210"
                     class="w-full px-3.5 py-2.5 rounded-xl bg-surface border border-line text-ink font-medium text-sm focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all placeholder:text-ink-3">
            </div>
            <div>
              <label class="block text-xs font-semibold text-ink-2 mb-1">Date of Birth</label>
              <input type="date" [(ngModel)]="dateOfBirth" name="dateOfBirth" autocomplete="bday"
                     class="w-full px-3.5 py-2.5 rounded-xl bg-surface border border-line text-ink font-medium text-sm focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all placeholder:text-ink-3">
            </div>
          </div>

          <div class="grid grid-cols-1 sm:grid-cols-2 gap-3.5">
            <div>
              <label class="block text-xs font-semibold text-ink-2 mb-1">Nationality</label>
              <input type="text" [(ngModel)]="nationality" name="nationality" autocomplete="country-name" placeholder="Indian"
                     class="w-full px-3.5 py-2.5 rounded-xl bg-surface border border-line text-ink font-medium text-sm focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all placeholder:text-ink-3">
            </div>
            <div>
              <label class="block text-xs font-semibold text-ink-2 mb-1">Password</label>
              <input type="password" [(ngModel)]="password" name="password" required autocomplete="new-password" placeholder="••••••••"
                     class="w-full px-3.5 py-2.5 rounded-xl bg-surface border border-line text-ink font-medium text-sm focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all placeholder:text-ink-3">
            </div>
          </div>

          <!-- Staff Setup Key Authorization Input -->
          @if (accountType === 'staff') {
            <div class="p-3 rounded-2xl bg-amber-500/10 border border-amber-500/30">
              <label class="block text-xs font-semibold text-amber-700 dark:text-amber-300 mb-1 flex items-center gap-1.5">
                <svg class="w-4 h-4 text-amber-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 7a2 2 0 012 2m4 0a6 6 0 01-7.743 5.743L11 17H9v2H7v2H4a1 1 0 01-1-1v-2.586a1 1 0 01.293-.707l5.964-5.964A6 6 0 1121 9z" />
                </svg>
                Staff Authorization Setup Key
              </label>
              <input type="text" [(ngModel)]="staffSetupKey" name="staffSetupKey" required autocomplete="off"
                     placeholder="AerobookStaffSetup2026"
                     class="w-full px-3 py-2 rounded-xl bg-surface border border-amber-500/40 text-amber-700 dark:text-amber-200 text-xs font-mono focus:outline-none focus:border-amber-500">
              <p class="text-[10px] text-amber-600 dark:text-amber-400 mt-1">Required header key for staff elevation (pre-filled).</p>
            </div>
          }

          <!-- Admin Setup Key Authorization Input -->
          @if (accountType === 'admin') {
            <div class="p-3 rounded-2xl bg-purple-500/10 border border-purple-500/30">
              <label class="block text-xs font-semibold text-purple-700 dark:text-purple-300 mb-1 flex items-center gap-1.5">
                <svg class="w-4 h-4 text-purple-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                </svg>
                Administrator Master Elevation Key
              </label>
              <input type="text" [(ngModel)]="adminSetupKey" name="adminSetupKey" required autocomplete="off"
                     placeholder="AerobookAdminSetup2026"
                     class="w-full px-3 py-2 rounded-xl bg-surface border border-purple-500/40 text-purple-700 dark:text-purple-200 text-xs font-mono focus:outline-none focus:border-purple-500">
              <p class="text-[10px] text-purple-600 dark:text-purple-400 mt-1">Master key required for administrator elevation (pre-filled).</p>
            </div>
          }

          <button type="submit" [disabled]="loading"
                  class="w-full py-3 rounded-xl bg-accent hover:opacity-95 text-white text-sm font-bold shadow-md shadow-accent/25 transition-all flex items-center justify-center gap-2 disabled:opacity-50 cursor-pointer mt-2">
            @if (loading) {
              <svg class="w-4 h-4 animate-spin text-white" fill="none" viewBox="0 0 24 24">
                <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
              </svg>
              <span>Provisioning Account...</span>
            } @else {
              <span>Register as {{ getRoleName() }}</span>
            }
          </button>
        </form>

        <!-- Footer switch -->
        <div class="mt-5 text-center text-xs text-ink-3">
          Already registered?
          <button (click)="switchToLogin()" class="text-accent hover:underline font-bold ml-1 cursor-pointer">
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

  accountType: 'user' | 'staff' | 'admin' = 'user';
  firstName: string = 'Asha';
  lastName: string = 'Khan';
  email: string = 'asha.khan@example.com';
  phoneNumber: string = '9876543210';
  dateOfBirth: string = '1998-05-12';
  nationality: string = 'Indian';
  password: string = 'Asha@12345';
  staffSetupKey: string = 'AerobookStaffSetup2026';
  adminSetupKey: string = 'AerobookAdminSetup2026';

  loading: boolean = false;
  errorMessage: string = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  setAccountType(type: 'user' | 'staff' | 'admin'): void {
    this.accountType = type;
    this.errorMessage = '';
    if (type === 'admin') {
      this.firstName = 'System';
      this.lastName = 'Admin';
      this.email = 'admin@aerobook.com';
      this.password = 'Admin@12345';
    } else if (type === 'staff') {
      this.firstName = 'Airport';
      this.lastName = 'Staff';
      this.email = 'staff@aerobook.com';
      this.password = 'Staff@12345';
    } else {
      this.firstName = 'Asha';
      this.lastName = 'Khan';
      this.email = 'asha.khan@example.com';
      this.password = 'Asha@12345';
    }
  }

  getRoleName(): string {
    if (this.accountType === 'admin') return 'Administrator';
    if (this.accountType === 'staff') return 'Airport Staff';
    return 'Passenger';
  }

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

    if (this.accountType === 'admin') {
      this.authService.registerAdmin(payload, this.adminSetupKey).subscribe({
        next: () => {
          this.loading = false;
          this.close.emit();
          this.router.navigate(['/admin']);
        },
        error: (err) => {
          this.loading = false;
          this.errorMessage = err.error?.message || 'Admin registration failed. Verify master setup key.';
        }
      });
    } else if (this.accountType === 'staff') {
      this.authService.registerStaff(payload, this.staffSetupKey).subscribe({
        next: () => {
          this.loading = false;
          this.close.emit();
          this.router.navigate(['/staff']);
        },
        error: (err) => {
          this.loading = false;
          this.errorMessage = err.error?.message || 'Staff registration failed. Verify staff setup key.';
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

import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <header class="glass-panel sticky top-0 z-50 border-b border-slate-800">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 h-16 flex items-center justify-between">
        
        <!-- Brand Logo -->
        <a routerLink="/" class="flex items-center space-x-3 group">
          <div class="w-10 h-10 rounded-xl bg-gradient-to-tr from-brand-600 to-cyan-400 flex items-center justify-center shadow-lg shadow-brand-500/25 group-hover:scale-105 transition-transform">
            <svg class="w-6 h-6 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8" />
            </svg>
          </div>
          <div>
            <span class="text-xl font-bold tracking-tight text-white flex items-center gap-1.5">
              Aero<span class="text-transparent bg-clip-text bg-gradient-to-r from-brand-400 to-cyan-300">Book</span>
              <span class="text-[10px] font-semibold uppercase tracking-wider px-1.5 py-0.5 rounded bg-brand-500/20 text-brand-300 border border-brand-500/30">Cloud</span>
            </span>
          </div>
        </a>

        <!-- Desktop Navigation Links -->
        <nav class="hidden md:flex items-center space-x-6 text-sm font-medium">
          <a routerLink="/" routerLinkActive="text-brand-400" [routerLinkActiveOptions]="{exact: true}" class="text-slate-300 hover:text-white transition-colors">Flights</a>
          
          @if (authService.isAuthenticated()) {
            @if (authService.isCustomer()) {
              <a routerLink="/customer" routerLinkActive="text-brand-400" class="text-slate-300 hover:text-white transition-colors flex items-center gap-1.5">
                <span class="w-2 h-2 rounded-full bg-emerald-400"></span>
                My Bookings & Portal
              </a>
            }
            @if (authService.isStaff()) {
              <a routerLink="/staff" routerLinkActive="text-brand-400" class="text-slate-300 hover:text-white transition-colors flex items-center gap-1.5">
                <span class="w-2 h-2 rounded-full bg-amber-400"></span>
                Airport Operations Hub
              </a>
            }
            @if (authService.isAdmin()) {
              <a routerLink="/admin" routerLinkActive="text-brand-400" class="text-slate-300 hover:text-white transition-colors flex items-center gap-1.5">
                <span class="w-2 h-2 rounded-full bg-purple-400"></span>
                Airline Admin Console
              </a>
            }
          }
        </nav>

        <!-- Right Action Controls -->
        <div class="flex items-center space-x-3">
          
          @if (authService.isAuthenticated()) {
            <!-- Role Badge -->
            <div class="hidden sm:flex items-center space-x-2 px-3 py-1 rounded-full text-xs font-semibold border"
                 [ngClass]="{
                   'bg-emerald-500/10 text-emerald-300 border-emerald-500/30': authService.isCustomer(),
                   'bg-amber-500/10 text-amber-300 border-amber-500/30': authService.isStaff(),
                   'bg-purple-500/10 text-purple-300 border-purple-500/30': authService.isAdmin()
                 }">
              <span>{{ getRoleLabel() }}</span>
            </div>

            <!-- Dashboard Button -->
            <button (click)="navigateToDashboard()" class="px-3.5 py-1.5 rounded-lg bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-medium border border-slate-700 transition-colors flex items-center gap-1.5">
              <svg class="w-4 h-4 text-brand-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2V6zM14 6a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2V6zM4 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2H6a2 2 0 01-2-2v-2zM14 16a2 2 0 012-2h2a2 2 0 012 2v2a2 2 0 01-2 2h-2a2 2 0 01-2-2v-2z" />
              </svg>
              Dashboard
            </button>

            <!-- Logout -->
            <button (click)="logout()" class="px-3 py-1.5 rounded-lg bg-red-500/10 hover:bg-red-500/20 text-red-300 text-xs font-medium border border-red-500/30 transition-colors">
              Sign Out
            </button>
          } @else {
            <button (click)="openLogin.emit()" class="px-4 py-2 rounded-xl text-xs font-semibold text-slate-200 hover:text-white hover:bg-slate-800/80 transition-colors">
              Sign In
            </button>
            <button (click)="openRegister.emit()" class="px-4 py-2 rounded-xl text-xs font-semibold bg-gradient-to-r from-brand-500 to-cyan-500 hover:from-brand-600 hover:to-cyan-600 text-white shadow-md shadow-brand-500/20 transition-all hover:shadow-brand-500/30">
              Create Account
            </button>
          }

        </div>

      </div>
    </header>
  `
})
export class NavbarComponent {
  @Output() openLogin = new EventEmitter<void>();
  @Output() openRegister = new EventEmitter<void>();

  constructor(
    public authService: AuthService,
    private router: Router
  ) {}

  getRoleLabel(): string {
    if (this.authService.isAdmin()) return '🛡️ System Admin';
    if (this.authService.isStaff()) return '✈️ Airport Staff';
    return '👤 Passenger';
  }

  navigateToDashboard(): void {
    if (this.authService.isAdmin()) {
      this.router.navigate(['/admin']);
    } else if (this.authService.isStaff()) {
      this.router.navigate(['/staff']);
    } else {
      this.router.navigate(['/customer']);
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}


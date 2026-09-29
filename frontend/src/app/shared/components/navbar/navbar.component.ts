import { Component, EventEmitter, HostListener, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { ThemeService } from '../../../core/services/theme.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <header class="fixed top-3 sm:top-4 left-0 right-0 z-50 px-4 sm:px-6 lg:px-8 pointer-events-none">
      <div class="max-w-7xl mx-auto pointer-events-auto">
        <div class="h-14 sm:h-16 px-4 sm:px-6 rounded-full bg-white/95 dark:bg-[#121024]/95 backdrop-blur-xl border border-line shadow-card flex items-center justify-between transition-all">
          
          <!-- Left: Logo & AeroBook Brand (Clicking navigates to home landing page) -->
          <a routerLink="/" (click)="closeMenus()" class="flex items-center gap-2.5 group cursor-pointer select-none">
            <div class="w-8 h-8 sm:w-9 sm:h-9 rounded-2xl bg-[#635BFF] flex items-center justify-center text-white shadow-md shadow-[#635BFF]/30 group-hover:scale-105 transition-transform">
              <svg class="w-4 h-4 sm:w-5 sm:h-5 text-white transform -rotate-45" fill="currentColor" viewBox="0 0 24 24">
                <path d="M21 16v-2l-8-5V3.5c0-.83-.67-1.5-1.5-1.5S10 2.67 10 3.5V9l-8 5v2l8-2.5V19l-2 1.5V22l3.5-1 3.5 1v-1.5L13 19v-5.5l8 2.5z"/>
              </svg>
            </div>
            <div class="flex flex-col">
              <span class="font-extrabold text-base sm:text-lg tracking-tight text-ink leading-tight">
                AeroBook
              </span>
            </div>
          </a>

          <!-- Right: Theme Toggle & User Auth Controls -->
          <div class="flex items-center gap-2 sm:gap-3">
            
            <!-- Theme Mode Toggle -->
            <button (click)="themeService.toggleTheme()"
                    [title]="themeService.isDark() ? 'Switch to Light Mode' : 'Switch to Dark Mode'"
                    class="w-8 h-8 rounded-full flex items-center justify-center text-ink-2 hover:text-ink hover:bg-seg transition-colors cursor-pointer"
                    aria-label="Toggle theme">
              @if (themeService.isDark()) {
                <!-- Sun Icon -->
                <svg class="w-4 h-4 text-amber-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 3v1m0 16v1m9-9h-1M4 12H3m15.364 6.364l-.707-.707M6.343 6.343l-.707-.707m12.728 0l-.707.707M6.343 17.657l-.707.707M16 12a4 4 0 11-8 0 4 4 0 018 0z" />
                </svg>
              } @else {
                <!-- Slim Moon Outline Icon -->
                <svg class="w-4 h-4 text-ink-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M20.354 15.354A9 9 0 018.646 3.646 9.003 9.003 0 0012 21a9.003 9.003 0 008.354-5.646z" />
                </svg>
              }
            </button>

            <!-- Authenticated User Profile Dropdown Pill -->
            @if (authService.isAuthenticated()) {
              <div class="relative user-menu-container">
                <button (click)="toggleUserMenu($event)"
                        class="flex items-center gap-2 pl-1 pr-2.5 sm:pr-3 py-1 rounded-full bg-seg/70 hover:bg-seg border border-line text-xs font-semibold text-ink transition-all cursor-pointer shadow-sm">
                  <div class="w-7 h-7 rounded-full bg-[#635BFF] text-white font-bold text-[11px] flex items-center justify-center shadow-sm">
                    {{ authService.getUserInitials() }}
                  </div>
                  <div class="hidden sm:flex flex-col text-left max-w-[120px] md:max-w-[150px]">
                    <span class="text-xs font-semibold text-ink truncate leading-tight">{{ authService.getUserDisplayName() }}</span>
                    <span class="text-[9px] font-mono text-ink-3 uppercase leading-none tracking-wider">{{ authService.getUserRoleLabel() }}</span>
                  </div>
                  <svg class="w-3.5 h-3.5 text-ink-3 transition-transform duration-200" [class.rotate-180]="isUserMenuOpen" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 9l-7 7-7-7"/>
                  </svg>
                </button>

                <!-- User Dropdown Menu -->
                @if (isUserMenuOpen) {
                  <div class="absolute right-0 mt-2 w-64 rounded-2xl bg-surface border border-line shadow-card p-2 text-xs text-ink space-y-1 z-50 animate-fadeIn backdrop-blur-xl">
                    
                    <!-- Identity Summary Header -->
                    <div class="p-2.5 rounded-xl bg-seg/50 border border-line/60 mb-1">
                      <div class="flex items-center gap-2 mb-1.5">
                        <div class="w-8 h-8 rounded-full bg-[#635BFF] text-white font-bold text-xs flex items-center justify-center shadow-sm">
                          {{ authService.getUserInitials() }}
                        </div>
                        <div class="flex-1 min-w-0">
                          <span class="font-bold text-ink text-xs block truncate">{{ authService.getUserDisplayName() }}</span>
                          <span class="text-[10px] text-ink-3 font-mono truncate block">{{ authService.currentEmail() }}</span>
                        </div>
                      </div>
                      <div class="flex items-center gap-1.5">
                        <span class="px-2 py-0.5 rounded-full text-[10px] font-bold"
                              [ngClass]="authService.isAdmin() ? 'bg-purple-500/15 text-purple-600 dark:text-purple-400 border border-purple-500/30' : (authService.isStaff() ? 'bg-amber-500/15 text-amber-600 dark:text-amber-400 border border-amber-500/30' : 'bg-accent/15 text-accent border border-accent/30')">
                          {{ authService.getUserRoleLabel() }}
                        </span>
                        <span class="text-[10px] text-ink-3 font-mono">UID: #{{ authService.currentUserId() }}</span>
                      </div>
                    </div>

                    <!-- Role-based activity navigation -->
                    @if (authService.isAdmin()) {
                      <a routerLink="/admin" (click)="closeMenus()" class="flex items-center gap-2.5 px-3 py-2 rounded-xl hover:bg-seg text-ink transition-colors cursor-pointer">
                        <span class="text-base">🛡️</span>
                        <div class="flex flex-col">
                          <span class="font-semibold text-xs text-ink">Admin Console</span>
                          <span class="text-[10px] text-ink-3">Master platform administration</span>
                        </div>
                      </a>
                      <a routerLink="/staff" (click)="closeMenus()" class="flex items-center gap-2.5 px-3 py-2 rounded-xl hover:bg-seg text-ink transition-colors cursor-pointer">
                        <span class="text-base">✈️</span>
                        <div class="flex flex-col">
                          <span class="font-semibold text-xs text-ink">Staff Operations</span>
                          <span class="text-[10px] text-ink-3">Gates & flight turnarounds</span>
                        </div>
                      </a>
                      <a routerLink="/customer" (click)="closeMenus()" class="flex items-center gap-2.5 px-3 py-2 rounded-xl hover:bg-seg text-ink transition-colors cursor-pointer">
                        <span class="text-base">👤</span>
                        <div class="flex flex-col">
                          <span class="font-semibold text-xs text-ink">Passenger Portal</span>
                          <span class="text-[10px] text-ink-3">Boarding & self-service</span>
                        </div>
                      </a>
                    } @else if (authService.isStaff()) {
                      <a routerLink="/staff" (click)="closeMenus()" class="flex items-center gap-2.5 px-3 py-2 rounded-xl hover:bg-seg text-ink transition-colors cursor-pointer">
                        <span class="text-base">✈️</span>
                        <div class="flex flex-col">
                          <span class="font-semibold text-xs text-ink">Staff Operations</span>
                          <span class="text-[10px] text-ink-3">Operational hub & gates</span>
                        </div>
                      </a>
                      <a routerLink="/customer" (click)="closeMenus()" class="flex items-center gap-2.5 px-3 py-2 rounded-xl hover:bg-seg text-ink transition-colors cursor-pointer">
                        <span class="text-base">👤</span>
                        <div class="flex flex-col">
                          <span class="font-semibold text-xs text-ink">Passenger Check-in</span>
                          <span class="text-[10px] text-ink-3">Verify passenger boarding</span>
                        </div>
                      </a>
                    } @else {
                      <a routerLink="/customer" (click)="closeMenus()" class="flex items-center gap-2.5 px-3 py-2 rounded-xl hover:bg-seg text-ink transition-colors cursor-pointer">
                        <span class="text-base">👤</span>
                        <div class="flex flex-col">
                          <span class="font-semibold text-xs text-ink">My Bookings & Check-in</span>
                          <span class="text-[10px] text-ink-3">Boarding passes & loyalty status</span>
                        </div>
                      </a>
                    }

                    <!-- Flight Search Activity for all authenticated users -->
                    <a routerLink="/" (click)="closeMenus()" class="flex items-center gap-2.5 px-3 py-2 rounded-xl hover:bg-seg text-ink transition-colors cursor-pointer">
                      <span class="text-base">🔍</span>
                      <div class="flex flex-col">
                        <span class="font-semibold text-xs text-ink">Search Flights</span>
                        <span class="text-[10px] text-ink-3">Book flights & live fares</span>
                      </div>
                    </a>

                    <div class="border-t border-line my-1.5"></div>

                    <!-- Sign Out Action -->
                    <button (click)="logout(); closeMenus()" class="w-full flex items-center gap-2.5 px-3 py-2 rounded-xl text-rose-600 dark:text-rose-400 hover:bg-rose-500/10 transition-colors text-left cursor-pointer">
                      <svg class="w-4 h-4 text-rose-500 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 16l4-4m0 0l-4-4m4 4H7m6 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h4a3 3 0 013 3v1"/>
                      </svg>
                      <span class="font-bold text-xs">Sign Out</span>
                    </button>
                  </div>
                }
              </div>
            } @else {
              <!-- Unauthenticated: Login & Register Icons / Buttons -->
              <div class="flex items-center gap-1.5 sm:gap-2">
                <button (click)="openLogin.emit(); closeMenus()"
                        class="flex items-center gap-1.5 px-3 sm:px-3.5 py-1.5 rounded-full bg-seg/70 hover:bg-seg border border-line text-xs font-semibold text-ink transition-all cursor-pointer shadow-sm">
                  <svg class="w-3.5 h-3.5 text-accent" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 16l-4-4m0 0l4-4m-4 4h14m-5 4v1a3 3 0 01-3 3H6a3 3 0 01-3-3V7a3 3 0 013-3h7a3 3 0 013 3v1" />
                  </svg>
                  <span>Sign In</span>
                </button>
                
                <button (click)="openRegister.emit(); closeMenus()"
                        class="flex items-center gap-1.5 px-3 sm:px-3.5 py-1.5 rounded-full bg-accent hover:opacity-95 text-white text-xs font-semibold transition-all cursor-pointer shadow-sm">
                  <svg class="w-3.5 h-3.5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18 9v3m0 0v3m0-3h3m-3 0h-3m-2-5a4 4 0 11-8 0 4 4 0 018 0zM3 20a6 6 0 0112 0v1H3v-1z" />
                  </svg>
                  <span>Register</span>
                </button>
              </div>
            }

          </div>
        </div>
      </div>
    </header>
  `
})
export class NavbarComponent {
  @Output() openLogin = new EventEmitter<void>();
  @Output() openRegister = new EventEmitter<void>();

  isUserMenuOpen = false;

  constructor(
    public authService: AuthService,
    public themeService: ThemeService,
    private router: Router
  ) {}

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.user-menu-container')) {
      this.isUserMenuOpen = false;
    }
  }

  toggleUserMenu(event: MouseEvent): void {
    event.stopPropagation();
    this.isUserMenuOpen = !this.isUserMenuOpen;
  }

  closeMenus(): void {
    this.isUserMenuOpen = false;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}

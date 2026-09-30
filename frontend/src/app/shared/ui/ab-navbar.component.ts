import { Component, EventEmitter, HostListener, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { GamificationService } from '../../core/services/gamification.service';
import { ThemeService } from '../../core/theme/theme.service';

@Component({
  selector: 'ab-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <nav class="sticky top-3 z-50 mx-auto max-w-[1080px] w-[calc(100%-2rem)]">
      <div class="bar flex items-center gap-3 sm:gap-5 h-[58px] px-3 sm:px-4 rounded-full bg-nav border border-line backdrop-blur-2xl shadow-card text-sm font-medium">
        
        <!-- Brand Logo Tile -->
        <a routerLink="/" class="brand flex items-center gap-2.5 font-extrabold text-lg tracking-tight text-ink group cursor-pointer select-none">
          <span class="logo w-[30px] h-[30px] rounded-[10px] bg-brand flex items-center justify-center shadow-[0_6px_16px_-4px_rgba(107,78,255,0.6)] group-hover:scale-105 transition-transform flex-shrink-0">
            <svg class="w-4 h-4 fill-white" viewBox="0 0 24 24" aria-hidden="true">
              <path d="M21 16v-2l-8-5V3.5a1.5 1.5 0 0 0-3 0V9l-8 5v2l8-2.5V19l-2 1.5V22l3.5-1 3.5 1v-1.5L13 19v-5.5z"/>
            </svg>
          </span>
          <span class="font-extrabold text-ink">AeroBook</span>
        </a>

        <!-- Desktop Navigation Links -->
        <div class="links hidden md:flex items-center gap-6 font-semibold text-sm">
          <a
            routerLink="/admin"
            routerLinkActive="on"
            class="text-ink-2 hover:text-ink transition-colors cursor-pointer"
          >
            Admin
          </a>
          <a
            routerLink="/customer"
            routerLinkActive="on"
            class="text-ink-2 hover:text-ink transition-colors cursor-pointer flex items-center gap-1.5"
          >
            <span>Passengers</span>
            @if (authService.isAuthenticated()) {
              <span class="text-[10px] px-1.5 py-0.5 rounded-full bg-accent-soft text-accent font-bold">
                Lv.{{ gamificationService.currentLevel() }}
              </span>
            }
          </a>
          <a
            routerLink="/staff"
            routerLinkActive="on"
            class="text-ink-2 hover:text-ink transition-colors cursor-pointer"
          >
            Staff
          </a>
          <a
            routerLink="/"
            routerLinkActive="on"
            [routerLinkActiveOptions]="{ exact: true }"
            class="text-ink-2 hover:text-ink transition-colors cursor-pointer"
          >
            Search flights
          </a>
        </div>

        <span class="sp flex-1"></span>

        <!-- Theme Mode Switcher -->
        <button
          type="button"
          (click)="themeService.toggle()"
          class="icon-btn w-[38px] h-[38px] rounded-full bg-seg hover:bg-line/40 flex items-center justify-center transition-transform hover:rotate-[18deg] text-ink cursor-pointer"
          [attr.aria-label]="themeService.isDark() ? 'Switch to light mode' : 'Switch to dark mode'"
          [title]="themeService.isDark() ? 'Switch to light mode' : 'Switch to dark mode'"
        >
          @if (themeService.isDark()) {
            <!-- Sun SVG -->
            <svg class="w-4 h-4 stroke-ink" viewBox="0 0 24 24" fill="none" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="4"></circle>
              <path d="M12 2v2M12 20v2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M2 12h2M20 12h2M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4"></path>
            </svg>
          } @else {
            <!-- Moon SVG -->
            <svg class="w-4 h-4 stroke-ink" viewBox="0 0 24 24" fill="none" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M20 14.5A8 8 0 0 1 9.5 4a8 8 0 1 0 10.5 10.5z"></path>
            </svg>
          }
        </button>

        <!-- User Chip or Login Button -->
        @if (authService.isAuthenticated()) {
          <div class="relative user-dropdown">
            <button
              type="button"
              (click)="toggleUserMenu($event)"
              class="avatar flex items-center gap-2 py-1 pr-3 pl-1 rounded-full bg-seg hover:bg-line/40 transition-colors text-ink font-semibold text-xs sm:text-sm cursor-pointer border-0"
            >
              <i class="w-[30px] h-[30px] rounded-full bg-brand text-white not-italic text-xs font-bold flex items-center justify-center shadow-sm">
                {{ getUserInitials() }}
              </i>
              <span class="hidden sm:inline-block max-w-[110px] truncate text-ink">
                {{ getUserDisplayName() }}
              </span>
              <svg class="w-3.5 h-3.5 text-ink-2" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <polyline points="6 9 12 15 18 9"/>
              </svg>
            </button>

            <!-- Dropdown Menu -->
            @if (userMenuOpen) {
              <div
                class="absolute right-0 mt-2 w-52 rounded-[22px] bg-surface border border-line p-2 shadow-card backdrop-blur-2xl z-50 text-xs font-medium"
              >
                <div class="px-3 py-2 border-b border-line text-ink-2">
                  <p class="font-bold text-ink truncate text-sm">
                    {{ authService.currentUser()?.fullName || authService.currentUser()?.username }}
                  </p>
                  <p class="text-[11px] text-accent font-semibold uppercase tracking-wider mt-0.5">
                    {{ getRoleLabel() }}
                  </p>
                </div>
                <button
                  type="button"
                  (click)="navigateToDashboard(); userMenuOpen = false"
                  class="w-full text-left px-3 py-2.5 rounded-xl text-ink hover:bg-seg transition-colors mt-1 flex items-center justify-between"
                >
                  <span>Dashboard</span>
                  <span class="text-xs text-ink-3">→</span>
                </button>
                <button
                  type="button"
                  (click)="logout(); userMenuOpen = false"
                  class="w-full text-left px-3 py-2.5 rounded-xl text-bad hover:bg-bad-soft transition-colors mt-1 font-semibold flex items-center justify-between"
                >
                  <span>Sign out</span>
                  <span class="text-xs">✕</span>
                </button>
              </div>
            }
          </div>
        } @else {
          <div class="flex items-center gap-2">
            <button
              type="button"
              (click)="openLogin.emit()"
              class="btn !py-2 !px-4 !text-xs font-semibold hover:!bg-seg"
            >
              Sign in
            </button>
            <button
              type="button"
              (click)="openRegister.emit()"
              class="btn p !py-2 !px-4 !text-xs font-bold hidden sm:inline-flex"
            >
              Register
            </button>
          </div>
        }

        <!-- Mobile Menu Toggle Button -->
        <button
          type="button"
          (click)="mobileMenuOpen = !mobileMenuOpen"
          class="md:hidden icon-btn w-[38px] h-[38px] rounded-full bg-seg flex items-center justify-center text-ink cursor-pointer"
          aria-label="Toggle mobile menu"
        >
          <svg class="w-4 h-4 stroke-ink" viewBox="0 0 24 24" fill="none" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            @if (mobileMenuOpen) {
              <line x1="18" y1="6" x2="6" y2="18"></line>
              <line x1="6" y1="6" x2="18" y2="18"></line>
            } @else {
              <line x1="3" y1="12" x2="21" y2="12"></line>
              <line x1="3" y1="6" x2="21" y2="6"></line>
              <line x1="3" y1="18" x2="21" y2="18"></line>
            }
          </svg>
        </button>
      </div>

      <!-- Mobile Dropdown Menu Sheet -->
      @if (mobileMenuOpen) {
        <div class="md:hidden mt-2 p-4 rounded-[24px] bg-surface border border-line backdrop-blur-2xl shadow-card flex flex-col gap-1.5 text-sm font-semibold">
          <a
            routerLink="/"
            [routerLinkActiveOptions]="{ exact: true }"
            routerLinkActive="bg-seg text-ink"
            (click)="mobileMenuOpen = false"
            class="px-4 py-2.5 rounded-xl hover:bg-seg text-ink-2 hover:text-ink transition-colors"
          >
            Search flights
          </a>
          <a
            routerLink="/customer"
            routerLinkActive="bg-seg text-ink"
            (click)="mobileMenuOpen = false"
            class="px-4 py-2.5 rounded-xl hover:bg-seg text-ink-2 hover:text-ink transition-colors flex items-center justify-between"
          >
            <span>Passenger portal</span>
            @if (authService.isAuthenticated()) {
              <span class="text-xs px-2 py-0.5 rounded-full bg-accent-soft text-accent">
                Lv.{{ gamificationService.currentLevel() }}
              </span>
            }
          </a>
          <a
            routerLink="/staff"
            routerLinkActive="bg-seg text-ink"
            (click)="mobileMenuOpen = false"
            class="px-4 py-2.5 rounded-xl hover:bg-seg text-ink-2 hover:text-ink transition-colors"
          >
            Staff operations
          </a>
          <a
            routerLink="/admin"
            routerLinkActive="bg-seg text-ink"
            (click)="mobileMenuOpen = false"
            class="px-4 py-2.5 rounded-xl hover:bg-seg text-ink-2 hover:text-ink transition-colors"
          >
            Admin console
          </a>

          @if (!authService.isAuthenticated()) {
            <div class="pt-3 border-t border-line flex gap-2">
              <button
                type="button"
                (click)="openLogin.emit(); mobileMenuOpen = false"
                class="btn flex-1 !text-xs !py-2.5"
              >
                Sign in
              </button>
              <button
                type="button"
                (click)="openRegister.emit(); mobileMenuOpen = false"
                class="btn p flex-1 !text-xs !py-2.5"
              >
                Register
              </button>
            </div>
          }
        </div>
      }
    </nav>
  `
})
export class AbNavbarComponent {
  @Output() openLogin = new EventEmitter<void>();
  @Output() openRegister = new EventEmitter<void>();

  userMenuOpen = false;
  mobileMenuOpen = false;

  constructor(
    public authService: AuthService,
    public gamificationService: GamificationService,
    public themeService: ThemeService,
    private router: Router
  ) {}

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement;
    if (!target.closest('.user-dropdown')) {
      this.userMenuOpen = false;
    }
  }

  toggleUserMenu(event: MouseEvent): void {
    event.stopPropagation();
    this.userMenuOpen = !this.userMenuOpen;
  }

  getUserInitials(): string {
    const u = this.authService.currentUser();
    if (!u) return 'AB';
    if (u.fullName) {
      const parts = u.fullName.trim().split(/\s+/);
      if (parts.length >= 2) {
        return (parts[0][0] + parts[1][0]).toUpperCase();
      }
      return parts[0].slice(0, 2).toUpperCase();
    }
    return (u.username || 'AB').slice(0, 2).toUpperCase();
  }

  getUserDisplayName(): string {
    const u = this.authService.currentUser();
    if (!u) return 'Guest';
    return u.fullName || u.username || 'Account';
  }

  getRoleLabel(): string {
    if (this.authService.isAdmin()) return 'System Admin';
    if (this.authService.isStaff()) return 'Airport Staff';
    return 'Passenger';
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

import { Injectable, signal, computed } from '@angular/core';

export type AppTheme = 'light' | 'dark';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly STORAGE_KEY = 'ab-theme';

  readonly theme = signal<AppTheme>(this.getInitialTheme());
  readonly isDark = computed(() => this.theme() === 'dark');

  constructor() {
    this.applyTheme(this.theme());
  }

  toggle(): void {
    const nextTheme: AppTheme = this.theme() === 'light' ? 'dark' : 'light';
    this.setTheme(nextTheme);
  }

  // Alias for backward compatibility
  toggleTheme(): void {
    this.toggle();
  }

  setTheme(newTheme: AppTheme): void {
    this.theme.set(newTheme);
    try {
      localStorage.setItem(this.STORAGE_KEY, newTheme);
    } catch {
      // LocalStorage might be disabled or unavailable
    }
    this.applyTheme(newTheme);
  }

  private getInitialTheme(): AppTheme {
    try {
      const stored = localStorage.getItem(this.STORAGE_KEY);
      if (stored === 'dark' || stored === 'light') {
        return stored;
      }
    } catch {
      // Fallback
    }
    if (typeof window !== 'undefined' && window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
      return 'dark';
    }
    return 'light';
  }

  private applyTheme(theme: AppTheme): void {
    if (typeof document === 'undefined') return;

    document.documentElement.dataset['theme'] = theme;
    if (theme === 'dark') {
      document.documentElement.classList.add('dark');
      document.documentElement.classList.remove('light');
    } else {
      document.documentElement.classList.remove('dark');
      document.documentElement.classList.add('light');
    }
  }
}

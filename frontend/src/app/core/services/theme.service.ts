import { Injectable, signal, computed } from '@angular/core';

export type AppTheme = 'light' | 'dark';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly STORAGE_KEY = 'aerobook_theme';

  // Reactive state signal - defaults to 'light' (current theme)
  readonly theme = signal<AppTheme>(this.getInitialTheme());

  // Computed helper
  readonly isDark = computed(() => this.theme() === 'dark');

  constructor() {
    this.applyTheme(this.theme());
  }

  toggleTheme(): void {
    const nextTheme: AppTheme = this.theme() === 'light' ? 'dark' : 'light';
    this.setTheme(nextTheme);
  }

  setTheme(newTheme: AppTheme): void {
    this.theme.set(newTheme);
    try {
      localStorage.setItem(this.STORAGE_KEY, newTheme);
    } catch {
      // LocalStorage unavailable
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
    return 'light'; // Default to current light theme
  }

  private applyTheme(theme: AppTheme): void {
    if (typeof document === 'undefined') return;

    const root = document.documentElement;
    const body = document.body;

    root.dataset['theme'] = theme;
    if (theme === 'dark') {
      root.classList.add('dark');
      root.classList.remove('light');
      body.classList.add('theme-dark');
      body.classList.remove('theme-light');
    } else {
      root.classList.remove('dark');
      root.classList.add('light');
      body.classList.remove('theme-dark');
      body.classList.add('theme-light');
    }
  }
}

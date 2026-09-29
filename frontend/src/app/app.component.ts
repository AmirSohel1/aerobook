import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './shared/components/navbar/navbar.component';
import { LoginModalComponent } from './shared/components/login-modal/login-modal.component';
import { RegisterModalComponent } from './shared/components/register-modal/register-modal.component';
import { ThemeService } from './core/services/theme.service';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NavbarComponent, LoginModalComponent, RegisterModalComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'AeroBook';

  constructor(
    public themeService: ThemeService,
    public authService: AuthService
  ) {}

  openLogin(): void {
    this.authService.openLogin();
  }

  openRegister(): void {
    this.authService.openRegister();
  }

  closeModals(): void {
    this.authService.closeModals();
  }
}

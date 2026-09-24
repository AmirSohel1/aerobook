import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './shared/components/navbar/navbar.component';
import { LoginModalComponent } from './shared/components/login-modal/login-modal.component';
import { RegisterModalComponent } from './shared/components/register-modal/register-modal.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NavbarComponent, LoginModalComponent, RegisterModalComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'AeroBook';
  showLoginModal = false;
  showRegisterModal = false;

  openLogin(): void {
    this.showLoginModal = true;
    this.showRegisterModal = false;
  }

  openRegister(): void {
    this.showRegisterModal = true;
    this.showLoginModal = false;
  }

  closeModals(): void {
    this.showLoginModal = false;
    this.showRegisterModal = false;
  }
}

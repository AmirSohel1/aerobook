import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { CustomerDashboardComponent } from './features/customer/customer-dashboard.component';
import { StaffDashboardComponent } from './features/staff/staff-dashboard.component';
import { AdminDashboardComponent } from './features/admin/admin-dashboard.component';
import { customerGuard, staffGuard, adminGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    component: HomeComponent
  },
  {
    path: 'customer',
    component: CustomerDashboardComponent,
    canActivate: [customerGuard]
  },
  {
    path: 'staff',
    component: StaffDashboardComponent,
    canActivate: [staffGuard]
  },
  {
    path: 'admin',
    component: AdminDashboardComponent,
    canActivate: [adminGuard]
  },
  {
    path: '**',
    redirectTo: ''
  }
];

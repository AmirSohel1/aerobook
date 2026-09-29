import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

/**
 * Universal authentication guard ensuring user has an active session.
 * Unauthenticated requests are redirected safely to the landing page.
 */
export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isAuthenticated()) {
    return true;
  }
  return router.createUrlTree(['/']);
};

/**
 * Passenger / Customer route guard:
 * Grants access to registered passengers (ROLE_USER) and platform administrators (ROLE_ADMIN).
 * Redirects unauthorized users to the landing page.
 */
export const customerGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isAuthenticated() && (auth.isCustomer() || auth.isAdmin())) {
    return true;
  }
  return router.createUrlTree(['/']);
};

/**
 * Staff operations route guard:
 * Grants access to airline staff (ROLE_STAFF) and administrators (ROLE_ADMIN).
 * Redirects unauthorized users to the landing page.
 */
export const staffGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isAuthenticated() && (auth.isStaff() || auth.isAdmin())) {
    return true;
  }
  return router.createUrlTree(['/']);
};

/**
 * Administrator route guard:
 * Strictly enforces administrative privileges (ROLE_ADMIN).
 * Redirects non-admin or unauthenticated users to the landing page.
 */
export const adminGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (auth.isAuthenticated() && auth.isAdmin()) {
    return true;
  }
  return router.createUrlTree(['/']);
};


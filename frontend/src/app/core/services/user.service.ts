import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/aerobook.models';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private readonly gatewayUrl = 'http://localhost:8083';

  constructor(private http: HttpClient) {}

  getUserById(id: number): Observable<User> {
    return this.http.get<User>(`${this.gatewayUrl}/api/v1/users/${id}`);
  }

  updateUser(id: number, user: Partial<User>): Observable<User> {
    return this.http.put<User>(`${this.gatewayUrl}/api/v1/users/${id}`, user);
  }

  // Admin: View all registered users
  getAllUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.gatewayUrl}/api/v1/users`);
  }

  // Admin: View credentials & security roles
  getAllCredentials(): Observable<any[]> {
    return this.http.get<any[]>(`${this.gatewayUrl}/api/auth/credentials`);
  }

  // Admin: Update user security role (e.g. promote to ROLE_STAFF or ROLE_ADMIN)
  updateUserRole(userId: number, role: string): Observable<any> {
    return this.http.put(`${this.gatewayUrl}/api/auth/credentials/${userId}/role`, null, {
      params: { role }
    });
  }

  // Admin: Delete user account
  deleteUser(userId: number): Observable<string> {
    return this.http.delete(`${this.gatewayUrl}/api/v1/users/${userId}`, { responseType: 'text' });
  }
}


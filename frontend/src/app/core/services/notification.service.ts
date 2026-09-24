import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { BroadcastRequest, Notification } from '../models/aerobook.models';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private readonly gatewayUrl = 'http://localhost:8083';

  constructor(private http: HttpClient) {}

  getUserNotifications(userId: number): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.gatewayUrl}/api/notifications/user/${userId}`);
  }

  getUnreadCount(userId: number): Observable<{ userId: number; unreadCount: number }> {
    return this.http.get<{ userId: number; unreadCount: number }>(`${this.gatewayUrl}/api/notifications/unread-count/${userId}`);
  }

  markAsRead(id: number): Observable<Notification> {
    return this.http.put<Notification>(`${this.gatewayUrl}/api/notifications/${id}/read`, null);
  }

  markAllAsRead(userId: number): Observable<{ message: string }> {
    return this.http.put<{ message: string }>(`${this.gatewayUrl}/api/notifications/user/${userId}/read-all`, null);
  }

  broadcastAlert(request: BroadcastRequest): Observable<Notification> {
    return this.http.post<Notification>(`${this.gatewayUrl}/api/notifications/broadcast`, request);
  }

  getAllNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.gatewayUrl}/api/notifications`);
  }
}


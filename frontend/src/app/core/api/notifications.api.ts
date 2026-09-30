import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import {
  BroadcastRequest,
  NotificationRequest,
  NotificationResponse,
  PageParams
} from './types';

@Injectable({
  providedIn: 'root'
})
export class NotificationsApi {
  constructor(private api: ApiService) {}

  getUserNotifications(userId: number, params?: PageParams): Observable<NotificationResponse[]> {
    const validId = userId || 3;
    return this.api.get<NotificationResponse[]>(`/api/notifications/user/${validId}`, params);
  }

  getUnreadCount(userId: number): Observable<{ userId: number; unreadCount: number }> {
    const validId = userId || 3;
    return this.api.get<{ userId: number; unreadCount: number }>(`/api/notifications/unread-count/${validId}`);
  }

  markAsRead(id: number): Observable<NotificationResponse> {
    return this.api.put<NotificationResponse>(`/api/notifications/${id}/read`, null);
  }

  markAllAsRead(userId: number): Observable<{ message: string }> {
    const validId = userId || 3;
    return this.api.put<{ message: string }>(`/api/notifications/user/${validId}/read-all`, null);
  }

  send(request: NotificationRequest): Observable<NotificationResponse> {
    return this.api.post<NotificationResponse>('/api/notifications/send', request);
  }

  broadcast(request: BroadcastRequest): Observable<NotificationResponse> {
    return this.api.post<NotificationResponse>('/api/notifications/broadcast', request);
  }

  getAll(params?: PageParams): Observable<NotificationResponse[]> {
    return this.api.get<NotificationResponse[]>('/api/notifications', params);
  }

  health(): Observable<any> {
    return this.api.get('/api/notifications/health');
  }
}

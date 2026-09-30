import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { BroadcastRequest, NotificationResponse } from './types';

@Injectable({
  providedIn: 'root'
})
export class BroadcastApi {
  constructor(private api: ApiService) {}

  dispatchEmergency(request: BroadcastRequest): Observable<NotificationResponse> {
    return this.api.post<NotificationResponse>('/api/notifications/broadcast', request);
  }

  dispatchFlightDelay(flightNumber: string, message: string): Observable<NotificationResponse> {
    return this.api.post<NotificationResponse>('/api/notifications/broadcast', {
      title: `Operational Notice: Flight ${flightNumber}`,
      message,
      type: 'FLIGHT_DELAY',
      flightNumber
    });
  }

  dispatchGateChange(flightNumber: string, newGate: string): Observable<NotificationResponse> {
    return this.api.post<NotificationResponse>('/api/notifications/broadcast', {
      title: `Gate Change: Flight ${flightNumber}`,
      message: `Departure gate reassigned to ${newGate}. Please proceed promptly.`,
      type: 'GATE_CHANGE',
      flightNumber
    });
  }
}

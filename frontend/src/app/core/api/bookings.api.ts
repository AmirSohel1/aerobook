import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import {
  BookingRequest,
  BookingResponse,
  PageParams
} from './types';

@Injectable({
  providedIn: 'root'
})
export class BookingsApi {
  constructor(private api: ApiService) {}

  create(request: BookingRequest): Observable<BookingResponse> {
    return this.api.post<BookingResponse>('/api/bookings', request);
  }

  getById(id: number): Observable<BookingResponse> {
    return this.api.get<BookingResponse>(`/api/bookings/${id}`);
  }

  getByPnr(pnr: string): Observable<BookingResponse> {
    return this.api.get<BookingResponse>(`/api/bookings/pnr/${encodeURIComponent(pnr)}`);
  }

  getByUser(userId: number, params?: PageParams): Observable<BookingResponse[]> {
    return this.api.get<BookingResponse[]>(`/api/bookings/user/${userId}`, params);
  }

  getByFlight(flightId: number): Observable<BookingResponse[]> {
    return this.api.get<BookingResponse[]>(`/api/bookings/flight/${flightId}`);
  }

  getAll(params?: PageParams): Observable<BookingResponse[]> {
    return this.api.get<BookingResponse[]>('/api/bookings', params);
  }

  cancel(bookingId: number): Observable<string> {
    return this.api.delete<string>(`/api/bookings/${bookingId}`);
  }

  health(): Observable<any> {
    return this.api.get('/api/bookings/health');
  }
}

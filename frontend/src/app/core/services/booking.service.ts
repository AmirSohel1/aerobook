import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Booking, BookingRequest } from '../models/aerobook.models';

@Injectable({
  providedIn: 'root'
})
export class BookingService {
  private readonly gatewayUrl = 'http://localhost:8083';

  constructor(private http: HttpClient) {}

  createBooking(request: BookingRequest): Observable<Booking> {
    return this.http.post<Booking>(`${this.gatewayUrl}/api/bookings`, request);
  }

  getBookingById(id: number): Observable<Booking> {
    return this.http.get<Booking>(`${this.gatewayUrl}/api/bookings/${id}`);
  }

  getBookingByPnr(pnr: string): Observable<Booking> {
    return this.http.get<Booking>(`${this.gatewayUrl}/api/bookings/pnr/${pnr}`);
  }

  getBookingsByUser(userId: number): Observable<Booking[]> {
    return this.http.get<Booking[]>(`${this.gatewayUrl}/api/bookings/user/${userId}`);
  }

  // Staff manifest: view passengers on a flight
  getBookingsByFlight(flightId: number): Observable<Booking[]> {
    return this.http.get<Booking[]>(`${this.gatewayUrl}/api/bookings/flight/${flightId}`);
  }

  // Staff and Admin: View all airline reservations
  getAllBookings(): Observable<Booking[]> {
    return this.http.get<Booking[]>(`${this.gatewayUrl}/api/bookings`);
  }

  cancelBooking(bookingId: number): Observable<string> {
    return this.http.delete(`${this.gatewayUrl}/api/bookings/${bookingId}`, { responseType: 'text' });
  }
}


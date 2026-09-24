import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CheckIn } from '../models/aerobook.models';

@Injectable({
  providedIn: 'root'
})
export class CheckInService {
  private readonly gatewayUrl = 'http://localhost:8083';

  constructor(private http: HttpClient) {}

  checkIn(request: { bookingId: number; passengerName: string; seatNumber?: string }): Observable<CheckIn> {
    return this.http.post<CheckIn>(`${this.gatewayUrl}/api/check-ins`, request);
  }

  getByBookingId(bookingId: number): Observable<CheckIn> {
    return this.http.get<CheckIn>(`${this.gatewayUrl}/api/check-ins/booking/${bookingId}`);
  }

  // Staff & Admin: List all check-ins
  getAllCheckIns(): Observable<CheckIn[]> {
    return this.http.get<CheckIn[]>(`${this.gatewayUrl}/api/check-ins`);
  }

  // Staff Gate Scanner: Verify boarding pass and mark as BOARDED
  verifyBoardingPass(boardingPassNumber: string): Observable<CheckIn> {
    return this.http.post<CheckIn>(`${this.gatewayUrl}/api/check-ins/verify`, null, {
      params: { boardingPassNumber }
    });
  }
}


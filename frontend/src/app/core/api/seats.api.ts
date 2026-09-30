import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import {
  SeatHoldRequest,
  SeatHoldResponse,
  SeatMapResponse
} from './types';

@Injectable({
  providedIn: 'root'
})
export class SeatsApi {
  constructor(private api: ApiService) {}

  getSeatMap(flightId: number): Observable<SeatMapResponse> {
    return this.api.get<SeatMapResponse>(`/api/flights/${flightId}/seatmap`);
  }

  holdSeat(request: SeatHoldRequest): Observable<SeatHoldResponse> {
    return this.api.post<SeatHoldResponse>('/api/flights/seats/hold', request);
  }

  releaseSeat(flightId: number, seatNumber: string): Observable<string> {
    return this.api.delete<string>(`/api/flights/${flightId}/seats/${seatNumber}/hold`);
  }
}

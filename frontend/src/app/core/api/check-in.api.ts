import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import {
  CheckInRequest,
  CheckInResponse,
  PageParams
} from './types';

@Injectable({
  providedIn: 'root'
})
export class CheckInApi {
  constructor(private api: ApiService) {}

  checkIn(request: CheckInRequest): Observable<CheckInResponse> {
    return this.api.post<CheckInResponse>('/api/check-ins', request);
  }

  getByBookingId(bookingId: number): Observable<CheckInResponse> {
    return this.api.get<CheckInResponse>(`/api/check-ins/booking/${bookingId}`);
  }

  getById(id: number): Observable<CheckInResponse> {
    return this.api.get<CheckInResponse>(`/api/check-ins/${id}`);
  }

  getAll(params?: PageParams): Observable<CheckInResponse[]> {
    return this.api.get<CheckInResponse[]>('/api/check-ins', params);
  }

  verifyBoardingPass(boardingPassNumber: string): Observable<CheckInResponse> {
    return this.api.post<CheckInResponse>('/api/check-ins/verify', null, { boardingPassNumber });
  }

  health(): Observable<any> {
    return this.api.get('/api/check-ins/health');
  }
}

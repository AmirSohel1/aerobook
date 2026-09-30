import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import {
  CreateFlightRequest,
  FlightResponse,
  FlightStatus,
  PageParams,
  UpdateFlightRequest
} from './types';

@Injectable({
  providedIn: 'root'
})
export class FlightsApi {
  constructor(private api: ApiService) {}

  search(source: string, destination: string): Observable<FlightResponse[]> {
    return this.api.get<FlightResponse[]>('/api/flights/search', { source, destination });
  }

  getAll(params?: PageParams): Observable<FlightResponse[]> {
    return this.api.get<FlightResponse[]>('/api/flights', params);
  }

  getById(id: number): Observable<FlightResponse> {
    return this.api.get<FlightResponse>(`/api/flights/${id}`);
  }

  updateStatus(id: number, status: FlightStatus): Observable<FlightResponse> {
    return this.api.put<FlightResponse>(`/api/flights/${id}/status`, null, { status });
  }

  // Admin Scheduling
  schedule(flight: CreateFlightRequest): Observable<FlightResponse> {
    return this.api.post<FlightResponse>('/api/admin/flights', flight);
  }

  updateSchedule(id: number, flight: UpdateFlightRequest): Observable<FlightResponse> {
    return this.api.put<FlightResponse>(`/api/admin/flights/${id}`, flight);
  }

  deleteSchedule(id: number): Observable<string> {
    return this.api.delete<string>(`/api/admin/flights/${id}`);
  }

  getAdminFlights(params?: PageParams): Observable<FlightResponse[]> {
    return this.api.get<FlightResponse[]>('/api/admin/flights', params);
  }

  health(): Observable<string> {
    return this.api.get<string>('/api/flights/health');
  }
}

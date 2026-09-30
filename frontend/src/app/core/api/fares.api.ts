import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { FareRequest, FareResponse, PageParams } from './types';

@Injectable({
  providedIn: 'root'
})
export class FaresApi {
  constructor(private api: ApiService) {}

  getAll(params?: PageParams): Observable<FareResponse[]> {
    return this.api.get<FareResponse[]>('/api/fares', params);
  }

  getById(id: number): Observable<FareResponse> {
    return this.api.get<FareResponse>(`/api/fares/${id}`);
  }

  getByFlightId(flightId: number): Observable<FareResponse> {
    return this.api.get<FareResponse>(`/api/fares/flight/${flightId}`);
  }

  create(fare: FareRequest): Observable<FareResponse> {
    return this.api.post<FareResponse>('/api/fares', fare);
  }

  update(id: number, fare: Partial<FareRequest>): Observable<FareResponse> {
    return this.api.put<FareResponse>(`/api/fares/${id}`, fare);
  }

  delete(id: number): Observable<string> {
    return this.api.delete<string>(`/api/fares/${id}`);
  }

  health(): Observable<any> {
    return this.api.get('/api/fares/health');
  }
}

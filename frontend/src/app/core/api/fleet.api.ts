import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { AircraftResponse, CreateAircraftRequest, PageParams } from './types';

@Injectable({
  providedIn: 'root'
})
export class FleetApi {
  constructor(private api: ApiService) {}

  getAll(params?: PageParams): Observable<AircraftResponse[]> {
    return this.api.get<AircraftResponse[]>('/api/admin/aircrafts', params);
  }

  getById(id: number): Observable<AircraftResponse> {
    return this.api.get<AircraftResponse>(`/api/admin/aircrafts/${id}`);
  }

  register(aircraft: CreateAircraftRequest): Observable<AircraftResponse> {
    return this.api.post<AircraftResponse>('/api/admin/aircrafts', aircraft);
  }

  delete(id: number): Observable<string> {
    return this.api.delete<string>(`/api/admin/aircrafts/${id}`);
  }
}

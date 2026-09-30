import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import {
  IncidentReportRequest,
  IncidentReportResponse,
  IncidentStatus,
  PageParams
} from './types';

@Injectable({
  providedIn: 'root'
})
export class IncidentsApi {
  constructor(private api: ApiService) {}

  getAll(params?: PageParams & { status?: IncidentStatus }): Observable<IncidentReportResponse[]> {
    return this.api.get<IncidentReportResponse[]>('/api/v1/users/incidents', params);
  }

  getById(id: number): Observable<IncidentReportResponse> {
    return this.api.get<IncidentReportResponse>(`/api/v1/users/incidents/${id}`);
  }

  create(report: IncidentReportRequest): Observable<IncidentReportResponse> {
    return this.api.post<IncidentReportResponse>('/api/v1/users/incidents', report);
  }

  updateStatus(id: number, status: IncidentStatus): Observable<IncidentReportResponse> {
    return this.api.put<IncidentReportResponse>(`/api/v1/users/incidents/${id}/status`, null, { status });
  }

  dismiss(id: number): Observable<string> {
    return this.api.delete<string>(`/api/v1/users/incidents/${id}`);
  }
}

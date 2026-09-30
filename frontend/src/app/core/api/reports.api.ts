import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import { AdminDashboardSummary } from './types';

@Injectable({
  providedIn: 'root'
})
export class ReportsApi {
  constructor(private api: ApiService) {}

  getDashboardSummary(): Observable<AdminDashboardSummary> {
    return this.api.get<AdminDashboardSummary>('/api/gateway/admin/summary');
  }

  getGatewayServices(): Observable<any> {
    return this.api.get('/api/gateway/services');
  }

  getGatewayHealth(): Observable<any> {
    return this.api.get('/api/gateway/health');
  }
}

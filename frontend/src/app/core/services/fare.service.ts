import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Fare } from '../models/aerobook.models';

@Injectable({
  providedIn: 'root'
})
export class FareService {
  private readonly gatewayUrl = 'http://localhost:8083';

  constructor(private http: HttpClient) {}

  getAllFares(): Observable<Fare[]> {
    return this.http.get<Fare[]>(`${this.gatewayUrl}/api/fares`);
  }

  getFareByFlightId(flightId: number): Observable<Fare> {
    return this.http.get<Fare>(`${this.gatewayUrl}/api/fares/flight/${flightId}`);
  }

  createFare(fare: Partial<Fare>): Observable<Fare> {
    return this.http.post<Fare>(`${this.gatewayUrl}/api/fares`, fare);
  }

  updateFare(id: number, fare: Partial<Fare>): Observable<Fare> {
    return this.http.put<Fare>(`${this.gatewayUrl}/api/fares/${id}`, fare);
  }

  deleteFare(id: number): Observable<string> {
    return this.http.delete(`${this.gatewayUrl}/api/fares/${id}`, { responseType: 'text' });
  }
}


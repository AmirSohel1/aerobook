import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Aircraft, Flight } from '../models/aerobook.models';

@Injectable({
  providedIn: 'root'
})
export class FlightService {
  private readonly gatewayUrl = 'http://localhost:8083';

  constructor(private http: HttpClient) {}

  searchFlights(source: string, destination: string): Observable<Flight[]> {
    return this.http.get<Flight[]>(`${this.gatewayUrl}/api/flights/search`, {
      params: { source, destination }
    });
  }

  getAllFlights(): Observable<Flight[]> {
    return this.http.get<Flight[]>(`${this.gatewayUrl}/api/flights`);
  }

  getFlightById(id: number): Observable<Flight> {
    return this.http.get<Flight>(`${this.gatewayUrl}/api/flights/${id}`);
  }

  // Staff & Admin status update (BOARDING, DELAYED, DEPARTED, COMPLETED)
  updateFlightStatus(id: number, status: string): Observable<Flight> {
    return this.http.put<Flight>(`${this.gatewayUrl}/api/flights/${id}/status`, null, {
      params: { status }
    });
  }

  // Admin: Schedule new flight
  createFlight(flight: Partial<Flight>): Observable<Flight> {
    return this.http.post<Flight>(`${this.gatewayUrl}/api/admin/flights`, flight);
  }

  // Admin: Update flight schedule
  updateFlight(id: number, flight: Partial<Flight>): Observable<Flight> {
    return this.http.put<Flight>(`${this.gatewayUrl}/api/admin/flights/${id}`, flight);
  }

  // Admin: Delete flight schedule
  deleteFlight(id: number): Observable<string> {
    return this.http.delete(`${this.gatewayUrl}/api/admin/flights/${id}`, { responseType: 'text' });
  }

  // Fleet Aircraft management
  getAllAircraft(): Observable<Aircraft[]> {
    return this.http.get<Aircraft[]>(`${this.gatewayUrl}/api/admin/aircrafts`);
  }

  createAircraft(aircraft: Partial<Aircraft>): Observable<Aircraft> {
    return this.http.post<Aircraft>(`${this.gatewayUrl}/api/admin/aircrafts`, aircraft);
  }

  deleteAircraft(id: number): Observable<string> {
    return this.http.delete(`${this.gatewayUrl}/api/admin/aircrafts/${id}`, { responseType: 'text' });
  }
}


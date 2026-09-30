import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

export interface MealOption {
  id: string;
  code: string;
  name: string;
  category: 'VEG' | 'NON_VEG' | 'DIABETIC' | 'KIDS' | 'JAIN';
  price: number;
  description: string;
}

@Injectable({
  providedIn: 'root'
})
export class MealsApi {
  constructor(private api: ApiService) {}

  getAvailableMeals(flightId?: number): Observable<MealOption[]> {
    return this.api.get<MealOption[]>('/api/flights/meals', flightId ? { flightId } : undefined);
  }

  selectMeal(bookingId: number, passengerId: number, mealCode: string): Observable<{ message: string }> {
    return this.api.post<{ message: string }>(`/api/bookings/${bookingId}/passengers/${passengerId}/meal`, { mealCode });
  }
}

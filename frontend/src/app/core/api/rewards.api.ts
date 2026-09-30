import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { ApiService } from './api.service';

export interface LoyaltyProfileResponse {
  userId: number;
  currentLevel: number;
  levelTitle: string;
  tier: string;
  currentXp: number;
  xpToNextLevel: number;
  progressPercent: number;
  consecutiveTripStreak: number;
}

@Injectable({
  providedIn: 'root'
})
export class RewardsApi {
  constructor(private api: ApiService) {}

  getProfile(userId: number): Observable<LoyaltyProfileResponse> {
    const validId = userId || 3;
    return this.api.get<LoyaltyProfileResponse>(`/api/v1/users/${validId}/rewards`);
  }

  claimQuest(userId: number, questId: string): Observable<{ success: boolean; message: string }> {
    const validId = userId || 3;
    return this.api.post<{ success: boolean; message: string }>(`/api/v1/users/${validId}/rewards/quests/${questId}/claim`, {});
  }
}

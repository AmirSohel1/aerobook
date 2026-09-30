import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import {
  CouponDto,
  CouponValidateRequest,
  CouponValidateResponse,
  PageParams
} from './types';

@Injectable({
  providedIn: 'root'
})
export class CouponsApi {
  constructor(private api: ApiService) {}

  getAll(params?: PageParams): Observable<CouponDto[]> {
    return this.api.get<CouponDto[]>('/api/fares/coupons', params);
  }

  getById(id: number): Observable<CouponDto> {
    return this.api.get<CouponDto>(`/api/fares/coupons/${id}`);
  }

  create(coupon: CouponDto): Observable<CouponDto> {
    return this.api.post<CouponDto>('/api/fares/coupons', coupon);
  }

  update(id: number, coupon: Partial<CouponDto>): Observable<CouponDto> {
    return this.api.put<CouponDto>(`/api/fares/coupons/${id}`, coupon);
  }

  toggleStatus(id: number, status: 'ACTIVE' | 'INACTIVE'): Observable<CouponDto> {
    return this.api.put<CouponDto>(`/api/fares/coupons/${id}`, { status });
  }

  delete(id: number): Observable<string> {
    return this.api.delete<string>(`/api/fares/coupons/${id}`);
  }

  validate(request: CouponValidateRequest): Observable<CouponValidateResponse> {
    return this.api.post<CouponValidateResponse>('/api/fares/coupons/validate', request);
  }
}

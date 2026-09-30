import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';
import {
  PageParams,
  PaymentOrderRequest,
  PaymentOrderResponse,
  PaymentRecord,
  PaymentRefundRequest,
  PaymentRefundResponse,
  PaymentVerifyRequest,
  PaymentVerifyResponse
} from './types';

@Injectable({
  providedIn: 'root'
})
export class PaymentsApi {
  constructor(private api: ApiService) {}

  createOrder(request: PaymentOrderRequest): Observable<PaymentOrderResponse> {
    return this.api.post<PaymentOrderResponse>('/api/payments/orders', request);
  }

  verifyPayment(request: PaymentVerifyRequest): Observable<PaymentVerifyResponse> {
    return this.api.post<PaymentVerifyResponse>('/api/payments/verify', request);
  }

  processRefund(paymentId: string, request?: PaymentRefundRequest): Observable<PaymentRefundResponse> {
    return this.api.post<PaymentRefundResponse>(`/api/payments/${paymentId}/refund`, request || {});
  }

  getAllPayments(params?: PageParams): Observable<PaymentRecord[]> {
    return this.api.get<PaymentRecord[]>('/api/payments', params);
  }

  getPaymentByBooking(bookingId: number): Observable<PaymentRecord> {
    return this.api.get<PaymentRecord>(`/api/payments/booking/${bookingId}`);
  }

  health(): Observable<any> {
    return this.api.get('/api/payments/health');
  }
}

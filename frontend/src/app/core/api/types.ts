// ============================================================================
// AeroBook Typed API DTOs - Mirroring Backend Models Exactly
// ============================================================================

export type RoleType = 'ROLE_USER' | 'ROLE_STAFF' | 'ROLE_ADMIN';

// ----------------------------------------------------------------------------
// 1. Pagination & Query Types
// ----------------------------------------------------------------------------
export interface PageParams {
  page?: number;
  size?: number;
  sort?: string;
  direction?: 'ASC' | 'DESC';
  search?: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
  last: boolean;
}

// ----------------------------------------------------------------------------
// 2. Auth DTOs
// ----------------------------------------------------------------------------
export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber?: string;
  dateOfBirth?: string; // YYYY-MM-DD
  nationality?: string;
  password: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  role: RoleType;
  userId: number;
}

export interface CredentialResponse {
  id: number;
  email: string;
  role: RoleType;
  createdAt: string;
  lastLogin?: string;
}

// ----------------------------------------------------------------------------
// 3. User DTOs
// ----------------------------------------------------------------------------
export interface UserRequest {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  dateOfBirth?: string;
  nationality?: string;
  role?: string;
}

export interface UserResponse {
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  dateOfBirth?: string;
  nationality?: string;
  role: string;
}

export interface TravelerProfile extends UserResponse {
  passportNumber?: string;
  passportExpiry?: string;
  frequentFlyerNumber?: string;
  preferredSeat?: 'WINDOW' | 'AISLE' | 'ANY';
  mealPreference?: string;
  specialAssistance?: string;
  emergencyContactName?: string;
  emergencyContactPhone?: string;
}

// ----------------------------------------------------------------------------
// 4. Flight DTOs
// ----------------------------------------------------------------------------
export type FlightStatus =
  | 'SCHEDULED'
  | 'BOARDING'
  | 'DELAYED'
  | 'DEPARTED'
  | 'COMPLETED'
  | 'CANCELLED';

export interface CreateFlightRequest {
  flightNumber: string;
  airlineName: string;
  source: string;
  destination: string;
  departureTime: string; // ISO-8601 UTC
  arrivalTime: string;   // ISO-8601 UTC
  totalSeats: number;
  baseFare: number;
  aircraftId: number;
}

export interface UpdateFlightRequest {
  airlineName?: string;
  source?: string;
  destination?: string;
  departureTime?: string;
  arrivalTime?: string;
  totalSeats?: number;
  baseFare?: number;
}

export interface FlightResponse {
  id: number;
  flightNumber: string;
  airlineName: string;
  source: string;
  destination: string;
  departureTime: string;
  arrivalTime: string;
  totalSeats: number;
  availableSeats: number;
  baseFare: number;
  status: FlightStatus;
  terminal?: string;
  gate?: string;
  aircraftId?: number;
}

// ----------------------------------------------------------------------------
// 5. Fleet / Aircraft DTOs
// ----------------------------------------------------------------------------
export type AircraftStatus = 'ACTIVE' | 'MAINTENANCE' | 'GROUNDED';

export interface CreateAircraftRequest {
  aircraftCode: string;
  aircraftName: string;
  manufacturer: string;
  capacity: number;
}

export interface AircraftResponse {
  id: number;
  aircraftCode: string;
  aircraftName: string;
  manufacturer: string;
  capacity: number;
  status: AircraftStatus;
  createdAt?: string;
  updatedAt?: string;
}

// ----------------------------------------------------------------------------
// 6. Fare & Coupon DTOs
// ----------------------------------------------------------------------------
export interface FareRequest {
  flightId: number;
  economyFare: number;
  businessFare: number;
  firstClassFare: number;
  taxPercentage: number;
  discountPercentage?: number;
  effectiveDate?: string; // YYYY-MM-DD
}

export interface FareResponse {
  id: number;
  flightId: number;
  economyFare: number;
  businessFare: number;
  firstClassFare: number;
  taxPercentage: number;
  discountPercentage: number;
  effectiveDate?: string;
  active: boolean;
}

export interface CouponDto {
  id?: number;
  code: string;
  description: string;
  discountPercentage: number;
  maxDiscount: number;
  minFare?: number;
  usageLimit?: number;
  usageCount?: number;
  validFrom?: string;
  validUntil?: string;
  status: 'ACTIVE' | 'EXPIRED' | 'PAUSED';
}

export interface CouponValidateRequest {
  code: string;
  flightId?: number;
  baseFare: number;
  cabinClass?: 'ECONOMY' | 'BUSINESS' | 'FIRST';
}

export interface CouponValidateResponse {
  valid: boolean;
  code: string;
  discountPercentage: number;
  discountAmount: number;
  finalFare: number;
  message?: string;
}

// ----------------------------------------------------------------------------
// 7. Booking DTOs
// ----------------------------------------------------------------------------
export type BookingStatus = 'CONFIRMED' | 'CANCELLED' | 'COMPLETED' | 'PENDING';

export interface PassengerRequest {
  firstName: string;
  lastName: string;
  age: number;
  gender: 'M' | 'F' | 'O';
  seatNumber?: string;
}

export interface PassengerResponse {
  id?: number;
  firstName: string;
  lastName: string;
  age: number;
  gender: string;
  seatNumber?: string;
}

export interface BookingRequest {
  userId: number;
  flightId: number;
  passengers: PassengerRequest[];
  couponCode?: string;
  cabinClass?: string;
}

export interface BookingResponse {
  bookingId: number;
  pnr: string;
  userId: number;
  flightId: number;
  flightNumber: string;
  airlineName: string;
  source: string;
  destination: string;
  departureTime: string;
  arrivalTime: string;
  bookingDate: string;
  totalFare: number;
  status: BookingStatus;
  passengers: PassengerResponse[];
}

// ----------------------------------------------------------------------------
// 8. Seat Map & Hold DTOs
// ----------------------------------------------------------------------------
export interface SeatItem {
  seatNumber: string;
  isBooked: boolean;
  isBusiness: boolean;
  isExitRow?: boolean;
  priceSurcharge?: number;
}

export interface SeatMapResponse {
  flightId: number;
  aircraftCode: string;
  totalRows: number;
  seatsPerRow: number;
  seats: SeatItem[];
}

export interface SeatHoldRequest {
  flightId: number;
  seatNumber: string;
  userId: number;
}

export interface SeatHoldResponse {
  seatNumber: string;
  held: boolean;
  expiresInSeconds: number;
}

// ----------------------------------------------------------------------------
// 9. Check-In & Boarding Pass DTOs
// ----------------------------------------------------------------------------
export type CheckInStatus = 'CHECKED_IN' | 'BOARDED' | 'CANCELLED';

export interface CheckInRequest {
  bookingId: number;
  passengerName: string;
  seatNumber?: string;
}

export interface CheckInResponse {
  id: number;
  bookingId: number;
  passengerName: string;
  seatNumber: string;
  boardingPassNumber: string;
  status: CheckInStatus;
  checkedInAt: string;
}

// ----------------------------------------------------------------------------
// 10. Payment DTOs (Razorpay Integration)
// ----------------------------------------------------------------------------
export type PaymentStatus = 'CREATED' | 'CAPTURED' | 'FAILED' | 'REFUNDED';

export interface PaymentOrderRequest {
  bookingId: number;
  userId?: number;
  amount: number; // in INR decimal
  currency?: string; // 'INR'
  receipt?: string;
  pnr?: string;
  notes?: Record<string, string>;
}

export interface PaymentOrderResponse {
  orderId: string; // Razorpay Order ID (e.g. order_EKwxwAgItmmXdp)
  amount: number;  // in INR decimal
  amountInPaise: number; // in paise for Razorpay Checkout
  currency: string;
  keyId: string;   // Razorpay Public Key ID
  bookingId: number;
  pnr?: string;
}

export interface PaymentVerifyRequest {
  razorpayOrderId: string;
  razorpayPaymentId: string;
  razorpaySignature: string;
  bookingId: number;
}

export interface PaymentVerifyResponse {
  verified: boolean;
  paymentId: string;
  orderId: string;
  bookingId: number;
  status: PaymentStatus;
  message: string;
}

export interface PaymentRefundRequest {
  paymentId: string;
  amount?: number;
  reason?: string;
}

export interface PaymentRefundResponse {
  refundId: string;
  paymentId: string;
  amount: number;
  status: 'PENDING' | 'PROCESSED' | 'FAILED';
}

export interface PaymentRecord {
  id: number;
  bookingId: number;
  orderId: string;
  paymentId: string;
  amount: number;
  currency: string;
  status: PaymentStatus;
  createdAt: string;
  refundId?: string;
}

// ----------------------------------------------------------------------------
// 11. Notification DTOs
// ----------------------------------------------------------------------------
export type NotificationType =
  | 'INFO'
  | 'BOOKING_CONFIRMED'
  | 'CHECKIN_SUCCESS'
  | 'FLIGHT_DELAY'
  | 'GATE_CHANGE'
  | 'BOARDING_CALL'
  | 'BROADCAST';

export type NotificationStatus = 'UNREAD' | 'READ';

export interface NotificationRequest {
  userId?: number;
  recipientEmail?: string;
  title: string;
  message: string;
  type: NotificationType;
  flightNumber?: string;
}

export interface BroadcastRequest {
  title: string;
  message: string;
  type: NotificationType;
  flightNumber?: string;
  targetAudience?: 'ALL' | 'PASSENGERS' | 'STAFF';
}

export interface NotificationResponse {
  id: number;
  userId?: number;
  recipientEmail?: string;
  title: string;
  message: string;
  type: NotificationType;
  status: NotificationStatus;
  flightNumber?: string;
  createdAt: string;
  readAt?: string;
}

// ----------------------------------------------------------------------------
// 12. Incident Reporting DTOs
// ----------------------------------------------------------------------------
export type IncidentSeverity = 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
export type IncidentStatus = 'OPEN' | 'INVESTIGATING' | 'RESOLVED' | 'DISMISSED';

export interface IncidentReportRequest {
  userId: number;
  userName: string;
  userEmail: string;
  reportedBy: string;
  reason: string;
  severity: IncidentSeverity;
  notes?: string;
}

export interface IncidentReportResponse {
  id: number;
  userId: number;
  userName: string;
  userEmail: string;
  reportedBy: string;
  reason: string;
  severity: IncidentSeverity;
  status: IncidentStatus;
  notes?: string;
  createdAt: string;
}

// ----------------------------------------------------------------------------
// 13. Dashboard Summary & System Health DTOs
// ----------------------------------------------------------------------------
export interface ServiceHealthProbe {
  service: string;
  port: number;
  status: 'UP' | 'DOWN' | 'DEGRADED';
  latencyMs?: number;
}

export interface AdminDashboardSummary {
  totalFlights: number;
  operationalAircraft: number;
  maintenanceAircraft: number;
  totalPassengers: number;
  totalStaff: number;
  grossBookingValue: number; // in INR
  openIncidents: number;
  systemHealth: 'HEALTHY' | 'WARNING' | 'CRITICAL';
  services: ServiceHealthProbe[];
  revenueTrend: { date: string; amount: number; bookingsCount: number }[];
}

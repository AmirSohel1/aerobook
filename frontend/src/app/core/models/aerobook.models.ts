// Aerobook TypeScript Data Models & DTOs

export type RoleType = 'ROLE_USER' | 'ROLE_STAFF' | 'ROLE_ADMIN';

export interface User {
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber?: string;
  dateOfBirth?: string;
  nationality?: string;
  role: RoleType;
  createdAt?: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  role: RoleType;
  userId: number;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber?: string;
  dateOfBirth?: string;
  nationality?: string;
  password: string;
}

export interface Flight {
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
  status: 'SCHEDULED' | 'BOARDING' | 'DELAYED' | 'DEPARTED' | 'COMPLETED' | 'CANCELLED';
  aircraftId?: number;
}

export interface Aircraft {
  id: number;
  model: string;
  aircraftNumber: string;
  capacity: number;
  status: 'ACTIVE' | 'MAINTENANCE' | 'GROUNDED';
}

export interface Fare {
  id: number;
  flightId: number;
  economyFare: number;
  businessFare: number;
  firstClassFare: number;
  taxRate: number;
  discountPercentage: number;
}

export interface Passenger {
  passengerId?: number;
  firstName: string;
  lastName: string;
  age: number;
  gender: string;
  seatNumber?: string;
}

export interface BookingRequest {
  userId: number;
  flightId: number;
  passengers: Passenger[];
}

export interface Booking {
  bookingId: number;
  pnr: string;
  userId: number;
  flightId: number;
  bookingDate: string;
  totalFare: number;
  status: 'CONFIRMED' | 'CANCELLED' | 'COMPLETED';
  passengers: Passenger[];
  flightNumber?: string;
  airlineName?: string;
  source?: string;
  destination?: string;
  departureTime?: string;
  arrivalTime?: string;
}

export interface CheckIn {
  id: number;
  bookingId: number;
  passengerName: string;
  seatNumber: string;
  boardingPassNumber: string;
  status: 'CHECKED_IN' | 'BOARDED' | 'CANCELLED';
  checkedInAt: string;
}

export interface Notification {
  id: number;
  userId?: number;
  recipientEmail?: string;
  title: string;
  message: string;
  type: 'INFO' | 'BOOKING_CONFIRMED' | 'CHECKIN_SUCCESS' | 'FLIGHT_DELAY' | 'GATE_CHANGE' | 'BOARDING_CALL' | 'BROADCAST';
  status: 'UNREAD' | 'READ';
  flightNumber?: string;
  createdAt: string;
  readAt?: string;
}

export interface BroadcastRequest {
  title: string;
  message: string;
  type: string;
  flightNumber?: string;
}


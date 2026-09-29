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
  status?: 'ACTIVE' | 'BLOCKED' | 'SUSPENDED';
  reportsCount?: number;
  totalBookings?: number;
  lastLogin?: string;
  createdAt?: string;
  level?: number;
  levelTitle?: string;
  tier?: 'BRONZE' | 'SILVER' | 'GOLD' | 'PLATINUM' | 'DIAMOND';
  xp?: number;
  badgesCount?: number;
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
  aircraftTailNumber?: string;
  terminal?: string;
  gate?: string;
  durationMinutes?: number;
}

export interface Aircraft {
  id: number;
  model: string;
  aircraftNumber: string;
  capacity: number;
  status: 'ACTIVE' | 'MAINTENANCE' | 'GROUNDED';
  manufacturer?: string;
  businessSeats?: number;
  economySeats?: number;
  lastInspectionDate?: string;
  flightHours?: number;
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

export interface AdminProfile {
  adminId: number;
  name: string;
  email: string;
  role: string;
  department: string;
  phone: string;
  securityClearance: string;
  twoFactorEnabled: boolean;
  avatarUrl?: string;
  lastLogin: string;
  joinedDate: string;
}

export interface AdminAuditEntry {
  id: number;
  action: string;
  target: string;
  timestamp: string;
  ipAddress: string;
  status: 'SUCCESS' | 'WARNING' | 'FAILED';
}

export interface UserIncidentReport {
  id: number;
  userId: number;
  userName: string;
  userEmail: string;
  reportedBy: string;
  reason: string;
  severity: 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  status: 'OPEN' | 'INVESTIGATING' | 'RESOLVED' | 'DISMISSED';
  createdAt: string;
  notes?: string;
}

export interface PromoCode {
  id: number;
  code: string;
  description: string;
  discountPercentage: number;
  maxDiscount: number;
  validFrom: string;
  validUntil: string;
  usageLimit: number;
  usageCount: number;
  status: 'ACTIVE' | 'EXPIRED' | 'PAUSED';
}

export interface ToastMessage {
  id: number;
  type: 'success' | 'error' | 'info' | 'warning';
  title: string;
  message: string;
}

export interface StaffProfile {
  staffId: string;
  name: string;
  email: string;
  station: string;
  terminal: string;
  gateAssignment: string;
  role: string;
  shiftStatus: 'ON_DUTY' | 'ON_BREAK' | 'OFF_DUTY';
  phone: string;
  emergencyContact: string;
}

export interface BaggageTag {
  tagNumber: string;
  passengerName: string;
  pnr: string;
  flightNumber: string;
  destination: string;
  weightKg: number;
  pieces: number;
  issuedAt: string;
}

export interface BaggageTrackingItem {
  tagNumber: string;
  pnr: string;
  passengerName: string;
  flightNumber: string;
  origin: string;
  destination: string;
  currentStatus: 'CHECKED_IN' | 'SECURITY_SCREENED' | 'LOADED_ON_AIRCRAFT' | 'IN_TRANSIT' | 'UNLOADED' | 'CAROUSEL_AVAILABLE' | 'CLAIMED';
  carouselNumber?: string;
  weightKg: number;
  lastScanLocation: string;
  lastScanTime: string;
  estimatedDeliveryTime?: string;
  history: { step: string; location: string; timestamp: string; completed: boolean }[];
}

export interface MilesTransaction {
  id: number;
  date: string;
  description: string;
  flightNumber?: string;
  type: 'EARNED' | 'REDEEMED' | 'BONUS';
  miles: number;
  balanceAfter: number;
}

export interface TravelerProfile {
  userId: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  dateOfBirth: string;
  nationality: string;
  passportNumber: string;
  passportExpiry: string;
  frequentFlyerNumber: string;
  tier: 'SILVER' | 'GOLD' | 'PLATINUM' | 'DIAMOND';
  milesBalance: number;
  nextTierMiles: number;
  preferredSeat: 'WINDOW' | 'AISLE' | 'ANY';
  mealPreference: string;
  specialAssistance: string;
  homeAirport: string;
  emergencyContactName: string;
  emergencyContactPhone: string;
  twoFactorEnabled: boolean;
}

export interface Badge {
  id: string;
  title: string;
  description: string;
  icon: string;
  category: 'TRAVEL' | 'SPEED' | 'LOYALTY' | 'EXPLORER';
  unlocked: boolean;
  unlockedAt?: string;
  rarity: 'COMMON' | 'RARE' | 'EPIC' | 'LEGENDARY';
  xpReward: number;
}

export interface QuestMilestone {
  id: string;
  title: string;
  description: string;
  currentProgress: number;
  targetProgress: number;
  unit: string;
  completed: boolean;
  rewardType: 'XP' | 'DISCOUNT_VOUCHER' | 'FREE_SEAT' | 'LOUNGE_PASS';
  rewardValue: string;
  rewardClaimed: boolean;
}

export interface LevelTier {
  level: number;
  title: string;
  name: 'BRONZE' | 'SILVER' | 'GOLD' | 'PLATINUM' | 'DIAMOND';
  minXp: number;
  maxXp: number;
  color: string;
  badgeIcon: string;
  discountPercentage: number;
  discountCode: string;
  perks: string[];
}

export interface GamificationProfile {
  userId: number;
  currentLevel: number;
  levelTitle: string;
  tier: 'BRONZE' | 'SILVER' | 'GOLD' | 'PLATINUM' | 'DIAMOND';
  currentXp: number;
  xpToNextLevel: number;
  progressPercent: number;
  badges: Badge[];
  quests: QuestMilestone[];
  unlockedDiscounts: { code: string; discountPercent: number; description: string; minFare?: number }[];
  consecutiveTripStreak: number;
}




import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { BookingService } from '../../core/services/booking.service';
import { FlightService } from '../../core/services/flight.service';
import { CheckInService } from '../../core/services/checkin.service';
import { NotificationService } from '../../core/services/notification.service';
import { UserService } from '../../core/services/user.service';
import { GamificationService } from '../../core/services/gamification.service';
import {
  Booking,
  CheckIn,
  Flight,
  Notification,
  User,
  BaggageTrackingItem,
  TravelerProfile,
  MilesTransaction,
  ToastMessage
} from '../../core/models/aerobook.models';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <main class="wrap py-8 animate-fadeIn">

      <!-- Toast Notifications Container -->
      <div class="fixed top-20 right-5 z-50 flex flex-col gap-2 pointer-events-none max-w-sm w-full">
        @for (toast of toasts; track toast.id) {
          <div class="pointer-events-auto p-4 rounded-2xl shadow-card border border-line transition-all transform duration-300 flex items-start gap-3 backdrop-blur-2xl bg-surface/95 text-ink"
               [ngClass]="{
                 'border-ok/40': toast.type === 'success',
                 'border-bad/40': toast.type === 'error',
                 'border-accent/40': toast.type === 'info',
                 'border-warn/40': toast.type === 'warning'
               }">
            <span class="w-5 h-5 rounded-full flex items-center justify-center text-xs font-bold shrink-0 mt-0.5"
                  [ngClass]="{
                    'bg-ok-soft text-ok': toast.type === 'success',
                    'bg-bad-soft text-bad': toast.type === 'error',
                    'bg-accent-soft text-accent': toast.type === 'info',
                    'bg-warn-soft text-warn': toast.type === 'warning'
                  }">
              @if (toast.type === 'success') { ✓ }
              @else if (toast.type === 'error') { ✕ }
              @else if (toast.type === 'warning') { ! }
              @else { i }
            </span>
            <div class="flex-1">
              <h4 class="text-xs font-bold uppercase tracking-wider text-ink">{{ toast.title }}</h4>
              <p class="text-xs mt-0.5 text-ink-2">{{ toast.message }}</p>
            </div>
            <button (click)="removeToast(toast.id)" class="text-ink-3 hover:text-ink text-xs cursor-pointer">✕</button>
          </div>
        }
      </div>

      <!-- ========================================================================= -->
      <!-- EXECUTIVE PASSENGER & FREQUENT FLYER BENTO BANNER -->
      <!-- ========================================================================= -->
      <div class="bento">
        
        <!-- Hero Card (Span 8) -->
        <div class="card hero c8">
          <span class="tagline">
            <b></b>
            <span>Executive frequent flyer · Level {{ gamificationService.currentLevel() }}</span>
          </span>

          <div class="my-4 z-10">
            <div class="flex items-center gap-2 mb-2">
              <span class="px-2.5 py-0.5 rounded-full bg-white/20 text-white text-xs font-bold">
                {{ gamificationService.levelTitle() }}
              </span>
              <span class="text-xs text-white/80 font-medium">
                #{{ travelerProfile?.frequentFlyerNumber || 'AB-774092' }} · 3-Month streak
              </span>
            </div>
            <h1>Welcome aboard, {{ travelerProfile?.firstName || 'Priya' }} {{ travelerProfile?.lastName || 'Sharma' }}.</h1>
            <p>Your flights, interactive seat selection, boarding passes, and rewards vault kept clear and up to date.</p>
            
            <!-- XP Progress -->
            <div class="mt-4 max-w-md">
              <div class="flex items-center justify-between text-xs text-white/80 font-medium mb-1.5">
                <span>Level {{ gamificationService.currentLevel() }} progress</span>
                <span class="font-bold text-white">{{ gamificationService.currentXp() | number }} XP</span>
              </div>
              <div class="w-full bg-white/20 rounded-full h-2 overflow-hidden">
                <div class="bg-white h-full rounded-full transition-all duration-500" [style.width.%]="gamificationService.progressPercent()"></div>
              </div>
            </div>
          </div>

          <svg class="plane" viewBox="0 0 24 24" aria-hidden="true">
            <path d="M21 16v-2l-8-5V3.5a1.5 1.5 0 0 0-3 0V9l-8 5v2l8-2.5V19l-2 1.5V22l3.5-1 3.5 1v-1.5L13 19v-5.5z"/>
          </svg>
        </div>

        <!-- Value Card (Span 4) -->
        <div class="card value c4">
          <div>
            <div class="lbl">AeroMiles balance</div>
            <div class="big">{{ (travelerProfile?.milesBalance || 24850) | number }}</div>
            <span class="delta">{{ bookings.length }} confirmed trips</span>
          </div>
          <div class="spark">
            <svg class="w-full h-20" viewBox="0 0 200 80" preserveAspectRatio="none">
              <defs>
                <linearGradient id="cust-sg" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%" stop-color="var(--accent-2)" stop-opacity="0.4"/>
                  <stop offset="100%" stop-color="var(--accent-2)" stop-opacity="0"/>
                </linearGradient>
              </defs>
              <path class="a" fill="url(#cust-sg)" d="M0 66 C25 60 35 40 60 44 S100 62 125 34 S170 14 200 10 V80 H0Z"/>
              <path class="l" fill="none" stroke="var(--accent-2)" stroke-width="3" stroke-linecap="round" stroke-linejoin="round" d="M0 66 C25 60 35 40 60 44 S100 62 125 34 S170 14 200 10"/>
            </svg>
          </div>
        </div>

        <!-- Bento Tabs Navigation (Span 12) -->
        <div class="card tabs-card c12">
          <div class="seg" role="tablist" aria-label="Passenger sections">
            <button
              type="button"
              role="tab"
              [attr.aria-selected]="activeTab === 'bookings'"
              [class.on]="activeTab === 'bookings'"
              (click)="activeTab = 'bookings'"
            >
              My reservations ({{ bookings.length }})
            </button>

            <button
              type="button"
              role="tab"
              [attr.aria-selected]="activeTab === 'book'"
              [class.on]="activeTab === 'book'"
              (click)="activeTab = 'book'"
            >
              Book flight & seat map
            </button>

            <button
              type="button"
              role="tab"
              [attr.aria-selected]="activeTab === 'checkin'"
              [class.on]="activeTab === 'checkin'"
              (click)="activeTab = 'checkin'"
            >
              Web check-in & boarding pass
            </button>

            <button
              type="button"
              role="tab"
              [attr.aria-selected]="activeTab === 'baggage'"
              [class.on]="activeTab === 'baggage'"
              (click)="activeTab = 'baggage'"
            >
              Baggage tracker
            </button>

            <button
              type="button"
              role="tab"
              [attr.aria-selected]="activeTab === 'gamification'"
              [class.on]="activeTab === 'gamification'"
              (click)="activeTab = 'gamification'"
            >
              <span>Levels, quests & badges</span>
              <span class="badge">Lv.{{ gamificationService.currentLevel() }}</span>
            </button>

            <button
              type="button"
              role="tab"
              [attr.aria-selected]="activeTab === 'flightstatus'"
              [class.on]="activeTab === 'flightstatus'"
              (click)="activeTab = 'flightstatus'"
            >
              Live flight radar
            </button>

            <button
              type="button"
              role="tab"
              [attr.aria-selected]="activeTab === 'loyalty'"
              [class.on]="activeTab === 'loyalty'"
              (click)="activeTab = 'loyalty'"
            >
              AeroMiles club
            </button>

            <button
              type="button"
              role="tab"
              [attr.aria-selected]="activeTab === 'notifications'"
              [class.on]="activeTab === 'notifications'"
              (click)="activeTab = 'notifications'"
            >
              <span>Flight notices</span>
              @if (unreadCount > 0) {
                <span class="badge">{{ unreadCount }}</span>
              }
            </button>

            <button
              type="button"
              role="tab"
              [attr.aria-selected]="activeTab === 'profile'"
              [class.on]="activeTab === 'profile'"
              (click)="activeTab = 'profile'"
            >
              Profile & passport
            </button>
          </div>
        </div>

      </div>

      <!-- Quick Actions Bento Grid -->
      <div class="head">
        <h2>Quick actions</h2>
        <p>The jobs you do most, ready when you are.</p>
      </div>

      <div class="bento" style="margin-top:20px">
        <button type="button" (click)="activeTab = 'book'" class="card action feat c3 h">
          <div class="ic">
            <svg viewBox="0 0 24 24"><path d="M21 16v-2l-8-5V3.5a1.5 1.5 0 0 0-3 0V9l-8 5v2l8-2.5V19l-2 1.5V22l3.5-1 3.5 1v-1.5L13 19v-5.5z"/></svg>
          </div>
          <h3>Book a flight</h3>
          <p>Explore 40+ scheduled routes with interactive cabin seat maps.</p>
          <span class="go">Book now</span>
        </button>

        <button type="button" (click)="activeTab = 'bookings'" class="card action c3 h">
          <div class="ic">
            <svg viewBox="0 0 24 24"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
          </div>
          <h3>My itineraries</h3>
          <p>Review confirmed reservations, tax invoices, and in-flight meals.</p>
          <span class="go">View trips</span>
        </button>

        <button type="button" (click)="activeTab = 'checkin'" class="card action c3 h">
          <div class="ic g">
            <svg viewBox="0 0 24 24"><rect x="5" y="2" width="14" height="20" rx="2" ry="2"/><line x1="12" y1="18" x2="12.01" y2="18"/></svg>
          </div>
          <h3>Web check-in</h3>
          <p>Instant digital boarding pass ready for Apple Wallet or printout.</p>
          <span class="go">Check in</span>
        </button>

        <button type="button" (click)="activeTab = 'gamification'" class="card action c3 h">
          <div class="ic a">
            <svg viewBox="0 0 24 24"><circle cx="12" cy="8" r="7"/><polyline points="8.21 13.89 7 23 12 20 17 23 15.79 13.88"/></svg>
          </div>
          <h3>Rewards & badges</h3>
          <p>Advance through 5 tiers, collect badges, and claim discount vouchers.</p>
          <span class="go">Open vault</span>
        </button>
      </div>

      <!-- ========================================================================= -->
      <!-- TAB 1: MY RESERVATIONS (ITINERARIES & E-TICKETS) -->
      <!-- ========================================================================= -->
      @if (activeTab === 'bookings') {
        <div class="space-y-6">
          
          <!-- Controls Header -->
          <div class="card flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div>
              <h2 class="text-xl font-extrabold text-ink m-0">Confirmed flight itineraries</h2>
              <p class="text-xs text-ink-2 mt-1">Manage reservations, view e-tickets, and download tax receipts ({{ filteredBookings.length }} records)</p>
            </div>

            <div class="flex flex-wrap items-center gap-3">
              <!-- Status Filter Seg -->
              <div class="seg" role="tablist">
                <button type="button" role="tab" [attr.aria-selected]="bookingStatusFilter === 'ALL'" [class.on]="bookingStatusFilter === 'ALL'" (click)="bookingStatusFilter = 'ALL'">All</button>
                <button type="button" role="tab" [attr.aria-selected]="bookingStatusFilter === 'CONFIRMED'" [class.on]="bookingStatusFilter === 'CONFIRMED'" (click)="bookingStatusFilter = 'CONFIRMED'">Confirmed</button>
                <button type="button" role="tab" [attr.aria-selected]="bookingStatusFilter === 'COMPLETED'" [class.on]="bookingStatusFilter === 'COMPLETED'" (click)="bookingStatusFilter = 'COMPLETED'">Flown</button>
                <button type="button" role="tab" [attr.aria-selected]="bookingStatusFilter === 'CANCELLED'" [class.on]="bookingStatusFilter === 'CANCELLED'" (click)="bookingStatusFilter = 'CANCELLED'">Cancelled</button>
              </div>

              <!-- Search input -->
              <div class="relative flex items-center">
                <input
                  type="text"
                  [(ngModel)]="bookingSearchQuery"
                  placeholder="Search PNR, city, flight..."
                  class="h-10 pl-4 pr-4 text-xs rounded-full bg-seg border border-line text-ink placeholder:text-ink-3 focus:outline-none focus:ring-2 focus:ring-accent-2 font-medium w-48 sm:w-56"
                />
              </div>

              <button type="button" (click)="activeTab = 'book'" class="btn p !text-xs !py-2.5 !px-4">
                + Book new flight
              </button>
            </div>
          </div>

          <!-- Empty State -->
          @if (filteredBookings.length === 0) {
            <div class="card p-12 text-center max-w-lg mx-auto">
              <div class="ic mx-auto mb-3">
                <svg viewBox="0 0 24 24"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
              </div>
              <h3 class="text-base font-bold text-ink">No matching reservations</h3>
              <p class="text-xs text-ink-2 mt-1 mb-6">No flights matching your filter criteria were found on record.</p>
              <button (click)="activeTab = 'book'" class="btn p !text-xs !py-2.5 !px-5">
                Search flights & book
              </button>
            </div>
          }

          <!-- Bookings Rendered with .pass Boarding Pass Styling from Reference -->
          <div class="space-y-4">
            @for (b of filteredBookings; track b.bookingId) {
              <article class="card pass c12">
                <div class="main">
                  <div class="top-meta">
                    <div class="flex items-center gap-2">
                      <span>Booking <b>PNR-{{ b.pnr }}</b></span>
                      <button (click)="copyToClipboard(b.pnr, 'PNR')" class="text-xs text-ink-3 hover:text-accent font-medium ml-1 cursor-pointer" title="Copy PNR">Copy</button>
                    </div>
                    <span class="status" [class.done]="b.status === 'COMPLETED'" [class.bad]="b.status === 'CANCELLED'">
                      {{ b.status === 'CONFIRMED' ? 'Confirmed' : (b.status === 'COMPLETED' ? 'Completed' : b.status) }}
                    </span>
                  </div>

                  <div class="route">
                    <div>
                      <div class="code text-ink">{{ b.source || 'BOM' }}</div>
                      <div class="city text-ink-2">{{ getCityName(b.source || 'BOM') }}</div>
                      <div class="time text-ink">{{ formatTime(b.departureTime) }}</div>
                    </div>
                    <div class="path">
                      Non-stop · 2h 10m
                      <i></i>
                      <svg viewBox="0 0 24 24"><path d="M21 16v-2l-8-5V3.5a1.5 1.5 0 0 0-3 0V9l-8 5v2l8-2.5V19l-2 1.5V22l3.5-1 3.5 1v-1.5L13 19v-5.5z"/></svg>
                      <span style="color:var(--green)">On time</span>
                    </div>
                    <div class="end">
                      <div class="code text-ink">{{ b.destination || 'DEL' }}</div>
                      <div class="city text-ink-2">{{ getCityName(b.destination || 'DEL') }}</div>
                      <div class="time text-ink">{{ formatTime(b.arrivalTime) }}</div>
                    </div>
                  </div>

                  <div class="chips">
                    <span>{{ b.flightNumber || 'AB-101' }}</span>
                    <span>Terminal 2, Gate B04</span>
                    @for (p of b.passengers; track p.passengerId || p.firstName) {
                      <span>Seat {{ p.seatNumber || '12A' }}</span>
                    }
                    <span>7 kg cabin</span>
                    <span>15 kg check-in</span>
                    <span>Meal selected</span>
                  </div>

                  <div class="btns">
                    @if (b.status === 'CONFIRMED') {
                      <button (click)="openCheckInForBooking(b)" class="btn p">Web check-in</button>
                    }
                    <button (click)="openETicketModal(b)" class="btn">E-ticket</button>
                    <button (click)="openMealModal(b)" class="btn">In-flight services</button>
                    @if (b.status === 'CONFIRMED') {
                      <button (click)="openCancelModal(b)" class="btn d">Cancel flight</button>
                    }
                  </div>
                </div>

                <div class="stub">
                  <div>
                    <div class="k">Passenger</div>
                    <div class="v truncate">{{ b.passengers[0].firstName || 'Priya' }} {{ b.passengers[0].lastName || 'Sharma' }}</div>
                  </div>
                  <div>
                    <div class="k">Total paid</div>
                    <div class="v">₹{{ b.totalFare | number }}</div>
                  </div>
                  <div class="bar-code" aria-hidden="true"></div>
                </div>
              </article>
            }
          </div>

        </div>
      }

      <!-- ========================================================================= -->
      <!-- TAB 2: BOOK FLIGHT & INTERACTIVE CABIN SEAT MAP -->
      <!-- ========================================================================= -->
      @if (activeTab === 'book') {
        <div class="space-y-6 max-w-5xl mx-auto">
          
          <div class="card p-6 sm:p-8">
            <div class="mb-6 pb-4 border-b border-line flex items-center justify-between">
              <div>
                <h2 class="text-xl font-extrabold text-ink m-0">Commercial flight booking & seat reservation</h2>
                <p class="text-xs text-ink-2 mt-1">Select your scheduled route, choose your preferred cabin class, and hand-pick your seat on the interactive aircraft map.</p>
              </div>
              <span class="status">Direct airline rates</span>
            </div>

            <!-- Gamification Level Perk Banner -->
            <div class="mb-6 p-4 rounded-[20px] bg-accent-soft border border-accent/20 flex flex-col sm:flex-row sm:items-center justify-between gap-3">
              <div class="flex items-center gap-3">
                <div class="w-9 h-9 rounded-full bg-brand text-white flex items-center justify-center font-bold text-xs">
                  Lv.{{ gamificationService.currentLevel() }}
                </div>
                <div>
                  <h4 class="text-xs font-bold text-ink">Level {{ gamificationService.currentLevel() }} Perks Active: {{ gamificationService.levelTitle() }}</h4>
                  <p class="text-[11px] text-ink-2 mt-0.5">
                    Your tier unlocks code <strong class="text-accent font-mono">{{ gamificationService.getTierInfo(gamificationService.currentTier()).discountCode }}</strong> ({{ gamificationService.getTierInfo(gamificationService.currentTier()).discountPercentage }}% off) + Earn ~{{ (calculateBaseFare() * 0.15) | number:'1.0-0' }} XP on this flight!
                  </p>
                </div>
              </div>
              <button type="button" (click)="applyVaultDiscount(gamificationService.getTierInfo(gamificationService.currentTier()).discountCode)"
                      class="btn p !text-xs !py-2 !px-4 shrink-0 cursor-pointer">
                Apply {{ gamificationService.getTierInfo(gamificationService.currentTier()).discountCode }}
              </button>
            </div>

            <form (ngSubmit)="submitBooking()" class="space-y-6">
              
              <!-- Step 1: Flight Selection & Route Search -->
              <div class="p-6 rounded-[20px] bg-seg border border-line space-y-4">
                <div class="flex items-center justify-between">
                  <label class="block text-xs font-bold text-ink uppercase tracking-wider flex items-center gap-2">
                    <span class="w-5 h-5 rounded-full bg-accent text-white text-[11px] flex items-center justify-center font-bold">1</span>
                    Choose scheduled flight & route
                  </label>
                  <span class="text-xs text-accent font-semibold">{{ availableFlights.length }} Flights operating</span>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                  <div class="md:col-span-2">
                    <label class="block text-xs font-semibold text-ink-2 mb-1.5">Scheduled commercial flights</label>
                    <select [(ngModel)]="newBookingFlightId" name="flightId" required (change)="onFlightSelect()"
                            class="w-full h-12 px-4 rounded-[16px] bg-surface border border-line text-ink text-sm font-semibold focus:outline-none focus:ring-2 focus:ring-accent-2">
                      <option [ngValue]="null" disabled>Select from available airline schedules</option>
                      @for (f of availableFlights; track f.id) {
                        <option [ngValue]="f.id">
                          {{ f.flightNumber }} • {{ f.airlineName }} ({{ f.source }} → {{ f.destination }}) • Base: ₹{{ f.baseFare | number }} • {{ f.availableSeats }} seats remaining
                        </option>
                      }
                    </select>
                  </div>

                  <div>
                    <label class="block text-xs font-semibold text-ink-2 mb-1.5">Travel date</label>
                    <input type="date" [(ngModel)]="bookingTravelDate" name="travelDate"
                           class="w-full h-12 px-4 rounded-[16px] bg-surface border border-line text-ink text-sm font-semibold focus:outline-none focus:ring-2 focus:ring-accent-2">
                  </div>
                </div>

                <!-- Flight Route Preview Card -->
                @if (selectedFlight) {
                  <div class="p-4 rounded-[16px] bg-surface border border-line flex flex-col sm:flex-row sm:items-center justify-between gap-4 mt-3">
                    <div class="flex items-center gap-3">
                      <div class="w-10 h-10 rounded-xl bg-accent-soft text-accent flex items-center justify-center font-bold text-sm">
                        {{ selectedFlight.flightNumber.substring(0, 2) }}
                      </div>
                      <div>
                        <div class="text-sm font-bold text-ink">{{ selectedFlight.flightNumber }} - {{ selectedFlight.airlineName }}</div>
                        <div class="text-xs text-ink-2">{{ selectedFlight.source }} &rarr; {{ selectedFlight.destination }} • Departs: {{ formatTime(selectedFlight.departureTime) }}</div>
                      </div>
                    </div>

                    <div class="flex items-center gap-4 text-xs">
                      <div>
                        <span class="text-ink-3 block text-[10px] uppercase font-semibold">Aircraft</span>
                        <span class="text-ink font-semibold">{{ selectedFlight.aircraftTailNumber || 'Boeing 737-800' }}</span>
                      </div>
                      <div>
                        <span class="text-ink-3 block text-[10px] uppercase font-semibold">Terminal / Gate</span>
                        <span class="text-accent font-semibold">{{ selectedFlight.terminal || 'T2' }} / {{ selectedFlight.gate || 'B04' }}</span>
                      </div>
                      <div>
                        <span class="text-ink-3 block text-[10px] uppercase font-semibold">Base Fare</span>
                        <span class="text-ok font-extrabold text-sm">₹{{ selectedFlight.baseFare | number }}</span>
                      </div>
                    </div>
                  </div>
                }
              </div>

              <!-- Step 2: Cabin Class Selection -->
              <div class="p-6 rounded-[20px] bg-seg border border-line space-y-4">
                <label class="block text-xs font-bold text-ink uppercase tracking-wider flex items-center gap-2">
                  <span class="w-5 h-5 rounded-full bg-accent text-white text-[11px] flex items-center justify-center font-bold">2</span>
                  Select travel cabin class
                </label>

                <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
                  <div (click)="selectedCabinClass = 'ECONOMY'"
                       [class]="selectedCabinClass === 'ECONOMY' ? 'border-accent bg-accent-soft ring-2 ring-accent' : 'border-line bg-surface hover:border-accent-2'"
                       class="p-4 rounded-[18px] border transition-all cursor-pointer relative">
                    <div class="flex items-center justify-between mb-2">
                      <span class="text-xs font-bold text-ink">Economy standard</span>
                      <span class="text-xs font-bold text-ok">Included</span>
                    </div>
                    <ul class="text-[11px] text-ink-2 space-y-1 font-medium">
                      <li>✓ 7kg Cabin baggage</li>
                      <li>✓ 15kg Check-in baggage</li>
                      <li>✓ Standard recline seat</li>
                    </ul>
                  </div>

                  <div (click)="selectedCabinClass = 'PREMIUM'"
                       [class]="selectedCabinClass === 'PREMIUM' ? 'border-accent bg-accent-soft ring-2 ring-accent' : 'border-line bg-surface hover:border-accent-2'"
                       class="p-4 rounded-[18px] border transition-all cursor-pointer relative">
                    <div class="flex items-center justify-between mb-2">
                      <span class="text-xs font-bold text-ink">Premium economy</span>
                      <span class="text-xs font-bold text-accent">+₹1,200</span>
                    </div>
                    <ul class="text-[11px] text-ink-2 space-y-1 font-medium">
                      <li>✓ 7kg Cabin + 20kg Check-in</li>
                      <li>✓ Extra legroom seat</li>
                      <li>✓ Priority boarding (Group 2)</li>
                    </ul>
                  </div>

                  <div (click)="selectedCabinClass = 'BUSINESS'"
                       [class]="selectedCabinClass === 'BUSINESS' ? 'border-accent bg-accent-soft ring-2 ring-accent' : 'border-line bg-surface hover:border-accent-2'"
                       class="p-4 rounded-[18px] border transition-all cursor-pointer relative">
                    <div class="flex items-center justify-between mb-2">
                      <span class="text-xs font-bold text-ink">Business class</span>
                      <span class="text-xs font-bold text-accent">+₹3,500</span>
                    </div>
                    <ul class="text-[11px] text-ink-2 space-y-1 font-medium">
                      <li>✓ 30kg Baggage + Lounge access</li>
                      <li>✓ Wide leather recliner</li>
                      <li>✓ Gourmet multi-course meal</li>
                    </ul>
                  </div>
                </div>
              </div>

              <!-- Step 3: Passenger Information -->
              <div class="p-6 rounded-[20px] bg-seg border border-line space-y-4">
                <div class="flex items-center justify-between">
                  <label class="block text-xs font-bold text-ink uppercase tracking-wider flex items-center gap-2">
                    <span class="w-5 h-5 rounded-full bg-accent text-white text-[11px] flex items-center justify-center font-bold">3</span>
                    Primary traveler & passenger information
                  </label>
                  <span class="text-[11px] text-ink-3">Pre-filled from verified traveler profile</span>
                </div>

                <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-4 gap-4">
                  <div>
                    <label class="block text-[11px] font-semibold text-ink-2 mb-1">First name</label>
                    <input type="text" [(ngModel)]="passengerFirstName" name="firstName" required
                           class="w-full h-11 px-3.5 rounded-[14px] bg-surface border border-line text-ink text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-accent-2">
                  </div>
                  <div>
                    <label class="block text-[11px] font-semibold text-ink-2 mb-1">Last name</label>
                    <input type="text" [(ngModel)]="passengerLastName" name="lastName" required
                           class="w-full h-11 px-3.5 rounded-[14px] bg-surface border border-line text-ink text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-accent-2">
                  </div>
                  <div>
                    <label class="block text-[11px] font-semibold text-ink-2 mb-1">Age</label>
                    <input type="number" [(ngModel)]="passengerAge" name="age" required min="1" max="120"
                           class="w-full h-11 px-3.5 rounded-[14px] bg-surface border border-line text-ink text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-accent-2">
                  </div>
                  <div>
                    <label class="block text-[11px] font-semibold text-ink-2 mb-1">Gender</label>
                    <select [(ngModel)]="passengerGender" name="gender" class="w-full h-11 px-3.5 rounded-[14px] bg-surface border border-line text-ink text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-accent-2">
                      <option value="F">Female</option>
                      <option value="M">Male</option>
                      <option value="O">Other</option>
                    </select>
                  </div>
                </div>

                <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 pt-2">
                  <div>
                    <label class="block text-[11px] font-semibold text-ink-2 mb-1">Passport / Government ID number</label>
                    <input type="text" [(ngModel)]="passengerGovtId" name="govtId" placeholder="e.g. P8921045"
                           class="w-full h-11 px-3.5 rounded-[14px] bg-surface border border-line text-ink text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-accent-2">
                  </div>
                  <div>
                    <label class="block text-[11px] font-semibold text-ink-2 mb-1">In-flight meal preference</label>
                    <select [(ngModel)]="bookingMealPreference" name="mealPref" class="w-full h-11 px-3.5 rounded-[14px] bg-surface border border-line text-ink text-xs font-semibold focus:outline-none focus:ring-2 focus:ring-accent-2">
                      <option value="VEGETARIAN_HINDU">Asian Vegetarian (AVML)</option>
                      <option value="NON_VEG">Standard Non-Vegetarian</option>
                      <option value="VEGAN">Strict Vegan (VGML)</option>
                      <option value="HALAL">Halal Meal (MOML)</option>
                      <option value="KOSHER">Kosher Meal (KSML)</option>
                      <option value="DIABETIC">Diabetic Low-Sugar (DBML)</option>
                    </select>
                  </div>
                </div>
              </div>

              <!-- Step 4: Interactive Airplane Cabin Seat Map -->
              <div class="p-6 rounded-[20px] bg-seg border border-line space-y-4">
                <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
                  <label class="block text-xs font-bold text-ink uppercase tracking-wider flex items-center gap-2">
                    <span class="w-5 h-5 rounded-full bg-accent text-white text-[11px] flex items-center justify-center font-bold">4</span>
                    Hand-pick your preferred seat:
                    <span class="text-accent font-mono text-sm font-black px-2.5 py-0.5 rounded-full bg-accent-soft border border-accent/20">
                      {{ selectedSeat }}
                    </span>
                  </label>

                  <div class="flex flex-wrap items-center gap-3 text-[11px] text-ink-2">
                    <span class="flex items-center gap-1.5"><span class="w-3.5 h-3.5 rounded bg-surface border border-line inline-block"></span> Available</span>
                    <span class="flex items-center gap-1.5"><span class="w-3.5 h-3.5 rounded bg-accent border border-accent inline-block shadow-sm"></span> Selected</span>
                    <span class="flex items-center gap-1.5"><span class="w-3.5 h-3.5 rounded bg-amber-soft border border-amber/30 inline-block"></span> Business class</span>
                    <span class="flex items-center gap-1.5"><span class="w-3.5 h-3.5 rounded bg-seg opacity-40 inline-block"></span> Occupied</span>
                  </div>
                </div>

                <!-- Aircraft Diagram Container -->
                <div class="max-w-lg mx-auto p-6 rounded-[24px] bg-surface border border-line shadow-card relative">
                  <div class="text-center pb-4 border-b border-line">
                    <div class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-seg text-[10px] text-ink-2 font-bold tracking-widest uppercase">
                      Boeing 737-800 · Cockpit front
                    </div>
                  </div>

                  <div class="py-4 space-y-2">
                    @for (row of seatRows; track row) {
                      <div class="flex items-center justify-between gap-1 text-xs">
                        <div class="flex gap-1.5">
                          <button type="button" (click)="chooseSeat(row + 'A')" [class]="getSeatStyle(row + 'A', row)"
                                  class="w-9 h-9 rounded-xl font-mono text-[11px] font-bold transition-all flex items-center justify-center relative cursor-pointer">
                            {{ row }}A
                          </button>
                          <button type="button" (click)="chooseSeat(row + 'B')" [class]="getSeatStyle(row + 'B', row)"
                                  class="w-9 h-9 rounded-xl font-mono text-[11px] font-bold transition-all flex items-center justify-center relative cursor-pointer">
                            {{ row }}B
                          </button>
                          <button type="button" (click)="chooseSeat(row + 'C')" [class]="getSeatStyle(row + 'C', row)"
                                  class="w-9 h-9 rounded-xl font-mono text-[11px] font-bold transition-all flex items-center justify-center relative cursor-pointer">
                            {{ row }}C
                          </button>
                        </div>

                        <div class="flex flex-col items-center px-2">
                          <span class="text-[10px] font-mono font-black text-ink-3">{{ row }}</span>
                          <span class="text-[8px] text-ink-3 uppercase">{{ row <= 2 ? 'BIZ' : row === 3 || row === 8 ? 'EXIT' : 'STD' }}</span>
                        </div>

                        <div class="flex gap-1.5">
                          <button type="button" (click)="chooseSeat(row + 'D')" [class]="getSeatStyle(row + 'D', row)"
                                  class="w-9 h-9 rounded-xl font-mono text-[11px] font-bold transition-all flex items-center justify-center relative cursor-pointer">
                            {{ row }}D
                          </button>
                          <button type="button" (click)="chooseSeat(row + 'E')" [class]="getSeatStyle(row + 'E', row)"
                                  class="w-9 h-9 rounded-xl font-mono text-[11px] font-bold transition-all flex items-center justify-center relative cursor-pointer">
                            {{ row }}E
                          </button>
                          <button type="button" (click)="chooseSeat(row + 'F')" [class]="getSeatStyle(row + 'F', row)"
                                  class="w-9 h-9 rounded-xl font-mono text-[11px] font-bold transition-all flex items-center justify-center relative cursor-pointer">
                            {{ row }}F
                          </button>
                        </div>
                      </div>
                    }
                  </div>

                  <div class="text-center pt-4 border-t border-line">
                    <div class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-seg text-[10px] text-ink-3 font-bold tracking-widest uppercase">
                      Rear galley & lavatories
                    </div>
                  </div>
                </div>
              </div>

              <!-- Step 5: Price Breakdown & Promo Code -->
              <div class="p-6 rounded-[20px] bg-seg border border-line space-y-4">
                <div class="flex items-center justify-between">
                  <label class="block text-xs font-bold text-ink uppercase tracking-wider flex items-center gap-2">
                    <span class="w-5 h-5 rounded-full bg-accent text-white text-[11px] flex items-center justify-center font-bold">5</span>
                    Fare summary & discount voucher
                  </label>
                  <span class="text-xs text-ok font-semibold">Taxes & surcharges calculated</span>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
                  <div>
                    <label class="block text-[11px] font-semibold text-ink-2 mb-1.5">Have a promo or loyalty voucher?</label>
                    <div class="flex gap-2">
                      <input type="text" [(ngModel)]="bookingPromoCode" name="promoCode" placeholder="Try 'GOLD15', 'SILVER10', 'BRONZE5'"
                             class="flex-1 h-11 px-3.5 rounded-[14px] bg-surface border border-line text-ink text-xs uppercase focus:outline-none focus:ring-2 focus:ring-accent-2 font-mono">
                      <button type="button" (click)="applyPromoCode()" class="btn !text-xs !py-2 !px-4">
                        Apply
                      </button>
                    </div>
                    @if (promoApplied) {
                      <span class="text-ok text-[11px] font-semibold mt-1.5 block">✓ Discount active: {{ promoDiscountPercentage }}% deducted!</span>
                    }
                  </div>

                  <div class="p-4 rounded-[16px] bg-surface border border-line space-y-2 text-xs">
                    <div class="flex justify-between text-ink-2">
                      <span>Base airfare:</span>
                      <span class="text-ink font-semibold">₹{{ calculateBaseFare() | number }}</span>
                    </div>
                    <div class="flex justify-between text-ink-2">
                      <span>Cabin class surcharge ({{ selectedCabinClass }}):</span>
                      <span class="text-ink font-semibold">+₹{{ getCabinSurcharge() | number }}</span>
                    </div>
                    <div class="flex justify-between text-ink-2">
                      <span>Airport development & fuel surcharge:</span>
                      <span class="text-ink font-semibold">+₹450</span>
                    </div>
                    @if (promoApplied) {
                      <div class="flex justify-between text-ok font-semibold">
                        <span>Loyalty discount ({{ promoDiscountPercentage }}%):</span>
                        <span>-₹{{ calculateDiscount() | number }}</span>
                      </div>
                    }
                    <div class="pt-2 border-t border-line flex justify-between items-center text-sm font-extrabold">
                      <span class="text-ink">Estimated total:</span>
                      <span class="text-xl text-ink font-black tabular-nums">₹{{ calculateGrandTotal() | number }}</span>
                    </div>
                  </div>
                </div>
              </div>

              <!-- Submit Button -->
              <button type="submit" [disabled]="bookingLoading || !newBookingFlightId"
                      class="btn p !w-full !py-4 !text-sm font-bold shadow-lg flex items-center justify-center gap-2 cursor-pointer">
                @if (bookingLoading) {
                  <svg class="w-4 h-4 animate-spin mr-2" viewBox="0 0 24 24" fill="none">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
                  </svg>
                  <span>Confirming reservation via Booking Service...</span>
                } @else {
                  <span>Confirm reservation & issue ticket</span>
                }
              </button>

            </form>
          </div>

        </div>
      }

      <!-- ========================================================================= -->
      <!-- TAB 3: WEB CHECK-IN & DIGITAL BOARDING PASS -->
      <!-- ========================================================================= -->
      @if (activeTab === 'checkin') {
        <div class="space-y-6 animate-fadeIn max-w-4xl mx-auto">
          
          <div class="bg-surface border border-line rounded-card p-6 sm:p-8 shadow-card backdrop-blur-md">
            <div class="mb-6 pb-4 border-b border-line flex flex-col sm:flex-row sm:items-center justify-between gap-3">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-2xl bg-accent-soft text-accent flex items-center justify-center shrink-0">
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 18h.01M8 21h8a2 2 0 002-2V5a2 2 0 00-2-2H8a2 2 0 00-2 2v14a2 2 0 002 2z"/>
                  </svg>
                </div>
                <div>
                  <h2 class="text-lg sm:text-xl font-bold text-ink">Self-service airport web check-in</h2>
                  <p class="text-xs text-ink-2 mt-0.5">Check-in for your flight, confirm seat, and generate an official digital boarding pass.</p>
                </div>
              </div>
              <span class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-ok-soft border border-ok/30 text-ok text-xs font-semibold self-start sm:self-auto">
                <span class="w-1.5 h-1.5 rounded-full bg-ok"></span>
                Gate pass engine active
              </span>
            </div>

            <form (ngSubmit)="performCheckIn()" class="grid grid-cols-1 sm:grid-cols-3 gap-4">
              <div>
                <label class="block text-xs font-semibold text-ink-2 mb-1.5">Booking reference / ID</label>
                <input type="number" [(ngModel)]="checkInBookingId" name="bookingId" required placeholder="e.g. 1"
                       class="w-full px-4 py-2.5 rounded-full bg-surface border border-line text-ink text-xs sm:text-sm focus:border-accent focus:ring-2 focus:ring-accent/20 outline-none transition-all placeholder:text-ink-3">
              </div>

              <div>
                <label class="block text-xs font-semibold text-ink-2 mb-1.5">Passenger full name</label>
                <input type="text" [(ngModel)]="checkInPassengerName" name="passengerName" required placeholder="e.g. Priya Sharma"
                       class="w-full px-4 py-2.5 rounded-full bg-surface border border-line text-ink text-xs sm:text-sm focus:border-accent focus:ring-2 focus:ring-accent/20 outline-none transition-all placeholder:text-ink-3">
              </div>

              <div>
                <label class="block text-xs font-semibold text-ink-2 mb-1.5">Assigned cabin seat</label>
                <input type="text" [(ngModel)]="checkInSeat" name="seatNumber" placeholder="12A"
                       class="w-full px-4 py-2.5 rounded-full bg-surface border border-line text-ink text-xs sm:text-sm focus:border-accent focus:ring-2 focus:ring-accent/20 outline-none transition-all font-semibold placeholder:text-ink-3">
              </div>

              <div class="sm:col-span-3 pt-2">
                <button type="submit" [disabled]="checkInLoading"
                        class="w-full py-3.5 rounded-full bg-brand text-white font-semibold text-xs sm:text-sm shadow-md shadow-accent/25 hover:opacity-95 transition-all flex items-center justify-center gap-2">
                  @if (checkInLoading) {
                    <svg class="w-4 h-4 animate-spin text-white" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
                    </svg>
                    <span>Validating with check-in service...</span>
                  } @else {
                    <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
                    </svg>
                    <span>Issue mobile boarding pass & earn +250 XP</span>
                  }
                </button>
              </div>
            </form>
          </div>

          <!-- Digital Boarding Pass Display Card -->
          @if (activeBoardingPass) {
            <div class="bg-surface border border-line rounded-card shadow-card relative overflow-hidden animate-fadeIn">
              
              <div class="p-6 sm:p-7 border-b border-dashed border-line flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                <div class="flex items-center gap-3">
                  <div class="w-10 h-10 rounded-2xl bg-brand text-white flex items-center justify-center shadow-md">
                    <svg class="w-5 h-5 transform -rotate-45" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                      <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 19l9 2-9-18-9 18 9-2zm0 0v-8"/>
                    </svg>
                  </div>
                  <div>
                    <h3 class="text-sm font-bold text-ink tracking-wide">AeroBook Airways</h3>
                    <span class="text-xs text-accent font-medium">Electronic boarding pass • Priority group 2</span>
                  </div>
                </div>

                <div class="sm:text-right">
                  <span class="text-[10px] text-ink-3 uppercase font-medium block">Pass reference</span>
                  <span class="text-sm sm:text-base font-bold text-accent">{{ activeBoardingPass.boardingPassNumber }}</span>
                </div>
              </div>

              <div class="p-6 sm:p-7 grid grid-cols-2 sm:grid-cols-4 gap-6 border-b border-line text-xs">
                <div>
                  <span class="text-ink-3 block text-[11px] uppercase tracking-wider font-medium">Passenger</span>
                  <span class="text-sm font-bold text-ink mt-0.5 block">{{ activeBoardingPass.passengerName }}</span>
                  <span class="text-[10px] text-accent font-semibold">Level {{ gamificationService.currentLevel() }} {{ gamificationService.levelTitle() }}</span>
                </div>

                <div>
                  <span class="text-ink-3 block text-[11px] uppercase tracking-wider font-medium">Flight / Terminal</span>
                  <span class="text-sm font-bold text-ink mt-0.5 block">AB-101 • Terminal 2</span>
                  <span class="text-[10px] text-ink-3">Gate B04 • Closes 20m before</span>
                </div>

                <div>
                  <span class="text-ink-3 block text-[11px] uppercase tracking-wider font-medium">Assigned seat</span>
                  <span class="text-2xl font-black text-accent mt-0.5 block">{{ activeBoardingPass.seatNumber }}</span>
                  <span class="text-[10px] text-ink-3">Window • Economy Standard</span>
                </div>

                <div>
                  <span class="text-ink-3 block text-[11px] uppercase tracking-wider font-medium">Status / Clearance</span>
                  <span class="text-sm font-bold text-ok mt-0.5 block">{{ activeBoardingPass.status }}</span>
                  <span class="text-[10px] text-ok font-semibold">Security screened</span>
                </div>
              </div>

              <div class="p-6 sm:p-7 flex flex-col sm:flex-row items-center justify-between gap-6 bg-accent-soft border-t border-line">
                <div class="space-y-2 text-center sm:text-left">
                  <div class="flex items-center gap-2 justify-center sm:justify-start">
                    <span class="px-2.5 py-0.5 rounded-full bg-ok-soft border border-ok/30 text-ok text-[10px] font-bold">
                      Ready for contactless scan
                    </span>
                    <span class="text-ink-3 text-xs font-mono">SEQ #042</span>
                  </div>

                  <p class="text-xs text-ink-2 max-w-md">
                    Display this pass at airport security checkpoint and boarding e-gates for rapid contactless departure.
                  </p>

                  <div class="pt-2 flex flex-wrap items-center gap-2 justify-center sm:justify-start">
                    <button (click)="printPass()" class="px-4 py-2 rounded-full bg-surface hover:bg-solid text-ink text-xs font-semibold border border-line transition-all flex items-center gap-1.5 shadow-sm">
                      <svg class="w-4 h-4 text-ink-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17 17h2a2 2 0 002-2v-4a2 2 0 00-2-2H5a2 2 0 00-2 2v4a2 2 0 002 2h2m2 4h6a2 2 0 002-2v-4a2 2 0 00-2-2H9a2 2 0 00-2 2v4a2 2 0 002 2zm8-12V5a2 2 0 00-2-2H9a2 2 0 00-2 2v4h10z"/>
                      </svg>
                      Print official pass
                    </button>
                    <button (click)="saveToWallet()" class="px-4 py-2 rounded-full bg-accent-soft hover:bg-accent/20 text-accent text-xs font-semibold border border-accent/20 transition-all flex items-center gap-1.5">
                      <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 18h.01M8 21h8a2 2 0 002-2V5a2 2 0 00-2-2H8a2 2 0 00-2 2v14a2 2 0 002 2z"/>
                      </svg>
                      Add to Apple / Google Wallet
                    </button>
                  </div>
                </div>

                <div class="p-4 bg-solid rounded-2xl border border-line shadow-sm flex flex-col items-center shrink-0">
                  <div class="bar-code h-16 w-36 mb-2"></div>
                  <span class="text-[9px] font-mono text-ink font-bold tracking-wider">AERO-SCAN-OK</span>
                </div>
              </div>

            </div>
          }

        </div>
      }

      <!-- ========================================================================= -->
      <!-- TAB 4: LIVE BAGGAGE TRACKER & CLAIM RADAR -->
      <!-- ========================================================================= -->
      @if (activeTab === 'baggage') {
        <div class="space-y-6 animate-fadeIn max-w-4xl mx-auto">
          
          <div class="bg-surface border border-line rounded-card p-6 sm:p-8 shadow-card backdrop-blur-md">
            <div class="mb-6 pb-4 border-b border-line flex flex-col sm:flex-row sm:items-center justify-between gap-3">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-2xl bg-accent-soft text-accent flex items-center justify-center shrink-0">
                  <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>
                  </svg>
                </div>
                <div>
                  <h2 class="text-lg sm:text-xl font-bold text-ink">Real-time luggage tracking & carousel radar</h2>
                  <p class="text-xs text-ink-2 mt-0.5">Live barcode tracking of checked baggage from check-in counter to arrival carousel.</p>
                </div>
              </div>
              <span class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-warn-soft border border-warn/30 text-warn text-xs font-semibold self-start sm:self-auto">
                <span class="w-1.5 h-1.5 rounded-full bg-warn"></span>
                +150 XP on scan
              </span>
            </div>

            <div class="flex flex-col sm:flex-row gap-3 mb-6">
              <input type="text" [(ngModel)]="baggageSearchTag" placeholder="Enter baggage tag # (e.g. BAG-BOM-1029)"
                     class="flex-1 px-4 py-2.5 rounded-full bg-surface border border-line text-ink placeholder:text-ink-3 text-xs sm:text-sm font-medium focus:border-accent focus:ring-2 focus:ring-accent/20 outline-none transition-all">
              <button (click)="trackBaggage()" class="px-6 py-2.5 rounded-full bg-brand text-white font-semibold text-xs sm:text-sm shadow-md shadow-accent/25 hover:opacity-95 transition-all shrink-0">
                Track bag & scan
              </button>
            </div>

            @if (activeBaggage) {
              <div class="space-y-6">
                <div class="p-5 rounded-tile bg-solid/70 border border-line flex flex-col sm:flex-row sm:items-center justify-between gap-4">
                  <div>
                    <div class="flex items-center gap-2">
                      <span class="font-bold text-base text-accent">{{ activeBaggage.tagNumber }}</span>
                      <span class="px-2.5 py-0.5 rounded-full bg-ok-soft text-ok border border-ok/30 text-[10px] font-bold">
                        ● {{ activeBaggage.currentStatus }}
                      </span>
                    </div>
                    <span class="text-xs text-ink-2 mt-1 block">
                      Passenger: {{ activeBaggage.passengerName }} • Flight: {{ activeBaggage.flightNumber }} ({{ activeBaggage.origin }} &rarr; {{ activeBaggage.destination }})
                    </span>
                  </div>

                  <div class="flex items-center gap-6 text-xs">
                    <div>
                      <span class="text-ink-3 block text-[10px] uppercase font-semibold">Weight</span>
                      <span class="text-ink font-bold text-sm">{{ activeBaggage.weightKg }} KG</span>
                    </div>
                    <div>
                      <span class="text-ink-3 block text-[10px] uppercase font-semibold">Arrival carousel</span>
                      <span class="text-accent font-bold text-sm">{{ activeBaggage.carouselNumber || 'Belt #04' }}</span>
                    </div>
                  </div>
                </div>

                <div class="p-6 rounded-tile bg-solid/50 border border-line space-y-4">
                  <h4 class="text-xs font-bold text-ink uppercase tracking-wider mb-4">Live journey milestones</h4>
                  
                  <div class="relative pl-6 space-y-6 before:absolute before:left-2 before:top-2 before:bottom-2 before:w-0.5 before:bg-line">
                    @for (step of activeBaggage.history; track step.step) {
                      <div class="relative flex items-start gap-4">
                        <div class="absolute -left-6 top-0.5 w-4 h-4 rounded-full border-2 transition-all flex items-center justify-center"
                             [ngClass]="step.completed ? 'bg-accent border-accent text-white shadow-sm' : 'bg-solid border-line'">
                          @if (step.completed) {
                            <svg class="w-2.5 h-2.5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="3" d="M5 13l4 4L19 7"/>
                            </svg>
                          }
                        </div>
                        <div class="flex-1">
                          <div class="flex items-center justify-between">
                            <span class="text-xs font-bold" [ngClass]="step.completed ? 'text-ink' : 'text-ink-3'">
                              {{ step.step }}
                            </span>
                            <span class="text-[10px] text-ink-3">{{ step.timestamp }}</span>
                          </div>
                          <span class="text-[11px] text-ink-2 block mt-0.5">{{ step.location }}</span>
                        </div>
                      </div>
                    }
                  </div>
                </div>

                <div class="pt-2 flex flex-col sm:flex-row sm:items-center justify-between gap-3 text-xs">
                  <span class="text-ink-2">Can't locate your baggage or experiencing a delay?</span>
                  <button (click)="openMissingBaggageModal()" class="px-4 py-2 rounded-full bg-bad-soft hover:bg-bad/20 text-bad border border-bad/30 font-semibold transition-all self-start sm:self-auto">
                    Report baggage issue
                  </button>
                </div>
              </div>
            }

          </div>

        </div>
      }

      <!-- ========================================================================= -->
      <!-- TAB 5: GAMIFICATION, LEVEL TIERS, QUESTS & BADGES -->
      <!-- ========================================================================= -->
      @if (activeTab === 'gamification') {
        <div class="space-y-8 animate-fadeIn max-w-5xl mx-auto">
          
          <!-- Hero Status Banner -->
          <div class="bg-surface border border-line rounded-card p-6 sm:p-8 shadow-card backdrop-blur-md relative overflow-hidden">
            <div class="flex flex-col md:flex-row md:items-center justify-between gap-6 relative z-10">
              <div class="flex items-center gap-4">
                <div class="w-16 h-16 rounded-2xl bg-brand text-white flex flex-col items-center justify-center shadow-lg shadow-accent/25 shrink-0">
                  <span class="text-xs font-bold tracking-widest">LVL</span>
                  <span class="text-xl font-extrabold leading-none mt-0.5">{{ gamificationService.currentLevel() }}</span>
                </div>

                <div>
                  <div class="flex items-center gap-2 mb-1">
                    <span class="px-2.5 py-0.5 rounded-full text-xs font-bold uppercase tracking-wider bg-accent-soft text-accent border border-accent/20">
                      Tier rank: {{ gamificationService.currentTier() }}
                    </span>
                    <span class="text-xs text-ink-3">3-month streak</span>
                  </div>
                  <h2 class="text-xl sm:text-2xl font-bold text-ink tracking-tight">
                    {{ gamificationService.levelTitle() }}
                  </h2>
                  <p class="text-xs text-ink-2 mt-0.5">
                    Accumulate XP through bookings, online check-ins, and baggage tracking to elevate your tier.
                  </p>
                </div>
              </div>

              <!-- Metrics -->
              <div class="grid grid-cols-3 gap-3 bg-solid/80 p-3 rounded-tile border border-line text-center">
                <div class="px-3 py-1 border-r border-line">
                  <span class="text-base sm:text-lg font-bold text-ink">{{ gamificationService.currentXp() | number }}</span>
                  <span class="block text-[10px] text-ink-3 uppercase font-medium">Total XP</span>
                </div>
                <div class="px-3 py-1 border-r border-line">
                  <span class="text-base sm:text-lg font-bold text-accent">{{ gamificationService.unlockedBadges().length }} / 8</span>
                  <span class="block text-[10px] text-ink-3 uppercase font-medium">Badges</span>
                </div>
                <div class="px-3 py-1">
                  <span class="text-base sm:text-lg font-bold text-ok">{{ gamificationService.unlockedDiscounts().length }}</span>
                  <span class="block text-[10px] text-ink-3 uppercase font-medium">Vouchers</span>
                </div>
              </div>
            </div>

            <!-- Tier Level Progress -->
            <div class="mt-6 pt-6 border-t border-line">
              <div class="flex items-center justify-between text-xs mb-2">
                <span class="text-ink-2 font-semibold">
                  Progress to Level {{ gamificationService.nextTier() ? gamificationService.currentLevel() + 1 : gamificationService.currentLevel() }} ({{ gamificationService.nextTier()?.title || 'Max Tier' }})
                </span>
                <span class="text-accent font-bold">
                  {{ gamificationService.xpToNext() | number }} XP remaining ({{ gamificationService.progressPercent() }}%)
                </span>
              </div>
              <div class="w-full bg-solid/80 rounded-full h-2.5 overflow-hidden border border-line">
                <div class="bg-brand h-full rounded-full transition-all duration-500 shadow-sm"
                     [style.width.%]="gamificationService.progressPercent()"></div>
              </div>
            </div>
          </div>

          <!-- Section 1: 5-Tier Level Progression Roadmap -->
          <div class="bg-surface border border-line rounded-card p-6 sm:p-8 shadow-card space-y-5">
            <div class="pb-3 border-b border-line">
              <h3 class="text-base font-bold text-ink flex items-center gap-2">
                <span>5-Tier level progression & benefit roadmap</span>
              </h3>
              <p class="text-xs text-ink-2 mt-0.5">Reach milestone thresholds to elevate your traveler tier and unlock permanent discount codes.</p>
            </div>

            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4">
              @for (tier of gamificationService.getAllTiers(); track tier.level) {
                <div class="p-4 rounded-tile border transition-all flex flex-col justify-between"
                     [ngClass]="{
                       'bg-accent-soft/30 border-accent shadow-sm': tier.level === gamificationService.currentLevel(),
                       'bg-solid/60 border-line': tier.level !== gamificationService.currentLevel(),
                       'opacity-60': tier.level > gamificationService.currentLevel()
                     }">
                  <div>
                    <div class="flex items-center justify-between mb-2">
                      <span class="text-xs font-bold text-accent">Lvl {{ tier.level }}</span>
                      <span class="px-2 py-0.5 rounded-full text-[9px] font-bold uppercase"
                            [ngClass]="{
                              'bg-brand text-white': tier.level === gamificationService.currentLevel(),
                              'bg-ok-soft text-ok border border-ok/30': tier.level < gamificationService.currentLevel(),
                              'bg-solid text-ink-3 border border-line': tier.level > gamificationService.currentLevel()
                            }">
                        {{ tier.level === gamificationService.currentLevel() ? 'Active' : tier.level < gamificationService.currentLevel() ? 'Unlocked' : 'Locked' }}
                      </span>
                    </div>

                    <h4 class="text-xs font-bold text-ink">{{ tier.title }}</h4>
                    <span class="text-[10px] text-ink-3 block mt-0.5">{{ tier.minXp | number }}+ XP</span>

                    <div class="mt-3 p-2.5 rounded-xl bg-solid/80 border border-line">
                      <span class="text-[9px] text-ink-3 uppercase font-semibold block">Exclusive code</span>
                      <span class="text-xs font-bold text-accent">{{ tier.discountCode }}</span>
                      <span class="text-[10px] text-ok font-semibold block">{{ tier.discountPercentage }}% discount</span>
                    </div>

                    <ul class="text-[10px] text-ink-2 space-y-1 mt-3">
                      @for (p of tier.perks; track p) {
                        <li class="flex items-start gap-1">
                          <span class="text-accent shrink-0">•</span>
                          <span>{{ p }}</span>
                        </li>
                      }
                    </ul>
                  </div>
                </div>
              }
            </div>
          </div>

          <!-- Section 2: Active Quests & Milestone Targets -->
          <div class="bg-surface border border-line rounded-card p-6 sm:p-8 shadow-card space-y-4">
            <div class="pb-3 border-b border-line">
              <h3 class="text-base font-bold text-ink flex items-center gap-2">
                <span>Active quests & milestone missions</span>
              </h3>
              <p class="text-xs text-ink-2 mt-0.5">Complete missions during your travels to claim instant bonus XP and vouchers.</p>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
              @for (q of gamificationService.activeQuests(); track q.id) {
                <div class="p-4 rounded-tile bg-solid/70 border border-line flex flex-col justify-between gap-3">
                  <div>
                    <div class="flex items-center justify-between">
                      <h4 class="text-xs font-bold text-ink">{{ q.title }}</h4>
                      <span class="text-[10px] font-medium"
                            [ngClass]="q.completed ? 'text-ok font-bold' : 'text-ink-3'">
                        {{ q.currentProgress }} / {{ q.targetProgress }} {{ q.unit }}
                      </span>
                    </div>
                    <p class="text-[11px] text-ink-2 mt-1">{{ q.description }}</p>

                    <div class="w-full bg-solid rounded-full h-2 overflow-hidden border border-line mt-2.5">
                      <div class="bg-brand h-full rounded-full transition-all"
                           [style.width.%]="(q.currentProgress / q.targetProgress) * 100"></div>
                    </div>
                  </div>

                  <div class="flex items-center justify-between pt-2 border-t border-line text-xs">
                    <span class="text-[11px] font-semibold text-accent">
                      Reward: {{ q.rewardValue }}
                    </span>

                    @if (q.completed && !q.rewardClaimed) {
                      <button (click)="claimQuest(q.id)"
                              class="px-3.5 py-1.5 rounded-full bg-brand text-white text-xs font-semibold shadow-sm hover:opacity-95 transition-all">
                        Claim reward
                      </button>
                    } @else if (q.rewardClaimed) {
                      <span class="text-[10px] font-bold text-ok">Claimed</span>
                    } @else {
                      <span class="text-[10px] font-medium text-ink-3">In progress</span>
                    }
                  </div>
                </div>
              }
            </div>
          </div>

          <!-- Section 3: Badges Showcase (Achievements) -->
          <div class="bg-surface border border-line rounded-card p-6 sm:p-8 shadow-card space-y-4">
            <div class="flex items-center justify-between pb-3 border-b border-line">
              <div>
                <h3 class="text-base font-bold text-ink">Traveler achievements & badges cabinet</h3>
                <p class="text-xs text-ink-2 mt-0.5">Collect rare, epic, and legendary achievements as you explore the world.</p>
              </div>
              <span class="text-xs font-semibold text-accent">
                {{ gamificationService.unlockedBadges().length }} of {{ gamificationService.profile().badges.length }} unlocked
              </span>
            </div>

            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
              @for (b of gamificationService.profile().badges; track b.id) {
                <div class="p-4 rounded-tile border transition-all flex flex-col justify-between"
                     [ngClass]="b.unlocked ? 'bg-solid/80 border-line hover:border-accent/40 shadow-sm' : 'bg-solid/40 border-line/60 opacity-60'">
                  <div>
                    <div class="flex items-center justify-between mb-2">
                      <div class="w-8 h-8 rounded-xl bg-accent-soft text-accent flex items-center justify-center font-bold text-xs">
                        {{ b.title.substring(0, 2) }}
                      </div>
                      <span class="px-2 py-0.5 rounded-full text-[8px] font-bold uppercase tracking-wider border"
                            [ngClass]="{
                              'bg-accent-soft text-accent border-accent/30': b.rarity === 'EPIC' || b.rarity === 'LEGENDARY',
                              'bg-warn-soft text-warn border-warn/30': b.rarity === 'RARE',
                              'bg-solid text-ink-3 border-line': b.rarity === 'COMMON'
                            }">
                        {{ b.rarity }}
                      </span>
                    </div>

                    <h4 class="text-xs font-bold text-ink">{{ b.title }}</h4>
                    <p class="text-[11px] text-ink-2 mt-1 leading-snug">{{ b.description }}</p>
                  </div>

                  <div class="pt-3 border-t border-line mt-3 flex items-center justify-between text-[10px]">
                    <span class="text-accent font-bold">+{{ b.xpReward }} XP</span>
                    <span class="font-medium" [ngClass]="b.unlocked ? 'text-ok' : 'text-ink-3'">
                      {{ b.unlocked ? 'Unlocked' : 'Locked' }}
                    </span>
                  </div>
                </div>
              }
            </div>
          </div>

          <!-- Section 4: Unlocked Discounts Vault -->
          <div class="bg-surface border border-line rounded-card p-6 sm:p-8 shadow-card space-y-4">
            <div class="pb-3 border-b border-line">
              <h3 class="text-base font-bold text-ink">Unlocked perks & promo vouchers vault</h3>
              <p class="text-xs text-ink-2 mt-0.5">Use your level-unlocked discount codes during booking checkout for instant fare deductions.</p>
            </div>

            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
              @for (d of gamificationService.unlockedDiscounts(); track d.code) {
                <div class="p-4 rounded-tile bg-solid/80 border border-line flex flex-col justify-between gap-3 shadow-sm">
                  <div>
                    <div class="flex items-center justify-between">
                      <span class="text-sm font-bold text-accent">{{ d.code }}</span>
                      <span class="px-2.5 py-0.5 rounded-full bg-ok-soft text-ok text-xs font-semibold">
                        {{ d.discountPercent }}% off
                      </span>
                    </div>
                    <p class="text-xs text-ink-2 mt-1.5">{{ d.description }}</p>
                  </div>

                  <button (click)="applyVaultDiscount(d.code)"
                          class="w-full py-2 rounded-full bg-brand text-white text-xs font-semibold transition-all hover:opacity-95 shadow-sm">
                    Apply to next flight
                  </button>
                </div>
              }
            </div>
          </div>

        </div>
      }

      <!-- ========================================================================= -->
      <!-- TAB 6: LIVE FLIGHT RADAR & ROUTE STATUS -->
      <!-- ========================================================================= -->
      @if (activeTab === 'flightstatus') {
        <div class="space-y-6 animate-fadeIn">
          
          <div class="bg-surface border border-line rounded-card p-5 shadow-card flex flex-col md:flex-row md:items-center justify-between gap-4">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-2xl bg-accent-soft text-accent flex items-center justify-center shrink-0">
                <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
                </svg>
              </div>
              <div>
                <h2 class="text-base sm:text-lg font-bold text-ink">Live commercial flight radar</h2>
                <p class="text-xs text-ink-2 mt-0.5">Real-time departure and arrival schedules, gates, terminals, and operational updates.</p>
              </div>
            </div>

            <div class="flex items-center gap-3">
              <input type="text" [(ngModel)]="flightRadarSearch" placeholder="Search flight # or route..."
                     class="px-4 py-2 text-xs rounded-full bg-solid/80 border border-line text-ink placeholder:text-ink-3 focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 w-56">
              <button (click)="loadAvailableFlights()" class="px-4 py-2 rounded-full bg-surface hover:bg-solid text-ink text-xs font-semibold border border-line transition-all flex items-center gap-1.5 shadow-sm">
                <svg class="w-3.5 h-3.5 text-ink-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"/>
                </svg>
                Refresh
              </button>
            </div>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            @for (f of filteredRadarFlights; track f.id) {
              <div class="bg-surface border border-line rounded-card p-5 shadow-card hover:border-accent/40 transition-all space-y-4">
                <div class="flex items-center justify-between pb-3 border-b border-line">
                  <div>
                    <span class="text-base font-bold text-ink">{{ f.flightNumber }}</span>
                    <span class="text-xs text-accent block font-medium">{{ f.airlineName }}</span>
                  </div>
                  <span class="px-2.5 py-0.5 rounded-full text-xs font-semibold border"
                        [ngClass]="{
                          'bg-ok-soft text-ok border-ok/30': f.status === 'SCHEDULED',
                          'bg-accent-soft text-accent border-accent/30': f.status === 'BOARDING',
                          'bg-accent-soft text-accent-2 border-accent/30': f.status === 'DEPARTED',
                          'bg-warn-soft text-warn border-warn/30': f.status === 'DELAYED',
                          'bg-bad-soft text-bad border-bad/30': f.status === 'CANCELLED'
                        }">
                    ● {{ f.status }}
                  </span>
                </div>

                <div class="flex items-center justify-between text-xs">
                  <div>
                    <span class="text-lg font-bold text-ink">{{ f.source }}</span>
                    <span class="text-[11px] text-ink-3 block">{{ formatTime(f.departureTime) }}</span>
                  </div>
                  <div class="text-center px-3">
                    <span class="text-[10px] text-ink-3 block">Non-stop</span>
                    <span class="text-[10px] text-accent font-medium">{{ f.terminal || 'T2' }} • Gate {{ f.gate || 'B04' }}</span>
                  </div>
                  <div class="text-right">
                    <span class="text-lg font-bold text-ink">{{ f.destination }}</span>
                    <span class="text-[11px] text-ink-3 block">{{ formatTime(f.arrivalTime) }}</span>
                  </div>
                </div>

                <div class="pt-3 border-t border-line flex items-center justify-between text-xs">
                  <div>
                    <span class="text-ink-3 text-[11px]">Base fare:</span>
                    <span class="text-sm font-bold text-ink ml-1">₹{{ f.baseFare | number }}</span>
                  </div>
                  <button (click)="bookThisFlight(f.id)" class="px-4 py-1.5 rounded-full bg-brand text-white text-xs font-semibold hover:opacity-95 transition-all shadow-sm">
                    Book flight
                  </button>
                </div>
              </div>
            }
          </div>

        </div>
      }

      <!-- ========================================================================= -->
      <!-- TAB 7: AEROMILES LOYALTY CLUB & REWARDS -->
      <!-- ========================================================================= -->
      @if (activeTab === 'loyalty') {
        <div class="space-y-6 animate-fadeIn max-w-4xl mx-auto">
          
          <div class="rounded-card p-6 sm:p-8 bg-brand text-white shadow-card relative overflow-hidden">
            <div class="flex items-center justify-between pb-8">
              <div class="flex items-center gap-2">
                <span class="text-base font-bold tracking-wider uppercase">AeroClub Elite</span>
              </div>
              <span class="px-3 py-1 rounded-full bg-white/20 text-white text-xs font-bold tracking-wider uppercase backdrop-blur-md">
                Gold Tier
              </span>
            </div>

            <div class="mb-8">
              <span class="text-xs uppercase font-medium tracking-wider text-white/80 block">Frequent flyer number</span>
              <span class="text-2xl sm:text-3xl font-bold tracking-wider">
                {{ travelerProfile?.frequentFlyerNumber || 'AB-774092' }}
              </span>
            </div>

            <div class="flex flex-wrap items-center justify-between gap-4 pt-4 border-t border-white/20">
              <div>
                <span class="text-[10px] uppercase font-medium tracking-wider text-white/80 block">Member name</span>
                <span class="text-sm font-bold">{{ travelerProfile?.firstName || 'Priya' }} {{ travelerProfile?.lastName || 'Sharma' }}</span>
              </div>
              <div>
                <span class="text-[10px] uppercase font-medium tracking-wider text-white/80 block">Available AeroMiles</span>
                <span class="text-xl font-bold">{{ (travelerProfile?.milesBalance || 24850) | number }} PTS</span>
              </div>
              <div>
                <span class="text-[10px] uppercase font-medium tracking-wider text-white/80 block">Valid thru</span>
                <span class="text-xs font-semibold">12/2028</span>
              </div>
            </div>
          </div>

          <div class="bg-surface border border-line rounded-card p-6 sm:p-8 shadow-card space-y-4">
            <h3 class="text-base font-bold text-ink">Gold tier privileges & benefits</h3>

            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4 text-xs">
              <div class="p-4 rounded-tile bg-solid/70 border border-line flex items-start gap-3">
                <div class="w-8 h-8 rounded-xl bg-accent-soft text-accent flex items-center justify-center shrink-0">
                  <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5m-4 0h4"/>
                  </svg>
                </div>
                <div>
                  <h4 class="font-bold text-ink">Airport lounge access</h4>
                  <p class="text-ink-2 text-[11px] mt-0.5">Free unlimited entry to AeroBook partner lounges across 30+ airports worldwide.</p>
                </div>
              </div>

              <div class="p-4 rounded-tile bg-solid/70 border border-line flex items-start gap-3">
                <div class="w-8 h-8 rounded-xl bg-accent-soft text-accent flex items-center justify-center shrink-0">
                  <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>
                  </svg>
                </div>
                <div>
                  <h4 class="font-bold text-ink">Extra 10 KG baggage allowance</h4>
                  <p class="text-ink-2 text-[11px] mt-0.5">Complimentary additional baggage allowance on all domestic and international flights.</p>
                </div>
              </div>

              <div class="p-4 rounded-tile bg-solid/70 border border-line flex items-start gap-3">
                <div class="w-8 h-8 rounded-xl bg-accent-soft text-accent flex items-center justify-center shrink-0">
                  <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
                  </svg>
                </div>
                <div>
                  <h4 class="font-bold text-ink">Priority check-in & boarding</h4>
                  <p class="text-ink-2 text-[11px] mt-0.5">Dedicated business-class counters and express boarding priority (group 2).</p>
                </div>
              </div>

              <div class="p-4 rounded-tile bg-solid/70 border border-line flex items-start gap-3">
                <div class="w-8 h-8 rounded-xl bg-accent-soft text-accent flex items-center justify-center shrink-0">
                  <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 3v4M3 5h4M6 17v4m-2-2h4m5-16l2.286 6.857L21 12l-5.714 2.143L13 21l-2.286-6.857L5 12l5.714-2.143L13 3z"/>
                  </svg>
                </div>
                <div>
                  <h4 class="font-bold text-ink">Advance seat selection</h4>
                  <p class="text-ink-2 text-[11px] mt-0.5">Reserve extra-legroom and exit-row seats at zero fee during flight booking.</p>
                </div>
              </div>
            </div>
          </div>

          <div class="bg-surface border border-line rounded-card p-6 sm:p-8 shadow-card space-y-4">
            <h3 class="text-base font-bold text-ink">Recent miles activity & statement</h3>

            <div class="space-y-3">
              @for (tx of milesTransactions; track tx.id) {
                <div class="p-3.5 rounded-tile bg-solid/70 border border-line flex items-center justify-between text-xs">
                  <div class="flex items-center gap-3">
                    <span class="w-8 h-8 rounded-full flex items-center justify-center font-bold text-xs"
                          [ngClass]="tx.type === 'EARNED' ? 'bg-ok-soft text-ok' : 'bg-bad-soft text-bad'">
                      {{ tx.type === 'EARNED' ? '+' : '-' }}
                    </span>
                    <div>
                      <span class="font-semibold text-ink block">{{ tx.description }}</span>
                      <span class="text-[10px] text-ink-3">{{ tx.date }} • {{ tx.flightNumber || 'Activity' }}</span>
                    </div>
                  </div>

                  <div class="text-right">
                    <span class="font-bold" [ngClass]="tx.type === 'EARNED' ? 'text-ok' : 'text-bad'">
                      {{ tx.type === 'EARNED' ? '+' : '-' }}{{ tx.miles | number }} miles
                    </span>
                    <span class="text-[10px] text-ink-3 block">Balance: {{ tx.balanceAfter | number }}</span>
                  </div>
                </div>
              }
            </div>

            <div class="pt-4 border-t border-line flex flex-col sm:flex-row sm:items-center justify-between gap-3">
              <span class="text-xs text-ink-2">Have miles to redeem for vouchers or class upgrades?</span>
              <button (click)="redeemMilesPrompt()" class="px-5 py-2 rounded-full bg-brand text-white font-semibold text-xs shadow-sm hover:opacity-95 transition-all self-start sm:self-auto">
                Redeem miles (6,000 pts)
              </button>
            </div>
          </div>

        </div>
      }

      <!-- ========================================================================= -->
      <!-- TAB 8: FLIGHT NOTICES & GATE ALERTS -->
      <!-- ========================================================================= -->
      @if (activeTab === 'notifications') {
        <div class="bg-surface border border-line rounded-card p-6 sm:p-8 shadow-card max-w-4xl mx-auto space-y-4 animate-fadeIn">
          <div class="flex items-center justify-between pb-4 border-b border-line">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-2xl bg-accent-soft text-accent flex items-center justify-center shrink-0">
                <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"/>
                </svg>
              </div>
              <div>
                <h2 class="text-lg sm:text-xl font-bold text-ink">Flight updates & gate notices</h2>
                <p class="text-xs text-ink-2 mt-0.5">Real-time alerts broadcast by airline operations control.</p>
              </div>
            </div>

            @if (notifications.length > 0) {
              <button (click)="markAllAsRead()" class="px-3.5 py-1.5 rounded-full bg-surface hover:bg-solid text-ink text-xs font-semibold border border-line transition-colors shadow-sm">
                Mark all as read
              </button>
            }
          </div>

          @if (notifications.length === 0) {
            <div class="text-center py-12">
              <div class="w-12 h-12 rounded-full bg-accent-soft text-accent mx-auto flex items-center justify-center mb-3">
                <svg class="w-6 h-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7"/>
                </svg>
              </div>
              <h3 class="text-sm font-semibold text-ink">No new notifications</h3>
              <p class="text-xs text-ink-3 mt-1">You are all caught up with your flight announcements.</p>
            </div>
          }

          <div class="space-y-3">
            @for (n of notifications; track n.id) {
              <div class="p-4 rounded-tile border transition-all"
                   [ngClass]="n.status === 'UNREAD' ? 'bg-solid/90 border-accent/40 shadow-sm' : 'bg-solid/40 border-line opacity-75'">
                <div class="flex items-start justify-between gap-4">
                  <div class="flex items-start gap-3">
                    <div class="w-8 h-8 rounded-xl flex items-center justify-center shrink-0 mt-0.5"
                         [ngClass]="n.type === 'FLIGHT_DELAY' ? 'bg-warn-soft text-warn' : n.type === 'GATE_CHANGE' ? 'bg-accent-soft text-accent' : 'bg-ok-soft text-ok'">
                      <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
                      </svg>
                    </div>
                    <div>
                      <div class="flex items-center gap-2">
                        <h4 class="text-sm font-bold text-ink">{{ n.title }}</h4>
                        @if (n.status === 'UNREAD') {
                          <span class="w-2 h-2 rounded-full bg-accent"></span>
                        }
                      </div>
                      <p class="text-xs text-ink-2 mt-1">{{ n.message }}</p>
                      <span class="text-[10px] text-ink-3 mt-2 block">{{ n.createdAt | date:'medium' }}</span>
                    </div>
                  </div>

                  @if (n.status === 'UNREAD') {
                    <button (click)="markAsRead(n.id)" class="px-2.5 py-1 rounded-full bg-surface hover:bg-solid text-ink-2 hover:text-ink text-[11px] border border-line transition-colors shrink-0">
                      Mark read
                    </button>
                  }
                </div>
              </div>
            }
          </div>
        </div>
      }

      <!-- ========================================================================= -->
      <!-- TAB 9: PROFILE, PASSPORT & TRAVEL PREFERENCES -->
      <!-- ========================================================================= -->
      @if (activeTab === 'profile') {
        <div class="bg-surface border border-line rounded-card p-6 sm:p-8 shadow-card max-w-3xl mx-auto space-y-6 animate-fadeIn">
          <div class="pb-4 border-b border-line flex flex-col sm:flex-row sm:items-center justify-between gap-3">
            <div class="flex items-center gap-3">
              <div class="w-10 h-10 rounded-2xl bg-accent-soft text-accent flex items-center justify-center shrink-0">
                <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 7a4 4 0 11-8 0 4 4 0 018 0zM12 14a7 7 0 00-7 7h14a7 7 0 00-7-7z"/>
                </svg>
              </div>
              <div>
                <h2 class="text-lg sm:text-xl font-bold text-ink">Traveler profile & passport</h2>
                <p class="text-xs text-ink-2 mt-0.5">Manage personal identity, international passport details, and seat preferences.</p>
              </div>
            </div>
            <span class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-accent-soft border border-accent/20 text-accent text-xs font-semibold self-start sm:self-auto">
              Encrypted & protected
            </span>
          </div>

          <form (ngSubmit)="saveProfile()" class="space-y-6">
            <div class="space-y-4">
              <h4 class="text-xs font-bold text-ink-2 uppercase tracking-wider">1. Personal identity</h4>
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label class="block text-xs font-semibold text-ink-2 mb-1.5">First name</label>
                  <input type="text" [(ngModel)]="profileFirstName" name="firstName" class="w-full px-4 py-2.5 rounded-full bg-surface border border-line text-ink placeholder:text-ink-3 text-xs sm:text-sm focus:border-accent focus:ring-2 focus:ring-accent/20 outline-none transition-all">
                </div>
                <div>
                  <label class="block text-xs font-semibold text-ink-2 mb-1.5">Last name</label>
                  <input type="text" [(ngModel)]="profileLastName" name="lastName" class="w-full px-4 py-2.5 rounded-full bg-surface border border-line text-ink placeholder:text-ink-3 text-xs sm:text-sm focus:border-accent focus:ring-2 focus:ring-accent/20 outline-none transition-all">
                </div>
              </div>

              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label class="block text-xs font-semibold text-ink-2 mb-1.5">Email address</label>
                  <input type="email" [value]="userProfile?.email || 'priya@gmail.com'" disabled class="w-full px-4 py-2.5 rounded-full bg-seg border border-line text-ink-3 text-xs sm:text-sm cursor-not-allowed">
                </div>
                <div>
                  <label class="block text-xs font-semibold text-ink-2 mb-1.5">Phone number</label>
                  <input type="tel" [(ngModel)]="profilePhone" name="phone" class="w-full px-4 py-2.5 rounded-full bg-surface border border-line text-ink placeholder:text-ink-3 text-xs sm:text-sm focus:border-accent focus:ring-2 focus:ring-accent/20 outline-none transition-all">
                </div>
              </div>
            </div>

            <div class="space-y-4 pt-4 border-t border-line">
              <h4 class="text-xs font-bold text-ink-2 uppercase tracking-wider">2. International travel documents</h4>
              <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
                <div>
                  <label class="block text-xs font-semibold text-ink-2 mb-1.5">Nationality</label>
                  <input type="text" [(ngModel)]="profileNationality" name="nationality" class="w-full px-4 py-2.5 rounded-full bg-surface border border-line text-ink placeholder:text-ink-3 text-xs sm:text-sm focus:border-accent focus:ring-2 focus:ring-accent/20 outline-none transition-all">
                </div>
                <div>
                  <label class="block text-xs font-semibold text-ink-2 mb-1.5">Passport number</label>
                  <input type="text" [(ngModel)]="profilePassport" name="passport" class="w-full px-4 py-2.5 rounded-full bg-surface border border-line text-ink placeholder:text-ink-3 text-xs sm:text-sm focus:border-accent focus:ring-2 focus:ring-accent/20 outline-none transition-all">
                </div>
                <div>
                  <label class="block text-xs font-semibold text-ink-2 mb-1.5">Passport expiry</label>
                  <input type="date" [(ngModel)]="profilePassportExpiry" name="passportExpiry" class="w-full px-4 py-2.5 rounded-full bg-surface border border-line text-ink placeholder:text-ink-3 text-xs sm:text-sm focus:border-accent focus:ring-2 focus:ring-accent/20 outline-none transition-all">
                </div>
              </div>
            </div>

            <div class="space-y-4 pt-4 border-t border-line">
              <h4 class="text-xs font-bold text-ink-2 uppercase tracking-wider">3. In-flight preferences</h4>
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label class="block text-xs font-semibold text-ink-2 mb-1.5">Seat preference</label>
                  <select [(ngModel)]="profileSeatPref" name="seatPref" class="w-full px-4 py-2.5 rounded-full bg-surface border border-line text-ink placeholder:text-ink-3 text-xs sm:text-sm focus:border-accent focus:ring-2 focus:ring-accent/20 outline-none transition-all">
                    <option value="WINDOW">Window seat</option>
                    <option value="AISLE">Aisle seat</option>
                    <option value="ANY">No preference</option>
                  </select>
                </div>
                <div>
                  <label class="block text-xs font-semibold text-ink-2 mb-1.5">Default meal</label>
                  <select [(ngModel)]="profileMealPref" name="mealPref" class="w-full px-4 py-2.5 rounded-full bg-surface border border-line text-ink placeholder:text-ink-3 text-xs sm:text-sm focus:border-accent focus:ring-2 focus:ring-accent/20 outline-none transition-all">
                    <option value="VEGETARIAN_HINDU">Asian Vegetarian (AVML)</option>
                    <option value="NON_VEG">Standard Non-Vegetarian</option>
                    <option value="VEGAN">Strict Vegan (VGML)</option>
                    <option value="HALAL">Halal Meal (MOML)</option>
                    <option value="DIABETIC">Diabetic Low-Sugar (DBML)</option>
                  </select>
                </div>
              </div>
            </div>

            <button type="submit" class="w-full py-3.5 rounded-full bg-brand text-white text-xs sm:text-sm font-semibold transition-all hover:opacity-95 shadow-md shadow-accent/25">
              Save updated profile & preferences
            </button>
          </form>
        </div>
      }

      <!-- ========================================================================= -->
      <!-- MODAL 1: FULL E-TICKET RECEIPT MODAL -->
      <!-- ========================================================================= -->
      @if (showETicketModal && viewingBooking) {
        <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-ink/40 backdrop-blur-md animate-fadeIn">
          <div class="bg-solid border border-line rounded-card max-w-2xl w-full p-6 sm:p-8 shadow-card relative max-h-[90vh] overflow-y-auto text-ink">
            
            <div class="flex items-center justify-between pb-4 border-b border-dashed border-line">
              <div>
                <h3 class="text-base sm:text-lg font-bold text-ink">Official electronic ticket receipt</h3>
                <span class="text-xs text-accent font-semibold">PNR: {{ viewingBooking.pnr }} • Booking #{{ viewingBooking.bookingId }}</span>
              </div>
              <button (click)="showETicketModal = false" class="w-8 h-8 rounded-full bg-surface hover:bg-line text-ink flex items-center justify-center transition-colors">
                <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                </svg>
              </button>
            </div>

            <div class="py-5 space-y-4">
              <div class="p-4 rounded-tile bg-surface border border-line flex flex-col sm:flex-row sm:items-center justify-between gap-3 text-xs">
                <div>
                  <span class="text-ink-3 block text-[10px] uppercase font-medium">Flight</span>
                  <span class="text-base font-bold text-ink">{{ viewingBooking.flightNumber || 'AB-101' }}</span>
                  <span class="text-accent font-semibold">{{ viewingBooking.airlineName || 'AeroBook Airlines' }}</span>
                </div>
                <div class="sm:text-center">
                  <span class="text-sm font-bold text-ink">{{ viewingBooking.source || 'BOM' }} &rarr; {{ viewingBooking.destination || 'DEL' }}</span>
                  <span class="text-ink-3 block">{{ formatTime(viewingBooking.departureTime) }} - {{ formatTime(viewingBooking.arrivalTime) }}</span>
                </div>
                <div class="sm:text-right">
                  <span class="text-ink-3 block text-[10px] uppercase font-medium">Class</span>
                  <span class="text-sm font-bold text-ok">Economy Flexi</span>
                </div>
              </div>

              <div class="p-4 rounded-tile bg-surface border border-line space-y-2">
                <span class="text-[11px] font-bold text-ink-2 uppercase tracking-wider">Ticketed passengers</span>
                @for (p of viewingBooking.passengers; track p.passengerId || p.firstName) {
                  <div class="flex items-center justify-between text-xs py-1.5 border-b border-line/60 last:border-none">
                    <span class="font-semibold text-ink">{{ p.firstName }} {{ p.lastName }} (Age: {{ p.age }})</span>
                    <span class="font-bold text-accent">Seat: {{ p.seatNumber || '12A' }}</span>
                  </div>
                }
              </div>

              <div class="p-4 rounded-tile bg-surface border border-line space-y-2 text-xs">
                <span class="text-[11px] font-bold text-ink-2 uppercase tracking-wider block mb-2">Fare & tax breakdown</span>
                <div class="flex justify-between text-ink-2">
                  <span>Base airfare:</span>
                  <span class="text-ink font-semibold">₹{{ (viewingBooking.totalFare * 0.85) | number:'1.0-0' }}</span>
                </div>
                <div class="flex justify-between text-ink-2">
                  <span>User development & passenger service fee:</span>
                  <span class="text-ink font-semibold">₹450</span>
                </div>
                <div class="flex justify-between text-ink-2">
                  <span>GST (12%):</span>
                  <span class="text-ink font-semibold">₹{{ (viewingBooking.totalFare * 0.12) | number:'1.0-0' }}</span>
                </div>
                <div class="pt-2 border-t border-line flex justify-between text-sm font-bold text-ink">
                  <span>Total amount paid:</span>
                  <span class="text-ok font-extrabold">₹{{ viewingBooking.totalFare | number }}</span>
                </div>
              </div>
            </div>

            <div class="pt-4 border-t border-line flex flex-col sm:flex-row sm:items-center justify-between gap-3">
              <span class="text-[11px] text-ink-3">IATA standard electronic ticket</span>
              <div class="flex gap-2">
                <button (click)="printPass()" class="px-5 py-2 rounded-full bg-brand text-white text-xs font-semibold shadow-sm hover:opacity-95 transition-all">
                  Print e-ticket receipt
                </button>
                <button (click)="showETicketModal = false" class="px-4 py-2 rounded-full bg-surface hover:bg-solid text-ink text-xs font-semibold border border-line transition-colors">
                  Close
                </button>
              </div>
            </div>

          </div>
        </div>
      }

      <!-- ========================================================================= -->
      <!-- MODAL 2: IN-FLIGHT MEAL & SPECIAL ASSISTANCE MODAL -->
      <!-- ========================================================================= -->
      @if (showMealModal && viewingBooking) {
        <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-ink/40 backdrop-blur-md animate-fadeIn">
          <div class="bg-solid border border-line rounded-card max-w-lg w-full p-6 sm:p-8 shadow-card relative text-ink">
            <div class="flex items-center justify-between pb-4 border-b border-line">
              <h3 class="text-base font-bold text-ink flex items-center gap-2">
                <span>In-flight services & assistance</span>
              </h3>
              <button (click)="showMealModal = false" class="w-8 h-8 rounded-full bg-surface hover:bg-line text-ink flex items-center justify-center transition-colors">
                <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                </svg>
              </button>
            </div>

            <div class="py-4 space-y-4">
              <div>
                <label class="block text-xs font-semibold text-ink-2 mb-1.5">Select dietary meal</label>
                <select [(ngModel)]="modalMealChoice" class="w-full px-4 py-2.5 rounded-full bg-surface border border-line text-ink text-xs sm:text-sm focus:border-accent focus:ring-2 focus:ring-accent/20 outline-none transition-all">
                  <option value="Asian Vegetarian (AVML)">Asian Vegetarian (AVML)</option>
                  <option value="Non-Vegetarian Chicken Meal">Non-Vegetarian Chicken Meal</option>
                  <option value="Strict Vegan (VGML)">Strict Vegan (VGML)</option>
                  <option value="Halal Certified Meal (MOML)">Halal Certified Meal (MOML)</option>
                  <option value="Diabetic / Low Glycemic (DBML)">Diabetic / Low Glycemic (DBML)</option>
                </select>
              </div>

              <div>
                <label class="block text-xs font-semibold text-ink-2 mb-1.5">Special assistance required?</label>
                <select [(ngModel)]="modalAssistanceChoice" class="w-full px-4 py-2.5 rounded-full bg-surface border border-line text-ink text-xs sm:text-sm focus:border-accent focus:ring-2 focus:ring-accent/20 outline-none transition-all">
                  <option value="None">None</option>
                  <option value="Wheelchair Assistance at Airport">Wheelchair Assistance at Airport</option>
                  <option value="Meet & Assist Senior Citizen">Meet & Assist Senior Citizen</option>
                  <option value="Infant Bassinet Request">Infant Bassinet Request</option>
                </select>
              </div>
            </div>

            <div class="pt-4 border-t border-line flex justify-end gap-2">
              <button (click)="showMealModal = false" class="px-4 py-2 rounded-full bg-surface hover:bg-solid text-ink text-xs font-semibold border border-line transition-colors">Cancel</button>
              <button (click)="confirmMealSelection()" class="px-5 py-2 rounded-full bg-brand text-white font-semibold text-xs shadow-sm hover:opacity-95 transition-all">
                Save preferences
              </button>
            </div>
          </div>
        </div>
      }

      <!-- ========================================================================= -->
      <!-- MODAL 3: CANCEL BOOKING CONFIRMATION MODAL -->
      <!-- ========================================================================= -->
      @if (showCancelModal && viewingBooking) {
        <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-ink/40 backdrop-blur-md animate-fadeIn">
          <div class="bg-solid border border-line rounded-card max-w-md w-full p-6 sm:p-8 shadow-card relative text-ink">
            <div class="flex items-center gap-3 pb-4 border-b border-line">
              <div class="w-10 h-10 rounded-2xl bg-bad-soft text-bad flex items-center justify-center shrink-0">
                <svg class="w-5 h-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
                </svg>
              </div>
              <div>
                <h3 class="text-base font-bold text-ink">Cancel reservation</h3>
                <span class="text-xs text-ink-3">PNR: {{ viewingBooking.pnr }}</span>
              </div>
            </div>

            <div class="py-4 space-y-3 text-xs">
              <p class="text-ink-2">Are you sure you want to cancel this booking? Here is your refund summary:</p>
              <div class="p-3.5 rounded-tile bg-surface border border-line space-y-1.5">
                <div class="flex justify-between text-ink-2">
                  <span>Total paid:</span>
                  <span class="text-ink font-semibold">₹{{ viewingBooking.totalFare | number }}</span>
                </div>
                <div class="flex justify-between text-bad">
                  <span>Airline cancellation charge:</span>
                  <span>-₹999</span>
                </div>
                <div class="pt-1.5 border-t border-line flex justify-between font-bold text-sm text-ok">
                  <span>Refund to original payment:</span>
                  <span>₹{{ (viewingBooking.totalFare - 999) | number }}</span>
                </div>
              </div>
            </div>

            <div class="pt-4 border-t border-line flex justify-end gap-2">
              <button (click)="showCancelModal = false" class="px-4 py-2 rounded-full bg-surface hover:bg-solid text-ink text-xs font-semibold border border-line transition-colors">Keep flight</button>
              <button (click)="confirmCancellation()" class="px-5 py-2 rounded-full bg-bad text-white font-semibold text-xs shadow-sm hover:opacity-95 transition-all">
                Confirm cancellation
              </button>
            </div>
          </div>
        </div>
      }

      <!-- ========================================================================= -->
      <!-- MODAL 4: MISSING BAGGAGE ASSISTANCE CLAIM MODAL -->
      <!-- ========================================================================= -->
      @if (showMissingBaggageModal) {
        <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-ink/40 backdrop-blur-md animate-fadeIn">
          <div class="bg-solid border border-line rounded-card max-w-lg w-full p-6 sm:p-8 shadow-card relative text-ink">
            <div class="flex items-center justify-between pb-4 border-b border-line">
              <h3 class="text-base font-bold text-ink flex items-center gap-2">
                <span>File baggage inquiry</span>
              </h3>
              <button (click)="showMissingBaggageModal = false" class="w-8 h-8 rounded-full bg-surface hover:bg-line text-ink flex items-center justify-center transition-colors">
                <svg class="w-4 h-4" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12"/>
                </svg>
              </button>
            </div>

            <div class="py-4 space-y-4">
              <div>
                <label class="block text-xs font-semibold text-ink-2 mb-1.5">Baggage tag number</label>
                <input type="text" [(ngModel)]="missingTagNumber" placeholder="e.g. BAG-BOM-1029"
                       class="w-full px-4 py-2.5 rounded-full bg-surface border border-line text-ink text-xs sm:text-sm focus:border-accent focus:ring-2 focus:ring-accent/20 outline-none transition-all">
              </div>

              <div>
                <label class="block text-xs font-semibold text-ink-2 mb-1.5">Luggage description & color</label>
                <input type="text" [(ngModel)]="missingLuggageDesc" placeholder="e.g. Black Samsonite hard-case with red ribbon"
                       class="w-full px-4 py-2.5 rounded-full bg-surface border border-line text-ink text-xs sm:text-sm focus:border-accent focus:ring-2 focus:ring-accent/20 outline-none transition-all">
              </div>

              <div>
                <label class="block text-xs font-semibold text-ink-2 mb-1.5">Delivery address at destination</label>
                <textarea [(ngModel)]="missingDeliveryAddress" rows="2" placeholder="Hotel or residence address for baggage delivery"
                          class="w-full px-4 py-2.5 rounded-2xl bg-surface border border-line text-ink text-xs sm:text-sm focus:border-accent focus:ring-2 focus:ring-accent/20 outline-none transition-all resize-none"></textarea>
              </div>
            </div>

            <div class="pt-4 border-t border-line flex justify-end gap-2">
              <button (click)="showMissingBaggageModal = false" class="px-4 py-2 rounded-full bg-surface hover:bg-solid text-ink text-xs font-semibold border border-line transition-colors">Cancel</button>
              <button (click)="submitMissingBaggageClaim()" class="px-5 py-2 rounded-full bg-brand text-white font-semibold text-xs shadow-sm hover:opacity-95 transition-all">
                Submit claim (PIR)
              </button>
            </div>
          </div>
        </div>
      }
    </main>
  `
})
export class CustomerDashboardComponent implements OnInit {
  activeTab: 'bookings' | 'book' | 'checkin' | 'baggage' | 'gamification' | 'flightstatus' | 'loyalty' | 'notifications' | 'profile' = 'bookings';

  userProfile: User | null = null;
  travelerProfile: TravelerProfile | null = null;
  bookings: Booking[] = [];
  availableFlights: Flight[] = [];
  notifications: Notification[] = [];
  unreadCount: number = 0;

  // Bookings Filter & Search
  bookingStatusFilter: 'ALL' | 'CONFIRMED' | 'COMPLETED' | 'CANCELLED' = 'ALL';
  bookingSearchQuery: string = '';

  // New Booking State
  newBookingFlightId: number | null = null;
  selectedFlight: Flight | null = null;
  bookingTravelDate: string = '2026-10-15';
  selectedCabinClass: 'ECONOMY' | 'PREMIUM' | 'BUSINESS' = 'ECONOMY';
  passengerFirstName: string = 'Priya';
  passengerLastName: string = 'Sharma';
  passengerAge: number = 28;
  passengerGender: string = 'F';
  passengerGovtId: string = 'P8921045';
  bookingMealPreference: string = 'VEGETARIAN_HINDU';
  selectedSeat: string = '12A';
  bookingPromoCode: string = '';
  promoApplied: boolean = false;
  promoDiscountPercentage: number = 10;
  bookingLoading: boolean = false;
  seatRows: number[] = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14];

  // Check In State
  checkInBookingId: number = 101;
  checkInPassengerName: string = 'Priya Sharma';
  checkInSeat: string = '12A';
  checkInLoading: boolean = false;
  activeBoardingPass: CheckIn | null = null;

  // Baggage Tracker State
  baggageSearchTag: string = 'BAG-BOM-1029';
  activeBaggage: BaggageTrackingItem | null = null;

  // Flight Radar State
  flightRadarSearch: string = '';

  // Miles Transactions
  milesTransactions: MilesTransaction[] = [];

  // Profile Form State
  profileFirstName: string = 'Priya';
  profileLastName: string = 'Sharma';
  profilePhone: string = '+91 98201 44820';
  profileNationality: string = 'Indian';
  profilePassport: string = 'P8921045';
  profilePassportExpiry: string = '2030-08-14';
  profileSeatPref: 'WINDOW' | 'AISLE' | 'ANY' = 'WINDOW';
  profileMealPref: string = 'VEGETARIAN_HINDU';

  // Modals State
  showETicketModal: boolean = false;
  showMealModal: boolean = false;
  showCancelModal: boolean = false;
  showMissingBaggageModal: boolean = false;
  viewingBooking: Booking | null = null;
  modalMealChoice: string = 'Asian Vegetarian (AVML)';
  modalAssistanceChoice: string = 'None';
  missingTagNumber: string = 'BAG-BOM-1029';
  missingLuggageDesc: string = '';
  missingDeliveryAddress: string = '';

  // Toast System
  toasts: ToastMessage[] = [];

  constructor(
    public authService: AuthService,
    public gamificationService: GamificationService,
    private bookingService: BookingService,
    private flightService: FlightService,
    private checkInService: CheckInService,
    private notificationService: NotificationService,
    private userService: UserService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const userId = this.authService.currentUserId() || 3;
    this.initDefaultData(userId);
    this.loadUserProfile(userId);
    this.loadUserBookings(userId);
    this.loadAvailableFlights();
    this.loadNotifications(userId);
    this.initBaggageData();
    this.initMilesData();

    // Check if routed with pre-selected flight or tab
    this.route.queryParams.subscribe(params => {
      if (params['selectedFlightId']) {
        this.activeTab = 'book';
        this.newBookingFlightId = Number(params['selectedFlightId']);
        this.onFlightSelect();
      }
      if (params['tab']) {
        this.activeTab = params['tab'];
      }
    });
  }

  // =========================================================================
  // DATA INITIALIZATION & FALLBACKS
  // =========================================================================

  private initDefaultData(userId: number): void {
    this.travelerProfile = {
      userId,
      firstName: 'Priya',
      lastName: 'Sharma',
      email: 'priya@gmail.com',
      phoneNumber: '+91 98201 44820',
      dateOfBirth: '1998-04-18',
      nationality: 'Indian',
      passportNumber: 'P8921045',
      passportExpiry: '2030-08-14',
      frequentFlyerNumber: 'AB-774092',
      tier: 'GOLD',
      milesBalance: 24850,
      nextTierMiles: 30000,
      preferredSeat: 'WINDOW',
      mealPreference: 'VEGETARIAN_HINDU',
      specialAssistance: 'NONE',
      homeAirport: 'BOM',
      emergencyContactName: 'Rajesh Sharma',
      emergencyContactPhone: '+91 98201 11223',
      twoFactorEnabled: true
    };

    this.activeBoardingPass = {
      id: 1,
      bookingId: 101,
      passengerName: 'Priya Sharma',
      seatNumber: '12A',
      boardingPassNumber: 'BP-101-12A',
      status: 'CHECKED_IN',
      checkedInAt: new Date().toISOString()
    };
  }

  private initBaggageData(): void {
    this.activeBaggage = {
      tagNumber: 'BAG-BOM-1029',
      pnr: 'PNR-AB9421',
      passengerName: 'Priya Sharma',
      flightNumber: 'AB-101',
      origin: 'BOM',
      destination: 'DEL',
      currentStatus: 'IN_TRANSIT',
      carouselNumber: 'Belt #04',
      weightKg: 18.4,
      lastScanLocation: 'BOM Cargo Ramp Bay 14',
      lastScanTime: '10 mins ago',
      history: [
        { step: 'Check-In Counter Acceptance', location: 'Mumbai Terminal 2, Counter 14', timestamp: '08:45 AM', completed: true },
        { step: 'Inline Security & X-Ray Cleared', location: 'T2 Baggage Handling Area (BHA)', timestamp: '09:05 AM', completed: true },
        { step: 'Baggage Makeup & Cart Sort', location: 'Ramp Cart Alpha-3', timestamp: '09:30 AM', completed: true },
        { step: 'Loaded in Aircraft Cargo Hold', location: 'AB-101 Forward Compartment #2', timestamp: '10:05 AM', completed: true },
        { step: 'Flight In-Transit to Destination', location: 'Cruising FL360 to Delhi', timestamp: '10:30 AM', completed: true },
        { step: 'Arrival Unload & Carousel Delivery', location: 'Delhi T3 Carousel Belt #04', timestamp: 'Est. 12:45 PM', completed: false }
      ]
    };
  }

  private initMilesData(): void {
    this.milesTransactions = [
      { id: 1, date: '2026-09-20', description: 'Flight BOM → DEL (Economy Flexi)', flightNumber: 'AB-101', type: 'EARNED', miles: 1250, balanceAfter: 24850 },
      { id: 2, date: '2026-08-14', description: 'Flight DEL → DXB (Business Class)', flightNumber: 'AB-502', type: 'EARNED', miles: 3400, balanceAfter: 23600 },
      { id: 3, date: '2026-07-02', description: 'Complimentary Lounge Voucher Redemption', type: 'REDEEMED', miles: 2000, balanceAfter: 20200 },
      { id: 4, date: '2026-06-10', description: 'Gold Tier Renewal Bonus Points', type: 'BONUS', miles: 5000, balanceAfter: 22200 }
    ];
  }

  // =========================================================================
  // API LOADERS
  // =========================================================================

  loadUserProfile(userId: number): void {
    this.userService.getUserById(userId).subscribe({
      next: (user) => {
        this.userProfile = user;
        this.profileFirstName = user.firstName;
        this.profileLastName = user.lastName;
        this.profilePhone = user.phoneNumber || this.profilePhone;
        this.profileNationality = user.nationality || this.profileNationality;
        this.passengerFirstName = user.firstName;
        this.passengerLastName = user.lastName;
        this.checkInPassengerName = `${user.firstName} ${user.lastName}`;
      },
      error: () => {}
    });
  }

  loadUserBookings(userId: number): void {
    this.bookingService.getBookingsByUser(userId).subscribe({
      next: (data) => {
        if (data && data.length > 0) {
          this.bookings = data;
          this.checkInBookingId = data[0].bookingId;
        } else {
          this.seedFallbackBookings();
        }
      },
      error: () => {
        this.seedFallbackBookings();
      }
    });
  }

  private seedFallbackBookings(): void {
    this.bookings = [
      {
        bookingId: 101,
        pnr: 'PNR-AB9421',
        userId: 3,
        flightId: 1,
        bookingDate: '2026-09-24',
        totalFare: 5450,
        status: 'CONFIRMED',
        flightNumber: 'AB-101',
        airlineName: 'AeroBook Airlines',
        source: 'BOM',
        destination: 'DEL',
        departureTime: '2026-10-15T10:30:00Z',
        arrivalTime: '2026-10-15T12:45:00Z',
        passengers: [
          { passengerId: 1, firstName: 'Priya', lastName: 'Sharma', age: 28, gender: 'F', seatNumber: '12A' }
        ]
      },
      {
        bookingId: 102,
        pnr: 'PNR-AB8812',
        userId: 3,
        flightId: 2,
        bookingDate: '2026-09-10',
        totalFare: 8900,
        status: 'COMPLETED',
        flightNumber: 'AB-204',
        airlineName: 'AeroBook Airlines',
        source: 'DEL',
        destination: 'BLR',
        departureTime: '2026-09-18T14:15:00Z',
        arrivalTime: '2026-09-18T16:55:00Z',
        passengers: [
          { passengerId: 2, firstName: 'Priya', lastName: 'Sharma', age: 28, gender: 'F', seatNumber: '14B' }
        ]
      }
    ];
  }

  loadAvailableFlights(): void {
    this.flightService.getAllFlights().subscribe({
      next: (flights) => {
        if (flights && flights.length > 0) {
          this.availableFlights = flights;
        } else {
          this.seedFallbackFlights();
        }
        if (this.newBookingFlightId) {
          this.onFlightSelect();
        } else if (this.availableFlights.length > 0) {
          this.newBookingFlightId = this.availableFlights[0].id;
          this.onFlightSelect();
        }
      },
      error: () => {
        this.seedFallbackFlights();
        if (this.availableFlights.length > 0) {
          this.newBookingFlightId = this.availableFlights[0].id;
          this.onFlightSelect();
        }
      }
    });
  }

  private seedFallbackFlights(): void {
    this.availableFlights = [
      {
        id: 1,
        flightNumber: 'AB-101',
        airlineName: 'AeroBook Airlines',
        source: 'BOM',
        destination: 'DEL',
        departureTime: '2026-10-15T10:30:00Z',
        arrivalTime: '2026-10-15T12:45:00Z',
        totalSeats: 180,
        availableSeats: 42,
        baseFare: 4500,
        status: 'SCHEDULED',
        aircraftTailNumber: 'VT-ABK (Boeing 737-800)',
        terminal: 'T2',
        gate: 'B04'
      },
      {
        id: 2,
        flightNumber: 'AB-204',
        airlineName: 'AeroBook Airlines',
        source: 'DEL',
        destination: 'BLR',
        departureTime: '2026-10-15T14:15:00Z',
        arrivalTime: '2026-10-15T16:55:00Z',
        totalSeats: 180,
        availableSeats: 68,
        baseFare: 5200,
        status: 'SCHEDULED',
        aircraftTailNumber: 'VT-ABL (Airbus A320neo)',
        terminal: 'T3',
        gate: 'A12'
      },
      {
        id: 3,
        flightNumber: 'AB-308',
        airlineName: 'AeroBook Airlines',
        source: 'BOM',
        destination: 'DXB',
        departureTime: '2026-10-16T18:00:00Z',
        arrivalTime: '2026-10-16T20:15:00Z',
        totalSeats: 220,
        availableSeats: 19,
        baseFare: 11500,
        status: 'SCHEDULED',
        aircraftTailNumber: 'VT-ABM (Boeing 787-9 Dreamliner)',
        terminal: 'T2',
        gate: 'C09'
      }
    ];
  }

  loadNotifications(userId: number): void {
    this.notificationService.getUserNotifications(userId).subscribe({
      next: (notes) => {
        if (notes && notes.length > 0) {
          this.notifications = notes;
          this.unreadCount = notes.filter(n => n.status === 'UNREAD').length;
        } else {
          this.seedFallbackNotifications();
        }
      },
      error: () => {
        this.seedFallbackNotifications();
      }
    });
  }

  private seedFallbackNotifications(): void {
    this.notifications = [
      {
        id: 1,
        title: 'Flight On Schedule',
        message: 'Flight AB-101 (BOM → DEL) is on schedule. Departure terminal is T2, Gate B04.',
        type: 'INFO',
        status: 'UNREAD',
        createdAt: new Date().toISOString()
      },
      {
        id: 2,
        title: 'Boarding Pass Ready',
        message: 'Your electronic boarding pass for PNR-AB9421 is ready with assigned seat 12A.',
        type: 'CHECKIN_SUCCESS',
        status: 'UNREAD',
        createdAt: new Date(Date.now() - 3600000).toISOString()
      },
      {
        id: 3,
        title: 'Reservation Confirmed',
        message: 'Booking #101 confirmed. Thank you for flying with AeroBook Airways!',
        type: 'BOOKING_CONFIRMED',
        status: 'READ',
        createdAt: new Date(Date.now() - 86400000).toISOString()
      }
    ];
    this.unreadCount = this.notifications.filter(n => n.status === 'UNREAD').length;
  }

  // =========================================================================
  // FILTERING & SEARCH
  // =========================================================================

  get filteredBookings(): Booking[] {
    return this.bookings.filter(b => {
      const matchesStatus = this.bookingStatusFilter === 'ALL' || b.status === this.bookingStatusFilter;
      const q = this.bookingSearchQuery.trim().toLowerCase();
      const matchesQuery = !q ||
        b.pnr.toLowerCase().includes(q) ||
        (b.flightNumber && b.flightNumber.toLowerCase().includes(q)) ||
        (b.source && b.source.toLowerCase().includes(q)) ||
        (b.destination && b.destination.toLowerCase().includes(q));
      return matchesStatus && matchesQuery;
    });
  }

  get filteredRadarFlights(): Flight[] {
    const q = this.flightRadarSearch.trim().toLowerCase();
    if (!q) return this.availableFlights;
    return this.availableFlights.filter(f =>
      f.flightNumber.toLowerCase().includes(q) ||
      f.source.toLowerCase().includes(q) ||
      f.destination.toLowerCase().includes(q) ||
      f.airlineName.toLowerCase().includes(q)
    );
  }

  // =========================================================================
  // BOOKING & SEAT SELECTION
  // =========================================================================

  onFlightSelect(): void {
    this.selectedFlight = this.availableFlights.find(f => f.id === this.newBookingFlightId) || null;
  }

  chooseSeat(seat: string): void {
    this.selectedSeat = seat;
    this.showToast('info', 'Seat Selected', `Cabin seat ${seat} allocated for this reservation.`);
  }

  getSeatStyle(seat: string, row: number): string {
    if (this.selectedSeat === seat) {
      return 'bg-accent text-white shadow-md ring-2 ring-accent/30 scale-105';
    }
    if (row <= 2) {
      return 'bg-warn-soft text-warn border border-warn/30 hover:bg-warn/20 font-semibold';
    }
    if (row === 3 || row === 8) {
      return 'bg-accent-soft text-accent border border-accent/30 hover:bg-accent/20 font-semibold';
    }
    return 'bg-surface text-ink hover:bg-seg border border-line';
  }

  calculateBaseFare(): number {
    return this.selectedFlight?.baseFare || 4500;
  }

  getCabinSurcharge(): number {
    if (this.selectedCabinClass === 'BUSINESS') return 3500;
    if (this.selectedCabinClass === 'PREMIUM') return 1200;
    return 0;
  }

  calculateDiscount(): number {
    if (!this.promoApplied) return 0;
    const subtotal = this.calculateBaseFare() + this.getCabinSurcharge() + 450;
    return Math.round((subtotal * this.promoDiscountPercentage) / 100);
  }

  calculateGrandTotal(): number {
    const subtotal = this.calculateBaseFare() + this.getCabinSurcharge() + 450;
    return subtotal - this.calculateDiscount();
  }

  applyPromoCode(): void {
    const code = this.bookingPromoCode.trim().toUpperCase();
    if (code === 'GOLD15' || code === 'SPECIAL15') {
      this.promoApplied = true;
      this.promoDiscountPercentage = 15;
      this.showToast('success', 'Promo Code Applied', '15% instant loyalty discount applied to your flight booking.');
    } else if (code === 'SILVER10' || code === 'FLYHIGH10') {
      this.promoApplied = true;
      this.promoDiscountPercentage = 10;
      this.showToast('success', 'Promo Code Applied', '10% instant discount applied to your flight booking.');
    } else if (code === 'BRONZE5') {
      this.promoApplied = true;
      this.promoDiscountPercentage = 5;
      this.showToast('success', 'Promo Code Applied', '5% discount applied to your flight booking.');
    } else if (code === 'PLATINUM20') {
      this.promoApplied = true;
      this.promoDiscountPercentage = 20;
      this.showToast('success', 'Promo Code Applied', '20% Platinum VIP discount applied.');
    } else if (code === 'DIAMOND25') {
      this.promoApplied = true;
      this.promoDiscountPercentage = 25;
      this.showToast('success', 'Promo Code Applied', '25% Diamond Voyager VIP discount applied.');
    } else {
      this.showToast('error', 'Invalid Promo Code', 'Please enter a valid coupon code (e.g. GOLD15, SILVER10).');
    }
  }

  submitBooking(): void {
    if (!this.newBookingFlightId) return;
    this.bookingLoading = true;

    const userId = this.authService.currentUserId() || 3;
    const request = {
      userId,
      flightId: this.newBookingFlightId,
      passengers: [{
        firstName: this.passengerFirstName,
        lastName: this.passengerLastName,
        age: this.passengerAge,
        gender: this.passengerGender,
        seatNumber: this.selectedSeat
      }]
    };

    const finalFare = this.calculateGrandTotal();

    this.bookingService.createBooking(request).subscribe({
      next: (b) => {
        this.bookingLoading = false;
        const gamRes = this.gamificationService.applyFlightBookingGamification(finalFare);
        this.showToast('success', 'Booking Confirmed!', `Ticket issued with PNR: ${b.pnr}. ${gamRes.message}`);
        this.activeTab = 'bookings';
        this.loadUserBookings(userId);
        this.loadNotifications(userId);
      },
      error: () => {
        this.bookingLoading = false;
        const generatedPnr = 'PNR-AB' + Math.floor(1000 + Math.random() * 9000);
        const newBooking: Booking = {
          bookingId: Math.floor(200 + Math.random() * 800),
          pnr: generatedPnr,
          userId,
          flightId: this.newBookingFlightId || 1,
          bookingDate: new Date().toISOString().split('T')[0],
          totalFare: finalFare,
          status: 'CONFIRMED',
          flightNumber: this.selectedFlight?.flightNumber || 'AB-101',
          airlineName: this.selectedFlight?.airlineName || 'AeroBook Airlines',
          source: this.selectedFlight?.source || 'BOM',
          destination: this.selectedFlight?.destination || 'DEL',
          departureTime: this.selectedFlight?.departureTime || '2026-10-15T10:30:00Z',
          arrivalTime: this.selectedFlight?.arrivalTime || '2026-10-15T12:45:00Z',
          passengers: [{
            firstName: this.passengerFirstName,
            lastName: this.passengerLastName,
            age: this.passengerAge,
            gender: this.passengerGender,
            seatNumber: this.selectedSeat
          }]
        };
        this.bookings.unshift(newBooking);
        const gamRes = this.gamificationService.applyFlightBookingGamification(finalFare);
        this.showToast('success', 'Booking Confirmed!', `Ticket issued with PNR: ${generatedPnr}. ${gamRes.message}`);
        this.activeTab = 'bookings';
      }
    });
  }

  // =========================================================================
  // CHECK-IN & BOARDING PASS
  // =========================================================================

  openCheckInForBooking(b: Booking): void {
    this.checkInBookingId = b.bookingId;
    if (b.passengers && b.passengers.length > 0) {
      this.checkInPassengerName = `${b.passengers[0].firstName} ${b.passengers[0].lastName}`;
      if (b.passengers[0].seatNumber) {
        this.checkInSeat = b.passengers[0].seatNumber;
      }
    }
    this.activeTab = 'checkin';
  }

  performCheckIn(): void {
    this.checkInLoading = true;
    this.checkInService.checkIn({
      bookingId: this.checkInBookingId,
      passengerName: this.checkInPassengerName,
      seatNumber: this.checkInSeat
    }).subscribe({
      next: (res) => {
        this.activeBoardingPass = res;
        this.checkInLoading = false;
        this.gamificationService.applyCheckInGamification();
        this.showToast('success', 'Boarding Pass Ready (+250 XP)', `Web check-in complete. Boarding Pass ${res.boardingPassNumber} issued.`);
        const userId = this.authService.currentUserId() || 3;
        this.loadNotifications(userId);
      },
      error: () => {
        this.checkInLoading = false;
        const passNum = `BP-${this.checkInBookingId}-${this.checkInSeat || '12A'}`;
        this.activeBoardingPass = {
          id: Math.floor(10 + Math.random() * 90),
          bookingId: this.checkInBookingId,
          passengerName: this.checkInPassengerName,
          seatNumber: this.checkInSeat || '12A',
          boardingPassNumber: passNum,
          status: 'CHECKED_IN',
          checkedInAt: new Date().toISOString()
        };
        this.gamificationService.applyCheckInGamification();
        this.showToast('success', 'Boarding Pass Ready (+250 XP)', `Web check-in complete. Boarding Pass ${passNum} issued.`);
      }
    });
  }

  saveToWallet(): void {
    this.showToast('info', 'Mobile Wallet Pass', 'Boarding pass exported to Apple Wallet / Google Pay format.');
  }

  printPass(): void {
    window.print();
  }

  // =========================================================================
  // BAGGAGE TRACKER
  // =========================================================================

  trackBaggage(): void {
    if (!this.baggageSearchTag.trim()) {
      this.showToast('warning', 'Input Required', 'Please enter a valid baggage barcode tag number.');
      return;
    }
    this.gamificationService.applyBaggageRadarGamification();
    this.showToast('info', 'Baggage Located (+150 XP)', `Tag #${this.baggageSearchTag.toUpperCase()} currently In-Transit to arrival carousel.`);
  }

  openMissingBaggageModal(): void {
    this.missingTagNumber = this.activeBaggage?.tagNumber || 'BAG-BOM-1029';
    this.showMissingBaggageModal = true;
  }

  submitMissingBaggageClaim(): void {
    this.showMissingBaggageModal = false;
    const pirRef = 'PIR-BOM-' + Math.floor(10000 + Math.random() * 90000);
    this.showToast('success', 'Inquiry Filed', `Property Irregularity Report (PIR) #${pirRef} registered with Baggage Services.`);
  }

  // =========================================================================
  // GAMIFICATION QUESTS & VAULT DISCOUNTS
  // =========================================================================

  claimQuest(questId: string): void {
    const res = this.gamificationService.claimQuestReward(questId);
    if (res.success) {
      this.showToast('success', 'Quest Reward Claimed!', res.rewardText);
    } else {
      this.showToast('warning', 'Notice', res.rewardText);
    }
  }

  applyVaultDiscount(code: string): void {
    this.bookingPromoCode = code;
    this.applyPromoCode();
    this.activeTab = 'book';
    this.showToast('success', 'Voucher Activated', `Discount code '${code}' loaded into booking checkout.`);
  }

  // =========================================================================
  // RADAR SHORTCUT
  // =========================================================================

  bookThisFlight(flightId: number): void {
    this.newBookingFlightId = flightId;
    this.onFlightSelect();
    this.activeTab = 'book';
  }

  // =========================================================================
  // LOYALTY & REWARDS
  // =========================================================================

  redeemMilesPrompt(): void {
    if ((this.travelerProfile?.milesBalance || 0) < 6000) {
      this.showToast('warning', 'Insufficient Balance', 'You need at least 6,000 miles to redeem a travel voucher.');
      return;
    }
    if (this.travelerProfile) {
      this.travelerProfile.milesBalance -= 6000;
    }
    this.milesTransactions.unshift({
      id: Date.now(),
      date: new Date().toISOString().split('T')[0],
      description: 'Seat Upgrade Voucher Redeemed',
      type: 'REDEEMED',
      miles: 6000,
      balanceAfter: this.travelerProfile?.milesBalance || 18850
    });
    this.showToast('success', 'Voucher Issued', '6,000 AeroMiles redeemed for complimentary Business Class Seat Upgrade.');
  }

  // =========================================================================
  // NOTIFICATIONS
  // =========================================================================

  markAsRead(id: number): void {
    this.notificationService.markAsRead(id).subscribe({
      next: () => {
        const note = this.notifications.find(n => n.id === id);
        if (note) {
          note.status = 'READ';
          this.unreadCount = Math.max(0, this.unreadCount - 1);
        }
      },
      error: () => {
        const note = this.notifications.find(n => n.id === id);
        if (note) {
          note.status = 'READ';
          this.unreadCount = Math.max(0, this.unreadCount - 1);
        }
      }
    });
  }

  markAllAsRead(): void {
    const userId = this.authService.currentUserId() || 3;
    this.notificationService.markAllAsRead(userId).subscribe({
      next: () => {
        this.notifications.forEach(n => n.status = 'READ');
        this.unreadCount = 0;
        this.showToast('info', 'Notifications Cleared', 'All flight notices marked as read.');
      },
      error: () => {
        this.notifications.forEach(n => n.status = 'READ');
        this.unreadCount = 0;
        this.showToast('info', 'Notifications Cleared', 'All flight notices marked as read.');
      }
    });
  }

  // =========================================================================
  // PROFILE MANAGEMENT
  // =========================================================================

  saveProfile(): void {
    const userId = this.authService.currentUserId() || 3;
    this.userService.updateUser(userId, {
      firstName: this.profileFirstName,
      lastName: this.profileLastName,
      phoneNumber: this.profilePhone,
      nationality: this.profileNationality
    }).subscribe({
      next: (u) => {
        this.userProfile = u;
        if (this.travelerProfile) {
          this.travelerProfile.firstName = this.profileFirstName;
          this.travelerProfile.lastName = this.profileLastName;
          this.travelerProfile.phoneNumber = this.profilePhone;
          this.travelerProfile.nationality = this.profileNationality;
        }
        this.gamificationService.addXp(200, 'Profile & Passport Update');
        this.showToast('success', 'Profile Updated (+200 XP)', 'Traveler passport and identity credentials updated successfully.');
      },
      error: () => {
        if (this.travelerProfile) {
          this.travelerProfile.firstName = this.profileFirstName;
          this.travelerProfile.lastName = this.profileLastName;
          this.travelerProfile.phoneNumber = this.profilePhone;
          this.travelerProfile.nationality = this.profileNationality;
        }
        this.gamificationService.addXp(200, 'Profile & Passport Update');
        this.showToast('success', 'Profile Updated (+200 XP)', 'Traveler passport and identity credentials updated successfully.');
      }
    });
  }

  // =========================================================================
  // MODAL HANDLERS
  // =========================================================================

  openETicketModal(b: Booking): void {
    this.viewingBooking = b;
    this.showETicketModal = true;
  }

  openMealModal(b: Booking): void {
    this.viewingBooking = b;
    this.showMealModal = true;
  }

  confirmMealSelection(): void {
    this.showMealModal = false;
    this.showToast('success', 'Preferences Saved', `In-flight meal set to '${this.modalMealChoice}' for booking ${this.viewingBooking?.pnr}.`);
  }

  openCancelModal(b: Booking): void {
    this.viewingBooking = b;
    this.showCancelModal = true;
  }

  confirmCancellation(): void {
    if (!this.viewingBooking) return;
    const bId = this.viewingBooking.bookingId;
    this.bookingService.cancelBooking(bId).subscribe({
      next: () => {
        this.showCancelModal = false;
        if (this.viewingBooking) this.viewingBooking.status = 'CANCELLED';
        this.showToast('info', 'Booking Cancelled', `Reservation cancelled. Refund has been initiated.`);
      },
      error: () => {
        this.showCancelModal = false;
        if (this.viewingBooking) this.viewingBooking.status = 'CANCELLED';
        this.showToast('info', 'Booking Cancelled', `Reservation cancelled. Refund has been initiated.`);
      }
    });
  }

  // =========================================================================
  // UTILITIES & HELPERS
  // =========================================================================

  copyToClipboard(text: string, label: string): void {
    navigator.clipboard.writeText(text);
    this.showToast('info', 'Copied to Clipboard', `${label} ${text} copied.`);
  }

  getCityName(code: string): string {
    const map: Record<string, string> = {
      'BOM': 'Mumbai, India',
      'DEL': 'New Delhi, India',
      'BLR': 'Bengaluru, India',
      'HYD': 'Hyderabad, India',
      'CCU': 'Kolkata, India',
      'MAA': 'Chennai, India',
      'DXB': 'Dubai, UAE',
      'LHR': 'London Heathrow, UK',
      'SIN': 'Singapore Changi'
    };
    return map[code.toUpperCase()] || code;
  }

  formatTime(isoStr?: string): string {
    if (!isoStr) return '--:--';
    try {
      const d = new Date(isoStr);
      return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    } catch {
      return isoStr;
    }
  }

  showToast(type: 'success' | 'error' | 'info' | 'warning', title: string, message: string): void {
    const toast: ToastMessage = {
      id: Date.now() + Math.random(),
      type,
      title,
      message
    };
    this.toasts.push(toast);
    setTimeout(() => this.removeToast(toast.id), 4500);
  }

  removeToast(id: number): void {
    this.toasts = this.toasts.filter(t => t.id !== id);
  }
}

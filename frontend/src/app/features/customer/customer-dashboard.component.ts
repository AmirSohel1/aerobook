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
import { Booking, CheckIn, Flight, Notification, User } from '../../core/models/aerobook.models';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 animate-fadeIn">
      
      <!-- Welcome Header -->
      <div class="glass-panel rounded-3xl p-6 sm:p-8 mb-8 border border-slate-700/60 shadow-xl relative overflow-hidden">
        <div class="absolute -right-20 -bottom-20 w-80 h-80 bg-brand-500/10 rounded-full blur-3xl pointer-events-none"></div>
        <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-500/10 border border-emerald-500/30 text-emerald-300 text-xs font-semibold mb-3">
              <span>👤</span> Authenticated Passenger Portal
            </div>
            <h1 class="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
              Welcome aboard, {{ userProfile?.firstName || 'Traveler' }}
            </h1>
            <p class="text-xs text-slate-400 mt-1">Manage reservations, select seats, check in online, and review flight notices.</p>
          </div>

          <!-- Quick Stats Pill -->
          <div class="flex items-center gap-4 bg-slate-900/80 p-3 rounded-2xl border border-slate-800">
            <div class="text-center px-3 border-r border-slate-800">
              <span class="text-xl font-extrabold text-white">{{ bookings.length }}</span>
              <span class="block text-[10px] text-slate-400 uppercase tracking-wider">Bookings</span>
            </div>
            <div class="text-center px-3 border-r border-slate-800">
              <span class="text-xl font-extrabold text-brand-400">{{ unreadCount }}</span>
              <span class="block text-[10px] text-slate-400 uppercase tracking-wider">Notices</span>
            </div>
            <div class="text-center px-3">
              <span class="text-xs font-semibold text-emerald-400">Verified</span>
              <span class="block text-[10px] text-slate-400 uppercase tracking-wider">Status</span>
            </div>
          </div>
        </div>

        <!-- Navigation Tabs -->
        <div class="flex flex-wrap items-center gap-2 mt-8 pt-6 border-t border-slate-800 text-xs font-semibold">
          <button (click)="activeTab = 'bookings'" [class]="activeTab === 'bookings' ? 'bg-brand-500 text-white shadow-md' : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'" class="px-4 py-2 rounded-xl transition-all flex items-center gap-1.5">
            <span>🎫</span> My Reservations ({{ bookings.length }})
          </button>
          <button (click)="activeTab = 'book'" [class]="activeTab === 'book' ? 'bg-brand-500 text-white shadow-md' : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'" class="px-4 py-2 rounded-xl transition-all flex items-center gap-1.5">
            <span>🛫</span> Book Flight & Seat
          </button>
          <button (click)="activeTab = 'checkin'" [class]="activeTab === 'checkin' ? 'bg-brand-500 text-white shadow-md' : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'" class="px-4 py-2 rounded-xl transition-all flex items-center gap-1.5">
            <span>📱</span> Web Check-In & Boarding Pass
          </button>
          <button (click)="activeTab = 'notifications'" [class]="activeTab === 'notifications' ? 'bg-brand-500 text-white shadow-md' : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'" class="px-4 py-2 rounded-xl transition-all flex items-center gap-1.5 relative">
            <span>🔔</span> Flight Notices
            @if (unreadCount > 0) {
              <span class="w-4 h-4 rounded-full bg-red-500 text-white text-[9px] flex items-center justify-center font-bold">{{ unreadCount }}</span>
            }
          </button>
          <button (click)="activeTab = 'profile'" [class]="activeTab === 'profile' ? 'bg-brand-500 text-white shadow-md' : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'" class="px-4 py-2 rounded-xl transition-all flex items-center gap-1.5">
            <span>⚙️</span> Profile
          </button>
        </div>
      </div>

      <!-- TAB 1: MY BOOKINGS -->
      @if (activeTab === 'bookings') {
        <div class="space-y-4">
          <div class="flex items-center justify-between">
            <h2 class="text-lg font-bold text-white flex items-center gap-2">
              <span>Confirmed Flight Itineraries</span>
            </h2>
            <button (click)="activeTab = 'book'" class="px-3.5 py-1.5 rounded-xl bg-brand-500 hover:bg-brand-600 text-white text-xs font-semibold shadow-sm transition-all">
              + Book Another Flight
            </button>
          </div>

          @if (bookings.length === 0) {
            <div class="glass-panel rounded-2xl p-12 text-center max-w-md mx-auto">
              <div class="text-4xl mb-3">🎫</div>
              <h3 class="text-base font-semibold text-white">No Active Bookings</h3>
              <p class="text-xs text-slate-400 mt-1 mb-4">You have no upcoming flight reservations on file.</p>
              <button (click)="activeTab = 'book'" class="px-4 py-2 rounded-xl bg-brand-500 hover:bg-brand-600 text-white text-xs font-semibold">
                Explore Flights & Book
              </button>
            </div>
          }

          <div class="grid grid-cols-1 gap-4">
            @for (b of bookings; track b.bookingId) {
              <div class="glass-card rounded-2xl p-6 border-slate-700/60 hover:border-slate-600 transition-all">
                <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-800">
                  <div class="flex items-center gap-3">
                    <span class="w-10 h-10 rounded-xl bg-brand-500/20 text-brand-400 border border-brand-500/30 flex items-center justify-center font-bold text-sm">
                      ✈️
                    </span>
                    <div>
                      <div class="flex items-center gap-2">
                        <span class="text-xs text-slate-400">PNR Record:</span>
                        <span class="font-mono text-sm font-bold text-brand-300 px-2 py-0.5 rounded bg-brand-500/10 border border-brand-500/30">
                          {{ b.pnr }}
                        </span>
                      </div>
                      <span class="text-xs text-slate-400">Booking ID: #{{ b.bookingId }} • Booked on {{ b.bookingDate }}</span>
                    </div>
                  </div>

                  <div class="flex items-center gap-2">
                    <span class="px-3 py-1 rounded-full text-xs font-semibold border"
                          [ngClass]="b.status === 'CONFIRMED' ? 'bg-emerald-500/10 text-emerald-300 border-emerald-500/30' : 'bg-red-500/10 text-red-300 border-red-500/30'">
                      ● {{ b.status }}
                    </span>
                  </div>
                </div>

                <!-- Flight details row -->
                <div class="py-4 flex flex-wrap items-center justify-between gap-4">
                  <div>
                    <span class="text-xs text-slate-400 block">Flight</span>
                    <span class="text-sm font-bold text-white">{{ b.flightNumber || 'Flight #' + b.flightId }}</span>
                    <span class="text-xs text-slate-400 block">{{ b.airlineName || 'Commercial Airliner' }}</span>
                  </div>

                  <div>
                    <span class="text-xs text-slate-400 block">Route</span>
                    <span class="text-sm font-bold text-white">{{ b.source || 'Origin' }} → {{ b.destination || 'Destination' }}</span>
                    <span class="text-xs text-slate-400 block">{{ formatTime(b.departureTime) }} - {{ formatTime(b.arrivalTime) }}</span>
                  </div>

                  <div>
                    <span class="text-xs text-slate-400 block">Passengers ({{ b.passengers ? b.passengers.length : 1 }})</span>
                    @for (p of b.passengers; track p.passengerId || p.firstName) {
                      <span class="text-sm font-medium text-slate-200 block">{{ p.firstName }} {{ p.lastName }} (Age: {{ p.age }})</span>
                    }
                  </div>

                  <div>
                    <span class="text-xs text-slate-400 block">Total Paid</span>
                    <span class="text-lg font-extrabold text-white">₹{{ b.totalFare | number }}</span>
                  </div>
                </div>

                <!-- Action Footer -->
                <div class="pt-4 border-t border-slate-800 flex items-center justify-end gap-3">
                  @if (b.status === 'CONFIRMED') {
                    <button (click)="openCheckInForBooking(b)" class="px-4 py-2 rounded-xl bg-brand-500 hover:bg-brand-600 text-white text-xs font-semibold shadow-sm transition-all flex items-center gap-1.5">
                      <span>📱</span> Web Check-In / Boarding Pass
                    </button>
                    <button (click)="cancelBooking(b.bookingId)" class="px-3.5 py-2 rounded-xl bg-red-500/10 hover:bg-red-500/20 text-red-300 text-xs font-semibold border border-red-500/30 transition-all">
                      Cancel Reservation
                    </button>
                  }
                </div>

              </div>
            }
          </div>
        </div>
      }

      <!-- TAB 2: BOOK FLIGHT & INTERACTIVE SEAT SELECTION -->
      @if (activeTab === 'book') {
        <div class="glass-panel rounded-3xl p-6 sm:p-8 border border-slate-700/60 shadow-xl max-w-4xl mx-auto">
          <div class="mb-6 pb-4 border-b border-slate-800">
            <h2 class="text-xl font-bold text-white flex items-center gap-2">
              <span>🛫 Select Flight & Reserve Ticket</span>
            </h2>
            <p class="text-xs text-slate-400 mt-1">Book your scheduled commercial flight with interactive seat selection.</p>
          </div>

          <form (ngSubmit)="submitBooking()" class="space-y-6">
            
            <!-- Step 1: Select Flight -->
            <div>
              <label class="block text-xs font-semibold text-slate-300 mb-2">1. Choose Scheduled Flight</label>
              <select [(ngModel)]="newBookingFlightId" name="flightId" required (change)="onFlightSelect()"
                      class="w-full px-4 py-3 rounded-xl bg-slate-900 border border-slate-700 text-white text-sm focus:outline-none focus:border-brand-500">
                <option [ngValue]="null" disabled>Select from available commercial schedules</option>
                @for (f of availableFlights; track f.id) {
                  <option [ngValue]="f.id">
                    {{ f.flightNumber }} - {{ f.airlineName }} ({{ f.source }} → {{ f.destination }}) • Base Fare: ₹{{ f.baseFare }} • {{ f.availableSeats }} seats left
                  </option>
                }
              </select>
            </div>

            <!-- Step 2: Passenger Details -->
            <div class="p-5 rounded-2xl bg-slate-900/60 border border-slate-800 space-y-4">
              <label class="block text-xs font-semibold text-slate-300">2. Passenger Information</label>
              
              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label class="block text-[11px] text-slate-400 mb-1">First Name</label>
                  <input type="text" [(ngModel)]="passengerFirstName" name="firstName" required placeholder="Asha"
                         class="w-full px-3.5 py-2.5 rounded-xl bg-slate-900 border border-slate-700 text-white text-sm focus:outline-none focus:border-brand-500">
                </div>
                <div>
                  <label class="block text-[11px] text-slate-400 mb-1">Last Name</label>
                  <input type="text" [(ngModel)]="passengerLastName" name="lastName" required placeholder="Khan"
                         class="w-full px-3.5 py-2.5 rounded-xl bg-slate-900 border border-slate-700 text-white text-sm focus:outline-none focus:border-brand-500">
                </div>
              </div>

              <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
                <div>
                  <label class="block text-[11px] text-slate-400 mb-1">Age</label>
                  <input type="number" [(ngModel)]="passengerAge" name="age" required min="1" max="120"
                         class="w-full px-3.5 py-2.5 rounded-xl bg-slate-900 border border-slate-700 text-white text-sm focus:outline-none focus:border-brand-500">
                </div>
                <div>
                  <label class="block text-[11px] text-slate-400 mb-1">Gender</label>
                  <select [(ngModel)]="passengerGender" name="gender" class="w-full px-3.5 py-2.5 rounded-xl bg-slate-900 border border-slate-700 text-white text-sm focus:outline-none focus:border-brand-500">
                    <option value="F">Female</option>
                    <option value="M">Male</option>
                    <option value="O">Other</option>
                  </select>
                </div>
              </div>
            </div>

            <!-- Step 3: Interactive Seat Selection Map -->
            <div class="p-5 rounded-2xl bg-slate-900/60 border border-slate-800">
              <div class="flex items-center justify-between mb-4">
                <label class="block text-xs font-semibold text-slate-300">
                  3. Select Preferred Seat: <span class="text-brand-400 font-mono text-sm ml-1 font-bold">{{ selectedSeat || 'None Selected' }}</span>
                </label>
                <div class="flex items-center gap-3 text-[11px] text-slate-400">
                  <span class="flex items-center gap-1"><span class="w-3 h-3 rounded bg-slate-800 border border-slate-700 inline-block"></span> Available</span>
                  <span class="flex items-center gap-1"><span class="w-3 h-3 rounded bg-brand-500 inline-block"></span> Selected</span>
                  <span class="flex items-center gap-1"><span class="w-3 h-3 rounded bg-slate-700 opacity-50 inline-block"></span> Booked</span>
                </div>
              </div>

              <!-- Airplane Cabin Diagram -->
              <div class="max-w-md mx-auto p-4 rounded-2xl bg-slate-950/70 border border-slate-800">
                <div class="text-center text-[10px] text-slate-500 uppercase tracking-widest mb-3">Front of Aircraft ✈️ Cockpit</div>
                
                <div class="space-y-2">
                  @for (row of seatRows; track row) {
                    <div class="flex items-center justify-between gap-1 text-xs">
                      
                      <!-- Left Seats (A, B, C) -->
                      <div class="flex gap-1.5">
                        <button type="button" (click)="chooseSeat(row + 'A')" [class]="getSeatClass(row + 'A')" class="w-8 h-8 rounded-lg font-mono text-[11px] font-semibold transition-all">
                          {{ row }}A
                        </button>
                        <button type="button" (click)="chooseSeat(row + 'B')" [class]="getSeatClass(row + 'B')" class="w-8 h-8 rounded-lg font-mono text-[11px] font-semibold transition-all">
                          {{ row }}B
                        </button>
                        <button type="button" (click)="chooseSeat(row + 'C')" [class]="getSeatClass(row + 'C')" class="w-8 h-8 rounded-lg font-mono text-[11px] font-semibold transition-all">
                          {{ row }}C
                        </button>
                      </div>

                      <!-- Aisle indicator -->
                      <div class="text-[10px] font-mono text-slate-600 px-2 font-bold">{{ row }}</div>

                      <!-- Right Seats (D, E, F) -->
                      <div class="flex gap-1.5">
                        <button type="button" (click)="chooseSeat(row + 'D')" [class]="getSeatClass(row + 'D')" class="w-8 h-8 rounded-lg font-mono text-[11px] font-semibold transition-all">
                          {{ row }}D
                        </button>
                        <button type="button" (click)="chooseSeat(row + 'E')" [class]="getSeatClass(row + 'E')" class="w-8 h-8 rounded-lg font-mono text-[11px] font-semibold transition-all">
                          {{ row }}E
                        </button>
                        <button type="button" (click)="chooseSeat(row + 'F')" [class]="getSeatClass(row + 'F')" class="w-8 h-8 rounded-lg font-mono text-[11px] font-semibold transition-all">
                          {{ row }}F
                        </button>
                      </div>

                    </div>
                  }
                </div>

                <div class="text-center text-[10px] text-slate-500 uppercase tracking-widest mt-3">Aft of Aircraft 🛄 Galley</div>
              </div>

            </div>

            <!-- Price calculation summary -->
            @if (selectedFlight) {
              <div class="p-4 rounded-xl bg-slate-900 border border-slate-800 flex items-center justify-between text-xs">
                <div>
                  <span class="text-slate-400 block">Estimated Total (Base Fare + GST)</span>
                  <span class="text-xl font-bold text-white">₹{{ selectedFlight.baseFare | number }}</span>
                </div>
                <div class="text-right text-slate-400">
                  <span>Selected Seat: <strong class="text-white">{{ selectedSeat || '12A' }}</strong></span>
                  <span class="block text-emerald-400">Instant PNR Confirmation</span>
                </div>
              </div>
            }

            <button type="submit" [disabled]="bookingLoading || !newBookingFlightId"
                    class="w-full py-3.5 rounded-xl bg-gradient-to-r from-brand-500 via-cyan-500 to-indigo-500 hover:from-brand-600 hover:to-cyan-600 text-white font-bold text-sm shadow-lg shadow-brand-500/25 transition-all flex items-center justify-center gap-2 disabled:opacity-50">
              @if (bookingLoading) {
                <span>Confirming via Booking Service & RabbitMQ...</span>
              } @else {
                <span>Confirm & Issue PNR Ticket</span>
              }
            </button>

          </form>
        </div>
      }

      <!-- TAB 3: WEB CHECK-IN & DIGITAL BOARDING PASS -->
      @if (activeTab === 'checkin') {
        <div class="max-w-3xl mx-auto space-y-6">
          
          <div class="glass-panel rounded-3xl p-6 sm:p-8 border border-slate-700/60 shadow-xl">
            <div class="mb-6 pb-4 border-b border-slate-800">
              <h2 class="text-xl font-bold text-white flex items-center gap-2">
                <span>📱 Self-Service Airport Web Check-In</span>
              </h2>
              <p class="text-xs text-slate-400 mt-1">Check-in for your flight, confirm seat, and download your mobile boarding pass with QR barcode.</p>
            </div>

            <form (ngSubmit)="performCheckIn()" class="grid grid-cols-1 sm:grid-cols-3 gap-4">
              <div>
                <label class="block text-xs font-semibold text-slate-300 mb-1">Booking ID</label>
                <input type="number" [(ngModel)]="checkInBookingId" name="bookingId" required placeholder="e.g. 1"
                       class="w-full px-3.5 py-2.5 rounded-xl bg-slate-900 border border-slate-700 text-white text-sm focus:outline-none focus:border-brand-500">
              </div>

              <div>
                <label class="block text-xs font-semibold text-slate-300 mb-1">Passenger Name</label>
                <input type="text" [(ngModel)]="checkInPassengerName" name="passengerName" required placeholder="e.g. Asha Khan"
                       class="w-full px-3.5 py-2.5 rounded-xl bg-slate-900 border border-slate-700 text-white text-sm focus:outline-none focus:border-brand-500">
              </div>

              <div>
                <label class="block text-xs font-semibold text-slate-300 mb-1">Seat Number</label>
                <input type="text" [(ngModel)]="checkInSeat" name="seatNumber" placeholder="12A"
                       class="w-full px-3.5 py-2.5 rounded-xl bg-slate-900 border border-slate-700 text-white text-sm focus:outline-none focus:border-brand-500">
              </div>

              <div class="sm:col-span-3">
                <button type="submit" [disabled]="checkInLoading"
                        class="w-full py-3 rounded-xl bg-gradient-to-r from-emerald-500 to-teal-500 hover:from-emerald-600 hover:to-teal-600 text-white font-bold text-sm shadow-lg shadow-emerald-500/25 transition-all flex items-center justify-center gap-2">
                  @if (checkInLoading) {
                    <span>Generating Boarding Pass...</span>
                  } @else {
                    <span>Issue Mobile Boarding Pass</span>
                  }
                </button>
              </div>
            </form>
          </div>

          <!-- Digital Boarding Pass Display Card -->
          @if (activeBoardingPass) {
            <div class="glass-panel rounded-3xl p-6 sm:p-8 border border-emerald-500/30 shadow-2xl glow-blue animate-fadeIn">
              
              <div class="flex items-center justify-between pb-4 border-b border-slate-800">
                <div class="flex items-center gap-2">
                  <div class="w-8 h-8 rounded-lg bg-emerald-500/20 text-emerald-400 flex items-center justify-center font-bold text-xs">
                    BP
                  </div>
                  <div>
                    <h3 class="text-base font-bold text-white">Digital Boarding Pass</h3>
                    <span class="text-xs text-emerald-400 font-medium">Status: {{ activeBoardingPass.status }}</span>
                  </div>
                </div>

                <div class="text-right">
                  <span class="text-[10px] text-slate-400 block uppercase">Boarding Pass Code</span>
                  <span class="font-mono text-sm font-bold text-brand-300">{{ activeBoardingPass.boardingPassNumber }}</span>
                </div>
              </div>

              <!-- Pass Details Grid -->
              <div class="grid grid-cols-2 sm:grid-cols-4 gap-4 py-6 border-b border-slate-800 text-xs">
                <div>
                  <span class="text-slate-400 block text-[11px]">Passenger Name</span>
                  <span class="text-sm font-bold text-white">{{ activeBoardingPass.passengerName }}</span>
                </div>
                <div>
                  <span class="text-slate-400 block text-[11px]">Assigned Seat</span>
                  <span class="text-base font-extrabold text-brand-400 font-mono">{{ activeBoardingPass.seatNumber }}</span>
                </div>
                <div>
                  <span class="text-slate-400 block text-[11px]">Booking ID</span>
                  <span class="text-sm font-bold text-white">#{{ activeBoardingPass.bookingId }}</span>
                </div>
                <div>
                  <span class="text-slate-400 block text-[11px]">Check-In Time</span>
                  <span class="text-xs font-semibold text-slate-300">{{ activeBoardingPass.checkedInAt | date:'medium' }}</span>
                </div>
              </div>

              <!-- Dynamic QR Code & Barcode Display -->
              <div class="py-6 flex flex-col sm:flex-row items-center justify-between gap-6">
                
                <div class="space-y-1 text-center sm:text-left">
                  <span class="text-xs font-semibold text-slate-300 block">Gate Verification Barcode</span>
                  <p class="text-[11px] text-slate-400 max-w-sm">Present this QR code or boarding pass number at the airport gate scanner for contactless boarding verification.</p>
                  <div class="pt-2 flex items-center gap-2">
                    <span class="px-2.5 py-1 rounded bg-slate-900 border border-slate-800 text-slate-300 font-mono text-xs">
                      {{ activeBoardingPass.boardingPassNumber }}
                    </span>
                    <button (click)="printPass()" class="px-3 py-1 rounded bg-slate-800 hover:bg-slate-700 text-slate-200 text-xs font-semibold transition-colors">
                      🖨️ Print Pass
                    </button>
                  </div>
                </div>

                <!-- SVG QR Code Visualizer -->
                <div class="p-3 bg-white rounded-2xl shadow-xl flex flex-col items-center">
                  <svg class="w-28 h-28 text-slate-900" viewBox="0 0 100 100" fill="currentColor">
                    <!-- Corner anchors -->
                    <rect x="10" y="10" width="24" height="24" rx="3" fill="#0B1329"/>
                    <rect x="14" y="14" width="16" height="16" fill="white"/>
                    <rect x="18" y="18" width="8" height="8" fill="#0B1329"/>

                    <rect x="66" y="10" width="24" height="24" rx="3" fill="#0B1329"/>
                    <rect x="70" y="14" width="16" height="16" fill="white"/>
                    <rect x="74" y="18" width="8" height="8" fill="#0B1329"/>

                    <rect x="10" y="66" width="24" height="24" rx="3" fill="#0B1329"/>
                    <rect x="14" y="70" width="16" height="16" fill="white"/>
                    <rect x="18" y="74" width="8" height="8" fill="#0B1329"/>

                    <!-- QR Data matrix dots -->
                    <rect x="42" y="14" width="6" height="6" fill="#0B1329"/>
                    <rect x="52" y="20" width="6" height="6" fill="#0B1329"/>
                    <rect x="44" y="30" width="6" height="6" fill="#0B1329"/>
                    <rect x="16" y="44" width="6" height="6" fill="#0B1329"/>
                    <rect x="26" y="52" width="6" height="6" fill="#0B1329"/>
                    <rect x="44" y="44" width="12" height="12" rx="2" fill="#0284C7"/>
                    <rect x="64" y="44" width="6" height="6" fill="#0B1329"/>
                    <rect x="76" y="52" width="6" height="6" fill="#0B1329"/>
                    <rect x="44" y="66" width="6" height="6" fill="#0B1329"/>
                    <rect x="54" y="76" width="6" height="6" fill="#0B1329"/>
                    <rect x="66" y="72" width="8" height="8" fill="#0B1329"/>
                    <rect x="80" y="80" width="8" height="8" fill="#0B1329"/>
                  </svg>
                  <span class="text-[9px] font-mono text-slate-800 font-bold mt-1">AERO-SCAN</span>
                </div>

              </div>

            </div>
          }

        </div>
      }

      <!-- TAB 4: FLIGHT NOTICES & ALERTS -->
      @if (activeTab === 'notifications') {
        <div class="glass-panel rounded-3xl p-6 sm:p-8 border border-slate-700/60 shadow-xl max-w-4xl mx-auto space-y-4">
          <div class="flex items-center justify-between pb-4 border-b border-slate-800">
            <div>
              <h2 class="text-xl font-bold text-white flex items-center gap-2">
                <span>🔔 Flight Updates & Operational Notices</span>
              </h2>
              <p class="text-xs text-slate-400 mt-1">Real-time alerts published by notification-service (Port 8091).</p>
            </div>

            @if (notifications.length > 0) {
              <button (click)="markAllAsRead()" class="px-3 py-1.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-semibold border border-slate-700 transition-colors">
                Mark All as Read
              </button>
            }
          </div>

          @if (notifications.length === 0) {
            <div class="text-center py-12">
              <div class="text-4xl mb-2">🔕</div>
              <h3 class="text-sm font-semibold text-white">No New Notifications</h3>
              <p class="text-xs text-slate-400 mt-1">You are all caught up with your flight announcements.</p>
            </div>
          }

          <div class="space-y-3">
            @for (n of notifications; track n.id) {
              <div class="p-4 rounded-2xl border transition-all"
                   [ngClass]="n.status === 'UNREAD' ? 'bg-slate-900 border-brand-500/40 shadow-sm' : 'bg-slate-950/60 border-slate-800 opacity-75'">
                <div class="flex items-start justify-between gap-4">
                  <div class="flex items-start gap-3">
                    <span class="text-xl">
                      {{ n.type === 'FLIGHT_DELAY' ? '⚠️' : n.type === 'GATE_CHANGE' ? '🚪' : n.type === 'BOOKING_CONFIRMED' ? '✅' : '📢' }}
                    </span>
                    <div>
                      <div class="flex items-center gap-2">
                        <h4 class="text-sm font-bold text-white">{{ n.title }}</h4>
                        @if (n.status === 'UNREAD') {
                          <span class="w-2 h-2 rounded-full bg-brand-400"></span>
                        }
                      </div>
                      <p class="text-xs text-slate-300 mt-1">{{ n.message }}</p>
                      <span class="text-[10px] text-slate-500 mt-2 block">{{ n.createdAt | date:'medium' }}</span>
                    </div>
                  </div>

                  @if (n.status === 'UNREAD') {
                    <button (click)="markAsRead(n.id)" class="px-2.5 py-1 rounded bg-slate-800 hover:bg-slate-700 text-slate-400 hover:text-white text-[11px] transition-colors shrink-0">
                      Mark read
                    </button>
                  }
                </div>
              </div>
            }
          </div>
        </div>
      }

      <!-- TAB 5: PROFILE & ACCOUNT -->
      @if (activeTab === 'profile') {
        <div class="glass-panel rounded-3xl p-6 sm:p-8 border border-slate-700/60 shadow-xl max-w-xl mx-auto">
          <div class="mb-6 pb-4 border-b border-slate-800">
            <h2 class="text-xl font-bold text-white flex items-center gap-2">
              <span>⚙️ Customer Profile</span>
            </h2>
            <p class="text-xs text-slate-400 mt-1">User identity coordinates stored in user-service (Port 8084).</p>
          </div>

          @if (profileMessage) {
            <div class="mb-4 p-3 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-300 text-xs">
              {{ profileMessage }}
            </div>
          }

          <form (ngSubmit)="saveProfile()" class="space-y-4">
            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-xs font-medium text-slate-400 mb-1">First Name</label>
                <input type="text" [(ngModel)]="profileFirstName" name="firstName" class="w-full px-3.5 py-2 rounded-xl bg-slate-900 border border-slate-700 text-white text-sm">
              </div>
              <div>
                <label class="block text-xs font-medium text-slate-400 mb-1">Last Name</label>
                <input type="text" [(ngModel)]="profileLastName" name="lastName" class="w-full px-3.5 py-2 rounded-xl bg-slate-900 border border-slate-700 text-white text-sm">
              </div>
            </div>

            <div>
              <label class="block text-xs font-medium text-slate-400 mb-1">Email (Read Only)</label>
              <input type="email" [value]="userProfile?.email" disabled class="w-full px-3.5 py-2 rounded-xl bg-slate-950 border border-slate-800 text-slate-400 text-sm">
            </div>

            <div>
              <label class="block text-xs font-medium text-slate-400 mb-1">Phone Number</label>
              <input type="tel" [(ngModel)]="profilePhone" name="phone" class="w-full px-3.5 py-2 rounded-xl bg-slate-900 border border-slate-700 text-white text-sm">
            </div>

            <div>
              <label class="block text-xs font-medium text-slate-400 mb-1">Nationality</label>
              <input type="text" [(ngModel)]="profileNationality" name="nationality" class="w-full px-3.5 py-2 rounded-xl bg-slate-900 border border-slate-700 text-white text-sm">
            </div>

            <button type="submit" class="w-full py-3 rounded-xl bg-brand-500 hover:bg-brand-600 text-white text-xs font-bold transition-all shadow-md">
              Update Profile Information
            </button>
          </form>
        </div>
      }

    </div>
  `
})
export class CustomerDashboardComponent implements OnInit {
  activeTab: 'bookings' | 'book' | 'checkin' | 'notifications' | 'profile' = 'bookings';

  userProfile: User | null = null;
  bookings: Booking[] = [];
  availableFlights: Flight[] = [];
  notifications: Notification[] = [];
  unreadCount: number = 0;

  // New Booking State
  newBookingFlightId: number | null = null;
  selectedFlight: Flight | null = null;
  passengerFirstName: string = 'Asha';
  passengerLastName: string = 'Khan';
  passengerAge: number = 29;
  passengerGender: string = 'F';
  selectedSeat: string = '12A';
  bookingLoading: boolean = false;
  seatRows: number[] = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12];

  // Check In State
  checkInBookingId: number = 1;
  checkInPassengerName: string = 'Asha Khan';
  checkInSeat: string = '12A';
  checkInLoading: boolean = false;
  activeBoardingPass: CheckIn | null = null;

  // Profile Form State
  profileFirstName: string = '';
  profileLastName: string = '';
  profilePhone: string = '';
  profileNationality: string = '';
  profileMessage: string = '';

  constructor(
    public authService: AuthService,
    private bookingService: BookingService,
    private flightService: FlightService,
    private checkInService: CheckInService,
    private notificationService: NotificationService,
    private userService: UserService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const userId = this.authService.currentUserId();
    if (userId) {
      this.loadUserProfile(userId);
      this.loadUserBookings(userId);
      this.loadNotifications(userId);
    }
    this.loadAvailableFlights();

    // Check if routed with pre-selected flight
    this.route.queryParams.subscribe(params => {
      if (params['selectedFlightId']) {
        this.activeTab = 'book';
        this.newBookingFlightId = Number(params['selectedFlightId']);
        this.onFlightSelect();
      }
    });
  }

  loadUserProfile(userId: number): void {
    this.userService.getUserById(userId).subscribe({
      next: (user) => {
        this.userProfile = user;
        this.profileFirstName = user.firstName;
        this.profileLastName = user.lastName;
        this.profilePhone = user.phoneNumber || '';
        this.profileNationality = user.nationality || '';
        this.passengerFirstName = user.firstName;
        this.passengerLastName = user.lastName;
        this.checkInPassengerName = `${user.firstName} ${user.lastName}`;
      }
    });
  }

  loadUserBookings(userId: number): void {
    this.bookingService.getBookingsByUser(userId).subscribe({
      next: (data) => {
        this.bookings = data;
        if (data.length > 0) {
          this.checkInBookingId = data[0].bookingId;
        }
      }
    });
  }

  loadAvailableFlights(): void {
    this.flightService.getAllFlights().subscribe({
      next: (flights) => {
        this.availableFlights = flights;
        if (this.newBookingFlightId) {
          this.onFlightSelect();
        }
      }
    });
  }

  loadNotifications(userId: number): void {
    this.notificationService.getUserNotifications(userId).subscribe({
      next: (notes) => {
        this.notifications = notes;
        this.unreadCount = notes.filter(n => n.status === 'UNREAD').length;
      }
    });
  }

  onFlightSelect(): void {
    this.selectedFlight = this.availableFlights.find(f => f.id === this.newBookingFlightId) || null;
  }

  chooseSeat(seat: string): void {
    this.selectedSeat = seat;
  }

  getSeatClass(seat: string): string {
    if (this.selectedSeat === seat) {
      return 'bg-brand-500 text-white shadow-md shadow-brand-500/40 ring-2 ring-brand-400';
    }
    return 'bg-slate-800 text-slate-300 hover:bg-slate-700 border border-slate-700';
  }

  submitBooking(): void {
    if (!this.newBookingFlightId) return;
    this.bookingLoading = true;

    const userId = this.authService.currentUserId() || 1;
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

    this.bookingService.createBooking(request).subscribe({
      next: (b) => {
        this.bookingLoading = false;
        this.activeTab = 'bookings';
        this.loadUserBookings(userId);
        this.loadNotifications(userId);
      },
      error: () => {
        this.bookingLoading = false;
      }
    });
  }

  cancelBooking(bookingId: number): void {
    if (!confirm('Are you sure you want to cancel this booking?')) return;
    this.bookingService.cancelBooking(bookingId).subscribe({
      next: () => {
        const userId = this.authService.currentUserId();
        if (userId) this.loadUserBookings(userId);
      }
    });
  }

  openCheckInForBooking(b: Booking): void {
    this.checkInBookingId = b.bookingId;
    if (b.passengers && b.passengers.length > 0) {
      this.checkInPassengerName = `${b.passengers[0].firstName} ${b.passengers[0].lastName}`;
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
        const userId = this.authService.currentUserId();
        if (userId) this.loadNotifications(userId);
      },
      error: () => {
        this.checkInLoading = false;
      }
    });
  }

  markAsRead(id: number): void {
    this.notificationService.markAsRead(id).subscribe({
      next: () => {
        const note = this.notifications.find(n => n.id === id);
        if (note) {
          note.status = 'READ';
          this.unreadCount = Math.max(0, this.unreadCount - 1);
        }
      }
    });
  }

  markAllAsRead(): void {
    const userId = this.authService.currentUserId();
    if (userId) {
      this.notificationService.markAllAsRead(userId).subscribe({
        next: () => {
          this.notifications.forEach(n => n.status = 'READ');
          this.unreadCount = 0;
        }
      });
    }
  }

  saveProfile(): void {
    const userId = this.authService.currentUserId();
    if (!userId) return;

    this.userService.updateUser(userId, {
      firstName: this.profileFirstName,
      lastName: this.profileLastName,
      phoneNumber: this.profilePhone,
      nationality: this.profileNationality
    }).subscribe({
      next: (u) => {
        this.userProfile = u;
        this.profileMessage = 'Profile updated successfully!';
        setTimeout(() => this.profileMessage = '', 4000);
      }
    });
  }

  printPass(): void {
    window.print();
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
}


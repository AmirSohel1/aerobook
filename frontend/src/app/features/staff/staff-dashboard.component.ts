import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FlightService } from '../../core/services/flight.service';
import { BookingService } from '../../core/services/booking.service';
import { CheckInService } from '../../core/services/checkin.service';
import { NotificationService } from '../../core/services/notification.service';
import { AuthService } from '../../core/services/auth.service';
import { Booking, CheckIn, Flight, Notification } from '../../core/models/aerobook.models';

@Component({
  selector: 'app-staff-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 animate-fadeIn">
      
      <!-- Operations Header Banner -->
      <div class="glass-panel rounded-3xl p-6 sm:p-8 mb-8 border border-amber-500/30 shadow-2xl relative overflow-hidden">
        <div class="absolute -right-20 -bottom-20 w-80 h-80 bg-amber-500/10 rounded-full blur-3xl pointer-events-none"></div>

        <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
          <div>
            <div class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-amber-500/10 border border-amber-500/30 text-amber-300 text-xs font-semibold mb-3">
              <span>✈️</span> Airport Operations & Ground Station Hub
            </div>
            <h1 class="text-2xl sm:text-3xl font-extrabold text-white tracking-tight">
              Airport Ground & Flight Operations
            </h1>
            <p class="text-xs text-slate-400 mt-1">Real-time gate boarding scanner, flight schedule status control, passenger manifests, and departure alerts.</p>
          </div>

          <!-- Station Status Controls -->
          <div class="flex items-center gap-3 bg-slate-900/90 p-3 rounded-2xl border border-slate-800">
            <div class="text-center px-3 border-r border-slate-800">
              <span class="text-xl font-extrabold text-white">{{ flights.length }}</span>
              <span class="block text-[10px] text-slate-400 uppercase tracking-wider">Flights</span>
            </div>
            <div class="text-center px-3 border-r border-slate-800">
              <span class="text-xl font-extrabold text-amber-400">{{ checkIns.length }}</span>
              <span class="block text-[10px] text-slate-400 uppercase tracking-wider">Check-Ins</span>
            </div>
            <div class="text-center px-3">
              <span class="text-xs font-semibold text-emerald-400">Station BOM</span>
              <span class="block text-[10px] text-slate-400 uppercase tracking-wider">Terminal 2</span>
            </div>
          </div>
        </div>

        <!-- Navigation Sub-Tabs -->
        <div class="flex flex-wrap items-center gap-2 mt-8 pt-6 border-t border-slate-800 text-xs font-semibold">
          <button (click)="activeTab = 'operations'" [class]="activeTab === 'operations' ? 'bg-amber-600 text-white shadow-md' : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'" class="px-4 py-2 rounded-xl transition-all flex items-center gap-1.5">
            <span>🎛️</span> Flight Status Board
          </button>
          <button (click)="activeTab = 'manifest'" [class]="activeTab === 'manifest' ? 'bg-amber-600 text-white shadow-md' : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'" class="px-4 py-2 rounded-xl transition-all flex items-center gap-1.5">
            <span>📋</span> Flight Manifest
          </button>
          <button (click)="activeTab = 'scanner'" [class]="activeTab === 'scanner' ? 'bg-amber-600 text-white shadow-md' : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'" class="px-4 py-2 rounded-xl transition-all flex items-center gap-1.5">
            <span>🔍</span> Gate Boarding Pass Scanner
          </button>
          <button (click)="activeTab = 'counter'" [class]="activeTab === 'counter' ? 'bg-amber-600 text-white shadow-md' : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'" class="px-4 py-2 rounded-xl transition-all flex items-center gap-1.5">
            <span>🧳</span> Airport Counter Check-In
          </button>
          <button (click)="activeTab = 'broadcast'" [class]="activeTab === 'broadcast' ? 'bg-amber-600 text-white shadow-md' : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/60'" class="px-4 py-2 rounded-xl transition-all flex items-center gap-1.5">
            <span>📢</span> Broadcast Operational Notice
          </button>
        </div>
      </div>

      <!-- SUCCESS / ALERT NOTICES -->
      @if (actionMessage) {
        <div class="mb-6 p-4 rounded-2xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-300 text-xs flex items-center justify-between animate-fadeIn">
          <div class="flex items-center gap-2">
            <span>✅</span>
            <span class="font-medium">{{ actionMessage }}</span>
          </div>
          <button (click)="actionMessage = ''" class="text-emerald-400 hover:text-emerald-200 text-xs">Dismiss</button>
        </div>
      }

      <!-- TAB 1: FLIGHT STATUS BOARD -->
      @if (activeTab === 'operations') {
        <div class="glass-panel rounded-3xl p-6 sm:p-8 border border-slate-700/60 shadow-xl space-y-6">
          <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-800">
            <div>
              <h2 class="text-lg font-bold text-white flex items-center gap-2">
                <span>Live Flight Status & Departure Transition Control</span>
              </h2>
              <p class="text-xs text-slate-400 mt-1">Ground staff operational authority to transition flight states across the airport platform.</p>
            </div>
            <button (click)="loadFlights()" class="px-3.5 py-1.5 rounded-xl bg-slate-800 hover:bg-slate-700 text-slate-300 text-xs font-semibold border border-slate-700 transition-colors">
              🔄 Refresh Board
            </button>
          </div>

          <div class="overflow-x-auto">
            <table class="w-full text-left text-xs">
              <thead class="bg-slate-900/80 text-slate-400 uppercase tracking-wider text-[10px] border-b border-slate-800">
                <tr>
                  <th class="py-3.5 px-4 font-semibold">Flight No.</th>
                  <th class="py-3.5 px-4 font-semibold">Airliner</th>
                  <th class="py-3.5 px-4 font-semibold">Route</th>
                  <th class="py-3.5 px-4 font-semibold">Departure</th>
                  <th class="py-3.5 px-4 font-semibold">Current Status</th>
                  <th class="py-3.5 px-4 font-semibold text-right">Operational Actions</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-slate-800/60">
                @for (f of flights; track f.id) {
                  <tr class="hover:bg-slate-900/40 transition-colors">
                    
                    <td class="py-3.5 px-4 font-bold font-mono text-brand-300">
                      {{ f.flightNumber }}
                    </td>

                    <td class="py-3.5 px-4 font-medium text-white">
                      {{ f.airlineName }}
                    </td>

                    <td class="py-3.5 px-4 font-semibold text-slate-200">
                      {{ f.source }} → {{ f.destination }}
                    </td>

                    <td class="py-3.5 px-4 text-slate-300">
                      {{ formatTime(f.departureTime) }}
                    </td>

                    <td class="py-3.5 px-4">
                      <span class="px-2.5 py-1 rounded-full text-[11px] font-semibold border"
                            [ngClass]="{
                              'bg-blue-500/10 text-blue-300 border-blue-500/30': f.status === 'SCHEDULED',
                              'bg-amber-500/10 text-amber-300 border-amber-500/30': f.status === 'BOARDING',
                              'bg-orange-500/10 text-orange-300 border-orange-500/30': f.status === 'DELAYED',
                              'bg-cyan-500/10 text-cyan-300 border-cyan-500/30': f.status === 'DEPARTED',
                              'bg-emerald-500/10 text-emerald-300 border-emerald-500/30': f.status === 'COMPLETED',
                              'bg-red-500/10 text-red-300 border-red-500/30': f.status === 'CANCELLED'
                            }">
                        ● {{ f.status }}
                      </span>
                    </td>

                    <td class="py-3.5 px-4 text-right">
                      <div class="inline-flex items-center gap-1.5">
                        <button (click)="updateStatus(f.id, 'BOARDING')" title="Call Boarding"
                                class="px-2.5 py-1 rounded-lg bg-amber-500/10 hover:bg-amber-500/20 text-amber-300 border border-amber-500/30 text-[11px] font-medium transition-colors">
                          Boarding
                        </button>
                        <button (click)="updateStatus(f.id, 'DEPARTED')" title="Mark Departed"
                                class="px-2.5 py-1 rounded-lg bg-cyan-500/10 hover:bg-cyan-500/20 text-cyan-300 border border-cyan-500/30 text-[11px] font-medium transition-colors">
                          Departed
                        </button>
                        <button (click)="updateStatus(f.id, 'DELAYED')" title="Mark Delayed"
                                class="px-2.5 py-1 rounded-lg bg-orange-500/10 hover:bg-orange-500/20 text-orange-300 border border-orange-500/30 text-[11px] font-medium transition-colors">
                          Delay
                        </button>
                        <button (click)="updateStatus(f.id, 'COMPLETED')" title="Mark Completed"
                                class="px-2.5 py-1 rounded-lg bg-emerald-500/10 hover:bg-emerald-500/20 text-emerald-300 border border-emerald-500/30 text-[11px] font-medium transition-colors">
                          Arrived
                        </button>
                      </div>
                    </td>

                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      }

      <!-- TAB 2: FLIGHT MANIFEST VIEWER -->
      @if (activeTab === 'manifest') {
        <div class="glass-panel rounded-3xl p-6 sm:p-8 border border-slate-700/60 shadow-xl space-y-6">
          <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-slate-800">
            <div>
              <h2 class="text-lg font-bold text-white flex items-center gap-2">
                <span>📋 Passenger Manifest & Flight Roster</span>
              </h2>
              <p class="text-xs text-slate-400 mt-1">Review all ticketed passengers on any scheduled departure.</p>
            </div>

            <div class="flex items-center gap-3">
              <label class="text-xs text-slate-400">Select Flight:</label>
              <select [(ngModel)]="selectedManifestFlightId" (change)="loadManifest()"
                      class="px-3.5 py-2 rounded-xl bg-slate-900 border border-slate-700 text-white text-xs font-semibold focus:outline-none focus:border-brand-500">
                @for (f of flights; track f.id) {
                  <option [ngValue]="f.id">{{ f.flightNumber }} ({{ f.source }} → {{ f.destination }})</option>
                }
              </select>
            </div>
          </div>

          @if (manifestBookings.length === 0) {
            <div class="text-center py-12">
              <div class="text-3xl mb-2">📄</div>
              <h3 class="text-sm font-semibold text-white">No Passenger Bookings for this Flight</h3>
              <p class="text-xs text-slate-400 mt-1">Try selecting another scheduled flight to review its manifest.</p>
            </div>
          } @else {
            <div class="overflow-x-auto">
              <table class="w-full text-left text-xs">
                <thead class="bg-slate-900/80 text-slate-400 uppercase tracking-wider text-[10px] border-b border-slate-800">
                  <tr>
                    <th class="py-3.5 px-4 font-semibold">Booking ID</th>
                    <th class="py-3.5 px-4 font-semibold">PNR Record</th>
                    <th class="py-3.5 px-4 font-semibold">Passenger Name</th>
                    <th class="py-3.5 px-4 font-semibold">Age / Gender</th>
                    <th class="py-3.5 px-4 font-semibold">Assigned Seat</th>
                    <th class="py-3.5 px-4 font-semibold">Ticket Status</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-slate-800/60">
                  @for (b of manifestBookings; track b.bookingId) {
                    @for (p of b.passengers; track p.passengerId || p.firstName) {
                      <tr class="hover:bg-slate-900/40 transition-colors">
                        <td class="py-3.5 px-4 font-mono text-slate-400">#{{ b.bookingId }}</td>
                        <td class="py-3.5 px-4 font-mono font-bold text-brand-300">{{ b.pnr }}</td>
                        <td class="py-3.5 px-4 font-bold text-white">{{ p.firstName }} {{ p.lastName }}</td>
                        <td class="py-3.5 px-4 text-slate-300">{{ p.age }} yrs / {{ p.gender }}</td>
                        <td class="py-3.5 px-4 font-mono font-bold text-amber-300">{{ p.seatNumber || 'Unassigned' }}</td>
                        <td class="py-3.5 px-4">
                          <span class="px-2 py-0.5 rounded text-[10px] font-semibold bg-emerald-500/10 text-emerald-300 border border-emerald-500/30">
                            {{ b.status }}
                          </span>
                        </td>
                      </tr>
                    }
                  }
                </tbody>
              </table>
            </div>
          }
        </div>
      }

      <!-- TAB 3: GATE SCANNER & BOARDING VERIFIER -->
      @if (activeTab === 'scanner') {
        <div class="glass-panel rounded-3xl p-6 sm:p-8 border border-slate-700/60 shadow-xl max-w-2xl mx-auto space-y-6">
          <div class="text-center pb-4 border-b border-slate-800">
            <div class="w-12 h-12 rounded-2xl bg-amber-500/10 border border-amber-500/30 text-amber-400 flex items-center justify-center text-xl mx-auto mb-3 shadow-lg">
              🔍
            </div>
            <h2 class="text-xl font-bold text-white">Boarding Gate Pass Verifier</h2>
            <p class="text-xs text-slate-400 mt-1">Scan or enter passenger boarding pass reference code to verify flight boarding eligibility.</p>
          </div>

          <form (ngSubmit)="verifyPass()" class="space-y-4">
            <div>
              <label class="block text-xs font-semibold text-slate-300 mb-1">Boarding Pass Code (e.g. BP-1-12A)</label>
              <div class="flex gap-2">
                <input type="text" [(ngModel)]="scanCode" name="scanCode" required placeholder="BP-1-12A"
                       class="flex-1 px-4 py-3 rounded-xl bg-slate-900 border border-slate-700 text-white font-mono text-sm tracking-wider uppercase focus:outline-none focus:border-amber-500">
                <button type="submit" [disabled]="scanLoading"
                        class="px-6 py-3 rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600 text-white font-bold text-xs shadow-md transition-all">
                  @if (scanLoading) {
                    <span>Verifying...</span>
                  } @else {
                    <span>Verify Pass</span>
                  }
                </button>
              </div>
            </div>
          </form>

          <!-- Verification Result Banner -->
          @if (scanResult) {
            <div class="p-6 rounded-2xl bg-emerald-500/10 border border-emerald-500/30 animate-fadeIn space-y-3">
              <div class="flex items-center gap-3">
                <div class="w-10 h-10 rounded-full bg-emerald-500 text-white flex items-center justify-center text-xl font-bold">
                  ✓
                </div>
                <div>
                  <h3 class="text-base font-bold text-white">BOARDING VERIFIED & CONFIRMED</h3>
                  <span class="text-xs text-emerald-400 font-semibold font-mono">STATUS: {{ scanResult.status }}</span>
                </div>
              </div>

              <div class="grid grid-cols-2 gap-4 pt-3 border-t border-emerald-500/20 text-xs">
                <div>
                  <span class="text-slate-400 block text-[11px]">Passenger Name</span>
                  <span class="text-sm font-bold text-white">{{ scanResult.passengerName }}</span>
                </div>
                <div>
                  <span class="text-slate-400 block text-[11px]">Assigned Seat</span>
                  <span class="text-base font-extrabold text-amber-400 font-mono">{{ scanResult.seatNumber }}</span>
                </div>
                <div>
                  <span class="text-slate-400 block text-[11px]">Boarding Code</span>
                  <span class="text-xs font-mono text-slate-200">{{ scanResult.boardingPassNumber }}</span>
                </div>
                <div>
                  <span class="text-slate-400 block text-[11px]">Booking ID</span>
                  <span class="text-xs font-mono text-slate-200">#{{ scanResult.bookingId }}</span>
                </div>
              </div>
            </div>
          }

          @if (scanError) {
            <div class="p-4 rounded-2xl bg-red-500/10 border border-red-500/30 text-red-300 text-xs flex items-center gap-2">
              <span>⚠️</span>
              <span>{{ scanError }}</span>
            </div>
          }
        </div>
      }

      <!-- TAB 4: AIRPORT COUNTER CHECK-IN -->
      @if (activeTab === 'counter') {
        <div class="glass-panel rounded-3xl p-6 sm:p-8 border border-slate-700/60 shadow-xl max-w-2xl mx-auto space-y-6">
          <div class="text-center pb-4 border-b border-slate-800">
            <h2 class="text-xl font-bold text-white flex items-center justify-center gap-2">
              <span>🧳 Airport Desk Check-In Assistance</span>
            </h2>
            <p class="text-xs text-slate-400 mt-1">Staff manual passenger check-in and seat assignment counter.</p>
          </div>

          <form (ngSubmit)="performStaffCheckIn()" class="space-y-4">
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
              <div>
                <label class="block text-xs font-semibold text-slate-300 mb-1">Booking ID</label>
                <input type="number" [(ngModel)]="counterBookingId" name="counterBookingId" required placeholder="e.g. 1"
                       class="w-full px-3.5 py-2.5 rounded-xl bg-slate-900 border border-slate-700 text-white text-sm focus:outline-none focus:border-amber-500">
              </div>
              <div>
                <label class="block text-xs font-semibold text-slate-300 mb-1">Counter Assigned Seat</label>
                <input type="text" [(ngModel)]="counterSeat" name="counterSeat" required placeholder="14B"
                       class="w-full px-3.5 py-2.5 rounded-xl bg-slate-900 border border-slate-700 text-white text-sm focus:outline-none focus:border-amber-500">
              </div>
            </div>

            <div>
              <label class="block text-xs font-semibold text-slate-300 mb-1">Passenger Full Name</label>
              <input type="text" [(ngModel)]="counterPassengerName" name="counterPassengerName" required placeholder="Asha Khan"
                     class="w-full px-3.5 py-2.5 rounded-xl bg-slate-900 border border-slate-700 text-white text-sm focus:outline-none focus:border-amber-500">
            </div>

            <button type="submit" class="w-full py-3 rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600 text-white font-bold text-xs shadow-md transition-all">
              Execute Counter Check-In & Issue Pass
            </button>
          </form>
        </div>
      }

      <!-- TAB 5: BROADCAST OPERATIONAL NOTICE -->
      @if (activeTab === 'broadcast') {
        <div class="glass-panel rounded-3xl p-6 sm:p-8 border border-slate-700/60 shadow-xl max-w-xl mx-auto space-y-6">
          <div class="text-center pb-4 border-b border-slate-800">
            <h2 class="text-xl font-bold text-white flex items-center justify-center gap-2">
              <span>📢 Operational Alert Broadcast Dispatcher</span>
            </h2>
            <p class="text-xs text-slate-400 mt-1">Dispatches notifications to passenger mobile apps via notification-service.</p>
          </div>

          <form (ngSubmit)="sendBroadcastAlert()" class="space-y-4">
            <div>
              <label class="block text-xs font-semibold text-slate-300 mb-1">Notice Category</label>
              <select [(ngModel)]="broadcastType" name="broadcastType" class="w-full px-3.5 py-2.5 rounded-xl bg-slate-900 border border-slate-700 text-white text-sm focus:outline-none focus:border-amber-500">
                <option value="FLIGHT_DELAY">⚠️ Flight Delay Alert</option>
                <option value="GATE_CHANGE">🚪 Departure Gate Change</option>
                <option value="BOARDING_CALL">✈️ Final Boarding Call</option>
                <option value="BROADCAST">📢 General Passenger Announcement</option>
              </select>
            </div>

            <div>
              <label class="block text-xs font-semibold text-slate-300 mb-1">Target Flight Number (Optional)</label>
              <input type="text" [(ngModel)]="broadcastFlight" name="broadcastFlight" placeholder="e.g. AI101"
                     class="w-full px-3.5 py-2.5 rounded-xl bg-slate-900 border border-slate-700 text-white text-sm focus:outline-none focus:border-amber-500">
            </div>

            <div>
              <label class="block text-xs font-semibold text-slate-300 mb-1">Announcement Title</label>
              <input type="text" [(ngModel)]="broadcastTitle" name="broadcastTitle" required placeholder="e.g. Gate Changed to Gate 4B"
                     class="w-full px-3.5 py-2.5 rounded-xl bg-slate-900 border border-slate-700 text-white text-sm focus:outline-none focus:border-amber-500">
            </div>

            <div>
              <label class="block text-xs font-semibold text-slate-300 mb-1">Detailed Message Content</label>
              <textarea [(ngModel)]="broadcastMessage" name="broadcastMessage" required rows="3" placeholder="Flight AI101 to Delhi will now board from Terminal 2, Gate 4B."
                        class="w-full px-3.5 py-2.5 rounded-xl bg-slate-900 border border-slate-700 text-white text-sm focus:outline-none focus:border-amber-500"></textarea>
            </div>

            <button type="submit" class="w-full py-3 rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600 text-white font-bold text-xs shadow-md transition-all">
              Broadcast Real-Time Notice
            </button>
          </form>
        </div>
      }

    </div>
  `
})
export class StaffDashboardComponent implements OnInit {
  activeTab: 'operations' | 'manifest' | 'scanner' | 'counter' | 'broadcast' = 'operations';

  flights: Flight[] = [];
  checkIns: CheckIn[] = [];
  selectedManifestFlightId: number | null = null;
  manifestBookings: Booking[] = [];

  actionMessage: string = '';

  // Gate Scanner State
  scanCode: string = 'BP-1-12A';
  scanLoading: boolean = false;
  scanResult: CheckIn | null = null;
  scanError: string = '';

  // Desk Check-In State
  counterBookingId: number = 1;
  counterPassengerName: string = 'Asha Khan';
  counterSeat: string = '14B';

  // Broadcast Alert State
  broadcastType: string = 'GATE_CHANGE';
  broadcastFlight: string = 'AI101';
  broadcastTitle: string = 'Gate Moved to Gate 4B';
  broadcastMessage: string = 'Passengers for Flight AI101 please proceed immediately to Gate 4B.';

  constructor(
    private flightService: FlightService,
    private bookingService: BookingService,
    private checkInService: CheckInService,
    private notificationService: NotificationService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadFlights();
    this.loadCheckIns();
  }

  loadFlights(): void {
    this.flightService.getAllFlights().subscribe({
      next: (data) => {
        this.flights = data;
        if (data.length > 0 && !this.selectedManifestFlightId) {
          this.selectedManifestFlightId = data[0].id;
          this.loadManifest();
        }
      }
    });
  }

  loadCheckIns(): void {
    this.checkInService.getAllCheckIns().subscribe({
      next: (data) => this.checkIns = data,
      error: () => {}
    });
  }

  updateStatus(flightId: number, status: string): void {
    this.flightService.updateFlightStatus(flightId, status).subscribe({
      next: (f) => {
        const item = this.flights.find(x => x.id === flightId);
        if (item) item.status = f.status;
        this.actionMessage = `Flight ${f.flightNumber} status successfully updated to ${status}.`;
        setTimeout(() => this.actionMessage = '', 4000);
      }
    });
  }

  loadManifest(): void {
    if (!this.selectedManifestFlightId) return;
    this.bookingService.getBookingsByFlight(this.selectedManifestFlightId).subscribe({
      next: (data) => this.manifestBookings = data,
      error: () => this.manifestBookings = []
    });
  }

  verifyPass(): void {
    if (!this.scanCode) return;
    this.scanLoading = true;
    this.scanResult = null;
    this.scanError = '';

    this.checkInService.verifyBoardingPass(this.scanCode.trim()).subscribe({
      next: (res) => {
        this.scanResult = res;
        this.scanLoading = false;
        this.actionMessage = `Passenger ${res.passengerName} verified and marked as BOARDED!`;
        this.loadCheckIns();
      },
      error: () => {
        this.scanLoading = false;
        this.scanError = `No valid check-in found for barcode: ${this.scanCode}`;
      }
    });
  }

  performStaffCheckIn(): void {
    this.checkInService.checkIn({
      bookingId: this.counterBookingId,
      passengerName: this.counterPassengerName,
      seatNumber: this.counterSeat
    }).subscribe({
      next: (res) => {
        this.actionMessage = `Airport check-in confirmed for ${res.passengerName}. Boarding pass: ${res.boardingPassNumber}`;
        this.loadCheckIns();
        this.scanCode = res.boardingPassNumber;
        this.activeTab = 'scanner';
      }
    });
  }

  sendBroadcastAlert(): void {
    this.notificationService.broadcastAlert({
      title: this.broadcastTitle,
      message: this.broadcastMessage,
      type: this.broadcastType,
      flightNumber: this.broadcastFlight
    }).subscribe({
      next: () => {
        this.actionMessage = `Real-time announcement broadcasted successfully to passengers!`;
        setTimeout(() => this.actionMessage = '', 4000);
      }
    });
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


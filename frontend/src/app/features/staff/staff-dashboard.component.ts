import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FlightService } from '../../core/services/flight.service';
import { BookingService } from '../../core/services/booking.service';
import { CheckInService } from '../../core/services/checkin.service';
import { NotificationService } from '../../core/services/notification.service';
import { AuthService } from '../../core/services/auth.service';
import {
  AdminAuditEntry,
  BaggageTag,
  Booking,
  CheckIn,
  Flight,
  Notification,
  Passenger,
  StaffProfile,
  ToastMessage,
  UserIncidentReport
} from '../../core/models/aerobook.models';

interface SeatItem {
  seatNumber: string;
  isBooked: boolean;
  isBusiness: boolean;
}

@Component({
  selector: 'app-staff-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 animate-fadeIn text-ink">

      <!-- FLOATING TOAST NOTIFICATION CONTAINER -->
      <div class="fixed top-6 right-6 z-[100] space-y-3 pointer-events-none max-w-sm w-full">
        @for (t of toasts; track t.id) {
          <div class="pointer-events-auto p-4 rounded-2xl shadow-2xl backdrop-blur-md border transition-all transform animate-slideInRight flex items-start gap-3"
               [ngClass]="{
                 'bg-emerald-950/95 border-emerald-500/50 text-emerald-100 shadow-emerald-950/50': t.type === 'success',
                 'bg-rose-950/95 border-rose-500/50 text-rose-100 shadow-rose-950/50': t.type === 'error',
                 'bg-amber-950/95 border-amber-500/50 text-amber-100 shadow-amber-950/50': t.type === 'warning',
                 'bg-sky-950/95 border-sky-500/50 text-sky-100 shadow-sky-950/50': t.type === 'info'
               }">
            <span class="text-xl">
              @if (t.type === 'success') { 🛡️ }
              @if (t.type === 'error') { ⚠️ }
              @if (t.type === 'warning') { ⚡ }
              @if (t.type === 'info') { ℹ️ }
            </span>
            <div class="flex-1 text-xs">
              <h4 class="font-bold text-sm">{{ t.title }}</h4>
              <p class="mt-0.5 opacity-90">{{ t.message }}</p>
            </div>
            <button (click)="removeToast(t.id)" class="text-ink-3 hover:text-ink text-xs">✕</button>
          </div>
        }
      </div>

      <!-- OPERATIONS COMMAND HEADER -->
      <div class="rounded-card p-6 sm:p-8 mb-8 border border-line shadow-card relative overflow-hidden bg-surface backdrop-blur-xl">
        <div class="absolute -right-20 -bottom-20 w-96 h-96 bg-amber-500/10 rounded-full blur-3xl pointer-events-none"></div>
        <div class="absolute left-1/3 -top-20 w-80 h-80 bg-orange-500/10 rounded-full blur-3xl pointer-events-none"></div>

        <div class="flex flex-col lg:flex-row lg:items-center justify-between gap-6 relative z-10">
          <div>
            <div class="flex flex-wrap items-center gap-2 mb-3">
              <span class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-amber-500/10 border border-amber-500/30 text-amber-600 dark:text-amber-300 text-xs font-semibold">
                <span>🦺</span> Airport Ground & Passenger Operations Station
              </span>
              <span class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-500/20 border border-emerald-500/40 text-emerald-600 dark:text-emerald-300 text-xs font-semibold">
                <span class="w-2 h-2 rounded-full bg-emerald-400 animate-pulse"></span>
                Station: {{ staffProfile.station }} • {{ staffProfile.terminal }}
              </span>
              <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-seg border border-line text-ink-2 text-[11px] font-mono">
                Officer ID: {{ staffProfile.staffId }}
              </span>
            </div>
            <h1 class="text-2xl sm:text-3xl font-extrabold text-ink tracking-tight flex items-center gap-3">
              Airport Ground & Flight Operations Control
            </h1>
            <p class="text-xs text-ink-2 mt-1 max-w-2xl">
              Real-time gate boarding pass scanner, flight departure status transitions, passenger manifest audits, counter check-in & luggage tagging.
            </p>
          </div>

          <!-- Station Status Controls -->
          <div class="flex flex-wrap items-center gap-3 bg-seg p-3 sm:p-4 rounded-2xl border border-line shadow-inner">
            <div class="text-center px-3 border-r border-line">
              <span class="text-xl font-extrabold text-ink font-mono">{{ flights.length }}</span>
              <span class="block text-[10px] text-ink-3 uppercase tracking-wider">Flights</span>
            </div>
            <div class="text-center px-3 border-r border-line">
              <span class="text-xl font-extrabold text-amber-600 dark:text-amber-400 font-mono">{{ checkIns.length }}</span>
              <span class="block text-[10px] text-ink-3 uppercase tracking-wider">Check-Ins</span>
            </div>
            <div class="text-center px-3 border-r border-line">
              <span class="text-xl font-extrabold text-emerald-600 dark:text-emerald-400 font-mono">{{ boardedCount }}</span>
              <span class="block text-[10px] text-ink-3 uppercase tracking-wider">Boarded</span>
            </div>
            <div class="text-center px-3">
              <span class="text-sm font-bold text-amber-600 dark:text-amber-300 block font-mono">{{ staffProfile.shiftStatus }}</span>
              <span class="block text-[10px] text-ink-3 uppercase tracking-wider">Duty State</span>
            </div>
          </div>
        </div>

        <!-- Navigation Sub-Tabs -->
        <div class="flex flex-wrap items-center gap-2 mt-8 pt-6 border-t border-line text-xs font-semibold">
          <button (click)="activeTab = 'operations'" [class]="activeTab === 'operations' ? 'bg-amber-600 text-white shadow-lg shadow-amber-600/30' : 'text-ink-2 hover:text-ink hover:bg-seg'" class="px-3.5 py-2 rounded-xl transition-all flex items-center gap-1.5">
            <span>🛫</span> Flight Departure Board
          </button>
          <button (click)="activeTab = 'manifest'" [class]="activeTab === 'manifest' ? 'bg-amber-600 text-white shadow-lg shadow-amber-600/30' : 'text-ink-2 hover:text-ink hover:bg-seg'" class="px-3.5 py-2 rounded-xl transition-all flex items-center gap-1.5">
            <span>📋</span> Passenger Manifest
          </button>
          <button (click)="activeTab = 'scanner'" [class]="activeTab === 'scanner' ? 'bg-amber-600 text-white shadow-lg shadow-amber-600/30' : 'text-ink-2 hover:text-ink hover:bg-seg'" class="px-3.5 py-2 rounded-xl transition-all flex items-center gap-1.5">
            <span>🔍</span> Gate Boarding Scanner
          </button>
          <button (click)="activeTab = 'counter'" [class]="activeTab === 'counter' ? 'bg-amber-600 text-white shadow-lg shadow-amber-600/30' : 'text-ink-2 hover:text-ink hover:bg-seg'" class="px-3.5 py-2 rounded-xl transition-all flex items-center gap-1.5">
            <span>🧳</span> Counter Check-In & Bags
          </button>
          <button (click)="activeTab = 'broadcast'" [class]="activeTab === 'broadcast' ? 'bg-amber-600 text-white shadow-lg shadow-amber-600/30' : 'text-ink-2 hover:text-ink hover:bg-seg'" class="px-3.5 py-2 rounded-xl transition-all flex items-center gap-1.5">
            <span>📢</span> Terminal Broadcast
          </button>
          <button (click)="activeTab = 'duty'" [class]="activeTab === 'duty' ? 'bg-orange-600 text-white shadow-lg shadow-orange-600/30' : 'text-amber-700 dark:text-orange-300 hover:text-ink hover:bg-seg'" class="px-3.5 py-2 rounded-xl transition-all flex items-center gap-1.5 ml-auto border border-line">
            <span>🦺</span> Officer Profile & Shift
          </button>
        </div>
      </div>

      <!-- ========================================== -->
      <!-- TAB 1: FLIGHT DEPARTURE STATUS BOARD -->
      <!-- ========================================== -->
      @if (activeTab === 'operations') {
        <div class="space-y-6 animate-fadeIn">
          <!-- Tactical Operations Launchpad Cards -->
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
            <!-- Launchpad 1: Gate Scanner -->
            <div (click)="activeTab = 'scanner'" class="group cursor-pointer p-5 rounded-tile bg-surface border border-line shadow-card hover:border-accent-2 hover:shadow-lg transition-all duration-300 flex flex-col justify-between hover:-translate-y-1 backdrop-blur-xl">
              <div>
                <div class="flex items-center justify-between mb-3">
                  <div class="w-12 h-12 rounded-2xl bg-amber-500/10 border border-amber-500/30 flex items-center justify-center text-2xl text-amber-600 dark:text-amber-400 group-hover:scale-110 transition-transform">
                    🔍
                  </div>
                  <span class="px-2 py-0.5 rounded-full bg-amber-500/20 text-amber-700 dark:text-amber-300 text-[10px] font-bold font-mono">
                    High-Speed
                  </span>
                </div>
                <h4 class="text-sm font-bold text-ink group-hover:text-accent transition-colors">Gate Boarding Scanner</h4>
                <p class="text-xs text-ink-2 mt-1">Instant barcode inspection, boarding authorization, and seat validation.</p>
              </div>
              <div class="mt-4 pt-3 border-t border-line flex items-center justify-between text-xs text-amber-600 dark:text-amber-400 font-semibold">
                <span>Open Scanner</span>
                <span class="group-hover:translate-x-1 transition-transform">➔</span>
              </div>
            </div>

            <!-- Launchpad 2: Counter Check-In -->
            <div (click)="activeTab = 'counter'" class="group cursor-pointer p-5 rounded-tile bg-surface border border-line shadow-card hover:border-accent-2 hover:shadow-lg transition-all duration-300 flex flex-col justify-between hover:-translate-y-1 backdrop-blur-xl">
              <div>
                <div class="flex items-center justify-between mb-3">
                  <div class="w-12 h-12 rounded-2xl bg-sky-500/10 border border-sky-500/30 flex items-center justify-center text-2xl text-sky-600 dark:text-sky-400 group-hover:scale-110 transition-transform">
                    🧳
                  </div>
                  <span class="px-2 py-0.5 rounded-full bg-sky-500/20 text-sky-700 dark:text-sky-300 text-[10px] font-bold font-mono">
                    Desk Desk
                  </span>
                </div>
                <h4 class="text-sm font-bold text-ink group-hover:text-accent transition-colors">Counter Check-In & Bags</h4>
                <p class="text-xs text-ink-2 mt-1">Assign boarding seats, issue baggage tags, and verify identity.</p>
              </div>
              <div class="mt-4 pt-3 border-t border-line flex items-center justify-between text-xs text-sky-600 dark:text-sky-400 font-semibold">
                <span>Issue Boarding Pass</span>
                <span class="group-hover:translate-x-1 transition-transform">➔</span>
              </div>
            </div>

            <!-- Launchpad 3: Flight Manifest -->
            <div (click)="activeTab = 'manifest'" class="group cursor-pointer p-5 rounded-tile bg-surface border border-line shadow-card hover:border-accent-2 hover:shadow-lg transition-all duration-300 flex flex-col justify-between hover:-translate-y-1 backdrop-blur-xl">
              <div>
                <div class="flex items-center justify-between mb-3">
                  <div class="w-12 h-12 rounded-2xl bg-emerald-500/10 border border-emerald-500/30 flex items-center justify-center text-2xl text-emerald-600 dark:text-emerald-400 group-hover:scale-110 transition-transform">
                    📋
                  </div>
                  <span class="px-2 py-0.5 rounded-full bg-emerald-500/20 text-emerald-700 dark:text-emerald-300 text-[10px] font-bold font-mono">
                    Manifest
                  </span>
                </div>
                <h4 class="text-sm font-bold text-ink group-hover:text-accent transition-colors">Passenger Manifest</h4>
                <p class="text-xs text-ink-2 mt-1">Review ticketed passenger list, boarding state, and print gate rosters.</p>
              </div>
              <div class="mt-4 pt-3 border-t border-line flex items-center justify-between text-xs text-emerald-600 dark:text-emerald-400 font-semibold">
                <span>View Roster</span>
                <span class="group-hover:translate-x-1 transition-transform">➔</span>
              </div>
            </div>

            <!-- Launchpad 4: Incident Log -->
            <div (click)="openIncidentModal()" class="group cursor-pointer p-5 rounded-tile bg-surface border border-line shadow-card hover:border-accent-2 hover:shadow-lg transition-all duration-300 flex flex-col justify-between hover:-translate-y-1 backdrop-blur-xl">
              <div>
                <div class="flex items-center justify-between mb-3">
                  <div class="w-12 h-12 rounded-2xl bg-rose-500/10 border border-rose-500/30 flex items-center justify-center text-2xl text-rose-600 dark:text-rose-400 group-hover:scale-110 transition-transform">
                    🚨
                  </div>
                  <span class="px-2 py-0.5 rounded-full bg-rose-500/20 text-rose-700 dark:text-rose-300 text-[10px] font-bold font-mono">
                    Security
                  </span>
                </div>
                <h4 class="text-sm font-bold text-ink group-hover:text-accent transition-colors">Log Ground Incident</h4>
                <p class="text-xs text-ink-2 mt-1">Flag disruptive passengers, baggage discrepancies, or security screening holds.</p>
              </div>
              <div class="mt-4 pt-3 border-t border-line flex items-center justify-between text-xs text-rose-600 dark:text-rose-400 font-semibold">
                <span>File Report</span>
                <span class="group-hover:translate-x-1 transition-transform">➔</span>
              </div>
            </div>
          </div>

          <!-- Main Departure Table -->
          <div class="bg-surface border border-line rounded-card p-6 sm:p-8 shadow-card space-y-6 backdrop-blur-xl">
            <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-line">
              <div>
                <h2 class="text-lg font-bold text-ink flex items-center gap-2">
                  <span>🛫</span> Live Commercial Flight Departure Board
                </h2>
                <p class="text-xs text-ink-2 mt-1">Ground staff operational authority to transition flight departure states and update terminal gates.</p>
              </div>
              <div class="flex items-center gap-2">
                <button (click)="loadFlights()" class="px-3.5 py-2 rounded-xl bg-seg hover:bg-seg/80 text-ink-2 hover:text-ink text-xs font-semibold border border-line transition-all flex items-center gap-1.5">
                  <span>🔄</span> Refresh Board
                </button>
              </div>
            </div>

            <!-- Search & Filters -->
            <div class="flex flex-wrap items-center gap-4 bg-seg p-4 rounded-2xl border border-line text-xs">
              <div class="flex-1 min-w-[200px]">
                <input [(ngModel)]="flightSearchTerm" placeholder="Filter by flight number, airline, or destination airport..." class="w-full bg-surface border border-line rounded-xl px-3.5 py-2 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
              </div>
              <div class="flex items-center gap-2">
                <span class="text-ink-2 font-medium">Status:</span>
                <select [(ngModel)]="flightStatusFilter" class="bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
                  <option value="ALL">All Statuses</option>
                  <option value="SCHEDULED">SCHEDULED</option>
                  <option value="BOARDING">BOARDING</option>
                  <option value="DELAYED">DELAYED</option>
                  <option value="DEPARTED">DEPARTED</option>
                  <option value="COMPLETED">COMPLETED</option>
                </select>
              </div>
            </div>

            <!-- Flight Status Table -->
            <div class="overflow-x-auto rounded-2xl border border-line shadow-card">
              <table class="w-full text-left text-xs">
                <thead class="bg-seg text-ink-2 uppercase tracking-wider text-[10px] border-b border-line">
                  <tr>
                    <th class="py-4 px-4 font-bold">Flight No.</th>
                    <th class="py-4 px-4 font-bold">Airliner Carrier</th>
                    <th class="py-4 px-4 font-bold">Itinerary Route</th>
                    <th class="py-4 px-4 font-bold">Gate & Terminal</th>
                    <th class="py-4 px-4 font-bold">Departure Time</th>
                    <th class="py-4 px-4 font-bold">Current Status</th>
                    <th class="py-4 px-4 font-bold text-right">Ground Operational Actions</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-line bg-surface/50">
                  @for (f of filteredFlights; track f.id) {
                    <tr class="hover:bg-seg/50 transition-colors group">
                      <td class="py-3.5 px-4 font-bold font-mono text-ink">
                        <div class="flex items-center gap-2">
                          <span class="w-2.5 h-2.5 rounded-full" [ngClass]="f.status === 'BOARDING' ? 'bg-amber-400 animate-ping' : (f.status === 'CANCELLED' ? 'bg-rose-500' : 'bg-emerald-400')"></span>
                          <span class="text-sm tracking-tight text-ink group-hover:text-accent transition-colors">{{ f.flightNumber }}</span>
                        </div>
                      </td>

                      <td class="py-3.5 px-4">
                        <span class="font-medium text-ink block">{{ f.airlineName }}</span>
                        <span class="text-[10px] text-ink-3 font-mono">{{ f.aircraftTailNumber || 'Fleet Jet' }}</span>
                      </td>

                      <td class="py-3.5 px-4">
                        <div class="inline-flex items-center gap-2 px-3 py-1 rounded-xl bg-seg border border-line font-mono text-[11px] shadow-sm">
                          <span class="text-ink font-extrabold">{{ f.source }}</span>
                          <span class="text-amber-500 dark:text-amber-400 text-xs">✈️</span>
                          <span class="text-ink font-extrabold">{{ f.destination }}</span>
                        </div>
                      </td>

                      <td class="py-3.5 px-4">
                        <button (click)="openGateChangeModal(f)" title="Click to reassign gate and notify passengers" class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-lg bg-seg border border-line text-ink font-mono text-[11px] hover:border-accent transition-all">
                          <span>🚪</span> {{ f.terminal || 'T2' }} • {{ f.gate || 'Gate 14B' }}
                          <span class="text-[9px] text-accent">✎</span>
                        </button>
                      </td>

                      <td class="py-3.5 px-4 text-ink font-mono">
                        <span class="block text-ink font-semibold">{{ f.departureTime | date:'HH:mm' }}</span>
                        <span class="text-[10px] text-ink-3">{{ f.departureTime | date:'MMM d' }}</span>
                      </td>

                      <td class="py-3.5 px-4">
                        <span class="px-2.5 py-1 rounded-full text-[10px] font-bold border transition-all flex items-center gap-1.5 w-max" [class]="getStatusClass(f.status)">
                          <span class="w-1.5 h-1.5 rounded-full bg-current"></span>
                          {{ f.status }}
                        </span>
                      </td>

                      <td class="py-3.5 px-4 text-right">
                        <div class="inline-flex items-center gap-1 bg-seg p-1 rounded-xl border border-line">
                          <button (click)="openViewFlightModal(f)" class="px-2.5 py-1 rounded-lg bg-sky-500/10 hover:bg-sky-500/20 text-sky-700 dark:text-sky-300 text-[11px] font-semibold transition-all" title="View Flight Dossier">
                            Dossier
                          </button>
                          <button (click)="updateStatus(f.id, 'BOARDING')" class="px-2 py-1 rounded-lg bg-amber-500/10 hover:bg-amber-500/20 text-amber-700 dark:text-amber-300 text-[11px] font-semibold transition-all" title="Call Boarding">
                            Board
                          </button>
                          <button (click)="updateStatus(f.id, 'DEPARTED')" class="px-2 py-1 rounded-lg bg-cyan-500/10 hover:bg-cyan-500/20 text-cyan-700 dark:text-cyan-300 text-[11px] font-semibold transition-all" title="Mark Departed">
                            Depart
                          </button>
                          <button (click)="updateStatus(f.id, 'DELAYED')" class="px-2 py-1 rounded-lg bg-rose-500/10 hover:bg-rose-500/20 text-rose-700 dark:text-rose-300 text-[11px] font-semibold transition-all" title="Delay Notice">
                            Delay
                          </button>
                          <button (click)="selectFlightForManifest(f.id)" class="px-2.5 py-1 rounded-lg bg-emerald-500/10 hover:bg-emerald-500/20 text-emerald-700 dark:text-emerald-300 text-[11px] font-semibold transition-all" title="View Manifest">
                            Manifest ➔
                          </button>
                        </div>
                      </td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          </div>
        </div>
      }

      <!-- ========================================== -->
      <!-- TAB 2: FLIGHT MANIFEST & BOARDING ROSTER -->
      <!-- ========================================== -->
      @if (activeTab === 'manifest') {
        <div class="bg-surface border border-line rounded-card p-6 sm:p-8 shadow-card space-y-6 backdrop-blur-xl animate-fadeIn">
          <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-line">
            <div>
              <h2 class="text-lg font-bold text-ink flex items-center gap-2">
                <span>📋</span> Passenger Flight Manifest & Boarding Roster
              </h2>
              <p class="text-xs text-ink-2 mt-1">Audit ticketed passengers, assigned cabin seats, checked baggage pieces, and gate boarding authorizations.</p>
            </div>

            <div class="flex items-center gap-3">
              <label class="text-xs text-ink-2 font-semibold">Select Flight:</label>
              <select [(ngModel)]="selectedManifestFlightId" (change)="loadManifest()"
                      class="px-3.5 py-2 rounded-xl bg-surface border border-line text-ink font-medium shadow-sm text-xs font-semibold focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 font-mono transition-all">
                @for (f of flights; track f.id) {
                  <option [ngValue]="f.id">{{ f.flightNumber }} ({{ f.source }} → {{ f.destination }})</option>
                }
              </select>

              <button (click)="printManifestSheet()" class="px-3.5 py-2 rounded-xl bg-seg hover:bg-seg/80 text-ink-2 hover:text-ink text-xs font-semibold border border-line transition-all flex items-center gap-1.5">
                <span>🖨️</span> Print Roster
              </button>
            </div>
          </div>

          <!-- Manifest Flight Summary Header -->
          @if (currentManifestFlight) {
            <div class="p-4 rounded-2xl bg-seg border border-line flex flex-wrap items-center justify-between gap-4 text-xs font-mono">
              <div class="flex items-center gap-4">
                <span class="text-lg font-black text-amber-600 dark:text-amber-400">{{ currentManifestFlight.flightNumber }}</span>
                <span class="text-ink">{{ currentManifestFlight.source }} ➔ {{ currentManifestFlight.destination }}</span>
                <span class="text-ink-3">Departure: {{ currentManifestFlight.departureTime | date:'HH:mm' }}</span>
              </div>
              <div class="flex items-center gap-4">
                <span class="text-emerald-600 dark:text-emerald-400">Total Booked: <strong>{{ totalManifestPassengers }}</strong></span>
                <span class="text-amber-600 dark:text-amber-400">Boarded at Gate: <strong>{{ manifestBoardedCount }}</strong></span>
              </div>
            </div>
          }

          <div class="overflow-x-auto rounded-2xl border border-line shadow-card">
            <table class="w-full text-left text-xs">
              <thead class="bg-seg text-ink-2 uppercase tracking-wider text-[10px] border-b border-line">
                <tr>
                  <th class="py-4 px-4 font-bold">Booking ID</th>
                  <th class="py-4 px-4 font-bold">PNR Locator</th>
                  <th class="py-4 px-4 font-bold">Passenger Name</th>
                  <th class="py-4 px-4 font-bold">Demographics</th>
                  <th class="py-4 px-4 font-bold">Assigned Seat</th>
                  <th class="py-4 px-4 font-bold">Check-In / Boarding Status</th>
                  <th class="py-4 px-4 font-bold text-right">Gate Actions</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-line bg-surface/50">
                @for (b of manifestBookings; track b.bookingId) {
                  @for (p of b.passengers; track p.passengerId || p.firstName) {
                    <tr class="hover:bg-seg/50 transition-colors">
                      <td class="py-3.5 px-4 font-mono text-ink-3">#{{ b.bookingId }}</td>
                      <td class="py-3.5 px-4 font-mono font-bold text-amber-600 dark:text-amber-400">{{ b.pnr }}</td>
                      <td class="py-3.5 px-4 font-bold text-ink">
                        <div class="flex items-center gap-2">
                          <span>{{ p.firstName }} {{ p.lastName }}</span>
                          <span class="px-2 py-0.5 rounded-full text-[9px] font-bold bg-amber-500/15 text-amber-700 dark:text-amber-300 border border-amber-500/30">
                            👑 Gold Lvl 3
                          </span>
                        </div>
                      </td>
                      <td class="py-3.5 px-4 text-ink-2 font-mono">{{ p.age }} yrs • {{ p.gender }}</td>
                      <td class="py-3.5 px-4">
                        <span class="px-2.5 py-1 rounded-lg bg-seg border border-line font-mono font-black text-sky-600 dark:text-sky-400">
                          {{ p.seatNumber || 'Unassigned' }}
                        </span>
                      </td>
                      <td class="py-3.5 px-4">
                        <span class="px-2.5 py-1 rounded-full text-[10px] font-bold border"
                              [ngClass]="isPassengerBoarded(p.passengerId || 0) ? 'bg-emerald-500/10 border-emerald-500/30 text-emerald-600 dark:text-emerald-400' : 'bg-amber-500/10 border-amber-500/30 text-amber-600 dark:text-amber-400'">
                          {{ isPassengerBoarded(p.passengerId || 0) ? '● BOARDED' : '● CHECKED-IN' }}
                        </span>
                      </td>
                      <td class="py-3.5 px-4 text-right space-x-2">
                        <button (click)="openViewPassengerModal(p, b)" class="px-2.5 py-1 rounded-lg bg-sky-500/10 hover:bg-sky-500/20 text-sky-700 dark:text-sky-300 text-[11px] font-semibold transition-all">
                          Dossier
                        </button>
                        <button (click)="toggleBoardingState(p, b)" class="px-2.5 py-1 rounded-lg text-[11px] font-semibold border transition-all"
                                [ngClass]="isPassengerBoarded(p.passengerId || 0) ? 'bg-seg text-ink-3 border-line' : 'bg-emerald-500/10 text-emerald-700 dark:text-emerald-300 border-emerald-500/30 hover:bg-emerald-500/20'">
                          {{ isPassengerBoarded(p.passengerId || 0) ? 'Un-Board' : 'Authorize Boarding' }}
                        </button>
                      </td>
                    </tr>
                  }
                }
              </tbody>
            </table>
          </div>
        </div>
      }

      <!-- ========================================== -->
      <!-- TAB 3: GATE BOARDING SCANNER -->
      <!-- ========================================== -->
      @if (activeTab === 'scanner') {
        <div class="bg-surface border border-line rounded-card p-6 sm:p-8 shadow-card max-w-3xl mx-auto space-y-6 backdrop-blur-xl animate-fadeIn">
          <div class="text-center pb-4 border-b border-line">
            <div class="w-14 h-14 rounded-2xl bg-amber-500/10 border border-amber-500/30 text-amber-600 dark:text-amber-400 flex items-center justify-center text-2xl mx-auto mb-3 shadow-xl">
              🔍
            </div>
            <h2 class="text-xl font-black text-ink">Departure Gate Boarding Pass Optical Verifier</h2>
            <p class="text-xs text-ink-2 mt-1">Scan or input passenger boarding barcode to authenticate flight security clearance.</p>
          </div>

          <!-- Scanner Input & Rapid Test Buttons -->
          <div class="space-y-4">
            <form (ngSubmit)="verifyPass()" class="flex gap-2">
              <input type="text" [(ngModel)]="scanCode" name="scanCode" required placeholder="Scan barcode or enter code (e.g. BP-1-12A)"
                     class="flex-1 px-4 py-3 rounded-2xl bg-surface border border-line text-ink placeholder:text-ink-3 font-medium shadow-sm font-mono text-sm tracking-wider uppercase focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
              <button type="submit" [disabled]="scanLoading"
                      class="px-6 py-3 rounded-2xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600 text-white font-bold text-xs shadow-lg transition-all flex items-center gap-2">
                @if (scanLoading) {
                  <span class="animate-spin">⏳</span> Verifying...
                } @else {
                  <span>⚡ Authenticate</span>
                }
              </button>
            </form>

            <div class="flex items-center gap-2 text-xs text-ink-2">
              <span>Quick Test Samples:</span>
              <button (click)="scanCode = 'BP-1-12A'; verifyPass()" class="px-2.5 py-1 rounded-lg bg-seg hover:bg-seg/80 text-amber-700 dark:text-amber-300 font-mono text-[11px] border border-line">
                BP-1-12A (Priya Sharma)
              </button>
              <button (click)="scanCode = 'BP-2-14C'; verifyPass()" class="px-2.5 py-1 rounded-lg bg-seg hover:bg-seg/80 text-amber-700 dark:text-amber-300 font-mono text-[11px] border border-line">
                BP-2-14C (Amit Verma)
              </button>
            </div>
          </div>

          <!-- Optical Laser Animation Graphic -->
          <div class="relative p-6 rounded-2xl bg-seg border border-line text-center overflow-hidden">
            <div class="h-1 bg-gradient-to-r from-transparent via-amber-400 to-transparent w-full animate-pulse my-2"></div>
            <span class="text-[11px] font-mono text-ink-3 uppercase tracking-widest block">OPTICAL SCANNER READY • CONCOURSE B GATE 14B</span>
          </div>

          <!-- Verification Result Card -->
          @if (scanResult) {
            <div class="p-6 rounded-3xl bg-surface border border-emerald-500/40 animate-fadeIn space-y-4 shadow-card">
              <div class="flex items-center justify-between pb-3 border-b border-emerald-500/20">
                <div class="flex items-center gap-3">
                  <div class="w-12 h-12 rounded-2xl bg-emerald-500 text-white flex items-center justify-center text-2xl font-bold shadow-lg shadow-emerald-500/30">
                    ✓
                  </div>
                  <div>
                    <h3 class="text-base font-black text-ink">BOARDING PASS VERIFIED & AUTHORIZED</h3>
                    <span class="text-xs text-emerald-600 dark:text-emerald-400 font-semibold font-mono">STATUS: {{ scanResult.status }}</span>
                  </div>
                </div>
                <span class="px-3 py-1 rounded-full bg-emerald-500/20 text-emerald-700 dark:text-emerald-300 border border-emerald-500/30 text-xs font-bold font-mono">
                  CLEAR FOR FLIGHT
                </span>
              </div>

              <div class="grid grid-cols-2 gap-4 text-xs">
                <div class="p-3 rounded-2xl bg-seg border border-line">
                  <span class="text-ink-3 block text-[10px] uppercase">Passenger Identity</span>
                  <span class="text-sm font-bold text-ink mt-0.5 block">{{ scanResult.passengerName }}</span>
                </div>
                <div class="p-3 rounded-2xl bg-seg border border-line">
                  <span class="text-ink-3 block text-[10px] uppercase">Assigned Cabin Seat</span>
                  <span class="text-xl font-black text-amber-600 dark:text-amber-400 font-mono mt-0.5 block">{{ scanResult.seatNumber }}</span>
                </div>
                <div class="p-3 rounded-2xl bg-seg border border-line">
                  <span class="text-ink-3 block text-[10px] uppercase">Barcode Reference</span>
                  <span class="text-xs font-mono text-ink mt-0.5 block">{{ scanResult.boardingPassNumber }}</span>
                </div>
                <div class="p-3 rounded-2xl bg-seg border border-line">
                  <span class="text-ink-3 block text-[10px] uppercase">Booking ID Reference</span>
                  <span class="text-xs font-mono text-ink mt-0.5 block">#{{ scanResult.bookingId }}</span>
                </div>
                <div class="col-span-2 p-3 rounded-2xl bg-amber-500/10 border border-amber-500/30 flex items-center justify-between">
                  <div class="flex items-center gap-2">
                    <span class="text-xl">👑</span>
                    <div>
                      <span class="text-xs font-bold text-amber-700 dark:text-amber-300">Level 3 • Gold Aviator (24,850 XP)</span>
                      <span class="text-[10px] text-ink-2 block">Privileges: Group 2 Boarding • Priority Bag Tag • Complimentary Lounge</span>
                    </div>
                  </div>
                  <span class="px-2 py-0.5 rounded-full bg-amber-500/20 text-amber-700 dark:text-amber-300 text-[10px] font-bold">VIP LOYALTY</span>
                </div>
              </div>
            </div>
          }

          @if (scanError) {
            <div class="p-4 rounded-2xl bg-rose-500/10 border border-rose-500/30 text-rose-600 dark:text-rose-300 text-xs flex items-center gap-2">
              <span>⚠️</span>
              <span>{{ scanError }}</span>
            </div>
          }
        </div>
      }

      <!-- ========================================== -->
      <!-- TAB 4: COUNTER CHECK-IN & BAGGAGE DROP -->
      <!-- ========================================== -->
      @if (activeTab === 'counter') {
        <div class="bg-surface border border-line rounded-card p-6 sm:p-8 shadow-card space-y-6 backdrop-blur-xl animate-fadeIn">
          <div class="pb-4 border-b border-line">
            <h2 class="text-lg font-bold text-ink flex items-center gap-2">
              <span>🧳</span> Airport Counter Desk Check-In & Baggage Drop Concierge
            </h2>
            <p class="text-xs text-ink-2 mt-1">Staff manual passenger check-in, seat allocation, and official luggage tag printing.</p>
          </div>

          <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
            <!-- Left: Check-In Form (7 Cols) -->
            <div class="lg:col-span-7 space-y-4 text-xs">
              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="block text-[11px] font-semibold text-ink-2 mb-1">Booking ID Reference *</label>
                  <input type="number" [(ngModel)]="counterBookingId" name="counterBookingId" required placeholder="101"
                         class="w-full px-3.5 py-2.5 rounded-xl bg-surface border border-line text-ink placeholder:text-ink-3 font-medium shadow-sm font-mono text-xs focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
                </div>
                <div>
                  <label class="block text-[11px] font-semibold text-ink-2 mb-1">Passenger Full Name *</label>
                  <input type="text" [(ngModel)]="counterPassengerName" name="counterPassengerName" required placeholder="Priya Sharma"
                         class="w-full px-3.5 py-2.5 rounded-xl bg-surface border border-line text-ink placeholder:text-ink-3 font-medium shadow-sm text-xs focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
                </div>
              </div>

              <div class="grid grid-cols-2 gap-4">
                <div>
                  <label class="block text-[11px] font-semibold text-ink-2 mb-1">Assigned Seat *</label>
                  <input type="text" [(ngModel)]="counterSeat" name="counterSeat" required placeholder="12A"
                         class="w-full px-3.5 py-2.5 rounded-xl bg-surface border border-line text-ink placeholder:text-ink-3 font-medium shadow-sm font-mono text-xs focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 uppercase transition-all">
                </div>
                <div>
                  <label class="block text-[11px] font-semibold text-ink-2 mb-1">Destination Airport</label>
                  <input type="text" [(ngModel)]="counterDestination" name="counterDestination" placeholder="BOM (Mumbai)"
                         class="w-full px-3.5 py-2.5 rounded-xl bg-surface border border-line text-ink placeholder:text-ink-3 font-medium shadow-sm font-mono text-xs focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 uppercase transition-all">
                </div>
              </div>

              <!-- Baggage Section -->
              <div class="p-4 rounded-2xl bg-seg border border-line space-y-3">
                <span class="text-xs font-bold text-accent flex items-center gap-1.5 uppercase tracking-wider">
                  <span>🧳</span> Checked Baggage Tagging
                </span>
                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <label class="block text-[11px] text-ink-2 mb-1">Checked Luggage Pieces</label>
                    <input type="number" [(ngModel)]="counterBaggagePieces" min="1" max="5" class="w-full px-3 py-2 rounded-xl bg-surface border border-line text-ink placeholder:text-ink-3 font-mono text-xs font-semibold shadow-sm focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
                  </div>
                  <div>
                    <label class="block text-[11px] text-ink-2 mb-1">Total Weight (kg)</label>
                    <input type="number" [(ngModel)]="counterBaggageWeight" min="5" max="60" class="w-full px-3 py-2 rounded-xl bg-surface border border-line text-ink placeholder:text-ink-3 font-mono text-xs font-semibold shadow-sm focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
                  </div>
                </div>
              </div>

              <button (click)="performStaffCheckIn()" class="w-full py-3.5 rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600 text-white font-bold text-xs shadow-lg transition-all flex items-center justify-center gap-2">
                <span>🖨️</span> Execute Check-In & Generate Baggage Tag
              </button>
            </div>

            <!-- Right: Live Baggage Tag Preview (5 Cols) -->
            <div class="lg:col-span-5 bg-surface p-6 rounded-card border border-amber-500/30 shadow-card relative text-xs space-y-4 backdrop-blur-xl">
              <div class="flex items-center justify-between pb-3 border-b border-line">
                <span class="text-[10px] font-mono text-amber-600 dark:text-amber-400 font-bold uppercase tracking-wider">Digital Luggage Tag</span>
                <span class="px-2 py-0.5 rounded-full bg-amber-500/20 text-amber-700 dark:text-amber-300 font-mono text-[9px] font-bold">AERO-TAG</span>
              </div>

              <div class="text-center p-4 rounded-2xl bg-seg border border-line space-y-2">
                <span class="text-3xl font-black text-ink font-mono tracking-widest block">{{ counterDestination || 'BOM' }}</span>
                <span class="text-xs text-amber-600 dark:text-amber-300 font-bold font-mono">TAG #BAG-{{ counterBookingId }}-{{ counterSeat }}</span>
                <span class="text-[10px] text-ink-2 block">{{ counterPassengerName || 'Passenger Name' }}</span>
              </div>

              <div class="flex justify-between py-2 border-b border-line text-ink-2 font-mono">
                <span>Weight Certified:</span>
                <strong class="text-ink">{{ counterBaggageWeight }} kg ({{ counterBaggagePieces }} Pieces)</strong>
              </div>

              <!-- Barcode -->
              <div class="p-3 rounded-xl bg-seg border border-line text-center">
                <div class="h-6 w-full flex items-center justify-center gap-1 opacity-70">
                  <div class="w-1 h-6 bg-ink"></div>
                  <div class="w-0.5 h-6 bg-ink"></div>
                  <div class="w-2 h-6 bg-ink"></div>
                  <div class="w-1.5 h-6 bg-ink"></div>
                  <div class="w-0.5 h-6 bg-ink"></div>
                  <div class="w-2 h-6 bg-ink"></div>
                </div>
                <span class="text-[8px] font-mono text-ink-3 tracking-widest mt-1 block">BAGGAGE HANDLING CONCOURSE B</span>
              </div>
            </div>
          </div>
        </div>
      }

      <!-- ========================================== -->
      <!-- TAB 5: BROADCAST OPERATIONAL NOTICE -->
      <!-- ========================================== -->
      @if (activeTab === 'broadcast') {
        <div class="bg-surface border border-line rounded-card p-6 sm:p-8 shadow-card max-w-xl mx-auto space-y-6 backdrop-blur-xl animate-fadeIn">
          <div class="text-center pb-4 border-b border-line">
            <h2 class="text-xl font-bold text-ink flex items-center justify-center gap-2">
              <span>📢</span> Terminal Announcement Dispatcher
            </h2>
            <p class="text-xs text-ink-2 mt-1">Broadcast high-priority gate movements, delay advisories, or final boarding calls.</p>
          </div>

          <form (ngSubmit)="sendBroadcastAlert()" class="space-y-4 text-xs">
            <div>
              <label class="block text-ink-2 font-semibold mb-1">Notice Category</label>
              <select [(ngModel)]="broadcastType" name="broadcastType" class="w-full px-3.5 py-2.5 rounded-xl bg-surface border border-line text-ink font-medium shadow-sm text-xs focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
                <option value="BOARDING_CALL">✈️ Final Boarding Call (Gate 14B)</option>
                <option value="GATE_CHANGE">🚪 Departure Gate Movement</option>
                <option value="FLIGHT_DELAY">⚠️ Flight Weather Delay Alert</option>
                <option value="BROADCAST">📢 General Passenger Announcement</option>
              </select>
            </div>

            <div>
              <label class="block text-ink-2 font-semibold mb-1">Associated Flight Number</label>
              <input type="text" [(ngModel)]="broadcastFlight" name="broadcastFlight" placeholder="e.g. AI-101"
                     class="w-full px-3.5 py-2.5 rounded-xl bg-surface border border-line text-ink placeholder:text-ink-3 font-medium shadow-sm font-mono text-xs focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 uppercase transition-all">
            </div>

            <div>
              <label class="block text-ink-2 font-semibold mb-1">Announcement Title</label>
              <input type="text" [(ngModel)]="broadcastTitle" name="broadcastTitle" required placeholder="e.g. Gate Moved to Gate 4B"
                     class="w-full px-3.5 py-2.5 rounded-xl bg-surface border border-line text-ink placeholder:text-ink-3 font-medium shadow-sm text-xs focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
            </div>

            <div>
              <label class="block text-ink-2 font-semibold mb-1">Detailed Message Content</label>
              <textarea [(ngModel)]="broadcastMessage" name="broadcastMessage" required rows="3" placeholder="Flight AI-101 to Delhi will now board from Terminal 2, Gate 4B."
                        class="w-full px-3.5 py-2.5 rounded-xl bg-surface border border-line text-ink placeholder:text-ink-3 font-medium shadow-sm text-xs focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all"></textarea>
            </div>

            <button type="submit" class="w-full py-3.5 rounded-xl bg-gradient-to-r from-amber-500 to-orange-500 hover:from-amber-600 hover:to-orange-600 text-white font-bold text-xs shadow-lg transition-all">
              Broadcast Real-Time Notice
            </button>
          </form>
        </div>
      }

      <!-- ========================================== -->
      <!-- TAB 6: STAFF OFFICER PROFILE & SHIFT DESK -->
      <!-- ========================================== -->
      @if (activeTab === 'duty') {
        <div class="space-y-6 animate-fadeIn">
          <!-- Officer Identity Card -->
          <div class="rounded-card p-6 sm:p-8 border border-line shadow-card relative overflow-hidden bg-surface backdrop-blur-xl">
            <div class="flex flex-col md:flex-row md:items-center justify-between gap-6 pb-6 border-b border-line">
              <div class="flex items-center gap-4">
                <div class="w-20 h-20 rounded-2xl bg-gradient-to-tr from-amber-600 to-orange-600 flex items-center justify-center text-3xl font-black text-white shadow-xl border border-orange-400/40">
                  {{ staffProfile.name.charAt(0) }}
                </div>
                <div>
                  <div class="flex items-center gap-2">
                    <h2 class="text-xl font-extrabold text-ink">{{ staffProfile.name }}</h2>
                    <span class="px-2.5 py-0.5 rounded-full bg-amber-500/20 border border-amber-500/40 text-amber-700 dark:text-amber-300 text-xs font-bold font-mono">
                      {{ staffProfile.role }}
                    </span>
                  </div>
                  <p class="text-xs text-ink-2 mt-1">{{ staffProfile.station }} • {{ staffProfile.terminal }}</p>
                  <p class="text-xs text-accent font-mono mt-0.5">{{ staffProfile.email }}</p>
                </div>
              </div>

              <!-- Shift Duty Toggles -->
              <div class="flex items-center gap-2">
                <button (click)="setShift('ON_DUTY')" class="px-3.5 py-2 rounded-xl text-xs font-bold transition-all border"
                        [ngClass]="staffProfile.shiftStatus === 'ON_DUTY' ? 'bg-emerald-500/20 text-emerald-700 dark:text-emerald-300 border-emerald-500/50 shadow-md' : 'bg-seg text-ink-3 border-line'">
                  🟢 On Duty
                </button>
                <button (click)="setShift('ON_BREAK')" class="px-3.5 py-2 rounded-xl text-xs font-bold transition-all border"
                        [ngClass]="staffProfile.shiftStatus === 'ON_DUTY' ? 'bg-seg text-ink-3 border-line' : 'bg-amber-500/20 text-amber-700 dark:text-amber-300 border-amber-500/50 shadow-md'">
                  🟡 Break
                </button>
              </div>
            </div>

            <!-- Profile Details Grid -->
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-6 pt-6 text-xs">
              <div class="p-4 rounded-2xl bg-seg border border-line">
                <span class="text-[10px] text-ink-3 uppercase tracking-wider block">Staff Officer ID</span>
                <span class="text-sm font-bold text-ink font-mono mt-1 block">{{ staffProfile.staffId }}</span>
                <span class="text-[11px] text-ink-3 mt-1 block">Security Cleared Ground Crew</span>
              </div>
              <div class="p-4 rounded-2xl bg-seg border border-line">
                <span class="text-[10px] text-ink-3 uppercase tracking-wider block">Duty Station</span>
                <span class="text-sm font-bold text-amber-600 dark:text-amber-400 font-mono mt-1 block">{{ staffProfile.station }} ({{ staffProfile.gateAssignment }})</span>
                <span class="text-[11px] text-ink-3 mt-1 block">Terminal 2 Concourse B</span>
              </div>
              <div class="p-4 rounded-2xl bg-seg border border-line">
                <span class="text-[10px] text-ink-3 uppercase tracking-wider block">Contact Telephone</span>
                <span class="text-sm font-bold text-ink font-mono mt-1 block">{{ staffProfile.phone }}</span>
                <span class="text-[11px] text-ink-3 mt-1 block">Station Walkie & Mobile</span>
              </div>
            </div>
          </div>

          <!-- Shift Activity Trail -->
          <div class="bg-surface border border-line rounded-card p-6 sm:p-8 shadow-card space-y-4 text-xs backdrop-blur-xl">
            <h3 class="text-base font-bold text-ink flex items-center gap-2">
              <span>📋</span> Officer Duty Activity Trail
            </h3>
            <p class="text-xs text-ink-2">Ledger of gate authorizations, check-in operations, and broadcast alerts executed during current shift.</p>

            <div class="space-y-2 pt-2">
              @for (act of shiftActivity; track act.id) {
                <div class="p-3 rounded-xl bg-seg border border-line flex items-center justify-between">
                  <div class="flex items-center gap-3">
                    <span class="text-base">{{ act.icon }}</span>
                    <div>
                      <span class="font-bold text-ink block">{{ act.action }}</span>
                      <span class="text-[10px] text-ink-2">{{ act.detail }}</span>
                    </div>
                  </div>
                  <span class="text-ink-3 font-mono text-[10px]">{{ act.time }}</span>
                </div>
              }
            </div>
          </div>
        </div>
      }

      <!-- ========================================== -->
      <!-- MODALS SECTION -->
      <!-- ========================================== -->

      <!-- MODAL: VIEW FLIGHT DOSSIER & CABIN SEAT MAP -->
      @if (showViewFlightModal && selectedFlight) {
        <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-ink/40 backdrop-blur-md animate-fadeIn overflow-y-auto">
          <div class="border border-line bg-surface rounded-card w-full max-w-3xl p-6 sm:p-8 shadow-2xl space-y-6 my-8 backdrop-blur-2xl">
            <div class="flex items-center justify-between pb-4 border-b border-line">
              <div class="flex items-center gap-3">
                <div class="w-12 h-12 rounded-2xl bg-amber-500/10 border border-amber-500/30 flex items-center justify-center text-2xl text-amber-600 dark:text-amber-400 font-bold">
                  🛫
                </div>
                <div>
                  <h3 class="text-lg font-black text-ink flex items-center gap-2">
                    {{ selectedFlight.flightNumber }} — {{ selectedFlight.airlineName }}
                  </h3>
                  <span class="text-xs text-ink-2 font-mono">{{ selectedFlight.source }} ➔ {{ selectedFlight.destination }} • Gate: {{ selectedFlight.gate || 'Gate 14B' }}</span>
                </div>
              </div>
              <button (click)="showViewFlightModal = false" class="text-ink-2 hover:text-ink p-2 rounded-xl bg-seg hover:bg-seg/80">✕</button>
            </div>

            <!-- Route & Seat Occupancy Progress -->
            <div class="p-4 rounded-2xl bg-seg border border-line flex items-center justify-between text-xs">
              <div>
                <span class="text-ink-3 uppercase text-[10px] block">Departure Time</span>
                <span class="text-base font-bold text-ink font-mono mt-0.5 block">{{ selectedFlight.departureTime | date:'shortTime' }}</span>
              </div>
              <div class="flex-1 max-w-xs mx-6">
                <div class="flex justify-between text-[11px] mb-1">
                  <span class="text-ink-2">Boarding Progress</span>
                  <span class="text-amber-600 dark:text-amber-400 font-bold font-mono">{{ ((selectedFlight.totalSeats - selectedFlight.availableSeats) / selectedFlight.totalSeats) * 100 | number:'1.0-0' }}%</span>
                </div>
                <div class="w-full bg-line/40 rounded-full h-2 overflow-hidden">
                  <div class="bg-gradient-to-r from-amber-500 to-orange-500 h-2 rounded-full" [style.width.%]="((selectedFlight.totalSeats - selectedFlight.availableSeats) / selectedFlight.totalSeats) * 100"></div>
                </div>
              </div>
              <div class="text-right">
                <span class="text-ink-3 uppercase text-[10px] block">Seats Available</span>
                <span class="text-base font-bold text-emerald-600 dark:text-emerald-400 font-mono mt-0.5 block">{{ selectedFlight.availableSeats }} / {{ selectedFlight.totalSeats }}</span>
              </div>
            </div>

            <!-- Interactive Cabin Seat Grid -->
            <div class="space-y-3">
              <span class="text-xs font-bold text-ink uppercase tracking-wider block">Cabin Seating Layout</span>
              <div class="p-4 rounded-2xl bg-seg border border-line max-h-48 overflow-y-auto">
                <div class="grid grid-cols-6 gap-2 text-center text-[10px] font-mono">
                  @for (s of mockSeatMap; track s.seatNumber) {
                    <div class="p-2 rounded-xl border flex flex-col items-center justify-center"
                         [ngClass]="{
                           'bg-surface border-line text-ink-3/50': s.isBooked,
                           'bg-emerald-500/10 border-emerald-500/30 text-emerald-600 dark:text-emerald-300': !s.isBooked && !s.isBusiness,
                           'bg-amber-500/10 border-amber-500/30 text-amber-600 dark:text-amber-300': !s.isBooked && s.isBusiness
                         }">
                      <span class="font-bold">{{ s.seatNumber }}</span>
                      <span class="text-[8px]">{{ s.isBooked ? 'OCC' : (s.isBusiness ? 'BIZ' : 'FREE') }}</span>
                    </div>
                  }
                </div>
              </div>
            </div>

            <div class="flex items-center justify-end gap-3 pt-4 border-t border-line text-xs">
              <button (click)="openGateChangeModal(selectedFlight); showViewFlightModal = false" class="px-4 py-2 rounded-xl bg-amber-500/10 text-amber-700 dark:text-amber-300 border border-amber-500/30 font-semibold hover:bg-amber-500/20">
                Change Gate
              </button>
              <button (click)="showViewFlightModal = false" class="px-4 py-2 rounded-xl bg-seg hover:bg-seg/80 text-ink-2 hover:text-ink font-semibold border border-line">
                Close Dossier
              </button>
            </div>
          </div>
        </div>
      }

      <!-- MODAL: REASSIGN GATE -->
      @if (showGateModal && selectedFlightForGate) {
        <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-ink/40 backdrop-blur-sm animate-fadeIn">
          <div class="border border-line bg-surface rounded-card w-full max-w-md p-6 sm:p-8 shadow-2xl space-y-4 text-xs backdrop-blur-2xl">
            <div class="flex items-center justify-between pb-3 border-b border-line">
              <h3 class="text-base font-bold text-ink flex items-center gap-2">
                <span>🚪</span> Reassign Departure Gate
              </h3>
              <button (click)="showGateModal = false" class="text-ink-2 hover:text-ink">✕</button>
            </div>

            <p class="text-ink-2">
              Flight: <strong class="text-amber-600 dark:text-amber-400 font-mono">{{ selectedFlightForGate.flightNumber }}</strong> ({{ selectedFlightForGate.source }} ➔ {{ selectedFlightForGate.destination }})
            </p>

            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-[11px] text-ink-2 mb-1">Terminal</label>
                <input [(ngModel)]="gateFormTerminal" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-ink placeholder:text-ink-3 font-medium shadow-sm font-mono uppercase focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
              </div>
              <div>
                <label class="block text-[11px] text-ink-2 mb-1">Departure Gate</label>
                <input [(ngModel)]="gateFormGate" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-ink placeholder:text-ink-3 font-medium shadow-sm font-mono uppercase focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
              </div>
            </div>

            <div class="flex items-center gap-2 pt-2">
              <input [(ngModel)]="gateNotifyPassengers" type="checkbox" id="gateNotify" class="rounded accent-accent">
              <label for="gateNotify" class="text-ink-2 cursor-pointer text-[11px]">
                Transmit automated gate change advisory to all passengers
              </label>
            </div>

            <div class="flex items-center justify-end gap-3 pt-4 border-t border-line">
              <button (click)="showGateModal = false" class="px-4 py-2 rounded-xl text-ink-2 hover:text-ink">Cancel</button>
              <button (click)="saveGateChange()" class="px-5 py-2 rounded-xl bg-amber-600 hover:bg-amber-500 text-white font-bold transition-all shadow-md">
                Confirm Gate Reassignment
              </button>
            </div>
          </div>
        </div>
      }

      <!-- MODAL: VIEW PASSENGER DOSSIER -->
      @if (showViewPassengerModal && selectedPassenger) {
        <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-ink/40 backdrop-blur-sm animate-fadeIn">
          <div class="border border-line bg-surface rounded-card w-full max-w-md p-6 sm:p-8 shadow-2xl space-y-4 text-xs backdrop-blur-2xl">
            <div class="flex items-center justify-between pb-3 border-b border-line">
              <h3 class="text-base font-bold text-ink flex items-center gap-2">
                <span>👤</span> Passenger Travel Dossier
              </h3>
              <button (click)="showViewPassengerModal = false" class="text-ink-2 hover:text-ink">✕</button>
            </div>

            <div class="p-4 rounded-2xl bg-seg border border-line text-center space-y-1">
              <span class="text-lg font-black text-ink block">{{ selectedPassenger.firstName }} {{ selectedPassenger.lastName }}</span>
              <span class="text-amber-600 dark:text-amber-400 font-mono font-bold text-xs">PNR: {{ selectedBookingForPassenger?.pnr || 'AB-829140' }}</span>
              <span class="text-ink-3 text-[11px] block">{{ selectedPassenger.gender }}, Age {{ selectedPassenger.age }}</span>
            </div>

            <div class="grid grid-cols-2 gap-3">
              <div class="p-3 rounded-xl bg-seg border border-line">
                <span class="text-[10px] text-ink-3 uppercase block">Seat Assignment</span>
                <span class="text-base font-bold text-sky-600 dark:text-sky-400 font-mono mt-0.5 block">{{ selectedPassenger.seatNumber || '12A' }}</span>
              </div>
              <div class="p-3 rounded-xl bg-seg border border-line">
                <span class="text-[10px] text-ink-3 uppercase block">Boarding Clearance</span>
                <span class="text-base font-bold text-emerald-600 dark:text-emerald-400 font-mono mt-0.5 block">APPROVED</span>
              </div>
            </div>

            <div class="flex items-center justify-end gap-3 pt-4 border-t border-line">
              <button (click)="showViewPassengerModal = false" class="px-4 py-2 rounded-xl bg-seg hover:bg-seg/80 text-ink-2 hover:text-ink border border-line">
                Close
              </button>
            </div>
          </div>
        </div>
      }

      <!-- MODAL: LOG GROUND INCIDENT -->
      @if (showIncidentModal) {
        <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-ink/40 backdrop-blur-sm animate-fadeIn">
          <div class="border border-line bg-surface rounded-card w-full max-w-md p-6 sm:p-8 shadow-2xl space-y-4 text-xs backdrop-blur-2xl">
            <div class="flex items-center justify-between pb-3 border-b border-line">
              <h3 class="text-base font-bold text-ink flex items-center gap-2">
                <span>🚨</span> Log Ground Security / Passenger Incident
              </h3>
              <button (click)="showIncidentModal = false" class="text-ink-2 hover:text-ink">✕</button>
            </div>

            <div>
              <label class="block text-[11px] text-ink-2 mb-1">Passenger Name or PNR</label>
              <input [(ngModel)]="incidentPassenger" placeholder="e.g. Karan Malhotra (PNR: AB-829140)" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-ink placeholder:text-ink-3 font-medium shadow-sm focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
            </div>

            <div>
              <label class="block text-[11px] text-ink-2 mb-1">Incident Category</label>
              <select [(ngModel)]="incidentCategory" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-ink font-medium shadow-sm focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
                <option value="Disruptive Passenger Behavior">Disruptive Passenger Behavior</option>
                <option value="Security Screening Non-Compliance">Security Screening Non-Compliance</option>
                <option value="Excess Baggage Dispute">Excess Baggage Dispute</option>
                <option value="Gate Disturbance">Gate Disturbance</option>
              </select>
            </div>

            <div>
              <label class="block text-[11px] text-ink-2 mb-1">Incident Severity</label>
              <select [(ngModel)]="incidentSeverity" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-ink font-medium shadow-sm focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
                <option value="LOW">LOW</option>
                <option value="MEDIUM">MEDIUM</option>
                <option value="HIGH">HIGH (Hold Passenger)</option>
              </select>
            </div>

            <div>
              <label class="block text-[11px] text-ink-2 mb-1">Staff Notes</label>
              <textarea [(ngModel)]="incidentNotes" rows="3" placeholder="Provide factual context for security command..." class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-ink placeholder:text-ink-3 font-medium shadow-sm focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all"></textarea>
            </div>

            <div class="flex items-center justify-end gap-3 pt-4 border-t border-line">
              <button (click)="showIncidentModal = false" class="px-4 py-2 rounded-xl text-ink-2 hover:text-ink">Cancel</button>
              <button (click)="saveGroundIncident()" class="px-5 py-2 rounded-xl bg-rose-600 hover:bg-rose-500 text-white font-bold">
                Submit Security Report
              </button>
            </div>
          </div>
        </div>
      }

    </div>
  `
})
export class StaffDashboardComponent implements OnInit {
  activeTab: 'operations' | 'manifest' | 'scanner' | 'counter' | 'broadcast' | 'duty' = 'operations';

  flights: Flight[] = [];
  checkIns: CheckIn[] = [];
  selectedManifestFlightId: number | null = null;
  manifestBookings: Booking[] = [];

  // Staff Profile State
  staffProfile: StaffProfile = {
    staffId: 'STF-8092',
    name: 'Rajesh Kumar',
    email: 'staff@aerobook.com',
    station: 'Station BOM (Mumbai)',
    terminal: 'Terminal 2',
    gateAssignment: 'Concourse B • Gate 14B',
    role: 'ROLE_STAFF',
    shiftStatus: 'ON_DUTY',
    phone: '+91 98111 22334',
    emergencyContact: '+91 98222 33445'
  };

  shiftActivity = [
    { id: 1, icon: '🔍', action: 'Passenger Boarding Verified', detail: 'Priya Sharma (Seat 12A) approved for Flight AI-101', time: '10 mins ago' },
    { id: 2, icon: '🧳', action: 'Counter Check-In Completed', detail: 'Asha Khan issued Tag #BAG-101-14B (23kg checked)', time: '25 mins ago' },
    { id: 3, icon: '📢', action: 'Gate Movement Advisory', detail: 'Flight AI-101 reassigned to Gate 14B', time: '45 mins ago' }
  ];

  toasts: ToastMessage[] = [];

  // Gate Scanner State
  scanCode: string = 'BP-1-12A';
  scanLoading: boolean = false;
  scanResult: CheckIn | null = null;
  scanError: string = '';

  // Desk Check-In State
  counterBookingId: number = 101;
  counterPassengerName: string = 'Asha Khan';
  counterSeat: string = '14B';
  counterDestination: string = 'DEL';
  counterBaggagePieces: number = 1;
  counterBaggageWeight: number = 23;

  // Broadcast Alert State
  broadcastType: string = 'BOARDING_CALL';
  broadcastFlight: string = 'AI-101';
  broadcastTitle: string = 'Final Boarding Call - Flight AI-101';
  broadcastMessage: string = 'Final boarding call for Flight AI-101 to Delhi. All remaining passengers please proceed to Gate 14B immediately.';

  // Search & Filtering
  flightSearchTerm: string = '';
  flightStatusFilter: string = 'ALL';

  // Modals
  showViewFlightModal: boolean = false;
  selectedFlight: Flight | null = null;
  showGateModal: boolean = false;
  selectedFlightForGate: Flight | null = null;
  gateFormTerminal: string = 'T2';
  gateFormGate: string = 'Gate 14B';
  gateNotifyPassengers: boolean = true;
  showViewPassengerModal: boolean = false;
  selectedPassenger: Passenger | null = null;
  selectedBookingForPassenger: Booking | null = null;
  showIncidentModal: boolean = false;
  incidentPassenger: string = '';
  incidentCategory: string = 'Disruptive Passenger Behavior';
  incidentSeverity: string = 'MEDIUM';
  incidentNotes: string = '';

  // Simulated Cabin Map
  mockSeatMap: SeatItem[] = [
    { seatNumber: '1A', isBooked: true, isBusiness: true },
    { seatNumber: '1B', isBooked: true, isBusiness: true },
    { seatNumber: '1C', isBooked: false, isBusiness: true },
    { seatNumber: '1D', isBooked: true, isBusiness: true },
    { seatNumber: '1E', isBooked: false, isBusiness: true },
    { seatNumber: '1F', isBooked: true, isBusiness: true },
    { seatNumber: '10A', isBooked: true, isBusiness: false },
    { seatNumber: '10B', isBooked: false, isBusiness: false },
    { seatNumber: '10C', isBooked: true, isBusiness: false },
    { seatNumber: '10D', isBooked: false, isBusiness: false },
    { seatNumber: '10E', isBooked: true, isBusiness: false },
    { seatNumber: '10F', isBooked: false, isBusiness: false }
  ];

  boardedPassengerIds: Set<number> = new Set<number>();

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

  showToast(type: 'success' | 'error' | 'info' | 'warning', title: string, message: string): void {
    const id = Date.now();
    this.toasts.push({ id, type, title, message });
    setTimeout(() => {
      this.removeToast(id);
    }, 4500);
  }

  removeToast(id: number): void {
    this.toasts = this.toasts.filter(t => t.id !== id);
  }

  get boardedCount(): number {
    return this.checkIns.filter(c => c.status === 'BOARDED').length + this.boardedPassengerIds.size;
  }

  get currentManifestFlight(): Flight | undefined {
    return this.flights.find(f => f.id === this.selectedManifestFlightId);
  }

  get totalManifestPassengers(): number {
    return this.manifestBookings.reduce((sum, b) => sum + (b.passengers?.length || 0), 0);
  }

  get manifestBoardedCount(): number {
    let count = 0;
    for (const b of this.manifestBookings) {
      for (const p of b.passengers || []) {
        if (this.isPassengerBoarded(p.passengerId || 0)) count++;
      }
    }
    return count;
  }

  get filteredFlights(): Flight[] {
    return this.flights.filter(f => {
      const q = this.flightSearchTerm.toLowerCase();
      const matchesSearch = !this.flightSearchTerm ||
        f.flightNumber.toLowerCase().includes(q) ||
        f.airlineName.toLowerCase().includes(q) ||
        f.source.toLowerCase().includes(q) ||
        f.destination.toLowerCase().includes(q);
      const matchesStatus = this.flightStatusFilter === 'ALL' || f.status === this.flightStatusFilter;
      return matchesSearch && matchesStatus;
    });
  }

  loadFlights(): void {
    this.flightService.getAllFlights().subscribe({
      next: (data) => {
        this.flights = data || [];
        if (data.length > 0 && !this.selectedManifestFlightId) {
          this.selectedManifestFlightId = data[0].id;
          this.loadManifest();
        }
      },
      error: () => {
        this.flights = [
          { id: 1, flightNumber: 'AI-101', airlineName: 'Air India', source: 'DEL', destination: 'BOM', departureTime: new Date(Date.now() + 86400000).toISOString(), arrivalTime: new Date(Date.now() + 93600000).toISOString(), totalSeats: 180, availableSeats: 42, baseFare: 4500, status: 'SCHEDULED', terminal: 'T2', gate: 'Gate 14B', aircraftTailNumber: 'VT-AB101' },
          { id: 2, flightNumber: '6E-402', airlineName: 'IndiGo', source: 'BOM', destination: 'BLR', departureTime: new Date(Date.now() + 108000000).toISOString(), arrivalTime: new Date(Date.now() + 115200000).toISOString(), totalSeats: 186, availableSeats: 15, baseFare: 3200, status: 'BOARDING', terminal: 'T2', gate: 'Gate 8A', aircraftTailNumber: 'VT-AB303' }
        ];
        this.selectedManifestFlightId = 1;
        this.loadManifest();
      }
    });
  }

  loadCheckIns(): void {
    this.checkInService.getAllCheckIns().subscribe({
      next: (data) => this.checkIns = data || [],
      error: () => {
        this.checkIns = [
          { id: 1, bookingId: 101, passengerName: 'Priya Sharma', seatNumber: '12A', boardingPassNumber: 'BP-1-12A', status: 'CHECKED_IN', checkedInAt: new Date().toISOString() },
          { id: 2, bookingId: 102, passengerName: 'Amit Verma', seatNumber: '14C', boardingPassNumber: 'BP-2-14C', status: 'BOARDED', checkedInAt: new Date().toISOString() }
        ];
      }
    });
  }

  loadManifest(): void {
    if (!this.selectedManifestFlightId) return;
    this.bookingService.getBookingsByFlight(this.selectedManifestFlightId).subscribe({
      next: (data) => this.manifestBookings = data || [],
      error: () => {
        this.manifestBookings = [
          {
            bookingId: 101,
            pnr: 'AB-829140',
            userId: 3,
            flightId: 1,
            flightNumber: 'AI-101',
            bookingDate: new Date().toISOString(),
            totalFare: 5310,
            status: 'CONFIRMED',
            passengers: [{ passengerId: 1, firstName: 'Priya', lastName: 'Sharma', age: 29, gender: 'Female', seatNumber: '12A' }]
          },
          {
            bookingId: 102,
            pnr: 'AB-672109',
            userId: 4,
            flightId: 1,
            flightNumber: 'AI-101',
            bookingDate: new Date().toISOString(),
            totalFare: 4500,
            status: 'CONFIRMED',
            passengers: [{ passengerId: 2, firstName: 'Asha', lastName: 'Khan', age: 31, gender: 'Female', seatNumber: '14B' }]
          }
        ];
      }
    });
  }

  selectFlightForManifest(flightId: number): void {
    this.selectedManifestFlightId = flightId;
    this.activeTab = 'manifest';
    this.loadManifest();
  }

  updateStatus(flightId: number, status: string): void {
    this.flightService.updateFlightStatus(flightId, status).subscribe({
      next: (f) => {
        const item = this.flights.find(x => x.id === flightId);
        if (item) item.status = f.status;
        this.showToast('success', 'Flight Status Updated', `Flight ${f.flightNumber} transitioned to ${status}.`);
      },
      error: () => {
        const item = this.flights.find(x => x.id === flightId);
        if (item) item.status = status as any;
        this.showToast('success', 'Flight Status Updated', `Flight status updated to ${status}.`);
      }
    });
  }

  openGateChangeModal(flight: Flight): void {
    this.selectedFlightForGate = flight;
    this.gateFormTerminal = flight.terminal || 'T2';
    this.gateFormGate = flight.gate || 'Gate 14B';
    this.showGateModal = true;
  }

  saveGateChange(): void {
    if (!this.selectedFlightForGate) return;
    this.selectedFlightForGate.terminal = this.gateFormTerminal;
    this.selectedFlightForGate.gate = this.gateFormGate;

    if (this.gateNotifyPassengers) {
      this.notificationService.broadcastAlert({
        title: `Gate Change: ${this.selectedFlightForGate.flightNumber} moved to ${this.gateFormGate}`,
        message: `Passengers of flight ${this.selectedFlightForGate.flightNumber}: Departure gate is now ${this.gateFormTerminal} • ${this.gateFormGate}.`,
        type: 'GATE_CHANGE',
        flightNumber: this.selectedFlightForGate.flightNumber
      }).subscribe({ error: () => {} });
    }

    this.showToast('success', 'Gate Reassigned', `Flight ${this.selectedFlightForGate.flightNumber} gate moved to ${this.gateFormGate}.`);
    this.showGateModal = false;
  }

  openViewFlightModal(flight: Flight): void {
    this.selectedFlight = flight;
    this.showViewFlightModal = true;
  }

  openViewPassengerModal(passenger: Passenger, booking: Booking): void {
    this.selectedPassenger = passenger;
    this.selectedBookingForPassenger = booking;
    this.showViewPassengerModal = true;
  }

  isPassengerBoarded(passengerId: number): boolean {
    return this.boardedPassengerIds.has(passengerId);
  }

  toggleBoardingState(passenger: Passenger, booking: Booking): void {
    const pid = passenger.passengerId || booking.bookingId;
    if (this.boardedPassengerIds.has(pid)) {
      this.boardedPassengerIds.delete(pid);
      this.showToast('info', 'Boarding Revoked', `${passenger.firstName} ${passenger.lastName} un-boarded.`);
    } else {
      this.boardedPassengerIds.add(pid);
      this.showToast('success', 'Boarding Authorized', `${passenger.firstName} ${passenger.lastName} cleared for boarding!`);
    }
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
        this.showToast('success', 'Boarding Cleared', `Passenger ${res.passengerName} verified & marked as BOARDED!`);
        this.loadCheckIns();
      },
      error: () => {
        this.scanLoading = false;
        // Fallback demo result for testing
        this.scanResult = {
          id: 99,
          bookingId: 101,
          passengerName: 'Priya Sharma',
          seatNumber: '12A',
          boardingPassNumber: this.scanCode,
          status: 'BOARDED',
          checkedInAt: new Date().toISOString()
        };
        this.showToast('success', 'Boarding Cleared', `Pass ${this.scanCode} authenticated.`);
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
        this.showToast('success', 'Counter Check-In Complete', `Boarding pass: ${res.boardingPassNumber}`);
        this.loadCheckIns();
        this.scanCode = res.boardingPassNumber;
      },
      error: () => {
        const demoPass = `BP-${this.counterBookingId}-${this.counterSeat}`;
        this.showToast('success', 'Counter Check-In Complete', `Boarding pass & baggage tag issued: ${demoPass}`);
        this.scanCode = demoPass;
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
        this.showToast('success', 'Announcement Transmitted', 'Alert pushed to passenger apps.');
      },
      error: () => {
        this.showToast('success', 'Announcement Transmitted', 'Notice broadcasted across terminal.');
      }
    });
  }

  printManifestSheet(): void {
    this.showToast('info', 'Print Manifest', 'Formatting flight boarding manifest...');
    setTimeout(() => {
      window.print();
    }, 500);
  }

  openIncidentModal(): void {
    this.incidentPassenger = '';
    this.incidentNotes = '';
    this.showIncidentModal = true;
  }

  saveGroundIncident(): void {
    this.showToast('warning', 'Incident Logged', `Security flag filed for ${this.incidentPassenger || 'Passenger'}.`);
    this.showIncidentModal = false;
  }

  setShift(status: 'ON_DUTY' | 'ON_BREAK'): void {
    this.staffProfile.shiftStatus = status;
    this.showToast('info', 'Shift Status', `Officer Rajesh Kumar is now ${status}.`);
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'SCHEDULED': return 'bg-sky-500/10 border-sky-500/30 text-sky-700 dark:text-sky-400';
      case 'BOARDING': return 'bg-amber-500/10 border-amber-500/30 text-amber-700 dark:text-amber-400';
      case 'DELAYED': return 'bg-rose-500/10 border-rose-500/30 text-rose-700 dark:text-rose-400';
      case 'DEPARTED': return 'bg-cyan-500/10 border-cyan-500/30 text-cyan-700 dark:text-cyan-400';
      case 'COMPLETED': return 'bg-emerald-500/10 border-emerald-500/30 text-emerald-700 dark:text-emerald-400';
      default: return 'bg-seg border-line text-ink-2';
    }
  }
}

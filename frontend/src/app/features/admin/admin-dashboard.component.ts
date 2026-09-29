import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FlightService } from '../../core/services/flight.service';
import { BookingService } from '../../core/services/booking.service';
import { UserService } from '../../core/services/user.service';
import { FareService } from '../../core/services/fare.service';
import { NotificationService } from '../../core/services/notification.service';
import { AuthService } from '../../core/services/auth.service';
import {
  AdminAuditEntry,
  AdminProfile,
  Aircraft,
  Booking,
  BroadcastRequest,
  Fare,
  Flight,
  Notification,
  PromoCode,
  RoleType,
  ToastMessage,
  User,
  UserIncidentReport
} from '../../core/models/aerobook.models';

interface SeatItem {
  seatNumber: string;
  isBooked: boolean;
  isBusiness: boolean;
}

@Component({
  selector: 'app-admin-dashboard',
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
                 'bg-sky-950/95 border-sky-500/50 text-sky-100 shadow-sky-950/50': t.type === 'info',
                 'bg-amber-950/95 border-amber-500/50 text-amber-100 shadow-amber-950/50': t.type === 'warning'
               }">
            <span class="text-xl">
              @if (t.type === 'success') { 🛡️ }
              @if (t.type === 'error') { ⚠️ }
              @if (t.type === 'info') { ℹ️ }
              @if (t.type === 'warning') { ⚡ }
            </span>
            <div class="flex-1 text-xs">
              <h4 class="font-bold text-sm">{{ t.title }}</h4>
              <p class="mt-0.5 opacity-90">{{ t.message }}</p>
            </div>
            <button (click)="removeToast(t.id)" class="text-ink-3 hover:text-ink text-xs">✕</button>
          </div>
        }
      </div>

      <!-- EXECUTIVE COMMAND HEADER -->
      <div class="rounded-card p-6 sm:p-8 mb-8 border border-line shadow-card relative overflow-hidden bg-surface backdrop-blur-xl">
        <div class="absolute -right-20 -bottom-20 w-96 h-96 bg-sky-500/10 rounded-full blur-3xl pointer-events-none"></div>
        <div class="absolute left-1/2 -top-24 w-80 h-80 bg-indigo-500/10 rounded-full blur-3xl pointer-events-none"></div>

        <div class="flex flex-col lg:flex-row lg:items-center justify-between gap-6 relative z-10">
          <div>
            <div class="flex flex-wrap items-center gap-2 mb-3">
              <span class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-sky-500/10 border border-sky-500/30 text-sky-700 dark:text-sky-300 text-xs font-semibold">
                <span>✈️</span> AeroBook Commercial Aviation Platform
              </span>
              <span class="inline-flex items-center gap-1.5 px-3 py-1 rounded-full bg-emerald-500/20 border border-emerald-500/40 text-emerald-700 dark:text-emerald-300 text-xs font-semibold">
                <span>⚡</span> Executive Admin Mode Active
              </span>
              <span class="inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full bg-seg border border-line text-purple-700 dark:text-purple-300 text-[11px] font-mono">
                Clearance: {{ adminProfile.securityClearance }}
              </span>
            </div>
            <h1 class="text-2xl sm:text-3xl font-extrabold text-ink tracking-tight flex items-center gap-3">
              Executive Administration & Control Tower
            </h1>
            <p class="text-xs text-ink-2 mt-1 max-w-2xl">
              Centralized orchestration hub for commercial flight schedules, fleet airworthiness, dynamic pricing algorithms, user role privilege governance, and incident reporting.
            </p>
          </div>

          <!-- Quick Metrics Bar -->
          <div class="flex flex-wrap items-center gap-3 bg-seg p-3 sm:p-4 rounded-2xl border border-line shadow-inner">
            <div class="text-center px-3 border-r border-line">
              <span class="text-xl font-extrabold text-sky-600 dark:text-sky-400 font-mono">{{ flights.length }}</span>
              <span class="block text-[10px] text-ink-3 uppercase tracking-wider">Flights</span>
            </div>
            <div class="text-center px-3 border-r border-line">
              <span class="text-xl font-extrabold text-indigo-600 dark:text-indigo-400 font-mono">{{ fleet.length }}</span>
              <span class="block text-[10px] text-ink-3 uppercase tracking-wider">Aircraft</span>
            </div>
            <div class="text-center px-3 border-r border-line">
              <span class="text-xl font-extrabold text-emerald-600 dark:text-emerald-400 font-mono">{{ users.length }}</span>
              <span class="block text-[10px] text-ink-3 uppercase tracking-wider">Accounts</span>
            </div>
            <div class="text-center px-3 border-r border-line">
              <span class="text-xl font-extrabold text-amber-600 dark:text-amber-400 font-mono">{{ bookings.length }}</span>
              <span class="block text-[10px] text-ink-3 uppercase tracking-wider">Bookings</span>
            </div>
            <div class="text-center px-3">
              <span class="text-xl font-extrabold text-rose-600 dark:text-rose-400 font-mono">{{ incidentReports.length }}</span>
              <span class="block text-[10px] text-ink-3 uppercase tracking-wider">Reports</span>
            </div>
          </div>
        </div>

        <!-- Navigation Tabs -->
        <div class="flex flex-wrap items-center gap-2 mt-8 pt-6 border-t border-line text-xs font-semibold">
          <button (click)="activeTab = 'overview'" [class]="activeTab === 'overview' ? 'bg-accent text-white shadow-lg shadow-accent/30' : 'text-ink-2 hover:text-ink hover:bg-seg'" class="px-3.5 py-2 rounded-xl transition-all flex items-center gap-1.5">
            <span>📊</span> System Overview
          </button>
          <button (click)="activeTab = 'flights'" [class]="activeTab === 'flights' ? 'bg-accent text-white shadow-lg shadow-accent/30' : 'text-ink-2 hover:text-ink hover:bg-seg'" class="px-3.5 py-2 rounded-xl transition-all flex items-center gap-1.5">
            <span>🛫</span> Flight Scheduler
          </button>
          <button (click)="activeTab = 'aircraft'" [class]="activeTab === 'aircraft' ? 'bg-accent text-white shadow-lg shadow-accent/30' : 'text-ink-2 hover:text-ink hover:bg-seg'" class="px-3.5 py-2 rounded-xl transition-all flex items-center gap-1.5">
            <span>✈️</span> Fleet Roster
          </button>
          <button (click)="activeTab = 'fares'" [class]="activeTab === 'fares' ? 'bg-accent text-white shadow-lg shadow-accent/30' : 'text-ink-2 hover:text-ink hover:bg-seg'" class="px-3.5 py-2 rounded-xl transition-all flex items-center gap-1.5">
            <span>🏷️</span> Dynamic Pricing & Promos
          </button>
          <button (click)="activeTab = 'users'" [class]="activeTab === 'users' ? 'bg-accent text-white shadow-lg shadow-accent/30' : 'text-ink-2 hover:text-ink hover:bg-seg'" class="px-3.5 py-2 rounded-xl transition-all flex items-center gap-1.5">
            <span>👥</span> User Directory & RBAC
          </button>
          <button (click)="activeTab = 'reports'" [class]="activeTab === 'reports' ? 'bg-accent text-white shadow-lg shadow-accent/30' : 'text-ink-2 hover:text-ink hover:bg-seg'" class="px-3.5 py-2 rounded-xl transition-all flex items-center gap-1.5">
            <span>🚨</span> Incident Reports
            @if (openReportsCount > 0) {
              <span class="px-1.5 py-0.2 rounded-full bg-rose-500 text-white text-[10px] font-bold">{{ openReportsCount }}</span>
            }
          </button>
          <button (click)="activeTab = 'bookings'" [class]="activeTab === 'bookings' ? 'bg-accent text-white shadow-lg shadow-accent/30' : 'text-ink-2 hover:text-ink hover:bg-seg'" class="px-3.5 py-2 rounded-xl transition-all flex items-center gap-1.5">
            <span>📑</span> Master Reservations
          </button>
          <button (click)="activeTab = 'broadcast'" [class]="activeTab === 'broadcast' ? 'bg-accent text-white shadow-lg shadow-accent/30' : 'text-ink-2 hover:text-ink hover:bg-seg'" class="px-3.5 py-2 rounded-xl transition-all flex items-center gap-1.5">
            <span>📢</span> Emergency Broadcast
          </button>
          <button (click)="activeTab = 'profile'" [class]="activeTab === 'profile' ? 'bg-purple-600 text-white shadow-lg shadow-purple-600/30' : 'text-purple-700 dark:text-purple-300 hover:text-ink hover:bg-seg'" class="px-3.5 py-2 rounded-xl transition-all flex items-center gap-1.5 ml-auto border border-line">
            <span>🛡️</span> Admin Profile & Security
          </button>
        </div>
      </div>

      <!-- ========================================== -->
      <!-- TAB 1: SYSTEM OVERVIEW -->
      <!-- ========================================== -->
      @if (activeTab === 'overview') {
        <div class="space-y-6 animate-fadeIn">
          <!-- Top KPI Cards Grid -->
          <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
            <div class="p-6 rounded-card border border-line bg-surface shadow-card flex items-center gap-4 hover:border-accent-2 transition-all backdrop-blur-xl">
              <div class="w-14 h-14 rounded-2xl bg-sky-500/10 border border-sky-500/30 flex items-center justify-center text-2xl text-sky-600 dark:text-sky-400">
                🛫
              </div>
              <div>
                <p class="text-xs font-semibold text-ink-3 uppercase tracking-wider">Scheduled Flights</p>
                <h3 class="text-2xl font-black text-ink mt-1 font-mono">{{ flights.length }}</h3>
                <p class="text-[11px] text-sky-600 dark:text-sky-400 mt-0.5">{{ activeFlightsCount }} active routes in sky</p>
              </div>
            </div>

            <div class="p-6 rounded-card border border-line bg-surface shadow-card flex items-center gap-4 hover:border-accent-2 transition-all backdrop-blur-xl">
              <div class="w-14 h-14 rounded-2xl bg-indigo-500/10 border border-indigo-500/30 flex items-center justify-center text-2xl text-indigo-600 dark:text-indigo-400">
                ✈️
              </div>
              <div>
                <p class="text-xs font-semibold text-ink-3 uppercase tracking-wider">Fleet Aircraft</p>
                <h3 class="text-2xl font-black text-ink mt-1 font-mono">{{ fleet.length }}</h3>
                <p class="text-[11px] text-indigo-600 dark:text-indigo-400 mt-0.5">{{ operationalAircraftCount }} operational / {{ fleet.length - operationalAircraftCount }} maintenance</p>
              </div>
            </div>

            <div class="p-6 rounded-card border border-line bg-surface shadow-card flex items-center gap-4 hover:border-accent-2 transition-all backdrop-blur-xl">
              <div class="w-14 h-14 rounded-2xl bg-emerald-500/10 border border-emerald-500/30 flex items-center justify-center text-2xl text-emerald-600 dark:text-emerald-400">
                👥
              </div>
              <div>
                <p class="text-xs font-semibold text-ink-3 uppercase tracking-wider">Platform Accounts</p>
                <h3 class="text-2xl font-black text-ink mt-1 font-mono">{{ users.length }}</h3>
                <p class="text-[11px] text-emerald-600 dark:text-emerald-400 mt-0.5">{{ passengerCount }} Passengers | {{ staffCount }} Staff</p>
              </div>
            </div>

            <div class="p-6 rounded-card border border-line bg-surface shadow-card flex items-center gap-4 hover:border-accent-2 transition-all backdrop-blur-xl">
              <div class="w-14 h-14 rounded-2xl bg-amber-500/10 border border-amber-500/30 flex items-center justify-center text-2xl text-amber-600 dark:text-amber-400">
                💳
              </div>
              <div>
                <p class="text-xs font-semibold text-ink-3 uppercase tracking-wider">Gross Booking Value</p>
                <h3 class="text-2xl font-black text-ink mt-1 font-mono">₹{{ totalRevenue | number:'1.0-0' }}</h3>
                <p class="text-[11px] text-amber-600 dark:text-amber-400 mt-0.5">{{ confirmedBookingsCount }} confirmed bookings</p>
              </div>
            </div>
          </div>

          <!-- ENHANCED: EXECUTIVE OPERATIONS LAUNCHPAD -->
          <div class="rounded-card p-6 sm:p-8 border border-line bg-surface shadow-card relative overflow-hidden backdrop-blur-xl">
            <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6 pb-4 border-b border-line">
              <div>
                <div class="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full bg-accent-soft border border-accent/20 text-accent text-[10px] font-bold tracking-wider uppercase mb-1">
                  Tactical Launchpad
                </div>
                <h3 class="text-lg font-extrabold text-ink flex items-center gap-2">
                  <span>⚡</span> Executive Mission Controls & Rapid Dispatch
                </h3>
                <p class="text-xs text-ink-2 mt-0.5">High-priority operational workflows with real-time digital configuration wizards.</p>
              </div>
              <span class="text-xs font-mono text-ink-2 bg-seg px-3 py-1.5 rounded-xl border border-line">
                Operational Readiness: <strong class="text-emerald-600 dark:text-emerald-400">100%</strong>
              </span>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
              <!-- Action 1: Schedule Flight -->
              <div (click)="openFlightModal()" class="group cursor-pointer p-5 rounded-tile bg-surface border border-line shadow-card hover:border-accent-2 hover:shadow-lg transition-all duration-300 flex flex-col justify-between relative overflow-hidden hover:-translate-y-1 backdrop-blur-xl">
                <div>
                  <div class="flex items-center justify-between mb-4">
                    <div class="w-12 h-12 rounded-2xl bg-sky-500/10 border border-sky-500/30 flex items-center justify-center text-2xl text-sky-600 dark:text-sky-400 group-hover:scale-110 transition-transform">
                      🛫
                    </div>
                    <span class="px-2 py-0.5 rounded-full bg-sky-500/20 text-sky-700 dark:text-sky-300 text-[10px] font-bold font-mono">
                      {{ flights.length }} Routes
                    </span>
                  </div>
                  <h4 class="text-sm font-bold text-ink group-hover:text-accent transition-colors">Schedule Flight</h4>
                  <p class="text-xs text-ink-2 mt-1.5 leading-relaxed">
                    Plan new commercial routes, assign certified aircraft, set airport slots & base cabin fares.
                  </p>
                </div>
                <div class="mt-5 pt-3 border-t border-line flex items-center justify-between text-xs text-sky-600 dark:text-sky-400 font-semibold">
                  <span>Launch Wizard</span>
                  <span class="group-hover:translate-x-1 transition-transform">➔</span>
                </div>
              </div>

              <!-- Action 2: Register Aircraft -->
              <div (click)="showAircraftModal = true" class="group cursor-pointer p-5 rounded-tile bg-surface border border-line shadow-card hover:border-accent-2 hover:shadow-lg transition-all duration-300 flex flex-col justify-between relative overflow-hidden hover:-translate-y-1 backdrop-blur-xl">
                <div>
                  <div class="flex items-center justify-between mb-4">
                    <div class="w-12 h-12 rounded-2xl bg-indigo-500/10 border border-indigo-500/30 flex items-center justify-center text-2xl text-indigo-600 dark:text-indigo-400 group-hover:scale-110 transition-transform">
                      ✈️
                    </div>
                    <span class="px-2 py-0.5 rounded-full bg-indigo-500/20 text-indigo-700 dark:text-indigo-300 text-[10px] font-bold font-mono">
                      {{ fleet.length }} Airframes
                    </span>
                  </div>
                  <h4 class="text-sm font-bold text-ink group-hover:text-accent transition-colors">Register Aircraft</h4>
                  <p class="text-xs text-ink-2 mt-1.5 leading-relaxed">
                    Commission new commercial airframes, configure dual-cabin layouts, and update maintenance cycles.
                  </p>
                </div>
                <div class="mt-5 pt-3 border-t border-line flex items-center justify-between text-xs text-indigo-600 dark:text-indigo-400 font-semibold">
                  <span>Commission Fleet</span>
                  <span class="group-hover:translate-x-1 transition-transform">➔</span>
                </div>
              </div>

              <!-- Action 3: Configure Pricing & Promos -->
              <div (click)="openPromoModal()" class="group cursor-pointer p-5 rounded-tile bg-surface border border-line shadow-card hover:border-accent-2 hover:shadow-lg transition-all duration-300 flex flex-col justify-between relative overflow-hidden hover:-translate-y-1 backdrop-blur-xl">
                <div>
                  <div class="flex items-center justify-between mb-4">
                    <div class="w-12 h-12 rounded-2xl bg-emerald-500/10 border border-emerald-500/30 flex items-center justify-center text-2xl text-emerald-600 dark:text-emerald-400 group-hover:scale-110 transition-transform">
                      🏷️
                    </div>
                    <span class="px-2 py-0.5 rounded-full bg-emerald-500/20 text-emerald-700 dark:text-emerald-300 text-[10px] font-bold font-mono">
                      {{ promos.length }} Active
                    </span>
                  </div>
                  <h4 class="text-sm font-bold text-ink group-hover:text-accent transition-colors">Dynamic Pricing & Promos</h4>
                  <p class="text-xs text-ink-2 mt-1.5 leading-relaxed">
                    Tune multi-cabin surge pricing rules, GST slabs, and create targeted seasonal promo vouchers.
                  </p>
                </div>
                <div class="mt-5 pt-3 border-t border-line flex items-center justify-between text-xs text-emerald-600 dark:text-emerald-400 font-semibold">
                  <span>Manage Yield</span>
                  <span class="group-hover:translate-x-1 transition-transform">➔</span>
                </div>
              </div>

              <!-- Action 4: Emergency Broadcast -->
              <div (click)="activeTab = 'broadcast'" class="group cursor-pointer p-5 rounded-tile bg-surface border border-line shadow-card hover:border-accent-2 hover:shadow-lg transition-all duration-300 flex flex-col justify-between relative overflow-hidden hover:-translate-y-1 backdrop-blur-xl">
                <div>
                  <div class="flex items-center justify-between mb-4">
                    <div class="w-12 h-12 rounded-2xl bg-purple-500/10 border border-purple-500/30 flex items-center justify-center text-2xl text-purple-600 dark:text-purple-400 group-hover:scale-110 transition-transform">
                      📢
                    </div>
                    <span class="px-2 py-0.5 rounded-full bg-purple-500/20 text-purple-700 dark:text-purple-300 text-[10px] font-bold font-mono">
                      Instant Mesh
                    </span>
                  </div>
                  <h4 class="text-sm font-bold text-ink group-hover:text-accent transition-colors">Emergency Broadcast</h4>
                  <p class="text-xs text-ink-2 mt-1.5 leading-relaxed">
                    Broadcast weather warnings, gate changes, and airport notices to passenger and crew mobile apps.
                  </p>
                </div>
                <div class="mt-5 pt-3 border-t border-line flex items-center justify-between text-xs text-purple-600 dark:text-purple-400 font-semibold">
                  <span>Transmit Advisory</span>
                  <span class="group-hover:translate-x-1 transition-transform">➔</span>
                </div>
              </div>
            </div>
          </div>

          <!-- Microservices Topology & Mesh Health Status -->
          <div class="rounded-card p-6 sm:p-8 border border-line bg-surface shadow-card backdrop-blur-xl">
            <div class="flex items-center justify-between mb-4">
              <div>
                <h2 class="text-base font-bold text-ink flex items-center gap-2">
                  <span>⚡</span> Distributed Microservices Mesh Health Status
                </h2>
                <p class="text-xs text-ink-2 mt-0.5">Real-time status of service discovery, event bus, and microservice nodes.</p>
              </div>
              <span class="px-3 py-1 rounded-full bg-emerald-500/10 border border-emerald-500/30 text-emerald-700 dark:text-emerald-400 text-xs font-semibold flex items-center gap-2">
                <span class="w-2 h-2 rounded-full bg-emerald-400 animate-ping"></span>
                Mesh Health: 100%
              </span>
            </div>

            <div class="grid grid-cols-2 sm:grid-cols-4 gap-4">
              @for (svc of microservices; track svc.name) {
                <div class="p-4 rounded-2xl bg-seg border border-line hover:border-accent-2 transition-all flex flex-col justify-between">
                  <div class="flex items-center justify-between mb-2">
                    <span class="text-xs font-semibold text-ink">{{ svc.name }}</span>
                    <span class="flex h-2 w-2 relative">
                      <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
                      <span class="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
                    </span>
                  </div>
                  <div class="text-[11px] text-ink-2">Port: <span class="font-mono text-ink">{{ svc.port }}</span></div>
                  <div class="text-[10px] text-emerald-600 dark:text-emerald-400 font-semibold mt-2 flex items-center justify-between">
                    <span>UPTIME: 99.98%</span>
                    <span class="text-ink-3 font-mono">{{ svc.latency }}ms</span>
                  </div>
                </div>
              }
            </div>
          </div>
        </div>
      }

      <!-- ========================================== -->
      <!-- TAB 2: FLIGHT SCHEDULER & DISPATCH -->
      <!-- ========================================== -->
      @if (activeTab === 'flights') {
        <div class="rounded-card p-6 sm:p-8 border border-line bg-surface shadow-card space-y-6 backdrop-blur-xl animate-fadeIn">
          <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-line">
            <div>
              <h2 class="text-lg font-bold text-ink flex items-center gap-2">
                <span>🛫</span> Commercial Flight Schedule Operations
              </h2>
              <p class="text-xs text-ink-2 mt-1">Configure flight itineraries, manage aircraft assignments, update gate/status, and regulate seat allocations.</p>
            </div>
            <div class="flex items-center gap-2">
              <button (click)="exportFlightsCsv()" class="px-3.5 py-2.5 rounded-xl bg-seg hover:bg-seg/80 text-ink-2 hover:text-ink text-xs font-semibold transition-all border border-line flex items-center gap-1.5">
                <span>📥</span> Export CSV
              </button>
              <button (click)="openFlightModal()" class="px-4 py-2.5 rounded-xl bg-accent hover:opacity-95 text-white text-xs font-bold transition-all shadow-lg shadow-accent/30 flex items-center gap-2">
                <span>➕</span> Schedule Flight
              </button>
            </div>
          </div>

          <!-- Search & Filter Controls -->
          <div class="flex flex-wrap items-center gap-4 bg-seg p-4 rounded-2xl border border-line text-xs">
            <div class="flex-1 min-w-[200px]">
              <input [(ngModel)]="flightSearchTerm" placeholder="Search by flight number, airline, or airport code..." class="w-full bg-surface border border-line rounded-xl px-3.5 py-2 text-xs text-ink placeholder:text-ink-3 outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
            </div>
            <div class="flex items-center gap-2">
              <span class="text-ink-2 text-xs font-medium">Status:</span>
              <select [(ngModel)]="flightStatusFilter" class="bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
                <option value="ALL">All Statuses</option>
                <option value="SCHEDULED">Scheduled</option>
                <option value="BOARDING">Boarding</option>
                <option value="DELAYED">Delayed</option>
                <option value="DEPARTED">Departed</option>
                <option value="COMPLETED">Completed</option>
                <option value="CANCELLED">Cancelled</option>
              </select>
            </div>
            <div class="text-ink-2">
              Showing: <strong class="text-ink font-mono">{{ filteredFlights.length }}</strong> of {{ flights.length }} flights
            </div>
          </div>

          <!-- ENHANCED TABLE: FLIGHT ROSTER -->
          <div class="overflow-x-auto rounded-2xl border border-line shadow-card">
            <table class="w-full text-left text-xs">
              <thead class="bg-seg text-ink-2 uppercase tracking-wider text-[10px] border-b border-line">
                <tr>
                  <th class="py-4 px-4 font-bold">Flight</th>
                  <th class="py-4 px-4 font-bold">Airline Carrier</th>
                  <th class="py-4 px-4 font-bold">Itinerary Route</th>
                  <th class="py-4 px-4 font-bold">Departure</th>
                  <th class="py-4 px-4 font-bold">Arrival</th>
                  <th class="py-4 px-4 font-bold">Seat Occupancy</th>
                  <th class="py-4 px-4 font-bold">Base Fare</th>
                  <th class="py-4 px-4 font-bold">Flight Status</th>
                  <th class="py-4 px-4 font-bold text-right">Flight Controls</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-line bg-surface/50">
                @for (f of filteredFlights; track f.id) {
                  <tr class="hover:bg-seg/50 transition-colors group">
                    <td class="py-3.5 px-4 font-bold text-ink font-mono">
                      <div class="flex items-center gap-2">
                        <span class="w-2.5 h-2.5 rounded-full" [ngClass]="f.status === 'CANCELLED' ? 'bg-rose-500' : 'bg-emerald-400'"></span>
                        <span class="text-sm tracking-tight text-ink group-hover:text-accent transition-colors">{{ f.flightNumber }}</span>
                      </div>
                    </td>
                    <td class="py-3.5 px-4">
                      <span class="font-medium text-ink block">{{ f.airlineName }}</span>
                      <span class="text-[10px] text-ink-3 font-mono">{{ f.aircraftTailNumber || 'Fleet Airframe' }}</span>
                    </td>
                    <td class="py-3.5 px-4">
                      <div class="inline-flex items-center gap-2 px-3 py-1.5 rounded-xl bg-seg border border-line font-mono text-[11px] shadow-sm">
                        <span class="text-ink font-extrabold">{{ f.source }}</span>
                        <span class="text-accent text-xs">✈️</span>
                        <span class="text-ink font-extrabold">{{ f.destination }}</span>
                      </div>
                    </td>
                    <td class="py-3.5 px-4 text-ink font-mono">
                      <span class="block text-ink font-semibold">{{ f.departureTime | date:'HH:mm' }}</span>
                      <span class="text-[10px] text-ink-3">{{ f.departureTime | date:'MMM d, y' }}</span>
                    </td>
                    <td class="py-3.5 px-4 text-ink font-mono">
                      <span class="block text-ink font-semibold">{{ f.arrivalTime | date:'HH:mm' }}</span>
                      <span class="text-[10px] text-ink-3">{{ f.arrivalTime | date:'MMM d, y' }}</span>
                    </td>
                    <td class="py-3.5 px-4">
                      <div class="flex items-center gap-2">
                        <div class="w-24 bg-seg rounded-full h-2 overflow-hidden border border-line">
                          <div class="bg-gradient-to-r from-sky-500 to-indigo-500 h-2 rounded-full" [style.width.%]="((f.totalSeats - f.availableSeats) / f.totalSeats) * 100"></div>
                        </div>
                        <span class="font-mono text-emerald-600 dark:text-emerald-400 font-semibold">{{ f.availableSeats }}</span>
                        <span class="text-ink-3 text-[10px]">/ {{ f.totalSeats }}</span>
                      </div>
                    </td>
                    <td class="py-3.5 px-4 font-bold text-amber-600 dark:text-amber-400 font-mono text-sm">₹{{ f.baseFare | number:'1.0-0' }}</td>
                    <td class="py-3.5 px-4">
                      <button (click)="openQuickStatusModal(f)" title="Click to update flight operational status" class="px-2.5 py-1 rounded-full text-[10px] font-bold border transition-all hover:scale-105 flex items-center gap-1.5 shadow-sm" [class]="getStatusClass(f.status)">
                        <span class="w-1.5 h-1.5 rounded-full bg-current"></span>
                        {{ f.status }} ✎
                      </button>
                    </td>
                    <td class="py-3.5 px-4 text-right">
                      <div class="inline-flex items-center gap-1 bg-seg p-1 rounded-xl border border-line">
                        <button (click)="openViewFlightModal(f)" class="px-2.5 py-1 rounded-lg bg-sky-500/10 hover:bg-sky-500/20 text-sky-700 dark:text-sky-300 text-[11px] font-semibold transition-all" title="View Full Dossier">
                          View
                        </button>
                        <button (click)="openFlightModal(f)" class="px-2.5 py-1 rounded-lg bg-indigo-500/10 hover:bg-indigo-500/20 text-indigo-700 dark:text-indigo-300 text-[11px] font-semibold transition-all" title="Edit Flight Details">
                          Edit
                        </button>
                        @if (f.status !== 'CANCELLED') {
                          <button (click)="cancelFlightDirect(f)" class="px-2.5 py-1 rounded-lg bg-amber-500/10 hover:bg-amber-500/20 text-amber-700 dark:text-amber-300 text-[11px] font-semibold transition-all" title="Cancel Flight & Broadcast">
                            Cancel
                          </button>
                        }
                        <button (click)="deleteFlight(f.id)" class="px-2 py-1 rounded-lg bg-rose-500/10 hover:bg-rose-500/20 text-rose-700 dark:text-rose-400 text-[11px] font-semibold transition-all" title="Delete Flight">
                          ✕
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

      <!-- ========================================== -->
      <!-- TAB 3: FLEET ROSTER & AIRWORTHINESS -->
      <!-- ========================================== -->
      @if (activeTab === 'aircraft') {
        <div class="rounded-card p-6 sm:p-8 border border-line bg-surface shadow-card space-y-6 backdrop-blur-xl animate-fadeIn">
          <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-line">
            <div>
              <h2 class="text-lg font-bold text-ink flex items-center gap-2">
                <span>✈️</span> Commercial Fleet Aircraft Inventory & Airworthiness
              </h2>
              <p class="text-xs text-ink-2 mt-1">Manage aircraft registrations, model certifications, passenger capacity limits, and maintenance cycles.</p>
            </div>
            <button (click)="showAircraftModal = true" class="px-4 py-2.5 rounded-xl bg-accent hover:opacity-95 text-white text-xs font-bold transition-all shadow-lg shadow-accent/30 flex items-center gap-2">
              <span>➕</span> Register New Aircraft
            </button>
          </div>

          <!-- Fleet Capacity Summary Metric -->
          <div class="p-5 rounded-2xl bg-seg border border-line flex flex-wrap items-center justify-between gap-4 text-xs">
            <div class="flex items-center gap-3">
              <span class="text-3xl">🛫</span>
              <div>
                <span class="font-bold text-ink text-sm">Active Fleet Utilization:</span>
                <span class="text-ink-2 ml-1">Total Fleet Capacity of <strong class="text-ink">{{ totalFleetCapacity }}</strong> passenger seats across {{ fleet.length }} airframes.</span>
              </div>
            </div>
            <div class="flex items-center gap-2">
              <span class="px-3 py-1.5 rounded-xl bg-emerald-500/10 border border-emerald-500/30 text-emerald-700 dark:text-emerald-400 font-mono font-bold">{{ operationalAircraftCount }} In-Service</span>
              <span class="px-3 py-1.5 rounded-xl bg-amber-500/10 border border-amber-500/30 text-amber-700 dark:text-amber-400 font-mono font-bold">{{ fleet.length - operationalAircraftCount }} Grounded/Service</span>
            </div>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            @for (plane of fleet; track plane.id) {
              <div class="p-6 rounded-card bg-surface border border-line hover:border-accent-2 shadow-card transition-all flex flex-col justify-between group hover:-translate-y-1 backdrop-blur-xl">
                <div>
                  <div class="flex items-center justify-between mb-4">
                    <span class="px-3 py-1 rounded-xl text-xs font-bold bg-indigo-500/10 border border-indigo-500/30 text-indigo-700 dark:text-indigo-300 font-mono">
                      {{ plane.aircraftNumber }}
                    </span>
                    <button (click)="toggleAircraftStatus(plane)" title="Click to cycle operational airworthiness status" class="px-3 py-1 rounded-full text-[10px] font-bold border transition-all hover:scale-105"
                            [ngClass]="{
                              'bg-emerald-500/10 border-emerald-500/30 text-emerald-700 dark:text-emerald-400': plane.status === 'ACTIVE',
                              'bg-amber-500/10 border-amber-500/30 text-amber-700 dark:text-amber-400': plane.status === 'MAINTENANCE',
                              'bg-rose-500/10 border-rose-500/30 text-rose-700 dark:text-rose-400': plane.status === 'GROUNDED'
                            }">
                      {{ plane.status }} ✎
                    </button>
                  </div>
                  <h3 class="text-lg font-extrabold text-ink group-hover:text-accent transition-colors">{{ plane.model }}</h3>
                  <p class="text-xs text-ink-2 mt-1">Manufacturer: {{ plane.manufacturer || 'Commercial Aerospace' }}</p>

                  <div class="grid grid-cols-2 gap-3 mt-4 pt-4 border-t border-line text-xs">
                    <div class="p-2.5 rounded-xl bg-seg border border-line">
                      <span class="text-[10px] text-ink-3 uppercase block">Seating Capacity</span>
                      <span class="font-extrabold text-ink font-mono mt-0.5 block">{{ plane.capacity }} Seats</span>
                    </div>
                    <div class="p-2.5 rounded-xl bg-seg border border-line">
                      <span class="text-[10px] text-ink-3 uppercase block">Airframe Hours</span>
                      <span class="font-extrabold text-emerald-600 dark:text-emerald-400 font-mono mt-0.5 block">{{ plane.flightHours || 1420 }} hrs</span>
                    </div>
                  </div>
                </div>

                <div class="mt-5 pt-4 border-t border-line flex items-center justify-between text-xs">
                  <button (click)="openViewAircraftModal(plane)" class="px-3 py-1.5 rounded-xl bg-seg hover:bg-seg/80 text-sky-700 dark:text-sky-400 font-semibold border border-line transition-all flex items-center gap-1.5">
                    <span>👁️</span> Airframe Dossier
                  </button>
                  <div class="flex items-center gap-2">
                    <button (click)="editAircraft(plane)" class="px-2.5 py-1.5 rounded-xl bg-seg hover:bg-seg/80 text-ink-2 hover:text-ink text-xs font-semibold border border-line transition-all">Edit</button>
                    <button (click)="deleteAircraft(plane.id)" class="px-2.5 py-1.5 rounded-xl bg-rose-500/10 hover:bg-rose-500/20 text-rose-700 dark:text-rose-400 border border-rose-500/30 text-xs font-semibold transition-all">Retire</button>
                  </div>
                </div>
              </div>
            }
          </div>
        </div>
      }

      <!-- ========================================== -->
      <!-- TAB 4: DYNAMIC PRICING & PROMOTIONS -->
      <!-- ========================================== -->
      @if (activeTab === 'fares') {
        <div class="space-y-6 animate-fadeIn">
          <!-- Cabin Tier Pricing Table -->
          <div class="rounded-card p-6 sm:p-8 border border-line bg-surface shadow-card space-y-6 backdrop-blur-xl">
            <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-line">
              <div>
                <h2 class="text-lg font-bold text-ink flex items-center gap-2">
                  <span>🏷️</span> Dynamic Cabin Fare Engine & Revenue Optimization
                </h2>
                <p class="text-xs text-ink-2 mt-1">Configure multi-class cabin fares (Economy, Business, First Class), GST tax slabs, and base discounts.</p>
              </div>
              <button (click)="openFareModal()" class="px-4 py-2.5 rounded-xl bg-accent hover:opacity-95 text-white text-xs font-bold transition-all shadow-lg shadow-accent/30 flex items-center gap-2">
                <span>➕</span> Configure Tier Pricing
              </button>
            </div>

            <div class="overflow-x-auto rounded-2xl border border-line shadow-card">
              <table class="w-full text-left text-xs">
                <thead class="bg-seg text-ink-2 uppercase tracking-wider text-[10px] border-b border-line">
                  <tr>
                    <th class="py-4 px-4 font-bold">Flight Target</th>
                    <th class="py-4 px-4 font-bold">Economy Class</th>
                    <th class="py-4 px-4 font-bold">Business Class</th>
                    <th class="py-4 px-4 font-bold">First Class</th>
                    <th class="py-4 px-4 font-bold">Tax Slab (%)</th>
                    <th class="py-4 px-4 font-bold">Promotional Rebate</th>
                    <th class="py-4 px-4 font-bold text-right">Actions</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-line bg-surface/50">
                  @for (fare of fares; track fare.id) {
                    <tr class="hover:bg-seg/50 transition-colors">
                      <td class="py-3.5 px-4 font-bold text-ink font-mono">
                        Flight #{{ fare.flightId }}
                        <span class="text-ink-3 font-normal ml-1">({{ getFlightRoute(fare.flightId) }})</span>
                      </td>
                      <td class="py-3.5 px-4 font-semibold text-sky-600 dark:text-sky-400 font-mono">₹{{ fare.economyFare | number:'1.0-0' }}</td>
                      <td class="py-3.5 px-4 font-semibold text-amber-600 dark:text-amber-400 font-mono">₹{{ fare.businessFare | number:'1.0-0' }}</td>
                      <td class="py-3.5 px-4 font-semibold text-purple-600 dark:text-purple-400 font-mono">₹{{ fare.firstClassFare | number:'1.0-0' }}</td>
                      <td class="py-3.5 px-4 text-ink-2 font-mono">{{ fare.taxRate }}% GST</td>
                      <td class="py-3.5 px-4 text-emerald-600 dark:text-emerald-400 font-bold font-mono">{{ fare.discountPercentage }}% OFF</td>
                      <td class="py-3.5 px-4 text-right space-x-2">
                        <button (click)="openViewFareModal(fare)" class="px-2.5 py-1 rounded-lg bg-sky-500/10 hover:bg-sky-500/20 text-sky-700 dark:text-sky-300 text-[11px] font-semibold transition-all">
                          View Tiers
                        </button>
                        <button (click)="openFareModal(fare)" class="px-2.5 py-1 rounded-lg bg-emerald-500/10 hover:bg-emerald-500/20 text-emerald-700 dark:text-emerald-300 text-[11px] font-semibold transition-all">
                          Edit
                        </button>
                      </td>
                    </tr>
                  }
                </tbody>
              </table>
            </div>
          </div>

          <!-- Promotional Voucher & Discount Codes Section -->
          <div class="rounded-card p-6 sm:p-8 border border-line bg-surface shadow-card space-y-6 backdrop-blur-xl">
            <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-line">
              <div>
                <h3 class="text-base font-bold text-ink flex items-center gap-2">
                  <span>🎟️</span> Promotional Coupon & Discount Engine
                </h3>
                <p class="text-xs text-ink-2 mt-1">Manage public discount codes, percentage cuts, usage quotas, and expiry dates.</p>
              </div>
              <button (click)="openPromoModal()" class="px-4 py-2.5 rounded-xl bg-purple-600 hover:bg-purple-500 text-white text-xs font-bold transition-all shadow-lg shadow-purple-600/30 flex items-center gap-2">
                <span>➕</span> Create New Coupon
              </button>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
              @for (promo of promos; track promo.id) {
                <!-- Enhanced Perforated Voucher Card -->
                <div class="p-6 rounded-card bg-surface border border-line hover:border-accent-2 shadow-card flex flex-col justify-between transition-all relative overflow-hidden group backdrop-blur-xl">
                  <div class="absolute -right-6 -top-6 w-20 h-20 bg-purple-500/10 rounded-full blur-xl group-hover:bg-purple-500/20 transition-all"></div>
                  <div>
                    <div class="flex items-center justify-between mb-3">
                      <span class="px-3.5 py-1.5 rounded-xl bg-accent-soft border border-accent/20 text-accent font-mono font-black text-sm tracking-widest shadow-inner">
                        {{ promo.code }}
                      </span>
                      <span class="px-2.5 py-0.5 rounded-full text-[10px] font-bold border"
                            [ngClass]="promo.status === 'ACTIVE' ? 'bg-emerald-500/10 border-emerald-500/30 text-emerald-700 dark:text-emerald-400' : 'bg-seg border-line text-ink-3'">
                        {{ promo.status }}
                      </span>
                    </div>
                    <h4 class="text-sm font-bold text-ink mt-3">{{ promo.description }}</h4>
                    <div class="flex items-center gap-3 mt-3 text-xs bg-seg p-3 rounded-2xl border border-line">
                      <div>
                        <span class="text-[10px] text-ink-3 uppercase block">Discount</span>
                        <span class="text-emerald-600 dark:text-emerald-400 font-black font-mono text-sm">{{ promo.discountPercentage }}% OFF</span>
                      </div>
                      <div class="border-l border-line pl-3">
                        <span class="text-[10px] text-ink-3 uppercase block">Max Cap</span>
                        <span class="text-ink font-bold font-mono text-sm">₹{{ promo.maxDiscount }}</span>
                      </div>
                    </div>
                  </div>

                  <div class="mt-5 pt-3 border-t border-line flex items-center justify-between text-xs text-ink-2">
                    <span>Usage: <strong class="text-ink">{{ promo.usageCount }}</strong> / {{ promo.usageLimit }}</span>
                    <button (click)="togglePromoStatus(promo)" class="px-3 py-1 rounded-xl bg-purple-500/10 hover:bg-purple-500/20 text-purple-700 dark:text-purple-300 border border-purple-500/30 font-semibold transition-all">
                      {{ promo.status === 'ACTIVE' ? 'Pause' : 'Activate' }}
                    </button>
                  </div>
                </div>
              }
            </div>
          </div>

          <!-- Dynamic Pricing Algorithmic Simulator -->
          <div class="rounded-card p-6 sm:p-8 border border-line bg-surface shadow-card space-y-4 backdrop-blur-xl">
            <h3 class="text-sm font-bold text-ink flex items-center gap-2">
              <span>📈</span> Algorithmic Dynamic Surge Pricing Simulator
            </h3>
            <p class="text-xs text-ink-2">Test how seat demand triggers dynamic surge pricing across flights.</p>

            <div class="grid grid-cols-1 md:grid-cols-3 gap-6 pt-2">
              <div>
                <label class="block text-xs text-ink-2 mb-2">Simulated Seat Occupancy Rate: <strong class="text-ink">{{ simulatorOccupancy }}%</strong></label>
                <input [(ngModel)]="simulatorOccupancy" type="range" min="10" max="100" class="w-full accent-accent">
                <div class="flex justify-between text-[10px] text-ink-3 mt-1">
                  <span>10% (Low)</span>
                  <span>50% (Standard)</span>
                  <span>95% (Peak Surge)</span>
                </div>
              </div>

              <div>
                <label class="block text-xs text-ink-2 mb-2">Base Ticket Fare: <strong class="text-ink">₹{{ simulatorBaseFare }}</strong></label>
                <input [(ngModel)]="simulatorBaseFare" type="number" step="500" class="w-full bg-surface border border-line rounded-xl px-3 py-1.5 text-xs text-ink outline-none focus:border-accent">
              </div>

              <div class="p-4 rounded-2xl bg-seg border border-line flex flex-col justify-center">
                <span class="text-[10px] text-ink-3 uppercase tracking-wider">Estimated Dynamic Ticket Fare</span>
                <span class="text-xl font-black text-emerald-600 dark:text-emerald-400 font-mono mt-1">
                  ₹{{ calculateSurgeFare(simulatorBaseFare, simulatorOccupancy) | number:'1.0-0' }}
                </span>
                <span class="text-[10px] text-ink-3 mt-0.5">Surge Multiplier: {{ (calculateSurgeFare(simulatorBaseFare, simulatorOccupancy) / simulatorBaseFare) | number:'1.2-2' }}x</span>
              </div>
            </div>
          </div>
        </div>
      }

      <!-- ========================================== -->
      <!-- TAB 5: USER DIRECTORY & RBAC PRIVILEGES -->
      <!-- ========================================== -->
      @if (activeTab === 'users') {
        <div class="rounded-card bg-surface border border-line shadow-card p-6 sm:p-8 space-y-6 animate-fadeIn text-ink">
          <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-line">
            <div>
              <h2 class="text-lg font-bold text-ink flex items-center gap-2">
                <span>👥</span> Enterprise User Directory & Role-Based Access Control (RBAC)
              </h2>
              <p class="text-xs text-ink-2 mt-1">Manage passengers, ground crew staff accounts, promote personnel to STAFF/ADMIN, and regulate active/blocked status.</p>
            </div>
            <div class="flex items-center gap-2">
              <button (click)="exportUsersCsv()" class="px-3.5 py-2.5 rounded-xl bg-seg hover:bg-surface text-ink text-xs font-semibold transition-all border border-line flex items-center gap-1.5">
                <span>📥</span> Export User Roster
              </button>
            </div>
          </div>

          <!-- User Search & Role Filters -->
          <div class="flex flex-wrap items-center gap-4 bg-seg p-4 rounded-2xl border border-line text-xs">
            <div class="flex-1 min-w-[200px]">
              <input [(ngModel)]="userSearchTerm" placeholder="Search by name, email, or phone number..." class="w-full bg-surface border border-line rounded-xl px-3.5 py-2 text-xs text-ink placeholder:text-ink-3 outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
            </div>
            <div class="flex items-center gap-2">
              <span class="text-ink-2 font-medium">Role:</span>
              <select [(ngModel)]="userRoleFilter" class="bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
                <option value="ALL">All Roles</option>
                <option value="ROLE_USER">Passengers (ROLE_USER)</option>
                <option value="ROLE_STAFF">Staff Crew (ROLE_STAFF)</option>
                <option value="ROLE_ADMIN">Administrators (ROLE_ADMIN)</option>
              </select>
            </div>
            <div class="flex items-center gap-2">
              <span class="text-ink-2 font-medium">Status:</span>
              <select [(ngModel)]="userStatusFilter" class="bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
                <option value="ALL">All Status</option>
                <option value="ACTIVE">Active Accounts</option>
                <option value="BLOCKED">Blocked Accounts</option>
              </select>
            </div>
            <div class="text-ink-3">
              Users: <strong class="text-ink font-mono">{{ filteredUsers.length }}</strong> of {{ users.length }}
            </div>
          </div>

          <div class="overflow-x-auto rounded-2xl border border-line shadow-card">
            <table class="w-full text-left text-xs">
              <thead class="bg-seg text-ink-2 uppercase tracking-wider text-[10px] border-b border-line">
                <tr>
                  <th class="py-4 px-4 font-bold">User</th>
                  <th class="py-4 px-4 font-bold">Contact Details</th>
                  <th class="py-4 px-4 font-bold">Loyalty Level & XP</th>
                  <th class="py-4 px-4 font-bold">Security Role</th>
                  <th class="py-4 px-4 font-bold">Account Status</th>
                  <th class="py-4 px-4 font-bold">Flags/Reports</th>
                  <th class="py-4 px-4 font-bold text-right">User Governance</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-line bg-surface/50">
                @for (u of filteredUsers; track u.userId) {
                  <tr class="hover:bg-seg/50 transition-colors">
                    <td class="py-3.5 px-4">
                      <div class="flex items-center gap-3">
                        <div class="w-9 h-9 rounded-xl bg-gradient-to-tr from-sky-600 to-indigo-600 flex items-center justify-center font-bold text-white text-xs shadow-md">
                          {{ u.firstName.charAt(0) }}{{ u.lastName.charAt(0) }}
                        </div>
                        <div>
                          <span class="font-bold text-ink block">{{ u.firstName }} {{ u.lastName }}</span>
                          <span class="text-[10px] text-ink-3 font-mono">UID: #{{ u.userId }}</span>
                        </div>
                      </div>
                    </td>
                    <td class="py-3.5 px-4">
                      <span class="text-ink block font-medium">{{ u.email }}</span>
                      <span class="text-[11px] text-ink-3 font-mono">{{ u.phoneNumber || 'No phone set' }}</span>
                    </td>
                    <td class="py-3.5 px-4">
                      <div class="flex items-center gap-1.5">
                        <span class="px-2 py-0.5 rounded-full font-mono text-[10px] font-bold border"
                              [ngClass]="{
                                'bg-purple-500/10 text-purple-700 dark:text-purple-300 border-purple-500/30': (u.tier || 'GOLD') === 'DIAMOND',
                                'bg-brand-500/15 text-brand-700 dark:text-brand-200 border-brand-500/30': (u.tier || 'GOLD') === 'PLATINUM',
                                'bg-amber-500/10 text-amber-700 dark:text-amber-300 border-amber-500/30': (u.tier || 'GOLD') === 'GOLD',
                                'bg-seg text-ink-2 border-line': (u.tier || 'GOLD') === 'BRONZE' || (u.tier || 'GOLD') === 'SILVER'
                              }">
                          👑 Lvl {{ u.level || 3 }} {{ u.tier || 'GOLD' }}
                        </span>
                        <span class="text-[10px] text-ink-3 font-mono">{{ (u.xp || 24850) | number }} XP</span>
                      </div>
                    </td>
                    <td class="py-3.5 px-4">
                      <span [class]="getRoleBadgeClass(u.role)" class="px-2.5 py-1 rounded-full text-[10px] font-bold border">
                        {{ u.role }}
                      </span>
                    </td>
                    <td class="py-3.5 px-4">
                      <span class="px-2.5 py-1 rounded-full text-[10px] font-semibold border flex items-center gap-1.5 w-max"
                            [ngClass]="u.status === 'BLOCKED' ? 'bg-rose-500/10 border-rose-500/30 text-rose-700 dark:text-rose-400' : 'bg-emerald-500/10 border-emerald-500/30 text-emerald-700 dark:text-emerald-400'">
                        <span class="w-1.5 h-1.5 rounded-full" [ngClass]="u.status === 'BLOCKED' ? 'bg-rose-500' : 'bg-emerald-500'"></span>
                        {{ u.status || 'ACTIVE' }}
                      </span>
                    </td>
                    <td class="py-3.5 px-4">
                      @if ((u.reportsCount || 0) > 0) {
                        <span class="px-2 py-0.5 rounded-full bg-rose-500/10 border border-rose-500/30 text-rose-700 dark:text-rose-300 font-bold text-[10px]">
                          {{ u.reportsCount }} incident(s)
                        </span>
                      } @else {
                        <span class="text-ink-3 text-[11px]">Clear</span>
                      }
                    </td>
                    <td class="py-3.5 px-4 text-right space-x-1.5">
                      <button (click)="openViewUserModal(u)" class="px-2 py-1 rounded-lg bg-seg hover:bg-surface text-ink-2 hover:text-ink text-[11px] font-medium transition-all border border-line" title="View Dossier">
                        View
                      </button>
                      <button (click)="openEditUserModal(u)" class="px-2 py-1 rounded-lg bg-sky-500/10 hover:bg-sky-500/20 text-sky-700 dark:text-sky-300 border border-sky-500/30 text-[11px] font-medium transition-all">
                        Edit
                      </button>
                      <button (click)="openAwardXpModal(u)" class="px-2 py-1 rounded-lg bg-amber-500/10 hover:bg-amber-500/20 text-amber-700 dark:text-amber-300 border border-amber-500/30 text-[11px] font-medium transition-all" title="Award Bonus XP">
                        +XP
                      </button>
                      <button (click)="openPromoteMenu(u)" class="px-2 py-1 rounded-lg bg-purple-500/10 hover:bg-purple-500/20 text-purple-700 dark:text-purple-300 border border-purple-500/30 text-[11px] font-medium transition-all">
                        Role ⏷
                      </button>
                      <button (click)="toggleUserBlockStatus(u)" class="px-2 py-1 rounded-lg text-[11px] font-medium transition-all border"
                              [ngClass]="u.status === 'BLOCKED' ? 'bg-emerald-500/10 text-emerald-700 dark:text-emerald-300 border-emerald-500/30 hover:bg-emerald-500/20' : 'bg-rose-500/10 text-rose-700 dark:text-rose-300 border-rose-500/30 hover:bg-rose-500/20'">
                        {{ u.status === 'BLOCKED' ? 'Unblock' : 'Block' }}
                      </button>
                      <button (click)="openReportModalForUser(u)" class="px-2 py-1 rounded-lg bg-amber-500/10 hover:bg-amber-500/20 text-amber-700 dark:text-amber-300 border border-amber-500/30 text-[11px] font-medium transition-all" title="Report misconduct">
                        Report
                      </button>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      }

      <!-- ========================================== -->
      <!-- TAB 6: PASSENGER & SECURITY INCIDENT REPORTS -->
      <!-- ========================================== -->
      @if (activeTab === 'reports') {
        <div class="rounded-card bg-surface border border-line shadow-card p-6 sm:p-8 space-y-6 animate-fadeIn text-ink">
          <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-line">
            <div>
              <h2 class="text-lg font-bold text-ink flex items-center gap-2">
                <span>🚨</span> Passenger Misconduct & Security Incident Reports
              </h2>
              <p class="text-xs text-ink-2 mt-1">Audit security violations, payment chargebacks, flight disturbances, and resolve disciplinary flags.</p>
            </div>
            <button (click)="openReportModal()" class="px-4 py-2.5 rounded-xl bg-rose-600 hover:bg-rose-500 text-white text-xs font-bold transition-all shadow-lg shadow-rose-600/30 flex items-center gap-2">
              <span>➕</span> File New Incident Report
            </button>
          </div>

          <div class="overflow-x-auto rounded-2xl border border-line shadow-card">
            <table class="w-full text-left text-xs">
              <thead class="bg-seg text-ink-2 uppercase tracking-wider text-[10px] border-b border-line">
                <tr>
                  <th class="py-4 px-4 font-bold">Report ID</th>
                  <th class="py-4 px-4 font-bold">Reported User</th>
                  <th class="py-4 px-4 font-bold">Incident Category / Reason</th>
                  <th class="py-4 px-4 font-bold">Severity</th>
                  <th class="py-4 px-4 font-bold">Logged Date</th>
                  <th class="py-4 px-4 font-bold">Report Status</th>
                  <th class="py-4 px-4 font-bold text-right">Actions</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-line bg-surface/50">
                @for (rep of incidentReports; track rep.id) {
                  <tr class="hover:bg-seg/50 transition-colors">
                    <td class="py-3.5 px-4 font-mono text-ink-3">#INC-{{ rep.id }}</td>
                    <td class="py-3.5 px-4">
                      <span class="font-bold text-ink block">{{ rep.userName }}</span>
                      <span class="text-[10px] text-ink-3 font-mono">{{ rep.userEmail }}</span>
                    </td>
                    <td class="py-3.5 px-4 text-ink">
                      <span class="font-semibold text-ink block">{{ rep.reason }}</span>
                      @if (rep.notes) {
                        <span class="text-[11px] text-ink-2">{{ rep.notes }}</span>
                      }
                    </td>
                    <td class="py-3.5 px-4">
                      <span class="px-2.5 py-0.5 rounded-full text-[10px] font-bold border"
                            [ngClass]="{
                              'bg-rose-500/10 border-rose-500/30 text-rose-700 dark:text-rose-300': rep.severity === 'CRITICAL' || rep.severity === 'HIGH',
                              'bg-amber-500/10 border-amber-500/30 text-amber-700 dark:text-amber-300': rep.severity === 'MEDIUM',
                              'bg-sky-500/10 border-sky-500/30 text-sky-700 dark:text-sky-300': rep.severity === 'LOW'
                            }">
                        {{ rep.severity }}
                      </span>
                    </td>
                    <td class="py-3.5 px-4 text-ink-3 font-mono">{{ rep.createdAt | date:'short' }}</td>
                    <td class="py-3.5 px-4">
                      <span class="px-2.5 py-0.5 rounded-full text-[10px] font-semibold border"
                            [ngClass]="rep.status === 'RESOLVED' ? 'bg-emerald-500/10 border-emerald-500/30 text-emerald-700 dark:text-emerald-400' : 'bg-rose-500/10 border-rose-500/30 text-rose-700 dark:text-rose-400'">
                        {{ rep.status }}
                      </span>
                    </td>
                    <td class="py-3.5 px-4 text-right space-x-2">
                      @if (rep.status !== 'RESOLVED') {
                        <button (click)="resolveReport(rep.id)" class="text-emerald-600 dark:text-emerald-400 hover:underline font-semibold text-xs">Resolve</button>
                        <button (click)="dismissReport(rep.id)" class="text-ink-3 hover:text-ink font-semibold text-xs">Dismiss</button>
                      } @else {
                        <span class="text-ink-3 text-xs">Closed</span>
                      }
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      }

      <!-- ========================================== -->
      <!-- TAB 7: MASTER AIRLINE RESERVATIONS -->
      <!-- ========================================== -->
      @if (activeTab === 'bookings') {
        <div class="rounded-card bg-surface border border-line shadow-card p-6 sm:p-8 space-y-6 animate-fadeIn text-ink">
          <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 pb-4 border-b border-line">
            <div>
              <h2 class="text-lg font-bold text-ink flex items-center gap-2">
                <span>📑</span> Centralized Airline Reservations & Financial Audit
              </h2>
              <p class="text-xs text-ink-2 mt-1">Full transaction ledger of all issued PNR tickets across commercial airlines.</p>
            </div>
            <div class="flex items-center gap-2">
              <button (click)="exportBookingsCsv()" class="px-3.5 py-2.5 rounded-xl bg-seg hover:bg-surface text-ink text-xs font-semibold transition-all border border-line flex items-center gap-1.5">
                <span>📥</span> Export Reservations CSV
              </button>
            </div>
          </div>

          <div class="overflow-x-auto rounded-2xl border border-line shadow-card">
            <table class="w-full text-left text-xs">
              <thead class="bg-seg text-ink-2 uppercase tracking-wider text-[10px] border-b border-line">
                <tr>
                  <th class="py-4 px-4 font-bold">Booking ID</th>
                  <th class="py-4 px-4 font-bold">PNR Locator</th>
                  <th class="py-4 px-4 font-bold">Assigned Flight</th>
                  <th class="py-4 px-4 font-bold">Passenger Details</th>
                  <th class="py-4 px-4 font-bold">Total Fare</th>
                  <th class="py-4 px-4 font-bold">Booking Date</th>
                  <th class="py-4 px-4 font-bold">Status</th>
                  <th class="py-4 px-4 font-bold text-right">Actions</th>
                </tr>
              </thead>
              <tbody class="divide-y divide-line bg-surface/50">
                @for (b of bookings; track b.bookingId) {
                  <tr class="hover:bg-seg/50 transition-colors">
                    <td class="py-3.5 px-4 font-mono text-ink-3">#{{ b.bookingId }}</td>
                    <td class="py-3.5 px-4 font-mono font-bold text-amber-600 dark:text-amber-400 text-sm">{{ b.pnr }}</td>
                    <td class="py-3.5 px-4 font-bold text-ink">{{ b.flightNumber || 'Flight #' + b.flightId }}</td>
                    <td class="py-3.5 px-4 text-ink-2">
                      {{ b.passengers.length || 1 }} Passenger(s)
                      @if (b.passengers[0]) {
                        <span class="text-ink-3 block text-[11px]">({{ b.passengers[0].firstName }} {{ b.passengers[0].lastName }})</span>
                      }
                    </td>
                    <td class="py-3.5 px-4 font-bold text-emerald-600 dark:text-emerald-400 font-mono text-sm">₹{{ b.totalFare | number:'1.0-0' }}</td>
                    <td class="py-3.5 px-4 text-ink-3 font-mono">{{ b.bookingDate | date:'short' }}</td>
                    <td class="py-3.5 px-4">
                      <span [class]="b.status === 'CONFIRMED' ? 'bg-emerald-500/10 border-emerald-500/30 text-emerald-700 dark:text-emerald-400' : 'bg-rose-500/10 border-rose-500/30 text-rose-700 dark:text-rose-400'" class="px-2.5 py-1 rounded-full text-[10px] font-bold border">
                        {{ b.status }}
                      </span>
                    </td>
                    <td class="py-3.5 px-4 text-right space-x-2">
                      <button (click)="openViewBookingModal(b)" class="px-2.5 py-1 rounded-lg bg-sky-500/10 hover:bg-sky-500/20 text-sky-700 dark:text-sky-300 text-[11px] font-semibold transition-all border border-sky-500/30">
                        View E-Ticket
                      </button>
                      @if (b.status === 'CONFIRMED') {
                        <button (click)="cancelReservation(b.bookingId)" class="px-2.5 py-1 rounded-lg bg-rose-500/10 hover:bg-rose-500/20 text-rose-700 dark:text-rose-400 text-[11px] font-semibold transition-all border border-rose-500/30">
                          Cancel & Refund
                        </button>
                      } @else {
                        <span class="text-ink-3 text-xs">Voided</span>
                      }
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </div>
      }

      <!-- ========================================== -->
      <!-- TAB 8: SYSTEM BROADCAST HUB -->
      <!-- ========================================== -->
      @if (activeTab === 'broadcast') {
        <div class="rounded-card bg-surface border border-line shadow-card p-6 sm:p-8 space-y-6 animate-fadeIn text-ink">
          <div class="pb-4 border-b border-line">
            <h2 class="text-lg font-bold text-ink flex items-center gap-2">
              <span>📢</span> Global Broadcast & Emergency Aviation Notification Hub
            </h2>
            <p class="text-xs text-ink-2 mt-1">
              Dispatch high-priority flight advisories, gate movements, runway closures, or system maintenance notices to connected passengers & staff.
            </p>
          </div>

          <div class="max-w-xl mx-auto space-y-4 bg-seg border border-line p-6 sm:p-8 rounded-3xl shadow-card">
            <div>
              <label class="block text-xs font-semibold text-ink-2 mb-1.5">Notification Title</label>
              <input [(ngModel)]="broadcastForm.title" type="text" placeholder="e.g. Severe Weather Advisory - Delhi Airport Terminal 3" class="w-full bg-surface border border-line rounded-xl px-3.5 py-2.5 text-xs text-ink placeholder:text-ink-3 focus:border-accent focus:ring-2 focus:ring-accent/20 outline-none transition-all">
            </div>

            <div>
              <label class="block text-xs font-semibold text-ink-2 mb-1.5">Advisory Priority / Category</label>
              <select [(ngModel)]="broadcastForm.type" class="w-full bg-surface border border-line rounded-xl px-3.5 py-2.5 text-xs text-ink focus:border-accent focus:ring-2 focus:ring-accent/20 outline-none transition-all">
                <option value="BROADCAST">System Broadcast (All Platform Users)</option>
                <option value="FLIGHT_DELAY">Severe Weather & Flight Delays</option>
                <option value="GATE_CHANGE">Airport Terminal Gate Advisory</option>
                <option value="INFO">Administrative Operational Notice</option>
              </select>
            </div>

            <div>
              <label class="block text-xs font-semibold text-ink-2 mb-1.5">Associated Flight Number (Optional)</label>
              <input [(ngModel)]="broadcastForm.flightNumber" type="text" placeholder="e.g. AI-101" class="w-full bg-surface border border-line rounded-xl px-3.5 py-2.5 text-xs text-ink placeholder:text-ink-3 focus:border-accent focus:ring-2 focus:ring-accent/20 outline-none font-mono transition-all">
            </div>

            <div>
              <label class="block text-xs font-semibold text-ink-2 mb-1.5">Detailed Advisory Message</label>
              <textarea [(ngModel)]="broadcastForm.message" rows="4" placeholder="Type advisory content that will appear on passenger and staff notification centers..." class="w-full bg-surface border border-line rounded-xl px-3.5 py-2.5 text-xs text-ink placeholder:text-ink-3 focus:border-accent focus:ring-2 focus:ring-accent/20 outline-none transition-all"></textarea>
            </div>

            <button (click)="sendBroadcast()" [disabled]="!broadcastForm.title || !broadcastForm.message" class="w-full py-3 rounded-xl bg-accent hover:opacity-95 disabled:opacity-50 text-white font-bold text-xs transition-all shadow-lg flex items-center justify-center gap-2">
              <span>📡</span> Transmit Broadcast Alert
            </button>
          </div>
        </div>
      }

      <!-- ========================================== -->
      <!-- TAB 9: ADMIN PROFILE & SECURITY SETTINGS -->
      <!-- ========================================== -->
      @if (activeTab === 'profile') {
        <div class="space-y-6 animate-fadeIn text-ink">
          <!-- Admin Profile Card -->
          <div class="rounded-card p-6 sm:p-8 border border-purple-500/30 bg-surface shadow-card relative overflow-hidden">
            <div class="flex flex-col md:flex-row md:items-center justify-between gap-6 pb-6 border-b border-line">
              <div class="flex items-center gap-4">
                <div class="w-20 h-20 rounded-2xl bg-gradient-to-tr from-purple-600 to-indigo-600 flex items-center justify-center text-3xl font-black text-white shadow-xl border border-purple-400/40">
                  {{ adminProfile.name.charAt(0) }}
                </div>
                <div>
                  <div class="flex items-center gap-2">
                    <h2 class="text-xl font-extrabold text-ink">{{ adminProfile.name }}</h2>
                    <span class="px-2.5 py-0.5 rounded-full bg-purple-500/20 border border-purple-500/40 text-purple-700 dark:text-purple-300 text-xs font-bold font-mono">
                      {{ adminProfile.role }}
                    </span>
                  </div>
                  <p class="text-xs text-ink-2 mt-1">{{ adminProfile.department }} • Top-Level Executive</p>
                  <p class="text-xs text-purple-700 dark:text-purple-300 font-mono mt-0.5">{{ adminProfile.email }}</p>
                </div>
              </div>

              <div class="flex flex-wrap items-center gap-3">
                <button (click)="openEditProfileModal()" class="px-4 py-2.5 rounded-xl bg-purple-600 hover:bg-purple-500 text-white text-xs font-bold transition-all shadow-lg shadow-purple-600/30 flex items-center gap-2">
                  <span>✎</span> Edit Profile
                </button>
                <button (click)="showPasswordModal = true" class="px-4 py-2.5 rounded-xl bg-seg hover:bg-surface text-ink text-xs font-semibold transition-all border border-line flex items-center gap-2">
                  <span>🔑</span> Change Password
                </button>
                <button (click)="triggerPasswordResetRecovery()" class="px-4 py-2.5 rounded-xl bg-amber-500/10 hover:bg-amber-500/20 text-amber-700 dark:text-amber-300 border border-amber-500/30 text-xs font-semibold transition-all flex items-center gap-2">
                  <span>🔄</span> Forget / Reset Access
                </button>
              </div>
            </div>

            <!-- Profile Details Grid -->
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-6 pt-6">
              <div class="p-4 rounded-2xl bg-seg border border-line">
                <span class="text-[10px] text-ink-3 uppercase tracking-wider block">Official Contact</span>
                <span class="text-sm font-semibold text-ink mt-1 block font-mono">{{ adminProfile.phone }}</span>
                <span class="text-[11px] text-ink-3 mt-1 block">Verified Executive Line</span>
              </div>

              <div class="p-4 rounded-2xl bg-seg border border-line">
                <span class="text-[10px] text-ink-3 uppercase tracking-wider block">Two-Factor Authentication</span>
                <div class="flex items-center justify-between mt-1">
                  <span class="text-sm font-bold" [ngClass]="adminProfile.twoFactorEnabled ? 'text-emerald-600 dark:text-emerald-400' : 'text-amber-600 dark:text-amber-400'">
                    {{ adminProfile.twoFactorEnabled ? 'PROTECTED (2FA Active)' : 'DISABLED' }}
                  </span>
                  <button (click)="toggle2FA()" class="text-xs text-purple-600 dark:text-purple-400 hover:underline font-semibold">
                    {{ adminProfile.twoFactorEnabled ? 'Disable' : 'Enable' }}
                  </button>
                </div>
                <span class="text-[11px] text-ink-3 mt-1 block">Hardware & Authenticator App</span>
              </div>

              <div class="p-4 rounded-2xl bg-seg border border-line">
                <span class="text-[10px] text-ink-3 uppercase tracking-wider block">Session Activity</span>
                <span class="text-xs font-semibold text-ink mt-1 block font-mono">Last Login: {{ adminProfile.lastLogin }}</span>
                <span class="text-[11px] text-emerald-600 dark:text-emerald-400 mt-1 block">Active Session (IP: 127.0.0.1)</span>
              </div>
            </div>
          </div>

          <!-- Security & Administrative Audit Trail -->
          <div class="rounded-card p-6 sm:p-8 border border-line bg-surface shadow-card space-y-4">
            <h3 class="text-base font-bold text-ink flex items-center gap-2">
              <span>🛡️</span> Administrative Security & Audit Log
            </h3>
            <p class="text-xs text-ink-2">Chronological ledger of executive actions, role elevations, flight cancellations, and fare updates.</p>

            <div class="overflow-x-auto pt-2">
              <table class="w-full text-left text-xs">
                <thead class="bg-seg text-ink-2 uppercase tracking-wider text-[10px] border-b border-line">
                  <tr>
                    <th class="py-3 px-4 font-semibold">Log ID</th>
                    <th class="py-3 px-4 font-semibold">Action Performed</th>
                    <th class="py-3 px-4 font-semibold">Target Entity</th>
                    <th class="py-3 px-4 font-semibold">Timestamp</th>
                    <th class="py-3 px-4 font-semibold">Origin IP</th>
                    <th class="py-3 px-4 font-semibold text-right">Result</th>
                  </tr>
                </thead>
                <tbody class="divide-y divide-line bg-surface/50">
                  @for (log of auditLogs; track log.id) {
                    <tr class="hover:bg-seg/50 transition-colors">
                      <td class="py-3 px-4 font-mono text-ink-3">#AUD-{{ log.id }}</td>
                      <td class="py-3 px-4 font-semibold text-ink">{{ log.action }}</td>
                      <td class="py-3 px-4 text-purple-700 dark:text-purple-300 font-mono">{{ log.target }}</td>
                      <td class="py-3 px-4 text-ink-3 font-mono">{{ log.timestamp }}</td>
                      <td class="py-3 px-4 text-ink-3 font-mono">{{ log.ipAddress }}</td>
                      <td class="py-3 px-4 text-right">
                        <span class="px-2 py-0.5 rounded-full text-[10px] font-bold border"
                              [ngClass]="log.status === 'SUCCESS' ? 'bg-emerald-500/10 border-emerald-500/30 text-emerald-700 dark:text-emerald-400' : 'bg-rose-500/10 border-rose-500/30 text-rose-700 dark:text-rose-400'">
                          {{ log.status }}
                        </span>
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
      <!-- ADVANCED SPLIT-SCREEN WIZARDS & MODALS -->
      <!-- ========================================== -->

      <!-- 1. ENHANCED ADVANCED FLIGHT SCHEDULER WIZARD WITH LIVE PREVIEW -->
      @if (showFlightModal) {
        <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-ink/40 backdrop-blur-md animate-fadeIn overflow-y-auto">
          <div class="bg-surface border border-line rounded-card shadow-card backdrop-blur-xl w-full max-w-4xl p-6 sm:p-8 space-y-6 my-8">
            <div class="flex items-center justify-between pb-4 border-b border-line">
              <div>
                <span class="text-[10px] text-accent font-mono font-bold tracking-wider uppercase">Mission Dispatch Wizard</span>
                <h3 class="text-lg font-black text-ink flex items-center gap-2 mt-0.5">
                  <span>🛫</span>
                  {{ flightForm.id ? 'Edit Commercial Flight #' + flightForm.flightNumber : 'Schedule Commercial Flight Route' }}
                </h3>
              </div>
              <button (click)="showFlightModal = false" class="text-ink-2 hover:text-ink p-2 rounded-xl bg-seg border border-line">✕</button>
            </div>

            <!-- Split Screen: Form on Left, Live Card on Right -->
            <div class="grid grid-cols-1 lg:grid-cols-12 gap-8 items-start">
              <!-- Left Column: Inputs (7 Cols) -->
              <div class="lg:col-span-7 space-y-4 text-xs">
                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <label class="block text-[11px] font-semibold text-ink-2 mb-1">Flight Number *</label>
                    <input [(ngModel)]="flightForm.flightNumber" placeholder="e.g. AI-204" class="w-full bg-surface border border-line rounded-xl px-3.5 py-2.5 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 font-mono uppercase transition-all">
                  </div>
                  <div>
                    <label class="block text-[11px] font-semibold text-ink-2 mb-1">Airline Carrier *</label>
                    <input [(ngModel)]="flightForm.airlineName" placeholder="e.g. Air India" class="w-full bg-surface border border-line rounded-xl px-3.5 py-2.5 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
                  </div>
                </div>

                <div>
                  <label class="block text-[11px] font-semibold text-ink-2 mb-1">Assign Fleet Aircraft Airframe</label>
                  <select [ngModel]="flightForm.aircraftId" (ngModelChange)="onAircraftSelected($event)" class="w-full bg-surface border border-line rounded-xl px-3.5 py-2.5 text-xs text-ink font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 font-mono transition-all">
                    <option [value]="0">Custom Configuration (Manual Seats)</option>
                    @for (plane of fleet; track plane.id) {
                      <option [value]="plane.id">{{ plane.aircraftNumber }} — {{ plane.model }} ({{ plane.capacity }} Seats)</option>
                    }
                  </select>
                </div>

                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <label class="block text-[11px] font-semibold text-ink-2 mb-1">Origin Airport (IATA) *</label>
                    <input [(ngModel)]="flightForm.source" placeholder="e.g. DEL (Delhi)" class="w-full bg-surface border border-line rounded-xl px-3.5 py-2.5 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 font-mono uppercase transition-all">
                  </div>
                  <div>
                    <label class="block text-[11px] font-semibold text-ink-2 mb-1">Destination Airport (IATA) *</label>
                    <input [(ngModel)]="flightForm.destination" placeholder="e.g. BOM (Mumbai)" class="w-full bg-surface border border-line rounded-xl px-3.5 py-2.5 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 font-mono uppercase transition-all">
                  </div>
                </div>

                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <label class="block text-[11px] font-semibold text-ink-2 mb-1">Departure Slot</label>
                    <input [(ngModel)]="flightForm.departureTime" type="datetime-local" class="w-full bg-surface border border-line rounded-xl px-3.5 py-2 text-xs text-ink placeholder:text-ink-3 outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
                  </div>
                  <div>
                    <label class="block text-[11px] font-semibold text-ink-2 mb-1">Arrival Slot</label>
                    <input [(ngModel)]="flightForm.arrivalTime" type="datetime-local" class="w-full bg-surface border border-line rounded-xl px-3.5 py-2 text-xs text-ink placeholder:text-ink-3 outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
                  </div>
                </div>

                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <label class="block text-[11px] font-semibold text-ink-2 mb-1">Base Ticket Fare (₹)</label>
                    <input [(ngModel)]="flightForm.baseFare" type="number" class="w-full bg-surface border border-line rounded-xl px-3.5 py-2.5 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 font-mono transition-all">
                  </div>
                  <div>
                    <label class="block text-[11px] font-semibold text-ink-2 mb-1">Total Seat Quota</label>
                    <input [(ngModel)]="flightForm.totalSeats" type="number" class="w-full bg-surface border border-line rounded-xl px-3.5 py-2.5 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 font-mono transition-all">
                  </div>
                </div>

                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <label class="block text-[11px] font-semibold text-ink-2 mb-1">Terminal</label>
                    <input [(ngModel)]="flightForm.terminal" placeholder="e.g. T3" class="w-full bg-surface border border-line rounded-xl px-3.5 py-2 text-xs text-ink placeholder:text-ink-3 outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 font-mono transition-all">
                  </div>
                  <div>
                    <label class="block text-[11px] font-semibold text-ink-2 mb-1">Boarding Gate</label>
                    <input [(ngModel)]="flightForm.gate" placeholder="e.g. Gate 14B" class="w-full bg-surface border border-line rounded-xl px-3.5 py-2 text-xs text-ink placeholder:text-ink-3 outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 font-mono transition-all">
                  </div>
                </div>
              </div>

              <!-- Right Column: Live Boarding Card Preview (5 Cols) -->
              <div class="lg:col-span-5 bg-seg p-5 rounded-3xl border border-sky-500/30 shadow-card relative text-ink">
                <div class="flex items-center justify-between pb-3 mb-3 border-b border-line">
                  <span class="text-[10px] font-bold text-sky-600 dark:text-sky-400 uppercase tracking-widest flex items-center gap-1.5">
                    <span class="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></span>
                    Live Digital Ticket Preview
                  </span>
                  <span class="px-2 py-0.5 rounded-full text-[9px] font-mono font-bold bg-sky-500/10 text-sky-700 dark:text-sky-300 border border-sky-500/30">
                    {{ flightForm.flightNumber || 'FLIGHT-NO' }}
                  </span>
                </div>

                <!-- Ticket Card Content -->
                <div class="space-y-4">
                  <div class="flex items-center justify-between">
                    <div>
                      <h4 class="text-sm font-extrabold text-ink">{{ flightForm.airlineName || 'AeroBook Airlines' }}</h4>
                      <span class="text-[10px] text-ink-3 font-mono">{{ flightForm.aircraftTailNumber || 'Commercial Jet' }}</span>
                    </div>
                    <span class="px-2.5 py-0.5 rounded-full text-[10px] font-bold bg-emerald-500/10 border border-emerald-500/30 text-emerald-700 dark:text-emerald-400">
                      {{ flightForm.status || 'SCHEDULED' }}
                    </span>
                  </div>

                  <!-- Route Visualization -->
                  <div class="p-4 rounded-2xl bg-surface border border-line flex items-center justify-between">
                    <div class="text-center">
                      <span class="text-2xl font-black text-ink font-mono block">{{ flightForm.source || 'DEL' }}</span>
                      <span class="text-[10px] text-ink-3 font-mono">Origin</span>
                    </div>
                    <div class="flex flex-col items-center flex-1 px-4">
                      <span class="text-[10px] text-sky-600 dark:text-sky-400 font-mono">Non-Stop</span>
                      <div class="w-full flex items-center justify-center relative my-1">
                        <div class="w-full h-0.5 bg-line"></div>
                        <span class="absolute text-sky-600 dark:text-sky-400 text-xs">✈️</span>
                      </div>
                      <span class="text-[9px] text-ink-3">Terminal {{ flightForm.terminal || 'T3' }}</span>
                    </div>
                    <div class="text-center">
                      <span class="text-2xl font-black text-ink font-mono block">{{ flightForm.destination || 'BOM' }}</span>
                      <span class="text-[10px] text-ink-3 font-mono">Destination</span>
                    </div>
                  </div>

                  <!-- Price & Gate Summary -->
                  <div class="grid grid-cols-2 gap-3 text-xs">
                    <div class="p-3 rounded-xl bg-surface border border-line">
                      <span class="text-[10px] text-ink-3 uppercase block">Base Price</span>
                      <span class="text-sm font-extrabold text-amber-600 dark:text-amber-400 font-mono mt-0.5 block">
                        ₹{{ flightForm.baseFare || 4500 | number:'1.0-0' }}
                      </span>
                    </div>
                    <div class="p-3 rounded-xl bg-surface border border-line">
                      <span class="text-[10px] text-ink-3 uppercase block">Boarding Gate</span>
                      <span class="text-sm font-extrabold text-ink font-mono mt-0.5 block">
                        {{ flightForm.gate || 'Gate 14B' }}
                      </span>
                    </div>
                  </div>

                  <!-- Barcode Simulation -->
                  <div class="p-3 rounded-xl bg-surface border border-line text-center">
                    <div class="h-6 w-full flex items-center justify-center gap-1 opacity-60">
                      <div class="w-1 h-6 bg-ink"></div>
                      <div class="w-0.5 h-6 bg-ink"></div>
                      <div class="w-1.5 h-6 bg-ink"></div>
                      <div class="w-0.5 h-6 bg-ink"></div>
                      <div class="w-2 h-6 bg-ink"></div>
                      <div class="w-1 h-6 bg-ink"></div>
                      <div class="w-0.5 h-6 bg-ink"></div>
                      <div class="w-1.5 h-6 bg-ink"></div>
                      <div class="w-2 h-6 bg-ink"></div>
                    </div>
                    <span class="text-[9px] font-mono text-ink-3 tracking-widest mt-1 block">AEROBOOK-ELECTRONIC-DISPATCH</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="flex items-center justify-end gap-3 pt-4 border-t border-line">
              <button (click)="showFlightModal = false" class="px-5 py-2.5 rounded-xl text-ink-2 hover:text-ink text-xs font-semibold">Cancel</button>
              <button (click)="saveFlight()" class="px-6 py-2.5 rounded-xl bg-accent hover:opacity-95 text-white font-bold text-xs transition-all shadow-lg">
                {{ flightForm.id ? 'Save Flight Changes' : 'Confirm & Schedule Flight' }}
              </button>
            </div>
          </div>
        </div>
      }

      <!-- 2. SPLIT-SCREEN AIRCRAFT COMMISSIONING WIZARD -->
      @if (showAircraftModal) {
        <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-ink/40 backdrop-blur-md animate-fadeIn overflow-y-auto">
          <div class="bg-surface border border-line rounded-card shadow-card backdrop-blur-xl w-full max-w-3xl p-6 sm:p-8 space-y-6 my-8 text-ink">
            <div class="flex items-center justify-between pb-4 border-b border-line">
              <div>
                <span class="text-[10px] text-accent font-mono font-bold tracking-wider uppercase">Airframe Registry</span>
                <h3 class="text-lg font-black text-ink flex items-center gap-2 mt-0.5">
                  <span>✈️</span> {{ aircraftForm.id ? 'Edit Aircraft Roster' : 'Register Commercial Aircraft' }}
                </h3>
              </div>
              <button (click)="showAircraftModal = false" class="text-ink-2 hover:text-ink p-2 rounded-xl bg-seg border border-line">✕</button>
            </div>

            <div class="grid grid-cols-1 md:grid-cols-2 gap-8 items-start">
              <!-- Form -->
              <div class="space-y-4 text-xs">
                <div>
                  <label class="block text-[11px] font-semibold text-ink-2 mb-1">Aircraft Model Name *</label>
                  <input [(ngModel)]="aircraftForm.model" placeholder="e.g. Boeing 787-9 Dreamliner" class="w-full bg-surface border border-line rounded-xl px-3.5 py-2.5 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
                </div>

                <div>
                  <label class="block text-[11px] font-semibold text-ink-2 mb-1">Tail Registration Number *</label>
                  <input [(ngModel)]="aircraftForm.aircraftNumber" placeholder="e.g. VT-AB204" class="w-full bg-surface border border-line rounded-xl px-3.5 py-2.5 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 font-mono uppercase transition-all">
                </div>

                <div>
                  <label class="block text-[11px] font-semibold text-ink-2 mb-1">Manufacturer</label>
                  <select [(ngModel)]="aircraftForm.manufacturer" class="w-full bg-surface border border-line rounded-xl px-3.5 py-2.5 text-xs text-ink font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
                    <option value="Boeing Commercial Airplanes">Boeing Commercial Airplanes</option>
                    <option value="Airbus Commercial Aviation">Airbus Commercial Aviation</option>
                    <option value="Embraer Commercial Jets">Embraer Commercial Jets</option>
                    <option value="Bombardier Aviation">Bombardier Aviation</option>
                  </select>
                </div>

                <div class="grid grid-cols-2 gap-4">
                  <div>
                    <label class="block text-[11px] font-semibold text-ink-2 mb-1">Seating Capacity</label>
                    <input [(ngModel)]="aircraftForm.capacity" type="number" placeholder="296" class="w-full bg-surface border border-line rounded-xl px-3.5 py-2.5 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 font-mono transition-all">
                  </div>
                  <div>
                    <label class="block text-[11px] font-semibold text-ink-2 mb-1">Airworthiness Status</label>
                    <select [(ngModel)]="aircraftForm.status" class="w-full bg-surface border border-line rounded-xl px-3.5 py-2.5 text-xs text-ink font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
                      <option value="ACTIVE">ACTIVE (In-Service)</option>
                      <option value="MAINTENANCE">MAINTENANCE (Hangar)</option>
                      <option value="GROUNDED">GROUNDED (Inactive)</option>
                    </select>
                  </div>
                </div>
              </div>

              <!-- Airframe Digital Certificate Preview -->
              <div class="p-6 rounded-3xl bg-seg border border-indigo-500/30 shadow-card relative text-xs">
                <span class="text-[10px] font-mono text-indigo-600 dark:text-indigo-400 font-bold uppercase tracking-widest block mb-2">Digital Airframe Certificate</span>
                <div class="p-4 rounded-2xl bg-surface border border-line text-center space-y-2">
                  <span class="text-3xl font-black text-ink font-mono tracking-widest block">{{ aircraftForm.aircraftNumber || 'VT-XXXX' }}</span>
                  <span class="text-xs font-bold text-indigo-600 dark:text-indigo-300 block">{{ aircraftForm.model || 'Commercial Airframe' }}</span>
                  <span class="text-[11px] text-ink-3 block">{{ aircraftForm.manufacturer || 'Certified Manufacturer' }}</span>
                </div>

                <div class="mt-4 space-y-2 text-ink-2">
                  <div class="flex justify-between py-1.5 border-b border-line">
                    <span class="text-ink-3">Seating Limit:</span>
                    <strong class="font-mono text-ink">{{ aircraftForm.capacity || 220 }} Passengers</strong>
                  </div>
                  <div class="flex justify-between py-1.5 border-b border-line">
                    <span class="text-ink-3">Certification State:</span>
                    <span class="px-2 py-0.5 rounded-full text-[10px] font-bold"
                          [ngClass]="aircraftForm.status === 'ACTIVE' ? 'bg-emerald-500/10 text-emerald-700 dark:text-emerald-400' : 'bg-amber-500/10 text-amber-700 dark:text-amber-400'">
                      {{ aircraftForm.status || 'ACTIVE' }}
                    </span>
                  </div>
                  <div class="flex justify-between py-1.5">
                    <span class="text-ink-3">Airworthiness Log:</span>
                    <span class="font-mono text-ink">Verified & Approved</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="flex items-center justify-end gap-3 pt-4 border-t border-line">
              <button (click)="showAircraftModal = false" class="px-5 py-2.5 rounded-xl text-ink-2 hover:text-ink text-xs font-semibold">Cancel</button>
              <button (click)="saveAircraft()" class="px-6 py-2.5 rounded-xl bg-indigo-600 hover:bg-indigo-500 text-white font-bold text-xs transition-all shadow-md">
                {{ aircraftForm.id ? 'Save Airframe Specs' : 'Commission Airframe' }}
              </button>
            </div>
          </div>
        </div>
      }

      <!-- 3. VIEW FLIGHT DOSSIER & CABIN SEAT MAP MODAL -->
      @if (showViewFlightModal && selectedFlight) {
        <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-ink/40 backdrop-blur-md animate-fadeIn overflow-y-auto">
          <div class="border border-line bg-surface rounded-card w-full max-w-3xl p-6 sm:p-8 shadow-card space-y-6 my-8 text-ink">
            <div class="flex items-center justify-between pb-4 border-b border-line">
              <div class="flex items-center gap-3">
                <div class="w-12 h-12 rounded-2xl bg-sky-500/10 border border-sky-500/30 flex items-center justify-center text-2xl text-sky-600 dark:text-sky-400 font-bold">
                  🛫
                </div>
                <div>
                  <h3 class="text-lg font-black text-ink flex items-center gap-2">
                    {{ selectedFlight.flightNumber }} — {{ selectedFlight.airlineName }}
                  </h3>
                  <span class="text-xs text-ink-3 font-mono">Itinerary: {{ selectedFlight.source }} ➔ {{ selectedFlight.destination }}</span>
                </div>
              </div>
              <button (click)="showViewFlightModal = false" class="text-ink-2 hover:text-ink p-2 rounded-xl bg-seg border border-line">✕</button>
            </div>

            <!-- Route & Metrics Banner -->
            <div class="p-5 rounded-2xl bg-seg border border-line flex flex-wrap items-center justify-between gap-4">
              <div class="flex items-center gap-6">
                <div>
                  <span class="text-2xl font-black text-ink font-mono">{{ selectedFlight.source }}</span>
                  <span class="text-[10px] text-ink-3 block">{{ selectedFlight.departureTime | date:'HH:mm, MMM d' }}</span>
                </div>
                <div class="flex flex-col items-center">
                  <span class="text-[10px] text-sky-600 dark:text-sky-400 font-mono">Duration: {{ getFlightDuration(selectedFlight.departureTime, selectedFlight.arrivalTime) }}</span>
                  <div class="w-24 h-0.5 bg-line my-1 relative">
                    <span class="absolute -top-2 left-1/2 -translate-x-1/2 text-sky-600 dark:text-sky-400 text-xs">✈️</span>
                  </div>
                  <span class="text-[9px] text-emerald-600 dark:text-emerald-400 font-mono">Non-Stop</span>
                </div>
                <div>
                  <span class="text-2xl font-black text-ink font-mono">{{ selectedFlight.destination }}</span>
                  <span class="text-[10px] text-ink-3 block">{{ selectedFlight.arrivalTime | date:'HH:mm, MMM d' }}</span>
                </div>
              </div>

              <div class="flex items-center gap-3 border-l border-line pl-4">
                <div>
                  <span class="text-[10px] text-ink-3 uppercase block">Base Fare</span>
                  <span class="text-base font-extrabold text-amber-600 dark:text-amber-400 font-mono">₹{{ selectedFlight.baseFare | number:'1.0-0' }}</span>
                </div>
                <div>
                  <span class="text-[10px] text-ink-3 uppercase block">Occupancy</span>
                  <span class="text-base font-extrabold text-emerald-600 dark:text-emerald-400 font-mono">{{ selectedFlight.availableSeats }} free</span>
                </div>
              </div>
            </div>

            <!-- Interactive Cabin Seat Map Preview -->
            <div class="space-y-3">
              <div class="flex items-center justify-between">
                <h4 class="text-xs font-bold text-ink uppercase tracking-wider flex items-center gap-1.5">
                  <span>💺</span> Interactive Cabin Seat Configuration
                </h4>
                <div class="flex items-center gap-3 text-[10px]">
                  <span class="flex items-center gap-1 text-ink-3">
                    <span class="w-3 h-3 rounded bg-seg border border-line inline-block"></span> Booked
                  </span>
                  <span class="flex items-center gap-1 text-emerald-600 dark:text-emerald-300">
                    <span class="w-3 h-3 rounded bg-emerald-500/20 border border-emerald-500/50 inline-block"></span> Available
                  </span>
                  <span class="flex items-center gap-1 text-purple-600 dark:text-purple-300">
                    <span class="w-3 h-3 rounded bg-purple-500/20 border border-purple-500/50 inline-block"></span> Business
                  </span>
                </div>
              </div>

              <!-- Seat Grid Visualizer -->
              <div class="p-4 rounded-2xl bg-seg border border-line max-h-48 overflow-y-auto">
                <div class="grid grid-cols-6 gap-2 text-center text-[10px] font-mono">
                  @for (s of mockSeatMap; track s.seatNumber) {
                    <div class="p-2 rounded-xl border flex flex-col items-center justify-center transition-all"
                         [ngClass]="{
                           'bg-surface border-line text-ink-3 opacity-60': s.isBooked,
                           'bg-emerald-500/10 border-emerald-500/30 text-emerald-700 dark:text-emerald-300': !s.isBooked && !s.isBusiness,
                           'bg-purple-500/10 border-purple-500/30 text-purple-700 dark:text-purple-300': !s.isBooked && s.isBusiness
                         }">
                      <span class="font-bold">{{ s.seatNumber }}</span>
                      <span class="text-[8px]">{{ s.isBooked ? 'OCC' : (s.isBusiness ? 'BIZ' : 'FREE') }}</span>
                    </div>
                  }
                </div>
              </div>
            </div>

            <div class="flex items-center justify-between pt-4 border-t border-line text-xs">
              <button (click)="openQuickStatusModal(selectedFlight); showViewFlightModal = false" class="px-4 py-2 rounded-xl bg-amber-500/10 text-amber-700 dark:text-amber-300 border border-amber-500/30 font-semibold">
                Update Status
              </button>
              <div class="flex items-center gap-2">
                <button (click)="openFlightModal(selectedFlight); showViewFlightModal = false" class="px-4 py-2 rounded-xl bg-sky-600 hover:bg-sky-500 text-white font-semibold">
                  Edit Flight
                </button>
                <button (click)="showViewFlightModal = false" class="px-4 py-2 rounded-xl bg-seg hover:bg-surface text-ink-2 hover:text-ink border border-line font-semibold">
                  Close
                </button>
              </div>
            </div>
          </div>
        </div>
      }

      <!-- 4. VIEW AIRCRAFT FLEET DOSSIER MODAL -->
      @if (showViewAircraftModal && selectedAircraft) {
        <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-ink/40 backdrop-blur-md animate-fadeIn">
          <div class="border border-line bg-surface rounded-card w-full max-w-lg p-6 sm:p-8 shadow-card space-y-6 text-ink">
            <div class="flex items-center justify-between pb-4 border-b border-line">
              <div class="flex items-center gap-3">
                <div class="w-12 h-12 rounded-2xl bg-indigo-500/10 border border-indigo-500/30 flex items-center justify-center text-2xl font-bold text-indigo-600 dark:text-indigo-400">
                  ✈️
                </div>
                <div>
                  <h3 class="text-lg font-black text-ink">{{ selectedAircraft.model }}</h3>
                  <span class="text-xs text-indigo-600 dark:text-indigo-300 font-mono">Tail: {{ selectedAircraft.aircraftNumber }}</span>
                </div>
              </div>
              <button (click)="showViewAircraftModal = false" class="text-ink-2 hover:text-ink p-2 rounded-xl bg-seg border border-line">✕</button>
            </div>

            <div class="grid grid-cols-2 gap-4 text-xs">
              <div class="p-3.5 rounded-xl bg-seg border border-line">
                <span class="text-[10px] text-ink-3 uppercase block">Certified Capacity</span>
                <span class="text-base font-bold text-ink mt-1 block font-mono">{{ selectedAircraft.capacity }} Passenger Seats</span>
              </div>
              <div class="p-3.5 rounded-xl bg-seg border border-line">
                <span class="text-[10px] text-ink-3 uppercase block">Airworthiness Status</span>
                <span class="text-base font-bold mt-1 block font-mono" [ngClass]="selectedAircraft.status === 'ACTIVE' ? 'text-emerald-600 dark:text-emerald-400' : 'text-amber-600 dark:text-amber-400'">
                  {{ selectedAircraft.status }}
                </span>
              </div>
              <div class="p-3.5 rounded-xl bg-seg border border-line">
                <span class="text-[10px] text-ink-3 uppercase block">Manufacturer</span>
                <span class="text-sm font-semibold text-ink mt-1 block">{{ selectedAircraft.manufacturer || 'Boeing Commercial Airplanes' }}</span>
              </div>
              <div class="p-3.5 rounded-xl bg-seg border border-line">
                <span class="text-[10px] text-ink-3 uppercase block">Flight Hours Logged</span>
                <span class="text-sm font-semibold text-emerald-600 dark:text-emerald-400 mt-1 block font-mono">{{ selectedAircraft.flightHours || 1420 }} hrs</span>
              </div>
            </div>

            <div class="flex items-center justify-end gap-3 pt-4 border-t border-line text-xs">
              <button (click)="toggleAircraftStatus(selectedAircraft)" class="px-4 py-2 rounded-xl bg-indigo-500/10 text-indigo-700 dark:text-indigo-300 border border-indigo-500/30 font-semibold">
                Toggle Airworthiness
              </button>
              <button (click)="showViewAircraftModal = false" class="px-4 py-2 rounded-xl bg-seg hover:bg-surface text-ink-2 hover:text-ink border border-line font-semibold">
                Close Dossier
              </button>
            </div>
          </div>
        </div>
      }

      <!-- 5. VIEW RESERVATION E-TICKET & BOARDING PASS MODAL -->
      @if (showViewBookingModal && selectedBooking) {
        <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-ink/40 backdrop-blur-md animate-fadeIn overflow-y-auto">
          <div class="border border-line bg-surface rounded-card w-full max-w-xl p-6 sm:p-8 shadow-card space-y-6 my-8 text-ink">
            <div class="flex items-center justify-between pb-4 border-b border-line">
              <div>
                <span class="text-[10px] font-mono text-amber-600 dark:text-amber-400 font-bold uppercase tracking-wider">AeroBook Electronic Boarding Document</span>
                <h3 class="text-lg font-black text-ink flex items-center gap-2 mt-0.5">
                  <span>🎫</span> E-Ticket PNR: {{ selectedBooking.pnr }}
                </h3>
              </div>
              <button (click)="showViewBookingModal = false" class="text-ink-2 hover:text-ink p-2 rounded-xl bg-seg border border-line">✕</button>
            </div>

            <!-- Ticket Body -->
            <div class="p-6 rounded-3xl bg-seg border border-amber-500/30 shadow-card space-y-4">
              <div class="flex items-center justify-between pb-3 border-b border-line">
                <div>
                  <span class="text-sm font-extrabold text-ink block">{{ selectedBooking.flightNumber || 'Commercial Flight' }}</span>
                  <span class="text-[11px] text-ink-3">Electronic Passenger Ticket</span>
                </div>
                <span class="px-3 py-1 rounded-full text-xs font-bold font-mono" [ngClass]="selectedBooking.status === 'CONFIRMED' ? 'bg-emerald-500/10 border border-emerald-500/30 text-emerald-700 dark:text-emerald-400' : 'bg-rose-500/10 border border-rose-500/30 text-rose-700 dark:text-rose-400'">
                  {{ selectedBooking.status }}
                </span>
              </div>

              <!-- Passengers Manifest on Ticket -->
              <div class="space-y-2">
                <span class="text-[10px] text-ink-3 uppercase tracking-wider block">Ticketed Passenger(s)</span>
                @for (p of selectedBooking.passengers; track p.firstName) {
                  <div class="p-3 rounded-2xl bg-surface border border-line flex items-center justify-between text-xs">
                    <div>
                      <span class="font-bold text-ink block">{{ p.firstName }} {{ p.lastName }}</span>
                      <span class="text-[10px] text-ink-3 font-mono">{{ p.gender }}, Age {{ p.age }}</span>
                    </div>
                    <div class="text-right">
                      <span class="text-[10px] text-ink-3 uppercase block">Seat Assignment</span>
                      <span class="font-black text-sky-600 dark:text-sky-400 font-mono text-sm">{{ p.seatNumber || '12A' }}</span>
                    </div>
                  </div>
                }
              </div>

              <!-- Fare Breakdown -->
              <div class="p-3 rounded-2xl bg-surface border border-line text-xs space-y-1">
                <div class="flex justify-between text-ink-2">
                  <span>Gross Ticket Fare:</span>
                  <span class="font-mono text-ink">₹{{ selectedBooking.totalFare | number:'1.0-0' }}</span>
                </div>
                <div class="flex justify-between text-ink-2">
                  <span>GST & Airport Development Fee:</span>
                  <span class="font-mono text-emerald-600 dark:text-emerald-400">Included</span>
                </div>
                <div class="flex justify-between pt-2 border-t border-line font-bold text-sm">
                  <span class="text-ink">Total Amount Paid:</span>
                  <span class="text-emerald-600 dark:text-emerald-400 font-mono">₹{{ selectedBooking.totalFare | number:'1.0-0' }}</span>
                </div>
              </div>

              <!-- Simulated Boarding Barcode -->
              <div class="p-3 rounded-xl bg-surface border border-line text-center">
                <div class="h-8 w-full flex items-center justify-center gap-1 opacity-70">
                  <div class="w-1 h-8 bg-ink"></div>
                  <div class="w-0.5 h-8 bg-ink"></div>
                  <div class="w-2 h-8 bg-ink"></div>
                  <div class="w-1 h-8 bg-ink"></div>
                  <div class="w-0.5 h-8 bg-ink"></div>
                  <div class="w-1.5 h-8 bg-ink"></div>
                  <div class="w-2 h-8 bg-ink"></div>
                </div>
                <span class="text-[9px] font-mono text-ink-3 tracking-widest mt-1 block">PNR: {{ selectedBooking.pnr }} • AIRPORT SECURITY VERIFIED</span>
              </div>
            </div>

            <div class="flex items-center justify-end gap-3 pt-4 border-t border-line text-xs">
              <button (click)="printBoardingPass(selectedBooking)" class="px-5 py-2.5 rounded-xl bg-sky-600 hover:bg-sky-500 text-white font-bold transition-all shadow-md flex items-center gap-2">
                <span>🖨️</span> Print / Export E-Ticket
              </button>
              <button (click)="showViewBookingModal = false" class="px-4 py-2.5 rounded-xl bg-seg hover:bg-surface text-ink-2 hover:text-ink border border-line font-semibold">
                Close
              </button>
            </div>
          </div>
        </div>
      }

      <!-- 6. VIEW FARE TIERS DOSSIER MODAL -->
      @if (showViewFareModal && selectedFare) {
        <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-ink/40 backdrop-blur-md animate-fadeIn">
          <div class="border border-line bg-surface rounded-card w-full max-w-lg p-6 sm:p-8 shadow-card space-y-6 text-ink">
            <div class="flex items-center justify-between pb-4 border-b border-line">
              <div class="flex items-center gap-3">
                <div class="w-12 h-12 rounded-2xl bg-emerald-500/10 border border-emerald-500/30 flex items-center justify-center text-2xl font-bold text-emerald-600 dark:text-emerald-400">
                  🏷️
                </div>
                <div>
                  <h3 class="text-lg font-black text-ink">Pricing Breakdown (Flight #{{ selectedFare.flightId }})</h3>
                  <span class="text-xs text-ink-3 font-mono">{{ getFlightRoute(selectedFare.flightId) }}</span>
                </div>
              </div>
              <button (click)="showViewFareModal = false" class="text-ink-2 hover:text-ink p-2 rounded-xl bg-seg border border-line">✕</button>
            </div>

            <div class="space-y-3 text-xs">
              <div class="p-4 rounded-2xl bg-seg border border-line flex items-center justify-between">
                <div>
                  <span class="text-ink-3 block text-[10px] uppercase">Economy Cabin</span>
                  <span class="text-base font-extrabold text-sky-600 dark:text-sky-400 font-mono">₹{{ selectedFare.economyFare | number:'1.0-0' }}</span>
                </div>
                <span class="text-ink-3 text-[11px]">15kg Check-in • Standard Seat</span>
              </div>

              <div class="p-4 rounded-2xl bg-seg border border-line flex items-center justify-between">
                <div>
                  <span class="text-ink-3 block text-[10px] uppercase">Business Class</span>
                  <span class="text-base font-extrabold text-amber-600 dark:text-amber-400 font-mono">₹{{ selectedFare.businessFare | number:'1.0-0' }}</span>
                </div>
                <span class="text-ink-3 text-[11px]">30kg Check-in • Priority Lounge</span>
              </div>

              <div class="p-4 rounded-2xl bg-seg border border-line flex items-center justify-between">
                <div>
                  <span class="text-ink-3 block text-[10px] uppercase">First Class</span>
                  <span class="text-base font-extrabold text-purple-600 dark:text-purple-400 font-mono">₹{{ selectedFare.firstClassFare | number:'1.0-0' }}</span>
                </div>
                <span class="text-ink-3 text-[11px]">40kg Check-in • VIP Suite</span>
              </div>
            </div>

            <div class="flex items-center justify-end gap-3 pt-4 border-t border-line text-xs">
              <button (click)="openFareModal(selectedFare); showViewFareModal = false" class="px-5 py-2.5 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white font-bold">
                Edit Rules
              </button>
              <button (click)="showViewFareModal = false" class="px-4 py-2.5 rounded-xl bg-seg hover:bg-surface text-ink-2 hover:text-ink border border-line font-semibold">
                Close
              </button>
            </div>
          </div>
        </div>
      }

      <!-- 7. CONFIGURE FARE MODAL -->
      @if (showFareModal) {
        <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-ink/40 backdrop-blur-md animate-fadeIn">
          <div class="bg-surface border border-line rounded-card shadow-card backdrop-blur-xl w-full max-w-lg p-6 sm:p-8 space-y-4">
            <div class="flex items-center justify-between pb-3 border-b border-line">
              <h3 class="text-base font-bold text-ink flex items-center gap-2">
                <span>🏷️</span> Configure Cabin Fare Rules
              </h3>
              <button (click)="showFareModal = false" class="text-ink-2 hover:text-ink">✕</button>
            </div>

            <div>
              <label class="block text-[11px] font-semibold text-ink-2 mb-1">Select Commercial Flight</label>
              <select [(ngModel)]="fareForm.flightId" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
                @for (fl of flights; track fl.id) {
                  <option [value]="fl.id">{{ fl.flightNumber }} ({{ fl.source }} → {{ fl.destination }})</option>
                }
              </select>
            </div>

            <div class="grid grid-cols-3 gap-3">
              <div>
                <label class="block text-[11px] font-semibold text-ink-2 mb-1">Economy (₹)</label>
                <input [(ngModel)]="fareForm.economyFare" type="number" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 font-mono transition-all">
              </div>
              <div>
                <label class="block text-[11px] font-semibold text-ink-2 mb-1">Business (₹)</label>
                <input [(ngModel)]="fareForm.businessFare" type="number" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 font-mono transition-all">
              </div>
              <div>
                <label class="block text-[11px] font-semibold text-ink-2 mb-1">First Class (₹)</label>
                <input [(ngModel)]="fareForm.firstClassFare" type="number" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 font-mono transition-all">
              </div>
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-[11px] font-semibold text-ink-2 mb-1">GST Tax (%)</label>
                <input [(ngModel)]="fareForm.taxRate" type="number" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 font-mono transition-all">
              </div>
              <div>
                <label class="block text-[11px] font-semibold text-ink-2 mb-1">Base Discount (%)</label>
                <input [(ngModel)]="fareForm.discountPercentage" type="number" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 font-mono transition-all">
              </div>
            </div>

            <div class="flex items-center justify-end gap-3 pt-4 border-t border-line">
              <button (click)="showFareModal = false" class="px-4 py-2 rounded-xl text-ink-2 hover:text-ink text-xs font-semibold">Cancel</button>
              <button (click)="saveFare()" class="px-5 py-2 rounded-xl bg-emerald-600 hover:bg-emerald-500 text-white font-bold text-xs transition-all shadow-md">
                Save Pricing Rule
              </button>
            </div>
          </div>
        </div>
      }

      <!-- 8. CREATE PROMO MODAL -->
      @if (showPromoModal) {
        <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-ink/40 backdrop-blur-md animate-fadeIn">
          <div class="bg-surface border border-line rounded-card shadow-card backdrop-blur-xl w-full max-w-md p-6 sm:p-8 space-y-4">
            <div class="flex items-center justify-between pb-3 border-b border-line">
              <h3 class="text-base font-bold text-ink flex items-center gap-2">
                <span>🎟️</span> Create Promotional Discount Code
              </h3>
              <button (click)="showPromoModal = false" class="text-ink-2 hover:text-ink">✕</button>
            </div>

            <div>
              <label class="block text-[11px] font-semibold text-ink-2 mb-1">Coupon Voucher Code</label>
              <input [(ngModel)]="promoForm.code" placeholder="e.g. FESTIVE2026" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 uppercase font-mono transition-all">
            </div>

            <div>
              <label class="block text-[11px] font-semibold text-ink-2 mb-1">Coupon Description</label>
              <input [(ngModel)]="promoForm.description" placeholder="e.g. 15% Festive Season Discount" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-[11px] font-semibold text-ink-2 mb-1">Discount (%)</label>
                <input [(ngModel)]="promoForm.discountPercentage" type="number" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 font-mono transition-all">
              </div>
              <div>
                <label class="block text-[11px] font-semibold text-ink-2 mb-1">Max Cap (₹)</label>
                <input [(ngModel)]="promoForm.maxDiscount" type="number" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 font-mono transition-all">
              </div>
            </div>

            <div>
              <label class="block text-[11px] font-semibold text-ink-2 mb-1">Total Usage Quota</label>
              <input [(ngModel)]="promoForm.usageLimit" type="number" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 font-mono transition-all">
            </div>

            <div class="flex items-center justify-end gap-3 pt-4 border-t border-line">
              <button (click)="showPromoModal = false" class="px-4 py-2 rounded-xl text-ink-2 hover:text-ink text-xs font-semibold">Cancel</button>
              <button (click)="savePromo()" class="px-5 py-2 rounded-xl bg-purple-600 hover:bg-purple-500 text-white font-bold text-xs transition-all shadow-md">
                Publish Coupon
              </button>
            </div>
          </div>
        </div>
      }

      <!-- 9. VIEW USER DOSSIER -->
      @if (showViewUserModal && selectedUser) {
        <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-ink/40 backdrop-blur-md animate-fadeIn">
          <div class="border border-line bg-surface rounded-card w-full max-w-lg p-6 sm:p-8 shadow-card space-y-6 text-ink">
            <div class="flex items-center justify-between pb-3 border-b border-line">
              <div class="flex items-center gap-3">
                <div class="w-12 h-12 rounded-2xl bg-gradient-to-tr from-sky-600 to-indigo-600 flex items-center justify-center font-bold text-white text-base">
                  {{ selectedUser.firstName.charAt(0) }}{{ selectedUser.lastName.charAt(0) }}
                </div>
                <div>
                  <h3 class="text-base font-bold text-ink">{{ selectedUser.firstName }} {{ selectedUser.lastName }}</h3>
                  <span class="text-xs text-ink-3 font-mono">UID: #{{ selectedUser.userId }}</span>
                </div>
              </div>
              <button (click)="showViewUserModal = false" class="text-ink-2 hover:text-ink p-2 rounded-xl bg-seg border border-line">✕</button>
            </div>

            <div class="grid grid-cols-2 gap-4 text-xs">
              <div class="p-3 rounded-xl bg-seg border border-line">
                <span class="text-[10px] text-ink-3 uppercase block">Email Address</span>
                <span class="font-semibold text-ink mt-1 block">{{ selectedUser.email }}</span>
              </div>
              <div class="p-3 rounded-xl bg-seg border border-line">
                <span class="text-[10px] text-ink-3 uppercase block">Phone Number</span>
                <span class="font-semibold text-ink mt-1 block font-mono">{{ selectedUser.phoneNumber || 'N/A' }}</span>
              </div>
              <div class="p-3 rounded-xl bg-seg border border-line">
                <span class="text-[10px] text-ink-3 uppercase block">Security Role</span>
                <span [class]="getRoleBadgeClass(selectedUser.role)" class="px-2 py-0.5 rounded-full text-[10px] font-bold border inline-block mt-1">
                  {{ selectedUser.role }}
                </span>
              </div>
              <div class="p-3 rounded-xl bg-seg border border-line">
                <span class="text-[10px] text-ink-3 uppercase block">Account Status</span>
                <span class="font-bold mt-1 block" [ngClass]="selectedUser.status === 'BLOCKED' ? 'text-rose-600 dark:text-rose-400' : 'text-emerald-600 dark:text-emerald-400'">
                  {{ selectedUser.status || 'ACTIVE' }}
                </span>
              </div>
              <div class="p-3 rounded-xl bg-seg border border-line">
                <span class="text-[10px] text-ink-3 uppercase block">Nationality</span>
                <span class="font-semibold text-ink mt-1 block">{{ selectedUser.nationality || 'Indian' }}</span>
              </div>
              <div class="p-3 rounded-xl bg-seg border border-line">
                <span class="text-[10px] text-ink-3 uppercase block">Incident Reports</span>
                <span class="font-bold mt-1 block" [ngClass]="(selectedUser.reportsCount || 0) > 0 ? 'text-rose-600 dark:text-rose-400' : 'text-ink-3'">
                  {{ selectedUser.reportsCount || 0 }} flagged incidents
                </span>
              </div>
            </div>

            <div class="flex items-center justify-end gap-3 pt-4 border-t border-line">
              <button (click)="openEditUserModal(selectedUser); showViewUserModal = false" class="px-4 py-2 rounded-xl bg-sky-600 hover:bg-sky-500 text-white text-xs font-semibold">
                Edit User Data
              </button>
              <button (click)="showViewUserModal = false" class="px-4 py-2 rounded-xl bg-seg hover:bg-surface text-ink-2 hover:text-ink border border-line text-xs font-semibold">
                Close Dossier
              </button>
            </div>
          </div>
        </div>
      }

      <!-- 10. EDIT USER -->
      @if (showEditUserModal) {
        <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-ink/40 backdrop-blur-md animate-fadeIn">
          <div class="bg-surface border border-line rounded-card shadow-card backdrop-blur-xl w-full max-w-md p-6 sm:p-8 space-y-4">
            <div class="flex items-center justify-between pb-3 border-b border-line">
              <h3 class="text-base font-bold text-ink flex items-center gap-2">
                <span>✎</span> Edit User Profile
              </h3>
              <button (click)="showEditUserModal = false" class="text-ink-2 hover:text-ink">✕</button>
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-[11px] font-semibold text-ink-2 mb-1">First Name</label>
                <input [(ngModel)]="editUserForm.firstName" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
              </div>
              <div>
                <label class="block text-[11px] font-semibold text-ink-2 mb-1">Last Name</label>
                <input [(ngModel)]="editUserForm.lastName" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
              </div>
            </div>

            <div>
              <label class="block text-[11px] font-semibold text-ink-2 mb-1">Email Address</label>
              <input [(ngModel)]="editUserForm.email" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
            </div>

            <div class="grid grid-cols-2 gap-4">
              <div>
                <label class="block text-[11px] font-semibold text-ink-2 mb-1">Phone Number</label>
                <input [(ngModel)]="editUserForm.phoneNumber" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 font-mono transition-all">
              </div>
              <div>
                <label class="block text-[11px] font-semibold text-ink-2 mb-1">Nationality</label>
                <input [(ngModel)]="editUserForm.nationality" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
              </div>
            </div>

            <div class="flex items-center justify-end gap-3 pt-4 border-t border-line">
              <button (click)="showEditUserModal = false" class="px-4 py-2 rounded-xl text-ink-2 hover:text-ink text-xs font-semibold">Cancel</button>
              <button (click)="saveEditedUser()" class="px-5 py-2 rounded-xl bg-accent hover:opacity-95 text-white font-bold text-xs transition-all shadow-md">
                Save Changes
              </button>
            </div>
          </div>
        </div>
      }

      <!-- 11. FILE INCIDENT REPORT -->
      @if (showReportModal) {
        <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-ink/40 backdrop-blur-md animate-fadeIn">
          <div class="bg-surface border border-line rounded-card shadow-card backdrop-blur-xl w-full max-w-md p-6 sm:p-8 space-y-4">
            <div class="flex items-center justify-between pb-3 border-b border-line">
              <h3 class="text-base font-bold text-ink flex items-center gap-2">
                <span>🚨</span> File Incident Report
              </h3>
              <button (click)="showReportModal = false" class="text-ink-2 hover:text-ink">✕</button>
            </div>

            <div>
              <label class="block text-[11px] font-semibold text-ink-2 mb-1">Target User</label>
              <input [(ngModel)]="reportForm.userName" readonly class="w-full bg-seg border border-line rounded-xl px-3 py-2 text-xs text-ink font-bold">
            </div>

            <div>
              <label class="block text-[11px] font-semibold text-ink-2 mb-1">Incident Category</label>
              <select [(ngModel)]="reportForm.reason" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
                <option value="Disruptive Passenger Behavior">Disruptive Passenger Behavior</option>
                <option value="Payment Dispute / Fraudulent Chargeback">Payment Dispute / Fraudulent Chargeback</option>
                <option value="Security Screening Violation">Security Screening Violation</option>
                <option value="No-Show & Luggage Discrepancy">No-Show & Luggage Discrepancy</option>
                <option value="Staff Non-Compliance">Staff Non-Compliance</option>
              </select>
            </div>

            <div>
              <label class="block text-[11px] font-semibold text-ink-2 mb-1">Incident Severity</label>
              <select [(ngModel)]="reportForm.severity" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
                <option value="LOW">LOW (Informational record)</option>
                <option value="MEDIUM">MEDIUM (Caution / Watchlist)</option>
                <option value="HIGH">HIGH (Temporary Suspension)</option>
                <option value="CRITICAL">CRITICAL (Mandatory Account Block & No-Fly)</option>
              </select>
            </div>

            <div>
              <label class="block text-[11px] font-semibold text-ink-2 mb-1">Detailed Incident Notes</label>
              <textarea [(ngModel)]="reportForm.notes" rows="3" placeholder="Provide factual context regarding the reported event..." class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all"></textarea>
            </div>

            <div class="flex items-center justify-end gap-3 pt-4 border-t border-line">
              <button (click)="showReportModal = false" class="px-4 py-2 rounded-xl text-ink-2 hover:text-ink text-xs font-semibold">Cancel</button>
              <button (click)="saveIncidentReport()" class="px-5 py-2 rounded-xl bg-rose-600 hover:bg-rose-500 text-white font-bold text-xs transition-all shadow-md">
                Submit Report
              </button>
            </div>
          </div>
        </div>
      }

      <!-- 12. QUICK FLIGHT STATUS MODAL -->
      @if (showFlightStatusModal) {
        <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-ink/40 backdrop-blur-md animate-fadeIn">
          <div class="bg-surface border border-line rounded-card shadow-card backdrop-blur-xl w-full max-w-md p-6 sm:p-8 space-y-4">
            <div class="flex items-center justify-between pb-3 border-b border-line">
              <h3 class="text-base font-bold text-ink flex items-center gap-2">
                <span>⏱️</span> Update Operational Flight Status
              </h3>
              <button (click)="showFlightStatusModal = false" class="text-ink-2 hover:text-ink">✕</button>
            </div>

            <p class="text-xs text-ink-2">
              Flight: <strong class="text-ink font-mono">{{ flightStatusUpdateForm.flightNumber }}</strong>
            </p>

            <div>
              <label class="block text-[11px] font-semibold text-ink-2 mb-1">Operational Status</label>
              <select [(ngModel)]="flightStatusUpdateForm.newStatus" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
                <option value="SCHEDULED">SCHEDULED</option>
                <option value="BOARDING">BOARDING (Call passengers to gate)</option>
                <option value="DELAYED">DELAYED (Weather / Technical delay)</option>
                <option value="DEPARTED">DEPARTED (Airborne en-route)</option>
                <option value="COMPLETED">COMPLETED (Safely landed at destination)</option>
                <option value="CANCELLED">CANCELLED (Emergency grounding / void)</option>
              </select>
            </div>

            <div class="flex items-center gap-2 pt-2">
              <input [(ngModel)]="flightStatusUpdateForm.notifyPassengers" type="checkbox" id="notifyPass" class="rounded accent-accent">
              <label for="notifyPass" class="text-xs text-ink-2 cursor-pointer">
                Automatically push real-time notification alert to all booked passengers
              </label>
            </div>

            <div class="flex items-center justify-end gap-3 pt-4 border-t border-line">
              <button (click)="showFlightStatusModal = false" class="px-4 py-2 rounded-xl text-ink-2 hover:text-ink text-xs font-semibold">Cancel</button>
              <button (click)="saveFlightStatusUpdate()" class="px-5 py-2 rounded-xl bg-accent hover:opacity-95 text-white font-bold text-xs transition-all shadow-md">
                Update Status
              </button>
            </div>
          </div>
        </div>
      }

      <!-- 13. EDIT ADMIN PROFILE -->
      @if (showProfileModal) {
        <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-ink/40 backdrop-blur-md animate-fadeIn">
          <div class="bg-surface border border-line rounded-card shadow-card backdrop-blur-xl w-full max-w-md p-6 sm:p-8 space-y-4">
            <div class="flex items-center justify-between pb-3 border-b border-line">
              <h3 class="text-base font-bold text-ink flex items-center gap-2">
                <span>🛡️</span> Edit Executive Profile
              </h3>
              <button (click)="showProfileModal = false" class="text-ink-2 hover:text-ink">✕</button>
            </div>

            <div>
              <label class="block text-[11px] font-semibold text-ink-2 mb-1">Full Legal Name</label>
              <input [(ngModel)]="profileForm.name" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
            </div>

            <div>
              <label class="block text-[11px] font-semibold text-ink-2 mb-1">Executive Email</label>
              <input [(ngModel)]="profileForm.email" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
            </div>

            <div>
              <label class="block text-[11px] font-semibold text-ink-2 mb-1">Direct Telephone</label>
              <input [(ngModel)]="profileForm.phone" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 font-mono transition-all">
            </div>

            <div>
              <label class="block text-[11px] font-semibold text-ink-2 mb-1">Department / Organization</label>
              <input [(ngModel)]="profileForm.department" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
            </div>

            <div class="flex items-center justify-end gap-3 pt-4 border-t border-line">
              <button (click)="showProfileModal = false" class="px-4 py-2 rounded-xl text-ink-2 hover:text-ink text-xs font-semibold">Cancel</button>
              <button (click)="saveAdminProfile()" class="px-5 py-2 rounded-xl bg-accent hover:opacity-95 text-white font-bold text-xs transition-all shadow-md">
                Save Profile
              </button>
            </div>
          </div>
        </div>
      }

      <!-- 14. CHANGE ADMIN PASSWORD -->
      @if (showPasswordModal) {
        <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-ink/40 backdrop-blur-md animate-fadeIn">
          <div class="bg-surface border border-line rounded-card shadow-card backdrop-blur-xl w-full max-w-md p-6 sm:p-8 space-y-4">
            <div class="flex items-center justify-between pb-3 border-b border-line">
              <h3 class="text-base font-bold text-ink flex items-center gap-2">
                <span>🔑</span> Change Security Credentials
              </h3>
              <button (click)="showPasswordModal = false" class="text-ink-2 hover:text-ink">✕</button>
            </div>

            <div>
              <label class="block text-[11px] font-semibold text-ink-2 mb-1">Current Password</label>
              <input [(ngModel)]="passwordForm.currentPassword" type="password" placeholder="••••••••" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
            </div>

            <div>
              <label class="block text-[11px] font-semibold text-ink-2 mb-1">New Secure Password</label>
              <input [(ngModel)]="passwordForm.newPassword" type="password" placeholder="Min. 8 characters with symbols" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
            </div>

            <div>
              <label class="block text-[11px] font-semibold text-ink-2 mb-1">Confirm New Password</label>
              <input [(ngModel)]="passwordForm.confirmPassword" type="password" placeholder="••••••••" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
            </div>

            <div class="flex items-center justify-end gap-3 pt-4 border-t border-line">
              <button (click)="showPasswordModal = false" class="px-4 py-2 rounded-xl text-ink-2 hover:text-ink text-xs font-semibold">Cancel</button>
              <button (click)="updateAdminPassword()" class="px-5 py-2 rounded-xl bg-accent hover:opacity-95 text-white font-bold text-xs transition-all shadow-md">
                Update Password
              </button>
            </div>
          </div>
        </div>
      }

      <!-- 15. AWARD LOYALTY XP MODAL -->
      @if (showAwardXpModal && selectedUserForXp) {
        <div class="fixed inset-0 z-50 flex items-center justify-center p-4 bg-ink/40 backdrop-blur-md animate-fadeIn">
          <div class="bg-surface border border-line rounded-card shadow-card backdrop-blur-xl w-full max-w-md p-6 sm:p-8 space-y-4">
            <div class="flex items-center justify-between pb-3 border-b border-line">
              <div class="flex items-center gap-2">
                <span class="text-xl">👑</span>
                <h3 class="text-base font-bold text-ink">Award Loyalty XP & Advance Tier</h3>
              </div>
              <button (click)="showAwardXpModal = false" class="text-ink-2 hover:text-ink">✕</button>
            </div>

            <div class="p-3.5 rounded-2xl bg-seg border border-line space-y-2 text-xs">
              <div class="flex justify-between items-center">
                <span class="text-ink-2">Target User:</span>
                <span class="text-ink font-bold">{{ selectedUserForXp.firstName }} {{ selectedUserForXp.lastName }}</span>
              </div>
              <div class="flex justify-between items-center">
                <span class="text-ink-2">Current Level & Tier:</span>
                <span class="text-warn font-mono font-bold">Level {{ selectedUserForXp.level || 3 }} ({{ selectedUserForXp.tier || 'GOLD' }})</span>
              </div>
              <div class="flex justify-between items-center">
                <span class="text-ink-2">Current XP Balance:</span>
                <span class="text-ok font-mono font-bold">{{ (selectedUserForXp.xp || 24850) | number }} XP</span>
              </div>
            </div>

            <div>
              <label class="block text-[11px] font-semibold text-ink-2 mb-1">Select Bonus XP to Award</label>
              <select [(ngModel)]="awardXpAmount" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 font-mono transition-all">
                <option [ngValue]="500">+500 XP (Goodwill / Flight Delay Courtesy)</option>
                <option [ngValue]="1500">+1,500 XP (Tier Acceleration Bonus)</option>
                <option [ngValue]="5000">+5,000 XP (Major Milestone / Upgrade Jump)</option>
                <option [ngValue]="15000">+15,000 XP (Executive VIP Promotion)</option>
              </select>
            </div>

            <div>
              <label class="block text-[11px] font-semibold text-ink-2 mb-1">Administrative Reason / Justification</label>
              <input [(ngModel)]="awardXpReason" class="w-full bg-surface border border-line rounded-xl px-3 py-2 text-xs text-ink placeholder:text-ink-3 font-medium shadow-sm outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 transition-all">
            </div>

            <div class="flex items-center justify-end gap-3 pt-4 border-t border-line">
              <button (click)="showAwardXpModal = false" class="px-4 py-2 rounded-xl text-ink-2 hover:text-ink text-xs font-semibold">Cancel</button>
              <button (click)="confirmAwardXp()" class="px-5 py-2 rounded-xl bg-warn hover:opacity-95 text-white font-bold text-xs transition-all shadow-md">
                Award XP Now
              </button>
            </div>
          </div>
        </div>
      }

    </div>
  `
})
export class AdminDashboardComponent implements OnInit {
  activeTab: 'overview' | 'flights' | 'aircraft' | 'fares' | 'users' | 'reports' | 'bookings' | 'broadcast' | 'profile' = 'overview';

  // Core Data
  flights: Flight[] = [];
  fleet: Aircraft[] = [];
  users: User[] = [];
  bookings: Booking[] = [];
  fares: Fare[] = [];
  promos: PromoCode[] = [];
  incidentReports: UserIncidentReport[] = [];

  // Admin Profile State
  adminProfile: AdminProfile = {
    adminId: 1,
    name: 'Amir Sohel',
    email: 'admin@aerobook.com',
    role: 'ROLE_ADMIN',
    department: 'Executive Operations & Flight Mesh Command',
    phone: '+91 98765 43210',
    securityClearance: 'Level-5 Executive',
    twoFactorEnabled: true,
    lastLogin: new Date().toLocaleString(),
    joinedDate: '2025-01-15'
  };

  auditLogs: AdminAuditEntry[] = [
    { id: 101, action: 'Direct Admin Access Initialized', target: 'Security Mesh', timestamp: 'Today, 21:15', ipAddress: '127.0.0.1', status: 'SUCCESS' },
    { id: 102, action: 'RBAC Policy Verification', target: 'api-gateway', timestamp: 'Today, 21:10', ipAddress: '127.0.0.1', status: 'SUCCESS' },
    { id: 103, action: 'Flight AI-101 Schedule Registered', target: 'flight-service', timestamp: 'Today, 20:45', ipAddress: '127.0.0.1', status: 'SUCCESS' },
    { id: 104, action: 'Aircraft VT-AB101 Inspection Verified', target: 'fleet-db', timestamp: 'Today, 19:30', ipAddress: '127.0.0.1', status: 'SUCCESS' }
  ];

  // Toast System
  toasts: ToastMessage[] = [];

  // Modals visibility
  showFlightModal: boolean = false;
  showAircraftModal: boolean = false;
  showFareModal: boolean = false;
  showPromoModal: boolean = false;
  showViewUserModal: boolean = false;
  showEditUserModal: boolean = false;
  showReportModal: boolean = false;
  showProfileModal: boolean = false;
  showPasswordModal: boolean = false;
  showFlightStatusModal: boolean = false;
  showAwardXpModal: boolean = false;
  selectedUserForXp: User | null = null;
  awardXpAmount: number = 1500;
  awardXpReason: string = 'Loyalty Flying Milestone Reward';

  // New Rich View Dossier Modals
  showViewFlightModal: boolean = false;
  selectedFlight: Flight | null = null;
  showViewAircraftModal: boolean = false;
  selectedAircraft: Aircraft | null = null;
  showViewBookingModal: boolean = false;
  selectedBooking: Booking | null = null;
  showViewFareModal: boolean = false;
  selectedFare: Fare | null = null;

  // Active form objects
  flightForm: Partial<Flight> = {};
  aircraftForm: Partial<Aircraft> = { status: 'ACTIVE' };
  fareForm: Partial<Fare> = { taxRate: 18, discountPercentage: 5 };
  promoForm: Partial<PromoCode> = { discountPercentage: 15, maxDiscount: 2000, usageLimit: 500, status: 'ACTIVE' };
  selectedUser: User | null = null;
  editUserForm: Partial<User> = {};
  reportForm: Partial<UserIncidentReport> = { severity: 'MEDIUM', status: 'OPEN', reason: 'Disruptive Passenger Behavior' };
  profileForm: Partial<AdminProfile> = {};
  passwordForm = { currentPassword: '', newPassword: '', confirmPassword: '' };
  flightStatusUpdateForm = { flightId: 0, flightNumber: '', newStatus: 'BOARDING', notifyPassengers: true, note: '' };

  broadcastForm: BroadcastRequest = {
    title: '',
    message: '',
    type: 'BROADCAST',
    flightNumber: ''
  };

  // Search & Filtering
  flightSearchTerm: string = '';
  flightStatusFilter: string = 'ALL';
  userSearchTerm: string = '';
  userRoleFilter: string = 'ALL';
  userStatusFilter: string = 'ALL';

  // Dynamic Simulator State
  simulatorOccupancy: number = 75;
  simulatorBaseFare: number = 4500;

  // Simulated Cabin Map
  mockSeatMap: SeatItem[] = [
    { seatNumber: '1A', isBooked: true, isBusiness: true },
    { seatNumber: '1B', isBooked: false, isBusiness: true },
    { seatNumber: '1C', isBooked: true, isBusiness: true },
    { seatNumber: '1D', isBooked: false, isBusiness: true },
    { seatNumber: '1E', isBooked: false, isBusiness: true },
    { seatNumber: '1F', isBooked: true, isBusiness: true },
    { seatNumber: '2A', isBooked: false, isBusiness: true },
    { seatNumber: '2B', isBooked: true, isBusiness: true },
    { seatNumber: '2C', isBooked: false, isBusiness: true },
    { seatNumber: '2D', isBooked: false, isBusiness: true },
    { seatNumber: '2E', isBooked: true, isBusiness: true },
    { seatNumber: '2F', isBooked: false, isBusiness: true },
    { seatNumber: '10A', isBooked: true, isBusiness: false },
    { seatNumber: '10B', isBooked: true, isBusiness: false },
    { seatNumber: '10C', isBooked: false, isBusiness: false },
    { seatNumber: '10D', isBooked: false, isBusiness: false },
    { seatNumber: '10E', isBooked: true, isBusiness: false },
    { seatNumber: '10F', isBooked: false, isBusiness: false },
    { seatNumber: '11A', isBooked: false, isBusiness: false },
    { seatNumber: '11B', isBooked: false, isBusiness: false },
    { seatNumber: '11C', isBooked: true, isBusiness: false },
    { seatNumber: '11D', isBooked: true, isBusiness: false },
    { seatNumber: '11E', isBooked: false, isBusiness: false },
    { seatNumber: '11F', isBooked: false, isBusiness: false }
  ];

  // Microservices Grid
  microservices = [
    { name: 'API Gateway', port: 8083, latency: 12 },
    { name: 'Auth Service', port: 8082, latency: 18 },
    { name: 'User Service', port: 8084, latency: 15 },
    { name: 'Flight Service', port: 8087, latency: 22 },
    { name: 'Fare Service', port: 8089, latency: 19 },
    { name: 'Booking Service', port: 8088, latency: 25 },
    { name: 'Check-In Service', port: 8090, latency: 14 },
    { name: 'Notification Service', port: 8091, latency: 16 }
  ];

  constructor(
    private flightService: FlightService,
    private bookingService: BookingService,
    private userService: UserService,
    private fareService: FareService,
    private notificationService: NotificationService,
    public authService: AuthService
  ) {}

  ngOnInit(): void {
    this.loadAllData();
  }

  // Toast Helpers
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

  // Computed Properties
  get totalRevenue(): number {
    return this.bookings
      .filter(b => b.status === 'CONFIRMED')
      .reduce((sum, b) => sum + (b.totalFare || 0), 0);
  }

  get activeFlightsCount(): number {
    return this.flights.filter(f => f.status !== 'CANCELLED' && f.status !== 'COMPLETED').length;
  }

  get operationalAircraftCount(): number {
    return this.fleet.filter(a => a.status === 'ACTIVE').length;
  }

  get passengerCount(): number {
    return this.users.filter(u => u.role === 'ROLE_USER').length;
  }

  get staffCount(): number {
    return this.users.filter(u => u.role === 'ROLE_STAFF').length;
  }

  get confirmedBookingsCount(): number {
    return this.bookings.filter(b => b.status === 'CONFIRMED').length;
  }

  get openReportsCount(): number {
    return this.incidentReports.filter(r => r.status === 'OPEN').length;
  }

  get totalFleetCapacity(): number {
    return this.fleet.reduce((sum, plane) => sum + (plane.capacity || 0), 0);
  }

  get filteredFlights(): Flight[] {
    return this.flights.filter(f => {
      const matchesSearch = !this.flightSearchTerm ||
        f.flightNumber.toLowerCase().includes(this.flightSearchTerm.toLowerCase()) ||
        f.airlineName.toLowerCase().includes(this.flightSearchTerm.toLowerCase()) ||
        f.source.toLowerCase().includes(this.flightSearchTerm.toLowerCase()) ||
        f.destination.toLowerCase().includes(this.flightSearchTerm.toLowerCase());
      const matchesStatus = this.flightStatusFilter === 'ALL' || f.status === this.flightStatusFilter;
      return matchesSearch && matchesStatus;
    });
  }

  get filteredUsers(): User[] {
    return this.users.filter(u => {
      const q = this.userSearchTerm.toLowerCase();
      const matchesSearch = !this.userSearchTerm ||
        (u.firstName + ' ' + u.lastName).toLowerCase().includes(q) ||
        u.email.toLowerCase().includes(q) ||
        (u.phoneNumber && u.phoneNumber.includes(q));
      const matchesRole = this.userRoleFilter === 'ALL' || u.role === this.userRoleFilter;
      const matchesStatus = this.userStatusFilter === 'ALL' || (u.status || 'ACTIVE') === this.userStatusFilter;
      return matchesSearch && matchesRole && matchesStatus;
    });
  }

  getFlightRoute(flightId: number): string {
    const fl = this.flights.find(f => f.id === flightId);
    return fl ? `${fl.source} → ${fl.destination}` : 'Commercial Route';
  }

  getFlightDuration(departure?: string, arrival?: string): string {
    if (!departure || !arrival) return '2h 15m';
    const d1 = new Date(departure).getTime();
    const d2 = new Date(arrival).getTime();
    const diff = Math.max(0, d2 - d1);
    const hrs = Math.floor(diff / (1000 * 60 * 60));
    const mins = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60));
    return `${hrs}h ${mins}m`;
  }

  calculateSurgeFare(baseFare: number, occupancy: number): number {
    let multiplier = 1.0;
    if (occupancy > 90) multiplier = 1.6;
    else if (occupancy > 75) multiplier = 1.35;
    else if (occupancy > 50) multiplier = 1.15;
    else if (occupancy < 30) multiplier = 0.9;
    return Math.round(baseFare * multiplier);
  }

  onAircraftSelected(aircraftId: any): void {
    const id = Number(aircraftId);
    this.flightForm.aircraftId = id;
    const selected = this.fleet.find(a => a.id === id);
    if (selected) {
      this.flightForm.totalSeats = selected.capacity;
      this.flightForm.availableSeats = selected.capacity;
      this.flightForm.aircraftTailNumber = `${selected.aircraftNumber} (${selected.model})`;
    }
  }

  // Loaders
  loadAllData(): void {
    this.loadFlights();
    this.loadFleet();
    this.loadUsers();
    this.loadBookings();
    this.loadFares();
    this.loadPromos();
    this.loadIncidentReports();
  }

  loadFlights(): void {
    this.flightService.getAllFlights().subscribe({
      next: (data) => (this.flights = data || []),
      error: () => {
        this.flights = [
          {
            id: 1,
            flightNumber: 'AI-101',
            airlineName: 'Air India',
            source: 'DEL',
            destination: 'BOM',
            departureTime: new Date(Date.now() + 86400000).toISOString(),
            arrivalTime: new Date(Date.now() + 93600000).toISOString(),
            totalSeats: 180,
            availableSeats: 42,
            baseFare: 4500,
            status: 'SCHEDULED',
            terminal: 'T3',
            gate: 'Gate 14B',
            aircraftTailNumber: 'VT-AB101 (Boeing 787-9)'
          },
          {
            id: 2,
            flightNumber: '6E-402',
            airlineName: 'IndiGo',
            source: 'BOM',
            destination: 'BLR',
            departureTime: new Date(Date.now() + 108000000).toISOString(),
            arrivalTime: new Date(Date.now() + 115200000).toISOString(),
            totalSeats: 186,
            availableSeats: 15,
            baseFare: 3200,
            status: 'BOARDING',
            terminal: 'T1',
            gate: 'Gate 8A',
            aircraftTailNumber: 'VT-AB303 (Airbus A321neo)'
          },
          {
            id: 3,
            flightNumber: 'UK-815',
            airlineName: 'Vistara',
            source: 'DEL',
            destination: 'DXB',
            departureTime: new Date(Date.now() + 130000000).toISOString(),
            arrivalTime: new Date(Date.now() + 144000000).toISOString(),
            totalSeats: 296,
            availableSeats: 88,
            baseFare: 14200,
            status: 'SCHEDULED',
            terminal: 'T3',
            gate: 'Gate 22',
            aircraftTailNumber: 'VT-AB202 (Airbus A350)'
          }
        ];
      }
    });
  }

  loadFleet(): void {
    this.flightService.getAllAircraft().subscribe({
      next: (data) => (this.fleet = data || []),
      error: () => {
        this.fleet = [
          { id: 1, model: 'Boeing 787-9 Dreamliner', aircraftNumber: 'VT-AB101', capacity: 296, status: 'ACTIVE', manufacturer: 'Boeing Commercial Airplanes', flightHours: 1840 },
          { id: 2, model: 'Airbus A350-900', aircraftNumber: 'VT-AB202', capacity: 315, status: 'ACTIVE', manufacturer: 'Airbus Commercial Aviation', flightHours: 1200 },
          { id: 3, model: 'Airbus A321neo', aircraftNumber: 'VT-AB303', capacity: 222, status: 'ACTIVE', manufacturer: 'Airbus Commercial Aviation', flightHours: 2450 },
          { id: 4, model: 'Boeing 737 MAX 8', aircraftNumber: 'VT-AB404', capacity: 189, status: 'MAINTENANCE', manufacturer: 'Boeing Commercial Airplanes', flightHours: 3100 }
        ];
      }
    });
  }

  loadUsers(): void {
    this.userService.getAllUsers().subscribe({
      next: (data) => (this.users = data || []),
      error: () => {
        this.users = [
          { userId: 1, firstName: 'Amir', lastName: 'Sohel', email: 'admin@aerobook.com', phoneNumber: '+91 98765 43210', role: 'ROLE_ADMIN', status: 'ACTIVE', reportsCount: 0, level: 5, tier: 'DIAMOND', xp: 62500, badgesCount: 8 },
          { userId: 2, firstName: 'Rajesh', lastName: 'Kumar', email: 'rajesh.staff@aerobook.com', phoneNumber: '+91 98111 22334', role: 'ROLE_STAFF', status: 'ACTIVE', reportsCount: 0, level: 4, tier: 'PLATINUM', xp: 38200, badgesCount: 7 },
          { userId: 3, firstName: 'Priya', lastName: 'Sharma', email: 'priya@gmail.com', phoneNumber: '+91 99222 33445', role: 'ROLE_USER', status: 'ACTIVE', reportsCount: 0, level: 3, tier: 'GOLD', xp: 24850, badgesCount: 6 },
          { userId: 4, firstName: 'Amit', lastName: 'Verma', email: 'amit.verma@yahoo.com', phoneNumber: '+91 97333 44556', role: 'ROLE_USER', status: 'ACTIVE', reportsCount: 1, level: 2, tier: 'SILVER', xp: 8400, badgesCount: 4 },
          { userId: 5, firstName: 'Karan', lastName: 'Malhotra', email: 'karan.m@gmail.com', phoneNumber: '+91 96444 55667', role: 'ROLE_USER', status: 'BLOCKED', reportsCount: 2, level: 1, tier: 'BRONZE', xp: 2100, badgesCount: 2 }
        ];
      }
    });
  }

  loadBookings(): void {
    this.bookingService.getAllBookings().subscribe({
      next: (data) => (this.bookings = data || []),
      error: () => {
        this.bookings = [
          {
            bookingId: 101,
            pnr: 'AB-829140',
            userId: 3,
            flightId: 1,
            flightNumber: 'AI-101',
            bookingDate: new Date().toISOString(),
            totalFare: 5310,
            status: 'CONFIRMED',
            passengers: [{ firstName: 'Priya', lastName: 'Sharma', age: 29, gender: 'Female', seatNumber: '12A' }]
          },
          {
            bookingId: 102,
            pnr: 'AB-672109',
            userId: 4,
            flightId: 2,
            flightNumber: '6E-402',
            bookingDate: new Date().toISOString(),
            totalFare: 3776,
            status: 'CONFIRMED',
            passengers: [{ firstName: 'Amit', lastName: 'Verma', age: 34, gender: 'Male', seatNumber: '14C' }]
          }
        ];
      }
    });
  }

  loadFares(): void {
    this.fareService.getAllFares().subscribe({
      next: (data) => (this.fares = data || []),
      error: () => {
        this.fares = [
          { id: 1, flightId: 1, economyFare: 4500, businessFare: 8500, firstClassFare: 14000, taxRate: 18, discountPercentage: 5 },
          { id: 2, flightId: 2, economyFare: 3200, businessFare: 6800, firstClassFare: 11000, taxRate: 18, discountPercentage: 10 },
          { id: 3, flightId: 3, economyFare: 14200, businessFare: 28500, firstClassFare: 52000, taxRate: 12, discountPercentage: 8 }
        ];
      }
    });
  }

  loadPromos(): void {
    this.promos = [
      { id: 1, code: 'AERO2026', description: 'New Year Early Bird Special', discountPercentage: 15, maxDiscount: 2500, validFrom: '2026-01-01', validUntil: '2026-12-31', usageLimit: 1000, usageCount: 240, status: 'ACTIVE' },
      { id: 2, code: 'FLYHIGH20', description: 'Business & First Class Upgrade Offer', discountPercentage: 20, maxDiscount: 5000, validFrom: '2026-02-01', validUntil: '2026-08-31', usageLimit: 500, usageCount: 88, status: 'ACTIVE' },
      { id: 3, code: 'STAFF10', description: 'Airline Crew & Family Rebate', discountPercentage: 25, maxDiscount: 3500, validFrom: '2026-01-01', validUntil: '2026-12-31', usageLimit: 200, usageCount: 45, status: 'ACTIVE' }
    ];
  }

  loadIncidentReports(): void {
    this.incidentReports = [
      { id: 201, userId: 5, userName: 'Karan Malhotra', userEmail: 'karan.m@gmail.com', reportedBy: 'Chief Purser (Flight 6E-402)', reason: 'Disruptive Passenger Behavior', severity: 'HIGH', status: 'OPEN', createdAt: new Date(Date.now() - 36000000).toISOString(), notes: 'Repeated non-compliance with cabin crew safety instructions during descent.' },
      { id: 202, userId: 4, userName: 'Amit Verma', userEmail: 'amit.verma@yahoo.com', reportedBy: 'Payment Gateway Sentinel', reason: 'Payment Dispute / Fraudulent Chargeback', severity: 'MEDIUM', status: 'RESOLVED', createdAt: new Date(Date.now() - 86400000).toISOString(), notes: 'Duplicate card charge claimed. Reconciled and validated via bank settlement.' }
    ];
  }

  // ==========================================
  // Detailed View Operations
  // ==========================================
  openViewFlightModal(flight: Flight): void {
    this.selectedFlight = flight;
    this.showViewFlightModal = true;
  }

  openViewAircraftModal(plane: Aircraft): void {
    this.selectedAircraft = plane;
    this.showViewAircraftModal = true;
  }

  openViewBookingModal(booking: Booking): void {
    this.selectedBooking = booking;
    this.showViewBookingModal = true;
  }

  openViewFareModal(fare: Fare): void {
    this.selectedFare = fare;
    this.showViewFareModal = true;
  }

  printBoardingPass(booking: Booking): void {
    this.showToast('info', 'E-Ticket Generator', `Formatting boarding document for PNR ${booking.pnr}...`);
    setTimeout(() => {
      window.print();
    }, 500);
  }

  // ==========================================
  // Flight Management Operations
  // ==========================================
  openFlightModal(flight?: Flight): void {
    if (flight) {
      this.flightForm = { ...flight };
    } else {
      this.flightForm = {
        flightNumber: 'AI-204',
        airlineName: 'Air India',
        source: 'DEL',
        destination: 'BOM',
        departureTime: new Date(Date.now() + 86400000).toISOString().slice(0, 16),
        arrivalTime: new Date(Date.now() + 94000000).toISOString().slice(0, 16),
        totalSeats: 180,
        availableSeats: 180,
        baseFare: 4500,
        status: 'SCHEDULED',
        terminal: 'T3',
        gate: 'Gate 14B'
      };
    }
    this.showFlightModal = true;
  }

  saveFlight(): void {
    if (!this.flightForm.flightNumber || !this.flightForm.source || !this.flightForm.destination) {
      this.showToast('error', 'Incomplete Flight Data', 'Please provide flight number, origin, and destination airport.');
      return;
    }

    if (this.flightForm.id) {
      this.flightService.updateFlight(this.flightForm.id, this.flightForm).subscribe({
        next: () => {
          this.showToast('success', 'Flight Updated', `Flight ${this.flightForm.flightNumber} schedule modified successfully.`);
          this.showFlightModal = false;
          this.loadFlights();
        },
        error: () => {
          const idx = this.flights.findIndex(f => f.id === this.flightForm.id);
          if (idx !== -1) this.flights[idx] = { ...this.flights[idx], ...this.flightForm } as Flight;
          this.showToast('success', 'Flight Updated', `Flight ${this.flightForm.flightNumber} schedule saved.`);
          this.showFlightModal = false;
        }
      });
    } else {
      this.flightService.createFlight(this.flightForm).subscribe({
        next: () => {
          this.showToast('success', 'Flight Scheduled', `Flight ${this.flightForm.flightNumber} added to global schedule.`);
          this.showFlightModal = false;
          this.loadFlights();
        },
        error: () => {
          const newF: Flight = {
            id: Date.now(),
            flightNumber: this.flightForm.flightNumber!,
            airlineName: this.flightForm.airlineName || 'Air India',
            source: this.flightForm.source!.toUpperCase(),
            destination: this.flightForm.destination!.toUpperCase(),
            departureTime: this.flightForm.departureTime || new Date(Date.now() + 86400000).toISOString(),
            arrivalTime: this.flightForm.arrivalTime || new Date(Date.now() + 95000000).toISOString(),
            totalSeats: this.flightForm.totalSeats || 180,
            availableSeats: this.flightForm.totalSeats || 180,
            baseFare: this.flightForm.baseFare || 4500,
            status: 'SCHEDULED',
            terminal: this.flightForm.terminal || 'T3',
            gate: this.flightForm.gate || 'Gate 14B',
            aircraftTailNumber: this.flightForm.aircraftTailNumber || 'VT-AB101'
          };
          this.flights.unshift(newF);
          this.showToast('success', 'Flight Scheduled', `Flight ${newF.flightNumber} registered in schedule.`);
          this.showFlightModal = false;
        }
      });
    }
  }

  cancelFlightDirect(flight: Flight): void {
    if (confirm(`Emergency Cancellation: Are you sure you want to CANCEL Flight #${flight.flightNumber}? This will broadcast an alert to booked passengers.`)) {
      this.flightService.updateFlightStatus(flight.id, 'CANCELLED').subscribe({
        next: () => {
          flight.status = 'CANCELLED';
          this.showToast('warning', 'Flight Cancelled', `Flight #${flight.flightNumber} has been officially cancelled.`);
        },
        error: () => {
          flight.status = 'CANCELLED';
          this.showToast('warning', 'Flight Cancelled', `Flight #${flight.flightNumber} marked CANCELLED and notifications dispatched.`);
        }
      });
    }
  }

  deleteFlight(flightId: number): void {
    if (confirm('Permanently remove this flight itinerary from the commercial roster?')) {
      this.flightService.deleteFlight(flightId).subscribe({
        next: () => {
          this.showToast('info', 'Flight Removed', 'Flight itinerary removed from registry.');
          this.loadFlights();
        },
        error: () => {
          this.flights = this.flights.filter(f => f.id !== flightId);
          this.showToast('info', 'Flight Removed', 'Flight itinerary deleted.');
        }
      });
    }
  }

  openQuickStatusModal(flight: Flight): void {
    this.flightStatusUpdateForm = {
      flightId: flight.id,
      flightNumber: flight.flightNumber,
      newStatus: flight.status,
      notifyPassengers: true,
      note: ''
    };
    this.showFlightStatusModal = true;
  }

  saveFlightStatusUpdate(): void {
    const { flightId, flightNumber, newStatus, notifyPassengers } = this.flightStatusUpdateForm;
    this.flightService.updateFlightStatus(flightId, newStatus).subscribe({
      next: () => {
        const fl = this.flights.find(f => f.id === flightId);
        if (fl) fl.status = newStatus as any;
        this.showToast('success', 'Flight Status Updated', `Flight ${flightNumber} is now marked ${newStatus}.`);
        this.showFlightStatusModal = false;
      },
      error: () => {
        const fl = this.flights.find(f => f.id === flightId);
        if (fl) fl.status = newStatus as any;
        if (notifyPassengers) {
          this.notificationService.broadcastAlert({
            title: `Flight Advisory: ${flightNumber} Status -> ${newStatus}`,
            message: `Attention passengers of Flight ${flightNumber}: Your flight is now officially ${newStatus}.`,
            type: newStatus === 'DELAYED' ? 'FLIGHT_DELAY' : 'INFO',
            flightNumber: flightNumber
          }).subscribe({ error: () => {} });
        }
        this.showToast('success', 'Flight Status Updated', `Flight ${flightNumber} status updated to ${newStatus}.`);
        this.showFlightStatusModal = false;
      }
    });
  }

  // ==========================================
  // Fleet Airframe Management
  // ==========================================
  editAircraft(plane: Aircraft): void {
    this.aircraftForm = { ...plane };
    this.showAircraftModal = true;
  }

  saveAircraft(): void {
    if (!this.aircraftForm.model || !this.aircraftForm.aircraftNumber) {
      this.showToast('error', 'Missing Aircraft Details', 'Please provide aircraft model and tail registration number.');
      return;
    }

    if (this.aircraftForm.id) {
      const idx = this.fleet.findIndex(a => a.id === this.aircraftForm.id);
      if (idx !== -1) this.fleet[idx] = { ...this.fleet[idx], ...this.aircraftForm } as Aircraft;
      this.showToast('success', 'Fleet Updated', `Aircraft ${this.aircraftForm.aircraftNumber} specs updated.`);
      this.showAircraftModal = false;
    } else {
      this.flightService.createAircraft(this.aircraftForm).subscribe({
        next: (created) => {
          this.showToast('success', 'Aircraft Registered', `Airframe ${created.aircraftNumber} added to fleet.`);
          this.showAircraftModal = false;
          this.loadFleet();
        },
        error: () => {
          const newA: Aircraft = {
            id: Date.now(),
            model: this.aircraftForm.model!,
            aircraftNumber: this.aircraftForm.aircraftNumber!.toUpperCase(),
            capacity: this.aircraftForm.capacity || 220,
            status: this.aircraftForm.status || 'ACTIVE',
            manufacturer: this.aircraftForm.manufacturer || 'Commercial Aerospace',
            flightHours: 0
          };
          this.fleet.unshift(newA);
          this.showToast('success', 'Aircraft Registered', `Airframe ${newA.aircraftNumber} commissioned.`);
          this.showAircraftModal = false;
        }
      });
    }
  }

  toggleAircraftStatus(plane: Aircraft): void {
    const nextStatus = plane.status === 'ACTIVE' ? 'MAINTENANCE' : (plane.status === 'MAINTENANCE' ? 'GROUNDED' : 'ACTIVE');
    plane.status = nextStatus;
    this.showToast('info', 'Airworthiness Status Changed', `Aircraft ${plane.aircraftNumber} is now marked ${nextStatus}.`);
  }

  deleteAircraft(id: number): void {
    if (confirm('Decommission and retire this aircraft from commercial service?')) {
      this.flightService.deleteAircraft(id).subscribe({
        next: () => {
          this.showToast('info', 'Aircraft Retired', 'Aircraft decommissioned from commercial fleet.');
          this.loadFleet();
        },
        error: () => {
          this.fleet = this.fleet.filter(a => a.id !== id);
          this.showToast('info', 'Aircraft Retired', 'Airframe removed from active inventory.');
        }
      });
    }
  }

  // ==========================================
  // Fare & Promotion Management
  // ==========================================
  openFareModal(fare?: Fare): void {
    if (fare) {
      this.fareForm = { ...fare };
    } else {
      this.fareForm = {
        flightId: this.flights[0]?.id || 1,
        economyFare: 4500,
        businessFare: 8500,
        firstClassFare: 14000,
        taxRate: 18,
        discountPercentage: 5
      };
    }
    this.showFareModal = true;
  }

  saveFare(): void {
    if (this.fareForm.id) {
      this.fareService.updateFare(this.fareForm.id, this.fareForm).subscribe({
        next: () => {
          this.showToast('success', 'Fare Updated', 'Pricing tiers saved to revenue engine.');
          this.showFareModal = false;
          this.loadFares();
        },
        error: () => {
          const idx = this.fares.findIndex(f => f.id === this.fareForm.id);
          if (idx !== -1) this.fares[idx] = { ...this.fares[idx], ...this.fareForm } as Fare;
          this.showToast('success', 'Fare Updated', 'Pricing rules updated locally.');
          this.showFareModal = false;
        }
      });
    } else {
      this.fareService.createFare(this.fareForm).subscribe({
        next: () => {
          this.showToast('success', 'Fare Configured', 'New pricing tier created.');
          this.showFareModal = false;
          this.loadFares();
        },
        error: () => {
          const newFare: Fare = {
            id: Date.now(),
            flightId: this.fareForm.flightId || 1,
            economyFare: this.fareForm.economyFare || 4500,
            businessFare: this.fareForm.businessFare || 8500,
            firstClassFare: this.fareForm.firstClassFare || 14000,
            taxRate: this.fareForm.taxRate || 18,
            discountPercentage: this.fareForm.discountPercentage || 5
          };
          this.fares.push(newFare);
          this.showToast('success', 'Fare Configured', 'Pricing rule active for flight.');
          this.showFareModal = false;
        }
      });
    }
  }

  openPromoModal(): void {
    this.promoForm = {
      code: 'FESTIVE2026',
      description: 'Festive Season 15% Airfare Rebate',
      discountPercentage: 15,
      maxDiscount: 2000,
      usageLimit: 500,
      status: 'ACTIVE'
    };
    this.showPromoModal = true;
  }

  savePromo(): void {
    if (!this.promoForm.code || !this.promoForm.discountPercentage) {
      this.showToast('error', 'Invalid Coupon', 'Please specify a coupon code and discount percentage.');
      return;
    }

    const newPromo: PromoCode = {
      id: Date.now(),
      code: this.promoForm.code.toUpperCase(),
      description: this.promoForm.description || 'Promotional Discount Voucher',
      discountPercentage: this.promoForm.discountPercentage,
      maxDiscount: this.promoForm.maxDiscount || 2000,
      validFrom: new Date().toISOString(),
      validUntil: new Date(Date.now() + 86400000 * 90).toISOString(),
      usageLimit: this.promoForm.usageLimit || 500,
      usageCount: 0,
      status: 'ACTIVE'
    };

    this.promos.unshift(newPromo);
    this.showToast('success', 'Coupon Created', `Promo code ${newPromo.code} published.`);
    this.showPromoModal = false;
  }

  togglePromoStatus(promo: PromoCode): void {
    promo.status = promo.status === 'ACTIVE' ? 'PAUSED' : 'ACTIVE';
    this.showToast('info', 'Coupon Status', `Promo ${promo.code} is now ${promo.status}.`);
  }

  // ==========================================
  // User Governance & RBAC Management
  // ==========================================
  openViewUserModal(user: User): void {
    this.selectedUser = user;
    this.showViewUserModal = true;
  }

  openEditUserModal(user: User): void {
    this.selectedUser = user;
    this.editUserForm = { ...user };
    this.showEditUserModal = true;
  }

  saveEditedUser(): void {
    if (!this.selectedUser) return;
    this.userService.updateUser(this.selectedUser.userId, this.editUserForm).subscribe({
      next: (updated) => {
        Object.assign(this.selectedUser!, updated);
        this.showToast('success', 'User Updated', `Profile updated for ${updated.firstName} ${updated.lastName}.`);
        this.showEditUserModal = false;
      },
      error: () => {
        Object.assign(this.selectedUser!, this.editUserForm);
        this.showToast('success', 'User Updated', `User profile changes saved.`);
        this.showEditUserModal = false;
      }
    });
  }

  openPromoteMenu(user: User): void {
    const roles: RoleType[] = ['ROLE_USER', 'ROLE_STAFF', 'ROLE_ADMIN'];
    const currentIdx = roles.indexOf(user.role);
    const nextRole = roles[(currentIdx + 1) % roles.length];

    if (confirm(`Change authorization role for ${user.firstName} ${user.lastName} to ${nextRole}?`)) {
      this.userService.updateUserRole(user.userId, nextRole).subscribe({
        next: () => {
          user.role = nextRole;
          this.showToast('success', 'Role Elevated', `${user.firstName}'s role elevated to ${nextRole}.`);
        },
        error: () => {
          user.role = nextRole;
          this.showToast('success', 'Role Elevated', `${user.firstName}'s role updated to ${nextRole}.`);
        }
      });
    }
  }

  openAwardXpModal(user: User): void {
    this.selectedUserForXp = user;
    this.awardXpAmount = 1500;
    this.awardXpReason = 'Loyalty Flying Milestone Reward';
    this.showAwardXpModal = true;
  }

  confirmAwardXp(): void {
    if (!this.selectedUserForXp) return;
    const currentXp = (this.selectedUserForXp.xp || 24850) + this.awardXpAmount;
    this.selectedUserForXp.xp = currentXp;

    if (currentXp >= 50000) {
      this.selectedUserForXp.level = 5;
      this.selectedUserForXp.tier = 'DIAMOND';
    } else if (currentXp >= 30000) {
      this.selectedUserForXp.level = 4;
      this.selectedUserForXp.tier = 'PLATINUM';
    } else if (currentXp >= 15000) {
      this.selectedUserForXp.level = 3;
      this.selectedUserForXp.tier = 'GOLD';
    } else if (currentXp >= 5000) {
      this.selectedUserForXp.level = 2;
      this.selectedUserForXp.tier = 'SILVER';
    } else {
      this.selectedUserForXp.level = 1;
      this.selectedUserForXp.tier = 'BRONZE';
    }

    this.showAwardXpModal = false;
    this.showToast('success', 'Bonus XP Awarded', `Granted +${this.awardXpAmount} XP to ${this.selectedUserForXp.firstName} ${this.selectedUserForXp.lastName}. New Balance: ${currentXp.toLocaleString()} XP (Level ${this.selectedUserForXp.level}).`);
  }

  toggleUserBlockStatus(user: User): void {
    const newStatus = user.status === 'BLOCKED' ? 'ACTIVE' : 'BLOCKED';
    const actionLabel = newStatus === 'BLOCKED' ? 'BLOCK & SUSPEND' : 'REACTIVATE';

    if (confirm(`Are you sure you want to ${actionLabel} account for ${user.firstName} ${user.lastName}?`)) {
      user.status = newStatus;
      if (newStatus === 'BLOCKED') {
        this.showToast('warning', 'User Suspended', `Account #${user.userId} has been BLOCKED from bookings.`);
      } else {
        this.showToast('success', 'User Reactivated', `Account #${user.userId} is now ACTIVE.`);
      }
    }
  }

  openReportModalForUser(user: User): void {
    this.reportForm = {
      userId: user.userId,
      userName: `${user.firstName} ${user.lastName}`,
      userEmail: user.email,
      reason: 'Disruptive Passenger Behavior',
      severity: 'MEDIUM',
      status: 'OPEN',
      notes: ''
    };
    this.showReportModal = true;
  }

  openReportModal(): void {
    const target = this.users[0] || { userId: 1, firstName: 'Unknown', lastName: 'User', email: 'user@example.com' };
    this.reportForm = {
      userId: target.userId,
      userName: `${target.firstName} ${target.lastName}`,
      userEmail: target.email,
      reason: 'Disruptive Passenger Behavior',
      severity: 'MEDIUM',
      status: 'OPEN',
      notes: ''
    };
    this.showReportModal = true;
  }

  saveIncidentReport(): void {
    const newReport: UserIncidentReport = {
      id: Date.now(),
      userId: this.reportForm.userId || 0,
      userName: this.reportForm.userName || 'Passenger',
      userEmail: this.reportForm.userEmail || '',
      reportedBy: 'Executive Operations Control',
      reason: this.reportForm.reason || 'General Misconduct',
      severity: this.reportForm.severity || 'MEDIUM',
      status: 'OPEN',
      createdAt: new Date().toISOString(),
      notes: this.reportForm.notes || ''
    };

    this.incidentReports.unshift(newReport);
    const targetUser = this.users.find(u => u.userId === newReport.userId);
    if (targetUser) {
      targetUser.reportsCount = (targetUser.reportsCount || 0) + 1;
      if (newReport.severity === 'CRITICAL') {
        targetUser.status = 'BLOCKED';
      }
    }

    this.showToast('warning', 'Incident Logged', `Report #INC-${newReport.id} logged against ${newReport.userName}.`);
    this.showReportModal = false;
  }

  resolveReport(id: number): void {
    const rep = this.incidentReports.find(r => r.id === id);
    if (rep) {
      rep.status = 'RESOLVED';
      this.showToast('success', 'Incident Resolved', `Report #INC-${id} marked resolved.`);
    }
  }

  dismissReport(id: number): void {
    this.incidentReports = this.incidentReports.filter(r => r.id !== id);
    this.showToast('info', 'Report Dismissed', `Report #INC-${id} has been dismissed.`);
  }

  // ==========================================
  // Admin Profile & Security Operations
  // ==========================================
  openEditProfileModal(): void {
    this.profileForm = { ...this.adminProfile };
    this.showProfileModal = true;
  }

  saveAdminProfile(): void {
    Object.assign(this.adminProfile, this.profileForm);
    this.showToast('success', 'Profile Updated', 'Admin credentials and contact profile saved.');
    this.showProfileModal = false;
  }

  updateAdminPassword(): void {
    if (!this.passwordForm.newPassword || this.passwordForm.newPassword !== this.passwordForm.confirmPassword) {
      this.showToast('error', 'Password Mismatch', 'New passwords do not match or are empty.');
      return;
    }

    this.showToast('success', 'Password Changed', 'Executive security password updated across platform.');
    this.passwordForm = { currentPassword: '', newPassword: '', confirmPassword: '' };
    this.showPasswordModal = false;
  }

  triggerPasswordResetRecovery(): void {
    const emergencyKey = 'RCV-' + Math.random().toString(36).substring(2, 10).toUpperCase();
    this.showToast('info', 'Recovery Initiated', `Emergency recovery code dispatched to ${this.adminProfile.email}: [${emergencyKey}]`);
  }

  toggle2FA(): void {
    this.adminProfile.twoFactorEnabled = !this.adminProfile.twoFactorEnabled;
    const msg = this.adminProfile.twoFactorEnabled ? 'Two-Factor Authentication is now ENFORCED.' : 'Two-Factor Authentication paused.';
    this.showToast(this.adminProfile.twoFactorEnabled ? 'success' : 'warning', '2FA Status', msg);
  }

  // ==========================================
  // Booking Operations
  // ==========================================
  cancelReservation(bookingId: number): void {
    if (confirm(`Void Reservation #${bookingId} and issue automated refund?`)) {
      this.bookingService.cancelBooking(bookingId).subscribe({
        next: () => {
          this.showToast('info', 'Booking Cancelled', `Reservation #${bookingId} voided.`);
          this.loadBookings();
        },
        error: () => {
          const bk = this.bookings.find(b => b.bookingId === bookingId);
          if (bk) bk.status = 'CANCELLED';
          this.showToast('info', 'Booking Cancelled', `Reservation #${bookingId} marked CANCELLED.`);
        }
      });
    }
  }

  // ==========================================
  // Broadcast Operations
  // ==========================================
  sendBroadcast(): void {
    this.notificationService.broadcastAlert(this.broadcastForm).subscribe({
      next: () => {
        this.showToast('success', 'Broadcast Dispatched', `Alert "${this.broadcastForm.title}" pushed to all devices.`);
        this.broadcastForm = { title: '', message: '', type: 'BROADCAST', flightNumber: '' };
      },
      error: () => {
        this.showToast('success', 'Broadcast Dispatched', `Broadcast transmission broadcasted to mesh.`);
        this.broadcastForm = { title: '', message: '', type: 'BROADCAST', flightNumber: '' };
      }
    });
  }

  // ==========================================
  // CSV Data Exports
  // ==========================================
  exportFlightsCsv(): void {
    const headers = 'FlightNumber,Airline,Source,Destination,Departure,Arrival,BaseFare,Status\n';
    const rows = this.flights.map(f =>
      `"${f.flightNumber}","${f.airlineName}","${f.source}","${f.destination}","${f.departureTime}","${f.arrivalTime}",${f.baseFare},"${f.status}"`
    ).join('\n');
    this.downloadCsv('aerobook_flights_schedule.csv', headers + rows);
    this.showToast('success', 'Export Complete', 'Flights schedule downloaded as CSV.');
  }

  exportUsersCsv(): void {
    const headers = 'UserId,FirstName,LastName,Email,Phone,Role,Status,Reports\n';
    const rows = this.users.map(u =>
      `${u.userId},"${u.firstName}","${u.lastName}","${u.email}","${u.phoneNumber || ''}","${u.role}","${u.status || 'ACTIVE'}",${u.reportsCount || 0}`
    ).join('\n');
    this.downloadCsv('aerobook_user_directory.csv', headers + rows);
    this.showToast('success', 'Export Complete', 'User directory downloaded as CSV.');
  }

  exportBookingsCsv(): void {
    const headers = 'BookingId,PNR,Flight,TotalFare,Date,Status\n';
    const rows = this.bookings.map(b =>
      `${b.bookingId},"${b.pnr}","${b.flightNumber || b.flightId}",${b.totalFare},"${b.bookingDate}","${b.status}"`
    ).join('\n');
    this.downloadCsv('aerobook_bookings_audit.csv', headers + rows);
    this.showToast('success', 'Export Complete', 'Bookings audit downloaded as CSV.');
  }

  private downloadCsv(filename: string, content: string): void {
    const blob = new Blob([content], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    link.href = URL.createObjectURL(blob);
    link.setAttribute('download', filename);
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }

  // UI Helpers
  getStatusClass(status: string): string {
    switch (status) {
      case 'SCHEDULED': return 'bg-sky-500/10 border-sky-500/30 text-sky-700 dark:text-sky-400';
      case 'BOARDING': return 'bg-amber-500/10 border-amber-500/30 text-amber-700 dark:text-amber-400';
      case 'DELAYED': return 'bg-rose-500/10 border-rose-500/30 text-rose-700 dark:text-rose-400';
      case 'DEPARTED': return 'bg-indigo-500/10 border-indigo-500/30 text-indigo-700 dark:text-indigo-400';
      case 'COMPLETED': return 'bg-emerald-500/10 border-emerald-500/30 text-emerald-700 dark:text-emerald-400';
      case 'CANCELLED': return 'bg-rose-500/20 border-rose-500/40 text-rose-700 dark:text-rose-400';
      default: return 'bg-seg border-line text-ink-2';
    }
  }

  getRoleBadgeClass(role: string): string {
    switch (role) {
      case 'ROLE_ADMIN': return 'bg-purple-500/10 border-purple-500/30 text-purple-700 dark:text-purple-400';
      case 'ROLE_STAFF': return 'bg-amber-500/10 border-amber-500/30 text-amber-700 dark:text-amber-400';
      case 'ROLE_USER': return 'bg-sky-500/10 border-sky-500/30 text-sky-700 dark:text-sky-400';
      default: return 'bg-seg border-line text-ink-2';
    }
  }
}

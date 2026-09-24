import { Component, OnInit, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { FlightService } from '../../core/services/flight.service';
import { AuthService } from '../../core/services/auth.service';
import { Flight } from '../../core/models/aerobook.models';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="relative overflow-hidden">
      
      <!-- Ambient Glow Accents -->
      <div class="absolute -top-40 -left-40 w-96 h-96 bg-brand-500/15 rounded-full blur-3xl pointer-events-none"></div>
      <div class="absolute top-60 -right-40 w-96 h-96 bg-cyan-500/10 rounded-full blur-3xl pointer-events-none"></div>

      <!-- Hero Section -->
      <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-12 pb-16">
        <div class="text-center max-w-3xl mx-auto">
          <div class="inline-flex items-center gap-2 px-3 py-1.5 rounded-full glass-card text-xs font-semibold text-brand-300 border border-brand-500/30 mb-6">
            <span class="flex h-2 w-2 relative">
              <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
              <span class="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
            </span>
            Real-Time Cloud Flight Operations & Booking
          </div>
          
          <h1 class="text-4xl sm:text-6xl font-extrabold text-white tracking-tight leading-tight">
            Seamless Skies, <br/>
            <span class="text-transparent bg-clip-text bg-gradient-to-r from-brand-400 via-cyan-300 to-indigo-400">
              Intelligent Flight Management
            </span>
          </h1>
          
          <p class="mt-4 text-base sm:text-lg text-slate-300 font-light">
            Search, book, check-in, and manage commercial airline operations powered by Spring Cloud microservices & Angular.
          </p>
        </div>

        <!-- Search Bar Widget -->
        <div class="mt-10 glass-panel rounded-3xl p-6 sm:p-8 shadow-2xl border border-slate-700/60 max-w-5xl mx-auto glow-blue">
          
          <div class="flex items-center gap-3 mb-6 pb-4 border-b border-slate-800 text-xs font-semibold">
            <span class="px-3 py-1 rounded-lg bg-brand-500 text-white">✈️ One Way</span>
            <span class="px-3 py-1 rounded-lg text-slate-400">Direct Flights Only</span>
            <span class="ml-auto text-slate-400 font-mono text-[11px]">Microservice Search Engine</span>
          </div>

          <form (ngSubmit)="handleSearch()" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            
            <!-- Departure Origin -->
            <div>
              <label class="block text-xs font-medium text-slate-400 mb-1.5 flex items-center gap-1">
                <span>🛫</span> Departure City
              </label>
              <div class="relative">
                <input type="text" [(ngModel)]="source" name="source" required placeholder="e.g. Mumbai"
                       class="w-full px-4 py-3 rounded-xl bg-slate-900/90 border border-slate-700 text-white text-sm font-medium focus:outline-none focus:border-brand-500 placeholder:text-slate-500">
              </div>
            </div>

            <!-- Arrival Destination -->
            <div>
              <label class="block text-xs font-medium text-slate-400 mb-1.5 flex items-center gap-1">
                <span>🛬</span> Arrival Destination
              </label>
              <div class="relative">
                <input type="text" [(ngModel)]="destination" name="destination" required placeholder="e.g. Delhi"
                       class="w-full px-4 py-3 rounded-xl bg-slate-900/90 border border-slate-700 text-white text-sm font-medium focus:outline-none focus:border-brand-500 placeholder:text-slate-500">
              </div>
            </div>

            <!-- Travel Date -->
            <div>
              <label class="block text-xs font-medium text-slate-400 mb-1.5 flex items-center gap-1">
                <span>📅</span> Travel Date
              </label>
              <input type="date" [(ngModel)]="travelDate" name="travelDate"
                     class="w-full px-4 py-3 rounded-xl bg-slate-900/90 border border-slate-700 text-white text-sm font-medium focus:outline-none focus:border-brand-500">
            </div>

            <!-- Search CTA -->
            <div class="flex items-end">
              <button type="submit" [disabled]="searching"
                      class="w-full py-3.5 rounded-xl bg-gradient-to-r from-brand-500 via-cyan-500 to-indigo-500 hover:from-brand-600 hover:to-cyan-600 text-white text-sm font-bold shadow-lg shadow-brand-500/25 transition-all flex items-center justify-center gap-2">
                @if (searching) {
                  <span class="animate-spin">🔄</span>
                  <span>Scanning Routes...</span>
                } @else {
                  <span>🔍 Search Flights</span>
                }
              </button>
            </div>

          </form>

          <!-- Quick Route Chips -->
          <div class="mt-5 pt-4 border-t border-slate-800/80 flex flex-wrap items-center gap-2 text-xs">
            <span class="text-slate-400">Popular Routes:</span>
            <button (click)="selectRoute('Mumbai', 'Delhi')" class="px-2.5 py-1 rounded-lg bg-slate-800/80 hover:bg-slate-700 text-slate-300 transition-colors">
              Mumbai → Delhi
            </button>
            <button (click)="selectRoute('Delhi', 'Bengaluru')" class="px-2.5 py-1 rounded-lg bg-slate-800/80 hover:bg-slate-700 text-slate-300 transition-colors">
              Delhi → Bengaluru
            </button>
            <button (click)="selectRoute('Mumbai', 'Goa')" class="px-2.5 py-1 rounded-lg bg-slate-800/80 hover:bg-slate-700 text-slate-300 transition-colors">
              Mumbai → Goa
            </button>
            <button (click)="loadAllFlights()" class="px-2.5 py-1 rounded-lg bg-brand-500/20 hover:bg-brand-500/30 text-brand-300 border border-brand-500/30 transition-colors ml-auto">
              Show All Flights Catalog
            </button>
          </div>

        </div>
      </section>

      <!-- Flight Results Section -->
      <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pb-20">
        
        <div class="flex items-center justify-between mb-6">
          <div>
            <h2 class="text-xl font-bold text-white flex items-center gap-2">
              <span>✈️ Available Commercial Flights</span>
              <span class="text-xs px-2.5 py-0.5 rounded-full bg-slate-800 text-slate-300 border border-slate-700">
                {{ flights.length }} Scheduled
              </span>
            </h2>
            <p class="text-xs text-slate-400 mt-0.5">Real-time seat inventory and dynamic fares verified by Flight & Fare Services</p>
          </div>
        </div>

        @if (flights.length === 0 && !searching) {
          <div class="glass-panel rounded-2xl p-12 text-center max-w-md mx-auto">
            <div class="text-4xl mb-3">🛫</div>
            <h3 class="text-base font-semibold text-white">No Flights Found</h3>
            <p class="text-xs text-slate-400 mt-1 mb-4">Try searching for other departure/arrival destinations or view all flights.</p>
            <button (click)="loadAllFlights()" class="px-4 py-2 rounded-xl bg-brand-500 hover:bg-brand-600 text-white text-xs font-semibold">
              Browse All Flights
            </button>
          </div>
        }

        <!-- Flights Cards Grid -->
        <div class="grid grid-cols-1 gap-4">
          @for (flight of flights; track flight.id) {
            <div class="glass-card rounded-2xl p-5 hover:border-brand-500/40 transition-all hover:shadow-xl hover:shadow-brand-500/5 group">
              <div class="flex flex-col lg:flex-row lg:items-center justify-between gap-6">
                
                <!-- Airline & Flight Info -->
                <div class="flex items-center gap-4 min-w-[200px]">
                  <div class="w-12 h-12 rounded-xl bg-gradient-to-tr from-slate-800 to-slate-700 border border-slate-600 flex items-center justify-center font-bold text-brand-400 text-sm shadow-md">
                    {{ flight.flightNumber.substring(0, 2) }}
                  </div>
                  <div>
                    <div class="flex items-center gap-2">
                      <span class="text-base font-bold text-white">{{ flight.airlineName }}</span>
                      <span class="text-xs font-mono px-2 py-0.5 rounded bg-brand-500/10 text-brand-300 border border-brand-500/30">
                        {{ flight.flightNumber }}
                      </span>
                    </div>
                    <div class="text-xs text-slate-400 mt-0.5 flex items-center gap-2">
                      <span [ngClass]="{
                        'text-emerald-400': flight.status === 'SCHEDULED' || flight.status === 'COMPLETED',
                        'text-amber-400': flight.status === 'BOARDING' || flight.status === 'DELAYED',
                        'text-red-400': flight.status === 'CANCELLED'
                      }" class="font-semibold flex items-center gap-1">
                        ● {{ flight.status }}
                      </span>
                      <span>•</span>
                      <span>{{ flight.availableSeats }} seats remaining</span>
                    </div>
                  </div>
                </div>

                <!-- Flight Route & Timeline -->
                <div class="flex-1 flex items-center justify-between sm:justify-center sm:space-x-12 px-2">
                  
                  <div class="text-left sm:text-right">
                    <div class="text-lg font-bold text-white">{{ formatTime(flight.departureTime) }}</div>
                    <div class="text-xs font-semibold text-brand-300">{{ flight.source }}</div>
                    <div class="text-[11px] text-slate-400">{{ formatDate(flight.departureTime) }}</div>
                  </div>

                  <!-- Flight path graphic -->
                  <div class="flex flex-col items-center px-4">
                    <span class="text-[10px] text-slate-400 font-mono mb-1">Non-Stop</span>
                    <div class="w-24 sm:w-36 flex items-center">
                      <div class="w-2 h-2 rounded-full bg-brand-400"></div>
                      <div class="h-0.5 flex-1 bg-gradient-to-r from-brand-500 to-cyan-400"></div>
                      <span class="text-xs text-cyan-300 -mx-1">✈️</span>
                      <div class="h-0.5 flex-1 bg-gradient-to-r from-cyan-400 to-indigo-500"></div>
                      <div class="w-2 h-2 rounded-full bg-indigo-400"></div>
                    </div>
                    <span class="text-[10px] text-slate-500 mt-1">Commercial Airway</span>
                  </div>

                  <div class="text-right sm:text-left">
                    <div class="text-lg font-bold text-white">{{ formatTime(flight.arrivalTime) }}</div>
                    <div class="text-xs font-semibold text-cyan-300">{{ flight.destination }}</div>
                    <div class="text-[11px] text-slate-400">{{ formatDate(flight.arrivalTime) }}</div>
                  </div>

                </div>

                <!-- Fare & Booking CTA -->
                <div class="flex items-center justify-between lg:justify-end gap-6 pt-4 lg:pt-0 border-t lg:border-t-0 border-slate-800">
                  <div class="text-left lg:text-right">
                    <span class="text-[10px] uppercase tracking-wider text-slate-400 block">Base Fare</span>
                    <span class="text-2xl font-extrabold text-white">₹{{ flight.baseFare | number }}</span>
                    <span class="text-[10px] text-emerald-400 block font-medium">Taxes Included</span>
                  </div>

                  <button (click)="initiateBooking(flight)"
                          [disabled]="flight.availableSeats <= 0 || flight.status === 'CANCELLED'"
                          class="px-6 py-2.5 rounded-xl bg-gradient-to-r from-brand-500 to-cyan-500 hover:from-brand-600 hover:to-cyan-600 text-white font-semibold text-xs shadow-md shadow-brand-500/20 transition-all hover:scale-105 disabled:opacity-40 disabled:hover:scale-100">
                    Book Flight
                  </button>
                </div>

              </div>
            </div>
          }
        </div>

      </section>

      <!-- Platform Architectural Featurettes -->
      <section class="border-t border-slate-800 bg-slate-900/40 py-16">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="text-center mb-12">
            <h2 class="text-2xl font-bold text-white">Engineered for High-Availability Airline Ops</h2>
            <p class="text-xs text-slate-400 mt-1">Multi-tier microservices connected via Spring Cloud Gateway, Eureka & RabbitMQ</p>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
            
            <div class="glass-card rounded-2xl p-6 border-slate-800">
              <div class="w-10 h-10 rounded-xl bg-blue-500/10 text-blue-400 flex items-center justify-center text-xl mb-4">
                🛡️
              </div>
              <h4 class="text-sm font-bold text-white">Role-Based Access</h4>
              <p class="text-xs text-slate-400 mt-1 leading-relaxed">
                Distinct operational privileges for Customers, Airport Staff, and Airline Admins with cryptographic JWT tokens.
              </p>
            </div>

            <div class="glass-card rounded-2xl p-6 border-slate-800">
              <div class="w-10 h-10 rounded-xl bg-cyan-500/10 text-cyan-400 flex items-center justify-center text-xl mb-4">
                📱
              </div>
              <h4 class="text-sm font-bold text-white">Instant Web Check-In</h4>
              <p class="text-xs text-slate-400 mt-1 leading-relaxed">
                Self-service passenger check-in, visual seat selection, and digital boarding pass generation with scannable QR.
              </p>
            </div>

            <div class="glass-card rounded-2xl p-6 border-slate-800">
              <div class="w-10 h-10 rounded-xl bg-amber-500/10 text-amber-400 flex items-center justify-center text-xl mb-4">
                ✈️
              </div>
              <h4 class="text-sm font-bold text-white">Airport Operations Hub</h4>
              <p class="text-xs text-slate-400 mt-1 leading-relaxed">
                Ground staff boarding gate verifier, flight status controller, and passenger manifest viewer for operations.
              </p>
            </div>

            <div class="glass-card rounded-2xl p-6 border-slate-800">
              <div class="w-10 h-10 rounded-xl bg-purple-500/10 text-purple-400 flex items-center justify-center text-xl mb-4">
                🔔
              </div>
              <h4 class="text-sm font-bold text-white">Real-Time Alerts</h4>
              <p class="text-xs text-slate-400 mt-1 leading-relaxed">
                Dedicated Notification Service (Port 8091) providing instant gate changes, operational delay notices, and booking updates.
              </p>
            </div>

          </div>
        </div>
      </section>

    </div>
  `
})
export class HomeComponent implements OnInit {
  @Output() openLogin = new EventEmitter<void>();

  source: string = 'Mumbai';
  destination: string = 'Delhi';
  travelDate: string = '2026-10-01';
  searching: boolean = false;
  flights: Flight[] = [];

  constructor(
    private flightService: FlightService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadAllFlights();
  }

  handleSearch(): void {
    this.searching = true;
    this.flightService.searchFlights(this.source.trim(), this.destination.trim()).subscribe({
      next: (data) => {
        this.flights = data;
        this.searching = false;
      },
      error: () => {
        // Fallback to all flights if route has no specific matches
        this.loadAllFlights();
        this.searching = false;
      }
    });
  }

  loadAllFlights(): void {
    this.searching = true;
    this.flightService.getAllFlights().subscribe({
      next: (data) => {
        this.flights = data;
        this.searching = false;
      },
      error: () => {
        this.searching = false;
      }
    });
  }

  selectRoute(src: string, dest: string): void {
    this.source = src;
    this.destination = dest;
    this.handleSearch();
  }

  initiateBooking(flight: Flight): void {
    if (!this.authService.isAuthenticated()) {
      this.openLogin.emit();
      return;
    }

    if (this.authService.isCustomer()) {
      this.router.navigate(['/customer'], {
        queryParams: { selectedFlightId: flight.id }
      });
    } else if (this.authService.isStaff()) {
      this.router.navigate(['/staff']);
    } else {
      this.router.navigate(['/admin']);
    }
  }

  formatTime(isoStr: string): string {
    if (!isoStr) return '--:--';
    try {
      const d = new Date(isoStr);
      return d.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
    } catch {
      return isoStr;
    }
  }

  formatDate(isoStr: string): string {
    if (!isoStr) return '';
    try {
      const d = new Date(isoStr);
      return d.toLocaleDateString([], { month: 'short', day: 'numeric', year: 'numeric' });
    } catch {
      return isoStr;
    }
  }
}


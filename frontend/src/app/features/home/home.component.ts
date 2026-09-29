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
    <div class="relative overflow-hidden text-ink">
      
      <!-- Ambient Glow Accents -->
      <div class="absolute -top-40 -left-40 w-96 h-96 bg-accent/15 rounded-full blur-3xl pointer-events-none"></div>
      <div class="absolute top-60 -right-40 w-96 h-96 bg-purple-500/15 rounded-full blur-3xl pointer-events-none"></div>

      <!-- Hero Section -->
      <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-6 sm:pt-10 pb-16">
        <div class="text-center max-w-3xl mx-auto">
          <div class="inline-flex items-center gap-2 px-3.5 py-1.5 rounded-full bg-seg border border-line text-xs font-semibold text-accent mb-6 shadow-sm">
            <span class="flex h-2 w-2 relative">
              <span class="animate-ping absolute inline-flex h-full w-full rounded-full bg-accent opacity-75"></span>
              <span class="relative inline-flex rounded-full h-2 w-2 bg-accent"></span>
            </span>
            Real-Time Cloud Flight Operations & Booking
          </div>
          
          <h1 class="text-4xl sm:text-6xl font-extrabold text-ink tracking-tight leading-tight">
            Seamless Skies, <br/>
            <span class="text-transparent bg-clip-text bg-gradient-to-r from-accent via-purple-600 to-indigo-600 dark:from-accent-2 dark:via-purple-400 dark:to-indigo-300">
              Intelligent Flight Management
            </span>
          </h1>
          
          <p class="mt-4 text-base sm:text-lg text-ink-2 font-normal">
            Search, book, check-in, and manage commercial airline operations powered by Spring Cloud microservices & Angular.
          </p>
        </div>

        <!-- Search Bar Widget -->
        <div class="mt-10 bg-surface border border-line rounded-card p-6 sm:p-8 shadow-card backdrop-blur-xl max-w-5xl mx-auto">
          
          <div class="flex items-center gap-3 mb-6 pb-4 border-b border-line text-xs font-semibold">
            <span class="px-3.5 py-1.5 rounded-full bg-accent text-white font-medium shadow-sm">✈️ One Way</span>
            <span class="px-3.5 py-1.5 rounded-full text-ink-2 font-medium">Direct Flights Only</span>
            <span class="ml-auto text-ink-3 font-mono text-[11px]">Microservice Search Engine</span>
          </div>

          <form (ngSubmit)="handleSearch()" class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
            
            <!-- Departure Origin -->
            <div>
              <label class="block text-xs font-semibold text-ink mb-1.5 flex items-center gap-1">
                <span>🛫</span> Departure City
              </label>
              <div class="relative">
                <input type="text" [(ngModel)]="source" name="source" required placeholder="e.g. Mumbai"
                       class="w-full px-4 py-3 rounded-xl bg-surface border border-line text-ink text-sm font-semibold focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 placeholder:text-ink-3 shadow-sm transition-all">
              </div>
            </div>

            <!-- Arrival Destination -->
            <div>
              <label class="block text-xs font-semibold text-ink mb-1.5 flex items-center gap-1">
                <span>🛬</span> Arrival Destination
              </label>
              <div class="relative">
                <input type="text" [(ngModel)]="destination" name="destination" required placeholder="e.g. Delhi"
                       class="w-full px-4 py-3 rounded-xl bg-surface border border-line text-ink text-sm font-semibold focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 placeholder:text-ink-3 shadow-sm transition-all">
              </div>
            </div>

            <!-- Travel Date -->
            <div>
              <label class="block text-xs font-semibold text-ink mb-1.5 flex items-center gap-1">
                <span>📅</span> Travel Date
              </label>
              <input type="date" [(ngModel)]="travelDate" name="travelDate"
                     class="w-full px-4 py-3 rounded-xl bg-surface border border-line text-ink text-sm font-semibold focus:outline-none focus:border-accent focus:ring-2 focus:ring-accent/20 placeholder:text-ink-3 shadow-sm transition-all">
            </div>

            <!-- Search CTA -->
            <div class="flex items-end">
              <button type="submit" [disabled]="searching"
                      class="w-full py-3.5 rounded-xl bg-accent hover:opacity-95 text-white text-sm font-bold shadow-lg shadow-accent/25 transition-all flex items-center justify-center gap-2 cursor-pointer">
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
          <div class="mt-5 pt-4 border-t border-line flex flex-wrap items-center gap-2 text-xs">
            <span class="text-ink-3">Popular Routes:</span>
            <button (click)="selectRoute('Mumbai', 'Delhi')" class="px-3 py-1.5 rounded-full bg-seg hover:bg-surface text-ink-2 hover:text-ink transition-colors font-medium border border-line cursor-pointer">
              Mumbai → Delhi
            </button>
            <button (click)="selectRoute('Delhi', 'Bengaluru')" class="px-3 py-1.5 rounded-full bg-seg hover:bg-surface text-ink-2 hover:text-ink transition-colors font-medium border border-line cursor-pointer">
              Delhi → Bengaluru
            </button>
            <button (click)="selectRoute('Mumbai', 'Goa')" class="px-3 py-1.5 rounded-full bg-seg hover:bg-surface text-ink-2 hover:text-ink transition-colors font-medium border border-line cursor-pointer">
              Mumbai → Goa
            </button>
            <button (click)="loadAllFlights()" class="px-3.5 py-1.5 rounded-full bg-accent-soft hover:bg-accent/20 text-accent transition-colors font-semibold ml-auto border border-accent/20 cursor-pointer">
              Show All Flights Catalog
            </button>
          </div>

        </div>
      </section>

      <!-- Flight Results Section -->
      <section class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pb-20">
        
        <div class="flex items-center justify-between mb-6">
          <div>
            <h2 class="text-xl font-bold text-ink flex items-center gap-2">
              <span>✈️ Available Commercial Flights</span>
              <span class="text-xs px-2.5 py-0.5 rounded-full bg-seg text-ink-2 border border-line">
                {{ flights.length }} Scheduled
              </span>
            </h2>
            <p class="text-xs text-ink-3 mt-0.5">Real-time seat inventory and dynamic fares verified by Flight & Fare Services</p>
          </div>
        </div>

        @if (flights.length === 0 && !searching) {
          <div class="rounded-card bg-surface border border-line p-12 text-center max-w-md mx-auto shadow-card">
            <div class="text-4xl mb-3">🛫</div>
            <h3 class="text-base font-semibold text-ink">No Flights Found</h3>
            <p class="text-xs text-ink-2 mt-1 mb-4">Try searching for other departure/arrival destinations or view all flights.</p>
            <button (click)="loadAllFlights()" class="px-4 py-2 rounded-xl bg-accent hover:opacity-95 text-white text-xs font-semibold cursor-pointer">
              Browse All Flights
            </button>
          </div>
        }

        <!-- Flights Cards Grid -->
        <div class="grid grid-cols-1 gap-4">
          @for (flight of flights; track flight.id) {
            <div class="rounded-card bg-surface border border-line p-5 hover:border-accent/40 transition-all shadow-card hover:shadow-lg group text-ink">
              <div class="flex flex-col lg:flex-row lg:items-center justify-between gap-6">
                
                <!-- Airline & Flight Info -->
                <div class="flex items-center gap-4 min-w-[200px]">
                  <div class="w-12 h-12 rounded-2xl bg-indigo-600/10 text-indigo-700 dark:text-indigo-400 border border-indigo-500/20 flex items-center justify-center font-black text-sm shadow-sm">
                    {{ flight.flightNumber.substring(0, 2) }}
                  </div>
                  <div>
                    <div class="flex items-center gap-2">
                      <span class="text-base font-bold text-ink">{{ flight.airlineName }}</span>
                      <span class="text-xs font-mono px-2 py-0.5 rounded-md bg-seg text-ink-2 border border-line">
                        {{ flight.flightNumber }}
                      </span>
                    </div>
                    <div class="text-xs text-ink-3 mt-0.5 flex items-center gap-2">
                      <span [ngClass]="{
                        'text-emerald-700 dark:text-emerald-400': flight.status === 'SCHEDULED' || flight.status === 'COMPLETED',
                        'text-amber-700 dark:text-amber-400': flight.status === 'BOARDING' || flight.status === 'DELAYED',
                        'text-rose-700 dark:text-rose-400': flight.status === 'CANCELLED'
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
                    <div class="text-xl font-black text-ink font-mono">{{ formatTime(flight.departureTime) }}</div>
                    <div class="text-xs font-bold text-accent">{{ flight.source }}</div>
                    <div class="text-[11px] text-ink-3">{{ formatDate(flight.departureTime) }}</div>
                  </div>

                  <!-- Flight path graphic -->
                  <div class="flex flex-col items-center px-4">
                    <span class="text-[10px] text-ink-3 font-mono mb-1">Non-Stop</span>
                    <div class="w-24 sm:w-36 flex items-center">
                      <div class="w-2 h-2 rounded-full bg-accent"></div>
                      <div class="h-0.5 flex-1 bg-line"></div>
                      <span class="text-xs text-accent -mx-1">✈️</span>
                      <div class="h-0.5 flex-1 bg-line"></div>
                      <div class="w-2 h-2 rounded-full bg-accent"></div>
                    </div>
                    <span class="text-[10px] text-ink-3 mt-1">Commercial Airway</span>
                  </div>

                  <div class="text-right sm:text-left">
                    <div class="text-xl font-black text-ink font-mono">{{ formatTime(flight.arrivalTime) }}</div>
                    <div class="text-xs font-bold text-accent">{{ flight.destination }}</div>
                    <div class="text-[11px] text-ink-3">{{ formatDate(flight.arrivalTime) }}</div>
                  </div>

                </div>

                <!-- Fare & Booking CTA -->
                <div class="flex items-center justify-between lg:justify-end gap-6 pt-4 lg:pt-0 border-t lg:border-t-0 border-line">
                  <div class="text-left lg:text-right">
                    <span class="text-[10px] uppercase tracking-wider text-ink-3 block">Base Fare</span>
                    <span class="text-2xl font-black text-ink font-mono">₹{{ flight.baseFare | number }}</span>
                    <span class="text-[10px] text-emerald-700 dark:text-emerald-400 block font-semibold">Taxes Included</span>
                  </div>

                  <button (click)="initiateBooking(flight)"
                          [disabled]="flight.availableSeats <= 0 || flight.status === 'CANCELLED'"
                          class="px-6 py-2.5 rounded-xl bg-accent hover:opacity-95 text-white font-bold text-xs shadow-md transition-all hover:scale-105 disabled:opacity-40 disabled:hover:scale-100 cursor-pointer">
                    Book Flight
                  </button>
                </div>

              </div>
            </div>
          }
        </div>

      </section>

      <!-- Platform Architectural Featurettes -->
      <section class="border-t border-line bg-surface/50 py-16 text-ink">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="text-center mb-12">
            <h2 class="text-2xl font-bold text-ink">Engineered for High-Availability Airline Ops</h2>
            <p class="text-xs text-ink-2 mt-1">Multi-tier microservices connected via Spring Cloud Gateway, Eureka & RabbitMQ</p>
          </div>

          <div class="grid grid-cols-1 md:grid-cols-4 gap-6">
            
            <div class="rounded-card bg-surface border border-line shadow-card p-6">
              <div class="w-10 h-10 rounded-2xl bg-seg text-accent border border-line flex items-center justify-center text-xl mb-4">
                🛡️
              </div>
              <h4 class="text-sm font-bold text-ink">Role-Based Access</h4>
              <p class="text-xs text-ink-2 mt-1 leading-relaxed">
                Distinct operational privileges for Customers, Airport Staff, and Airline Admins with cryptographic JWT tokens.
              </p>
            </div>

            <div class="rounded-card bg-surface border border-line shadow-card p-6">
              <div class="w-10 h-10 rounded-2xl bg-seg text-accent border border-line flex items-center justify-center text-xl mb-4">
                📱
              </div>
              <h4 class="text-sm font-bold text-ink">Instant Web Check-In</h4>
              <p class="text-xs text-ink-2 mt-1 leading-relaxed">
                Self-service passenger check-in, visual seat selection, and digital boarding pass generation with scannable QR.
              </p>
            </div>

            <div class="rounded-card bg-surface border border-line shadow-card p-6">
              <div class="w-10 h-10 rounded-2xl bg-seg text-accent border border-line flex items-center justify-center text-xl mb-4">
                ✈️
              </div>
              <h4 class="text-sm font-bold text-ink">Airport Operations Hub</h4>
              <p class="text-xs text-ink-2 mt-1 leading-relaxed">
                Ground staff boarding gate verifier, flight status controller, and passenger manifest viewer for operations.
              </p>
            </div>

            <div class="rounded-card bg-surface border border-line shadow-card p-6">
              <div class="w-10 h-10 rounded-2xl bg-seg text-accent border border-line flex items-center justify-center text-xl mb-4">
                🔔
              </div>
              <h4 class="text-sm font-bold text-ink">Real-Time Alerts</h4>
              <p class="text-xs text-ink-2 mt-1 leading-relaxed">
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
      this.authService.openLogin();
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

import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AbStatusPillComponent } from './ab-status-pill.component';

@Component({
  selector: 'ab-ticket',
  standalone: true,
  imports: [CommonModule, AbStatusPillComponent],
  template: `
    <article class="card pass w-full" [ngClass]="customClass">
      <!-- Main Ticket Body -->
      <div class="main p-6 sm:p-7 flex flex-col justify-between">
        <!-- Top header info -->
        <div class="top-meta flex justify-between items-center gap-3 flex-wrap text-sm text-ink-2 mb-4">
          <div class="flex items-center gap-2">
            <span>Booking</span>
            <b class="text-ink font-bold tracking-wide">{{ pnr }}</b>
            @if (flightNumber) {
              <span class="text-xs px-2.5 py-0.5 rounded-full bg-seg text-ink-2 font-semibold">
                {{ flightNumber }}
              </span>
            }
          </div>
          <ab-status-pill [status]="status" [label]="statusLabel"></ab-status-pill>
        </div>

        <!-- Route Visualization -->
        <div class="route my-5">
          <!-- Origin -->
          <div>
            <div class="code text-ink">{{ originCode }}</div>
            <div class="city text-ink-2 font-medium">{{ originCity }}</div>
            @if (departureTime) {
              <div class="time text-ink mt-1">{{ departureTime }}</div>
            }
          </div>

          <!-- Path -->
          <div class="path">
            <span class="font-medium text-xs sm:text-sm text-ink-2">{{ duration || 'Direct' }}</span>
            <i></i>
            <svg viewBox="0 0 24 24" aria-hidden="true">
              <path d="M21 16v-2l-8-5V3.5a1.5 1.5 0 0 0-3 0V9l-8 5v2l8-2.5V19l-2 1.5V22l3.5-1 3.5 1v-1.5L13 19v-5.5z"/>
            </svg>
            @if (routeNote) {
              <span class="block mt-1 text-xs font-semibold" [ngClass]="routeNoteColor">{{ routeNote }}</span>
            }
          </div>

          <!-- Destination -->
          <div class="end">
            <div class="code text-ink">{{ destinationCode }}</div>
            <div class="city text-ink-2 font-medium">{{ destinationCity }}</div>
            @if (arrivalTime) {
              <div class="time text-ink mt-1">{{ arrivalTime }}</div>
            }
          </div>
        </div>

        <!-- Metadata Chips -->
        <div class="chips flex flex-wrap gap-2 my-2">
          @if (flightNumber) {
            <span>{{ flightNumber }}</span>
          }
          @if (gate) {
            <span>{{ gate }}</span>
          }
          @if (seat) {
            <span>Seat {{ seat }}</span>
          }
          @for (chip of chips; track chip) {
            <span>{{ chip }}</span>
          }
          <ng-content select="[slot=chips]"></ng-content>
        </div>

        <!-- Action Buttons -->
        <div class="btns flex flex-wrap gap-2.5 mt-5">
          <ng-content select="[slot=actions]"></ng-content>
        </div>
      </div>

      <!-- Perforated Stub Section -->
      <div class="stub p-6 sm:p-7 flex flex-col justify-between gap-4 bg-accent-soft">
        <div>
          <div class="k text-xs text-ink-3">Passenger</div>
          <div class="v text-lg sm:text-xl font-extrabold tracking-tight text-ink mt-0.5 truncate">
            {{ passengerName || 'Passenger' }}
          </div>
        </div>

        @if (totalPrice) {
          <div>
            <div class="k text-xs text-ink-3">Total paid</div>
            <div class="v text-xl sm:text-2xl font-extrabold tracking-tight text-ink mt-0.5">
              {{ totalPrice }}
            </div>
          </div>
        }

        @if (seat) {
          <div>
            <div class="k text-xs text-ink-3">Seat assigned</div>
            <div class="v text-lg font-extrabold text-accent mt-0.5">
              {{ seat }}
            </div>
          </div>
        }

        <!-- Dynamic Barcode graphic -->
        <div class="bar-code w-full" aria-hidden="true"></div>
      </div>
    </article>
  `
})
export class AbTicketComponent {
  @Input() pnr = '';
  @Input() flightNumber = '';
  @Input() status = 'confirmed';
  @Input() statusLabel?: string;
  @Input() originCode = 'BOM';
  @Input() originCity = 'Mumbai';
  @Input() departureTime = '';
  @Input() destinationCode = 'DEL';
  @Input() destinationCity = 'New Delhi';
  @Input() arrivalTime = '';
  @Input() duration = 'Non-stop';
  @Input() routeNote = 'On time';
  @Input() routeNoteColor = 'text-ok';
  @Input() passengerName = '';
  @Input() seat = '';
  @Input() gate = '';
  @Input() totalPrice = '';
  @Input() chips: string[] = [];
  @Input() customClass = '';
}

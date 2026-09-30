import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'ab-hero-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="card hero w-full h-full relative" [ngClass]="customClass">
      @if (tagline) {
        <span class="tagline">
          <b></b>
          <span>{{ tagline }}</span>
        </span>
      }
      <div class="z-10 my-4">
        <h1 class="text-white font-extrabold tracking-tightest m-0 leading-tight">
          {{ title }}
        </h1>
        @if (subtitle) {
          <p class="text-white/85 text-base sm:text-lg mt-3 max-w-2xl leading-relaxed">
            {{ subtitle }}
          </p>
        }
        <ng-content></ng-content>
      </div>

      <svg class="plane" viewBox="0 0 24 24" aria-hidden="true">
        <path d="M21 16v-2l-8-5V3.5a1.5 1.5 0 0 0-3 0V9l-8 5v2l8-2.5V19l-2 1.5V22l3.5-1 3.5 1v-1.5L13 19v-5.5z"/>
      </svg>
    </div>
  `
})
export class AbHeroCardComponent {
  @Input() tagline = '';
  @Input() title = '';
  @Input() subtitle = '';
  @Input() customClass = '';
}

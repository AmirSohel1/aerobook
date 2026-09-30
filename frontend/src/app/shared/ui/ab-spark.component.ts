import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'ab-spark',
  standalone: true,
  imports: [CommonModule],
  template: `
    <svg class="spark w-full h-20" viewBox="0 0 200 80" preserveAspectRatio="none" [ngClass]="customClass">
      <defs>
        <linearGradient [id]="gradientId" x1="0" y1="0" x2="0" y2="1">
          <stop offset="0%" stop-color="var(--accent-2)" stop-opacity="0.45" />
          <stop offset="100%" stop-color="var(--accent-2)" stop-opacity="0" />
        </linearGradient>
      </defs>
      <path class="a" [attr.fill]="'url(#' + gradientId + ')'" [attr.d]="areaPath" />
      <path class="l" [attr.d]="linePath" />
    </svg>
  `
})
export class AbSparkComponent {
  @Input() customClass = '';
  @Input() gradientId = 'sg-' + Math.random().toString(36).substring(2, 9);
  @Input() linePath = 'M0 66 C25 60 35 40 60 44 S100 62 125 34 S170 14 200 10';
  @Input() areaPath = 'M0 66 C25 60 35 40 60 44 S100 62 125 34 S170 14 200 10 V80 H0Z';
}

import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

export type StatVariant = 'accent' | 'green' | 'amber' | 'red';

@Component({
  selector: 'ab-stat-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="card flex flex-col justify-between h-full" [ngClass]="customClass">
      <div>
        <div class="flex items-center justify-between mb-3">
          <div
            class="ic"
            [ngClass]="{
              'g': variant === 'green',
              'a': variant === 'amber',
              'p': variant === 'red'
            }"
          >
            <ng-content select="[slot=icon]"></ng-content>
          </div>
          @if (delta) {
            <span
              class="delta"
              [ngClass]="{
                'warn': variant === 'amber',
                'bad': variant === 'red'
              }"
            >
              {{ delta }}
            </span>
          }
        </div>
        <div class="lbl">{{ label }}</div>
        <div class="num my-1">{{ value }}</div>
      </div>
      @if (note) {
        <div class="note">{{ note }}</div>
      }
      <ng-content></ng-content>
    </div>
  `
})
export class AbStatCardComponent {
  @Input() label = '';
  @Input() value: string | number = '';
  @Input() note = '';
  @Input() delta = '';
  @Input() variant: StatVariant = 'accent';
  @Input() customClass = '';
}

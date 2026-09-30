import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

export type StatusType = 'confirmed' | 'ontime' | 'completed' | 'delayed' | 'cancelled' | 'pending';

@Component({
  selector: 'ab-status-pill',
  standalone: true,
  imports: [CommonModule],
  template: `
    <span
      class="status inline-flex items-center gap-1.5"
      [ngClass]="{
        'done': status === 'completed',
        'warn': status === 'delayed' || status === 'pending',
        'bad': status === 'cancelled'
      }"
    >
      <span class="w-1.5 h-1.5 rounded-full"
        [ngClass]="{
          'bg-ok': status === 'confirmed' || status === 'ontime',
          'bg-accent': status === 'completed',
          'bg-warn': status === 'delayed' || status === 'pending',
          'bg-bad': status === 'cancelled'
        }"
      ></span>
      <span>{{ label || formatStatus(status) }}</span>
    </span>
  `
})
export class AbStatusPillComponent {
  @Input() status: StatusType | string = 'confirmed';
  @Input() label?: string;

  formatStatus(st: string): string {
    if (!st) return '';
    return st.charAt(0).toUpperCase() + st.slice(1).toLowerCase();
  }
}

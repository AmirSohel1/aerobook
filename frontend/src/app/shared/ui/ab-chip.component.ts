import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'ab-chip',
  standalone: true,
  imports: [CommonModule],
  template: `
    <span
      class="inline-flex items-center gap-1.5 py-1.5 px-3 rounded-full text-xs font-semibold"
      [ngClass]="[
        variant === 'seg' ? 'bg-seg text-ink-2' : '',
        variant === 'accent' ? 'bg-accent-soft text-accent' : '',
        variant === 'green' ? 'bg-ok-soft text-ok' : '',
        variant === 'amber' ? 'bg-warn-soft text-warn' : '',
        variant === 'red' ? 'bg-bad-soft text-bad' : '',
        customClass
      ]"
    >
      <ng-content select="[slot=icon]"></ng-content>
      <ng-content></ng-content>
    </span>
  `
})
export class AbChipComponent {
  @Input() variant: 'seg' | 'accent' | 'green' | 'amber' | 'red' = 'seg';
  @Input() customClass: string = '';
}

import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'ab-meter',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="card mesh flex flex-col gap-3.5" [ngClass]="customClass">
      <div class="row flex justify-between items-center">
        <strong class="font-bold text-ink text-sm">{{ title }}</strong>
        <span
          class="dot w-2.5 h-2.5 rounded-full"
          [ngClass]="{
            'bg-ok shadow-[0_0_0_5px_var(--green-soft)]': status === 'ok',
            'bg-warn shadow-[0_0_0_5px_var(--amber-soft)]': status === 'warn',
            'bg-bad shadow-[0_0_0_5px_var(--red-soft)]': status === 'bad'
          }"
        ></span>
      </div>
      <div class="meter h-1.5 rounded-full bg-seg overflow-hidden">
        <i
          class="block h-full rounded-full transition-all duration-500"
          [style.width.%]="percentage"
          [ngStyle]="{
            'background': status === 'ok' ? 'linear-gradient(90deg, var(--accent-2), var(--green))' : (status === 'warn' ? 'var(--amber)' : 'var(--red)')
          }"
        ></i>
      </div>
      <small class="text-ink-3 text-xs flex justify-between">
        <span>{{ leftLabel || (percentage + '% uptime') }}</span>
        <span>{{ rightLabel }}</span>
      </small>
    </div>
  `
})
export class AbMeterComponent {
  @Input() title = '';
  @Input() status: 'ok' | 'warn' | 'bad' = 'ok';
  @Input() percentage = 99.98;
  @Input() leftLabel = '';
  @Input() rightLabel = '';
  @Input() customClass = '';
}

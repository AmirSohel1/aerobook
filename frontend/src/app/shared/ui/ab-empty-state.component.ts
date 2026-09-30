import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'ab-empty-state',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="p-8 sm:p-12 text-center rounded-2xl bg-surface/60 border border-line flex flex-col items-center justify-center space-y-3">
      <div class="w-14 h-14 rounded-2xl bg-seg border border-line flex items-center justify-center text-2xl shadow-sm">
        {{ icon || '📭' }}
      </div>
      <div class="space-y-1 max-w-sm">
        <h4 class="text-sm font-bold text-ink">{{ title }}</h4>
        <p class="text-xs text-ink-2 leading-relaxed">{{ description }}</p>
      </div>
      @if (actionLabel) {
        <button type="button" (click)="action.emit()"
                class="mt-2 px-4 py-2 rounded-xl bg-accent text-white text-xs font-semibold hover:opacity-90 shadow-sm transition-all cursor-pointer">
          {{ actionLabel }}
        </button>
      }
    </div>
  `
})
export class AbEmptyStateComponent {
  @Input() icon?: string;
  @Input() title: string = 'No records found';
  @Input() description: string = 'There is currently no data to display in this view.';
  @Input() actionLabel?: string;
  @Output() action = new EventEmitter<void>();
}

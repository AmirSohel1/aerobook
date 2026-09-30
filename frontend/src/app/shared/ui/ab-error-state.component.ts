import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'ab-error-state',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="p-6 rounded-2xl bg-danger-soft/40 border border-danger/30 text-ink flex flex-col items-center justify-center text-center space-y-3">
      <div class="w-12 h-12 rounded-2xl bg-danger/10 border border-danger/20 text-danger flex items-center justify-center text-xl">
        ⚠️
      </div>
      <div class="space-y-1 max-w-sm">
        <h4 class="text-sm font-bold text-danger">{{ title || 'Failed to Load Data' }}</h4>
        <p class="text-xs text-ink-2 leading-relaxed">{{ message || 'Unable to connect to service. Please check your connection and retry.' }}</p>
      </div>
      @if (showRetry) {
        <button type="button" (click)="retry.emit()"
                class="px-4 py-2 rounded-xl bg-danger/10 hover:bg-danger/20 text-danger border border-danger/30 text-xs font-bold transition-all cursor-pointer flex items-center gap-1.5">
          <span>🔄</span>
          <span>Retry</span>
        </button>
      }
    </div>
  `
})
export class AbErrorStateComponent {
  @Input() title?: string;
  @Input() message?: string;
  @Input() showRetry: boolean = true;
  @Output() retry = new EventEmitter<void>();
}

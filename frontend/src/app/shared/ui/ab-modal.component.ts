import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'ab-modal',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div
      class="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6 overflow-y-auto"
      role="dialog"
      aria-modal="true"
    >
      <!-- Backdrop -->
      <div
        class="fixed inset-0 bg-black/40 backdrop-blur-md transition-opacity animate-fade-in"
        (click)="closeModal()"
      ></div>

      <!-- Dialog Card -->
      <div
        class="relative w-full z-10 card p-6 sm:p-8 !rounded-card shadow-card my-auto overflow-hidden animate-scale-up"
        [ngClass]="maxWidthClass"
      >
        <!-- Header -->
        <div class="flex items-start justify-between gap-4 mb-6">
          <div>
            @if (tagline) {
              <span class="inline-flex items-center gap-1.5 py-1 px-3 rounded-full text-xs font-semibold bg-accent-soft text-accent mb-2">
                {{ tagline }}
              </span>
            }
            <h2 class="text-xl sm:text-2xl font-extrabold tracking-tight text-ink m-0">
              {{ title }}
            </h2>
            @if (subtitle) {
              <p class="text-sm text-ink-2 mt-1">{{ subtitle }}</p>
            }
          </div>

          <button
            type="button"
            (click)="closeModal()"
            class="w-9 h-9 rounded-full bg-seg hover:bg-line/40 text-ink-2 hover:text-ink flex items-center justify-center transition-all hover:rotate-90"
            aria-label="Close modal"
          >
            <svg class="w-5 h-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="18" y1="6" x2="6" y2="18"></line>
              <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
          </button>
        </div>

        <!-- Content -->
        <div class="text-ink">
          <ng-content></ng-content>
        </div>

        <!-- Actions footer -->
        @if (hasFooter) {
          <div class="mt-6 pt-4 border-t border-line flex flex-wrap items-center justify-end gap-3">
            <ng-content select="[slot=footer]"></ng-content>
          </div>
        }
      </div>
    </div>
  `
})
export class AbModalComponent {
  @Input() title = '';
  @Input() subtitle = '';
  @Input() tagline = '';
  @Input() maxWidthClass = 'max-w-lg';
  @Input() hasFooter = false;
  @Output() close = new EventEmitter<void>();

  closeModal(): void {
    this.close.emit();
  }
}

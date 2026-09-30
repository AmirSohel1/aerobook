import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

export type ButtonVariant = 'primary' | 'secondary' | 'danger' | 'ghost';
export type ButtonSize = 'sm' | 'md' | 'lg';

@Component({
  selector: 'ab-button',
  standalone: true,
  imports: [CommonModule],
  template: `
    <button
      [type]="type"
      [disabled]="disabled || loading"
      (click)="onClick($event)"
      class="btn"
      [ngClass]="[
        variant === 'primary' ? 'p' : '',
        variant === 'danger' ? 'd' : '',
        variant === 'ghost' ? '!bg-transparent hover:!bg-seg' : '',
        size === 'sm' ? '!py-1.5 !px-3.5 !text-xs' : '',
        size === 'lg' ? '!py-3.5 !px-7 !text-base' : '',
        fullWidth ? '!w-full' : '',
        disabled || loading ? 'opacity-50 cursor-not-allowed pointer-events-none' : '',
        customClass
      ]"
    >
      @if (loading) {
        <svg class="animate-spin w-4 h-4 mr-2" viewBox="0 0 24 24" fill="none">
          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8v8H4z"></path>
        </svg>
      }
      <ng-content select="[slot=prefix]"></ng-content>
      <ng-content></ng-content>
      <ng-content select="[slot=suffix]"></ng-content>
    </button>
  `
})
export class AbButtonComponent {
  @Input() variant: ButtonVariant = 'secondary';
  @Input() size: ButtonSize = 'md';
  @Input() type: 'button' | 'submit' | 'reset' = 'button';
  @Input() disabled: boolean = false;
  @Input() loading: boolean = false;
  @Input() fullWidth: boolean = false;
  @Input() customClass: string = '';
  @Output() btnClick = new EventEmitter<MouseEvent>();

  onClick(event: MouseEvent): void {
    if (!this.disabled && !this.loading) {
      this.btnClick.emit(event);
    }
  }
}

import { Component, forwardRef, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'ab-checkbox',
  standalone: true,
  imports: [CommonModule],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => AbCheckboxComponent),
      multi: true
    }
  ],
  template: `
    <label class="inline-flex items-center gap-3 cursor-pointer select-none group" [class.opacity-50]="disabled">
      <div
        class="w-5 h-5 rounded-lg border flex items-center justify-center transition-all"
        [ngClass]="value ? 'bg-accent border-accent text-white' : 'bg-seg border-line group-hover:border-accent-2'"
      >
        @if (value) {
          <svg class="w-3.5 h-3.5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="3" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="20 6 9 17 4 12"></polyline>
          </svg>
        }
      </div>
      <input
        type="checkbox"
        [checked]="value"
        [disabled]="disabled"
        (change)="onToggle($event)"
        class="sr-only"
      />
      <span class="text-sm font-medium text-ink-2 group-hover:text-ink">
        <ng-content></ng-content>
      </span>
    </label>
  `
})
export class AbCheckboxComponent implements ControlValueAccessor {
  @Input() disabled = false;
  value = false;

  onChange: any = () => {};
  onTouched: any = () => {};

  writeValue(val: boolean): void {
    this.value = !!val;
  }

  registerOnChange(fn: any): void {
    this.onChange = fn;
  }

  registerOnTouched(fn: any): void {
    this.onTouched = fn;
  }

  setDisabledState?(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  onToggle(event: Event): void {
    if (this.disabled) return;
    this.value = (event.target as HTMLInputElement).checked;
    this.onChange(this.value);
    this.onTouched();
  }
}

import { Component, forwardRef, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR } from '@angular/forms';

export interface SelectOption {
  value: any;
  label: string;
}

@Component({
  selector: 'ab-select',
  standalone: true,
  imports: [CommonModule, FormsModule],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => AbSelectComponent),
      multi: true
    }
  ],
  template: `
    <div class="w-full flex flex-col gap-1.5" [ngClass]="customClass">
      @if (label) {
        <label [for]="id" class="text-xs font-semibold text-ink-2 tracking-tight">
          {{ label }}
          @if (required) {
            <span class="text-bad ml-0.5">*</span>
          }
        </label>
      }

      <div class="relative flex items-center">
        <select
          [id]="id"
          [disabled]="disabled"
          [value]="value"
          (change)="onSelectChange($event)"
          (blur)="onBlur()"
          class="w-full h-12 pl-4 pr-10 rounded-[16px] bg-seg border border-line text-ink font-medium text-sm appearance-none transition-all focus:outline-none focus:ring-2 focus:ring-accent-2 focus:border-accent disabled:opacity-50 disabled:cursor-not-allowed"
          [ngClass]="{
            'border-bad focus:ring-bad': hasError
          }"
        >
          @if (placeholder) {
            <option value="" disabled [selected]="!value">{{ placeholder }}</option>
          }
          @for (opt of options; track opt.value) {
            <option [value]="opt.value" [selected]="opt.value === value" class="bg-solid text-ink">
              {{ opt.label }}
            </option>
          }
          <ng-content></ng-content>
        </select>

        <div class="absolute right-3.5 pointer-events-none text-ink-2">
          <svg class="w-4 h-4" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <polyline points="6 9 12 15 18 9"></polyline>
          </svg>
        </div>
      </div>

      @if (hint && !error) {
        <span class="text-[11px] text-ink-3">{{ hint }}</span>
      }
      @if (error) {
        <span class="text-[11px] font-medium text-bad">{{ error }}</span>
      }
    </div>
  `
})
export class AbSelectComponent implements ControlValueAccessor {
  @Input() id = 'ab-select-' + Math.random().toString(36).substring(2, 9);
  @Input() label = '';
  @Input() placeholder = '';
  @Input() options: SelectOption[] = [];
  @Input() hint = '';
  @Input() error = '';
  @Input() required = false;
  @Input() disabled = false;
  @Input() customClass = '';

  value: any = '';

  onChange: any = () => {};
  onTouched: any = () => {};

  get hasError(): boolean {
    return !!this.error;
  }

  writeValue(val: any): void {
    this.value = val ?? '';
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

  onSelectChange(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.value = select.value;
    this.onChange(this.value);
  }

  onBlur(): void {
    this.onTouched();
  }
}

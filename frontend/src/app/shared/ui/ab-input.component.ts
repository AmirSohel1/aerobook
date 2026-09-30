import { Component, forwardRef, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ControlValueAccessor, FormsModule, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'ab-input',
  standalone: true,
  imports: [CommonModule, FormsModule],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => AbInputComponent),
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
        <ng-content select="[slot=prefix]"></ng-content>
        <input
          [id]="id"
          [type]="type"
          [placeholder]="placeholder"
          [disabled]="disabled"
          [readonly]="readonly"
          [min]="min"
          [max]="max"
          [step]="step"
          [value]="value"
          (input)="onInputChange($event)"
          (blur)="onBlur()"
          class="w-full h-12 px-4 rounded-[16px] bg-seg border border-line text-ink placeholder:text-ink-3 font-medium text-sm transition-all focus:outline-none focus:ring-2 focus:ring-accent-2 focus:border-accent disabled:opacity-50 disabled:cursor-not-allowed"
          [ngClass]="{
            'border-bad focus:ring-bad': hasError,
            'pl-10': hasPrefix
          }"
        />
        <ng-content select="[slot=suffix]"></ng-content>
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
export class AbInputComponent implements ControlValueAccessor {
  @Input() id = 'ab-input-' + Math.random().toString(36).substring(2, 9);
  @Input() label = '';
  @Input() type = 'text';
  @Input() placeholder = '';
  @Input() hint = '';
  @Input() error = '';
  @Input() required = false;
  @Input() disabled = false;
  @Input() readonly = false;
  @Input() min?: string | number;
  @Input() max?: string | number;
  @Input() step?: string | number;
  @Input() customClass = '';
  @Input() hasPrefix = false;

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

  onInputChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.value = this.type === 'number' ? (input.value === '' ? null : Number(input.value)) : input.value;
    this.onChange(this.value);
  }

  onBlur(): void {
    this.onTouched();
  }
}

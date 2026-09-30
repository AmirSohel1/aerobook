import { Component, forwardRef, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ControlValueAccessor, NG_VALUE_ACCESSOR } from '@angular/forms';

@Component({
  selector: 'ab-toggle',
  standalone: true,
  imports: [CommonModule],
  providers: [
    {
      provide: NG_VALUE_ACCESSOR,
      useExisting: forwardRef(() => AbToggleComponent),
      multi: true
    }
  ],
  template: `
    <button
      type="button"
      role="switch"
      [attr.aria-checked]="value"
      [disabled]="disabled"
      (click)="toggle()"
      class="relative inline-flex h-6 w-11 flex-shrink-0 cursor-pointer rounded-full border-2 border-transparent transition-colors duration-200 ease-in-out focus:outline-none focus:ring-2 focus:ring-accent-2 focus:ring-offset-2"
      [ngClass]="value ? 'bg-accent' : 'bg-seg border-line'"
    >
      <span
        class="pointer-events-none inline-block h-5 w-5 transform rounded-full bg-white shadow ring-0 transition duration-200 ease-in-out"
        [ngClass]="value ? 'translate-x-5' : 'translate-x-0'"
      ></span>
    </button>
  `
})
export class AbToggleComponent implements ControlValueAccessor {
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

  toggle(): void {
    if (this.disabled) return;
    this.value = !this.value;
    this.onChange(this.value);
    this.onTouched();
  }
}

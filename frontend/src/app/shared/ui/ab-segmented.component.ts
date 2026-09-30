import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface TabOption {
  id: string;
  label: string;
  badge?: number | string;
  icon?: string;
}

@Component({
  selector: 'ab-segmented',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="seg" role="tablist" [attr.aria-label]="ariaLabel">
      @for (item of items; track item.id) {
        <button
          type="button"
          role="tab"
          [attr.aria-selected]="selectedId === item.id"
          [class.on]="selectedId === item.id"
          (click)="selectTab(item.id)"
        >
          <span>{{ item.label }}</span>
          @if (item.badge) {
            <span class="badge">{{ item.badge }}</span>
          }
        </button>
      }
    </div>
  `
})
export class AbSegmentedComponent {
  @Input() items: TabOption[] = [];
  @Input() selectedId: string = '';
  @Input() ariaLabel: string = 'Navigation tabs';
  @Output() selectedChange = new EventEmitter<string>();

  selectTab(id: string): void {
    if (this.selectedId !== id) {
      this.selectedId = id;
      this.selectedChange.emit(id);
    }
  }
}

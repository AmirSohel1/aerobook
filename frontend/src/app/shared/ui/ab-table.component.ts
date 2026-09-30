import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'ab-table',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="w-full overflow-hidden rounded-[20px] border border-line bg-surface/50 backdrop-blur-md">
      <div class="overflow-x-auto">
        <table class="w-full text-left text-sm border-collapse">
          <thead class="bg-seg text-xs uppercase tracking-wider text-ink-2 font-semibold border-b border-line">
            <ng-content select="[slot=head]"></ng-content>
          </thead>
          <tbody class="divide-y divide-line font-medium text-ink">
            <ng-content select="[slot=body]"></ng-content>
          </tbody>
        </table>
      </div>
      @if (hasFooter) {
        <div class="p-3 bg-seg/40 border-t border-line text-xs text-ink-3 flex items-center justify-between">
          <ng-content select="[slot=footer]"></ng-content>
        </div>
      }
    </div>
  `
})
export class AbTableComponent {
  @Input() hasFooter = false;
}

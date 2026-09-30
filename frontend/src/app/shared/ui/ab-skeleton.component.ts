import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'ab-skeleton',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div [ngClass]="[
      'animate-pulse rounded-xl bg-seg/80 dark:bg-seg/40 border border-line/40',
      customClass || 'h-10 w-full'
    ]"></div>
  `
})
export class AbSkeletonComponent {
  @Input() customClass?: string;
}

import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'ab-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div
      class="card transition-all duration-300"
      [ngClass]="customClass"
    >
      <ng-content></ng-content>
    </div>
  `
})
export class AbCardComponent {
  @Input() customClass = '';
}

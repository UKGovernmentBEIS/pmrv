import { ChangeDetectionStrategy, Component } from '@angular/core';

import { BehaviorSubject } from 'rxjs';

@Component({
  selector: 'div[app-dropdown-button]',
  template: `
    <div class="dropdown-button-container">
      <button type="button" class="govuk-button main-button" (click)="click()">
        Actions
        <span class="arrow"></span>
      </button>
      <ng-container *ngIf="isOpen | async">
        <div class="action-buttons">
          <ng-content></ng-content>
        </div>
      </ng-container>
    </div>
  `,
  styleUrls: ['./dropdown-button.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DropdownButtonComponent {
  isOpen = new BehaviorSubject<boolean>(false);

  click(): void {
    this.isOpen.next(!this.isOpen.getValue());
  }
}

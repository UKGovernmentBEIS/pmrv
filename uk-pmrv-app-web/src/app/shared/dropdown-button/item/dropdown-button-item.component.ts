import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'div[app-dropdown-button-item]',
  template: ` <button type="button" class="govuk-button button-item" (click)="click()">{{ label }}</button> `,
  styles: [
    `
      .button-item {
        margin-bottom: 0px;
        color: #383f43;
        background: #eeefef;
        box-shadow: none;
      }
    `,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DropdownButtonItemComponent {
  @Input() label: string;
  @Output() readonly actionEmit = new EventEmitter();

  click(): void {
    this.actionEmit.emit(null);
  }
}

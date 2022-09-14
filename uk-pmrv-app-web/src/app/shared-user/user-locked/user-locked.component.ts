import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-user-locked',
  template: `
    <div class="locked">
      <span aria-hidden="true" class="govuk-warning-text__icon">!</span>
      <span class="locked-text govuk-!-font-size-19 govuk-!-padding-left-1">Locked</span>
    </div>
  `,
  styleUrls: ['./user-locked.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserLockedComponent {}

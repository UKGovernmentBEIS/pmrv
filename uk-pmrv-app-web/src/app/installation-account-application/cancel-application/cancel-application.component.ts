import { ChangeDetectionStrategy, Component } from '@angular/core';

import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';

@Component({
  selector: 'app-cancel',
  template: `
    <app-page-heading caption="Delete installation account creation" size="xl">
      Are you sure you want to leave this page?
    </app-page-heading>

    <p class="govuk-body">The data entered will be permanently deleted. This action cannot be undone.</p>

    <div class="govuk-button-group">
      <button govukWarnButton type="button" routerLink="/" (click)="store.reset()">Yes, delete</button>
      <a govukLink routerLink="..">Cancel</a>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelApplicationComponent {
  constructor(readonly store: InstallationAccountApplicationStore) {}
}

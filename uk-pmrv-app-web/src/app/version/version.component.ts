import { ChangeDetectionStrategy, Component } from '@angular/core';

import { VERSION } from '../../environments/version';

@Component({
  selector: 'app-version',
  template: `
    <app-page-heading caption="Information about the application version" size="l">About</app-page-heading>

    <p class="govuk-body">
      Version: <span class="govuk-!-font-weight-bold">{{ version.tag }}</span>
    </p>
    <h2 class="govuk-heading-m">Development details</h2>
    <p class="govuk-body">
      Branch: <span class="govuk-!-font-weight-bold">{{ version.branch }}</span>
    </p>
    <p class="govuk-body">
      Version: <span class="govuk-!-font-weight-bold">{{ version.devVersion }}</span>
    </p>
    <p class="govuk-body">
      Commit hash: <span class="govuk-!-font-weight-bold">{{ version.hash }}</span>
    </p>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class VersionComponent {
  version = VERSION;
}

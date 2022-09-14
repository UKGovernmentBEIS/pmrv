import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-internal-server-error',
  template: `
    <app-page-heading size="xl">Sorry, there is a problem with the service</app-page-heading>

    <p class="govuk-body">Try again later.</p>

    <p class="govuk-body">
      If problem persists contact the PMRV at:
      <a href="mailto:support@pmrv.com" govukLink class="govuk-!-font-weight-bold">support@pmrv.com</a>
    </p>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InternalServerErrorComponent {}

import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-page-not-found',
  template: `
    <app-page-heading size="xl">Page Not Found</app-page-heading>
    <p class="govuk-body">If you typed the web address, check it is correct.</p>
    <p class="govuk-body">If you pasted the web address, check you copied the entire address.</p>
    <p class="govuk-body">
      If the web address is correct please contact the PMRV at:
      <a href="mailto:support@pmrv.com" govukLink class="govuk-!-font-weight-bold">support@pmrv.com</a>
    </p>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PageNotFoundComponent {}

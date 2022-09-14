import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { BreadcrumbService } from '../../shared/breadcrumbs/breadcrumb.service';

@Component({
  selector: 'app-start-process',
  template: `
    <app-page-heading>Create a UK Emissions Trading Scheme reporting sign in - GOV.UK</app-page-heading>
    <div class="govuk-body">
      <p>Register your details to sign in and access the UK Emissions Trading Scheme reporting service.</p>
      <br />
      <h2 class="govuk-heading-m">Before you start</h2>
      <p>You'll need an email address that is not shared with anyone else. This should be your work email.</p>
      <p>You'll need a mobile phone or tablet to set up two-factor authentication.</p>
      <a govukButton routerLink="email"> Continue </a>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BreadcrumbService],
})
export class StartProcessComponent implements OnInit {
  constructor(private readonly breadcrumbService: BreadcrumbService) {}

  ngOnInit(): void {
    this.breadcrumbService.show([{ text: 'Home', link: ['..'] }]);
  }
}

import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, tap } from 'rxjs';

import {
  effectiveDateMinValidator,
  feeDateMinValidator,
  PERMIT_REVOCATION_TASK_FORM,
  permitRevocationFormProvider,
} from '@permit-revocation/factory/permit-revocation-form-provider';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';

@Component({
  selector: 'app-summary-container',
  template: `<div class="govuk-grid-row">
    <div class="govuk-grid-column-full">
      <govuk-notification-banner *ngIf="notification" type="success">
        <h1 class="govuk-notification-banner__heading">Details updated</h1>
      </govuk-notification-banner>
      <govuk-error-summary [form]="form"></govuk-error-summary>
      <app-page-heading>{{ (route.data | async)?.pageTitle }}</app-page-heading>
      <app-summary class="govuk-!-margin-bottom-6 govuk-!-display-block" [errors]="form?.errors"></app-summary>
      <a govukLink routerLink="../..">Return to: Permit revocation task list</a>
    </div>
  </div>`,
  providers: [permitRevocationFormProvider],

  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryContainerComponent implements OnInit {
  notification: any;

  constructor(
    @Inject(PERMIT_REVOCATION_TASK_FORM) readonly form: FormGroup,
    readonly route: ActivatedRoute,
    readonly router: Router,
    readonly store: PermitRevocationStore,
  ) {
    this.notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  }

  ngOnInit(): void {
    this.store
      .pipe(
        first(),
        tap((state) => {
          this.form.addValidators(effectiveDateMinValidator());
          if (state.permitRevocation?.feeDate) {
            this.form.addValidators(feeDateMinValidator());
          }
        }),
      )
      .subscribe(() => this.form.updateValueAndValidity());
  }
}

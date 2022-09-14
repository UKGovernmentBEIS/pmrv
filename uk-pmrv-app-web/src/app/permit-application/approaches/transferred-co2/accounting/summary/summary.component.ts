import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { map, pluck, tap } from 'rxjs';

import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { isReviewUrl } from '../../../approaches';
import { measurementDevicesFormProvider } from '../measurement-devices-form.provider';

@Component({
  selector: 'app-summary',
  template: `
    <app-permit-task
      [notification]="notification"
      [breadcrumb]="[{ text: 'Transferred CO2', link: ['transferred-co2'] }]"
    >
      <govuk-error-summary *ngIf="isSummaryDisplayed$ | async" [form]="form"></govuk-error-summary>
      <app-page-heading caption="Transferred CO2">Accounting for emissions from transferred CO2</app-page-heading>
      <div class="govuk-body">
        <p>Get help with <a [routerLink]="" govukLink>transferred CO2</a>.</p>
      </div>
      <app-accounting-summary-template
        [accounting]="('TRANSFERRED_CO2' | monitoringApproachTask | async).accountingEmissions"
        [changePerStage]="true"
        [missingMeasurementDevice]="isSummaryDisplayed$ | async"
        cssClass="summary-list--edge-border"
      ></app-accounting-summary-template>
      <app-approach-return-link
        parentTitle="Transferred CO2"
        reviewGroupUrl="transferred-co2"
      ></app-approach-return-link>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [measurementDevicesFormProvider],
})
export class SummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  returnToUrl$ = this.store.pipe(
    pluck('requestTaskType'),
    map((type) => (isReviewUrl(type) ? '../../../review/transferred-co2' : '../..')),
  );

  isSummaryDisplayed$ = this.store.pipe(
    tap(() => this.form.updateValueAndValidity()),
    map(() => this.form.errors?.measurementDeviceNotExist),
  );

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    private readonly router: Router,
  ) {}
}

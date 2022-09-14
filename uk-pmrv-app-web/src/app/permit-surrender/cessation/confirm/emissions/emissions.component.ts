import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { PERMIT_SURRENDER_TASK_FORM } from '../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../store/permit-surrender.store';
import { emissionsFormProvider } from './emissions-form.provider';

@Component({
  selector: 'app-emissions',
  template: `<app-wizard-step
      (formSubmit)="onContinue()"
      [formGroup]="form"
      submitText="Continue"
      [hideSubmit]="(store.isEditable$ | async) === false"
    >
      <span class="govuk-caption-l">Confirm cessation of regulated activities</span>
      <app-page-heading>What is the installations annual reportable emissions?</app-page-heading>
      <div
        formControlName="annualReportableEmissions"
        govuk-text-input
        inputType="number"
        hint="Tonnes of carbon dioxide equivalent (CO2e)"
        suffix="tCO2e"
        widthClass="govuk-input--width-10"
      ></div>
    </app-wizard-step>
    <a govukLink routerLink="../..">Return to: Cessation</a> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [emissionsFormProvider],
})
export class EmissionsComponent implements PendingRequest {
  constructor(
    @Inject(PERMIT_SURRENDER_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitSurrenderStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../refund'], { relativeTo: this.route });
    } else {
      const annualReportableEmissions = this.form.value.annualReportableEmissions;

      this.store
        .pipe(
          first(),
          switchMap((state) =>
            this.store.postSaveCessation(
              {
                ...state.cessation,
                annualReportableEmissions,
              },
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['../refund'], { relativeTo: this.route }));
    }
  }
}

import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import {
  PERMIT_REVOCATION_CESSATION_TASK_FORM,
  permitRevocationCessationFormProvider,
} from '@permit-revocation/cessation/confirm/core/factory/cessation-form-provider';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { PermitRevocationStore } from '../../../store/permit-revocation-store';

@Component({
  selector: 'app-revocation-cessation-emissions',
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
  providers: [permitRevocationCessationFormProvider],
})
export class EmissionsComponent implements PendingRequest {
  constructor(
    @Inject(PERMIT_REVOCATION_CESSATION_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitRevocationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    const navigateToNextStep = () => this.router.navigate(['../refund'], { relativeTo: this.route });
    if (!this.form.dirty) {
      navigateToNextStep();
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
        .subscribe(() => navigateToNextStep());
    }
  }
}

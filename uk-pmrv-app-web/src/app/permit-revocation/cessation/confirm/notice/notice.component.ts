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
import { OfficialNoticeTypeMap } from '../core/cessation';

@Component({
  selector: 'app-revocation-cessation-notice',
  template: `
    <app-wizard-step
      (formSubmit)="onContinue()"
      [formGroup]="form"
      submitText="Continue"
      [hideSubmit]="(store.isEditable$ | async) === false"
    >
      <span class="govuk-caption-l">Confirm cessation of regulated activities</span>
      <app-page-heading>What text should appear in the official notice following the cessation?</app-page-heading>
      <div govuk-radio formControlName="noticeType" hint="Explain how this is used and where its included">
        <govuk-radio-option
          [label]="officialNoticeTypeMap['SATISFIED_WITH_REQUIREMENTS_COMPLIANCE']"
          value="SATISFIED_WITH_REQUIREMENTS_COMPLIANCE"
        ></govuk-radio-option>
        <govuk-radio-option
          [label]="officialNoticeTypeMap['NO_PROSPECT_OF_FURTHER_ALLOWANCES']"
          value="NO_PROSPECT_OF_FURTHER_ALLOWANCES"
        ></govuk-radio-option>
      </div>
    </app-wizard-step>
    <a govukLink routerLink="../..">Return to: Cessation</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [permitRevocationCessationFormProvider],
})
export class NoticeComponent implements PendingRequest {
  readonly officialNoticeTypeMap = OfficialNoticeTypeMap;

  constructor(
    @Inject(PERMIT_REVOCATION_CESSATION_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitRevocationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    const navigateToNextStep = () => this.router.navigate(['../notes'], { relativeTo: this.route });
    if (!this.form.dirty) {
      navigateToNextStep();
    } else {
      const noticeType = this.form.value.noticeType;

      this.store
        .pipe(
          first(),
          switchMap((state) =>
            this.store.postSaveCessation(
              {
                ...state.cessation,
                noticeType,
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

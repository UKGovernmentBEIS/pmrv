import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { PERMIT_SURRENDER_TASK_FORM } from '../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../store/permit-surrender.store';
import { refundFormProvider } from './refund-form.provider';

@Component({
  selector: 'app-refund',
  template: `<app-wizard-step
      (formSubmit)="onContinue()"
      [formGroup]="form"
      submitText="Continue"
      [hideSubmit]="(store.isEditable$ | async) === false"
    >
      <span class="govuk-caption-l">Confirm cessation of regulated activities</span>

      <app-page-heading>Should the installations subsistence fee be refunded?</app-page-heading>

      <div govuk-radio formControlName="subsistenceFeeRefunded">
        <govuk-radio-option label="Yes" [value]="true"></govuk-radio-option>
        <govuk-radio-option label="No" [value]="false"></govuk-radio-option>
      </div>
    </app-wizard-step>
    <a govukLink routerLink="../..">Return to: Cessation</a> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [refundFormProvider],
})
export class RefundComponent implements PendingRequest {
  constructor(
    @Inject(PERMIT_SURRENDER_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitSurrenderStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../notice'], { relativeTo: this.route });
    } else {
      const subsistenceFeeRefunded = this.form.value.subsistenceFeeRefunded;

      this.store
        .pipe(
          first(),
          switchMap((state) =>
            this.store.postSaveCessation(
              {
                ...state.cessation,
                subsistenceFeeRefunded,
              },
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['../notice'], { relativeTo: this.route }));
    }
  }
}

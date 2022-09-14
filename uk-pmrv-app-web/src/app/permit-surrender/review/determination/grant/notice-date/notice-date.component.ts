import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../core/interfaces/pending-request.interface';
import { PERMIT_SURRENDER_TASK_FORM } from '../../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { noticeDateFormProvider } from './notice-date-form.provider';

@Component({
  selector: 'app-notice-date',
  template: `
    <app-wizard-step
      (formSubmit)="onContinue()"
      [formGroup]="form"
      submitText="Continue"
      [hideSubmit]="(store.isEditable$ | async) === false"
    >
      <span class="govuk-caption-l">Grant permit surrender</span>

      <app-page-heading>Set the effective date of the notice granting the surrender request</app-page-heading>
      <div class="govuk-hint">
        A notice must be sent at least 28 days before the chosen date to allow the operator to lodge an appeal if they
        wish.
      </div>

      <div formControlName="noticeDate" govuk-date-input></div>
    </app-wizard-step>
    <a govukLink routerLink="../../..">Return to: Permit surrender review</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [noticeDateFormProvider],
})
export class NoticeDateComponent implements PendingRequest {
  constructor(
    @Inject(PERMIT_SURRENDER_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitSurrenderStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../report'], { relativeTo: this.route });
    } else {
      const noticeDate = this.form.value.noticeDate;
      this.store
        .pipe(
          first(),
          switchMap((state) =>
            this.store.postReviewDetermination(
              {
                ...state.reviewDetermination,
                noticeDate,
              },
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['../report'], { relativeTo: this.route }));
    }
  }
}

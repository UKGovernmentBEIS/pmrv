import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../core/interfaces/pending-request.interface';
import { PERMIT_SURRENDER_TASK_FORM } from '../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';
import { stopDateFormProvider } from './stop-date-form.provider';

@Component({
  selector: 'app-stop-date',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-full">
        <app-wizard-step
          (formSubmit)="onContinue()"
          [formGroup]="form"
          submitText="Continue"
          [hideSubmit]="(store.isEditable$ | async) === false"
        >
          <span class="govuk-caption-l">Surrender your permit</span>

          <app-page-heading>On what date did the regulated activities at the installation stop?</app-page-heading>
          <div class="govuk-hint">The date must not be in the future, for example, 27 3 2007</div>

          <div formControlName="stopDate" govuk-date-input [max]="today"></div>
        </app-wizard-step>
        <a govukLink routerLink="..">Return to: Permit surrender task list</a>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [stopDateFormProvider],
})
export class StopDateComponent implements PendingRequest {
  today = new Date();

  constructor(
    @Inject(PERMIT_SURRENDER_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitSurrenderStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['justification'], { relativeTo: this.route });
    } else {
      const stopDate = this.form.value.stopDate;

      combineLatest([this.route.data, this.store])
        .pipe(
          first(),
          switchMap(([data, state]) =>
            this.store.postApplyPermitSurrender({
              ...state,
              permitSurrender: {
                ...state.permitSurrender,
                stopDate,
              },
              sectionsCompleted: {
                ...state.sectionsCompleted,
                [data.statusKey]: false,
              },
            }),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['justification'], { relativeTo: this.route }));
    }
  }
}

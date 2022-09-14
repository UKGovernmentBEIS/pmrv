import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../core/interfaces/pending-request.interface';
import { PERMIT_SURRENDER_TASK_FORM } from '../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';
import { justificationFormProvider } from './justification-form.provider';

@Component({
  selector: 'app-justification',
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

          <app-page-heading>Tell us why the activities have stopped</app-page-heading>

          <div formControlName="justification" govuk-textarea [maxLength]="10000"></div>
        </app-wizard-step>
        <a govukLink routerLink="../..">Return to: Permit surrender task list</a>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [justificationFormProvider],
})
export class JustificationComponent implements PendingRequest {
  constructor(
    @Inject(PERMIT_SURRENDER_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitSurrenderStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../support-documents'], { relativeTo: this.route });
    } else {
      const justification = this.form.value.justification;

      combineLatest([this.route.data, this.store])
        .pipe(
          first(),
          switchMap(([data, state]) =>
            this.store.postApplyPermitSurrender({
              ...state,
              permitSurrender: {
                ...state.permitSurrender,
                justification,
              },
              sectionsCompleted: {
                ...state.sectionsCompleted,
                [data.statusKey]: false,
              },
            }),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['../support-documents'], { relativeTo: this.route }));
    }
  }
}

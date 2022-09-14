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
  selector: 'app-revocation-cessation-notes',
  template: `<app-wizard-step
      (formSubmit)="onContinue()"
      [formGroup]="form"
      submitText="Continue"
      [hideSubmit]="(store.isEditable$ | async) === false"
    >
      <span class="govuk-caption-l">Confirm cessation of regulated activities</span>

      <app-page-heading>Notes about the cessation</app-page-heading>

      <div
        formControlName="notes"
        govuk-textarea
        [maxLength]="10000"
        hint="This cannot be viewed by the operator"
      ></div>
    </app-wizard-step>
    <a govukLink routerLink="../..">Return to: Cessation</a> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [permitRevocationCessationFormProvider],
})
export class NotesComponent implements PendingRequest {
  constructor(
    @Inject(PERMIT_REVOCATION_CESSATION_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitRevocationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    const navigateToNextStep = () => this.router.navigate(['../answers'], { relativeTo: this.route });
    if (!this.form.dirty) {
      navigateToNextStep();
    } else {
      const notes = this.form.value.notes;

      this.store
        .pipe(
          first(),
          switchMap((state) =>
            this.store.postSaveCessation(
              {
                ...state.cessation,
                notes,
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

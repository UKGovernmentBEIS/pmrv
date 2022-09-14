import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { filter, takeUntil, tap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { originalOrder } from '@shared/keyvalue-order';

import { GovukValidators } from 'govuk-components';

import { ApplicationSectionType } from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';
import { etsSchemeMap } from './ets-scheme';

@Component({
  selector: 'app-ets-scheme',
  template: `
    <app-wizard-step
      [formGroup]="form"
      (formSubmit)="onSubmit()"
      heading="What emissions trading scheme will this installation be part of?"
      caption="ETS Scheme"
    >
      <div govuk-radio formControlName="etsSchemeType" hint="Select one option">
        <ng-container govukLegend>
          <span class="govuk-visually-hidden">What emissions trading scheme will this installation be part of?</span>
        </ng-container>
        <govuk-radio-option
          *ngFor="let option of options | keyvalue: originalOrder"
          [value]="option.key"
          [label]="option.value"
        ></govuk-radio-option>
      </div>
    </app-wizard-step>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class EtsSchemeComponent implements OnInit {
  readonly originalOrder = originalOrder;
  form: FormGroup = this.formBuilder.group({
    etsSchemeType: [null, GovukValidators.required('Select the emissions trading scheme of this installation')],
  });

  options = etsSchemeMap;

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly store: InstallationAccountApplicationStore,
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
    private readonly pendingRequestService: PendingRequestService,
  ) {}

  ngOnInit(): void {
    this.store
      .getTask(ApplicationSectionType.etsScheme)
      .pipe(
        filter((task) => !!task.value),
        tap((task) => this.form.patchValue(task.value)),
        takeUntil(this.destroy$),
      )
      .subscribe();
  }

  onSubmit(): void {
    this.store.updateTask(ApplicationSectionType.etsScheme, this.form.value, 'complete');

    if (this.store.getState().isReviewed) {
      this.store
        .amend()
        .pipe(this.pendingRequestService.trackRequest())
        .subscribe(() => this.store.nextStep('../..', this.route));
    } else {
      this.store.nextStep('../..', this.route);
    }
  }
}

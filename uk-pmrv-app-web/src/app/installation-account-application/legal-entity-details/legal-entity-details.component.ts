import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { legalEntityTypeMap } from '@shared/interfaces/legal-entity';
import { originalOrder } from '@shared/keyvalue-order';

import { GovukValidators } from 'govuk-components';

import { LEGAL_ENTITY_FORM } from '../factories/legal-entity-form.factory';
import { ApplicationSectionType } from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';

@Component({
  selector: 'app-legal-entity-details',
  templateUrl: './legal-entity-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class LegalEntityDetailsComponent implements OnInit {
  readonly originalOrder = originalOrder;
  form: FormGroup;
  radioOptions = legalEntityTypeMap;
  private readonly requiredReferenceNumberValidator = GovukValidators.required(
    'Enter a company registration number, or enter a reason for not providing one',
  );

  constructor(
    private readonly store: InstallationAccountApplicationStore,
    private readonly route: ActivatedRoute,
    @Inject(LEGAL_ENTITY_FORM) private readonly legalEntityForm: FormGroup,
    private readonly destroy$: DestroySubject,
  ) {
    this.form = this.legalEntityForm.get('detailsGroup') as FormGroup;
    this.form.markAsPristine();
    this.form.markAsUntouched();
  }

  ngOnInit(): void {
    this.checkValidators(this.form.get('noReferenceNumberReason').value);
    this.form
      .get('noReferenceNumberReason')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((value) => this.checkValidators(value));
  }

  onSubmit(): void {
    // The parent form is not submitted, thus not updated
    this.legalEntityForm.updateValueAndValidity();
    this.store.updateTask(ApplicationSectionType.legalEntity, this.legalEntityForm.value, 'complete');

    if (this.store.getState().isReviewed) {
      this.store.amend().subscribe(() => this.store.nextStep('../..', this.route));
    } else {
      this.store.nextStep('../..', this.route);
    }
  }

  private checkValidators(noReferenceNumberReasonValue: string): void {
    const control = this.form.get('referenceNumber');
    if (noReferenceNumberReasonValue) {
      control.removeValidators(this.requiredReferenceNumberValidator);
    } else {
      control.addValidators(this.requiredReferenceNumberValidator);
    }

    control.updateValueAndValidity();
  }
}

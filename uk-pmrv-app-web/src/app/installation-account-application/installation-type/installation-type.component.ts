import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { INSTALLATION_FORM } from '../factories/installation-form.factory';
import { ApplicationSectionType } from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';

@Component({
  selector: 'app-installation-type',
  template: `
    <app-wizard-step
      [formGroup]="form"
      heading="Select the installation type"
      caption="Installation details"
      (formSubmit)="onSubmit()"
    >
      <div govuk-radio formControlName="type" hint="Select one option">
        <ng-container govukLegend>
          <span class="govuk-visually-hidden">Select one option</span>
        </ng-container>
        <govuk-radio-option label="Onshore" value="ONSHORE"></govuk-radio-option>
        <govuk-radio-option label="Offshore" value="OFFSHORE"></govuk-radio-option>
      </div>
    </app-wizard-step>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InstallationTypeComponent {
  form: FormGroup;

  constructor(
    private readonly route: ActivatedRoute,
    @Inject(INSTALLATION_FORM) private readonly installationForm: FormGroup,
    private readonly store: InstallationAccountApplicationStore,
  ) {
    this.form = this.installationForm.get('installationTypeGroup') as FormGroup;
    this.form.markAsPristine();
    this.form.markAsUntouched();
  }

  onSubmit(): void {
    // The parent form is not submitted, thus not updated
    this.installationForm.updateValueAndValidity();
    const lowerCaseValue = this.form.get('type').value.toLowerCase();
    const detailsGroup = this.installationForm.get(`${lowerCaseValue}Group`) as FormGroup;
    // Enable the relevant group in order to check it for validity below
    // This is needed when changing the type from the summary
    detailsGroup.enable();
    if ((this.store.getState().isSummarized || this.store.getState().isReviewed) && detailsGroup.valid) {
      this.store.updateTask(ApplicationSectionType.installation, this.installationForm.value);
    }
    this.store.nextStep(`../${lowerCaseValue}`, this.route, [detailsGroup]);
  }
}

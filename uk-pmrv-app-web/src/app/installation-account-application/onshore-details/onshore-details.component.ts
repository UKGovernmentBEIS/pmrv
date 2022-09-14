import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { INSTALLATION_FORM } from '../factories/installation-form.factory';
import { ApplicationSectionType } from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';

@Component({
  selector: 'app-onshore-details',
  templateUrl: './onshore-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OnshoreDetailsComponent {
  form: FormGroup;

  constructor(
    private readonly route: ActivatedRoute,
    @Inject(INSTALLATION_FORM) private readonly installationForm: FormGroup,
    private readonly store: InstallationAccountApplicationStore,
  ) {
    this.installationForm.get('offshoreGroup').disable();
    this.form = this.installationForm.get('onshoreGroup') as FormGroup;
    this.form.enable();
    this.form.markAsPristine();
    this.form.markAsUntouched();
  }

  onSubmit(): void {
    // The parent form is not submitted, thus not updated
    this.installationForm.updateValueAndValidity();
    const locationGroup = this.installationForm.get('locationGroup') as FormGroup;

    if ((this.store.getState().isSummarized && locationGroup.valid) || this.store.getState().isReviewed) {
      this.store.updateTask(ApplicationSectionType.installation, this.installationForm.value);
    }
    if (this.store.getState().isReviewed) {
      this.store.amend().subscribe(() => this.store.nextStep('../emissions', this.route, [locationGroup]));
    } else {
      this.store.nextStep('../emissions', this.route, [locationGroup]);
    }
  }
}

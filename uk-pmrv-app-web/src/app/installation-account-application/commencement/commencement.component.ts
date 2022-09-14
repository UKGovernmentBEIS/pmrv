import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { filter, takeUntil, tap } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { DestroySubject } from '../../core/services/destroy-subject.service';
import { ApplicationSectionType } from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-commencement',
  templateUrl: './commencement.component.html',
  providers: [DestroySubject],
})
export class CommencementComponent implements OnInit {
  form: FormGroup = this.formBuilder.group({
    commencementDate: [null, GovukValidators.required('Enter the date of commencement')],
  });

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly store: InstallationAccountApplicationStore,
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.store
      .getTask(ApplicationSectionType.commencement)
      .pipe(
        filter((task) => !!task.value),
        tap((task) => this.form.patchValue(task.value)),
        takeUntil(this.destroy$),
      )
      .subscribe();
  }

  onSubmit(): void {
    this.store.updateTask(ApplicationSectionType.commencement, this.form.value, 'complete');
    this.store.nextStep('../', this.route);
  }
}

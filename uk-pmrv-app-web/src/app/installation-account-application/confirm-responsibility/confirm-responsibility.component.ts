import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { filter, takeUntil, tap } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';

import { GovukValidators } from 'govuk-components';

import { ApplicationSectionType, ResponsibilitySection } from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';
import { ResponsibilityOption } from './responsibility';

@Component({
  selector: 'app-confirm-responsibility',
  templateUrl: './confirm-responsibility.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class ConfirmResponsibilityComponent implements OnInit {
  form: FormGroup = this.fb.group({
    responsibility: [
      [],
      GovukValidators.builder('You must select all options', (control: AbstractControl) =>
        control.value.length !== 3 ? { notAllChecked: true } : null,
      ),
    ],
  });
  ResponsibilityOption = ResponsibilityOption;

  constructor(
    private readonly fb: FormBuilder,
    private readonly store: InstallationAccountApplicationStore,
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.store
      .getTask<ResponsibilitySection>(ApplicationSectionType.responsibility)
      .pipe(
        filter((task) => !!task.value),
        tap((task) => this.form.patchValue(task.value)),
        takeUntil(this.destroy$),
      )
      .subscribe();
  }

  onSubmit(): void {
    this.store.updateTask(ApplicationSectionType.responsibility, this.form.value, 'complete');
    this.store.nextStep('../', this.route);
  }
}

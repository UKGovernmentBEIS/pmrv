import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import {
  installationActivities,
  InstallationActivity,
  subCategoryInstallationActivityMap,
} from '@tasks/aer/submit/nace-codes/nace-code-types';
import { naceCodeInstallationActivityFormProvider } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-installation-activity/nace-code-installation-activity-form.provider';

@Component({
  selector: 'app-nace-code-installation-activity',
  templateUrl: './nace-code-installation-activity.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [naceCodeInstallationActivityFormProvider],
})
export class NaceCodeInstallationActivityComponent implements OnInit {
  installationActivityOptions: [string, string | any][];
  readonly subCategory = this.router.getCurrentNavigation()?.extras.state?.subCategory;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: FormGroup,
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly aerService: AerService,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    const relevantInstallationActivities = Object.entries(subCategoryInstallationActivityMap).find(
      (entry) => entry[0] === this.subCategory,
    )[1];
    this.installationActivityOptions = Object.entries(installationActivities).filter((entry) =>
      relevantInstallationActivities.includes(entry[0] as InstallationActivity),
    );
    this.enableOptionalFields();
  }

  onSubmit(): void {
    const activity = this.form.value.installationActivityChild ?? this.form.value.installationActivity;
    this.aerService
      .getTask('naceCodes')
      .pipe(
        first(),
        switchMap((naceCodes) =>
          this.aerService.postTaskSave(
            {
              naceCodes: {
                codes: (naceCodes?.codes ?? []).concat(activity),
              },
            },
            undefined,
            false,
            'naceCodes',
          ),
        ),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }

  // same solution as DecisionComponent. The bug is probably in the conditional component and we should fix it there.
  private enableOptionalFields() {
    this.form
      .get('installationActivity')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        if (this.form.get('installationActivity')) {
          this.form.get('installationActivityChild').setValue(null);
          this.form.get('installationActivityChild').enable();
        }
      });
  }
}

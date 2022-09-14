import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { emissionFactorFormProvider } from './emission-factor-form.provider';

@Component({
  selector: 'app-emission-factor',
  template: `
    <app-permit-task>
      <app-wizard-step
        (formSubmit)="onContinue()"
        [formGroup]="form"
        submitText="Continue"
        caption="Perfluorocarbons"
        heading="Is a tier 2 emission factor being applied to any of your Perfluorocarbon source streams?"
        [hideSubmit]="(store.isEditable$ | async) === false"
      >
        <app-boolean-radio-group controlName="exist"></app-boolean-radio-group>
      </app-wizard-step>
      <app-approach-return-link parentTitle="Perfluorocarbons" reviewGroupUrl="pfc"></app-approach-return-link>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [emissionFactorFormProvider],
})
export class EmissionFactorComponent implements PendingRequest {
  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['determination-installation'], { relativeTo: this.route });
    } else {
      this.route.data
        .pipe(
          first(),
          switchMap((data) =>
            this.store
              .patchTask(
                data.taskKey,
                this.form.value.exist
                  ? this.form.value
                  : { exist: this.form.value.exist, determinationInstallation: null, scheduleMeasurements: null },
                false,
                data.statusKey,
              )
              .pipe(this.pendingRequest.trackRequest()),
          ),
        )
        .subscribe(() => this.router.navigate(['determination-installation'], { relativeTo: this.route }));
    }
  }
}

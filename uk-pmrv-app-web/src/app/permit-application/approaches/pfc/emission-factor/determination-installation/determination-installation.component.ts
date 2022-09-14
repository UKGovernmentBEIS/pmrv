import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';

import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { determinationInstallationFormProvider } from './determination-installation-form.provider';

@Component({
  selector: 'app-determination-installation',
  templateUrl: './determination-installation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [determinationInstallationFormProvider],
})
export class DeterminationInstallationComponent implements PendingRequest {
  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../schedule-measurements'], { relativeTo: this.route });
    } else {
      this.store
        .patchTask(
          this.route.snapshot.data.taskKey,
          {
            exist: true,
            determinationInstallation: this.form.value,
          },
          false,
          this.route.snapshot.data.statusKey,
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['../schedule-measurements'], { relativeTo: this.route }));
    }
  }
}

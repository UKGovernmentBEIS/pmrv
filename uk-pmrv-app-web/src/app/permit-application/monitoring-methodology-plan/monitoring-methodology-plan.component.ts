import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { PendingRequest } from '../../core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationStore } from '../store/permit-application.store';
import { monitoringMethodologyPlanAddFormFactory } from './monitoring-methodology-plan-form.provider';

@Component({
  selector: 'app-monitoring-methodology-plan',
  templateUrl: './monitoring-methodology-plan.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [monitoringMethodologyPlanAddFormFactory],
})
export class MonitoringMethodologyPlanComponent implements PendingRequest {
  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['upload-file'], { relativeTo: this.route });
    } else {
      this.route.data
        .pipe(
          first(),
          switchMap((data) =>
            this.store
              .patchTask(
                data.permitTask,
                this.form.value.exist ? this.form.value : { exist: this.form.value.exist, plans: null },
                false,
              )
              .pipe(this.pendingRequest.trackRequest()),
          ),
        )
        .subscribe(() => this.router.navigate(['upload-file'], { relativeTo: this.route }));
    }
  }
}

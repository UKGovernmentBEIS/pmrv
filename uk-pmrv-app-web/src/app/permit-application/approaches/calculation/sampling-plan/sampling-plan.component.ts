import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { SAMPLING_PLAN_FORM, samplingPlanFormProvider } from './sampling-plan-form.provider';

@Component({
  selector: 'app-sampling-plan',
  templateUrl: './sampling-plan.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [samplingPlanFormProvider],
})
export class SamplingPlanComponent implements PendingRequest {
  constructor(
    @Inject(SAMPLING_PLAN_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['plan'], { relativeTo: this.route });
    } else {
      this.route.data
        .pipe(
          switchMap((data) =>
            this.store.findTask(data.taskKey).pipe(
              first(),
              switchMap((samplingPlan) =>
                this.store.postTask(
                  data.taskKey,
                  {
                    exist: this.form.value.exist,
                    details: this.form.value.exist
                      ? {
                          ...samplingPlan?.details,
                          analysis: {
                            ...this.form.value.analysis,
                          },
                        }
                      : undefined,
                  },
                  false,
                  data.statusKey,
                ),
              ),
              this.pendingRequest.trackRequest(),
            ),
          ),
        )
        .subscribe(() => this.router.navigate(['plan'], { relativeTo: this.route }));
    }
  }
}

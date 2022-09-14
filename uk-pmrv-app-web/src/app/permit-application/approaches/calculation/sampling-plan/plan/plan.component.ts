import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, startWith, switchMap, switchMapTo, tap } from 'rxjs';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../core/interfaces/pending-request.interface';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { PLAN_FORM, planFormProvider } from './plan-form.provider';

@Component({
  selector: 'app-plan',
  templateUrl: './plan.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [planFormProvider],
})
export class PlanComponent implements PendingRequest {
  isFileUploaded$ = this.form.get('planIds').valueChanges.pipe(
    startWith(this.form.get('planIds').value),
    map((value) => value?.length > 0),
  );

  constructor(
    @Inject(PLAN_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  getDownloadUrl() {
    return this.store.createBaseFileDownloadUrl();
  }

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../appropriateness'], { relativeTo: this.route });
    } else {
      this.route.data
        .pipe(
          switchMap((data) =>
            this.store.findTask(data.taskKey).pipe(
              first(),
              switchMap((samplingPlan) =>
                this.store.patchTask(
                  data.taskKey,
                  {
                    ...samplingPlan?.details,
                    procedurePlan: {
                      ...this.form.value.procedurePlan,
                      procedurePlanIds: this.form.value.planIds?.map((file) => file.uuid),
                    },
                  },
                  false,
                  data.statusKey,
                ),
              ),
              switchMapTo(this.store),
              first(),
              tap((state) =>
                this.store.setState({
                  ...state,
                  permitAttachments: {
                    ...state.permitAttachments,
                    ...this.form.value.planIds?.reduce(
                      (result, item) => ({ ...result, [item.uuid]: item.file.name }),
                      {},
                    ),
                  },
                }),
              ),
              this.pendingRequest.trackRequest(),
            ),
          ),
        )
        .subscribe(() => this.router.navigate(['../appropriateness'], { relativeTo: this.route }));
    }
  }
}

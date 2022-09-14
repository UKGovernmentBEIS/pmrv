import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import {
  PERMIT_REVOCATION_TASK_FORM,
  permitRevocationFormProvider,
} from '@permit-revocation/factory/permit-revocation-form-provider';
import { PermitRevocationState } from '@permit-revocation/store/permit-revocation.state';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import moment from 'moment';

@Component({
  selector: 'app-stop-date',
  templateUrl: './stop-date.component.html',
  providers: [permitRevocationFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class StopDateComponent {
  maxDate: Date;

  constructor(
    @Inject(PERMIT_REVOCATION_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitRevocationStore,
    readonly router: Router,
    readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {
    const setHours = moment().subtract(1, 'days').set({ hour: 23, minute: 59, second: 59, millisecond: 999 });
    this.maxDate = new Date(setHours.toISOString());
  }

  onContinue() {
    const navigate = () => this.router.navigate(['..', 'notice'], { relativeTo: this.route });
    if (!this.form.dirty) {
      navigate();
    } else {
      const activitiesStopped = this.form?.value.activitiesStopped;
      const stoppedDate = activitiesStopped ? this.form.value.stoppedDate : null;

      combineLatest([this.route.data, this.store])
        .pipe(
          first(),
          switchMap(([data, state]) => {
            const permitRevocation: PermitRevocationState = {
              ...state,
              permitRevocation: {
                ...state.permitRevocation,
                activitiesStopped: activitiesStopped,
                stoppedDate: stoppedDate,
              },
              sectionsCompleted: {
                [data.statusKey]: false,
              },
            };
            return this.store.postApplyPermitRevocation(permitRevocation);
          }),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => navigate());
    }
  }
}

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

@Component({
  selector: 'app-reason',
  templateUrl: './reason.component.html',
  providers: [permitRevocationFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReasonComponent {
  constructor(
    @Inject(PERMIT_REVOCATION_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitRevocationStore,
    private readonly router: Router,
    readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onContinue() {
    const navigate = () => this.router.navigate(['..', 'stop-date'], { relativeTo: this.route });
    if (!this.form.dirty) {
      navigate();
    } else {
      const reason = this.form.value.reason;

      combineLatest([this.route.data, this.store])
        .pipe(
          first(),
          switchMap(([data, state]) => {
            const permitRevocationState: PermitRevocationState = {
              ...state,
              permitRevocation: {
                ...state.permitRevocation,
                reason,
              },
              sectionsCompleted: {
                [data.statusKey]: false,
              },
            };
            return this.store.postApplyPermitRevocation(permitRevocationState);
          }),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => navigate());
    }
  }
}

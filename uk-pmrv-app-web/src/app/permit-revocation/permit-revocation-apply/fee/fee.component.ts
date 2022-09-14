import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, pluck, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import {
  PERMIT_REVOCATION_TASK_FORM,
  permitRevocationFormProvider,
} from '@permit-revocation/factory/permit-revocation-form-provider';
import { PermitRevocationState } from '@permit-revocation/store/permit-revocation.state';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';

@Component({
  selector: 'app-fee',
  templateUrl: './fee.component.html',
  providers: [permitRevocationFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FeeComponent {
  feeAmount$ = this.store.pipe(pluck('feeAmount'));

  constructor(
    @Inject(PERMIT_REVOCATION_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitRevocationStore,
    private readonly router: Router,
    readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
  ) {}

  onContinue() {
    const navigate = () => this.router.navigate(['..', 'answers'], { relativeTo: this.route });
    if (!this.form.dirty) {
      navigate();
    } else {
      const feeCharged = this.form.value.feeCharged;
      const feeDate = feeCharged ? this.form.value.feeDate : null;
      const feeDetails = feeCharged ? this.form.value.feeDetails : null;

      combineLatest([this.route.data, this.store])
        .pipe(
          first(),
          switchMap(([data, state]) => {
            const permitRevocation: PermitRevocationState = {
              ...state,
              permitRevocation: {
                ...state.permitRevocation,
                feeCharged,
                feeDate,
                feeDetails,
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

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
  selector: 'app-surrender-allowances',
  templateUrl: './surrender-allowances.component.html',
  providers: [permitRevocationFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SurrenderAllowancesComponent {
  minDate: Date;
  constructor(
    @Inject(PERMIT_REVOCATION_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitRevocationStore,
    private readonly router: Router,
    readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
  ) {
    const setHours = moment().set({ hour: 0, minute: 0, second: 0, millisecond: 0 });
    this.minDate = new Date(setHours.toISOString());
  }

  onContinue() {
    const navigate = () => this.router.navigate(['..', 'fee'], { relativeTo: this.route });
    if (!this.form.dirty) {
      navigate();
    } else {
      const surrenderRequired = this.form.value.surrenderRequired;
      const surrenderDate = surrenderRequired ? this.form.value.surrenderDate : null;

      combineLatest([this.route.data, this.store])
        .pipe(
          first(),
          switchMap(([data, state]) => {
            const permitRevocation: PermitRevocationState = {
              ...state,
              permitRevocation: {
                ...state.permitRevocation,
                surrenderRequired,
                surrenderDate,
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

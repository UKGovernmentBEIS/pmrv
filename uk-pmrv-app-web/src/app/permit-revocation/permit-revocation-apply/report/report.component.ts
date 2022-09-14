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
  selector: 'app-report',
  templateUrl: './report.component.html',
  providers: [permitRevocationFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReportComponent {
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
    const navigate = () => this.router.navigate(['..', 'surrender-allowances'], { relativeTo: this.route });
    if (!this.form.dirty) {
      navigate();
    } else {
      const annualEmissionsReportRequired = this.form.value.annualEmissionsReportRequired;
      const annualEmissionsReportDate = annualEmissionsReportRequired
        ? this.form.value.annualEmissionsReportDate
        : null;
      combineLatest([this.route.data, this.store])
        .pipe(
          first(),
          switchMap(([data, state]) => {
            const permitRevocation: PermitRevocationState = {
              ...state,
              permitRevocation: {
                ...state.permitRevocation,
                annualEmissionsReportRequired,
                annualEmissionsReportDate,
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

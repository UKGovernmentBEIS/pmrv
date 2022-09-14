import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import moment from 'moment';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../core/interfaces/pending-request.interface';
import { PERMIT_SURRENDER_TASK_FORM } from '../../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { reportFormProvider } from './report-form.provider';

@Component({
  selector: 'app-report',
  templateUrl: './report.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [reportFormProvider],
})
export class ReportComponent implements PendingRequest {
  today = moment().startOf('day').toDate();

  constructor(
    @Inject(PERMIT_SURRENDER_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitSurrenderStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../allowances'], { relativeTo: this.route });
    } else {
      const reportRequired: boolean = this.form.value.reportRequired;
      this.store
        .pipe(
          first(),
          switchMap((state) =>
            this.store.postReviewDetermination(
              {
                ...state.reviewDetermination,
                reportRequired,
                reportDate: reportRequired ? this.form.value.reportDate : null,
              },
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['../allowances'], { relativeTo: this.route }));
    }
  }
}

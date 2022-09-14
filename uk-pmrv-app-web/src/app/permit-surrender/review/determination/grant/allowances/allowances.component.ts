import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import moment from 'moment';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../core/interfaces/pending-request.interface';
import { PERMIT_SURRENDER_TASK_FORM } from '../../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { allowancesFormProvider } from './allowances-form.provider';

@Component({
  selector: 'app-allowances',
  templateUrl: './allowances.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [allowancesFormProvider],
})
export class AllowancesComponent implements PendingRequest {
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
      this.router.navigate(['../answers'], { relativeTo: this.route });
    } else {
      const allowancesSurrenderRequired: boolean = this.form.value.allowancesSurrenderRequired;
      this.store
        .pipe(
          first(),
          switchMap((state) =>
            this.store.postReviewDetermination(
              {
                ...state.reviewDetermination,
                allowancesSurrenderRequired,
                allowancesSurrenderDate: allowancesSurrenderRequired ? this.form.value.allowancesSurrenderDate : null,
              },
              false,
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['../answers'], { relativeTo: this.route }));
    }
  }
}

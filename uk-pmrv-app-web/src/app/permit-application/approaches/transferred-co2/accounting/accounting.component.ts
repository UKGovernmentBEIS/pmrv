import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { TRANSFERRED_CO2_AccountingStatus } from '../transferred-co2-status';
import { accountingFormProvider } from './accounting-form.provider';

@Component({
  selector: 'app-accounting',
  templateUrl: './accounting.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [accountingFormProvider],
})
export class AccountingComponent implements PendingRequest {
  cannotStart$ = this.store.pipe(map((state) => TRANSFERRED_CO2_AccountingStatus(state) === 'cannot start yet'));

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['details'], { relativeTo: this.route });
    } else {
      this.route.data
        .pipe(
          first(),
          switchMap((data) =>
            this.store
              .patchTask(
                data.taskKey,
                this.form.value.chemicallyBound
                  ? { chemicallyBound: this.form.value.chemicallyBound, accountingEmissionsDetails: null }
                  : this.form.value,
                false,
                data.statusKey,
              )
              .pipe(this.pendingRequest.trackRequest()),
          ),
        )
        .subscribe(() => this.router.navigate(['details'], { relativeTo: this.route }));
    }
  }
}

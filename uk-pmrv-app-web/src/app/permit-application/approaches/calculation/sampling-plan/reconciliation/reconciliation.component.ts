import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { switchMap } from 'rxjs';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../../core/interfaces/pending-request.interface';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { RECONCILIATION_FORM, reconciliationFormProvider } from './reconciliation-form.provider';

@Component({
  selector: 'app-reconciliation',
  templateUrl: './reconciliation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [reconciliationFormProvider],
})
export class ReconciliationComponent implements PendingRequest {
  constructor(
    @Inject(RECONCILIATION_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../answers'], { relativeTo: this.route });
    } else {
      this.route.data
        .pipe(
          switchMap((data) =>
            this.store
              .postTask(data.taskKey, this.form.value, false, data.statusKey)
              .pipe(this.pendingRequest.trackRequest()),
          ),
        )
        .subscribe(() => this.router.navigate(['../answers'], { relativeTo: this.route }));
    }
  }
}

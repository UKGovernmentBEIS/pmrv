import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';

import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { APPROPRIATENESS_FORM, appropriatenessFormProvider } from './appropriateness-form.provider';

@Component({
  selector: 'app-appropriateness',
  templateUrl: './appropriateness.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [appropriatenessFormProvider],
})
export class AppropriatenessComponent implements PendingRequest {
  constructor(
    @Inject(APPROPRIATENESS_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../reconciliation'], { relativeTo: this.route });
    } else {
      this.route.data
        .pipe(
          switchMap((data) =>
            this.store
              .patchTask(data.taskKey, { appropriateness: this.form.value }, false, data.statusKey)
              .pipe(this.pendingRequest.trackRequest()),
          ),
        )
        .subscribe(() => this.router.navigate(['../reconciliation'], { relativeTo: this.route }));
    }
  }
}

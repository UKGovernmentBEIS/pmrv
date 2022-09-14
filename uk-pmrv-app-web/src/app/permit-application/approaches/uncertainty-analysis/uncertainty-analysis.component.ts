import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { PERMIT_TASK_FORM } from '../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { uncertaintyAnalysisFormProvider } from './uncertainty-analysis-form.provider';

@Component({
  selector: 'app-uncertainty-analysis',
  templateUrl: './uncertainty-analysis.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [uncertaintyAnalysisFormProvider],
})
export class UncertaintyAnalysisComponent {
  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['upload-file'], { relativeTo: this.route });
    } else {
      this.route.data
        .pipe(
          first(),
          switchMap((data) =>
            this.store
              .patchTask(
                data.permitTask,
                this.form.value.exist ? this.form.value : { exist: this.form.value.exist, attachments: null },
                false,
              )
              .pipe(this.pendingRequest.trackRequest()),
          ),
        )
        .subscribe(() => this.router.navigate(['upload-file'], { relativeTo: this.route }));
    }
  }
}

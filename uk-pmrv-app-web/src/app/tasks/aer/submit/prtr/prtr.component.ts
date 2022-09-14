import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { prtrFormProvider } from '@tasks/aer/submit/prtr/prtr-form.provider';

@Component({
  selector: 'app-prtr',
  templateUrl: './prtr.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [prtrFormProvider],
})
export class PrtrComponent {
  isEditable$ = this.aerService.isEditable$;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: FormGroup,
    readonly pendingRequest: PendingRequestService,
    readonly aerService: AerService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty || this.form.get('exist').value) {
      this.form.get('exist').value
        ? this.router.navigate(['activity', 0], { relativeTo: this.route })
        : this.router.navigate(['summary'], { relativeTo: this.route });
    } else {
      this.aerService
        .postTaskSave(
          {
            pollutantRegisterActivities: {
              exist: false,
              activities: [],
            },
          },
          undefined,
          false,
          'pollutantRegisterActivities',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['summary'], { relativeTo: this.route, state: { notification: true } }));
    }
  }
}

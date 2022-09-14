import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationStore } from '../store/permit-application.store';
import { permitTypeFormProvider } from './permit-type-form.provider';

@Component({
  selector: 'app-permit-type',
  templateUrl: './permit-type.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [permitTypeFormProvider],
})
export class PermitTypeComponent {
  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    combineLatest([this.store, this.route.data])
      .pipe(
        first(),
        switchMap(([state, data]) =>
          this.store.postCategoryTask(data.permitTask, {
            ...state,
            permitType: this.form.get('type').value,
          }),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['summary'], { relativeTo: this.route }));
  }
}

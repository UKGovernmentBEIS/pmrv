import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap, switchMapTo } from 'rxjs';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { uncertaintyFormProvider } from './uncertainty-form.provider';

@Component({
  selector: 'app-uncertainty',
  templateUrl: './uncertainty.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [uncertaintyFormProvider],
})
export class UncertaintyComponent {
  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit(): void {
    this.route.data
      .pipe(
        first(),
        switchMap((data) => this.store.postTask(data.taskKey, this.form.value, true, data.statusKey)),
        this.pendingRequest.trackRequest(),
        switchMapTo(this.store),
        first(),
      )
      .subscribe((state) =>
        this.router.navigate(
          [state.requestTaskType === 'PERMIT_ISSUANCE_APPLICATION_REVIEW' ? '../../review/fall-back' : 'summary'],
          { relativeTo: this.route, state: { notification: true } },
        ),
      );
  }
}

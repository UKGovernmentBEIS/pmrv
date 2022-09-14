import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap, withLatestFrom } from 'rxjs';

import { ReceivingTransferringInstallation } from 'pmrv-api';

import { PendingRequestService } from '../../../../../core/guards/pending-request.service';
import { PERMIT_TASK_FORM } from '../../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { detailsFormProvider, typeOptions } from './details-form.provider';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [detailsFormProvider],
})
export class DetailsComponent {
  readonly typeOptions = typeOptions;

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
  ) {}

  onSubmit(): void {
    this.route.data
      .pipe(
        first(),
        switchMap((data) => this.store.findTask<ReceivingTransferringInstallation[]>(data.taskKey)),
        first(),
        withLatestFrom(this.route.paramMap, this.route.data),
        switchMap(([installations, paramMap, data]) =>
          this.store.postTask(
            data.taskKey,
            paramMap.get('index')
              ? installations.map((item, index) => (Number(paramMap.get('index')) === index ? this.form.value : item))
              : [...(installations ?? []), this.form.value],
            false,
            data.statusKey,
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route, state: { notification: true } }));
  }
}

import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { PendingRequest } from '../../core/interfaces/pending-request.interface';
import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationStore } from '../store/permit-application.store';
import { installationDescriptionFormProvider } from './installation-description-form.provider';

@Component({
  selector: 'app-installation-description',
  templateUrl: './installation-description.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [installationDescriptionFormProvider],
})
export class InstallationDescriptionComponent implements PendingRequest {
  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    this.store
      .postTask('installationDescription', this.form.value, true)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.store.navigate(this.route, 'summary', 'details'));
  }
}

import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationStore } from '../store/permit-application.store';
import { environmentalSystemFormProvider } from './environmental-system-form.provider';

@Component({
  selector: 'app-environmental-system',
  templateUrl: './environmental-system.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [environmentalSystemFormProvider],
})
export class EnvironmentalSystemComponent {
  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    private readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    this.store
      .postTask('environmentalManagementSystem', this.form.value, true)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.store.navigate(this.route, 'summary', 'management-procedures'));
  }
}

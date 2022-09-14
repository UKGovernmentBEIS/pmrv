import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';

import { confidentialityStatementFormProvider } from './confidentiality-statement-form.provider';

@Component({
  selector: 'app-confidentiality-statement',
  template: `
    <app-aer-task [breadcrumb]="true">
      <app-confidentiality-statement-template
        (formSubmit)="onSubmit()"
        [form]="form"
        [isEditable]="aerService.isEditable$ | async"
      >
      </app-confidentiality-statement-template>
      <app-return-link></app-return-link>
    </app-aer-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [confidentialityStatementFormProvider, DestroySubject],
})
export class ConfidentialityStatementComponent {
  constructor(
    @Inject(AER_TASK_FORM) readonly form: FormGroup,
    readonly aerService: AerService,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    this.aerService
      .postTaskSave(
        {
          confidentialityStatement: this.form.value,
        },
        undefined,
        true,
        'confidentialityStatement',
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.router.navigate(['summary'], { relativeTo: this.route, state: { notification: true } }));
  }
}

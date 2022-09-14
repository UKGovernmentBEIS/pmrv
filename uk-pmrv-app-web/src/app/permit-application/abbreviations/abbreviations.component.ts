import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationStore } from '../store/permit-application.store';
import { abbreviationsFormProvider } from './abbreviations-form.provider';

@Component({
  selector: 'app-abbreviations',
  template: `
    <app-permit-task>
      <app-abbreviations-template
        (formSubmit)="onSubmit()"
        [form]="form"
        [isEditable]="store.isEditable$ | async"
        caption="Additional information"
      ></app-abbreviations-template>
      <app-list-return-link
        reviewGroupTitle="Additional information"
        reviewGroupUrl="additional-info"
      ></app-list-return-link>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [abbreviationsFormProvider],
})
export class AbbreviationsComponent {
  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    this.store
      .postTask('abbreviations', this.form.value, true)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.store.navigate(this.route, 'summary', 'additional-info'));
  }
}

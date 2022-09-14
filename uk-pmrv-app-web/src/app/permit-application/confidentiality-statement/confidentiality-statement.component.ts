import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  Inject,
  QueryList,
  ViewChild,
  ViewChildren,
} from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { DestroySubject } from '../../core/services/destroy-subject.service';
import { WizardStepComponent } from '../../shared/wizard/wizard-step.component';
import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationStore } from '../store/permit-application.store';
import { confidentialityStatementFormProvider } from './confidentiality-statement-form.provider';

@Component({
  selector: 'app-confidentiality-statement',
  template: `
    <app-permit-task>
      <app-confidentiality-statement-template
        (formSubmit)="onSubmit()"
        [form]="form"
        [isEditable]="store.isEditable$ | async"
        [caption]="'Confidentiality statement'"
      >
      </app-confidentiality-statement-template>
      <app-list-return-link reviewGroupTitle="Confidentiality" reviewGroupUrl="confidentiality"></app-list-return-link>
    </app-permit-task>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [confidentialityStatementFormProvider, DestroySubject],
})
export class ConfidentialityStatementComponent {
  @ViewChild(WizardStepComponent, { read: ElementRef, static: true }) wizardStep: ElementRef<HTMLElement>;
  @ViewChild(WizardStepComponent) wizardStepComponent: WizardStepComponent;
  @ViewChildren('removeButton') removeButtons: QueryList<ElementRef<HTMLButtonElement>>;

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    this.store
      .postTask('confidentialityStatement', this.form.value, true)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.store.navigate(this.route, 'summary', 'confidentiality'));
  }
}

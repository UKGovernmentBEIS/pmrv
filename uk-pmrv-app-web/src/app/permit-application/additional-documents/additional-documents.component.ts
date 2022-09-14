import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationStore } from '../store/permit-application.store';
import { additionalDocumentsFormFactory } from './additional-documents-form.provider';

@Component({
  selector: 'app-additional-documents',
  template: `
    <app-additional-documents-shared
      [form]="form"
      [isEditable]="store.isEditable$ | async"
      (formSubmit)="onSubmit()"
      [downloadUrl]="getDownloadUrl()"
    ></app-additional-documents-shared>
    <app-list-return-link
      reviewGroupTitle="Additional information"
      reviewGroupUrl="additional-info"
    ></app-list-return-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [additionalDocumentsFormFactory],
})
export class AdditionalDocumentsComponent {
  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    this.store
      .postTask(
        'additionalDocuments',
        {
          ...this.form.value,
          documents: this.form.value.documents?.map((file) => file.uuid),
        },
        true,
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.store.setState({
          ...this.store.getState(),
          permitAttachments: {
            ...this.store.getState().permitAttachments,
            ...this.form.value.documents?.reduce((result, item) => ({ ...result, [item.uuid]: item.file.name }), {}),
          },
        });
        this.store.navigate(this.route, 'summary', 'additional-info');
      });
  }

  getDownloadUrl() {
    return this.store.createBaseFileDownloadUrl();
  }
}

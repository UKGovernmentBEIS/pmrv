import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AerService } from '@tasks/aer/core/aer.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';

import { additionalDocumentsFormFactory } from './additional-documents-form.provider';

@Component({
  selector: 'app-additional-documents',
  template: `
    <app-additional-documents-shared
      [form]="form"
      [isEditable]="aerService.isEditable$ | async"
      (formSubmit)="onSubmit()"
      [downloadUrl]="getDownloadUrl()"
    ></app-additional-documents-shared>
    <app-return-link></app-return-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [additionalDocumentsFormFactory],
})
export class AdditionalDocumentsComponent {
  constructor(
    @Inject(AER_TASK_FORM) readonly form: FormGroup,
    readonly aerService: AerService,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    if (!this.form.dirty) {
      this.router.navigate(['summary'], { relativeTo: this.route });
    } else {
      this.aerService
        .postTaskSave(
          {
            additionalDocuments: {
              ...this.form.value,
              documents: this.form.value.documents?.map((file) => file.uuid),
            },
          },
          this.form.value?.documents
            ?.map((document) => {
              return {
                [document.uuid]: document.file.name,
              };
            })
            .reduce((prev, cur) => ({ ...prev, ...cur }), {}),
          true,
          'additionalDocuments',
        )
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => this.router.navigate(['summary'], { relativeTo: this.route, state: { notification: true } }));
    }
  }

  getDownloadUrl() {
    return this.aerService.createBaseFileDownloadUrl();
  }
}

import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, startWith, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../core/interfaces/pending-request.interface';
import { PERMIT_SURRENDER_TASK_FORM } from '../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';
import { uploadDocumentsFormProvider } from './upload-documents-form.provider';

@Component({
  selector: 'app-upload-documents',
  templateUrl: './upload-documents.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [uploadDocumentsFormProvider],
})
export class UploadDocumentsComponent implements PendingRequest {
  constructor(
    @Inject(PERMIT_SURRENDER_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitSurrenderStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  readonly isFileUploaded$: Observable<boolean> = this.form.get('documents').valueChanges.pipe(
    startWith(this.form.get('documents').value),
    map((value) => value?.length > 0),
  );

  getDownloadUrl() {
    return this.store.createBaseFileDownloadUrl();
  }

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../answers'], { relativeTo: this.route });
    } else {
      combineLatest([this.route.data, this.store])
        .pipe(
          first(),
          switchMap(([data, state]) =>
            this.store.postApplyPermitSurrender({
              ...state,
              permitSurrender: {
                ...state.permitSurrender,
                documents: this.form.value.documents?.map((file) => file.uuid),
              },
              permitSurrenderAttachments: {
                ...state.permitSurrenderAttachments,
                ...this.form.value.documents?.reduce(
                  (result, item) => ({ ...result, [item.uuid]: item.file.name }),
                  {},
                ),
              },
              sectionsCompleted: {
                ...state.sectionsCompleted,
                [data.statusKey]: false,
              },
            }),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['../answers'], { relativeTo: this.route }));
    }
  }
}

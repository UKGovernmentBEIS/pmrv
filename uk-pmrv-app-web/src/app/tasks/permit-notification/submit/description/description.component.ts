import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, startWith, switchMap } from 'rxjs';

import {
  NonSignificantChange,
  OtherFactor,
  PermitNotification,
  TemporaryChange,
  TemporaryFactor,
  TemporarySuspension,
} from 'pmrv-api';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { PermitNotificationService } from '../../core/permit-notification.service';
import { PERMIT_NOTIFICATION_TASK_FORM } from '../../core/permit-notification-task-form.token';
import { descriptionFormProvider } from './description-form.provider';

@Component({
  selector: 'app-description',
  templateUrl: './description.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [descriptionFormProvider],
})
export class DescriptionComponent implements PendingRequest {
  readonly isFileUploaded$ = this.form
    .get('notification')
    .get('documents')
    .valueChanges.pipe(
      startWith(this.form.get('notification').get('documents').value),
      map((value) => value?.length > 0),
    );
  today = new Date();

  constructor(
    @Inject(PERMIT_NOTIFICATION_TASK_FORM) readonly form: FormGroup,
    readonly permitNotificationService: PermitNotificationService,
    readonly store: CommonTasksStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  getDownloadUrl() {
    return this.permitNotificationService.createBaseFileDownloadUrl();
  }

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../answers'], { relativeTo: this.route });
    } else {
      this.permitNotificationService
        .getPayload()
        .pipe(
          first(),
          switchMap((payload) =>
            this.permitNotificationService.postTaskSave(
              {
                permitNotification: {
                  ...this.getFormData(payload.permitNotification.type),
                  type: payload.permitNotification.type,
                },
              },
              this.notificationDocumentsField.value?.reduce(
                (result, item) => ({ ...result, [item.uuid]: item.file.name }),
                {},
              ),
              false,
              'DETAILS_CHANGE',
            ),
          ),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['../answers'], { relativeTo: this.route }));
    }
  }

  get notificationField() {
    return this.form.get('notification');
  }

  get notificationDocumentsField() {
    return this.notificationField.get('documents');
  }

  private getFormData(
    type: PermitNotification['type'],
  ): TemporaryFactor | TemporaryChange | TemporarySuspension | NonSignificantChange | OtherFactor {
    const notificationFieldValue = {
      ...this.notificationField.value,
      documents: this.notificationDocumentsField.value?.map((item) => item.uuid) ?? null,
    };
    switch (type) {
      case 'NON_SIGNIFICANT_CHANGE':
        return { ...notificationFieldValue };
      case 'OTHER_FACTOR':
        return {
          ...notificationFieldValue,
          startDateOfNonCompliance:
            notificationFieldValue.reportingType === 'RENOUNCE_FREE_ALLOCATIONS'
              ? null
              : this.notificationField.get('startDateOfNonCompliance_' + notificationFieldValue.reportingType).value,
          endDateOfNonCompliance:
            notificationFieldValue.reportingType === 'OTHER_ISSUE'
              ? notificationFieldValue.endDateOfNonCompliance
              : null,
        };
      default:
        return notificationFieldValue;
    }
  }
}

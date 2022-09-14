import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { PermitNotificationService } from '@tasks/permit-notification/core/permit-notification.service';

import { GovukValidators } from 'govuk-components';

import {
  PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload,
  PermitNotificationFollowUpSaveApplicationAmendRequestTaskActionPayload,
} from 'pmrv-api';

import { Files, SummaryList } from '../model/model';

interface TaskPayload extends PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload {
  files?: Files[];
  changesRequired?: string;
}

@Component({
  selector: 'app-details-amends',
  templateUrl: './details-amends.component.html',
  providers: [BackLinkService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsAmendsComponent implements OnInit {
  isEditable$ = this.permitNotificationService.getIsEditable();

  form: FormGroup = this.fb.group({
    changes: [null, GovukValidators.required('Check the box to confirm you have made changes')],
  });

  data$: Observable<
    Pick<TaskPayload, 'followUpResponseExpirationDate' | 'followUpSectionsCompleted' | 'files' | 'changesRequired'>
  > = combineLatest([
    this.permitNotificationService.getPayload(),
    this.permitNotificationService.downloadUrlFiles$,
  ]).pipe(
    map(([payload, files]) => {
      const taskPayload = payload as PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload;
      return {
        files,
        followUpResponseExpirationDate: taskPayload.followUpResponseExpirationDate,
        followUpSectionsCompleted: taskPayload.followUpSectionsCompleted,
        changesRequired: taskPayload.reviewDecision.changesRequired,
      };
    }),
  );

  displayErrorSummary$ = new BehaviorSubject<boolean>(false);

  summaryListMapper: Record<
    keyof {
      changesRequired: string;
      files: string;
      followUpResponseExpirationDate: string;
      followUpSectionsCompleted: string;
    },
    SummaryList
  > = {
    changesRequired: { label: 'Regulators comments', order: 1, type: 'string' },
    files: {
      label: 'Uploaded files',
      order: 2,
      type: 'files',
      isArray: true,
    },
    followUpResponseExpirationDate: { label: 'Changes due by', order: 3, type: 'date' },
    followUpSectionsCompleted: {
      label: 'I have made changes and want to mark this task as complete',
      order: 4,
      type: 'boolean',
      statusKey: 'AMENDS_NEEDED',
    },
  };
  constructor(
    private readonly permitNotificationService: PermitNotificationService,
    readonly route: ActivatedRoute,
    private readonly fb: FormBuilder,
    private readonly backLinkService: BackLinkService,
    private readonly pendingRequest: PendingRequestService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  submit() {
    if (this.form.valid) {
      const sectionsCompleted = this.form.get('changes').value;

      this.permitNotificationService
        .getPayload()
        .pipe(
          first(),
          switchMap((payload) => {
            const taskPayload = payload as PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload;
            const attachments = taskPayload.followUpAttachments;
            const taskActionPayload: PermitNotificationFollowUpSaveApplicationAmendRequestTaskActionPayload = {
              response: taskPayload.followUpResponse,
              files: taskPayload.followUpFiles?.length > 0 ? taskPayload.followUpFiles : [],
              followUpSectionsCompleted: { ['AMENDS_NEEDED']: sectionsCompleted[0] },
              reviewSectionsCompleted: taskPayload.reviewSectionsCompleted,
            };

            return this.permitNotificationService.postSubmit(
              'PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_APPLICATION_AMEND',
              'PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_APPLICATION_AMEND_PAYLOAD',
              taskActionPayload,
              attachments,
            );
          }),
          this.pendingRequest.trackRequest(),
        )
        .subscribe();
    } else {
      this.displayErrorSummary$.next(true);
    }
  }
}

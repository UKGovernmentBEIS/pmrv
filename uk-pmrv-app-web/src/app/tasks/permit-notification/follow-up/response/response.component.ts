import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, first, map, Observable, startWith, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { PermitNotificationFollowUpRequestTaskPayload } from 'pmrv-api';

import { PermitNotificationService } from '../../core/permit-notification.service';
import { PERMIT_NOTIFICATION_FOLLOW_UP_FORM, permitNotificationFollowUpFormProvider } from '../factory/form-provider';

@Component({
  selector: 'app-response',
  templateUrl: './response.component.html',
  providers: [BackLinkService, permitNotificationFollowUpFormProvider],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResponseComponent implements OnInit {
  isErrorSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  readonly isFileUploaded$: Observable<boolean> = this.form.get('files').valueChanges.pipe(
    startWith(this.form.get('files').value),
    map((value) => value?.length > 0),
  );
  isEditable$ = this.store.select('isEditable');
  followUpRequest$: Observable<string> = this.store.pipe(
    map((state) => {
      const payload = state.requestTaskItem.requestTask.payload as PermitNotificationFollowUpRequestTaskPayload;
      return payload?.followUpRequest;
    }),
  );

  getDownloadUrl(): string {
    return this.permitNotificationService.createBaseFileDownloadUrl();
  }
  constructor(
    readonly route: ActivatedRoute,
    private readonly backlinkService: BackLinkService,
    readonly store: CommonTasksStore,
    @Inject(PERMIT_NOTIFICATION_FOLLOW_UP_FORM) readonly form: FormGroup,
    private readonly router: Router,
    private readonly pendingRequest: PendingRequestService,
    private readonly permitNotificationService: PermitNotificationService,
  ) {}

  ngOnInit(): void {
    this.backlinkService.show();
  }

  onSubmit() {
    if (this.form.valid) {
      const response = this.form.get('followUpResponse').value;
      const files: string[] = this.form.get('files').value.map((file) => file.uuid);

      const requestTaskActionPayload = { response, files };

      this.store
        .pipe(
          first(),
          switchMap((state) => {
            const payload = state.requestTaskItem.requestTask.payload as PermitNotificationFollowUpRequestTaskPayload;
            const amendsNeeded =
              (state.requestTaskItem.requestTask.type as string) ===
              'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT';

            const reviewSectionsCompleted = { reviewSectionsCompleted: { RESPONSE: false } };
            const followUpSectionsCompleted = {
              followUpSectionsCompleted: (state.requestTaskItem.requestTask.payload as any).followUpSectionsCompleted,
            };

            const attachments = {
              ...payload.followUpAttachments,
              ...this.form
                .get('files')
                .value?.reduce((result, item) => ({ ...result, [item.uuid]: item.file.name }), {}),
            };
            return this.permitNotificationService.postSubmit(
              !amendsNeeded
                ? 'PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_RESPONSE'
                : 'PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_APPLICATION_AMEND',
              !amendsNeeded
                ? 'PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_RESPONSE_PAYLOAD'
                : 'PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_APPLICATION_AMEND_PAYLOAD',
              {
                ...requestTaskActionPayload,
                ...(amendsNeeded && reviewSectionsCompleted),
                ...(amendsNeeded && followUpSectionsCompleted),
              },
              attachments,
            );
          }),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => {
          const amendsNeeded =
            (this.store.getState().requestTaskItem.requestTask.type as string) ===
            'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT';

          const reviewSectionsCompleted = { reviewSectionsCompleted: { RESPONSE: false } };

          this.store.setState({
            ...this.store.getState(),
            requestTaskItem: {
              ...this.store.getState().requestTaskItem,
              requestTask: {
                ...this.store.getState().requestTaskItem.requestTask,
                payload: {
                  ...this.store.getState().requestTaskItem.requestTask.payload,
                  ...(amendsNeeded && reviewSectionsCompleted),
                } as unknown,
              },
            },
          });

          this.router.navigate(['..', 'summary'], { relativeTo: this.route, state: { notification: true } });
        });
    } else {
      this.isErrorSummaryDisplayed$.next(true);
    }
  }
}

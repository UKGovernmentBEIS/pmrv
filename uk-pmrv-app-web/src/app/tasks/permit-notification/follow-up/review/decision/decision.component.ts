import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, first, map, Observable, startWith, takeUntil, tap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
  PermitNotificationFollowUpReviewDecision,
} from 'pmrv-api';

import { PermitNotificationService } from '../../../core/permit-notification.service';
import { SummaryList } from '../../model/model';
import { FOLLOW_UP_REVIEW_DECISION_FORM, followUpReviewDecisionFormProvider } from './decision-form.provider';

@Component({
  selector: 'app-follow-up-decision',
  templateUrl: './decision.component.html',
  providers: [followUpReviewDecisionFormProvider, DestroySubject, BreadcrumbService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FollowUpReviewDecisionComponent implements OnInit {
  isEditable$ = this.store.select('isEditable');
  isErrorSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  showSummary$ = new BehaviorSubject<boolean>(false);
  files: { downloadUrl: string; fileName: string }[];
  followUpFiles$ = new BehaviorSubject<{ downloadUrl: string; fileName: string }[]>([]);
  showNotificationBanner = false;

  readonly isFileUploaded$ = this.form.get('files').valueChanges.pipe(
    startWith(this.form.get('files').value),
    map((value) => value?.length > 0),
  );

  reviewPayload$: Observable<PermitNotificationFollowUpApplicationReviewRequestTaskPayload> =
    this.permitNotificationService.getPayload().pipe(
      first(),
      tap((res: PermitNotificationFollowUpApplicationReviewRequestTaskPayload) => {
        if (res.reviewDecision?.type && res.reviewSectionsCompleted.RESPONSE !== false) {
          this.showSummary$.next(true);
        }
        if (res.followUpFiles) {
          this.files = this.permitNotificationService.getDownloadUrlFiles(res.followUpFiles);
        }
      }),
    );

  summaryData$ = this.reviewPayload$.pipe(
    map((p: PermitNotificationFollowUpApplicationReviewRequestTaskPayload) => {
      const reviewDecision = p.reviewDecision;
      const type = reviewDecision.type === 'ACCEPTED' ? 'Accepted' : 'Operator amends needed';
      const { changesRequired, dueDate, notes } = reviewDecision;
      const files = this.permitNotificationService.getDownloadUrlFiles(reviewDecision.files);
      return { changesRequired, dueDate, type, files, notes };
    }),
  );

  followUpDecisionSummaryListMapper: Record<
    keyof { type: string; changesRequired: string; files: string[]; dueDate: string; notes: string },
    SummaryList
  > = {
    type: { label: 'Decision status', order: 1, type: 'string' },
    changesRequired: { label: 'List of all the changes required', order: 2, type: 'string' },
    files: { label: 'Supporting documents', order: 3, type: 'files', isArray: true },
    dueDate: { label: 'New due date for the response', order: 4, type: 'date' },
    notes: { label: 'Notes', order: 5, type: 'string' },
  };

  constructor(
    @Inject(FOLLOW_UP_REVIEW_DECISION_FORM) readonly form: FormGroup,
    public readonly route: ActivatedRoute,
    private readonly store: CommonTasksStore,
    readonly pendingRequest: PendingRequestService,
    public readonly permitNotificationService: PermitNotificationService,
    private readonly destroy$: DestroySubject,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}

  ngOnInit(): void {
    this.store.pipe(takeUntil(this.destroy$)).subscribe((state) =>
      this.followUpFiles$.next(
        this.form.get('files').value?.map((file: { uuid: string; file: { name: string } }) => ({
          downloadUrl: `/tasks/${state.requestTaskItem.requestTask.id}/file-download/${file.uuid}`,
          fileName: file.file.name,
        })),
      ),
    );

    this.route.paramMap.pipe(takeUntil(this.destroy$)).subscribe((paramMap) => {
      const link = ['/tasks', paramMap.get('taskId'), 'permit-notification', 'follow-up', 'review'];
      this.breadcrumbService.show([{ text: 'Permit Notification Follow Up Review', link }]);
    });
    this.enableOptionalFields();
  }

  getDownloadUrl() {
    return this.permitNotificationService.createBaseFileDownloadUrl();
  }

  onSubmit(): void {
    if (this.form.valid) {
      const type: PermitNotificationFollowUpReviewDecision['type'] = this.form.get('type').value;
      const payload: PermitNotificationFollowUpReviewDecision = {
        type,
        notes: this.form.get('notes').value,
        ...(type === 'AMENDS_NEEDED'
          ? {
              changesRequired: this.form.get('changesRequired').value,
              files: this.form.controls.files.value?.map((file: { uuid: string; file: { name: string } }) => file.uuid),
              dueDate: this.form.get('dueDate').value,
            }
          : null),
      };

      this.permitNotificationService
        .postFollowUpDecision(payload)
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => {
          let newFollowUpAttachments = {};

          this.form.get('files').value.forEach((e: { uuid: string; file: { name: string } }) => {
            newFollowUpAttachments = {
              ...newFollowUpAttachments,
              [e.uuid]: e.file.name,
            };
          });

          this.store.setState({
            ...this.store.getState(),
            requestTaskItem: {
              ...this.store.getState().requestTaskItem,
              requestTask: {
                ...this.store.getState().requestTaskItem.requestTask,
                payload: {
                  ...this.store.getState().requestTaskItem.requestTask.payload,
                  reviewSectionsCompleted: {},
                  followUpAttachments: {
                    ...this.store.getState().requestTaskItem.requestTask.payload['followUpAttachments'],
                    ...newFollowUpAttachments,
                  },
                } as PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
              },
            },
          });

          this.showSummary$.next(true);
          this.showNotificationBanner = true;
        });
    } else {
      this.isErrorSummaryDisplayed$.next(true);
      this.showNotificationBanner = false;
    }
  }

  changeDecisionClick() {
    this.showSummary$.next(false);
  }

  private enableOptionalFields() {
    this.form
      .get('type')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe((type: PermitNotificationFollowUpReviewDecision['type']) => {
        if (type === 'AMENDS_NEEDED') {
          this.form.get('changesRequired').enable();
          this.form.get('files').enable();
          this.form.get('dueDate').enable();
        }
      });
  }
}

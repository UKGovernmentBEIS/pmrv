import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { BehaviorSubject, first, takeUntil, tap } from 'rxjs';

import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { DestroySubject } from '../../../../core/services/destroy-subject.service';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { PermitNotificationService } from '../../core/permit-notification.service';
import { decisionFormProvider, REVIEW_FORM } from './decision-form.provider';

@Component({
  selector: 'app-decision',
  templateUrl: './decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [decisionFormProvider, DestroySubject, BreadcrumbService],
})
export class DecisionComponent implements OnInit {
  isEditable$ = this.store.select('isEditable');
  isErrorSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  showSummary$ = new BehaviorSubject<boolean>(false);
  showNotificationBanner$ = new BehaviorSubject<boolean>(false);

  reviewDecision$ = this.permitNotificationService.reviewDecision$;

  returnTo: { text: string; link: string };

  constructor(
    @Inject(REVIEW_FORM) readonly form: FormGroup,
    private readonly store: CommonTasksStore,
    readonly pendingRequest: PendingRequestService,
    readonly permitNotificationService: PermitNotificationService,
    private readonly destroy$: DestroySubject,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}

  notification$ = this.permitNotificationService.permitNotification$;

  ngOnInit(): void {
    this.reviewDecision$
      .pipe(
        first(),
        tap((res) => {
          if (res) {
            this.showSummary$.next(true);
          }
        }),
      )
      .subscribe();

    this.showLinks();

    this.enableOptionalFields();
  }

  onSubmit(): void {
    if (this.form.valid) {
      const followUpResponseRequired = this.form.get('followUpResponseRequired').value;
      const payload = {
        type: this.form.get('type').value ? 'ACCEPTED' : 'REJECTED',
        officialNotice: this.form.get('officialNotice').value,
        ...(this.form.get('type').value === true
          ? {
              followUp: {
                followUpResponseRequired,
                ...(followUpResponseRequired ? { followUpRequest: this.form.get('followUpRequest').value } : null),
                ...(followUpResponseRequired
                  ? { followUpResponseExpirationDate: this.form.get('followUpResponseExpirationDate').value }
                  : null),
              },
            }
          : null),
        notes: this.form.get('notes').value,
      };

      this.permitNotificationService
        .postPermitNotificationDecision(payload)
        .pipe(this.pendingRequest.trackRequest())
        .subscribe(() => {
          this.showSummary$.next(true);
          this.showNotificationBanner$.next(true);
        });
    } else {
      this.isErrorSummaryDisplayed$.next(true);
    }
  }

  changeDecisionClick() {
    this.showSummary$.next(false);
  }

  private showLinks() {
    this.store.pipe(first()).subscribe((state) => {
      const taskId = state.requestTaskItem.requestTask.id;
      let text, link;

      switch (state.requestTaskItem.requestTask.type) {
        case 'PERMIT_NOTIFICATION_APPLICATION_REVIEW':
          text = 'Permit Notification';
          link = ['/tasks', taskId, 'permit-notification', 'review'];
          break;
        case 'PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW':
          text = 'Notification application';
          link = ['/tasks', taskId, 'permit-notification', 'peer-review'];
          break;
        case 'PERMIT_NOTIFICATION_WAIT_FOR_PEER_REVIEW':
          text = 'Notification application';
          link = ['/tasks', taskId, 'permit-notification', 'peer-review-wait'];
          break;
        default:
          break;
      }
      this.breadcrumbService.show([{ text, link }]);

      this.returnTo = { text, link };
    });
  }

  private enableOptionalFields() {
    this.form
      .get('type')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => {
        if (this.form.get('followUpResponseRequired')) {
          this.form.get('followUpRequest').enable();
          this.form.get('followUpResponseExpirationDate').enable();
        }
      });
  }
}

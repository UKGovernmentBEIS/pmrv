import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { PermitNotificationService } from '../../core/permit-notification.service';

@Component({
  selector: 'app-answers',
  templateUrl: './answers.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BreadcrumbService, DestroySubject],
})
export class AnswersComponent {
  constructor(
    readonly store: CommonTasksStore,
    readonly permitNotificationService: PermitNotificationService,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}

  confirm(): void {
    this.permitNotificationService
      .getPayload()
      .pipe(
        first(),
        switchMap((payload) =>
          this.permitNotificationService.postTaskSave(
            {
              permitNotification: {
                ...payload.permitNotification,
              },
            },
            {},
            true,
            'DETAILS_CHANGE',
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../summary'], { relativeTo: this.route, state: { notification: true } }));

    this.route.paramMap.pipe(takeUntil(this.destroy$)).subscribe((paramMap) => {
      const link = [
        '/tasks',
        paramMap.get('taskId'),
        'permit-notification',
        this.router.url.includes('review') ? 'review' : 'submit',
      ];
      this.breadcrumbService.show([{ text: 'Permit Notification', link }]);
    });
  }
}

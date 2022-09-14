import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { PermitNotificationService } from '../../core/permit-notification.service';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject, BreadcrumbService],
})
export class SummaryComponent implements OnInit {
  constructor(
    private readonly router: Router,
    private route: ActivatedRoute,
    private destroy$: DestroySubject,
    private breadcrumbService: BreadcrumbService,
    readonly store: CommonTasksStore,
    readonly permitNotificationService: PermitNotificationService,
  ) {}
  notificationBanner = this.router.getCurrentNavigation()?.extras.state?.notification;

  notification$ = this.permitNotificationService.permitNotification$;

  ngOnInit(): void {
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

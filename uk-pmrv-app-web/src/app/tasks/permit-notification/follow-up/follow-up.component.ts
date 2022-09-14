import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, of, switchMap, take } from 'rxjs';

import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { RequestActionsService } from 'pmrv-api';

import { CommonTasksStore } from '../../store/common-tasks.store';

@Component({
  selector: 'app-follow-up',
  templateUrl: './follow-up.component.html',
  providers: [BreadcrumbService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FollowUpComponent {
  readonly daysRemaining$ = this.store.pipe(map((state) => state.requestTaskItem.requestTask.daysRemaining));
  navigationState = { returnUrl: this.router.url };

  constructor(
    readonly route: ActivatedRoute,
    readonly store: CommonTasksStore,
    private readonly requestActionsService: RequestActionsService,
    private readonly router: Router,
  ) {}

  viewAmends(): void {
    of(this.store.requestId)
      .pipe(
        first(),
        switchMap((requestId) => this.requestActionsService.getRequestActionsByRequestIdUsingGET(requestId)),
        map((actions) => actions.find((action) => action.type === 'PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS')),
        map((action) => action.id),
        take(1),
      )
      .subscribe((actionId) =>
        this.router.navigate(['/actions', actionId, 'permit-notification', 'follow-up-return-for-amends']),
      );
  }
}

import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { hasRequestTaskAllowedActions } from "@shared/components/related-actions/request-task-allowed-actions.map";

import { RequestActionInfoDTO, RequestActionsService, RequestTaskItemDTO } from 'pmrv-api';

@Component({
  selector: 'app-wait-for-appeal-tasklist',
  templateUrl: './wait-for-appeal-tasklist.component.html',
  providers: [BreadcrumbService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WaitForAppealTasklistComponent implements OnInit {
  readonly navigationState = { returnUrl: this.router.url };

  hasRelatedActions$ = this.store.pipe(
    map((state) => state.assignable || hasRequestTaskAllowedActions(state.allowedRequestTaskActions))
  );
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  readonly requestTaskItem$: Observable<RequestTaskItemDTO> = this.store.pipe(
    first(),
    map((state) => ({
      requestTask: {
        id: state.requestTaskId,
        type: state.requestTaskType,
        assigneeUserId: state.assignee.assigneeUserId,
        assigneeFullName: state.assignee.assigneeFullName,
        assignable: state.assignable,
      },
      requestInfo: { id: state.requestId },
      userAssignCapable: state.userAssignCapable,
      allowedRequestTaskActions: state.allowedRequestTaskActions,
    })),
  );
  readonly timelineActions$: Observable<RequestActionInfoDTO[]> = this.store.pipe(
    first(),
    switchMap((state) => this.requestActionsService.getRequestActionsByRequestIdUsingGET(state.requestId)),
    map((res) => this.sortTimeline(res, 'creationDate')),
  );
  constructor(
    readonly store: PermitRevocationStore,
    private readonly requestActionsService: RequestActionsService,
    private router: Router,
    readonly route: ActivatedRoute,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}

  ngOnInit(): void {
    this.breadcrumbService.show([{ text: 'Dashboard', link: ['/dashboard'] }]);
  }

  notifyOperator(): void {
    this.router.navigate(['withdraw-notify-operator'], { relativeTo: this.route });
  }

  checkAllowedActions(actions: RequestTaskItemDTO['allowedRequestTaskActions']): boolean {
    return actions.includes('PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL');
  }

  private sortTimeline(res: RequestActionInfoDTO[], key: string): RequestActionInfoDTO[] {
    return res.slice().sort((a, b) => new Date(b[key]).getTime() - new Date(a[key]).getTime());
  }
}

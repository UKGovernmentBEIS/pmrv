import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, filter, first, map, Observable, switchMap } from 'rxjs';

import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { hasRequestTaskAllowedActions } from "@shared/components/related-actions/request-task-allowed-actions.map";

import {
  ItemDTOResponse,
  RequestActionInfoDTO,
  RequestActionsService,
  RequestItemsService,
  RequestTaskItemDTO,
} from 'pmrv-api';

import { SectionStatus } from './confirm/core/cessation-status';
import { resolveConfirmSectionStatus } from './confirm/core/cessation-status';

@Component({
  selector: 'app-cessation',
  templateUrl: './cessation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CessationComponent {
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

  readonly relatedTasks$ = this.store.pipe(
    filter((state) => !!state.requestId),
    switchMap((state) => {
      const requestTaskId = state.requestTaskId;
      return this.requestItemsService.getItemsByRequestUsingGET(state.requestId).pipe(
        map((response) => {
          return [response, requestTaskId];
        }),
      );
    }),
    map(([items, requestTaskId]) => {
      const taskId = requestTaskId as number;
      return (items as ItemDTOResponse)?.items.filter((item) => item.taskId !== taskId);
    }),
  );

  readonly isNotifyOperatorActionEnabled$: Observable<boolean> = combineLatest([
    this.store.pipe(first()),
    this.requestTaskItem$,
  ]).pipe(
    map(
      ([state, requestTaskItem]) =>
        state?.cessationCompleted &&
        requestTaskItem.allowedRequestTaskActions?.includes('PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_CESSATION'),
    ),
  );

  readonly timelineActions$: Observable<RequestActionInfoDTO[]> = this.store.pipe(
    first(),
    switchMap((state) => this.requestActionsService.getRequestActionsByRequestIdUsingGET(state.requestId)),
    map((res) => this.sortTimeline(res, 'creationDate')),
  );

  readonly confirmSectionStatus$: Observable<SectionStatus> = this.store.pipe(
    first(),
    map((state) => resolveConfirmSectionStatus(state)),
  );

  constructor(
    readonly store: PermitRevocationStore,
    private readonly requestActionsService: RequestActionsService,
    private router: Router,
    readonly route: ActivatedRoute,
    private readonly requestItemsService: RequestItemsService,
  ) {}

  private sortTimeline(res: RequestActionInfoDTO[], key: string): RequestActionInfoDTO[] {
    return res.slice().sort((a, b) => new Date(b[key]).getTime() - new Date(a[key]).getTime());
  }

  notifyOperator(): void {
    this.router.navigate(['notify-operator'], { relativeTo: this.route });
  }
}

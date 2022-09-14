import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, of, switchMap, withLatestFrom } from 'rxjs';

import { hasRequestTaskAllowedActions } from "@shared/components/related-actions/request-task-allowed-actions.map";

import {
  ItemDTOResponse,
  RequestActionInfoDTO,
  RequestActionsService,
  RequestItemsService,
  RequestTaskItemDTO,
} from 'pmrv-api';

import { PermitSurrenderStore } from '../store/permit-surrender.store';
import { SectionStatus } from './core/cessation';
import { resolveConfirmSectionStatus } from './core/cessation-status';

@Component({
  selector: 'app-cessation',
  templateUrl: './cessation.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CessationComponent {
  private readonly storeFirst$ = this.store.pipe(first());
  readonly navigationState = { returnUrl: this.router.url };

  hasRelatedActions$ = this.store.pipe(
    map((state) => state.assignable || hasRequestTaskAllowedActions(state.allowedRequestTaskActions))
  );
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  
  readonly actions$ = this.storeFirst$.pipe(
    switchMap((state) => this.requestActionsService.getRequestActionsByRequestIdUsingGET(state.requestId)),
    map((res) => this.sortTimeline(res)),
  );
  readonly requestTaskItem$: Observable<RequestTaskItemDTO> = this.storeFirst$.pipe(
    map((state) => ({
      requestTask: {
        id: state.requestTaskId,
        type: state.requestTaskType,
        assigneeUserId: state.assignee.assigneeUserId,
        assigneeFullName: state.assignee.assigneeFullName,
        assignable: state.assignable,
        daysRemaining: state.daysRemaining,
      },
      requestInfo: { id: state.requestId },
      userAssignCapable: state.userAssignCapable,
      allowedRequestTaskActions: state.allowedRequestTaskActions,
    })),
  );
  readonly relatedTasks$ = this.store.pipe(
    switchMap((state) =>
      state.requestId
        ? this.requestItemsService.getItemsByRequestUsingGET(state.requestId)
        : of({ items: [], totalItems: 0 } as ItemDTOResponse),
    ),
    withLatestFrom(this.store),
    map(([items, state]) => items?.items.filter((item) => item.taskId !== state?.requestTaskId)),
  );

  readonly confirmSectionStatus$: Observable<SectionStatus> = this.storeFirst$.pipe(
    map((state) => resolveConfirmSectionStatus(state)),
  );

  readonly isNotifyOperatorActionEnabled$: Observable<boolean> = combineLatest([
    this.storeFirst$,
    this.requestTaskItem$,
  ]).pipe(
    map(
      ([state, requestTaskItem]) =>
        state?.cessationCompleted &&
        requestTaskItem.allowedRequestTaskActions?.includes('PERMIT_SURRENDER_CESSATION_NOTIFY_OPERATOR_FOR_DECISION'),
    ),
  );

  constructor(
    readonly store: PermitSurrenderStore,
    private readonly requestActionsService: RequestActionsService,
    private readonly requestItemsService: RequestItemsService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  notifyOperator(): void {
    this.router.navigate(['notify-operator'], { relativeTo: this.route });
  }

  private sortTimeline(res: RequestActionInfoDTO[]): RequestActionInfoDTO[] {
    return res.slice().sort((a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime());
  }
}

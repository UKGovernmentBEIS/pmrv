import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { hasRequestTaskAllowedActions } from "@shared/components/related-actions/request-task-allowed-actions.map";

import { RequestActionInfoDTO, RequestActionsService, RequestTaskItemDTO } from 'pmrv-api';

import { PermitSurrenderStore } from '../../store/permit-surrender.store';

@Component({
  selector: 'app-wait-review',
  templateUrl: './wait-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WaitReviewComponent {
  readonly storeFirst$ = this.store.pipe(first());
  readonly navigationState = { returnUrl: this.router.url };

  hasRelatedActions$ = this.storeFirst$.pipe(
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
      allowedRequestTaskActions: state.allowedRequestTaskActions,
    })),
  );

  constructor(
    readonly store: PermitSurrenderStore,
    private readonly requestActionsService: RequestActionsService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  private sortTimeline(res: RequestActionInfoDTO[]): RequestActionInfoDTO[] {
    return res.slice().sort((a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime());
  }
}

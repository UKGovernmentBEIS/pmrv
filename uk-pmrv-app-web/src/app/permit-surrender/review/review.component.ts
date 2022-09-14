import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { filter, first, map, Observable, of, switchMap, takeUntil, withLatestFrom } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { hasRequestTaskAllowedActions } from "@shared/components/related-actions/request-task-allowed-actions.map";

import {
  ItemDTOResponse,
  RequestActionInfoDTO,
  RequestActionsService,
  RequestItemsService,
  RequestTaskItemDTO,
} from 'pmrv-api';

import { PermitSurrenderStore } from '../store/permit-surrender.store';
import { needsReview } from './core/review-status';

@Component({
  selector: 'app-review',
  templateUrl: './review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class ReviewComponent {
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
  requestTaskItem$: Observable<RequestTaskItemDTO> = this.store.pipe(
    filter((state) => !!state.requestTaskId),
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

  readonly determinationLink: Observable<any> = this.store.pipe(
    map((state) => {
      const reviewDetermination = state.reviewDetermination;
      if (!reviewDetermination?.type) {
        return 'determination';
      }
      switch (state.reviewDetermination.type) {
        case 'GRANTED':
          return 'determination/grant/summary';
        case 'REJECTED':
          return 'determination/reject/summary';
        case 'DEEMED_WITHDRAWN':
          return 'determination/deem-withdraw/summary';
      }
    }),
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

  readonly isDeterminationCompleted$: Observable<boolean> = this.store.pipe(
    map((state) => state?.reviewDeterminationCompleted === true && !needsReview(state)),
  );

  constructor(
    readonly store: PermitSurrenderStore,
    private readonly requestActionsService: RequestActionsService,
    private readonly requestItemsService: RequestItemsService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly destroy$: DestroySubject,
  ) {}

  notifyOperator(): void {
    this.store
      .pipe(
        map((state) => needsReview(state)),
        takeUntil(this.destroy$),
      )
      .subscribe((needsReview) =>
        needsReview
          ? this.router.navigate(['invalid-data'], { relativeTo: this.route })
          : this.router.navigate(['notify-operator'], { relativeTo: this.route }),
      );
  }

  peerReviewDecision(): void {
    this.router.navigate(['peer-review-decision'], { relativeTo: this.route });
  }

  private sortTimeline(res: RequestActionInfoDTO[]): RequestActionInfoDTO[] {
    return res.slice().sort((a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime());
  }

  sendPeerReview(): void {
    this.store
      .pipe(
        map((state) => needsReview(state)),
        takeUntil(this.destroy$),
      )
      .subscribe((needsReview) =>
        needsReview
          ? this.router.navigate(['invalid-data'], { relativeTo: this.route })
          : this.router.navigate(['peer-review'], { relativeTo: this.route }),
      );
  }
}

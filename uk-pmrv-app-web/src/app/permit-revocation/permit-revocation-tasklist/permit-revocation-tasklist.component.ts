import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, pluck, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { checkForReviewStatus } from '@permit-revocation/core/section-status';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { hasRequestTaskAllowedActions } from "@shared/components/related-actions/request-task-allowed-actions.map";

import {
  RequestActionInfoDTO,
  RequestActionsService,
  RequestTaskItemDTO,
} from 'pmrv-api';

@Component({
  selector: 'app-permit-revocation-tasklist',
  templateUrl: './permit-revocation-tasklist.component.html',
  providers: [BackLinkService, DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PermitRevocationTasklistComponent implements OnInit {
  readonly navigationState = { returnUrl: this.router.url };

  hasRelatedActions$ = this.store.pipe(
    map((state) => state.assignable || hasRequestTaskAllowedActions(state.allowedRequestTaskActions))
  );
  readonly isEditable$: Observable<boolean> = this.store.isEditable$;

  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  isTask$ = this.store.pipe(pluck('isRequestTask'));

  readonly timelineActions$: Observable<RequestActionInfoDTO[]> = this.store.pipe(
    first(),
    switchMap((state) => this.requestActionsService.getRequestActionsByRequestIdUsingGET(state.requestId)),
    map((res) => this.sortTimeline(res, 'creationDate')),
  );

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

  readonly isDeterminationCompleted$: Observable<boolean> = this.store.pipe(
    map((state) => state?.sectionsCompleted?.REVOCATION_APPLY && checkForReviewStatus(state)),
  );

  constructor(
    readonly store: PermitRevocationStore,
    private router: Router,
    private requestActionsService: RequestActionsService,
    private route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.isEditable$.pipe(takeUntil(this.destroy$)).subscribe((editable) => {
      if (!editable) {
        this.backLinkService.show();
      }
    });
  }

  private sortTimeline(res: RequestActionInfoDTO[], key: string): RequestActionInfoDTO[] {
    return res.slice().sort((a, b) => new Date(b[key]).getTime() - new Date(a[key]).getTime());
  }

  notifyOperator(): void {
    this.router.navigate(['notify-operator'], { relativeTo: this.route });
  }

  sendPeerReview(): void {
    this.store
      .pipe(
        takeUntil(this.destroy$),
        map((state) => checkForReviewStatus(state)),
      )
      .subscribe((reviewStatus) =>
        reviewStatus
          ? this.router.navigate(['peer-review'], { relativeTo: this.route })
          : this.router.navigate(['invalid-data'], { relativeTo: this.route }),
      );
  }

  peerReviewDecision(): void {
    this.router.navigate(['peer-review-decision'], { relativeTo: this.route });
  }
}

import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, filter, first, map, Observable, of, switchMap, takeUntil, withLatestFrom } from 'rxjs';

import {
  ItemDTOResponse,
  RequestActionInfoDTO,
  RequestActionsService,
  RequestItemsService,
  RequestTaskItemDTO,
} from 'pmrv-api';

import { DestroySubject } from '../../core/services/destroy-subject.service';
import { BackLinkService } from '../../shared/back-link/back-link.service';
import { hasRequestTaskAllowedActions } from '../../shared/components/related-actions/request-task-allowed-actions.map';
import { PermitApplicationStore } from '../store/permit-application.store';
import { variationDetailsReviewStatus } from '../variation/variation-status';
import {
  isValidForAmends,
  resolveDeterminationStatus,
  ReviewDeterminationStatus,
  reviewGroupHeading,
  ReviewGroupStatus,
} from './review';

@Component({
  selector: 'app-review',
  templateUrl: './review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService, DestroySubject],
})
export class ReviewComponent implements OnInit {
  isTask$ = this.store.pipe(map((state) => state.isRequestTask));

  hasRelatedActions$ = this.store.pipe(
    map((state) => state.assignable || hasRequestTaskAllowedActions(state.allowedRequestTaskActions)),
  );
  isRelatedActionsSectionVisible$ = combineLatest([this.isTask$, this.hasRelatedActions$]).pipe(
    map(([isTask, hasRelatedActions]) => isTask && hasRelatedActions),
  );

  isVariation$: Observable<boolean> = this.store.pipe(map((state) => state.isVariation));
  taskId$ = this.store.pipe(map((state) => state.requestTaskId));

  variationDetailsReviewStatus$: Observable<ReviewGroupStatus> = this.store.pipe(
    map((state) => variationDetailsReviewStatus(state)),
  );

  allowedRequestTaskActions$ = this.store.pipe(
    filter((state) => state.isRequestTask),
    map((state) => state.allowedRequestTaskActions),
  );

  requestTaskType$ = this.store.pipe(
    filter((state) => state.isRequestTask),
    map((state) => state.requestTaskType),
  );

  linkText = reviewGroupHeading;
  navigationState = { returnUrl: this.router.url };

  readonly info$: Observable<RequestTaskItemDTO> = this.store.pipe(
    map((state) => ({
      requestTask: {
        id: state.requestTaskId,
        assigneeUserId: state.assignee?.assigneeUserId,
        assigneeFullName: state.assignee?.assigneeFullName,
        assignable: state.assignable,
        type: state.requestTaskType as any,
      },
      userAssignCapable: state.userAssignCapable,
    })),
  );

  readonly isPermitReviewSectionsVisible$ = this.store.pipe(
    filter(
      (state) =>
        [
          'PERMIT_ISSUANCE_APPLICATION_REVIEW',
          'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW',
          'PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW',
          'PERMIT_ISSUANCE_WAIT_FOR_AMENDS',
          'PERMIT_VARIATION_APPLICATION_REVIEW',
        ].includes(state.requestTaskType) || ['PERMIT_ISSUANCE_APPLICATION_GRANTED'].includes(state.requestActionType),
    ),
  );

  readonly header$ = this.store.pipe(
    map((state) => {
      if (state.isRequestTask) {
        switch (state.requestTaskType) {
          case 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW':
            return state.permitType + ' permit peer review';
          case 'PERMIT_ISSUANCE_WAIT_FOR_REVIEW':
            return 'Apply for a permit';
          case 'PERMIT_VARIATION_APPLICATION_REVIEW':
            return 'Permit variation review';
          case 'PERMIT_VARIATION_WAIT_FOR_REVIEW':
            return 'Make a change to your permit';
          default:
            return state.permitType + ' permit determination';
        }
      } else {
        return state.permitType + ' permit determination';
      }
    }),
  );

  readonly requestActions$ = this.store.pipe(
    filter((state) => state.isRequestTask),
    switchMap((state) => this.requestActionsService.getRequestActionsByRequestIdUsingGET(state.requestId)),
    map((res) => this.sortTimeline(res)),
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

  readonly determinationStatus$: Observable<ReviewDeterminationStatus> = this.store.pipe(
    map((state) => {
      if (state.requestActionType === 'PERMIT_ISSUANCE_APPLICATION_GRANTED') {
        return 'granted';
      } else {
        return resolveDeterminationStatus(state);
      }
    }),
  );

  readonly isDeterminationCompleted$: Observable<boolean> = this.store.pipe(
    map((state) => state?.reviewSectionsCompleted?.determination === true),
  );

  readonly isAnyForAmends$: Observable<boolean> = this.store.pipe(
    first(),
    map((state) => isValidForAmends(state)),
  );

  constructor(
    readonly store: PermitApplicationStore,
    private readonly requestActionsService: RequestActionsService,
    private readonly requestItemsService: RequestItemsService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly destroy$: DestroySubject,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit() {
    this.store.isEditable$.pipe(takeUntil(this.destroy$)).subscribe((editable) => {
      if (!editable) {
        this.backLinkService.show();
      }
    });
  }

  private sortTimeline(res: RequestActionInfoDTO[]): RequestActionInfoDTO[] {
    return res.slice().sort((a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime());
  }

  notifyOperator(): void {
    this.router.navigate(['notify-operator'], { relativeTo: this.route });
  }

  sendPeerReview(): void {
    this.router.navigate(['peer-review'], { relativeTo: this.route });
  }

  peerReviewDecision(): void {
    this.router.navigate(['peer-review-decision'], { relativeTo: this.route });
  }

  sendReturnForAmends(): void {
    this.router.navigate(['return-for-amends'], { relativeTo: this.route });
  }
}

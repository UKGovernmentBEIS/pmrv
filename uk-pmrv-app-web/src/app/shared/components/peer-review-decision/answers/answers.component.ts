import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap, tap } from 'rxjs';

import { SharedStore } from '@shared/store/shared.store';

import { PeerReviewDecision, RequestTaskActionPayload, TasksService } from 'pmrv-api';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../../core/interfaces/pending-request.interface';
import { catchTaskReassignedBadRequest } from '../../../../error/business-errors';
import { ConcurrencyErrorService } from '../../../../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode } from '../../../../error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '../../../errors/concurrency-error';
import {
  getPreviousPage,
  getRequestTaskActionPayloadType,
  getRequestTaskActionType,
  getRequestType,
} from '../peer-review-decision-type-resolver';

@Component({
  selector: 'app-answers',
  templateUrl: './answers.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AnswersComponent implements OnInit, PendingRequest {
  private readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  type: PeerReviewDecision['type'];
  notes: string;

  requestType = getRequestType(this.router.url);
  previousPage = getPreviousPage(this.requestType);

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly tasksService: TasksService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
    private readonly sharedStore: SharedStore,
  ) {}

  ngOnInit(): void {
    this.sharedStore
      .pipe(
        map((state) => state.peerReviewDecision),
        first(),
      )
      .subscribe((peerReviewDecision) => {
        this.type = peerReviewDecision?.type;
        this.notes = peerReviewDecision?.notes;
      });
  }

  onSubmit(): void {
    this.taskId$
      .pipe(
        first(),
        switchMap((taskId) =>
          this.tasksService.processRequestTaskActionUsingPOST({
            requestTaskActionType: getRequestTaskActionType(this.requestType),
            requestTaskId: taskId,
            requestTaskActionPayload: {
              payloadType: getRequestTaskActionPayloadType(this.requestType),
              decision: {
                type: this.type,
                notes: this.notes,
              },
            } as RequestTaskActionPayload,
          }),
        ),
        tap(() =>
          this.sharedStore.setState({
            ...this.sharedStore.getState(),
            peerReviewDecision: undefined,
          }),
        ),
        this.pendingRequest.trackRequest(),
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      )
      .subscribe(() => this.router.navigate(['../confirmation'], { relativeTo: this.route }));
  }

  changeClick(): void {
    this.router.navigate(['../'], {
      relativeTo: this.route,
      state: {
        type: this.type,
        notes: this.notes,
      },
    });
  }
}

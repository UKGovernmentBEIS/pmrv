import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, switchMap, take } from 'rxjs';

import { RequestTaskActionPayload, TasksService } from 'pmrv-api';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../core/interfaces/pending-request.interface';
import { catchTaskReassignedBadRequest } from '../../../error/business-errors';
import { ConcurrencyErrorService } from '../../../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode } from '../../../error/not-found-error';
import { BackLinkService } from '../../../shared/back-link/back-link.service';
import { requestTaskReassignedError, taskNotFoundError } from '../../../shared/errors/concurrency-error';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { reviewGroupHeading } from '../review';

@Component({
  selector: 'app-return-for-amends',
  templateUrl: './return-for-amends.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class ReturnForAmendsComponent implements PendingRequest, OnInit {
  isTask$: Observable<boolean> = this.store.pipe(map((state) => state.isRequestTask));
  returnUrl = this.router.getCurrentNavigation()?.extras.state?.returnUrl ?? '/';

  decisionAmends$ = this.store.pipe(
    map((state) =>
      Object.keys(state?.reviewGroupDecisions ?? [])
        .filter((key) => state?.reviewGroupDecisions[key].type === 'OPERATOR_AMENDS_NEEDED')
        .map((key) => ({ groupKey: key, data: state.reviewGroupDecisions[key] })),
    ),
  );

  heading = reviewGroupHeading;

  constructor(
    private readonly tasksService: TasksService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onSubmit(): void {
    this.route.paramMap
      .pipe(
        first(),
        map((paramMap) => Number(paramMap.get('taskId'))),
        switchMap((taskId) =>
          this.tasksService.processRequestTaskActionUsingPOST({
            requestTaskActionType: 'PERMIT_ISSUANCE_REVIEW_RETURN_FOR_AMENDS',
            requestTaskId: taskId,
            requestTaskActionPayload: {
              payloadType: 'EMPTY_PAYLOAD',
            } as RequestTaskActionPayload,
          }),
        ),
        this.pendingRequest.trackRequest(),
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        take(1),
      )
      .subscribe(() => this.router.navigate(['./confirmation'], { relativeTo: this.route }));
  }
}

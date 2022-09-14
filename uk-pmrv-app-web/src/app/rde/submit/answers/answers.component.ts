import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, EMPTY, first, map, switchMap, takeUntil } from 'rxjs';

import { RequestTaskActionPayload, TasksService } from 'pmrv-api';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { DestroySubject } from '../../../core/services/destroy-subject.service';
import {
  catchBadRequest,
  catchTaskReassignedBadRequest,
  ErrorCode as BusinessErrorCode,
} from '../../../error/business-errors';
import { ConcurrencyErrorService } from '../../../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode as NotFoundError } from '../../../error/not-found-error';
import { BackLinkService } from '../../../shared/back-link/back-link.service';
import { requestTaskReassignedError, taskNotFoundError } from '../../../shared/errors/concurrency-error';
import { UserFullNamePipe } from '../../../shared/pipes/user-full-name.pipe';
import { RdeStore } from '../../store/rde.store';

@Component({
  selector: 'app-answers',
  templateUrl: './answers.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [UserFullNamePipe, BackLinkService, DestroySubject],
})
export class AnswersComponent implements OnInit {
  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  readonly isTemplateGenerationErrorDisplayed$ = new BehaviorSubject<boolean>(false);
  templateFailed: string;

  constructor(
    readonly store: RdeStore,
    readonly pendingRequest: PendingRequestService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
    private readonly tasksService: TasksService,
    private readonly backLinkService: BackLinkService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.store
      .pipe(
        first(),
        map((state) =>
          state.requestTaskId
            ? state.requestType === 'PERMIT_SURRENDER'
              ? `/permit-surrender/${state.requestTaskId}/review`
              : `/permit-application/${state.requestTaskId}/review`
            : null,
        ),
        takeUntil(this.destroy$),
      )
      .subscribe((link) => this.backLinkService.show(link));
  }

  confirm(): void {
    combineLatest([this.taskId$, this.store])
      .pipe(
        first(),
        switchMap(([taskId, state]) =>
          this.tasksService.processRequestTaskActionUsingPOST({
            requestTaskActionType: 'RDE_SUBMIT',
            requestTaskId: taskId,
            requestTaskActionPayload: {
              payloadType: 'RDE_SUBMIT_PAYLOAD',
              rdePayload: {
                extensionDate: this.getDate(state.rdePayload.extensionDate),
                deadline: this.getDate(state.rdePayload.deadline),
                ...(state.rdePayload.operators
                  ? {
                      operators: state.rdePayload.operators,
                    }
                  : {}),
                signatory: state.rdePayload.signatory,
              },
            } as RequestTaskActionPayload,
          }),
        ),
        this.pendingRequest.trackRequest(),
        catchNotFoundRequest(NotFoundError.NOTFOUND1001, () =>
          this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchBadRequest(BusinessErrorCode.NOTIF1002, (res) => {
          this.isTemplateGenerationErrorDisplayed$.next(true);
          this.templateFailed = res.error.data[0];
          return EMPTY;
        }),
        catchTaskReassignedBadRequest(() =>
          this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      )
      .subscribe(() => this.router.navigate(['../confirmation'], { relativeTo: this.route }));
  }

  private getDate(dateTime) {
    const dt = new Date(dateTime);

    return dt.getFullYear() + '-' + ('0' + (dt.getMonth() + 1)).slice(-2) + '-' + ('0' + dt.getDate()).slice(-2);
  }
}

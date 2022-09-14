import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, EMPTY, first, map, Observable, pluck, switchMap, takeUntil } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/concurrency-error';

import { RequestActionUserInfo, RequestTaskActionPayload, TasksService } from 'pmrv-api';

import {
  catchBadRequest,
  catchTaskReassignedBadRequest,
  ErrorCode as BusinessErrorCode,
} from '../../../error/business-errors';
import { ConcurrencyErrorService } from '../../../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode as NotFoundError } from '../../../error/not-found-error';
import { RfiStore } from '../../store/rfi.store';

@Component({
  selector: 'app-answers',
  templateUrl: './answers.component.html',
  providers: [BackLinkService, DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AnswersComponent implements PendingRequest, OnInit {
  private readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  questions$: Observable<string[]>;
  deadline$: Observable<string>;
  signatory$: Observable<string>;
  usersInfo$: Observable<{
    [key: string]: RequestActionUserInfo;
  }>;
  operators$: Observable<string[]>;
  readonly isTemplateGenerationErrorDisplayed$ = new BehaviorSubject<boolean>(false);
  templateFailed: string;

  constructor(
    readonly store: RfiStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly tasksService: TasksService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
    private readonly backLinkService: BackLinkService,
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

    this.questions$ = this.store.pipe(pluck('rfiSubmitPayload', 'questions'));
    this.deadline$ = this.store.pipe(pluck('rfiSubmitPayload', 'deadline'));
    this.signatory$ = this.store.pipe(pluck('rfiSubmitPayload', 'signatory'));
    this.usersInfo$ = this.store.pipe(pluck('usersInfo')) as Observable<{ [key: string]: RequestActionUserInfo }>;
    this.operators$ = combineLatest([this.usersInfo$, this.signatory$]).pipe(
      first(),
      map(([usersInfo, signatory]) => Object.keys(usersInfo).filter((userId) => userId !== signatory)),
    );
  }

  onConfirm(): void {
    combineLatest([this.taskId$, this.store])
      .pipe(
        first(),
        switchMap(([taskId, store]) =>
          this.tasksService.processRequestTaskActionUsingPOST({
            requestTaskActionType: 'RFI_SUBMIT',
            requestTaskId: taskId,
            requestTaskActionPayload: {
              payloadType: 'RFI_SUBMIT_PAYLOAD',
              rfiSubmitPayload: {
                questions: store.rfiSubmitPayload.questions,
                files: store.rfiSubmitPayload.files,
                deadline: store.rfiSubmitPayload.deadline,
                ...(store.rfiSubmitPayload.operators
                  ? {
                      operators: store.rfiSubmitPayload.operators,
                    }
                  : {}),
                signatory: store.rfiSubmitPayload.signatory,
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
      .subscribe(() => this.router.navigate(['../submit-confirmation'], { relativeTo: this.route }));
  }

  changeClick(wizardStep?: string): void {
    this.router.navigate(['../' + wizardStep], { relativeTo: this.route, state: { changing: true } });
  }
}

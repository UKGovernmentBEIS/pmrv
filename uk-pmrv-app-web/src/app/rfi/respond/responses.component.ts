import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, of, pluck, startWith, switchMap, withLatestFrom } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { DestroySubject } from '@core/services/destroy-subject.service';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/concurrency-error';

import {
  ItemDTOResponse,
  RequestActionInfoDTO,
  RequestActionsService,
  RequestItemsService,
  RequestTaskActionPayload,
  TasksService,
} from 'pmrv-api';

import { catchTaskReassignedBadRequest } from '../../error/business-errors';
import { ConcurrencyErrorService } from '../../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode } from '../../error/not-found-error';
import { RfiStore } from '../store/rfi.store';
import { responseFormProvider, RFI_FORM } from './responses-form.provider';

@Component({
  selector: 'app-responses',
  templateUrl: './responses.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject, responseFormProvider],
})
export class ResponsesComponent {
  private readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  navigationState = { returnUrl: this.router.url };

  readonly questions$ = this.store.pipe(pluck('rfiQuestionPayload', 'questions')) as Observable<Array<string>>;

  readonly relatedTasks$ = this.store.pipe(
    first(),
    switchMap((state) =>
      state.requestId
        ? this.requestItemsService.getItemsByRequestUsingGET(state.requestId)
        : of({ items: [], totalItems: 0 } as ItemDTOResponse),
    ),
    withLatestFrom(this.store),
    map(([items, state]) => items?.items.filter((item) => item.taskId !== state?.requestTaskId)),
  );

  readonly actions$ = this.store.pipe(
    first(),
    switchMap((state) => this.requestActionsService.getRequestActionsByRequestIdUsingGET(state.requestId)),
    map((res) => this.sortTimeline(res)),
  );

  readonly isFileUploaded$: Observable<boolean> = this.form.get('files').valueChanges.pipe(
    startWith(this.form.get('files').value),
    map((value) => value?.length > 0),
  );

  readonly daysRemaining$ = this.store.pipe(pluck('daysRemaining'));
  readonly assignee$ = this.store.pipe(pluck('assignee'));

  constructor(
    @Inject(RFI_FORM) readonly form: FormGroup,
    readonly store: RfiStore,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly tasksService: TasksService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
    private readonly requestItemsService: RequestItemsService,
    private readonly requestActionsService: RequestActionsService,
  ) {}

  onContinue(): void {
    this.taskId$
      .pipe(
        first(),
        switchMap((taskId) =>
          this.tasksService.processRequestTaskActionUsingPOST({
            requestTaskActionType: 'RFI_RESPONSE_SUBMIT',
            requestTaskId: taskId,
            requestTaskActionPayload: {
              payloadType: 'RFI_RESPONSE_SUBMIT_PAYLOAD',
              rfiResponsePayload: {
                answers: (this.form.get('pairs').value as Array<any>).map((a) => a.response),
                files: (this.form.get('files').value as Array<any>).map((a) => a.uuid),
              },
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
      )
      .subscribe(() => this.router.navigate(['../respond-confirmation'], { relativeTo: this.route }));
  }

  getDownloadUrl() {
    return this.store.createBaseFileDownloadUrl();
  }

  private sortTimeline(res: RequestActionInfoDTO[]): RequestActionInfoDTO[] {
    return res.slice().sort((a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime());
  }
}

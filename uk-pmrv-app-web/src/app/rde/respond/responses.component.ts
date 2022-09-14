import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, of, switchMap, withLatestFrom } from 'rxjs';

import {
  ItemDTOResponse,
  RequestActionInfoDTO,
  RequestActionsService,
  RequestItemsService,
  RequestTaskActionPayload,
  TasksService,
} from 'pmrv-api';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { catchTaskReassignedBadRequest } from '../../error/business-errors';
import { ConcurrencyErrorService } from '../../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode } from '../../error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '../../shared/errors/concurrency-error';
import { RdeStore } from '../store/rde.store';
import { RDE_FORM, responseFormProvider } from './responses-form.provider';

@Component({
  selector: 'app-responses',
  templateUrl: './responses.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [responseFormProvider],
})
export class ResponsesComponent {
  private readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  navigationState = { returnUrl: this.router.url };

  readonly actions$ = this.store.pipe(
    first(),
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

  constructor(
    @Inject(RDE_FORM) readonly form: FormGroup,
    readonly store: RdeStore,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly tasksService: TasksService,
    readonly pendingRequest: PendingRequestService,
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
            requestTaskActionType: 'RDE_RESPONSE_SUBMIT',
            requestTaskId: taskId,
            requestTaskActionPayload: {
              payloadType: 'RDE_RESPONSE_SUBMIT_PAYLOAD',
              rdeDecisionPayload: {
                decision: this.form.get('decision').value,
                reason: this.form.get('reason').value,
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
      .subscribe(() =>
        this.router.navigate(['../respond-confirmation'], {
          relativeTo: this.route,
          state: { decision: this.form.get('decision').value },
        }),
      );
  }

  private sortTimeline(res: RequestActionInfoDTO[]): RequestActionInfoDTO[] {
    return res.slice().sort((a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime());
  }
}

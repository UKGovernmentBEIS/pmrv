import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, Observable, of, startWith, switchMap, withLatestFrom } from 'rxjs';

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
import { forceDecisionProvider, RDE_FORM } from './force-decision-form.provider';

@Component({
  selector: 'app-force-decision',
  templateUrl: './force-decision.component.html',
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [forceDecisionProvider],
})
export class ForceDecisionComponent {
  private readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  navigationState = { returnUrl: this.router.url };

  readonly relatedTasks$ = this.store.pipe(
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

  getDownloadUrl() {
    return this.store.createBaseFileDownloadUrl();
  }

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
            requestTaskActionType: 'RDE_FORCE_DECISION',
            requestTaskId: taskId,
            requestTaskActionPayload: {
              payloadType: 'RDE_FORCE_DECISION_PAYLOAD',
              rdeForceDecisionPayload: {
                decision: this.form.get('decision').value,
                evidence: this.form.get('evidence').value,
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
      .subscribe(() =>
        this.router.navigate(['../manual-approval-confirmation'], {
          relativeTo: this.route,
          state: { decision: this.form.get('decision').value },
        }),
      );
  }

  private sortTimeline(res: RequestActionInfoDTO[]): RequestActionInfoDTO[] {
    return res.slice().sort((a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime());
  }
}

import { Inject, Injectable } from '@angular/core';

import { distinctUntilChanged, filter, interval, map, Observable, pluck, Subscription, switchMapTo, tap } from 'rxjs';

import { LOCAL_STORAGE } from '@core/services/local-storage';
import { Store } from '@core/store';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/concurrency-error';

import {
  PermitCessation,
  PermitRevocation,
  PermitRevocationApplicationSubmitRequestTaskPayload,
  PermitRevocationApplicationWithdrawRequestTaskActionPayload,
  PermitRevocationSaveApplicationRequestTaskActionPayload,
  PermitSaveCessationRequestTaskActionPayload,
  TasksService,
} from 'pmrv-api';

import { catchTaskReassignedBadRequest } from '../../error/business-errors';
import { ConcurrencyErrorService } from '../../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode } from '../../error/not-found-error';
import { initialState, PermitRevocationState } from './permit-revocation.state';

const localStorageItemCode = 'permit-revocation';

@Injectable({ providedIn: 'root' })
export class PermitRevocationStore extends Store<PermitRevocationState> {
  private localStorageSubscription: Subscription;

  private storage$ = interval(2000).pipe(
    switchMapTo(this),
    pluck('requestTaskId'),
    filter((requestTaskId) => !!requestTaskId),
    map((requestTaskId) => this.localStorage.getItem(`${localStorageItemCode}/${requestTaskId}`)),
    distinctUntilChanged(),
    filter((state) => state && state !== JSON.stringify(this.getState())),
    map((state) => JSON.parse(state)),
  );

  constructor(
    @Inject(LOCAL_STORAGE) private readonly localStorage: Storage,
    private readonly tasksService: TasksService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {
    super(initialState);
  }

  listenToStorage(): void {
    this.localStorageSubscription = this.storage$.subscribe((state) => this.setState(state));
  }

  stopListeningToStorage(): void {
    this.localStorageSubscription.unsubscribe();
  }

  setState(state: PermitRevocationState): void {
    if (state.requestTaskId) {
      this.localStorage.setItem(`${localStorageItemCode}/${state.requestTaskId}`, JSON.stringify(state));
    }
    super.setState(state);
  }

  get payload(): PermitRevocationApplicationSubmitRequestTaskPayload {
    return this.getValue() as PermitRevocationApplicationSubmitRequestTaskPayload;
  }

  get permitRevocation(): PermitRevocation {
    return this.payload.permitRevocation;
  }

  get isEditable$(): Observable<boolean> {
    return this.pipe(pluck('isEditable'));
  }

  getPermitRevocation(): Observable<PermitRevocation> {
    return this.pipe(pluck('permitRevocation'), distinctUntilChanged());
  }

  postApplyPermitRevocation(state: PermitRevocationState) {
    const payload: PermitRevocationSaveApplicationRequestTaskActionPayload = {
      payloadType: 'PERMIT_REVOCATION_SAVE_APPLICATION_PAYLOAD',
      permitRevocation: state.permitRevocation,
      sectionsCompleted: state.sectionsCompleted,
    };

    return this.tasksService
      .processRequestTaskActionUsingPOST({
        requestTaskActionType: 'PERMIT_REVOCATION_SAVE_APPLICATION',
        requestTaskId: state.requestTaskId,
        requestTaskActionPayload: payload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        tap(() => {
          this.setState(state);
        }),
      );
  }

  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    const url = this.createBaseFileDownloadUrl();

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: this.getState().revocationAttachments[id],
      })) ?? []
    );
  }

  postPermitRevocationWithdraw(state: PermitRevocationState) {
    const payload: PermitRevocationApplicationWithdrawRequestTaskActionPayload = {
      payloadType: 'PERMIT_REVOCATION_WITHDRAW_APPLICATION_PAYLOAD',
      reason: state.reason,
      files: state.withdrawFiles,
    };

    return this.tasksService
      .processRequestTaskActionUsingPOST({
        requestTaskActionType: 'PERMIT_REVOCATION_WITHDRAW_APPLICATION',
        requestTaskId: state.requestTaskId,
        requestTaskActionPayload: payload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        tap(() => {
          this.setState(state);
        }),
      );
  }

  postSaveCessation(value: PermitCessation, status: boolean) {
    return this.tasksService
      .processRequestTaskActionUsingPOST({
        requestTaskActionType: 'PERMIT_REVOCATION_SAVE_CESSATION',
        requestTaskId: this.getState().requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_REVOCATION_SAVE_CESSATION_PAYLOAD',
          cessation: value,
          cessationCompleted: status,
        } as PermitSaveCessationRequestTaskActionPayload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        tap(() =>
          this.setState({
            ...this.getState(),
            cessation: value,
            cessationCompleted: status,
          }),
        ),
      );
  }

  createBaseFileDownloadUrl() {
    return this.getState().requestActionId
      ? `/permit-revocation/action/${this.getState().requestActionId}/file-download/attachment/`
      : `/permit-revocation/${this.getState().requestTaskId}/file-download/attachment/`;
  }
}

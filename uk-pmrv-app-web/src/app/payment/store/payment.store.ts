import { Inject, Injectable } from '@angular/core';

import {
  distinctUntilChanged,
  filter,
  interval,
  map,
  Observable,
  pluck,
  skipWhile,
  Subscription,
  switchMapTo,
  tap,
} from 'rxjs';

import { LOCAL_STORAGE } from '@core/services/local-storage';
import { Store } from '@core/store';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/concurrency-error';

import {
  PaymentCancelRequestTaskActionPayload,
  PaymentMarkAsReceivedRequestTaskActionPayload,
  PaymentsService,
  RequestTaskActionEmptyPayload,
  TasksService,
} from 'pmrv-api';

import { catchTaskReassignedBadRequest } from '../../error/business-errors';
import { ConcurrencyErrorService } from '../../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode } from '../../error/not-found-error';
import { initialState, PaymentState } from './payment.state';

@Injectable({ providedIn: 'root' })
export class PaymentStore extends Store<PaymentState> {
  private storage$ = interval(2000).pipe(
    switchMapTo(this),
    pluck('requestTaskId'),
    skipWhile((requestTaskId) => !requestTaskId),
    map((requestTaskId) => this.localStorage.getItem(`payment/${requestTaskId}`)),
    distinctUntilChanged(),
    filter((state) => state && state !== JSON.stringify(this.getState())),
    map((state) => JSON.parse(state)),
  );
  private localStorageSubscription: Subscription;

  constructor(
    private readonly tasksService: TasksService,
    private readonly paymentsService: PaymentsService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
    @Inject(LOCAL_STORAGE) private readonly localStorage: Storage,
  ) {
    super(initialState);
  }

  listenToStorage(): void {
    this.localStorageSubscription = this.storage$.subscribe((state) => this.setState(state));
  }

  stopListeningToStorage(): void {
    this.localStorageSubscription.unsubscribe();
  }

  setState(state: PaymentState): void {
    if (state.requestTaskId) {
      this.localStorage.setItem(`payment/${state.requestTaskId}`, JSON.stringify(state));
    }
    super.setState(state);
  }

  get isEditable$(): Observable<boolean> {
    return this.pipe(pluck('isEditable'));
  }

  postMarkAsPaid(state: PaymentState) {
    return this.tasksService
      .processRequestTaskActionUsingPOST({
        requestTaskActionType: 'PAYMENT_MARK_AS_PAID',
        requestTaskId: this.getState().requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'EMPTY_PAYLOAD',
        } as RequestTaskActionEmptyPayload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        tap(() => this.setState(state)),
      );
  }

  postTrackPaymentAsPaid(data: any) {
    return this.tasksService
      .processRequestTaskActionUsingPOST({
        requestTaskActionType: 'PAYMENT_MARK_AS_RECEIVED',
        requestTaskId: this.getState().requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PAYMENT_MARK_AS_RECEIVED_PAYLOAD',
          ...data,
        } as PaymentMarkAsReceivedRequestTaskActionPayload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      );
  }

  postTrackPaymentCancel(data: any) {
    return this.tasksService
      .processRequestTaskActionUsingPOST({
        requestTaskActionType: 'PAYMENT_CANCEL',
        requestTaskId: this.getState().requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PAYMENT_CANCEL_PAYLOAD',
          ...data,
        } as PaymentCancelRequestTaskActionPayload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      );
  }

  postCreateCardPayment(requestTaskId: number) {
    return this.paymentsService
      .createCardPaymentUsingPOST(requestTaskId)
      .pipe(
        catchTaskReassignedBadRequest(() =>
          this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      );
  }

  postProcessExistingCardPayment(requestTaskId: number) {
    return this.paymentsService
      .processExistingCardPaymentUsingPOST(requestTaskId)
      .pipe(
        catchTaskReassignedBadRequest(() =>
          this.concurrencyErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      );
  }
}

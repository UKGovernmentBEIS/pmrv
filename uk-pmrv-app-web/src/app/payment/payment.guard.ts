import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanDeactivate } from '@angular/router';

import { mapTo, tap } from 'rxjs';

import { taskNotFoundError } from '@shared/errors/concurrency-error';
import { SharedStore } from '@shared/store/shared.store';

import { TasksService } from 'pmrv-api';

import { ConcurrencyErrorService } from '../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode } from '../error/not-found-error';
import { PaymentStore } from './store/payment.store';

@Injectable({ providedIn: 'root' })
export class PaymentGuard implements CanActivate, CanDeactivate<any> {
  constructor(
    private readonly store: PaymentStore,
    private readonly sharedStore: SharedStore,
    private readonly tasksService: TasksService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): any {
    return this.tasksService.getTaskItemInfoByIdUsingGET(Number(route.paramMap.get('taskId'))).pipe(
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      tap((requestTaskItem) => {
        this.store.reset();
        this.store.setState({
          ...this.store.getState(),
          requestTaskId: requestTaskItem.requestTask.id,
          requestId: requestTaskItem.requestInfo.id,
          requestTaskItem: {
            requestTask: requestTaskItem.requestTask,
            requestInfo: requestTaskItem.requestInfo,
            userAssignCapable: requestTaskItem.userAssignCapable,
            allowedRequestTaskActions: requestTaskItem.allowedRequestTaskActions,
          },
          isEditable:
            requestTaskItem.allowedRequestTaskActions.includes('PAYMENT_MARK_AS_PAID') ||
            requestTaskItem.allowedRequestTaskActions.includes('PAYMENT_MARK_AS_RECEIVED'),
          paymentDetails: requestTaskItem.requestTask.payload,
        });
        this.store.listenToStorage();

        this.sharedStore.reset();
        this.sharedStore.setState({ ...this.sharedStore.getState(), accountId: requestTaskItem.requestInfo.accountId });
      }),
      mapTo(true),
    );
  }

  canDeactivate(): boolean {
    this.store.stopListeningToStorage();
    this.sharedStore.reset();
    return true;
  }
}

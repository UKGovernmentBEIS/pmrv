import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanDeactivate } from '@angular/router';

import { mapTo, tap } from 'rxjs';

import { taskNotFoundError } from '@shared/errors/concurrency-error';
import { SharedStore } from '@shared/store/shared.store';

import { RequestTaskItemDTO, TasksService } from 'pmrv-api';

import { ConcurrencyErrorService } from '../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode } from '../error/not-found-error';
import { PermitRevocationStore } from './store/permit-revocation-store';

@Injectable({
  providedIn: 'root',
})
export class PermitRevocationTaskGuard implements CanActivate, CanDeactivate<any> {
  constructor(
    private store: PermitRevocationStore,
    private readonly sharedStore: SharedStore,
    private readonly tasksService: TasksService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): any {
    return this.tasksService.getTaskItemInfoByIdUsingGET(Number(route.paramMap.get('taskId'))).pipe(
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      tap((requestTaskItem: RequestTaskItemDTO) => {
        this.store.reset();
        this.store.setState({
          ...this.store.getState(),
          requestTaskId: Number(route.paramMap.get('taskId')),
          requestTaskType: requestTaskItem.requestTask.type,
          daysRemaining: requestTaskItem.requestTask.daysRemaining,
          assignee: {
            assigneeUserId: requestTaskItem.requestTask.assigneeUserId,
            assigneeFullName: requestTaskItem.requestTask.assigneeFullName,
          },
          isEditable: [
            'PERMIT_REVOCATION_SAVE_APPLICATION',
            'PERMIT_REVOCATION_CANCEL_APPLICATION',
            'PERMIT_REVOCATION_WITHDRAW_APPLICATION',
            'PERMIT_REVOCATION_SAVE_CESSATION',
          ].some((type: any) => requestTaskItem.allowedRequestTaskActions.includes(type)),
          assignable: requestTaskItem.requestTask.assignable,
          isRequestTask: true,

          ...requestTaskItem.requestTask.payload,

          allowedRequestTaskActions: requestTaskItem.allowedRequestTaskActions,
          userAssignCapable: requestTaskItem.userAssignCapable,

          requestId: requestTaskItem.requestInfo.id,
          requestType: requestTaskItem.requestInfo.type,
          competentAuthority: requestTaskItem.requestInfo.competentAuthority,
          accountId: requestTaskItem.requestInfo.accountId,
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

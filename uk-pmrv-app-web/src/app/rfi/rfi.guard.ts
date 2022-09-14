import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanDeactivate, RouterStateSnapshot } from '@angular/router';

import { mapTo, tap } from 'rxjs';

import { taskNotFoundError } from '@shared/errors/concurrency-error';
import { SharedStore } from '@shared/store/shared.store';

import { TasksService } from 'pmrv-api';

import { ConcurrencyErrorService } from '../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode } from '../error/not-found-error';
import { RfiStore } from './store/rfi.store';

@Injectable({ providedIn: 'root' })
export class RfiGuard implements CanActivate, CanDeactivate<any> {
  constructor(
    private readonly store: RfiStore,
    private readonly sharedStore: SharedStore,
    private readonly tasksService: TasksService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, routerState: RouterStateSnapshot): any {
    return this.tasksService.getTaskItemInfoByIdUsingGET(Number(route.paramMap.get('taskId'))).pipe(
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      tap((requestTaskItem) => {
        // do not reset the state when a download file tab opens, as rfi state is not saved in the backend
        if (!routerState.url.includes('file-download')) {
          this.store.reset();
          const state = this.store.getState();
          this.store.setState({
            ...state,
            requestTaskId: requestTaskItem.requestTask.id,
            assignee: {
              assigneeUserId: requestTaskItem.requestTask.assigneeUserId,
              assigneeFullName: requestTaskItem.requestTask.assigneeFullName,
            },
            requestId: requestTaskItem.requestInfo.id,
            accountId: requestTaskItem.requestInfo.accountId,
            ...requestTaskItem.requestTask.payload,
            isEditable: ['RFI_SUBMIT', 'RFI_RESPONSE_SUBMIT', 'RFI_CANCEL'].some((perm) =>
              requestTaskItem.allowedRequestTaskActions.includes(perm as any),
            ),
            assignable: requestTaskItem.requestTask.assignable,
            daysRemaining: requestTaskItem.requestTask.daysRemaining,
            requestType: requestTaskItem.requestInfo.type,
            allowedRequestTaskActions: requestTaskItem.allowedRequestTaskActions,
          });
        }
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

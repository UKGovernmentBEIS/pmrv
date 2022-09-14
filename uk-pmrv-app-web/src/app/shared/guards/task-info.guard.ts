import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanDeactivate, Resolve } from '@angular/router';

import { mapTo, Observable, tap } from 'rxjs';

import { RequestTaskItemDTO, TasksService } from 'pmrv-api';

import { ConcurrencyErrorService } from '../../error/concurrency-error/concurrency-error.service';
import { catchNotFoundRequest, ErrorCode } from '../../error/not-found-error';
import { taskNotFoundError } from '../errors/concurrency-error';
import { SharedStore } from '../store/shared.store';

@Injectable({ providedIn: 'root' })
export class TaskInfoGuard implements CanActivate, CanDeactivate<any>, Resolve<any> {
  private info: RequestTaskItemDTO;

  constructor(
    private readonly tasksService: TasksService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
    private readonly sharedStore: SharedStore,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.tasksService.getTaskItemInfoByIdUsingGET(Number(route.paramMap.get('taskId'))).pipe(
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.concurrencyErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      tap((info) => {
        this.info = info;
        this.sharedStore.reset();
        this.sharedStore.setState({ ...this.sharedStore.getState(), accountId: info.requestInfo?.accountId });
      }),
      mapTo(true),
    );
  }

  canDeactivate(): boolean {
    this.sharedStore.reset();
    return true;
  }

  resolve(): RequestTaskItemDTO {
    return this.info;
  }
}

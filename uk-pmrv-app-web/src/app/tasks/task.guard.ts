import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, UrlTree } from '@angular/router';

import { filter, mapTo, Observable } from 'rxjs';

import { SharedStore } from '@shared/store/shared.store';

import { CommonTasksStore } from './store/common-tasks.store';

@Injectable({ providedIn: 'root' })
export class TaskGuard implements CanActivate {
  constructor(private readonly store: CommonTasksStore, private readonly sharedStore: SharedStore) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const taskId = route.params['taskId'];
    if (taskId) {
      this.store.requestedTask(taskId);
    }
    return this.store.storeInitialized$.pipe(
      filter((init) => !!init),
      mapTo(true),
    );
  }

  canDeactivate(): boolean {
    this.sharedStore.reset();
    return true;
  }
}

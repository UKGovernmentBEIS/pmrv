import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable, pluck, switchMap } from 'rxjs';

import { RequestItemsService } from 'pmrv-api';

import { waitForRfiRdeTypes } from '../../core/rfi';
import { RfiStore } from '../../store/rfi.store';

@Injectable({ providedIn: 'root' })
export class QuestionsGuard implements CanActivate {
  constructor(
    private readonly requestItemsService: RequestItemsService,
    private readonly router: Router,
    private readonly store: RfiStore,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      first(),
      pluck('requestId'),
      switchMap((requestId) => this.requestItemsService.getItemsByRequestUsingGET(requestId)),
      first(),
      map(
        (res) =>
          !res.items.some((i) => waitForRfiRdeTypes.includes(i.taskType)) ||
          this.router.parseUrl(`/rfi/${route.paramMap.get('taskId')}/not-allowed`),
      ),
    );
  }
}

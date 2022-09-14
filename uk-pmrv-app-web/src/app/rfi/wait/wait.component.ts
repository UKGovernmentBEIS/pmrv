import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import {combineLatest, first, map, of, pluck, switchMap, withLatestFrom} from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';
import { hasRequestTaskAllowedActions } from "@shared/components/related-actions/request-task-allowed-actions.map";

import { ItemDTOResponse, RequestActionInfoDTO, RequestActionsService, RequestItemsService } from 'pmrv-api';

import { RfiStore } from '../store/rfi.store';

@Component({
  selector: 'app-cancel-rfi',
  templateUrl: './wait.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class WaitComponent implements OnInit {
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
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

  readonly daysRemaining$ = this.store.pipe(pluck('daysRemaining'));
  readonly assignee$ = this.store.pipe(pluck('assignee'));
  readonly allowedRequestTaskActions$ = this.store.pipe(pluck('allowedRequestTaskActions'));
  readonly isAssignable$ = this.store.pipe(pluck('assignable'));
  hasRelatedActions$ = combineLatest([this.isAssignable$, this.allowedRequestTaskActions$]).pipe(
    map(([isAssignable, allowedRequestTaskActions]) => isAssignable || hasRequestTaskAllowedActions(allowedRequestTaskActions))
  );

  constructor(
    readonly store: RfiStore,
    private readonly requestItemsService: RequestItemsService,
    private readonly requestActionsService: RequestActionsService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit() {
    this.backLinkService.show();
  }

  cancel(): void {
    this.router.navigate(['../cancel-verify'], { relativeTo: this.route });
  }

  private sortTimeline(res: RequestActionInfoDTO[]): RequestActionInfoDTO[] {
    return res.slice().sort((a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime());
  }
}

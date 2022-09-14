import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, filter, first, map, of, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { PermitApplicationStore } from '@permit-application/store/permit-application.store';
import { BackLinkService } from '@shared/back-link/back-link.service';

import { ItemDTOResponse, RequestActionInfoDTO, RequestActionsService, RequestItemsService } from 'pmrv-api';

@Component({
  selector: 'app-permit-task-list-container',
  template: `<app-permit-task-list
    [actions$]="actions$"
    [relatedTasks$]="relatedTasks$"
    (submitEvent)="onSubmit()"
  ></app-permit-task-list>`,
  providers: [DestroySubject, BackLinkService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PermitTaskListContainerComponent implements OnInit {
  relatedTasks$ = this.store.pipe(
    filter((state) => !!state.requestId),
    switchMap((state) => {
      const requestTaskId = state.requestTaskId;
      return this.requestItemsService.getItemsByRequestUsingGET(state.requestId).pipe(
        map((response) => {
          return [response, requestTaskId];
        }),
      );
    }),
    map(([items, requestTaskId]) => {
      const taskId = requestTaskId as number;
      return (items as ItemDTOResponse)?.items.filter((item) => item.taskId !== taskId);
    }),
  );

  actions$ = this.store.pipe(
    first(),
    map(({ requestId }) => requestId),
    filter((requestId) => !!requestId),
    switchMap((requestId) => this.requestActionsService.getRequestActionsByRequestIdUsingGET(requestId)),
    first(),
    map((res) => this.sortTimeline(res)),
  );

  constructor(
    readonly store: PermitApplicationStore,
    private readonly requestItemsService: RequestItemsService,
    private readonly requestActionsService: RequestActionsService,
    private readonly destroy$: DestroySubject,
    private readonly backLinkService: BackLinkService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.store.setState({
      ...this.store.getState(),
      returnUrl: window.history.state?.returnUrl ? window.history.state?.returnUrl : this.store.value.returnUrl,
    });

    combineLatest([this.store.isEditable$, this.store.select('returnUrl')])
      .pipe(
        takeUntil(this.destroy$),
        switchMap(([editable, returnUrl]) => {
          if (!editable) {
            this.backLinkService.show(returnUrl ?? null);
          }
          return of();
        }),
      )
      .subscribe();
  }

  private sortTimeline(res: RequestActionInfoDTO[]): RequestActionInfoDTO[] {
    return res.slice().sort((a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime());
  }

  onSubmit() {
    this.router.navigate(['summary'], { relativeTo: this.route });
  }
}

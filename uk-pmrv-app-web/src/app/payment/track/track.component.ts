import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, pluck, switchMap, withLatestFrom } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';
import { hasRequestTaskAllowedActions } from "@shared/components/related-actions/request-task-allowed-actions.map";

import { RequestActionInfoDTO, RequestActionsService, RequestItemsService } from 'pmrv-api';

import { contentMap, headingMap, mapTrackPaymentToPaymentDetails } from '../core/payment.map';
import { PaymentStore } from '../store/payment.store';

@Component({
  selector: 'app-track',
  templateUrl: './track.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class TrackComponent implements OnInit {
  isAssignable$ = this.store.pipe(
    first(),
    map((state) => state.requestTaskItem.requestTask.assignable),
  );
  allowedRequestTaskActions$ = this.store.pipe(
    first(),
    map((state) => state.requestTaskItem.allowedRequestTaskActions),
  );
  hasRelatedActions$ = combineLatest([this.isAssignable$, this.allowedRequestTaskActions$]).pipe(
    map(([isAssignable, allowedRequestTaskActions]) => isAssignable || hasRequestTaskAllowedActions(allowedRequestTaskActions))
  );
  taskId$ = this.store.pipe(
    first(),
    map((state) => state.requestTaskItem.requestTask.id),
  );
  details$ = this.store.pipe(
    first(),
    map((state) => mapTrackPaymentToPaymentDetails(state)),
  );
  type$ = this.store.pipe(
    first(),
    map((state) => state.requestTaskItem.requestTask.type),
  );

  readonly requestTaskItem$ = this.store.pipe(pluck('requestTaskItem'));

  readonly relatedTasks$ = this.store.pipe(
    first(),
    switchMap((state) => this.requestItemsService.getItemsByRequestUsingGET(state.requestTaskItem.requestInfo.id)),
    withLatestFrom(this.store),
    map(([items, state]) => items?.items.filter((item) => item.taskId !== state?.requestTaskId)),
  );
  actions$ = this.store.pipe(
    first(),
    switchMap((state) =>
      this.requestActionsService.getRequestActionsByRequestIdUsingGET(state.requestTaskItem.requestInfo.id),
    ),
    first(),
    map((res) => this.sortTimeline(res)),
  );

  readonly headingMap = headingMap;
  readonly contentMap = contentMap;
  navigationState = { returnUrl: this.router.url };

  constructor(
    readonly store: PaymentStore,
    private readonly backLinkService: BackLinkService,
    private readonly requestItemsService: RequestItemsService,
    private readonly requestActionsService: RequestActionsService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  received(): void {
    this.router.navigate(['mark-paid'], { relativeTo: this.route });
  }

  cancel(): void {
    this.router.navigate(['cancel'], { relativeTo: this.route });
  }

  private sortTimeline(res: RequestActionInfoDTO[]): RequestActionInfoDTO[] {
    return res.slice().sort((a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime());
  }
}

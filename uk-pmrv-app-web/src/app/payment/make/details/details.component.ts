import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, of, pluck, switchMap, withLatestFrom } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';
import { hasRequestTaskAllowedActions } from "@shared/components/related-actions/request-task-allowed-actions.map";

import {
  ItemDTOResponse,
  PaymentMakeRequestTaskPayload,
  RequestActionInfoDTO,
  RequestActionsService,
  RequestItemsService,
} from 'pmrv-api';

import { contentMap, headingMap } from '../../core/payment.map';
import { PaymentStore } from '../../store/payment.store';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class DetailsComponent implements OnInit {
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

  readonly paymentDetails$ = this.store.pipe(
    first(),
    map((state) => state.paymentDetails as PaymentMakeRequestTaskPayload),
  );
  type$ = this.store.pipe(
    first(),
    map((state) => state.requestTaskItem.requestTask.type),
  );

  readonly requestTaskItem$ = this.store.pipe(pluck('requestTaskItem'));
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

  readonly headingMap = headingMap;
  readonly contentMap = contentMap;

  constructor(
    readonly store: PaymentStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly backLinkService: BackLinkService,
    private readonly requestItemsService: RequestItemsService,
    private readonly requestActionsService: RequestActionsService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  makePayment(): void {
    this.router.navigate(['../options'], { relativeTo: this.route });
  }

  private sortTimeline(res: RequestActionInfoDTO[]): RequestActionInfoDTO[] {
    return res.slice().sort((a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime());
  }
}

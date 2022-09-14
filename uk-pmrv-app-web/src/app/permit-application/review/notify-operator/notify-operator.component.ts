import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { first, map, pluck, Subject, switchMap, tap } from 'rxjs';

import { RequestItemsService } from 'pmrv-api';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { resolveDeterminationStatus } from '../review';

@Component({
  selector: 'app-permit-issuance-notify-operator',
  template: `<div class="govuk-grid-row">
    <div class="govuk-grid-column-two-thirds">
      <app-notify-operator
        [taskId]="taskId$ | async"
        [accountId]="accountId$ | async"
        requestTaskActionType="PERMIT_ISSUANCE_NOTIFY_OPERATOR_FOR_DECISION"
        [pendingRfi]="pendingRfi$ | async"
        [pendingRde]="pendingRde$ | async"
        [confirmationMessage]="confirmationMessage$ | async"
      ></app-notify-operator>
    </div>
  </div> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotifyOperatorComponent implements OnInit {
  constructor(
    private readonly store: PermitApplicationStore,
    private readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
    private readonly requestItemsService: RequestItemsService,
  ) {}

  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  readonly accountId$ = this.store.pipe(pluck('accountId'));
  readonly confirmationMessage$ = this.store.pipe(
    map((state) => `${state.permitType} permit ${resolveDeterminationStatus(state)}`),
  );

  pendingRfi$: Subject<boolean> = new Subject<boolean>();
  pendingRde$: Subject<boolean> = new Subject<boolean>();

  ngOnInit(): void {
    this.store
      .pipe(
        first(),
        pluck('requestId'),
        switchMap((requestId) => this.requestItemsService.getItemsByRequestUsingGET(requestId)),
        first(),
        tap((res) =>
          this.pendingRfi$.next(
            res.items.some((i) =>
              ['PERMIT_ISSUANCE_WAIT_FOR_RFI_RESPONSE', 'PERMIT_VARIATION_WAIT_FOR_RFI_RESPONSE'].includes(i.taskType),
            ),
          ),
        ),
        tap((res) =>
          this.pendingRde$.next(
            res.items.some((i) =>
              ['PERMIT_ISSUANCE_WAIT_FOR_RDE_RESPONSE', 'PERMIT_VARIATION_WAIT_FOR_RDE_RESPONSE'].includes(i.taskType),
            ),
          ),
        ),
      )
      .subscribe();
  }
}

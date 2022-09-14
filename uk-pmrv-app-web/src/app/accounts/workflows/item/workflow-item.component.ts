import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';

import { RequestActionInfoDTO, RequestActionsService, RequestItemsService, RequestsService } from 'pmrv-api';

import { workflowDetailsTypesMap, workflowStatusesTagMap } from '../workflowMap';

@Component({
  selector: 'app-workflow-item',
  templateUrl: './workflow-item.component.html',
  styles: [
    `
      span.search-results-list_item_status {
        float: right;
      }
    `,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService, DestroySubject],
})
export class WorkflowItemComponent implements OnInit {
  private requestId$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('request-id')));

  navigationState = { returnUrl: this.router.url };
  readonly workflowStatusesTagMap = workflowStatusesTagMap;
  readonly workflowDetailsTypesMap = workflowDetailsTypesMap;

  readonly requestInfo$ = this.requestId$.pipe(
    switchMap((requestId) => this.requestsService.getRequestDetailsByIdUsingGET(requestId)),
  );
  readonly relatedTasks$ = this.requestId$.pipe(
    switchMap((requestId) => this.requestItemsService.getItemsByRequestUsingGET(requestId)),
    map((items) => items.items),
  );
  readonly actions$ = this.requestId$.pipe(
    switchMap((requestId) => this.requestActionsService.getRequestActionsByRequestIdUsingGET(requestId)),
    map((res) => this.sortTimeline(res)),
  );

  constructor(
    private readonly requestsService: RequestsService,
    private readonly requestItemsService: RequestItemsService,
    private readonly requestActionsService: RequestActionsService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly backLinkService: BackLinkService,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.route.paramMap
      .pipe(takeUntil(this.destroy$))
      .subscribe((paramMap) => this.backLinkService.show(`/accounts/${paramMap.get('accountId')}`, 'workflows'));
  }

  private sortTimeline(res: RequestActionInfoDTO[]): RequestActionInfoDTO[] {
    return res.slice().sort((a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime());
  }
}

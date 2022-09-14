import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import {
  BehaviorSubject,
  combineLatest,
  distinctUntilChanged,
  Observable,
  pluck,
  shareReplay,
  switchMap,
  takeUntil,
  tap,
} from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';

import {
  AccountDetailsDTO,
  RequestDetailsDTO,
  RequestDetailsSearchResults,
  RequestSearchCriteria,
  RequestsService,
} from 'pmrv-api';

import { workflowStatusesMap, workflowStatusesTagMap, workflowTypesMap } from './workflowMap';

@Component({
  selector: 'app-workflows',
  templateUrl: './workflows.component.html',
  styles: [
    `
      span.search-results-list_item_status {
        float: right;
      }
    `,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class WorkflowsComponent implements OnInit {
  readonly workflowTypesMap = workflowTypesMap;
  readonly workflowStatusesMap = workflowStatusesMap;
  readonly workflowStatusesTagMap = workflowStatusesTagMap;

  readonly pageSize = 30;
  page$ = new BehaviorSubject<number>(1);
  pageCount$: Observable<number>;

  accountId$: Observable<number>;
  workflowResults$: Observable<RequestDetailsSearchResults>;
  workflows$: Observable<RequestDetailsDTO[]>;
  showPagination$ = new BehaviorSubject<boolean>(true);

  searchForm: FormGroup = this.fb.group({
    workflowTypes: [[], { updateOn: 'change' }],
    workflowStatuses: [[], { updateOn: 'change' }],
  });

  private searchTypes$ = new BehaviorSubject<RequestDetailsDTO['requestType'][]>([]);
  private searchStatuses$ = new BehaviorSubject<RequestSearchCriteria['status'][]>(null);

  constructor(
    private readonly route: ActivatedRoute,
    private readonly fb: FormBuilder,
    private readonly requestsService: RequestsService,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.accountId$ = (
      this.route.data as Observable<{
        account: AccountDetailsDTO;
      }>
    ).pipe(pluck('account', 'id'));

    this.searchForm
      .get('workflowTypes')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => this.searchTypes$.next(this.searchForm.get('workflowTypes').value));

    this.searchForm
      .get('workflowStatuses')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => this.searchStatuses$.next(this.searchForm.get('workflowStatuses').value));

    this.workflowResults$ = combineLatest([
      this.accountId$,
      this.searchTypes$,
      this.searchStatuses$,
      this.page$.pipe(distinctUntilChanged()),
    ]).pipe(
      switchMap(([accountId, types, statuses, page]) =>
        this.requestsService.getRequestDetailsByAccountIdUsingPOST({
          accountId: accountId,
          category: 'PERMIT',
          requestTypes: types,
          status: statuses?.length > 1 ? null : statuses?.[0],
          page: page - 1,
          pageSize: this.pageSize,
        }),
      ),
      tap((workflows) => {
        this.showPagination$.next(workflows.total > this.pageSize);
      }),
      shareReplay({ bufferSize: 1, refCount: true }),
    );

    this.workflows$ = this.workflowResults$.pipe(pluck('requestDetails'));
    this.pageCount$ = this.workflowResults$.pipe(pluck('total'));
  }

  keyValueWithoutSort() {
    return 0;
  }
}

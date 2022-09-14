import { ChangeDetectionStrategy, Component, ViewEncapsulation } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import {
  BehaviorSubject,
  combineLatest,
  distinctUntilChanged,
  filter,
  first,
  map,
  Observable,
  pluck,
  shareReplay,
  Subject,
  switchMap,
  tap,
} from 'rxjs';

import { AuthService } from '@core/services/auth.service';

import { GovukTableColumn } from 'govuk-components';

import {
  ItemDTO,
  ItemDTOResponse,
  ItemsAssignedToMeService,
  ItemsAssignedToOthersService,
  UnassignedItemsService,
} from 'pmrv-api';

/* eslint-disable @angular-eslint/use-component-view-encapsulation */
@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styles: [
    `
      .js-enabled .govuk-tabs__panel {
        border: 0px;
      }
    `,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None,
})
export class DashboardComponent {
  tableColumns: GovukTableColumn<ItemDTO>[] = [
    { field: 'taskType', header: 'Task', isSortable: true },
    { field: 'daysRemaining', header: 'Days remaining', isSortable: true },
    { field: 'permitId', header: 'Permit ID', isSortable: true },
    { field: 'accountName', header: 'Installation', isSortable: true },
    { field: 'leName', header: `Operator`, isSortable: true },
  ];
  currentPage$ = new Subject<number>();
  currentTab$ = new BehaviorSubject<string>(null);
  showPagination$ = new BehaviorSubject<boolean>(true);

  readonly pageSize = 10;

  data$: Observable<{ items: ItemDTO[]; totalItems: number }> = combineLatest([
    combineLatest([
      this.currentPage$,
      this.activatedRoute.fragment.pipe(
        tap((fragment) => {
          if (fragment != 'main-content') {
            this.router.navigate(['.'], { preserveFragment: true, relativeTo: this.activatedRoute });
          }
        }),
      ),
    ]).pipe(
      filter(([page]) => page === 1),
      map(([, currentTab]) => currentTab),
      distinctUntilChanged(),
    ),
    this.currentPage$,
    this.currentTab$,
  ]).pipe(
    switchMap(([, currentPage, currentTab]): Observable<ItemDTOResponse> => {
      switch (currentTab) {
        case 'assigned-to-others':
          return this.itemsAssignedToOthersService.getAssignedToOthersItemsUsingGET(currentPage - 1, this.pageSize);
        case 'unassigned':
          return this.unassignedItemsService.getUnassignedItemsUsingGET(currentPage - 1, this.pageSize);
        case 'assigned-to-me':
        default:
          return this.itemsAssignedToMeService.getAssignedItemsUsingGET(currentPage - 1, this.pageSize);
      }
    }),
    tap((tasks) => {
      this.showPagination$.next(tasks.totalItems > this.pageSize);
    }),
    map((tasks) => ({
      items: tasks.items,
      totalItems: tasks.totalItems,
    })),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  isOperator$: Observable<boolean> = this.authService.userStatus.pipe(
    first(),
    pluck('roleType'),
    map((roleType) => roleType === 'OPERATOR'),
  );

  constructor(
    private readonly itemsAssignedToMeService: ItemsAssignedToMeService,
    private readonly itemsAssignedToOthersService: ItemsAssignedToOthersService,
    private readonly unassignedItemsService: UnassignedItemsService,
    private readonly activatedRoute: ActivatedRoute,
    private readonly router: Router,
    readonly authService: AuthService,
  ) {}

  addAnotherInstallation(): void {
    this.router.navigate(['/'], { state: { addAnotherInstallation: true } });
  }

  selectedTab(selected: string) {
    this.currentTab$.next(selected);
  }
}

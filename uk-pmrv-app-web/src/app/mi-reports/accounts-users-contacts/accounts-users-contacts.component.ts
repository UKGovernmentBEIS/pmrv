import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, map, Observable, shareReplay, Subject, switchMap } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';

import { GovukTableColumn } from 'govuk-components';

import { AccountsUsersContactsMiReportResult, AccountUserContact, MiReportsService } from 'pmrv-api';

import { createTablePage, pageSize } from '../core/mi-report';

@Component({
  selector: 'app-accounts-users-contacts',
  templateUrl: './accounts-users-contacts.component.html',
  providers: [BackLinkService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountsUsersContactsComponent implements OnInit {
  readonly pageSize = pageSize;
  reportItems$: Observable<AccountUserContact[]>;
  pageItems$: Observable<AccountUserContact[]>;
  totalNumOfItems$: Observable<number>;

  currentPage$ = new BehaviorSubject<number>(1);
  generateReport$ = new Subject<void>();

  tableColumns: GovukTableColumn<AccountUserContact>[] = [
    { field: 'accountType', header: 'Account Type', isSortable: false },
    { field: 'accountId', header: 'Account Id', isSortable: false },
    { field: 'accountName', header: 'Account Name', isSortable: false },
    { field: 'accountStatus', header: 'Account Status', isSortable: false },
    { field: 'permitId', header: 'Permit Id', isSortable: false },
    { field: 'permitType', header: 'Permit Type', isSortable: false },
    { field: 'legalEntityName', header: 'Legal Entity Name', isSortable: false },
    { field: 'isPrimaryContact', header: 'Primary Contact', isSortable: false },
    { field: 'isSecondaryContact', header: 'Secondary Contact', isSortable: false },
    { field: 'isFinancialContact', header: 'Financial Contact', isSortable: false },
    { field: 'isServiceContact', header: 'Service Contact', isSortable: false },
    { field: 'authorityStatus', header: 'Authority Status', isSortable: false },
    { field: 'name', header: 'Name', isSortable: false },
    { field: 'telephone', header: 'Telephone', isSortable: false },
    { field: 'lastLogon', header: 'Last Logon', isSortable: false },
    { field: 'email', header: 'Email', isSortable: false },
    { field: 'role', header: 'Role', isSortable: false },
  ];

  constructor(
    private readonly miReportsService: MiReportsService,
    private readonly backlinkService: BackLinkService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.backlinkService.show();

    this.reportItems$ = this.generateReport$.pipe(
      switchMap(() => this.miReportsService.generateReportUsingPOST({ reportType: 'LIST_OF_ACCOUNTS_USERS_CONTACTS' })),
      map((res: AccountsUsersContactsMiReportResult) => res.payload),
      shareReplay({ bufferSize: 1, refCount: true }),
    );

    this.pageItems$ = combineLatest([this.reportItems$, this.currentPage$]).pipe(
      map(([items, currentPage]) => createTablePage(currentPage, this.pageSize, items)),
    );
    this.totalNumOfItems$ = this.reportItems$.pipe(map((items) => items.length));
  }

  generateReport() {
    this.generateReport$.next();
    this.router.navigate([], { relativeTo: this.route, queryParams: { page: 1 }, queryParamsHandling: 'merge' });
  }
}

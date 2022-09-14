import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import {
  BehaviorSubject,
  combineLatest,
  distinctUntilChanged,
  Observable,
  pluck,
  shareReplay,
  switchMap,
  takeUntil,
} from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { AccountDetailsDTO, AccountInfoDTO, AccountSearchResults, AccountsService, UserStatusDTO } from 'pmrv-api';

import { AuthService } from '../core/services/auth.service';
import { DestroySubject } from '../core/services/destroy-subject.service';

@Component({
  selector: 'app-accounts',
  templateUrl: './accounts.component.html',
  styles: [
    `
      span.search-results-list_item_status {
        float: right;
        white-space: nowrap;
      }
      span.wrap-text {
        word-break: break-all;
      }
    `,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class AccountsComponent implements OnInit {
  readonly pageSize = 30;
  isSummaryDisplayed = new BehaviorSubject<boolean>(false);
  page$ = new BehaviorSubject<number>(1);
  totalItems$: Observable<number>;

  accountResults$: Observable<AccountSearchResults>;
  accounts$: Observable<AccountInfoDTO[]>;
  userRoleType$: Observable<UserStatusDTO['roleType']>;

  searchForm: FormGroup = this.fb.group({
    term: [
      null,
      {
        validators: [
          GovukValidators.minLength(3, 'Enter at least 3 characters'),
          GovukValidators.maxLength(256, 'Enter up to 256 characters'),
        ],
      },
    ],
    accountTypes: [[], { updateOn: 'change' }],
  });

  private term$ = new BehaviorSubject<string>(this.getTermQueryParam());
  private accountTypes$ = new BehaviorSubject<AccountDetailsDTO['accountType'][]>(this.getAccountTypesQueryParam());

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly fb: FormBuilder,
    private readonly authService: AuthService,
    private readonly accountsService: AccountsService,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.userRoleType$ = this.authService.userStatus.pipe(pluck('roleType'));

    // populate form with initial values
    this.searchForm.get('term').setValue(this.getTermQueryParam());
    this.searchForm.get('accountTypes').setValue(this.getAccountTypesQueryParam());

    this.searchForm
      .get('accountTypes')
      .valueChanges.pipe(takeUntil(this.destroy$))
      .subscribe(() => this.accountTypes$.next(this.searchForm.get('accountTypes').value));

    this.updateUrlParamsOnFormChanges();

    this.accountResults$ = combineLatest([
      this.term$,
      this.accountTypes$,
      this.page$.pipe(distinctUntilChanged()),
    ]).pipe(
      switchMap(([term, accountTypes, page]) =>
        this.accountsService.getCurrentUserAccountsUsingGET(
          page - 1,
          this.pageSize,
          term ? term : null,
          accountTypes?.length > 1 ? null : accountTypes?.[0],
        ),
      ),
      shareReplay({ bufferSize: 1, refCount: true }),
    );

    this.accounts$ = this.accountResults$.pipe(pluck('accounts'));
    this.totalItems$ = this.accountResults$.pipe(pluck('total'));
  }

  onSearch() {
    if (this.searchForm.valid) {
      this.term$.next(this.searchForm.get('term').value);
    } else {
      this.isSummaryDisplayed.next(true);
    }
  }

  navigateToAccount(accountId: number): void {
    this.router.navigate([accountId], {
      relativeTo: this.route,
      state: { accountSearchParams: this.route.snapshot.queryParams },
    });
  }

  private updateUrlParamsOnFormChanges() {
    this.searchForm.valueChanges.pipe(takeUntil(this.destroy$)).subscribe(() => {
      this.router.navigate([], {
        relativeTo: this.route,
        queryParams: {
          term: this.searchForm.get('term').value,
          accountTypes: this.searchForm.get('accountTypes').value,
          page: 1,
        },
        queryParamsHandling: 'merge',
      });
    });
  }

  private getTermQueryParam(): string {
    return this.route.snapshot.queryParamMap.get('term');
  }

  private getAccountTypesQueryParam(): ('AVIATION' | 'INSTALLATION')[] {
    return this.route.snapshot.queryParamMap.getAll('accountTypes') as AccountDetailsDTO['accountType'][];
  }
}

import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import {
  combineLatest,
  distinctUntilChanged,
  filter,
  map,
  merge,
  Observable,
  pluck,
  ReplaySubject,
  shareReplay,
  Subject,
  switchMap,
  switchMapTo,
  tap,
} from 'rxjs';

import { GovukSelectOption, GovukTableColumn } from 'govuk-components';

import {
  AccountContactInfoDTO,
  CaSiteContactsService,
  RegulatorAuthoritiesService,
  RegulatorUserAuthorityInfoDTO
} from 'pmrv-api';

import { catchBadRequest, ErrorCode } from '../../error/business-errors';
import { ConcurrencyErrorService } from '../../error/concurrency-error/concurrency-error.service';
import { UserFullNamePipe } from '../../shared/pipes/user-full-name.pipe';
import { savePartiallyNotFoundSiteContactError } from '../errors/concurrency-error';

type TableData = AccountContactInfoDTO & { user: RegulatorUserAuthorityInfoDTO; type: 'Installation' };

@Component({
  selector: 'app-site-contacts',
  templateUrl: './site-contacts.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [UserFullNamePipe],
})
export class SiteContactsComponent implements OnInit {
  page$ = new ReplaySubject<number>(1);
  count$: Observable<number>;
  columns: GovukTableColumn<TableData>[] = [
    { field: 'accountName', header: 'Permit holding account', isHeader: true },
    { field: 'type', header: 'Type' },
    { field: 'user', header: 'Assigned to' },
  ];
  tableData$: Observable<TableData[]>;
  isEditable$: Observable<boolean>;
  readonly pageSize = 50;
  form = this.fb.group({ siteContacts: this.fb.array([]) });
  assigneeOptions$: Observable<GovukSelectOption<string>[]>;
  refresh$ = new Subject<void>();

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly siteContactsService: CaSiteContactsService,
    private readonly fullNamePipe: UserFullNamePipe,
    private readonly regulatorAuthoritiesService: RegulatorAuthoritiesService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  ngOnInit(): void {
    const activatedTab$ = this.route.fragment.pipe(filter((fragment) => fragment === 'site-contacts'));

    const regulators$ = merge(activatedTab$, this.refresh$).pipe(
      switchMapTo(this.regulatorAuthoritiesService.getCaRegulatorsUsingGET()),
      pluck('caUsers'),
      shareReplay({ bufferSize: 1, refCount: false }),
    );

    const contacts$ = combineLatest([
      merge(this.refresh$.pipe(switchMapTo(this.page$)), this.page$.pipe(distinctUntilChanged())),
      activatedTab$,
    ]).pipe(
      switchMap(([page]) => this.siteContactsService.getCaSiteContactsUsingGET(page - 1, this.pageSize)),
      shareReplay({ bufferSize: 1, refCount: true }),
    );

    this.count$ = contacts$.pipe(pluck('totalItems'));

    this.assigneeOptions$ = regulators$.pipe(
      map((regulators: RegulatorUserAuthorityInfoDTO[]) =>
        regulators.filter((reg) => reg.authorityStatus === 'ACTIVE'),
      ),
      map((users) =>
        [{ text: 'Unassigned', value: null }].concat(
          users.map((user) => ({ text: this.fullNamePipe.transform(user), value: user.userId })),
        ),
      ),
    );
    this.isEditable$ = contacts$.pipe(pluck('editable'));
    this.tableData$ = combineLatest([
      contacts$.pipe(
        map((response) => response.contacts.slice().sort((a, b) => a.accountName.localeCompare(b.accountName))),
      ),
      regulators$,
    ]).pipe(
      map(([contacts, users]) =>
        contacts.map(
          (contact): TableData => ({
            ...contact,
            user: users.find((user) => user.userId === contact.userId),
            type: 'Installation',
          }),
        ),
      ),
      tap((contacts) =>
        this.form.setControl(
          'siteContacts',
          this.fb.array(contacts.map(({ accountId, userId }) => this.fb.group({ accountId, userId }))),
        ),
      ),
    );
  }

  onSave(): void {
    this.siteContactsService
      .updateCaSiteContactsUsingPOST(this.form.get('siteContacts').value)
      .pipe(
        catchBadRequest([ErrorCode.AUTHORITY1003, ErrorCode.ACCOUNT1004], () =>
          this.concurrencyErrorService.showError(savePartiallyNotFoundSiteContactError),
        ),
      )
      .subscribe();
  }
}

import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, map, Observable, shareReplay, Subject, switchMap } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';
import moment from 'moment';

import { GovukTableColumn, GovukValidators } from 'govuk-components';

import {
  ExecutedRequestAction,
  ExecutedRequestActionsMiReportParams,
  ExecutedRequestActionsMiReportResult,
  MiReportsService,
} from 'pmrv-api';

import { DateInputValidators } from '../../../../projects/govuk-components/src/lib/date-input/date-input.validators';
import { createTablePage, pageSize } from '../core/mi-report';

@Component({
  selector: 'app-completed-work',
  templateUrl: './completed-work.component.html',
  providers: [BackLinkService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CompletedWorkComponent implements OnInit {
  readonly pageSize = pageSize;
  reportItems$: Observable<ExecutedRequestAction[]>;
  pageItems$: Observable<ExecutedRequestAction[]>;
  totalNumOfItems$: Observable<number>;

  currentPage$ = new BehaviorSubject<number>(1);
  generateReport$ = new Subject<void>();

  reportOptionsForm: FormGroup = this.fb.group({
    option: [null, [GovukValidators.required('Select an option')]],
    year: [
      null,
      [
        GovukValidators.required('Enter a year value'),
        GovukValidators.pattern('[0-9]*', 'Enter a valid year value e.g. 2022'),
        GovukValidators.builder(
          `Enter a valid year value e.g. 2022`,
          DateInputValidators.dateFieldValidator('year', 1900, 2100),
        ),
      ],
    ],
  });

  tableColumns: GovukTableColumn<ExecutedRequestAction>[] = [
    { field: 'accountType', header: 'Account Type' },
    { field: 'accountId', header: 'Account Id' },
    { field: 'accountName', header: 'Account Name' },
    { field: 'accountStatus', header: 'Account Status' },
    { field: 'legalEntityName', header: 'Legal Entity Name' },
    { field: 'permitId', header: 'Permit Id' },
    { field: 'requestId', header: 'Workflow Id' },
    { field: 'requestType', header: 'Workflow Type' },
    { field: 'requestStatus', header: 'Workflow Status' },
    { field: 'requestActionType', header: 'Timeline Event Type' },
    { field: 'requestActionSubmitter', header: 'Timeline Event Completed By' },
    { field: 'requestActionCompletionDate', header: 'Timeline Event Date Completed' },
  ];

  constructor(
    private readonly fb: FormBuilder,
    private readonly miReportsService: MiReportsService,
    private readonly backlinkService: BackLinkService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.backlinkService.show();

    this.reportItems$ = this.generateReport$.pipe(
      switchMap(() => this.miReportsService.generateReportUsingPOST(this.constructRequestBody())),
      map((res: ExecutedRequestActionsMiReportResult) => res.actions),
      shareReplay({ bufferSize: 1, refCount: true }),
    );

    this.pageItems$ = combineLatest([this.reportItems$, this.currentPage$]).pipe(
      map(([items, currentPage]) => createTablePage(currentPage, this.pageSize, items)),
    );
    this.totalNumOfItems$ = this.reportItems$.pipe(map((items) => items.length));
  }

  onSubmit() {
    if (this.reportOptionsForm.valid) {
      this.generateReport$.next();
      this.router.navigate([], { relativeTo: this.route, queryParams: { page: 1 }, queryParamsHandling: 'merge' });
    }
  }

  private constructRequestBody(): ExecutedRequestActionsMiReportParams {
    switch (this.reportOptionsForm.get('option').value) {
      case 'LAST_30_DAYS': {
        return {
          reportType: 'COMPLETED_WORK',
          fromDate: moment().subtract(30, 'd').format('YYYY-MM-DD'),
        };
      }
      case 'ANNUAL': {
        return {
          reportType: 'COMPLETED_WORK',
          fromDate: moment([this.reportOptionsForm.get('year').value]).format('YYYY-MM-DD'),
          toDate: moment([Number(this.reportOptionsForm.get('year').value) + 1]).format('YYYY-MM-DD'),
        };
      }
    }
  }
}

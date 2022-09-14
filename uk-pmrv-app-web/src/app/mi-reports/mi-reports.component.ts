import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, combineLatest, map, Observable, shareReplay } from 'rxjs';

import { GovukTableColumn } from 'govuk-components';

import { MiReportSearchResult } from 'pmrv-api';

import { createTablePage, miReportTypeDescriptionMap, miReportTypeLinkMap } from './core/mi-report';

@Component({
  selector: 'app-mi-reports',
  templateUrl: './mi-reports.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MiReportsComponent {
  readonly pageSize = 10;
  readonly miReportTypeLinkMap = miReportTypeLinkMap;

  tableColumns: GovukTableColumn[] = [{ field: 'description', header: 'MI Report Type' }];

  currentPage$ = new BehaviorSubject<number>(1);

  private data$: Observable<MiReportSearchResult[]> = this.route.data.pipe(map((data) => data.miReports));

  currentPageData$ = combineLatest([this.data$, this.currentPage$]).pipe(
    map(([data, currentPage]) =>
      createTablePage(currentPage, this.pageSize, data)
        .map((p) => ({ ...p, description: miReportTypeDescriptionMap[p.miReportType] }))
        .sort((a, b) => a.description.localeCompare(b.description)),
    ),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  totalPages$ = this.data$.pipe(map((reports) => reports.length));

  constructor(private readonly route: ActivatedRoute) {}

}

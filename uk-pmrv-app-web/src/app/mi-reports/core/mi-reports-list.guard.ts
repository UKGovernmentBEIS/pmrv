import { Injectable } from '@angular/core';
import { CanActivate, Resolve } from '@angular/router';

import { mapTo, Observable, tap } from 'rxjs';

import { MiReportSearchResult, MiReportsService } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class MiReportsListGuard implements CanActivate, Resolve<MiReportSearchResult[]> {
  miReports: MiReportSearchResult[];

  constructor(private readonly miReportsService: MiReportsService) {}

  canActivate(): Observable<boolean> {
    return this.miReportsService.getCurrentUserMiReportsUsingGET().pipe(
      tap((result) => (this.miReports = result)),
      mapTo(true),
    );
  }

  resolve(): MiReportSearchResult[] {
    return this.miReports;
  }
}

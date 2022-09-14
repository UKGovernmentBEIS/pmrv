import { Location } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { Observable, pluck } from 'rxjs';

import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { AccountDetailsDTO } from 'pmrv-api';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styles: [
    `
      span.status {
        margin-left: 30px;
      }
      button.start-new-process {
        float: right;
      }
    `,
  ],
  providers: [BreadcrumbService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AccountComponent implements OnInit {
  account$: Observable<AccountDetailsDTO>;

  constructor(
    private readonly route: ActivatedRoute,
    readonly location: Location,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}

  ngOnInit(): void {
    this.breadcrumbService.show([
      { text: 'Search results', link: ['/accounts'], queryParams: window.history.state['accountSearchParams'] },
    ]);
    this.account$ = (
      this.route.data as Observable<{
        account: AccountDetailsDTO;
      }>
    ).pipe(pluck('account'));
  }
}

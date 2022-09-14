import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { Observable, of, pluck, switchMap } from 'rxjs';

import { AccountDetailsDTO, AccountViewService } from 'pmrv-api';

import { catchNotFoundRequest, ErrorCode } from '../../error/not-found-error';
import { SharedStore } from '../store/shared.store';

@Component({
  selector: 'app-incorporate-header',
  template: `
    <div class="govuk-phase-banner" *ngIf="accountDetails$ | async as accountDetails">
      <div class="govuk-phase-banner__content">
        <span class="govuk-!-font-weight-bold"> {{ accountDetails.name }} </span>
        <span class="govuk-!-padding-left-3" *ngIf="accountDetails.permitId">
          Permit ID:
          <span> {{ accountDetails.permitId }} / {{ accountDetails.status | accountStatus }} </span>
        </span>
        <span class="govuk-!-padding-left-3" *ngIf="accountDetails?.emitterType">
          Type:
          <ng-container *ngIf="accountDetails.emitterType === 'GHGE'; else hseTemplate">
            {{ accountDetails.emitterType }} / {{ accountDetails.installationCategory | installationCategory }}
          </ng-container>
          <ng-template #hseTemplate>
            {{ accountDetails.emitterType }}
          </ng-template>
        </span>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class IncorporateHeaderComponent implements OnInit {
  accountDetails$: Observable<AccountDetailsDTO>;
  constructor(private readonly sharedStore: SharedStore, private readonly accountViewService: AccountViewService) {}

  ngOnInit(): void {
    this.accountDetails$ = this.sharedStore.pipe(
      pluck('accountId'),
      switchMap((accountId) => {
        return of(accountId).pipe(
          switchMap((accountId) =>
            accountId ? this.accountViewService.getAccountHeaderInfoByIdUsingGET(accountId) : of(undefined),
          ),
          catchNotFoundRequest(ErrorCode.NOTFOUND1001, () => of(undefined)),
        );
      }),
    );
  }
}

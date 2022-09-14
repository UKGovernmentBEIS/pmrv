import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { Observable, pluck } from 'rxjs';

import { AuthService } from '@core/services/auth.service';

import { AccountDetailsDTO } from 'pmrv-api';

import { applicationTypeMap } from '../core/accountApplicationType';
import { accountFinalStatuses } from '../core/accountFinalStatuses';

@Component({
  selector: 'app-account-details',
  templateUrl: './details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsComponent {
  account$: Observable<any> = this.route.data.pipe(pluck('account'));
  userRoleType$ = this.authService.userStatus.pipe(pluck('roleType'));
  applicationTypeMap = applicationTypeMap;

  constructor(private readonly route: ActivatedRoute, private readonly authService: AuthService) {}

  canChangeByStatus(status: AccountDetailsDTO['status']): boolean {
    return accountFinalStatuses(status);
  }
}

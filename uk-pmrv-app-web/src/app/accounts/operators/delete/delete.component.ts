import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import {
  BehaviorSubject,
  combineLatest,
  first,
  map,
  Observable,
  pluck,
  switchMap,
  switchMapTo,
  withLatestFrom,
} from 'rxjs';

import { ApplicationUserDTO, OperatorAuthoritiesService, OperatorUserDTO } from 'pmrv-api';

import { AuthService } from '../../../core/services/auth.service';
import { catchBadRequest, ErrorCode } from '../../../error/business-errors';
import { ConcurrencyErrorService } from '../../../error/concurrency-error/concurrency-error.service';
import {
  activeOperatorAdminError,
  financialContactError,
  primaryContactError,
  saveNotFoundOperatorError,
  serviceContactError,
} from '../errors/concurrency-error';

@Component({
  selector: 'app-delete',
  templateUrl: './delete.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteComponent {
  user$ = (this.route.data as Observable<{ user: OperatorUserDTO | ApplicationUserDTO }>).pipe(pluck('user'));
  deleteStatus = new BehaviorSubject<'success' | null>(null);
  accountId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('accountId'))));
  isCurrentUser$ = combineLatest([this.route.paramMap, this.authService.userStatus]).pipe(
    map(([paramMap, userStatus]) => paramMap.get('userId') === userStatus.userId),
  );

  constructor(
    readonly authService: AuthService,
    private readonly operatorAuthoritiesService: OperatorAuthoritiesService,
    private readonly route: ActivatedRoute,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  confirm(): void {
    this.accountId$
      .pipe(
        first(),
        withLatestFrom(this.isCurrentUser$, this.route.paramMap.pipe(map((paramMap) => paramMap.get('userId')))),
        switchMap(([accountId, isCurrentUser, userId]) =>
          isCurrentUser
            ? this.operatorAuthoritiesService
                .deleteCurrentUserAccountOperatorAuthorityUsingDELETE(accountId)
                .pipe(switchMapTo(this.authService.loadUserStatus()))
            : this.operatorAuthoritiesService.deleteAccountOperatorAuthorityUsingDELETE(accountId, userId),
        ),
        catchBadRequest(
          [
            ErrorCode.AUTHORITY1001,
            ErrorCode.AUTHORITY1004,
            ErrorCode.ACCOUNT_CONTACT1001,
            ErrorCode.ACCOUNT_CONTACT1002,
            ErrorCode.ACCOUNT_CONTACT1003,
          ],
          (res) =>
            this.accountId$.pipe(
              first(),
              switchMap((accountId) =>
                this.concurrencyErrorService.showError(
                  (() => {
                    switch (res.error.code) {
                      case ErrorCode.AUTHORITY1001:
                        return activeOperatorAdminError(accountId);
                      case ErrorCode.AUTHORITY1004:
                        return saveNotFoundOperatorError(accountId);
                      case ErrorCode.ACCOUNT_CONTACT1001:
                        return primaryContactError(accountId);
                      case ErrorCode.ACCOUNT_CONTACT1002:
                        return financialContactError(accountId);
                      case ErrorCode.ACCOUNT_CONTACT1003:
                        return serviceContactError(accountId);
                    }
                  })(),
                ),
              ),
            ),
        ),
      )
      .subscribe(() => this.deleteStatus.next('success'));
  }
}

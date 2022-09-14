import { Location } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, combineLatest, first, Observable, pluck, switchMap, tap } from 'rxjs';

import { ApplicationUserDTO, VerifierAuthoritiesService, VerifierUserDTO } from 'pmrv-api';

import { AuthService } from '../../core/services/auth.service';
import { catchBadRequest, ErrorCode } from '../../error/business-errors';
import { ConcurrencyErrorService } from '../../error/concurrency-error/concurrency-error.service';
import { deleteUniqueActiveVerifierError, saveNotFoundVerifierError } from '../errors/concurrency-error';

@Component({
  selector: 'app-delete',
  templateUrl: './delete.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteComponent {
  verifier$ = (this.route.data as Observable<{ user: ApplicationUserDTO | VerifierUserDTO }>).pipe(pluck('user'));
  isConfirmationDisplayed$ = new BehaviorSubject<boolean>(false);

  constructor(
    readonly location: Location,
    private readonly authService: AuthService,
    private readonly verifierAuthoritiesService: VerifierAuthoritiesService,
    private readonly route: ActivatedRoute,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  delete(): void {
    combineLatest([this.authService.userStatus, this.route.paramMap])
      .pipe(
        first(),
        switchMap(([userStatus, paramMap]) =>
          userStatus.userId === paramMap.get('userId')
            ? this.verifierAuthoritiesService
                .deleteCurrentVerifierAuthorityUsingDELETE()
                .pipe(tap(() => this.authService.logout()))
            : this.verifierAuthoritiesService.deleteVerifierAuthorityUsingDELETE(paramMap.get('userId')),
        ),
        catchBadRequest([ErrorCode.AUTHORITY1006, ErrorCode.AUTHORITY1007], (res) => {
          switch (res.error.code) {
            case ErrorCode.AUTHORITY1006:
              return this.concurrencyErrorService.showError(saveNotFoundVerifierError);
            case ErrorCode.AUTHORITY1007:
              return this.concurrencyErrorService.showError(deleteUniqueActiveVerifierError);
          }
        }),
      )
      .subscribe(() => this.isConfirmationDisplayed$.next(true));
  }
}

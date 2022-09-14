import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, combineLatest, first, Observable, pluck, switchMap, tap } from 'rxjs';

import { ApplicationUserDTO, RegulatorAuthoritiesService, RegulatorUserDTO } from 'pmrv-api';

import { AuthService } from '../../core/services/auth.service';
import { catchBadRequest, ErrorCode } from '../../error/business-errors';
import { ConcurrencyErrorService } from '../../error/concurrency-error/concurrency-error.service';
import { saveNotFoundRegulatorError } from '../errors/concurrency-error';

@Component({
  selector: 'app-delete',
  templateUrl: './delete.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteComponent {
  regulator$ = (this.route.data as Observable<{ user: ApplicationUserDTO | RegulatorUserDTO }>).pipe(pluck('user'));
  isConfirmationDisplayed$ = new BehaviorSubject<boolean>(false);

  constructor(
    private readonly authService: AuthService,
    private readonly regulatorAuthoritiesService: RegulatorAuthoritiesService,
    private readonly route: ActivatedRoute,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
  ) {}

  deleteRegulator(): void {
    combineLatest([this.authService.userStatus, this.route.paramMap])
      .pipe(
        first(),
        switchMap(([userStatus, paramMap]) =>
          userStatus.userId === paramMap.get('userId')
            ? this.regulatorAuthoritiesService
                .deleteCurrentRegulatorUserByCompetentAuthorityUsingDELETE()
                .pipe(tap(() => this.authService.logout()))
            : this.regulatorAuthoritiesService.deleteRegulatorUserByCompetentAuthorityUsingDELETE(
                paramMap.get('userId'),
              ),
        ),
        catchBadRequest(ErrorCode.AUTHORITY1003, () =>
          this.concurrencyErrorService.showError(saveNotFoundRegulatorError),
        ),
      )
      .subscribe(() => this.isConfirmationDisplayed$.next(true));
  }
}

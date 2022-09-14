import { Location } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import {
  BehaviorSubject,
  combineLatest,
  EMPTY,
  filter,
  first,
  map,
  Observable,
  pluck,
  switchMap,
  takeUntil,
  tap,
  withLatestFrom,
} from 'rxjs';

import cleanDeep from 'clean-deep';

import { GovukValidators } from 'govuk-components';

import {
  CaExternalContactDTO,
  VerifierUserInvitationDTO,
  VerifierUsersInvitationService,
  VerifierUsersService,
} from 'pmrv-api';

import { AuthService } from '../../core/services/auth.service';
import { DestroySubject } from '../../core/services/destroy-subject.service';
import { catchBadRequest, ErrorCode } from '../../error/business-errors';
import { ConcurrencyErrorService } from '../../error/concurrency-error/concurrency-error.service';
import { BackLinkService } from '../../shared/back-link/back-link.service';
import { saveNotFoundVerifierError } from '../errors/concurrency-error';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService, DestroySubject],
})
export class DetailsComponent implements OnInit {
  confirmedAddedVerifier$ = new BehaviorSubject<string>(null);
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);

  isLoggedUser$ = combineLatest([this.authService.userStatus, this.route.paramMap]).pipe(
    first(),
    map(([userStatus, parameters]) => userStatus.userId === parameters.get('userId')),
  );

  form = this.fb.group({
    firstName: [
      null,
      [
        GovukValidators.required(`Enter user's first name`),
        GovukValidators.maxLength(255, `The verifier's first name should not be more than 255 characters`),
      ],
    ],
    lastName: [
      null,
      [
        GovukValidators.required(`Enter user's last name`),
        GovukValidators.maxLength(255, `The verifier's last name should not be more than 255 characters`),
      ],
    ],
    phoneNumber: [
      null,
      [
        GovukValidators.required(`Enter user's telephone number`),
        GovukValidators.maxLength(255, 'Telephone number should not be more than 255 characters'),
      ],
    ],
    mobileNumber: [null, GovukValidators.maxLength(255, 'Mobile number should not be more than 255 characters')],
    email: [
      null,
      [
        GovukValidators.required(`Enter user's email address`),
        GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
        GovukValidators.maxLength(255, 'Email should not be more than 255 characters'),
      ],
    ],
  });

  userLoaded$: Observable<CaExternalContactDTO> = this.route.data.pipe(
    pluck('user'),
    filter((user) => user),
    tap((user) => this.form.patchValue(user)),
    tap(() => this.form.get('email').disable()),
  );

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly verifierUsersInvitationService: VerifierUsersInvitationService,
    private readonly verifierUsersService: VerifierUsersService,
    private readonly authService: AuthService,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
    private readonly backLinkService: BackLinkService,
    private readonly destroy$: DestroySubject,
    private readonly location: Location,
  ) {}

  ngOnInit(): void {
    this.confirmedAddedVerifier$.pipe(takeUntil(this.destroy$)).subscribe((res) => {
      if (res) {
        this.backLinkService.hide();
      } else {
        this.backLinkService.show();
      }
    });
  }

  addNewUser(): void {
    if (this.form.valid) {
      this.route.paramMap
        .pipe(
          first(),
          map((paramMap) => paramMap.get('userId')),
          withLatestFrom(this.route.queryParamMap, this.authService.userStatus),
          switchMap(([userId, queryParamMap, userStatus]) =>
            userId
              ? userStatus.userId === userId
                ? this.verifierUsersService.updateCurrentVerifierUserUsingPATCH(this.form.getRawValue())
                : this.verifierUsersService.updateVerifierUserByIdUsingPATCH(userId, this.form.getRawValue())
              : this.verifierUsersInvitationService.inviteVerifierUserUsingPOST(
                  cleanDeep({
                    ...this.form.value,
                    roleCode: queryParamMap.get('roleCode'),
                  }) as VerifierUserInvitationDTO,
                ),
          ),
          catchBadRequest(ErrorCode.AUTHORITY1006, () =>
            this.concurrencyErrorService.showError(saveNotFoundVerifierError),
          ),
          catchBadRequest(ErrorCode.USER1001, () => {
            this.form.get('email').setErrors({ existingUser: 'This user email already exists in PMRV' });
            this.isSummaryDisplayed$.next(true);

            return EMPTY;
          }),
        )
        .subscribe((user) =>
          user ? this.location.back() : this.confirmedAddedVerifier$.next(this.form.get('email').value),
        );
    } else {
      this.isSummaryDisplayed$.next(true);
    }
  }
}

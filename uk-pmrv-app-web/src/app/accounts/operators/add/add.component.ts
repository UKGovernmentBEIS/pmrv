import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import {
  BehaviorSubject,
  catchError,
  combineLatest,
  EMPTY,
  map,
  mapTo,
  Observable,
  shareReplay,
  switchMap,
  take,
  tap,
  throwError,
} from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { OperatorUsersInvitationService } from 'pmrv-api';

import { ErrorCode, isBadRequest } from '../../../error/business-errors';
import { BackLinkService } from '../../../shared/back-link/back-link.service';

@Component({
  selector: 'app-add',
  templateUrl: './add.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class AddComponent implements OnInit {
  accountId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('accountId'))));
  userType$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('userType')));
  confirmAddedEmail$: Observable<string>;

  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  form: FormGroup = this.fb.group({
    firstName: [
      null,
      [
        GovukValidators.required(`Enter user's first name`),
        GovukValidators.maxLength(255, 'First name should not be more than 255 characters'),
      ],
    ],
    lastName: [
      null,
      [
        GovukValidators.required(`Enter user's last name`),
        GovukValidators.maxLength(255, 'Last name should not be more than 255 characters'),
      ],
    ],
    email: [
      null,
      [
        GovukValidators.required(`Enter user's email address`),
        GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
        GovukValidators.maxLength(255, 'Email should not be more than 255 characters'),
      ],
    ],
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly operatorUsersInvitationService: OperatorUsersInvitationService,
    private readonly route: ActivatedRoute,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  submitForm(): void {
    if (this.form.valid) {
      const email = this.form.get('email').value;
      this.confirmAddedEmail$ = combineLatest([this.accountId$, this.userType$]).pipe(
        take(1),
        switchMap(([accountId, userType]: [number, string]) =>
          this.operatorUsersInvitationService.inviteOperatorUserToAccountUsingPOST(Number(accountId), {
            email,
            firstName: this.form.get('firstName').value,
            lastName: this.form.get('lastName').value,
            roleCode: userType,
          }),
        ),
        catchError((res: unknown) => {
          if (isBadRequest(res)) {
            switch (res.error?.code) {
              case ErrorCode.AUTHORITY1000:
              case ErrorCode.AUTHORITY1005:
                this.form.get('email').setErrors({
                  emailExists: 'The email address of the new user must not already exist at your installation',
                });
                break;
              case ErrorCode.USER1001:
                this.form.get('email').setErrors({
                  emailExists: 'This email address is already linked to a non-operator account',
                });
                break;
              default:
                return throwError(() => res);
            }
            this.isSummaryDisplayed$.next(true);
            return EMPTY;
          } else {
            return throwError(() => res);
          }
        }),
        mapTo(email),
        tap((email) => {
          if (email) {
            this.backLinkService.hide();
          }
        }),
        shareReplay({ bufferSize: 1, refCount: false }),
      );
    } else {
      this.isSummaryDisplayed$.next(true);
      this.form.markAllAsTouched();
    }
  }
}

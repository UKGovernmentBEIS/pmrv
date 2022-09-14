import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import {
  BehaviorSubject,
  combineLatest,
  filter,
  first,
  map,
  mapTo,
  Observable,
  shareReplay,
  startWith,
  switchMap,
  tap,
} from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { OperatorUserDTO, OperatorUsersService } from 'pmrv-api';

import { AuthService } from '../../../core/services/auth.service';
import { catchBadRequest, ErrorCode } from '../../../error/business-errors';
import { ConcurrencyErrorService } from '../../../error/concurrency-error/concurrency-error.service';
import { AddressInputComponent } from '../../../shared/address-input/address-input.component';
import { BackLinkService } from '../../../shared/back-link/back-link.service';
import { PhoneInputComponent } from '../../../shared/phone-input/phone-input.component';
import { saveNotFoundOperatorError } from '../errors/concurrency-error';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class DetailsComponent implements OnInit {
  sendNoticesForm = this.fb.group({ sendNotices: [[], { updateOn: 'blur' }] });
  isLoggedUser$ = combineLatest([this.authService.userStatus, this.activatedRoute.paramMap]).pipe(
    first(),
    map(([userStatus, parameters]) => userStatus.userId === parameters.get('userId')),
  );
  form$: Observable<FormGroup> = this.activatedRoute.data.pipe(
    map(({ user }: { user: OperatorUserDTO }) => {
      this.sendNoticesForm.get('sendNotices').setValue([Object.values(user.address).length > 0]);

      return this.fb.group({
        firstName: [
          user.firstName,
          [
            GovukValidators.required('Enter your first name'),
            GovukValidators.maxLength(255, 'Your first name should not be larger than 255 characters'),
          ],
        ],
        lastName: [
          user.lastName,
          [
            GovukValidators.required('Enter your last name'),
            GovukValidators.maxLength(255, 'Your last name should not be larger than 255 characters'),
          ],
        ],
        phoneNumber: [
          user.phoneNumber,
          [GovukValidators.empty('Enter your phone number'), ...PhoneInputComponent.validators],
        ],
        mobileNumber: [user.mobileNumber, PhoneInputComponent.validators],
        email: [{ value: user.email, disabled: true }],
        address: this.fb.group(AddressInputComponent.controlsFactory(user.address)),
      });
    }),
    switchMap((form) =>
      this.sendNoticesForm.get('sendNotices').valueChanges.pipe(
        startWith(this.sendNoticesForm.get('sendNotices').value),
        tap(([sendNotices]) => this.addRemoveAddressDetails(form, sendNotices)),
        mapTo(form),
      ),
    ),
    shareReplay({ bufferSize: 1, refCount: true }),
  );
  isSummaryDisplayed = new BehaviorSubject<boolean>(false);

  constructor(
    private readonly fb: FormBuilder,
    private readonly activatedRoute: ActivatedRoute,
    private readonly operatorService: OperatorUsersService,
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly concurrencyErrorService: ConcurrencyErrorService,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onSubmit(): void {
    combineLatest([this.form$, this.authService.userStatus, this.activatedRoute.paramMap])
      .pipe(
        first(),
        tap(([form]) => {
          if (!form.valid) {
            this.isSummaryDisplayed.next(true);
          }
        }),
        filter(([form]) => form.valid),
        switchMap(([form, { userId }, params]) => {
          const payload = {
            ...form.value,
            email: form.get('email').value,
            address: this.sendNoticesForm.value.sendNotices[0] ? form.value.address : null,
          };
          return userId === params.get('userId')
            ? this.operatorService.updateCurrentOperatorUserUsingPATCH(payload)
            : this.operatorService.updateOperatorUserByIdUsingPATCH(
                Number(params.get('accountId')),
                params.get('userId'),
                payload,
              );
        }),
        catchBadRequest(ErrorCode.AUTHORITY1004, () =>
          this.activatedRoute.paramMap.pipe(
            switchMap((paramMap) =>
              this.concurrencyErrorService.showError(saveNotFoundOperatorError(Number(paramMap.get('accountId')))),
            ),
          ),
        ),
      )
      .subscribe(() => this.router.navigate(['../..'], { fragment: 'users', relativeTo: this.activatedRoute }));
  }

  private addRemoveAddressDetails(form: FormGroup, isSendNoticesChecked: boolean): void {
    if (isSendNoticesChecked) {
      form.get('address').enable();
    } else {
      form.get('address').disable();
    }
  }
}

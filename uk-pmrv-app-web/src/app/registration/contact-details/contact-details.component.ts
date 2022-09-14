import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormGroup, ValidatorFn } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, filter, takeUntil, tap } from 'rxjs';

import { GovukValidators } from 'govuk-components';

import { OperatorUsersRegistrationService } from 'pmrv-api';

import { DestroySubject } from '../../core/services/destroy-subject.service';
import { AddressInputComponent } from '../../shared/address-input/address-input.component';
import { PhoneInputComponent } from '../../shared/phone-input/phone-input.component';
import { UserRegistrationStore } from '../store/user-registration.store';

@Component({
  selector: 'app-contact-details',
  templateUrl: './contact-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class ContactDetailsComponent implements OnInit {
  isSummaryDisplayed = false;
  form: FormGroup = this.fb.group({
    firstName: [
      null,
      [
        GovukValidators.required('Enter your first name'),
        GovukValidators.maxLength(255, 'Your first name should not be larger than 255 characters'),
      ],
    ],
    lastName: [
      null,
      [
        GovukValidators.required('Enter your last name'),
        GovukValidators.maxLength(255, 'Your last name should not be larger than 255 characters'),
      ],
    ],
    phoneNumber: [
      { countryCode: '44', number: null },
      [...PhoneInputComponent.validators, this.phoneNumberSizeValidator()],
    ],
    mobileNumber: [null, [...PhoneInputComponent.validators, this.phoneNumberSizeValidator()]],
    email: [{ value: null, disabled: true }],
    sendNotices: [],
    address: this.fb.group(AddressInputComponent.controlsFactory(null)),
  });

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: UserRegistrationStore,
    private readonly operatorUsersRegistrationService: OperatorUsersRegistrationService,
    private readonly fb: FormBuilder,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    combineLatest([this.store.select('userRegistrationDTO'), this.store.select('email')])
      .pipe(
        takeUntil(this.destroy$),
        tap(([, email]) => this.form.patchValue({ email })),
        filter(([user]) => !!user),
        tap(([user]) => this.form.patchValue({ ...user, sendNotices: [!!user.address] })),
      )
      .subscribe();
  }

  submitContactDetails(): void {
    if (this.form.valid) {
      const { sendNotices, ...model } = this.form.value;
      this.store.setState({ ...this.store.getState(), userRegistrationDTO: model });

      this.router.navigate(
        [
          this.store.getState().isSummarized ||
          this.store.getState().invitationStatus === 'PENDING_USER_REGISTRATION_NO_PASSWORD'
            ? '../summary'
            : '../choose-password',
        ],
        {
          relativeTo: this.route,
        },
      );
    } else {
      this.isSummaryDisplayed = true;
    }
  }

  private phoneNumberSizeValidator(): ValidatorFn {
    return (control: AbstractControl): { [key: string]: string } | null => {
      return control.value?.number?.length > 255
        ? { invalidSize: `Your phone number should not be larger than 255 characters` }
        : null;
    };
  }
}

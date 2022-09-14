import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';

import { BehaviorSubject, catchError, EMPTY, first, mapTo, Observable, shareReplay, tap, throwError } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';

import { GovukValidators } from 'govuk-components';

import { VerificationBodiesService } from 'pmrv-api';

import { ErrorCode, isBadRequest } from '../../error/business-errors';
import { VERIFICATION_BODY_FORM, verificationBodyFormFactory } from '../form/form.factory';

@Component({
  selector: 'app-add',
  templateUrl: './add.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService, verificationBodyFormFactory],
})
export class AddComponent implements OnInit {
  confirmedVBname$: Observable<string>;
  isSummaryDisplayed$ = new BehaviorSubject<boolean>(false);
  private adminVerifierUserInvitation = this.fb.group({
    firstName: [
      null,
      [
        GovukValidators.required('Enter the first name of the admin user'),
        GovukValidators.maxLength(255, 'The first name should not be larger than 255 characters'),
      ],
    ],
    lastName: [
      null,
      [
        GovukValidators.required('Enter the last name of the admin user'),
        GovukValidators.maxLength(255, 'The last name should not be larger than 255 characters'),
      ],
    ],
    phoneNumber: [
      null,
      [
        GovukValidators.required('Enter the telephone number of the admin user'),
        GovukValidators.maxLength(255, 'The telephone number should not be larger than 255 characters'),
      ],
    ],
    mobileNumber: [],
    email: [
      null,
      [
        GovukValidators.required('Enter the email address of the admin user'),
        GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
        GovukValidators.maxLength(255, 'The email address should not be larger than 255 characters'),
      ],
    ],
  });

  constructor(
    @Inject(VERIFICATION_BODY_FORM) public readonly form: FormGroup,
    private readonly verificationBodiesService: VerificationBodiesService,
    private readonly fb: FormBuilder,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
    this.form.addControl('adminVerifierUserInvitation', this.adminVerifierUserInvitation);
  }

  submit(): void {
    if (this.form.valid) {
      const vbName = this.form.get('details.name').value;
      this.confirmedVBname$ = this.verificationBodiesService
        .createVerificationBodyUsingPOST({
          name: vbName,
          accreditationReferenceNumber: this.form.get('details.accreditationRefNum').value,
          address: this.form.get('details.address').value,
          emissionTradingSchemes: this.form.get('types').value,
          adminVerifierUserInvitation: this.form.get('adminVerifierUserInvitation').value,
        })
        .pipe(
          first(),
          mapTo(vbName),
          tap(() => this.backLinkService.hide()),
          shareReplay({ bufferSize: 1, refCount: false }),
          catchError((res: unknown) => {
            if (isBadRequest(res, [ErrorCode.VERBODY1001, ErrorCode.USER1001])) {
              switch (res.error.code) {
                case ErrorCode.USER1001:
                  this.form.get('adminVerifierUserInvitation.email').setErrors({
                    emailExists: 'This user email already exists in PMRV',
                  });
                  break;
                case ErrorCode.VERBODY1001:
                  this.form.get('details.accreditationRefNum').setErrors({
                    uniqueAccred: 'Enter a unique Accreditation reference number',
                  });
                  break;
              }
              this.isSummaryDisplayed$.next(true);
              return EMPTY;
            } else {
              return throwError(() => res);
            }
          }),
        );
    } else {
      this.isSummaryDisplayed$.next(true);
    }
  }
}

import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { OperatorUsersRegistrationService } from 'pmrv-api';

import { BackLinkService } from '../../shared/back-link/back-link.service';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-email',
  templateUrl: './email.component.html',
  providers: [BackLinkService],
})
export class EmailComponent implements OnInit {
  isSummaryDisplayed: boolean;
  isVerificationSent: boolean;
  isSubmitDisabled: boolean;

  form = this.fb.group({
    email: [
      null,
      [
        GovukValidators.required('Enter your email address'),
        GovukValidators.email('Enter an email address in the correct format, like name@example.com'),
        GovukValidators.maxLength(255, 'Enter an email address with a maximum of 255 characters'),
      ],
    ],
  });

  constructor(
    private readonly operatorUsersRegistrationService: OperatorUsersRegistrationService,
    private readonly fb: FormBuilder,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  submitEmail(): void {
    if (this.form.valid) {
      this.isSubmitDisabled = true;
      this.operatorUsersRegistrationService
        .sendVerificationEmailUsingPOST({ email: this.form.get('email').value })
        .subscribe(() => {
          this.isVerificationSent = true;
          this.backLinkService.hide();
        });
    } else {
      this.isSummaryDisplayed = true;
    }
  }
}

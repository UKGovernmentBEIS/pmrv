import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute } from '@angular/router';

import { GovukValidators } from 'govuk-components';

import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-invalid-email',
  template: `
    <govuk-error-summary [form]="form"></govuk-error-summary>

    <h3 class="govuk-heading-m">What happens next?</h3>
    <p class="govuk-body" [ngSwitch]="errorCode">
      <ng-container *ngSwitchCase="'USER1001'">
        Please use the registered e-mail address to
        <a [routerLink]="" govukLink (click)="signIn()">sign in</a> to the system.
      </ng-container>
      <ng-container *ngSwitchDefault>
        You need to go <a govukLink routerLink="..">back</a> and start the registration process again.
      </ng-container>
    </p>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InvalidEmailLinkComponent implements OnInit {
  errorCode: string;
  form = this.fb.group({ email: [] });

  constructor(
    private readonly fb: FormBuilder,
    private readonly route: ActivatedRoute,
    private readonly authService: AuthService,
    private readonly titleService: Title,
  ) {}

  ngOnInit(): void {
    this.errorCode = this.route.snapshot.queryParamMap.get('code');
    const control = this.form.get('email');
    let message;

    switch (this.errorCode) {
      case 'USER1001':
        message = 'The email address has already been registered';
        break;
      case 'EMAIL1001':
        message = 'The email verification link has expired';
        break;
      case 'TOKEN1001':
        message = 'Invalid token.';
        break;
      case 'FORM1001':
        message = 'Form validation failed';
        break;
      default:
        message = 'Invalid token';
        break;
    }

    this.titleService.setTitle(message);
    control.markAsTouched();
    control.setValidators(GovukValidators.builder(message, () => ({ expired: true })));
    control.updateValueAndValidity();
  }

  signIn(): void {
    this.authService.login();
  }
}

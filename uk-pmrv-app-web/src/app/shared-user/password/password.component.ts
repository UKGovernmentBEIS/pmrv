import { Component } from '@angular/core';
import { ControlContainer, FormGroupDirective } from '@angular/forms';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-password',
  templateUrl: './password.component.html',
  viewProviders: [{ provide: ControlContainer, useExisting: FormGroupDirective }],
})
export class PasswordComponent {
  passwordStrength: number;

  constructor(readonly formGroupDirective: FormGroupDirective) {}
}

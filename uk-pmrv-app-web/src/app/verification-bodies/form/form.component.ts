import { Component } from '@angular/core';
import { ControlContainer, FormGroupDirective } from '@angular/forms';

import { vbTypes } from './verification-body-type.pipe';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
  viewProviders: [{ provide: ControlContainer, useExisting: FormGroupDirective }],
})
export class FormComponent {
  typeOptions = vbTypes;

  constructor(readonly formGroupDirective: FormGroupDirective) {}
}

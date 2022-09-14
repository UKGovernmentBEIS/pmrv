import { Component, HostBinding, Input, Optional, Self } from '@angular/core';
import { ControlValueAccessor, FormControl, NgControl } from '@angular/forms';

import { FormService } from 'govuk-components';

/* eslint-disable
   @angular-eslint/prefer-on-push-component-change-detection,
   @typescript-eslint/no-empty-function,
   @typescript-eslint/no-unused-vars
*/
@Component({
  selector: 'div[app-radio-option]',
  templateUrl: './radio-option.component.html',
})
export class RadioOptionComponent implements ControlValueAccessor {
  @Input() index: string;
  @Input() value: string;
  @Input() label: string;
  @Input() isDisabled: boolean;

  @HostBinding('class.govuk-radios__item') readonly govukRadiosItem = true;

  constructor(@Self() @Optional() readonly ngControl: NgControl, private readonly formService: FormService) {
    ngControl.valueAccessor = this;
  }

  get identifier(): string {
    return this.formService.getControlIdentifier(this.ngControl);
  }

  get control(): FormControl {
    return this.ngControl.control as FormControl;
  }

  writeValue = (_: any): void => {};

  registerOnChange = (_: any): void => {};

  registerOnTouched = (_: any): void => {};
}

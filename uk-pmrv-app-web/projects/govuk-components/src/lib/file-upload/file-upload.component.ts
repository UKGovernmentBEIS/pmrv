import { Component, HostBinding, Input, Optional, Self } from '@angular/core';
import { ControlValueAccessor, FormControl, NgControl } from '@angular/forms';

import { FormService } from '../form/form.service';

/*
  eslint-disable
  @typescript-eslint/no-unused-vars,
  @angular-eslint/prefer-on-push-component-change-detection,
  @typescript-eslint/no-empty-function
*/
@Component({
  selector: 'div[govukFileUpload],govuk-file-upload',
  templateUrl: './file-upload.component.html',
})
export class FileUploadComponent implements ControlValueAccessor {
  @Input()
  set label(label: string) {
    this.isLabelHidden = false;
    this.currentLabel = label;
  }

  @Input() accepted: string;
  @Input() isMultiple: boolean;
  @HostBinding('class.govuk-!-display-block') readonly govukDisplayBlock = true;
  @HostBinding('class.govuk-form-group') readonly govukFormGroupClass = true;

  @HostBinding('class.govuk-form-group--error') get govukFormGroupErrorClass(): boolean {
    return this.control?.invalid && this.control?.touched;
  }

  isLabelHidden = true;
  currentLabel = 'Legend';

  constructor(@Self() @Optional() public ngControl: NgControl, private formService: FormService) {
    ngControl.valueAccessor = this;
  }

  get control(): FormControl {
    return this.ngControl.control as FormControl;
  }

  get identifier(): string {
    return this.formService.getControlIdentifier(this.ngControl);
  }

  onChange(event: Event): void {
    this.control.patchValue((event?.target as HTMLInputElement)?.files);
  }

  onBlur(): void {
    this.control.markAsTouched();
  }

  writeValue(_: any): void {}

  registerOnChange(_: any): void {}

  registerOnTouched(_: any): void {}
}

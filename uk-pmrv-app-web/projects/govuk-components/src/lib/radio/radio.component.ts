import { AfterContentInit, Component, ContentChildren, Input, Optional, QueryList, Self } from '@angular/core';
import { ControlContainer, ControlValueAccessor, NgControl } from '@angular/forms';

import { LegendSizeType } from '../fieldset';
import { FormService } from '../form/form.service';
import { FormInput } from '../form/form-input';
import { RadioOptionComponent } from './radio-option/radio-option.component';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'div[govuk-radio]',
  templateUrl: './radio.component.html',
})
export class RadioComponent<T> extends FormInput implements AfterContentInit, ControlValueAccessor {
  @Input() legend: string;
  @Input() hint: string;
  @Input() radioSize: 'medium' | 'large' = 'large';
  @Input() isInline = false;
  @Input() legendSize: LegendSizeType = 'normal';
  @ContentChildren(RadioOptionComponent) readonly options: QueryList<RadioOptionComponent<T>>;
  private onChange: (_: T) => any;
  private onBlur: () => any;
  private isDisabled: boolean;

  constructor(
    @Self() @Optional() ngControl: NgControl,
    formService: FormService,
    @Optional() container: ControlContainer,
  ) {
    super(ngControl, formService, container);
  }

  ngAfterContentInit(): void {
    this.options.forEach((option, index) => {
      option.index = index;
      option.groupIdentifier = this.identifier;
      this.options?.forEach((option) => option.registerOnChange(this.onChange));
      option.changeDetectorRef.markForCheck();
    });

    this.registerOnTouched(this.onBlur);
    this.setDisabledState(this.isDisabled);
    this.writeValue(this.control.value);
  }

  writeValue(value: T): void {
    this.options?.forEach((option) => option.writeValue(value));
  }

  registerOnChange(onChange: (_: T) => any): void {
    this.onChange = (option) => {
      this.writeValue(option);
      onChange(option);
    };
    this.options?.forEach((option) => option.registerOnChange(this.onChange));
  }

  registerOnTouched(onBlur: () => any): void {
    this.onBlur = onBlur;
    this.options?.forEach((option) => option.registerOnTouched(this.onBlur));
  }

  setDisabledState(isDisabled: boolean) {
    this.isDisabled = isDisabled;
    this.options?.forEach((option) => option.setDisabledState(isDisabled));
  }
}

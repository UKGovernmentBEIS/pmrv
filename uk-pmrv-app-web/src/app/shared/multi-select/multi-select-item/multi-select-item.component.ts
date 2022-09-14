import { ChangeDetectionStrategy, ChangeDetectorRef, Component, HostBinding, Input } from '@angular/core';
import { ControlValueAccessor, FormControl, NgControl } from '@angular/forms';

@Component({
  selector: 'div[app-multi-select-item]',
  template: `
    <input
      type="checkbox"
      class="govuk-checkboxes__input"
      [attr.value]="itemValue"
      [id]="id"
      [name]="groupIdentifier"
      [checked]="isChecked"
      [disabled]="isDisabled"
      (change)="onChange($event)"
      (blur)="onBlur()"
    />
    <label class="govuk-label govuk-checkboxes__label" [for]="id"> {{ label }} </label>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MultiSelectItemComponent implements ControlValueAccessor {
  @Input() label: string;
  @Input() itemValue: string;

  @HostBinding('class.govuk-checkboxes__item') readonly govukCheckboxesItem = true;

  isChecked: boolean;
  index: number;
  isDisabled: boolean;
  groupIdentifier: string;
  onBlur: () => any;
  onChange: (event: Event) => any;

  constructor(readonly cdRef: ChangeDetectorRef, private readonly ngControl: NgControl) {}

  get control(): FormControl {
    return this.ngControl.control as FormControl;
  }

  get id() {
    return `${this.groupIdentifier}-${this.index}`;
  }

  registerOnChange(onChange: () => any): void {
    this.onChange = (event) => {
      this.writeValue((event.target as HTMLInputElement).checked);
      onChange();
    };
  }

  registerOnTouched(fn: any): void {
    this.onBlur = fn;
  }

  writeValue(value: boolean): void {
    this.isChecked = value;
    this.cdRef.markForCheck();
  }

  setDisabledState(isDisabled: boolean) {
    this.isDisabled = isDisabled;
    this.cdRef.markForCheck();
  }
}

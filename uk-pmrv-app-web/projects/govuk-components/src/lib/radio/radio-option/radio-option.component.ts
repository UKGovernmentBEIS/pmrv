import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ContentChild,
  Input,
  TemplateRef,
  ViewChild,
} from '@angular/core';
import { ControlValueAccessor } from '@angular/forms';

import { ConditionalContentDirective } from '../../directives';

@Component({
  selector: 'govuk-radio-option',
  templateUrl: './radio-option.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RadioOptionComponent<T> implements ControlValueAccessor {
  @Input() value: T;
  @Input() label: string;
  @Input() hint?: string;
  @Input() divider?: boolean;
  @ContentChild(ConditionalContentDirective, { static: true }) readonly conditional: ConditionalContentDirective;
  @ViewChild('conditionalTemplate', { static: true }) conditionalTemplate: TemplateRef<any>;
  @ViewChild('optionTemplate', { static: true }) optionTemplate: TemplateRef<any>;
  isChecked: boolean;
  index: number;
  isDisabled: boolean;
  onChange: (_: T) => any;
  onBlur: () => any;
  groupIdentifier: string;

  constructor(readonly changeDetectorRef: ChangeDetectorRef) {}

  get identifier(): string {
    return `${this.groupIdentifier}-option${this.index}`;
  }

  registerOnChange(onChange: (_: T) => any): void {
    this.onChange = onChange;
  }

  registerOnTouched(onBlur: () => any): void {
    this.onBlur = onBlur;
  }

  writeValue(newValue: T): void {
    this.isChecked = newValue === this.value;
    this.setConditionalDisabledState();
    this.changeDetectorRef.detectChanges();
  }

  setDisabledState(isDisabled: boolean) {
    this.isDisabled = isDisabled;
    this.setConditionalDisabledState();
    this.changeDetectorRef.markForCheck();
  }

  private setConditionalDisabledState() {
    if (this.isChecked && !this.isDisabled) {
      this.conditional?.enableControls();
    } else {
      this.conditional?.disableControls();
    }
  }
}

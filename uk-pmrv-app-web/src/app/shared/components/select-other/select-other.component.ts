import {
  AfterContentInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ContentChild,
  Input,
} from '@angular/core';
import { ControlValueAccessor, FormControl, NgControl } from '@angular/forms';

import { ConditionalContentDirective, FormService, GovukTextWidthClass } from 'govuk-components';

@Component({
  selector: 'app-select-other',
  templateUrl: './select-other.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SelectOtherComponent implements ControlValueAccessor, AfterContentInit {
  @Input() hint: string;
  @Input() label: string;
  @Input() widthClass: GovukTextWidthClass;
  currentValue: string;
  onChange: (event: Event) => any;
  onBlur: () => any;
  @ContentChild(ConditionalContentDirective) private readonly conditional: ConditionalContentDirective;

  constructor(
    readonly ngControl: NgControl,
    private readonly formService: FormService,
    private readonly changeDetectorRef: ChangeDetectorRef,
  ) {
    ngControl.valueAccessor = this;
  }

  get control(): FormControl {
    return this.ngControl.control as FormControl;
  }

  get identifier(): string {
    return this.formService.getControlIdentifier(this.ngControl);
  }

  get conditionalId() {
    return `${this.identifier}-conditional`;
  }

  ngAfterContentInit(): void {
    this.toggleChildControls();
  }

  registerOnChange(onChange: (value: string) => any): void {
    this.onChange = (event: Event) => {
      this.currentValue = (event.target as HTMLInputElement).value;
      this.toggleChildControls();
      onChange(this.currentValue);
    };
  }

  registerOnTouched(onBlur: () => any): void {
    this.onBlur = onBlur;
  }

  writeValue(value: string): void {
    this.currentValue = value;
    this.toggleChildControls();
    this.changeDetectorRef.markForCheck();
  }

  private toggleChildControls(): void {
    if (this.conditional) {
      if (this.currentValue === 'OTHER') {
        this.conditional.enableControls();
      } else {
        this.conditional.disableControls();
      }
    }
  }
}

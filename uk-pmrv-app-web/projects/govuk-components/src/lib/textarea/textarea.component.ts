import { AfterViewInit, Component, Input, Optional, Self } from '@angular/core';
import { ControlContainer, ControlValueAccessor, NgControl } from '@angular/forms';

import { distinctUntilChanged, Subject, takeUntil, tap } from 'rxjs';

import { FormService } from '../form/form.service';
import { FormInput } from '../form/form-input';

/*
  eslint-disable
  @angular-eslint/prefer-on-push-component-change-detection,
  @typescript-eslint/no-empty-function
*/
@Component({
  selector: 'div[govuk-textarea]',
  templateUrl: './textarea.component.html',
})
export class TextareaComponent extends FormInput implements ControlValueAccessor, AfterViewInit {
  private static readonly WARNING_PERCENTAGE = 0.99;

  @Input() hint: string;
  @Input() rows = '5';
  @Input() maxLength: number;
  currentLabel = 'Insert text details';
  isLabelHidden = true;
  onBlur: (_: any) => any;

  private textSubject = new Subject<string>();

  constructor(
    @Self() @Optional() ngControl: NgControl,
    formService: FormService,
    @Optional() container: ControlContainer,
  ) {
    super(ngControl, formService, container);
  }

  @Input() set label(label: string) {
    this.currentLabel = label;
    this.isLabelHidden = false;
  }

  writeValue(): void {}

  registerOnChange(): void {}

  registerOnTouched(onBlur: any): void {
    this.onBlur = onBlur;
  }

  setDisabledState(): void {}

  getInputValue(event: Event): string {
    return (event.target as HTMLTextAreaElement).value;
  }

  handleBlur(value: string): void {
    this.onBlur(value);
  }

  exceedsMaxLength(length: number): boolean {
    return length > this.maxLength;
  }

  approachesMaxLength(length: number): boolean {
    return !this.exceedsMaxLength(length) && length >= this.maxLength * TextareaComponent.WARNING_PERCENTAGE;
  }

  ngAfterViewInit(): void {
    this.textSubject
      .asObservable()
      .pipe(
        distinctUntilChanged((prev, curr) => prev === curr),
        tap((value) => {
          const trimmedValue = value ? (value.trim() === '' ? null : value.trim()) : value;
          this.control.setValue(trimmedValue);
        }),
        takeUntil(this.destroy$),
      )
      .subscribe();
  }

  handleChange($event: Event) {
    const value = this.getInputValue($event);
    this.textSubject.next(value);
  }
}

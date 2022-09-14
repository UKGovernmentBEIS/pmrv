import { DatePipe } from '@angular/common';
import { Component, DoCheck, Input, OnDestroy, OnInit, Optional, Self } from '@angular/core';
import {
  ControlContainer,
  ControlValueAccessor,
  FormBuilder,
  NgControl,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';

import { BehaviorSubject, combineLatest, filter, takeUntil } from 'rxjs';

import { GovukValidators } from '../error-message/govuk-validators';
import { FormService } from '../form/form.service';
import { FormInput } from '../form/form-input';
import { DateInputValidators } from './date-input.validators';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'div[govuk-date-input]',
  templateUrl: './date-input.component.html',
  providers: [DatePipe],
})
export class DateInputComponent extends FormInput implements ControlValueAccessor, OnInit, DoCheck, OnDestroy {
  @Input() label: string;
  @Input() hint: string;
  @Input() min: Date;
  @Input() max: Date;
  formGroup = this.formBuilder.group(
    {
      day: [null, GovukValidators.pattern('[0-9]*', 'Insert valid day number')],
      month: [
        null,
        [
          GovukValidators.builder(
            `The month field is incorrect`,
            DateInputValidators.dateFieldValidator('month', 1, 12),
          ),
          GovukValidators.pattern('[0-9]*', 'Insert valid month number'),
        ],
      ],
      year: [
        null,
        [
          GovukValidators.builder(
            `The year field is incorrect`,
            DateInputValidators.dateFieldValidator('year', 1900, 2100),
          ),
          GovukValidators.pattern('[0-9]*', 'Insert valid year number'),
        ],
      ],
    },
    { updateOn: 'blur' },
  );

  private initialValidator: ValidatorFn;
  private touch$ = new BehaviorSubject(false);
  private min$ = new BehaviorSubject(null);
  private max$ = new BehaviorSubject(null);
  private onChange: (value: { year: number; month: number; day: number }) => void;
  private onBlur: () => any;

  constructor(
    @Self() @Optional() ngControl: NgControl,
    formService: FormService,
    private readonly datePipe: DatePipe,
    @Optional() container: ControlContainer,
    private readonly formBuilder: FormBuilder,
  ) {
    super(ngControl, formService, container);
  }

  ngOnInit(): void {
    super.ngOnInit();
    this.initialValidator = this.control.validator;
    this.control.setValidators([this.validate.bind(this)]);
    this.control.updateValueAndValidity();

    this.touch$
      .pipe(
        takeUntil(this.destroy$),
        filter((value) => value),
      )
      .subscribe(() => this.formGroup.markAllAsTouched());

    combineLatest([this.min$, this.max$])
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.control.updateValueAndValidity());

    if (this.control.validator(this.control)?.required) {
      this.formGroup.get('day').addValidators(Validators.required);
      this.formGroup.get('day').updateValueAndValidity();
      this.formGroup.get('month').addValidators(Validators.required);
      this.formGroup.get('month').updateValueAndValidity();
      this.formGroup.get('year').addValidators(Validators.required);
      this.formGroup.get('year').updateValueAndValidity();
    }

    this.formGroup.valueChanges
      .pipe(
        takeUntil(this.destroy$),
        filter(() => !!this.onChange),
      )
      .subscribe((value) => this.onChange(value));
  }

  ngDoCheck(): void {
    if (this.touch$.getValue() !== this.control.touched) {
      this.touch$.next(this.control.touched);
    }

    if (this.min$.getValue() !== this.min) {
      this.min$.next(this.min);
    }

    if (this.max$.getValue() !== this.max) {
      this.max$.next(this.max);
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.unsubscribe();
  }

  hasFieldError(identifier: string): boolean {
    if (this.shouldDisplayErrors) {
      const fieldControl = this.formGroup.get(identifier);

      return (
        fieldControl.invalid ||
        (!fieldControl.value && this.control.errors?.incomplete) ||
        this.control.errors?.minDate ||
        this.control.errors?.maxDate ||
        (identifier === 'day' && (this.control.errors?.leapYear || this.control.errors?.day))
      );
    } else {
      return false;
    }
  }

  registerOnChange(onChange: (_: Date) => void): void {
    this.onChange = (values) => onChange(this.formGroup.invalid ? null : DateInputValidators.buildDate(values));
  }

  registerOnTouched(onBlur: () => any): void {
    this.onBlur = onBlur;
  }

  writeValue(value: Date | null): void {
    if (value) {
      this.formGroup.setValue({
        day: value.getDate(),
        month: value.getMonth() + 1,
        year: value.getFullYear(),
      });
    }
  }

  setDisabledState(isDisabled: boolean): void {
    if (isDisabled) {
      this.formGroup.disable();
    } else {
      this.formGroup.enable();
    }
  }

  onInputBlur(): void {
    if (Object.values(this.formGroup.controls).every((control) => control.touched)) {
      this.onBlur();
    }
  }

  private buildErrorMessage(): string {
    return DateInputValidators.buildDate(this.formGroup.value) < this.min
      ? `This date must be the same as or after ${this.datePipe.transform(this.min, 'd MMMM y')}`
      : DateInputValidators.buildDate(this.formGroup.value) > this.max
      ? `This date must be the same as or before ${this.datePipe.transform(this.max, 'd MMMM y')}`
      : '';
  }

  private validate(): ValidationErrors {
    return {
      ...this.formGroup.get('day').errors,
      ...this.formGroup.get('month').errors,
      ...this.formGroup.get('year').errors,
      ...this.validateFormGroup(),
      ...this.validateControl(),
    };
  }

  private validateControl(): ValidationErrors {
    return {
      ...(this.initialValidator ? this.initialValidator(this.control) : null),
      ...GovukValidators.builder(
        this.buildErrorMessage(),
        DateInputValidators.minMaxDateValidator(this.min, this.max),
      )(this.control),
    };
  }

  private validateFormGroup(): ValidationErrors {
    return {
      ...GovukValidators.builder('The date is incomplete', DateInputValidators.dateIncompleteValidator)(this.formGroup),
      ...GovukValidators.builder('This is not a leap year', DateInputValidators.leapYearValidator)(this.formGroup),
      ...GovukValidators.builder(
        'The day field is incorrect',
        DateInputValidators.incorrectDayValidator,
      )(this.formGroup),
    };
  }
}

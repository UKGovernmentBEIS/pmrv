import { Directive, HostBinding, OnDestroy, OnInit } from '@angular/core';
import {
  ControlContainer,
  ControlValueAccessor,
  FormControl,
  FormGroupDirective,
  NgControl,
  NgForm,
} from '@angular/forms';

import { Subject, takeUntil } from 'rxjs';

import { FormService } from './form.service';

@Directive()
export abstract class FormInput implements ControlValueAccessor, OnInit, OnDestroy {
  @HostBinding('class.govuk-!-display-block') readonly govukDisplayBlock = true;
  @HostBinding('class.govuk-form-group') readonly govukFormGroupClass = true;
  protected readonly destroy$ = new Subject<void>();
  private isSubmitted = false;

  protected constructor(
    private readonly ngControl: NgControl,
    private readonly formService: FormService,
    private readonly container: ControlContainer,
  ) {
    ngControl.valueAccessor = this;
  }

  @HostBinding('class.govuk-form-group--error') get govukFormGroupErrorClass(): boolean {
    return this.shouldDisplayErrors;
  }

  get identifier(): string {
    return this.formService.getControlIdentifier(this.ngControl);
  }

  get control(): FormControl {
    return this.ngControl.control as FormControl;
  }

  get shouldDisplayErrors(): boolean {
    return this.control?.invalid && (!this.form || this.isSubmitted);
  }

  private get form(): FormGroupDirective | NgForm | null {
    return this.container &&
      (this.container.formDirective instanceof FormGroupDirective || this.container.formDirective instanceof NgForm)
      ? this.container.formDirective
      : null;
  }

  ngOnInit(): void {
    this.form?.ngSubmit.pipe(takeUntil(this.destroy$)).subscribe(() => (this.isSubmitted = true));
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  abstract writeValue(value: any): void;

  abstract registerOnChange(onChange: (value: any) => any): void;

  abstract registerOnTouched(onBlur: () => any): void;
}

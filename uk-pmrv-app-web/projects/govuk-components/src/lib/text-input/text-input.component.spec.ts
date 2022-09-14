import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { LabelDirective } from '../directives';
import { ErrorMessageComponent } from '../error-message/error-message.component';
import { GovukValidators } from '../error-message/govuk-validators';
import { TextInputComponent } from './text-input.component';

describe('TextInputComponent', () => {
  let testComponent: TextInputComponent;
  let numericComponent: TextInputComponent;
  let hostTestComponent: TestComponent;
  let hostNumericComponent: TestNumericComponent;
  let fixtureTestComponent: ComponentFixture<TestComponent>;
  let fixtureNumericComponent: ComponentFixture<TestNumericComponent>;

  @Component({
    template: `
      <div govuk-text-input [formControl]="control" [prefix]="prefix" [suffix]="suffix" label="First control"></div>
      <div govuk-text-input [formControl]="control" [prefix]="prefix" [suffix]="suffix">
        <ng-container govukLabel>Second control <span class="govuk-visually-hidden">hidden</span></ng-container>
      </div>
      <form [formGroup]="group">
        <div govuk-text-input formControlName="text" label="Form control"></div>
        <button type="submit">Submit</button>
      </form>
    `,
  })
  class TestComponent {
    control = new FormControl();
    group = new FormGroup(
      { text: new FormControl(null, { validators: GovukValidators.minLength(5, 'Enter a value') }) },
      { updateOn: 'submit' },
    );
    prefix: string;
    suffix: string;
  }

  @Component({
    template: '<div govuk-text-input [formControl]="control" inputType="number" [numberFormat]="format"></div>',
  })
  class TestNumericComponent {
    control = new FormControl(null, GovukValidators.max(5, 'Max test'));
    format: string;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TextInputComponent, TestNumericComponent, TestComponent, ErrorMessageComponent, LabelDirective],
      imports: [ReactiveFormsModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixtureTestComponent = TestBed.createComponent(TestComponent);
    fixtureNumericComponent = TestBed.createComponent(TestNumericComponent);
    hostTestComponent = fixtureTestComponent.componentInstance;
    hostNumericComponent = fixtureNumericComponent.componentInstance;
    testComponent = fixtureTestComponent.debugElement.query(By.directive(TextInputComponent)).componentInstance;
    numericComponent = fixtureNumericComponent.debugElement.query(By.directive(TextInputComponent)).componentInstance;
    fixtureTestComponent.detectChanges();
    fixtureNumericComponent.detectChanges();
  });

  it('should create', () => {
    expect(testComponent).toBeTruthy();
    expect(numericComponent).toBeTruthy();
  });

  it('should disable the input', () => {
    hostTestComponent.control.disable();
    fixtureTestComponent.detectChanges();

    const hostElement: HTMLElement = fixtureTestComponent.nativeElement;
    const input = hostElement.querySelector<HTMLInputElement>('input');

    expect(input.disabled).toBeTruthy();
  });

  it('should assign value', () => {
    const stringValue = 'This is a test';
    hostTestComponent.control.patchValue(stringValue);
    fixtureTestComponent.detectChanges();

    const hostElement: HTMLElement = fixtureTestComponent.nativeElement;
    const input = hostElement.querySelector<HTMLInputElement>('input');
    expect(input.value).toEqual(stringValue);
  });

  it('should emit value', () => {
    const stringValue = 'This is a test';
    const input = fixtureTestComponent.debugElement.query(By.css('input'));

    expect(hostTestComponent.control.value).toBeNull();

    input.triggerEventHandler('input', { target: { value: stringValue } });

    fixtureTestComponent.detectChanges();

    expect(hostTestComponent.control.value).toEqual(stringValue);
  });

  it('should emit numeric value', () => {
    const numericValue = '2.';
    const input = fixtureNumericComponent.debugElement.query(By.css('input'));
    input.triggerEventHandler('input', { target: { value: numericValue } });
    fixtureNumericComponent.detectChanges();

    expect(hostNumericComponent.control.value).toEqual(2);
    expect(hostNumericComponent.control.value).not.toEqual('2.');
    expect(hostNumericComponent.control.value).not.toEqual('2');
  });

  it('should show and hide invalid number errors while typing if elements are not in form', () => {
    hostNumericComponent.control.markAsTouched();
    const element: HTMLElement = fixtureNumericComponent.nativeElement;
    expect(element.querySelector('.govuk-error-message')).toBeNull();

    const input = fixtureNumericComponent.debugElement.query(By.css('input'));
    input.triggerEventHandler('input', { target: { value: '2.a' } });
    fixtureNumericComponent.detectChanges();

    expect(element.querySelector('.govuk-error-message')).not.toBeNull();

    input.triggerEventHandler('input', { target: { value: '2.' } });
    fixtureNumericComponent.detectChanges();
    expect(element.querySelector('.govuk-error-message')).toBeNull();

    input.triggerEventHandler('input', { target: { value: '5.4' } });
    fixtureNumericComponent.detectChanges();
    expect(element.querySelector('.govuk-error-message')).not.toBeNull();
  });

  it('should display errors on submission', () => {
    const element: HTMLElement = fixtureTestComponent.nativeElement;
    const formControl = fixtureTestComponent.debugElement.query(By.css('input[name="text"]'));

    hostTestComponent.group.get('text').patchValue('  abc  ');
    fixtureTestComponent.detectChanges();

    expect(hostTestComponent.group.value).toEqual({ text: 'abc' });
    expect(formControl.classes['govuk-input--error']).toBeFalsy();

    element.querySelector('form').submit();
    fixtureTestComponent.detectChanges();

    expect(hostTestComponent.group.value).toEqual({ text: 'abc' });
    expect(formControl.classes['govuk-input--error']).toBeTruthy();
  });

  it('should format on blur and revert on focus', () => {
    hostNumericComponent.format = '1.0-0';
    fixtureNumericComponent.detectChanges();
    const input = fixtureNumericComponent.debugElement.query(By.css('input'));
    hostNumericComponent.control.patchValue('2000');
    fixtureNumericComponent.detectChanges();

    expect(input.nativeElement.value).toEqual('2,000');

    input.triggerEventHandler('focus', null);
    expect(input.nativeElement.value).toEqual('2000');
  });

  it('should display a prefix or suffix if provided', () => {
    const element: HTMLElement = fixtureTestComponent.nativeElement;

    const getPrefix = () => element.querySelector<HTMLDivElement>('.govuk-input__prefix');
    const getSuffix = () => element.querySelector<HTMLDivElement>('.govuk-input__suffix');

    expect(getPrefix()).toBeNull();
    expect(getSuffix()).toBeNull();

    hostTestComponent.prefix = 'Eur';
    fixtureTestComponent.detectChanges();

    expect(getPrefix().textContent).toEqual('Eur');
    expect(getSuffix()).toBeNull();

    hostTestComponent.suffix = '%';
    fixtureTestComponent.detectChanges();

    expect(getPrefix().textContent).toEqual('Eur');
    expect(getSuffix().textContent).toEqual('%');

    hostTestComponent.prefix = undefined;
    fixtureTestComponent.detectChanges();

    expect(getPrefix()).toBeNull();
    expect(getSuffix().textContent).toEqual('%');
  });

  it('should set null value for empty numeric input', () => {
    hostNumericComponent.control.patchValue('');
    fixtureTestComponent.detectChanges();
    expect(hostNumericComponent.control.value).toEqual(null);
    expect(hostNumericComponent.control.value).not.toEqual(0);
    expect(hostNumericComponent.control.value).not.toEqual('');
  });

  it('should display custom labels', () => {
    const element: HTMLElement = fixtureTestComponent.nativeElement;
    const labels = Array.from(element.querySelectorAll('label'));

    expect(labels.map((label) => label.textContent.trim())).toEqual([
      'First control',
      'Second control hidden',
      'Form control',
    ]);
    expect(element.querySelector('.govuk-visually-hidden').textContent).toEqual('hidden');
  });
});

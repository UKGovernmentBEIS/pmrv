import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { ConditionalContentDirective } from '../directives';
import { ErrorMessageComponent } from '../error-message/error-message.component';
import { GovukValidators } from '../error-message/govuk-validators';
import { FieldsetDirective, FieldsetHintDirective, LegendDirective } from '../fieldset';
import { CheckboxComponent } from './checkbox/checkbox.component';
import { CheckboxesComponent } from './checkboxes.component';

describe('CheckboxesComponent', () => {
  let component: CheckboxesComponent<any>;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let element: HTMLElement;

  @Component({
    template: `
      <form [formGroup]="form">
        <div govuk-checkboxes formControlName="checkboxes" legend="Some options" hint="Choose an option">
          <govuk-checkbox label="First" [value]="0"></govuk-checkbox>
          <govuk-checkbox label="Second" [value]="1"></govuk-checkbox>
          <govuk-checkbox label="Third" [value]="2">
            <ng-container govukConditionalContent>
              <p>The third option is more complicated</p>
            </ng-container>
          </govuk-checkbox>
        </div>
        <div govuk-checkboxes formControlName="extraCheckboxes">
          <ng-container govukLegend>Some extra options</ng-container>
          <govuk-checkbox label="First" [value]="0"></govuk-checkbox>
          <govuk-checkbox label="Second" [value]="1"></govuk-checkbox>
        </div>
      </form>
    `,
  })
  class TestComponent {
    form: FormGroup = new FormGroup({
      checkboxes: new FormControl(null),
      extraCheckboxes: new FormControl(null),
    });
  }

  const getAllCheckboxes = () => fixture.debugElement.queryAll(By.css('input[type="checkbox"]'));

  const getAllInputs = () => element.querySelectorAll('input');

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule],
      declarations: [
        CheckboxComponent,
        CheckboxesComponent,
        ConditionalContentDirective,
        FieldsetDirective,
        FieldsetHintDirective,
        LegendDirective,
        TestComponent,
        ErrorMessageComponent,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(CheckboxesComponent)).componentInstance;
    element = fixture.nativeElement;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should hydrate form with value', () => {
    hostComponent.form.setValue({ checkboxes: [0, 1], extraCheckboxes: [] });
    fixture.detectChanges();

    expect(getAllCheckboxes()[0].properties.checked).toBeTruthy();
    expect(getAllCheckboxes()[1].properties.checked).toBeTruthy();
    expect(getAllCheckboxes()[2].properties.checked).toBeFalsy();
  });

  it('should emit events on user input', () => {
    getAllInputs()[2].click();
    fixture.detectChanges();

    expect(hostComponent.form.value).toEqual({ checkboxes: [2], extraCheckboxes: null });

    getAllInputs()[1].click();
    fixture.detectChanges();

    expect(hostComponent.form.value).toEqual({ checkboxes: [1, 2], extraCheckboxes: null });

    getAllInputs()[1].click();
    fixture.detectChanges();

    expect(hostComponent.form.value).toEqual({ checkboxes: [2], extraCheckboxes: null });
  });

  it('should disable and re-enable the inputs', () => {
    hostComponent.form.disable();
    fixture.detectChanges();

    getAllInputs().forEach((input) => expect(input.disabled).toBeTruthy());

    hostComponent.form.enable();
    fixture.detectChanges();

    getAllInputs().forEach((input) => expect(input.disabled).toBeFalsy());
  });

  it('should get touched on blur of all checkboxes', () => {
    getAllCheckboxes()[0].triggerEventHandler('blur', {});
    fixture.detectChanges();

    expect(component.options.get(0).isTouched).toBeTruthy();
    expect(component.control.touched).toBeFalsy();

    getAllCheckboxes()[1].triggerEventHandler('blur', {});
    fixture.detectChanges();

    expect(component.options.get(1).isTouched).toBeTruthy();
    expect(component.control.touched).toBeFalsy();

    getAllCheckboxes()[2].triggerEventHandler('blur', {});
    fixture.detectChanges();

    expect(component.options.get(2).isTouched).toBeTruthy();
    expect(component.control.touched).toBeTruthy();
  });

  it('should reveal conditionally revealing content', () => {
    const element: HTMLElement = fixture.nativeElement;

    expect(element.querySelector('.govuk-checkboxes__conditional')).toBeTruthy();
    expect(element.querySelector('.govuk-checkboxes__conditional--hidden')).toBeTruthy();

    getAllInputs()[1].click();
    fixture.detectChanges();

    expect(element.querySelector('.govuk-checkboxes__conditional--hidden')).toBeTruthy();

    getAllInputs()[2].click();
    fixture.detectChanges();

    expect(element.querySelector('.govuk-checkboxes__conditional--hidden')).toBeFalsy();
  });

  it('should display the error message when the form is submitted', () => {
    const findErrorMessage = () => fixture.debugElement.query(By.directive(ErrorMessageComponent));
    getAllCheckboxes().forEach((checkbox, index) => {
      checkbox.triggerEventHandler('change', { target: { value: index } });
      checkbox.triggerEventHandler('blur', { target: { value: index } });
    });
    fixture.detectChanges();

    expect(findErrorMessage()).toBeFalsy();

    hostComponent.form.get('checkboxes').setValidators(GovukValidators.builder('Error', () => ({ required: true })));
    hostComponent.form.get('checkboxes').updateValueAndValidity();
    const element: HTMLElement = fixture.nativeElement;
    element.querySelector('form').submit();
    fixture.detectChanges();

    expect(findErrorMessage()).toBeTruthy();
  });

  it('should display a simple legend', () => {
    expect(element.querySelector('legend').textContent).toEqual('Some options');
  });

  it('should display a simple hint', () => {
    expect(element.querySelector('.govuk-hint').textContent).toEqual('Choose an option');
  });

  it('should display a custom legend', () => {
    expect(element.querySelectorAll('legend')[1].textContent).toEqual('Some extra options');
  });
});

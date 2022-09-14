import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { GovukComponentsModule, GovukValidators } from 'govuk-components';

import { BasePage, CountryServiceStub } from '../../../testing';
import { CountryService } from '../../core/services/country.service';
import { PhoneInputComponent } from './phone-input.component';

describe('PhoneInputComponent', () => {
  let component: PhoneInputComponent;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `
      <form [formGroup]="form">
        <div app-phone-input formControlName="firstPhone"></div>
        <button type="submit">Submit</button>
      </form>
    `,
  })
  class TestComponent {
    form = new FormGroup({ firstPhone: new FormControl(null, [GovukValidators.required('This is required')]) });
  }

  class Page extends BasePage<TestComponent> {
    get options() {
      return this.queryAll('option');
    }

    get countryCode() {
      return this.query<HTMLSelectElement>('select[name="firstPhone.countryCode"]');
    }

    get countryCodeValue() {
      return this.getInputValue(this.countryCode);
    }

    set countryCodeValue(value: string) {
      this.setInputValue('select[name="firstPhone.countryCode"]', value);
    }

    get phone() {
      return this.query<HTMLInputElement>('input[name="firstPhone"]');
    }

    get phoneValue() {
      return this.getInputValue(this.phone);
    }

    set phoneValue(value: string) {
      this.setInputValue('input[name="firstPhone"]', value);
    }

    get errorMessage() {
      return this.query<HTMLSpanElement>('.govuk-error-message');
    }

    get groupError() {
      return this.query<HTMLDivElement>('.govuk-form-group--error');
    }

    get submitButton() {
      return this.query('button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, GovukComponentsModule],
      declarations: [PhoneInputComponent, TestComponent],
      providers: [{ provide: CountryService, useClass: CountryServiceStub }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(PhoneInputComponent)).componentInstance;
    hostComponent = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the govuk countries', () => {
    expect(page.options[1].textContent).toEqual('AF (93)');
  });

  it('should apply identifiers', () => {
    expect(page.countryCode.id).toEqual('firstPhone.countryCode');
    expect(page.phone.id).toEqual('firstPhone');
  });

  it('should disable and re-enable control', () => {
    hostComponent.form.get('firstPhone').disable();
    fixture.detectChanges();

    expect(page.phone.disabled).toBeTruthy();
    expect(page.countryCode.disabled).toBeTruthy();

    hostComponent.form.get('firstPhone').enable();
    fixture.detectChanges();

    expect(page.phone.disabled).toBeFalsy();
    expect(page.countryCode.disabled).toBeFalsy();
  });

  it('should touch controls', () => {
    expect(page.countryCode.classList).not.toContain('ng-touched');
    expect(page.phone.classList).not.toContain('ng-touched');

    hostComponent.form.markAllAsTouched();
    fixture.detectChanges();

    expect(page.countryCode.classList).toContain('ng-touched');
    expect(page.phone.classList).toContain('ng-touched');
  });

  it('should touch control if both controls are touched', () => {
    expect(hostComponent.form.get('firstPhone').touched).toBeFalsy();

    page.countryCode.focus();
    page.countryCode.blur();
    fixture.detectChanges();

    expect(hostComponent.form.get('firstPhone').touched).toBeFalsy();

    page.phone.focus();
    page.phone.blur();
    fixture.detectChanges();

    expect(hostComponent.form.get('firstPhone').touched).toBeTruthy();
  });

  it('should apply a supplied value', () => {
    hostComponent.form.get('firstPhone').setValue({ countryCode: '30', number: '1234567890' });
    fixture.detectChanges();

    expect(page.countryCodeValue).toEqual('30');
    expect(page.phoneValue).toEqual('1234567890');
  });

  it('should accept empty values', () => {
    hostComponent.form.get('firstPhone').setValue(null);
    fixture.detectChanges();

    expect(component.formGroup.value).toEqual({ countryCode: null, number: null });
  });

  it('should display errors', () => {
    hostComponent.form
      .get('firstPhone')
      .setValidators(GovukValidators.incomplete('Enter both country code and number'));

    expect(page.errorMessage).toBeFalsy();
    expect(page.groupError).toBeFalsy();

    page.phoneValue = 'test';
    fixture.detectChanges();

    expect(page.errorMessage).toBeFalsy();
    expect(page.groupError).toBeFalsy();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorMessage).toBeTruthy();
    expect(page.groupError).toBeTruthy();
    expect(page.errorMessage.textContent.trim()).toEqual('Error: Enter both country code and number');

    page.phoneValue = '';
    fixture.detectChanges();

    expect(page.errorMessage).toBeFalsy();
    expect(page.groupError).toBeFalsy();

    hostComponent.form
      .get('firstPhone')
      .setValidators([
        GovukValidators.empty('Enter your phone number'),
        GovukValidators.incomplete('Enter both country code and number'),
      ]);
    hostComponent.form.get('firstPhone').updateValueAndValidity();
    fixture.detectChanges();

    expect(page.errorMessage).toBeTruthy();
    expect(page.groupError).toBeTruthy();
    expect(page.errorMessage.textContent.trim()).toEqual('Error: Enter your phone number');
  });

  it('should return empty value for placeholder', () => {
    page.countryCodeValue = '';
    fixture.detectChanges();

    expect(component.formGroup.value).toEqual({ countryCode: '', number: null });
    expect(hostComponent.form.value.firstPhone).toEqual({ countryCode: null, number: null });
  });
});

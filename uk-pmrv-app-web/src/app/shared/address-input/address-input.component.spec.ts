import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlContainer, FormGroup, FormGroupName } from '@angular/forms';
import { By } from '@angular/platform-browser';

import { GovukValidators, SelectComponent, TextInputComponent } from 'govuk-components';

import { AddressDTO } from 'pmrv-api';

import { changeInputValue, CountryServiceStub, getInputValue } from '../../../testing';
import { CountryService } from '../../core/services/country.service';
import { SharedModule } from '../shared.module';
import { AddressInputComponent } from './address-input.component';

describe('AddressInputComponent', () => {
  let component: AddressInputComponent;
  let fixture: ComponentFixture<TestComponent>;
  let hostComponent: TestComponent;

  @Component({
    template: `
      <form [formGroup]="form">
        <fieldset govukFieldset>
          <legend govukLegend>What is your address?</legend>
          <app-address-input formGroupName="address"></app-address-input>
        </fieldset>
      </form>
    `,
  })
  class TestComponent {
    form = new FormGroup({
      address: new FormGroup(AddressInputComponent.controlsFactory(null), [
        GovukValidators.required('This is required'),
      ]),
    });
  }

  const address: AddressDTO = {
    line1: 'Street',
    line2: 'Neighbourhood',
    city: 'City',
    postcode: '23415',
    country: 'GR',
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      declarations: [TestComponent],
      providers: [{ provide: CountryService, useClass: CountryServiceStub }],
    })
      .overrideComponent(AddressInputComponent, {
        set: { providers: [{ provide: ControlContainer, useExisting: FormGroupName }] },
      })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(AddressInputComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render address labels', () => {
    const inputs = fixture.debugElement.queryAll(By.directive(TextInputComponent));
    const getLabel = (element: HTMLElement) => element.querySelector('label');
    expect(inputs.map((input) => getLabel(input.nativeElement).textContent)).toEqual([
      'Address line 1',
      'Address line 2 (optional)',
      'Town or city',
      'Postcode',
    ]);

    const select: HTMLElement = fixture.debugElement.query(By.directive(SelectComponent)).nativeElement;
    expect(select.querySelector('label').textContent.trim()).toEqual('Country');
  });

  it('should set autocomplete attributes', () => {
    const inputs = fixture.debugElement.queryAll(By.directive(TextInputComponent));
    const getInput = (element: HTMLElement) => element.querySelector('input');
    expect(inputs.map((input) => getInput(input.nativeElement).autocomplete)).toEqual([
      'address-line1',
      'address-line2',
      'address-level2',
      'postal-code',
    ]);
  });

  it('should update values on setValue', () => {
    hostComponent.form.get('address').setValue(address);
    fixture.detectChanges();

    expect(getInputValue(fixture, '#address\\.line1')).toEqual(address.line1);
    expect(getInputValue(fixture, '#address\\.line2')).toEqual(address.line2);
    expect(getInputValue(fixture, '#address\\.city')).toEqual(address.city);
    expect(getInputValue(fixture, '#address\\.postcode')).toEqual(address.postcode);
    expect(getInputValue(fixture, '#address\\.country')).toEqual(`2: ${address.country}`);
  });

  it('should apply field validations', () => {
    hostComponent.form.markAllAsTouched();
    fixture.detectChanges();

    expect(hostComponent.form.invalid).toBeTruthy();
    expect(Object.values(hostComponent.form.controls).every((control) => control.invalid)).toBeTruthy();

    const line1 = hostComponent.form.get('address.line1');
    expect(line1.errors).toEqual({ required: 'Enter an address' });
    changeInputValue(fixture, '#address\\.line1', 'a'.repeat(257));
    fixture.detectChanges();
    expect(line1.errors).toEqual({ maxlength: 'The address should not be more than 255 characters' });
    changeInputValue(fixture, '#address\\.line1', address.line1);
    fixture.detectChanges();
    expect(hostComponent.form.invalid).toBeTruthy();
    expect(line1.valid).toBeTruthy();

    const line2 = hostComponent.form.get('address.line2');
    changeInputValue(fixture, '#address\\.line2', 'a'.repeat(257));
    fixture.detectChanges();
    expect(line2.errors).toEqual({ maxlength: 'The address should not be more than 255 characters' });
    changeInputValue(fixture, '#address\\.line2', address.line2);
    fixture.detectChanges();
    expect(hostComponent.form.invalid).toBeTruthy();
    expect(line2.valid).toBeTruthy();

    const city = hostComponent.form.get('address.city');
    expect(city.errors).toEqual({ required: 'Enter a town or city' });
    changeInputValue(fixture, '#address\\.city', 'a'.repeat(257));
    fixture.detectChanges();
    expect(city.errors).toEqual({ maxlength: 'The city should not be more than 255 characters' });
    changeInputValue(fixture, '#address\\.city', address.city);
    fixture.detectChanges();
    expect(hostComponent.form.invalid).toBeTruthy();
    expect(city.valid).toBeTruthy();

    const country = hostComponent.form.get('address.country');
    expect(country.errors).toEqual({ required: 'Enter a country' });
    changeInputValue(fixture, '#address\\.country', address.country);
    fixture.detectChanges();
    expect(hostComponent.form.invalid).toBeTruthy();
    expect(country.valid).toBeTruthy();

    const postcode = hostComponent.form.get('address.postcode');
    expect(postcode.errors).toEqual({ required: 'Enter a postcode' });
    changeInputValue(fixture, '#address\\.postcode', 'a'.repeat(65));
    fixture.detectChanges();
    expect(postcode.errors).toEqual({ maxlength: 'The postcode should not be more than 64 characters' });
    changeInputValue(fixture, '#address\\.postcode', address.postcode);
    fixture.detectChanges();
    expect(hostComponent.form.valid).toBeTruthy();
    expect(postcode.valid).toBeTruthy();
  });
});

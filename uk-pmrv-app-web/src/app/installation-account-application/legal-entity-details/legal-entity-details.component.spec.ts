import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlContainer, FormGroupName } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { cloneDeep } from 'lodash-es';

import { ErrorMessageComponent } from 'govuk-components';

import { LegalEntitiesService } from 'pmrv-api';

import { buttonClick, changeInputValue, CountryServiceStub, getInputValue } from '../../../testing';
import { LegalEntitiesServiceStub } from '../../../testing/legal-entities.service.stub';
import { CountryService } from '../../core/services/country.service';
import { AddressInputComponent } from '../../shared/address-input/address-input.component';
import { LegalEntityDetails } from '../../shared/interfaces/legal-entity';
import { SharedModule } from '../../shared/shared.module';
import { legalEntityFormFactory } from '../factories/legal-entity-form.factory';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';
import { LegalEntityDetailsComponent } from './legal-entity-details.component';

const value: LegalEntityDetails = {
  name: 'test',
  type: 'PARTNERSHIP',
  referenceNumber: 'ab123456',
  noReferenceNumberReason: '',
  address: {
    line1: 'line1',
    line2: 'line2',
    city: 'city',
    country: 'GR',
    postcode: 'postcode',
  },
};

describe('LegalEntityDetailsComponent', () => {
  let component: LegalEntityDetailsComponent;
  let fixture: ComponentFixture<LegalEntityDetailsComponent>;
  let router: Router;
  let store: InstallationAccountApplicationStore;

  @NgModule({
    imports: [CommonModule],
    entryComponents: [ErrorMessageComponent],
  })
  class FormErrorTestModule {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, FormErrorTestModule],
      declarations: [LegalEntityDetailsComponent],
      providers: [
        { provide: CountryService, useClass: CountryServiceStub },
        { provide: LegalEntitiesService, useClass: LegalEntitiesServiceStub },
        legalEntityFormFactory,
        InstallationAccountApplicationStore,
      ],
    })
      .overrideComponent(AddressInputComponent, {
        set: { providers: [{ provide: ControlContainer, useExisting: FormGroupName }] },
      })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LegalEntityDetailsComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    store = TestBed.inject(InstallationAccountApplicationStore);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fill form from setState', () => {
    component.form.patchValue(value);
    fixture.detectChanges();

    expect(getInputValue(fixture, '#name')).toBe(value.name);
    expect(getInputValue(fixture, '#type-option0')).toBeFalsy();
    expect(getInputValue(fixture, '#type-option1')).toBeTruthy();
    expect(getInputValue(fixture, '#type-option2')).toBeFalsy();
    expect(getInputValue(fixture, '#referenceNumber')).toBe(value.referenceNumber);
    expect(getInputValue(fixture, '#noReferenceNumberReason')).toBe(value.noReferenceNumberReason);
    expect(getInputValue(fixture, '#address\\.line1')).toBe(value.address.line1);
    expect(getInputValue(fixture, '#address\\.line2')).toBe(value.address.line2);
    expect(getInputValue(fixture, '#address\\.city')).toBe(value.address.city);
    expect(getInputValue(fixture, '#address\\.country')).toBe('2: ' + value.address.country);
    expect(getInputValue(fixture, '#address\\.postcode')).toBe(value.address.postcode);

    expect(component.form.value).toEqual(value);
  });

  it('should submit only if address is filled', () => {
    const mock = cloneDeep(value);
    Object.keys(mock.address).forEach((key) => (mock.address[key] = null));
    component.form.patchValue(mock);
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    changeInputValue(fixture, '#address\\.line1', 'line1');
    changeInputValue(fixture, '#address\\.city', 'city');
    changeInputValue(fixture, '#address\\.country', 'GR');
    buttonClick(fixture);

    expect(navigateSpy).not.toHaveBeenCalled();

    changeInputValue(fixture, '#address\\.postcode', 'post code');
    buttonClick(fixture);

    expect(navigateSpy).toHaveBeenCalled();
  });

  it('should submit only if referenceNumber is filled', () => {
    component.form.patchValue(value);
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    changeInputValue(fixture, '#referenceNumber', null);
    changeInputValue(fixture, '#noReferenceNumberReason', null);
    buttonClick(fixture);

    expect(navigateSpy).not.toHaveBeenCalled();

    changeInputValue(fixture, '#referenceNumber', 'ab123456');
    buttonClick(fixture);

    expect(navigateSpy).toHaveBeenCalled();
  });

  it('should submit only if referenceNumber has valid length', () => {
    component.form.patchValue(value);
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    changeInputValue(fixture, '#referenceNumber', 'aReferenceNumberWithLengthMoreThan15Chars');
    buttonClick(fixture);

    expect(navigateSpy).not.toHaveBeenCalled();

    changeInputValue(fixture, '#referenceNumber', 'validLengthRN');
    buttonClick(fixture);

    expect(navigateSpy).toHaveBeenCalled();
  });

  it('should submit only if noReferenceNumberReason is completed', () => {
    component.form.patchValue(value);
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
    changeInputValue(fixture, '#referenceNumber', null);
    changeInputValue(fixture, '#noReferenceNumberReason', null);

    buttonClick(fixture);

    expect(navigateSpy).not.toHaveBeenCalled();

    changeInputValue(fixture, '#noReferenceNumberReason', 'noCompanies ref details');
    buttonClick(fixture);

    expect(navigateSpy).toHaveBeenCalled();
  });

  it('should not submit if legal entity exists', () => {
    component.form.patchValue(value);
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    changeInputValue(fixture, '#name', 'Mock Entity');
    buttonClick(fixture);

    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('should not amend if not reviewed', () => {
    const nextStepSpy = jest.spyOn(store, 'nextStep').mockImplementation();
    const amendSpy = jest.spyOn(store, 'amend').mockImplementation();
    component.form.patchValue(value);

    buttonClick(fixture);

    expect(nextStepSpy).toHaveBeenCalled();
    expect(amendSpy).not.toHaveBeenCalled();
  });

  it('should amend if reviewed', () => {
    const nextStepSpy = jest.spyOn(store, 'nextStep').mockImplementation();
    const amendSpy = jest.spyOn(store, 'amend').mockImplementation().mockReturnValue(of(null));
    store.setState({ ...store.getState(), isReviewed: true });
    component.form.patchValue(value);

    buttonClick(fixture);

    expect(nextStepSpy).toHaveBeenCalled();
    expect(amendSpy).toHaveBeenCalled();
  });
});

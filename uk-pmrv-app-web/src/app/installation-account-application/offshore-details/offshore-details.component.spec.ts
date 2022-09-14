import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { cloneDeep } from 'lodash-es';

import { AccountsService } from 'pmrv-api';

import { BasePage } from '../../../testing';
import { AccountsServiceStub } from '../../../testing/accounts.service.stub';
import { SharedModule } from '../../shared/shared.module';
import { installationFormFactory } from '../factories/installation-form.factory';
import { InstallationTypeGroup, OffshoreDetails } from '../installation-type/installation';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';
import { OffshoreDetailsComponent } from './offshore-details.component';

const value: InstallationTypeGroup & OffshoreDetails = {
  name: 'Name',
  siteName: 'Site name',
  type: 'OFFSHORE',
  latitude: { degree: 1, minute: 2, second: 3, cardinalDirection: 'EAST' },
  longitude: { degree: 4, minute: 5, second: 6, cardinalDirection: 'SOUTH' },
};

describe('OffshoreDetailsComponent', () => {
  let component: OffshoreDetailsComponent;
  let fixture: ComponentFixture<OffshoreDetailsComponent>;
  let store: InstallationAccountApplicationStore;
  let router: Router;
  let page: Page;

  class Page extends BasePage<OffshoreDetailsComponent> {
    get nameValue() {
      return this.getInputValue('[name="name"]');
    }

    set nameValue(value: string) {
      this.setInputValue('[name="name"]', value);
    }

    get siteNameValue() {
      return this.getInputValue('[name="siteName"]');
    }

    get latitudeDegreeValue() {
      return this.getInputValue('[name="latitude.degree"]');
    }

    get latitudeMinuteValue() {
      return this.getInputValue('[name="latitude.minute"]');
    }

    set latitudeMinuteValue(value: string) {
      this.setInputValue('[name="latitude.minute"]', value);
    }

    get latitudeSecondValue() {
      return this.getInputValue('[name="latitude.second"]');
    }

    set longitudeDegreeValue(value: string) {
      this.setInputValue('[name="longitude.degree"]', value);
    }

    set longitudeMinuteValue(value: string) {
      this.setInputValue('[name="longitude.minute"]', value);
    }

    set longitudeSecondValue(value: string) {
      this.setInputValue('[name="longitude.second"]', value);
    }

    set longitudeCardinalDirection(value: string) {
      this.setInputValue('[name="longitude.cardinalDirection"]', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OffshoreDetailsComponent],
      imports: [SharedModule, RouterTestingModule],
      providers: [
        InstallationAccountApplicationStore,
        installationFormFactory,
        { provide: AccountsService, useClass: AccountsServiceStub },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OffshoreDetailsComponent);
    component = fixture.componentInstance;
    store = TestBed.inject(InstallationAccountApplicationStore);
    router = TestBed.inject(Router);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fill form from store and navigate', () => {
    component.form.patchValue(value);
    fixture.detectChanges();

    expect(page.nameValue).toBe(value.name);
    expect(page.siteNameValue).toBe(value.siteName);
    expect(page.latitudeDegreeValue).toBe(String(value.latitude.degree));
    expect(page.latitudeMinuteValue).toBe(String(value.latitude.minute));
    expect(page.latitudeSecondValue).toBe(String(value.latitude.second));
  });

  it('should not save to store until form is valid', () => {
    const mockValue = cloneDeep(value);
    mockValue.longitude = {
      degree: null,
      minute: null,
      second: null,
      cardinalDirection: null,
    };
    component.form.patchValue(mockValue);
    const storeSpy = jest.spyOn(store, 'updateTask');
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    page.submitButton.click();

    expect(storeSpy).not.toHaveBeenCalled();
    expect(navigateSpy).not.toHaveBeenCalled();

    page.longitudeDegreeValue = '180';
    page.longitudeMinuteValue = '1';
    page.longitudeSecondValue = '1';
    page.longitudeCardinalDirection = 'EAST';

    page.submitButton.click();

    expect(storeSpy).not.toHaveBeenCalled();
    expect(navigateSpy).not.toHaveBeenCalled();

    page.longitudeDegreeValue = '50';
    page.longitudeMinuteValue = '60';
    page.longitudeSecondValue = '1';

    page.submitButton.click();

    expect(storeSpy).not.toHaveBeenCalled();
    expect(navigateSpy).not.toHaveBeenCalled();

    page.longitudeDegreeValue = '40.5';
    page.longitudeMinuteValue = '5.7';
    page.longitudeSecondValue = '1';

    page.submitButton.click();

    expect(storeSpy).not.toHaveBeenCalled();
    expect(navigateSpy).not.toHaveBeenCalled();

    page.longitudeDegreeValue = '4';
    page.longitudeMinuteValue = '5';
    page.longitudeSecondValue = '6.5';
    page.nameValue = 'Existing';

    page.submitButton.click();

    expect(storeSpy).not.toHaveBeenCalled();
    expect(navigateSpy).not.toHaveBeenCalled();

    page.nameValue = 'Name';

    page.submitButton.click();
    expect(storeSpy).toHaveBeenCalled();
    expect(navigateSpy).toHaveBeenCalled();
    expect(component.form.value.longitude.degree).toBe(4);
    expect(component.form.value.longitude.minute).toBe(5);
    expect(component.form.value.longitude.second).toBe(6.5);
  });

  it('should not amend if not reviewed', () => {
    const nextStepSpy = jest.spyOn(store, 'nextStep').mockImplementation();
    const amendSpy = jest.spyOn(store, 'amend').mockImplementation();

    component.form.patchValue(value);
    fixture.detectChanges();
    page.submitButton.click();

    expect(nextStepSpy).toHaveBeenCalled();
    expect(amendSpy).not.toHaveBeenCalled();
  });

  it('should amend if reviewed', () => {
    const nextStepSpy = jest.spyOn(store, 'nextStep').mockImplementation();
    const amendSpy = jest.spyOn(store, 'amend').mockImplementation().mockReturnValue(of(null));
    store.setState({ ...store.getState(), isReviewed: true });

    component.form.patchValue(value);
    fixture.detectChanges();
    page.submitButton.click();

    expect(nextStepSpy).toHaveBeenCalled();
    expect(amendSpy).toHaveBeenCalled();
  });
});

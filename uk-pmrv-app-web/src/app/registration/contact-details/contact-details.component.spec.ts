import { ChangeDetectorRef, Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlContainer, FormGroupName } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { OperatorUserRegistrationDTO } from 'pmrv-api';

import { BasePage, CountryServiceStub } from '../../../testing';
import { CountryService } from '../../core/services/country.service';
import { AddressInputComponent } from '../../shared/address-input/address-input.component';
import { SharedModule } from '../../shared/shared.module';
import { UserRegistrationStore } from '../store/user-registration.store';
import { ContactDetailsComponent } from './contact-details.component';

describe('ContactDetailsComponent', () => {
  let component: ContactDetailsComponent;
  let fixture: ComponentFixture<ContactDetailsComponent>;
  let router: Router;
  let route: ActivatedRoute;
  let store: UserRegistrationStore;
  let page: Page;

  class Page extends BasePage<ContactDetailsComponent> {
    get firstName() {
      return this.query<HTMLInputElement>('input[name="firstName"]');
    }

    get firstNameValue() {
      return this.getInputValue(this.firstName);
    }

    set firstNameValue(value: string) {
      this.setInputValue('input[name="firstName"]', value);
    }

    get lastName() {
      return this.query<HTMLInputElement>('input[name="lastName"]');
    }

    get lastNameValue() {
      return this.getInputValue(this.lastName);
    }

    set lastNameValue(value: string) {
      this.setInputValue('input[name="lastName"]', value);
    }

    get countryCode() {
      return this.query<HTMLSelectElement>('select[name="phoneNumber.countryCode"]');
    }

    get countryCodeValue() {
      return this.getInputValue(this.countryCode);
    }

    set countryCodeValue(value: string) {
      this.setInputValue('select[name="phoneNumber.countryCode"]', value);
    }

    get phoneNumber() {
      return this.query<HTMLInputElement>('input[name="phoneNumber"]');
    }

    get phoneNumberValue() {
      return this.getInputValue(this.phoneNumber);
    }

    set phoneNumberValue(value: string) {
      this.setInputValue('input[name="phoneNumber"]', value);
    }

    get email() {
      return this.query<HTMLInputElement>('input[name="email"]');
    }

    get emailValue() {
      return this.getInputValue(this.email);
    }

    get addressLine1() {
      return this.query<HTMLInputElement>('input[name="address.line1"]');
    }

    get addressLine1Value() {
      return this.getInputValue(this.addressLine1);
    }

    set addressLine1Value(value: string) {
      this.setInputValue('input[name="address.line1"]', value);
    }

    get addressLine2() {
      return this.query<HTMLInputElement>('input[name="address.line2"]');
    }

    get addressLine2Value() {
      return this.getInputValue('input[name="address.line2"]');
    }

    get addressCity() {
      return this.query<HTMLInputElement>('input[name="address.city"]');
    }

    get addressCityValue() {
      return this.getInputValue(this.addressCity);
    }

    set addressCityValue(value: string) {
      this.setInputValue('input[name="address.city"]', value);
    }

    get addressCountry() {
      return this.query<HTMLSelectElement>('select[name="address.country"]');
    }

    get addressCountryValue() {
      return this.getInputValue('select[name="address.country"]');
    }

    set addressCountryValue(value: string) {
      this.setInputValue('select[name="address.country"]', value);
    }

    get addressPostCode() {
      return this.query<HTMLInputElement>('input[name="address.postcode"]');
    }

    get addressPostCodeValue() {
      return this.getInputValue(this.addressPostCode);
    }

    set addressPostCodeValue(value: string) {
      this.setInputValue('input[name="address.postcode"]', value);
    }

    get sendNotices() {
      return this.query<HTMLInputElement>('#sendNotices-0');
    }

    get sendNoticesValue() {
      return this.getInputValue(this.sendNotices);
    }

    set sendNoticesValue(value: boolean) {
      this.setInputValue('input[name="sendNotices"]', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  @Component({ template: '' })
  class NoopComponent {}

  const mockContactDetails: Omit<OperatorUserRegistrationDTO, 'emailToken' | 'password'> = {
    firstName: 'John',
    lastName: 'Doe',
    address: {
      line1: 'Line',
      line2: null,
      city: 'City',
      country: 'Country',
      postcode: 'PostCode',
    },
    phoneNumber: {
      countryCode: '30',
      number: '123',
    },
    mobileNumber: {
      countryCode: null,
      number: null,
    },
  };

  // Making Angular aware of changes in component tests With OnPush Change Detection
  async function runOnPushChangeDetection(fixture: ComponentFixture<any>): Promise<void> {
    const changeDetectorRef = fixture.debugElement.injector.get<ChangeDetectorRef>(ChangeDetectorRef);
    changeDetectorRef.detectChanges();
    return fixture.whenStable();
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ContactDetailsComponent, NoopComponent],
      imports: [SharedModule, RouterTestingModule.withRoutes([{ path: 'choose-password', component: NoopComponent }])],
      providers: [UserRegistrationStore, { provide: CountryService, useClass: CountryServiceStub }],
    })
      .overrideComponent(AddressInputComponent, {
        set: { providers: [{ provide: ControlContainer, useExisting: FormGroupName }] },
      })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ContactDetailsComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    store = TestBed.inject(UserRegistrationStore);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fill form from state', async () => {
    store.setState({ userRegistrationDTO: mockContactDetails, email: 'test@email.com' });
    await runOnPushChangeDetection(fixture);

    expect(page.firstNameValue).toBe(mockContactDetails.firstName);
    expect(page.lastNameValue).toBe(mockContactDetails.lastName);
    expect(page.countryCodeValue).toEqual(mockContactDetails.phoneNumber.countryCode);
    expect(page.phoneNumberValue).toBe(mockContactDetails.phoneNumber.number);
    expect(page.emailValue).toBe('test@email.com');
    expect(page.addressLine1Value).toBe(mockContactDetails.address.line1);
    expect(page.addressLine2Value).toBe(mockContactDetails.address.line2);
    expect(page.addressCityValue).toBe(mockContactDetails.address.city);
    expect(page.addressCountryValue).toBe(mockContactDetails.address.country);
    expect(page.addressPostCodeValue).toBe(mockContactDetails.address.postcode);
    expect(page.sendNoticesValue).toBeTruthy();
    expect(page.email.disabled).toBeTruthy();
    expect(page.addressLine1.disabled).toBeFalsy();
    expect(page.addressLine2.disabled).toBeFalsy();
    expect(page.addressCity.disabled).toBeFalsy();
    expect(page.addressCountry.disabled).toBeFalsy();
    expect(page.addressPostCode.disabled).toBeFalsy();
  });

  it('should not submit on invalid form', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    page.firstNameValue = 'first name';
    page.lastNameValue = 'last name';
    page.countryCodeValue = '30';

    page.submitButton.click();
    await runOnPushChangeDetection(fixture);

    expect(navigateSpy).not.toHaveBeenCalled();
    expect(component.isSummaryDisplayed).toBeTruthy();

    page.phoneNumberValue = '3126459778';
    page.submitButton.click();
    await runOnPushChangeDetection(fixture);

    expect(navigateSpy).toHaveBeenCalledWith(['../choose-password'], { relativeTo: route });
  });

  it('should not submit on invalid form with send notices enabled', async () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    page.firstNameValue = 'first name';
    page.lastNameValue = 'last name';
    page.countryCodeValue = '30';

    const sendNoticesCheckbox = fixture.nativeElement.querySelector('input[name="sendNotices"]');
    sendNoticesCheckbox.click();
    await runOnPushChangeDetection(fixture);

    page.sendNoticesValue = true;
    page.addressLine1Value = 'line 1';
    page.addressCityValue = 'city';
    page.addressPostCodeValue = 'postcode';
    page.addressCountryValue = 'GR';

    page.submitButton.click();

    expect(navigateSpy).not.toHaveBeenCalled();
    expect(component.isSummaryDisplayed).toBeTruthy();

    page.phoneNumberValue = 'office number';
    page.submitButton.click();

    expect(navigateSpy).toHaveBeenCalledWith(['../choose-password'], { relativeTo: route });
  });

  it('should navigate to choose password', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    store.setState({ userRegistrationDTO: mockContactDetails, isSummarized: false });

    const button = fixture.nativeElement.querySelector('button');
    button.click();

    expect(store.getState().userRegistrationDTO).toEqual(mockContactDetails);
    expect(navigateSpy).toHaveBeenCalledWith(['../choose-password'], { relativeTo: TestBed.inject(ActivatedRoute) });
  });

  it('should navigate to summary', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    store.setState({ userRegistrationDTO: mockContactDetails, isSummarized: true });

    const button = fixture.nativeElement.querySelector('button');
    button.click();

    expect(store.getState().userRegistrationDTO).toEqual(mockContactDetails);
    expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: TestBed.inject(ActivatedRoute) });
  });

  it('should navigate to summary for emitter users', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
    const token = 'thisisatoken';

    store.setState({
      ...store.getState(),
      userRegistrationDTO: mockContactDetails,
      invitationStatus: 'PENDING_USER_REGISTRATION_NO_PASSWORD',
      token,
    });

    const button = fixture.nativeElement.querySelector('button');
    button.click();

    expect(navigateSpy).toHaveBeenCalledWith(['../summary'], {
      relativeTo: TestBed.inject(ActivatedRoute),
    });
  });
});

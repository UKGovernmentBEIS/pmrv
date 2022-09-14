import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlContainer, FormGroup, FormGroupName } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AccountsService } from 'pmrv-api';

import { BasePage, CountryServiceStub } from '../../../testing';
import { AccountsServiceStub } from '../../../testing/accounts.service.stub';
import { CountryService } from '../../core/services/country.service';
import { AddressInputComponent } from '../../shared/address-input/address-input.component';
import { SharedModule } from '../../shared/shared.module';
import { INSTALLATION_FORM, installationFormFactory } from '../factories/installation-form.factory';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';
import { mockOnshoreInstallation } from '../testing/mock-state';
import { OnshoreDetailsComponent } from './onshore-details.component';

describe('OnshoreDetailsComponent', () => {
  let component: OnshoreDetailsComponent;
  let fixture: ComponentFixture<OnshoreDetailsComponent>;
  let page: Page;
  let router: Router;
  let form: FormGroup;
  let activatedRoute: ActivatedRoute;
  let navigateSpy: jest.SpyInstance<Promise<boolean>>;
  let store: InstallationAccountApplicationStore;

  class Page extends BasePage<OnshoreDetailsComponent> {
    get nameValue() {
      return this.getInputValue('[name="name"]');
    }

    set nameValue(value: string) {
      this.setInputValue('[name="name"]', value);
    }

    get siteNameValue() {
      return this.getInputValue('[name="siteName"]');
    }

    set siteNameValue(value: string) {
      this.setInputValue('[name="siteName"]', value);
    }

    get gridReferenceValue() {
      return this.getInputValue('[name="gridReference"]');
    }

    set gridReferenceValue(value: string) {
      this.setInputValue('[name="gridReference"]', value);
    }

    get addressLine1Value() {
      return this.getInputValue('[name="address.line1"]');
    }

    set addressLine1Value(value: string) {
      this.setInputValue('[name="address.line1"]', value);
    }

    get addressCountryValue() {
      return this.getInputValue('[name="address.country"]');
    }

    set addressCountryValue(value: string) {
      this.setInputValue('[name="address.country"]', value);
    }

    get addressPostCodeValue() {
      return this.getInputValue('[name="address.postcode"]');
    }

    set addressPostCodeValue(value: string) {
      this.setInputValue('[name="address.postcode"]', value);
    }

    get addressCityValue() {
      return this.getInputValue('[name="address.city"]');
    }

    set addressCityValue(value: string) {
      this.setInputValue('[name="address.city"]', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OnshoreDetailsComponent],
      imports: [SharedModule, RouterTestingModule],
      providers: [
        InstallationAccountApplicationStore,
        installationFormFactory,
        { provide: AccountsService, useClass: AccountsServiceStub },
        { provide: CountryService, useClass: CountryServiceStub },
      ],
    })
      .overrideComponent(AddressInputComponent, {
        set: { providers: [{ provide: ControlContainer, useExisting: FormGroupName }] },
      })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OnshoreDetailsComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    page = new Page(fixture);
    navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);
    activatedRoute = TestBed.inject(ActivatedRoute);
    form = TestBed.inject(INSTALLATION_FORM);
    store = TestBed.inject(InstallationAccountApplicationStore);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not navigate until form is valid', () => {
    form.get('installationTypeGroup').get('type').setValue('ONSHORE');

    page.nameValue = 'input';
    page.siteNameValue = 'input';
    page.gridReferenceValue = 'ab 123 456 23';
    page.addressLine1Value = 'input';

    page.submitButton.click();
    fixture.detectChanges();
    expect(navigateSpy).not.toHaveBeenCalled();

    page.addressCountryValue = 'GR';
    page.addressPostCodeValue = 'input';
    page.addressCityValue = 'input';
    page.gridReferenceValue = 'NN166712***';

    page.submitButton.click();
    expect(navigateSpy).not.toHaveBeenCalled();

    page.gridReferenceValue = 'ab 123 456';

    page.submitButton.click();
    expect(navigateSpy).toHaveBeenCalled();
  });

  it('should fill form from store and navigate', () => {
    form.patchValue(mockOnshoreInstallation);

    expect(page.nameValue).toBe(mockOnshoreInstallation.onshoreGroup.name);
    expect(page.siteNameValue).toBe(mockOnshoreInstallation.onshoreGroup.siteName);
    expect(page.gridReferenceValue).toBe(mockOnshoreInstallation.onshoreGroup.gridReference);
    expect(page.addressLine1Value).toBe(mockOnshoreInstallation.onshoreGroup.address.line1);
    expect(page.addressCityValue).toBe(mockOnshoreInstallation.onshoreGroup.address.city);
    expect(page.addressCountryValue).toContain(mockOnshoreInstallation.onshoreGroup.address.country);
    expect(page.addressPostCodeValue).toBe(mockOnshoreInstallation.onshoreGroup.address.postcode);

    page.submitButton.click();

    expect(navigateSpy).toHaveBeenCalledWith(['../emissions'], { relativeTo: activatedRoute });
  });

  it('should navigate to the summary on submit if coming from operator summary', () => {
    store.setState({ ...store.getState(), isSummarized: true });

    form.patchValue(mockOnshoreInstallation);

    page.submitButton.click();
    expect(navigateSpy).toHaveBeenCalledWith(['/installation-account/application/summary']);
  });

  it('should not amend if not reviewed', () => {
    const nextStepSpy = jest.spyOn(store, 'nextStep').mockImplementation();
    const amendSpy = jest.spyOn(store, 'amend').mockImplementation();

    form.patchValue(mockOnshoreInstallation);
    page.submitButton.click();

    expect(store.getState().isReviewed).not.toBeTruthy();
    expect(nextStepSpy).toHaveBeenCalled();
    expect(amendSpy).not.toHaveBeenCalled();
  });

  it('should amend if reviewed', () => {
    const nextStepSpy = jest.spyOn(store, 'nextStep').mockImplementation();
    const amendSpy = jest.spyOn(store, 'amend').mockImplementation().mockReturnValue(of(null));
    store.setState({ ...store.getState(), isReviewed: true });

    form.patchValue(mockOnshoreInstallation);
    page.submitButton.click();

    expect(store.getState().isReviewed).toBeTruthy();
    expect(nextStepSpy).toHaveBeenCalled();
    expect(amendSpy).toHaveBeenCalled();
  });
});

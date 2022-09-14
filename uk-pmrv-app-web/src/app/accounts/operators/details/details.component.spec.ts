import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ControlContainer, FormGroupName } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, firstValueFrom, of, throwError } from 'rxjs';

import { OperatorUsersService, UserStatusDTO } from 'pmrv-api';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { AuthService } from '../../../core/services/auth.service';
import { ConcurrencyTestingModule, expectConcurrentErrorToBe } from '../../../error/testing/concurrency-error';
import { AddressInputComponent } from '../../../shared/address-input/address-input.component';
import { SharedModule } from '../../../shared/shared.module';
import { TwoFaLinkComponent } from '../../../shared-user/two-fa-link/two-fa-link.component';
import { operator, operatorUserRole } from '../../testing/mock-data';
import { saveNotFoundOperatorError } from '../errors/concurrency-error';
import { DetailsComponent } from './details.component';

describe('DetailsComponent', () => {
  let component: DetailsComponent;
  let fixture: ComponentFixture<DetailsComponent>;
  let page: Page;
  let operatorUsersService: Partial<jest.Mocked<OperatorUsersService>>;
  let authService: Partial<jest.Mocked<AuthService>>;
  let router: Router;
  let activatedRoute: ActivatedRouteStub;

  class Page extends BasePage<DetailsComponent> {
    get firstNameValue() {
      return this.getInputValue('#firstName');
    }

    set firstNameValue(value: string) {
      this.setInputValue('#firstName', value);
    }

    get lastNameValue() {
      return this.getInputValue('#lastName');
    }

    get phoneNumberValue() {
      return this.getInputValue('#phoneNumber');
    }

    get mobileNumberValue() {
      return this.getInputValue('#mobileNumber');
    }

    get emailValue() {
      return this.getInputValue('#email');
    }

    get addressLine1Value() {
      return this.getInputValue(this.addressLine1);
    }

    get addressLine2Value() {
      return this.getInputValue(this.addressLine2);
    }

    get addressCityValue() {
      return this.getInputValue(this.addressCity);
    }

    get addressCountryValue() {
      return this.getInputValue('#address.country');
    }

    get addressPostCodeValue() {
      return this.getInputValue(this.addressPostCode);
    }

    get sendNoticesValue() {
      return this.getInputValue('#sendNotices-0');
    }

    set sendNoticesValue(value: boolean) {
      this.setInputValue('#sendNotices-0', value);
    }

    get addressLine1() {
      return this.query<HTMLInputElement>('#address\\.line1');
    }

    get addressLine2() {
      return this.query<HTMLInputElement>('#address\\.line2');
    }

    get addressCity() {
      return this.query<HTMLInputElement>('#address\\.city');
    }

    get addressCountry() {
      return this.query<HTMLSelectElement>('#address\\.country');
    }

    get addressPostCode() {
      return this.query<HTMLInputElement>('#address\\.postcode');
    }

    get sendNotices() {
      return this.query<HTMLInputElement>('#sendNotices-0');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get saveButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get links() {
      return this.queryAll<HTMLLinkElement>('a');
    }
  }

  beforeEach(async () => {
    operatorUsersService = {
      updateCurrentOperatorUserUsingPATCH: jest.fn().mockReturnValue(of(operator)),
      updateOperatorUserByIdUsingPATCH: jest.fn().mockReturnValue(of(operator)),
    };
    authService = {
      userStatus: new BehaviorSubject<UserStatusDTO>(operatorUserRole),
    };
    activatedRoute = new ActivatedRouteStub({ accountId: '1', userId: operatorUserRole.userId }, null, {
      user: operator,
    });
    await TestBed.configureTestingModule({
      imports: [ConcurrencyTestingModule, RouterTestingModule, SharedModule],
      declarations: [DetailsComponent, TwoFaLinkComponent],
      providers: [
        { provide: OperatorUsersService, useValue: operatorUsersService },
        { provide: AuthService, useValue: authService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    })
      .overrideComponent(AddressInputComponent, {
        set: { providers: [{ provide: ControlContainer, useExisting: FormGroupName }] },
      })
      .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = fixture.debugElement.injector.get(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the pre-populated form', () => {
    expect(page.firstNameValue).toEqual(operator.firstName);
    expect(page.lastNameValue).toEqual(operator.lastName);
    expect(page.phoneNumberValue).toEqual(operator.phoneNumber.number);
    expect(page.mobileNumberValue).toEqual(operator.mobileNumber.number);
    expect(page.emailValue).toEqual(operator.email);
    expect(page.addressLine1Value).toEqual(operator.address.line1);
    expect(page.addressLine2Value).toEqual(operator.address.line2);
    expect(page.addressCityValue).toEqual(operator.address.city);
    expect(page.addressCountryValue).toEqual(operator.address.country);
    expect(page.addressPostCodeValue).toEqual(operator.address.postcode);
    expect(page.sendNoticesValue).toBeTruthy();
    expect(page.addressLine1.disabled).toBeFalsy();
    expect(page.addressLine2.disabled).toBeFalsy();
    expect(page.addressCity.disabled).toBeFalsy();
    expect(page.addressCountry.disabled).toBeFalsy();
    expect(page.addressPostCode.disabled).toBeFalsy();
  });

  it('should disable address fields when notices are not sent', () => {
    page.sendNoticesValue = false;
    fixture.detectChanges();

    expect(page.addressLine1.disabled).toBeTruthy();
    expect(page.addressLine2.disabled).toBeTruthy();
    expect(page.addressCity.disabled).toBeTruthy();
    expect(page.addressCountry.disabled).toBeTruthy();
    expect(page.addressPostCode.disabled).toBeTruthy();

    page.saveButton.click();

    expect(operatorUsersService.updateCurrentOperatorUserUsingPATCH).toHaveBeenCalledWith({
      ...operator,
      address: null,
    });
  });

  it('should save the current user', () => {
    authService.userStatus.next({ ...operatorUserRole, userId: 'asdf4' });
    page.firstNameValue = 'Mary';
    fixture.detectChanges();

    page.saveButton.click();
    fixture.detectChanges();

    expect(operatorUsersService.updateCurrentOperatorUserUsingPATCH).toHaveBeenCalledWith(operator);
    expect(operatorUsersService.updateOperatorUserByIdUsingPATCH).not.toHaveBeenCalled();
  });

  it('should save other user than the current', () => {
    authService.userStatus.next({ ...operatorUserRole, userId: 'abc1' });
    page.firstNameValue = 'Mary';
    fixture.detectChanges();

    page.saveButton.click();

    expect(operatorUsersService.updateOperatorUserByIdUsingPATCH).toHaveBeenCalledWith(
      1,
      operatorUserRole.userId,
      operator,
    );
    expect(operatorUsersService.updateCurrentOperatorUserUsingPATCH).not.toHaveBeenCalled();
  });

  it('should throw an error when updating a deleted user', async () => {
    operatorUsersService.updateOperatorUserByIdUsingPATCH.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'AUTHORITY1004' } })),
    );

    authService.userStatus.next({ ...operatorUserRole, userId: 'abc1' });
    page.firstNameValue = 'Mary';
    fixture.detectChanges();

    page.saveButton.click();
    fixture.detectChanges();

    await expectConcurrentErrorToBe(saveNotFoundOperatorError(1));
  });

  it('should display errors', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    expect(page.errorSummary).toBeFalsy();

    authService.userStatus.next({ ...operatorUserRole, userId: 'abc1' });
    page.firstNameValue = '';
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();

    page.saveButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(operatorUsersService.updateOperatorUserByIdUsingPATCH).not.toHaveBeenCalled();
    expect(navigateSpy).not.toHaveBeenCalled();
  });

  it('should display the 2fa link', () => {
    expect(page.links.map((el) => el.textContent)).toContain('Change two factor authentication');
  });

  it('should not display the 2fa link', async () => {
    authService.userStatus.next({ ...operatorUserRole, userId: 'asdf4' });
    activatedRoute.setParamMap({ userId: '222' });

    await expect(firstValueFrom(component.isLoggedUser$)).resolves.toEqual(false);
  });
});

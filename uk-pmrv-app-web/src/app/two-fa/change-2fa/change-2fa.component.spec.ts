import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import { UsersSecuritySetupService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../testing';
import { SharedModule } from '../../shared/shared.module';
import { Change2faComponent } from './change-2fa.component';

describe('Change2faComponent', () => {
  let component: Change2faComponent;
  let fixture: ComponentFixture<Change2faComponent>;
  let page: Page;
  let router: Router;
  const usersSecuritySetupService = mockClass(UsersSecuritySetupService);

  class Page extends BasePage<Change2faComponent> {
    get passwordValue() {
      return this.getInputValue('#password');
    }
    set passwordValue(value: string) {
      this.setInputValue('#password', value);
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }
    get confirmationPanel() {
      return this.query<HTMLDivElement>('.govuk-panel');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [{ provide: UsersSecuritySetupService, useValue: usersSecuritySetupService }],
      declarations: [Change2faComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(Change2faComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display errors', () => {
    expect(page.submitButton.disabled).toBeFalsy();

    page.submitButton.click();
    fixture.detectChanges();
    expect(page.errorSummaryList).toEqual(['Enter the 6-digit code']);

    page.passwordValue = '123abc';
    page.submitButton.click();
    fixture.detectChanges();
    expect(page.errorSummaryList).toEqual(['Digit code must contain numbers only']);

    page.passwordValue = '123';
    page.submitButton.click();
    fixture.detectChanges();
    expect(page.errorSummaryList).toEqual(['Digit code must contain exactly 6 characters']);

    page.passwordValue = '1234567';
    page.submitButton.click();
    fixture.detectChanges();
    expect(page.errorSummaryList).toEqual(['Digit code must contain exactly 6 characters']);
  });

  it('should submit the request change', () => {
    usersSecuritySetupService.requestTwoFactorAuthChangeUsingPOST.mockReturnValueOnce(of({}));

    page.passwordValue = '123456';
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();

    expect(usersSecuritySetupService.requestTwoFactorAuthChangeUsingPOST).toHaveBeenCalledTimes(1);
    expect(usersSecuritySetupService.requestTwoFactorAuthChangeUsingPOST).toHaveBeenCalledWith({ password: '123456' });
    expect(page.confirmationPanel).toBeTruthy();
  });

  it('on returning error should navigate to invalid code error page', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    usersSecuritySetupService.requestTwoFactorAuthChangeUsingPOST.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'OTP1001' } })),
    );

    page.passwordValue = '123456';
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();

    expect(usersSecuritySetupService.requestTwoFactorAuthChangeUsingPOST).toHaveBeenCalledTimes(1);
    expect(usersSecuritySetupService.requestTwoFactorAuthChangeUsingPOST).toHaveBeenCalledWith({ password: '123456' });
    expect(page.confirmationPanel).toBeFalsy();
    expect(navigateSpy).toHaveBeenCalledWith(['2fa', 'invalid-code']);
  });
});

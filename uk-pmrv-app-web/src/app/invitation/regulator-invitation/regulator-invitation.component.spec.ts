import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import { GovukComponentsModule } from 'govuk-components';

import { RegulatorUsersRegistrationService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../testing';
import { SharedModule } from '../../shared/shared.module';
import { PasswordService } from '../../shared-user/password/password.service';
import { SharedUserModule } from '../../shared-user/shared-user.module';
import { RegulatorInvitationComponent } from './regulator-invitation.component';

describe('RegulatorInvitationComponent', () => {
  let component: RegulatorInvitationComponent;
  let fixture: ComponentFixture<RegulatorInvitationComponent>;
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let regulatorUsersRegistrationService: jest.Mocked<RegulatorUsersRegistrationService>;
  let passwordService: Partial<jest.Mocked<PasswordService>>;

  class Page extends BasePage<RegulatorInvitationComponent> {
    get emailValue() {
      return this.getInputValue<string>('#email');
    }

    set passwordValue(value: string) {
      this.setInputValue('#password', value);
    }

    set repeatedPasswordValue(value: string) {
      this.setInputValue('#validatePassword', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  beforeEach(async () => {
    regulatorUsersRegistrationService = mockClass(RegulatorUsersRegistrationService);
    passwordService = mockClass(PasswordService);
    const activatedRoute = new ActivatedRouteStub(
      undefined,
      { token: 'token' },
      {
        invitedUser: { email: 'user@pmrv.uk' },
      },
    );

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, GovukComponentsModule, SharedModule, ReactiveFormsModule, SharedUserModule],
      declarations: [RegulatorInvitationComponent],
      providers: [
        { provide: RegulatorUsersRegistrationService, useValue: regulatorUsersRegistrationService },
        { provide: PasswordService, useValue: passwordService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegulatorInvitationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should populate the form with email information', () => {
    expect(component.form.get('email').value).toEqual('user@pmrv.uk');
    expect(page.emailValue).toEqual('user@pmrv.uk');
  });

  it('should navigate for link related error', () => {
    regulatorUsersRegistrationService.enableRegulatorInvitedUserUsingPUT.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'EMAIL1001' }, status: 400 })),
    );
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
    passwordService.blacklisted.mockReturnValue(of(null));
    passwordService.strong.mockReturnValue(null);

    component.form.get('password').setValue('ThisIsAStrongP@ssw0rd');
    component.form.get('validatePassword').setValue('ThisIsAStrongP@ssw0rd');
    page.submitButton.click();
    fixture.detectChanges();

    // expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['invalid-link'], {
      relativeTo: route,
      queryParams: { code: 'EMAIL1001' },
    });

    regulatorUsersRegistrationService.enableRegulatorInvitedUserUsingPUT.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'TOKEN1001' }, status: 400 })),
    );

    component.form.get('password').setValue('ThisIsAStrongP@ssw0rd');
    page.submitButton.click();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledTimes(2);
    expect(navigateSpy).toHaveBeenCalledWith(['invalid-link'], {
      relativeTo: route,
      queryParams: { code: 'TOKEN1001' },
    });
  });

  it('should submit only if form valid', () => {
    page.passwordValue = '';
    page.repeatedPasswordValue = '';
    page.submitButton.click();
    fixture.detectChanges();

    expect(regulatorUsersRegistrationService.enableRegulatorInvitedUserUsingPUT).not.toHaveBeenCalled();

    page.passwordValue = 'test';
    page.submitButton.click();
    fixture.detectChanges();

    expect(regulatorUsersRegistrationService.enableRegulatorInvitedUserUsingPUT).not.toHaveBeenCalled();

    passwordService.blacklisted.mockReturnValue(of(null));
    passwordService.strong.mockReturnValue(null);
    page.passwordValue = 'ThisIsAStrongP@ssw0rd';
    page.repeatedPasswordValue = 'ThisIsAStrongP@ssw0rd';
    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();
    expect(regulatorUsersRegistrationService.enableRegulatorInvitedUserUsingPUT).toHaveBeenCalledTimes(1);
    expect(regulatorUsersRegistrationService.enableRegulatorInvitedUserUsingPUT).toHaveBeenCalledWith({
      invitationToken: 'token',
      password: 'ThisIsAStrongP@ssw0rd',
    });
  });
});

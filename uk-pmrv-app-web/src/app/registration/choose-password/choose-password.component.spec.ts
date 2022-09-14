import { ComponentFixture, fakeAsync, inject, TestBed, tick } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { GovukComponentsModule } from 'govuk-components';

import { OperatorUsersRegistrationService } from 'pmrv-api';

import { BasePage, mockClass, MockType } from '../../../testing';
import { SharedModule } from '../../shared/shared.module';
import { PasswordService } from '../../shared-user/password/password.service';
import { SharedUserModule } from '../../shared-user/shared-user.module';
import { UserRegistrationStore } from '../store/user-registration.store';
import { ChoosePasswordComponent } from './choose-password.component';

describe('ChoosePasswordComponent', () => {
  let component: ChoosePasswordComponent;
  let fixture: ComponentFixture<ChoosePasswordComponent>;
  let page: Page;
  let passwordService: jest.Mocked<PasswordService>;

  class Page extends BasePage<ChoosePasswordComponent> {
    get emailValue() {
      return this.getInputValue('#email');
    }

    get passwordValue() {
      return this.getInputValue('#password');
    }

    set passwordValue(password: string) {
      this.setInputValue('#password', password);
    }

    get repeatedPasswordValue() {
      return this.query<HTMLInputElement>('#validatePassword').value;
    }

    set repeatedPasswordValue(password: string) {
      this.setInputValue('#validatePassword', password);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  const operatorUsersRegistrationService: MockType<OperatorUsersRegistrationService> = {
    enableOperatorInvitedUserUsingPUT: jest.fn().mockReturnValue(of(null)),
    acceptOperatorInvitationUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  beforeEach(async () => {
    passwordService = mockClass(PasswordService);

    await TestBed.configureTestingModule({
      imports: [GovukComponentsModule, SharedModule, RouterTestingModule, ReactiveFormsModule, SharedUserModule],
      declarations: [ChoosePasswordComponent],
      providers: [
        UserRegistrationStore,
        { provide: OperatorUsersRegistrationService, useValue: operatorUsersRegistrationService },
        { provide: PasswordService, useValue: passwordService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChoosePasswordComponent);
    component = fixture.debugElement.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should fill form from store', inject(
    [UserRegistrationStore],
    fakeAsync((store: UserRegistrationStore) => {
      passwordService.blacklisted.mockReturnValue(of(null));
      store.setState({ password: 'password', email: 'test@pmrv.uk' });

      tick();
      fixture.detectChanges();

      expect(page.emailValue).toBe('test@pmrv.uk');
      expect(page.passwordValue).toBe('password');
      expect(page.repeatedPasswordValue).toBe('password');
    }),
  ));

  it('should submit only if form valid', inject([Router], (router: Router) => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
    passwordService.blacklisted.mockReturnValue(of(null));

    page.passwordValue = '';
    page.repeatedPasswordValue = '';
    page.submitButton.click();
    fixture.detectChanges();

    page.passwordValue = 'test';
    page.submitButton.click();
    fixture.detectChanges();
    expect(navigateSpy).not.toHaveBeenCalled();

    page.passwordValue = 'ThisIsAStrongP@ssw0rd';
    page.repeatedPasswordValue = 'ThisIsAStrongP@ssw0rd';

    page.submitButton.click();
    fixture.detectChanges();
    expect(navigateSpy).toHaveBeenCalled();
  }));

  it('should navigate to summary when creating an operator from an emitter', inject(
    [Router, UserRegistrationStore],
    (router: Router, store: UserRegistrationStore) => {
      const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
      const token = 'thisisatoken';
      const password = 'ThisIsAStrongP@ssw0rd';

      passwordService.blacklisted.mockReturnValue(of(null));

      store.setState({
        invitationStatus: 'PENDING_USER_ENABLE',
        token: token,
        password: password,
      });

      page.passwordValue = password;
      page.repeatedPasswordValue = password;

      page.submitButton.click();
      fixture.detectChanges();

      expect(operatorUsersRegistrationService.enableOperatorInvitedUserUsingPUT).toHaveBeenCalledWith({
        invitationToken: token,
        password: password,
      });
      expect(operatorUsersRegistrationService.acceptOperatorInvitationUsingPOST).toHaveBeenCalledWith({
        token,
      });

      expect(navigateSpy).toHaveBeenCalledWith(['../success'], {
        relativeTo: TestBed.inject(ActivatedRoute),
      });
    },
  ));
});

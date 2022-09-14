import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { throwError } from 'rxjs';

import { AdminVerifierUserInvitationDTO, VerifierUsersInvitationService } from 'pmrv-api';

import { ActivatedRouteStub, asyncData, BasePage } from '../../../../testing';
import { ErrorCode } from '../../../error/business-errors';
import { ConcurrencyTestingModule, expectConcurrentErrorToBe } from '../../../error/testing/concurrency-error';
import { SharedModule } from '../../../shared/shared.module';
import { disabledVerificationBodyError } from '../../errors/concurrency-error';
import { AddComponent } from './add.component';

describe('AddComponent', () => {
  let component: AddComponent;
  let fixture: ComponentFixture<AddComponent>;
  let page: Page;
  let verifierUsersInvitationService: Partial<jest.Mocked<VerifierUsersInvitationService>>;
  const activatedRoute = new ActivatedRouteStub({ verificationBodyId: '1' });

  class Page extends BasePage<AddComponent> {
    get firstNameValue() {
      return this.getInputValue<string>('#firstName');
    }

    set firstNameValue(value: string) {
      this.setInputValue('#firstName', value);
    }

    get lastNameValue() {
      return this.getInputValue<string>('#lastName');
    }

    set lastNameValue(value: string) {
      this.setInputValue('#lastName', value);
    }

    get phoneValue() {
      return this.getInputValue<string>('#phoneNumber');
    }

    set phoneValue(value: string) {
      this.setInputValue('#phoneNumber', value);
    }

    get mobileValue() {
      return this.getInputValue<string>('#mobileNumber');
    }

    set mobileValue(value: string) {
      this.setInputValue('#mobileNumber', value);
    }

    get emailValue() {
      return this.getInputValue<string>('#email');
    }

    set emailValue(value: string) {
      this.setInputValue('#email', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLUListElement>('.govuk-error-summary__list');
    }

    get confirmationPanel() {
      return this.query<HTMLDivElement>('.govuk-panel');
    }
  }

  const user: AdminVerifierUserInvitationDTO = {
    firstName: 'John',
    lastName: 'Wall',
    phoneNumber: '6981031041056',
    mobileNumber: '6981031041057',
    email: 'john.wall@pmrv.uk',
  };
  const fillForm = () => {
    page.firstNameValue = user.firstName;
    page.lastNameValue = user.lastName;
    page.phoneValue = user.phoneNumber;
    page.mobileValue = user.mobileNumber;
    page.emailValue = user.email;
    fixture.detectChanges();
  };

  beforeEach(async () => {
    verifierUsersInvitationService = {
      inviteVerifierAdminUserByVerificationBodyIdUsingPOST: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [ConcurrencyTestingModule, SharedModule, RouterTestingModule],
      declarations: [AddComponent],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: VerifierUsersInvitationService, useValue: verifierUsersInvitationService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AddComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit add a new contact', () => {
    verifierUsersInvitationService.inviteVerifierAdminUserByVerificationBodyIdUsingPOST.mockReturnValue(
      asyncData(null),
    );

    fillForm();

    page.submitButton.click();
    fixture.detectChanges();

    expect(verifierUsersInvitationService.inviteVerifierAdminUserByVerificationBodyIdUsingPOST).toHaveBeenCalledWith(
      1,
      user,
    );

    expect(page.confirmationPanel).toBeTruthy();
    expect(page.submitButton).toBeFalsy();
  });

  it('should require all fields except mobile to be set', () => {
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary.querySelectorAll('li')).toHaveLength(4);
  });

  it('should require that the email is unique', () => {
    verifierUsersInvitationService.inviteVerifierAdminUserByVerificationBodyIdUsingPOST.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: ErrorCode.USER1001 } })),
    );

    fillForm();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary.querySelector('li').textContent.trim()).toEqual('This user email already exists in PMRV');
    expect(component.form.valid).toBeFalsy();
  });

  it('should display error page if the body is no longer active', async () => {
    verifierUsersInvitationService.inviteVerifierAdminUserByVerificationBodyIdUsingPOST.mockReturnValue(
      throwError(() => new HttpErrorResponse({ status: 404 })),
    );

    fillForm();

    page.submitButton.click();
    fixture.detectChanges();

    await expectConcurrentErrorToBe(disabledVerificationBodyError);
  });
});

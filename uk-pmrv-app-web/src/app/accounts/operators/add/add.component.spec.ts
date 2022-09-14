import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import { OperatorUsersInvitationService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { AddComponent } from './add.component';

describe('AddComponent', () => {
  const route = new ActivatedRouteStub(null, { accountId: 8 });
  let component: AddComponent;
  let fixture: ComponentFixture<AddComponent>;
  let operatorUsersInvitationService: OperatorUsersInvitationService;
  let page: Page;

  class Page extends BasePage<AddComponent> {
    get firstValue() {
      return this.getInputValue('input[name="firstName"]');
    }

    set firstValue(value: string) {
      this.setInputValue('input[name="firstName"]', value);
    }

    set lastValue(value: string) {
      this.setInputValue('input[name="lastName"]', value);
    }

    set emailValue(value: string) {
      this.setInputValue('input[name="email"]', value);
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get confirmationTitle() {
      return this.query<HTMLHeadingElement>('h1.govuk-panel__title');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AddComponent],
      imports: [SharedModule, RouterTestingModule],
      providers: [OperatorUsersInvitationService, { provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
    operatorUsersInvitationService = TestBed.inject(OperatorUsersInvitationService);
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

  it('should submit form when all fields are valid', () => {
    const element: HTMLElement = fixture.nativeElement;

    fixture.detectChanges();

    jest.spyOn(operatorUsersInvitationService, 'inviteOperatorUserToAccountUsingPOST').mockReturnValue(of(null));

    page.firstValue = 'John';
    page.lastValue = 'Doe';
    page.emailValue = 'john';

    page.submitButton.click();
    fixture.detectChanges();
    expect(operatorUsersInvitationService.inviteOperatorUserToAccountUsingPOST).not.toHaveBeenCalled();

    page.emailValue = 'john@doe.com';
    page.submitButton.click();
    fixture.detectChanges();
    expect(operatorUsersInvitationService.inviteOperatorUserToAccountUsingPOST).toHaveBeenCalledTimes(1);

    const title = element.querySelector<HTMLHeadingElement>('h1.govuk-panel__title');

    expect(title.textContent).toContain('john@doe.com');
  });

  it('should not navigate to success page when service returns error', () => {
    jest
      .spyOn(operatorUsersInvitationService, 'inviteOperatorUserToAccountUsingPOST')
      .mockReturnValue(throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'AUTHORITY1005' } })));

    page.firstValue = 'John';
    page.lastValue = 'Doe';
    page.emailValue = 'john@doe.com';

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.confirmationTitle).toBeFalsy();
    expect(page.errorSummary).toBeTruthy();

    jest
      .spyOn(operatorUsersInvitationService, 'inviteOperatorUserToAccountUsingPOST')
      .mockReturnValue(throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'AUTHORITY1000' } })));
    page.emailValue = 'john@doe3.com';
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.confirmationTitle).toBeFalsy();
    expect(page.errorSummary).toBeTruthy();

    jest
      .spyOn(operatorUsersInvitationService, 'inviteOperatorUserToAccountUsingPOST')
      .mockReturnValue(throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'USER1001' } })));
    page.emailValue = 'john@doe5.com';
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.confirmationTitle).toBeFalsy();
    expect(page.errorSummary).toBeTruthy();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, of } from 'rxjs';

import { AccountsService, UserStatusDTO } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../testing';
import { AuthService } from '../core/services/auth.service';
import { SharedModule } from '../shared/shared.module';
import { SharedUserModule } from '../shared-user/shared-user.module';
import { AccountsComponent } from './accounts.component';
import { mockAccountResults, operatorUserRole, regulatorUserRole } from './testing/mock-data';

describe('AccountsComponent', () => {
  let component: AccountsComponent;
  let fixture: ComponentFixture<AccountsComponent>;
  let page: Page;
  let userStatus$: BehaviorSubject<UserStatusDTO>;
  let authService: Partial<jest.Mocked<AuthService>>;
  const accountsService = mockClass(AccountsService);

  class Page extends BasePage<AccountsComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading h1.govuk-heading-xl');
    }

    get termValue() {
      return this.getInputValue('#term');
    }
    set termValue(value: string) {
      this.setInputValue('#term', value);
    }
    get termErrorMessage() {
      return this.query<HTMLElement>('div[formcontrolname="term"] span.govuk-error-message');
    }

    get accountTypesFieldSet() {
      return this.query<HTMLFieldSetElement>('fieldset#accountTypes');
    }

    get aviationCheckbox() {
      return this.query<HTMLInputElement>('input#accountTypes-0');
    }
    get installationCheckbox() {
      return this.query<HTMLInputElement>('input#accountTypes-1');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get accounts() {
      return this.queryAll<HTMLLIElement>('form#search-form ul.govuk-list > li');
    }

    get accountNames() {
      return this.queryAll<HTMLLIElement>('form#search-form ul.govuk-list > li a');
    }

    get accountStatuses() {
      return this.queryAll<HTMLSpanElement>('form#search-form ul.govuk-list > li span.search-results-list_item_status');
    }
  }

  const createComponent = async () => {
    fixture = TestBed.createComponent(AccountsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  const createModule = async () => {
    await TestBed.configureTestingModule({
      declarations: [AccountsComponent],
      imports: [RouterTestingModule, SharedModule, SharedUserModule],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: AccountsService, useValue: accountsService },
      ],
    }).compileComponents();
  };

  describe('for operators', () => {
    beforeEach(async () => {
      userStatus$ = new BehaviorSubject<UserStatusDTO>(operatorUserRole);
      authService = {
        userStatus: userStatus$,
      };
      accountsService.getCurrentUserAccountsUsingGET.mockReturnValueOnce(of(mockAccountResults));
    });

    beforeEach(createModule);
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the heading', async () => {
      expect(page.heading.textContent.trim()).toEqual('Account search');
    });

    it('should not render the account types fieldset', async () => {
      expect(page.accountTypesFieldSet).toBeNull();
    });

    it('should show results upon loading the page', () => {
      fixture.detectChanges();
      expect(page.accountNames.map((accountName) => accountName.textContent.trim())).toEqual([
        'account1',
        'account2',
        'account3',
      ]);
    });

    it('should show error when term less than 3 characters and pressing search button', () => {
      fixture.detectChanges();

      page.termValue = 'te';
      page.submitButton.click();
      fixture.detectChanges();
      expect(page.termErrorMessage.textContent.trim()).toContain('Enter at least 3 characters');
    });

    it('should show accounts when term filled and pressing search button', () => {
      fixture.detectChanges();

      page.termValue = 'term';
      page.submitButton.click();

      fixture.detectChanges();

      expect(page.termErrorMessage).toBeNull();
      expect(accountsService.getCurrentUserAccountsUsingGET).toHaveBeenCalledTimes(1);
      expect(accountsService.getCurrentUserAccountsUsingGET).toHaveBeenLastCalledWith(0, 30, 'term', undefined);
      expect(page.accountNames.map((accountName) => accountName.textContent.trim())).toEqual([
        'account1',
        'account2',
        'account3',
      ]);
      expect(page.accountStatuses.map((accountStatus) => accountStatus.textContent.trim())).toEqual([
        'Live',
        'Live',
        'Live',
      ]);
    });
  });

  describe('for regulators', () => {
    beforeEach(async () => {
      userStatus$ = new BehaviorSubject<UserStatusDTO>(regulatorUserRole);
      authService = {
        userStatus: userStatus$,
      };
      accountsService.getCurrentUserAccountsUsingGET.mockReturnValueOnce(of(mockAccountResults));
    });
    beforeEach(createModule);
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the heading', async () => {
      expect(page.heading.textContent.trim()).toEqual('Account search');
    });

    it('should render the account types fieldset', async () => {
      expect(page.accountTypesFieldSet).toBeDefined();
    });

    it('should show accounts when term filled and pressing search button', () => {
      fixture.detectChanges();

      page.termValue = 'term';
      page.submitButton.click();

      fixture.detectChanges();

      page.installationCheckbox.click();
      fixture.detectChanges();

      expect(page.termErrorMessage).toBeNull();
      expect(accountsService.getCurrentUserAccountsUsingGET).toHaveBeenCalledTimes(1);
      expect(accountsService.getCurrentUserAccountsUsingGET).toHaveBeenLastCalledWith(0, 30, 'term', undefined);
      expect(page.accountNames.map((accountName) => accountName.textContent.trim())).toEqual([
        'account1',
        'account2',
        'account3',
      ]);
      expect(page.accountStatuses.map((accountStatus) => accountStatus.textContent.trim())).toEqual([
        'Live',
        'Live',
        'Live',
      ]);
    });

    it('should show accounts when account type checked and pressing search button', () => {
      page.installationCheckbox.click();
      fixture.detectChanges();

      expect(page.termErrorMessage).toBeNull();
      expect(accountsService.getCurrentUserAccountsUsingGET).toHaveBeenCalledTimes(1);
      expect(accountsService.getCurrentUserAccountsUsingGET).toHaveBeenLastCalledWith(0, 30, null, 'INSTALLATION');
      expect(page.accountNames.map((accountName) => accountName.textContent.trim())).toEqual([
        'account1',
        'account2',
        'account3',
      ]);
      expect(page.accountStatuses.map((accountStatus) => accountStatus.textContent.trim())).toEqual([
        'Live',
        'Live',
        'Live',
      ]);
    });
  });

  describe('for regulators with search params set', () => {
    const activatedRouteStub = new ActivatedRouteStub(
      undefined,
      {
        term: 'account',
        accountTypes: 'INSTALLATION',
      },
      undefined,
    );

    beforeEach(async () => {
      userStatus$ = new BehaviorSubject<UserStatusDTO>(regulatorUserRole);
      authService = {
        userStatus: userStatus$,
      };
      accountsService.getCurrentUserAccountsUsingGET.mockReturnValueOnce(of(mockAccountResults));
    });
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        declarations: [AccountsComponent],
        imports: [RouterTestingModule, SharedModule, SharedUserModule],
        providers: [
          { provide: ActivatedRoute, useValue: activatedRouteStub },
          { provide: AuthService, useValue: authService },
          { provide: AccountsService, useValue: accountsService },
        ],
      }).compileComponents();
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should load the accounts based on the query params', () => {
      expect(page.accountNames.map((accountName) => accountName.textContent.trim())).toEqual([
        'account1',
        'account2',
        'account3',
      ]);
      expect(page.accountStatuses.map((accountStatus) => accountStatus.textContent.trim())).toEqual([
        'Live',
        'Live',
        'Live',
      ]);
    });
  });
});

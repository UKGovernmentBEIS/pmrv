import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, of, throwError } from 'rxjs';

import { cloneDeep } from 'lodash-es';

import {
  AccountDetailsDTO,
  AccountVerificationBodyService,
  AuthoritiesService,
  OperatorAuthoritiesService,
  UserStatusDTO,
} from 'pmrv-api';

import {
  ActivatedRouteStub,
  asyncData,
  BasePage,
  changeInputValue,
  MockType,
  RouterStubComponent,
} from '../../../testing';
import { AuthService } from '../../core/services/auth.service';
import { ConcurrencyTestingModule, expectConcurrentErrorToBe } from '../../error/testing/concurrency-error';
import { SharedModule } from '../../shared/shared.module';
import { SharedUserModule } from '../../shared-user/shared-user.module';
import { mockedAccount, mockOperatorListRouteData, mockOperatorRoleCodes } from '../testing/mock-data';
import { DeleteComponent } from './delete/delete.component';
import { savePartiallyNotFoundOperatorError } from './errors/concurrency-error';
import { OperatorsComponent } from './operators.component';

describe('OperatorsComponent', () => {
  let component: OperatorsComponent;
  let fixture: ComponentFixture<OperatorsComponent>;
  let page: Page;
  let router: Router;
  let accountVerificationBodyService: MockType<AccountVerificationBodyService>;
  let authService: MockType<AuthService>;
  let activatedRouteStub: ActivatedRouteStub;
  let userStatus$: BehaviorSubject<UserStatusDTO>;

  class Page extends BasePage<OperatorsComponent> {
    get addUserFormButton() {
      return this.query<HTMLButtonElement>('form[id="add-user-form"] button[type="submit"]');
    }

    get registerUserButton() {
      return this.query<HTMLButtonElement>('button[id="reg-add-user"][type="button"]');
    }

    get userTypeSelect() {
      return this.query<HTMLSelectElement>('select[name="userType"]');
    }

    get usersForm() {
      return this.query<HTMLFormElement>('form[id="users-form"]');
    }

    get usersFormSubmitButton() {
      return this.usersForm.querySelector<HTMLButtonElement>('button[type="submit"]');
    }

    get nameSortingButton() {
      return this.usersForm.querySelector<HTMLButtonElement>('thead button');
    }

    get rows() {
      return Array.from(this.usersForm.querySelectorAll<HTMLTableRowElement>('tbody tr'));
    }

    get nameColumns() {
      return this.rows.map((row) => row.querySelector('td'));
    }

    get nameLinks() {
      return this.nameColumns.map((name) => name.querySelector('a'));
    }

    get roleSelects() {
      return this.queryAll<HTMLSelectElement>('select[name$=".roleCode"]');
    }

    get roleSelectsValue() {
      return this.roleSelects.map((select) => this.getInputValue(`#${select.id}`));
    }

    set roleSelectsValue(value: string[]) {
      this.roleSelects.forEach((select, index) => {
        if (value[index] !== undefined) {
          this.setInputValue(`#${select.id}`, value[index]);
        }
      });
    }

    get accountStatusSelects() {
      return this.queryAll<HTMLSelectElement>('select[name$=".authorityStatus"]');
    }

    get accountStatusSelectsValue() {
      return this.accountStatusSelects.map((select) => this.getInputValue(`#${select.id}`));
    }

    set accountStatusSelectsValue(value: string[]) {
      this.accountStatusSelects.forEach((select, index) => {
        if (value[index] !== undefined) {
          this.setInputValue(`#${select.id}`, value[index]);
        }
      });
    }

    get deleteLinks() {
      return this.rows.map((row) => row.querySelectorAll('a')[1]);
    }

    get contactRadios() {
      return this.rows.map((row) => row.querySelectorAll<HTMLInputElement>('input[type="radio"]'));
    }

    get locks() {
      return this.queryAll<HTMLDivElement>('.locked');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get appointVerifierLink() {
      return this.query<HTMLAnchorElement>('form > div > div > a');
    }

    get lastRow() {
      return this.rows[this.rows.length - 1];
    }

    get details() {
      return this.query<HTMLDetailsElement>('details.govuk-details');
    }

    get detailsSummary() {
      return this.query<HTMLSpanElement>('.govuk-details__summary-text');
    }
  }

  const operatorAuthoritiesService: MockType<OperatorAuthoritiesService> = {
    updateAccountOperatorAuthoritiesUsingPOST: jest.fn().mockReturnValue(of(null)),
    deleteAccountOperatorAuthorityUsingDELETE: jest.fn().mockReturnValue(of(null)),
    deleteCurrentUserAccountOperatorAuthorityUsingDELETE: jest.fn().mockReturnValue(of(null)),
    getAccountOperatorAuthoritiesUsingGET: jest.fn().mockReturnValue(of(mockOperatorListRouteData.users)),
  };

  const authoritiesService: MockType<AuthoritiesService> = {
    getOperatorRoleCodesUsingGET: jest.fn().mockReturnValue(asyncData(mockOperatorRoleCodes)),
  };

  const setUser = (roleType: UserStatusDTO['roleType'], editable: boolean) => {
    userStatus$.next({ roleType, userId: 'opTestId' });
    const testData = cloneDeep(mockOperatorListRouteData);
    testData.users.editable = editable;
    activatedRouteStub.setResolveMap(testData);
    fixture.detectChanges();
  };
  const setUserAsOperatorAdmin = () => setUser('OPERATOR', true);
  const setUserAsOperator = () => setUser('OPERATOR', false);
  const setUserAsRegulatorAdmin = () => setUser('REGULATOR', true);
  const setUserAsRegulator = () => setUser('REGULATOR', false);
  const expectUserOrderToBe = (indexes: number[]) =>
    expect(page.nameColumns.map((name) => name.textContent.trim())).toEqual(
      indexes.map(
        (index) =>
          `${mockOperatorListRouteData.users.authorities[index].firstName} ${mockOperatorListRouteData.users.authorities[index].lastName}`,
      ),
    );

  const createComponent = () => {
    fixture = TestBed.createComponent(OperatorsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  const createModule = async () => {
    accountVerificationBodyService = {
      getVerificationBodyOfAccountUsingGET: jest.fn().mockReturnValue(throwError(() => ({ status: 404 }))),
    };
    activatedRouteStub = new ActivatedRouteStub(
      { accountId: mockedAccount.id },
      null,
      cloneDeep(mockOperatorListRouteData),
    );
    userStatus$ = new BehaviorSubject<UserStatusDTO>({
      loginStatus: 'ENABLED',
      roleType: 'OPERATOR',
      userId: 'opTestId',
    });
    authService = {
      userStatus: userStatus$,
      loadUserStatus: jest.fn(),
    };

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, ConcurrencyTestingModule, SharedModule, SharedUserModule],
      declarations: [OperatorsComponent, DeleteComponent, RouterStubComponent],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: AuthService, useValue: authService },
        { provide: AuthoritiesService, useValue: authoritiesService },
        { provide: OperatorAuthoritiesService, useValue: operatorAuthoritiesService },
        { provide: AccountVerificationBodyService, useValue: accountVerificationBodyService },
      ],
    }).compileComponents();

    router = TestBed.inject(Router);
  };

  afterEach(() => {
    jest.clearAllMocks();
  });

  describe('for approved accounts', () => {
    beforeEach(createModule);
    beforeEach(createComponent);

    it('should create the component', () => {
      expect(component).toBeTruthy();
    });

    it('should render the title', () => {
      const element: HTMLElement = fixture.nativeElement;
      const header = element.querySelector('h1[class="govuk-heading-l"]');

      expect(header).toBeTruthy();
      expect(header.innerHTML.trim()).toEqual('Users, contacts and verifiers');
    });

    it('should display the add new user button for the regulator admin', () => {
      setUserAsRegulatorAdmin();

      expect(page.addUserFormButton).toBeFalsy();

      expect(page.registerUserButton).toBeTruthy();
      expect(page.registerUserButton.innerHTML.trim()).toEqual('Add new operator admin');

      setUserAsRegulator();
      fixture.detectChanges();

      expect(page.registerUserButton).toBeFalsy();
      expect(page.addUserFormButton).toBeFalsy();
    });

    it('should display the add new user button for the operator', () => {
      setUserAsOperatorAdmin();
      fixture.detectChanges();

      expect(page.addUserFormButton).toBeTruthy();
      expect(page.addUserFormButton.innerHTML.trim()).toEqual('Continue');
      expect(Array.from(page.userTypeSelect.options).map((option) => option.textContent.trim())).toEqual(
        mockOperatorRoleCodes.map((opCode) => opCode.name),
      );

      setUserAsOperator();
      fixture.detectChanges();

      expect(page.addUserFormButton).toBeFalsy();
    });

    it('should render a learn more section to operator admins', () => {
      setUserAsRegulator();
      expect(page.details).toBeFalsy();

      setUserAsRegulatorAdmin();
      expect(page.details).toBeFalsy();

      setUserAsOperator();
      expect(page.details).toBeFalsy();

      setUserAsOperatorAdmin();
      expect(page.details).toBeTruthy();
      expect(page.detailsSummary.innerHTML.trim()).toEqual('Learn more about user types, roles and permissions');
    });

    it('should initialize with default sorting by created date', () => {
      expectUserOrderToBe([0, 2, 1, 3]);
    });

    it('should sort by name', () => {
      page.nameSortingButton.click();
      fixture.detectChanges();

      expectUserOrderToBe([3, 2, 0, 1]);

      page.nameSortingButton.click();
      fixture.detectChanges();

      expectUserOrderToBe([1, 0, 2, 3]);
    });

    it('should show locked status sign', () => {
      const testData = cloneDeep(mockOperatorListRouteData);
      testData.users.editable = true;
      activatedRouteStub.setResolveMap(testData);
      fixture.detectChanges();

      expect(page.locks).toHaveLength(1);
    });

    it('should render a save changes button', () => {
      userStatus$.next({ roleType: 'OPERATOR', userId: 'opTestId' });
      const testData = cloneDeep(mockOperatorListRouteData);
      testData.users.editable = true;
      activatedRouteStub.setResolveMap(testData);
      fixture.detectChanges();

      expect(page.usersFormSubmitButton).toBeTruthy();
      expect(page.usersFormSubmitButton.innerHTML.trim()).toEqual('Save');

      testData.users.editable = false;
      activatedRouteStub.setResolveMap(testData);
      fixture.detectChanges();

      expect(page.usersFormSubmitButton).toBeFalsy();

      userStatus$.next({ roleType: 'REGULATOR', userId: 'regTestId' });
      fixture.detectChanges();

      expect(page.usersFormSubmitButton).toBeFalsy();

      testData.users.editable = false;
      activatedRouteStub.setResolveMap(testData);
      fixture.detectChanges();

      expect(page.usersFormSubmitButton).toBeFalsy();
    });

    it('should render the list of users with links where applicable', () => {
      userStatus$.next({ roleType: 'OPERATOR', userId: 'userTest1' });
      const testData = cloneDeep(mockOperatorListRouteData);
      activatedRouteStub.setResolveMap(testData);
      fixture.detectChanges();

      expectUserOrderToBe([0, 2, 1, 3]);

      testData.users.editable = true;
      activatedRouteStub.setResolveMap(testData);
      fixture.detectChanges();

      expect(page.nameLinks[1]).toBeTruthy();
      expect(page.nameLinks[1].textContent.trim()).toEqual(
        `${mockOperatorListRouteData.users.authorities[2].firstName} ${mockOperatorListRouteData.users.authorities[2].lastName}`,
      );
      expect(page.roleSelectsValue[0]).toEqual('0: operator_admin');
    });

    it('should not accept submission without at least one active operator admin', () => {
      page.roleSelectsValue = ['operator'];
      fixture.detectChanges();

      page.usersFormSubmitButton.click();
      fixture.detectChanges();

      expect(page.usersFormSubmitButton.disabled).toBeFalsy();
      expect(page.errorSummary).toBeTruthy();

      page.roleSelectsValue = [undefined, undefined, 'operator_admin'];
      page.usersFormSubmitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
    });

    it('should not accept submission with the same primary and secondary contact', () => {
      page.contactRadios[2][0].click();
      page.contactRadios[2][1].click();
      fixture.detectChanges();

      page.usersFormSubmitButton.click();
      fixture.detectChanges();

      expect(page.usersFormSubmitButton.disabled).toBeFalsy();
      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummary.textContent).toContain(
        'You cannot assign the same user as a primary and secondary contact on your account',
      );

      page.contactRadios[3][1].click();
      page.usersFormSubmitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
    });

    it('should not accept submission without at least one active primary, service and financial contact', () => {
      page.accountStatusSelectsValue = ['DISABLED'];
      fixture.detectChanges();

      page.usersFormSubmitButton.click();
      fixture.detectChanges();

      expect(page.usersFormSubmitButton.disabled).toBeFalsy();
      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummary.textContent).toContain('You must have a primary contact on your account');

      page.accountStatusSelectsValue = ['ACTIVE', undefined, 'DISABLED'];
      page.usersFormSubmitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary.textContent).not.toContain('You must have a primary contact on your account');
      expect(page.errorSummary.textContent).toContain('You must have a service contact on your account');

      page.accountStatusSelectsValue = [undefined, undefined, 'ACTIVE', 'DISABLED'];
      page.usersFormSubmitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary.textContent).not.toContain('You must have a primary contact on your account');
      expect(page.errorSummary.textContent).not.toContain('You must have a service contact on your account');
      expect(page.errorSummary.textContent).toContain('You must have a financial contact on your account');

      page.accountStatusSelectsValue = [undefined, undefined, undefined, 'ACTIVE'];
      page.usersFormSubmitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
    });

    it('should display correct dropdown values in users table', () => {
      expect(Array.from(page.roleSelects[0].options).map((option) => option.textContent.trim())).toEqual(
        mockOperatorRoleCodes.slice(0, 2).map((opCode) => opCode.name),
      );
    });

    it('should only allow role edit on operator and operator admins', () => {
      expect(page.roleSelects[0]).toBeTruthy();
      expect(page.rows[1].querySelector('select[name$=".roleCode"]')).toBeFalsy();
      expect(page.rows[1].querySelectorAll('td')[1].textContent.trim()).toEqual(
        mockOperatorListRouteData.users.authorities[2].roleName,
      );
    });

    it('should show appoint verifier button if one is not already appointed', async () => {
      expect(page.appointVerifierLink).toBeTruthy();

      accountVerificationBodyService.getVerificationBodyOfAccountUsingGET.mockReturnValue(
        asyncData({
          id: 1,
          name: 'Verifying company',
        }),
      );
      activatedRouteStub.setParamMap({ accountId: 2 });

      await fixture.whenStable();
      fixture.detectChanges();

      expect(page.appointVerifierLink).toBeFalsy();
    });

    it('should navigate to add operator form', () => {
      const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

      page.addUserFormButton.click();
      fixture.detectChanges();

      expect(navigateSpy).toHaveBeenCalledWith(['users/add', 'operator'], {
        relativeTo: TestBed.inject(ActivatedRoute),
      });
    });

    it('should disable the inputs of a disabled record', () => {
      expect(page.nameColumns[1].textContent.trim()).toEqual('Darth Vader');
      expect(page.rows[1].querySelector('select[name$=".roleCode"]')).toBeFalsy();
      page.contactRadios[1].forEach((radio) => expect(radio.disabled).toBeTruthy());
      page.contactRadios[0].forEach((radio) => expect(radio.disabled).toBeFalsy());

      page.accountStatusSelectsValue = [undefined, 'ACTIVE'];
      fixture.detectChanges();

      expect(page.roleSelects[1]).toBeTruthy();
    });

    it('should keep the changed values and form status after sort', () => {
      expect(page.nameColumns[1].textContent.trim()).toEqual('Darth Vader');

      page.accountStatusSelectsValue = [undefined, 'ACTIVE'];
      fixture.detectChanges();
      expect(page.accountStatusSelectsValue[1]).toEqual('0: ACTIVE');

      page.nameSortingButton.click();
      fixture.detectChanges();

      expect(page.nameColumns[1].textContent.trim()).toEqual('Darth Vader');
      expect(page.accountStatusSelectsValue[1]).toEqual('0: ACTIVE');

      page.nameSortingButton.click();
      fixture.detectChanges();

      expect(page.nameColumns[2].textContent.trim()).toEqual('Darth Vader');
      expect(page.accountStatusSelectsValue[2]).toEqual('0: ACTIVE');
    });

    it('should post only changed values on save', () => {
      const testData = cloneDeep(mockOperatorListRouteData);
      testData.users.editable = true;
      activatedRouteStub.setResolveMap(testData);
      fixture.detectChanges();

      changeInputValue(fixture, '#usersArray\\.3\\.roleCode', 'operator_admin');
      fixture.detectChanges();

      page.usersFormSubmitButton.click();
      fixture.detectChanges();

      expect(operatorAuthoritiesService.updateAccountOperatorAuthoritiesUsingPOST).toHaveBeenCalledWith(
        mockedAccount.id,
        {
          accountOperatorAuthorityUpdateList: [
            { authorityStatus: 'ACTIVE', roleCode: 'operator_admin', userId: 'userTest4' },
          ],
          contactTypes: testData.users.contactTypes,
        },
      );
    });

    it('should post only changed values on save after sort', () => {
      const testData = cloneDeep(mockOperatorListRouteData);
      testData.users.editable = true;
      activatedRouteStub.setResolveMap(testData);
      fixture.detectChanges();

      changeInputValue(fixture, '#usersArray\\.3\\.roleCode', 'operator_admin');
      fixture.detectChanges();

      page.nameSortingButton.click();
      fixture.detectChanges();

      page.usersFormSubmitButton.click();
      fixture.detectChanges();

      expect(operatorAuthoritiesService.updateAccountOperatorAuthoritiesUsingPOST).toHaveBeenCalledWith(
        mockedAccount.id,
        {
          accountOperatorAuthorityUpdateList: [
            { authorityStatus: 'ACTIVE', roleCode: 'operator_admin', userId: 'userTest4' },
          ],
          contactTypes: testData.users.contactTypes,
        },
      );
    });

    it('should display check marks for regulators or no editable operators', () => {
      const testData = cloneDeep(mockOperatorListRouteData);
      activatedRouteStub.setResolveMap(testData);
      fixture.detectChanges();
      const checkNonEditableContacts = () => {
        expect(page.rows[0].querySelectorAll('td')[2].textContent.trim()).toEqual('✓');
        expect(page.rows[0].querySelectorAll('td')[3].textContent.trim()).toBeFalsy();
        expect(page.rows[1].querySelectorAll('td')[3].textContent.trim()).toEqual('✓');
        expect(page.rows[1].querySelectorAll('td')[2].textContent.trim()).toBeFalsy();
        expect(page.roleSelects[0]).toBeFalsy();
        expect(page.roleSelects[2]).toBeFalsy();
      };

      setUserAsRegulator();
      fixture.detectChanges();

      checkNonEditableContacts();

      setUserAsRegulatorAdmin();
      fixture.detectChanges();

      expect(page.rows[0].querySelectorAll('td')[2].textContent.trim()).not.toEqual('✓');
      expect(page.rows[1].querySelectorAll('td')[3].textContent.trim()).not.toEqual('✓');
      expect(page.roleSelects[0]).toBeTruthy();
      expect(page.roleSelects[2]).toBeTruthy();

      setUserAsOperator();
      fixture.detectChanges();

      checkNonEditableContacts();

      setUserAsOperatorAdmin();
      fixture.detectChanges();

      expect(page.rows[0].querySelectorAll('td')[2].textContent.trim()).not.toEqual('✓');
      expect(page.rows[1].querySelectorAll('td')[3].textContent.trim()).not.toEqual('✓');
      expect(page.roleSelects[0]).toBeTruthy();
      expect(page.roleSelects[2]).toBeTruthy();
    });

    it('should show error summary when updating a deleted user', async () => {
      operatorAuthoritiesService.updateAccountOperatorAuthoritiesUsingPOST.mockReturnValue(
        throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'AUTHORITY1004' } })),
      );

      const testData = cloneDeep(mockOperatorListRouteData);
      testData.users.editable = true;
      activatedRouteStub.setResolveMap(testData);
      fixture.detectChanges();

      changeInputValue(fixture, '#usersArray\\.3\\.roleCode', 'operator_admin');
      fixture.detectChanges();

      page.usersFormSubmitButton.click();
      fixture.detectChanges();

      await expectConcurrentErrorToBe(savePartiallyNotFoundOperatorError(mockedAccount.id));
    });

    it('should not display the appoint verifier link to non operator admins', () => {
      setUserAsOperator();

      expect(page.appointVerifierLink).toBeFalsy();
    });

    it('should show verifier row if one is already appointed', async () => {
      accountVerificationBodyService.getVerificationBodyOfAccountUsingGET.mockReturnValue(
        asyncData({
          id: 1,
          name: 'Verifying company',
        }),
      );
      activatedRouteStub.setParamMap({ accountId: mockedAccount.id });
      await fixture.whenStable();
      fixture.detectChanges();

      expect(Array.from(page.lastRow.querySelectorAll('td')).map((cell) => cell.textContent.trim())).toEqual([
        'Verifying company',
        'Verifier',
        '',
        '',
        '',
        '',
        '',
        'Replace verifier',
      ]);

      setUserAsOperator();

      expect(Array.from(page.lastRow.querySelectorAll('td')).map((cell) => cell.textContent.trim())).toEqual([
        'Verifying company',
        'Verifier',
        '',
        '',
        '',
        '',
      ]);
    });
  });

  describe('for unapproved accounts', () => {
    let unapprovedAccount: AccountDetailsDTO;
    beforeEach(async () => {
      unapprovedAccount = { ...mockedAccount, status: 'UNAPPROVED' };
    });

    beforeEach(createModule);
    beforeEach(createComponent);

    it('should create the component', () => {
      expect(component).toBeTruthy();
    });

    it('should not display the add new user button when account is in unapproved status', () => {
      setUserAsRegulatorAdmin();

      const account = cloneDeep(unapprovedAccount);

      activatedRouteStub.setResolveMap({ account });

      fixture.detectChanges();

      expect(page.registerUserButton).toBeFalsy();
    });
  });

  describe('for denied accounts', () => {
    let unapprovedAccount: AccountDetailsDTO;
    beforeEach(async () => {
      unapprovedAccount = { ...mockedAccount, status: 'DENIED' };
    });

    beforeEach(createModule);
    beforeEach(createComponent);

    it('should create the component', () => {
      expect(component).toBeTruthy();
    });

    it('should not display the add new user button when account is in unapproved status', () => {
      setUserAsRegulatorAdmin();

      const account = cloneDeep(unapprovedAccount);

      activatedRouteStub.setResolveMap({ account });

      fixture.detectChanges();

      expect(page.registerUserButton).toBeFalsy();
    });
  });
});

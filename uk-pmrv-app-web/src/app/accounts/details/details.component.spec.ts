import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { CountryService } from '@core/services/country.service';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, CountryServiceStub } from '@testing';

import { UserStatusDTO } from 'pmrv-api';

import { SharedUserModule } from '../../shared-user/shared-user.module';
import { mockedAccount } from '../testing/mock-data';
import { DetailsComponent } from './details.component';

describe('DetailsComponent', () => {
  let component: DetailsComponent;
  let fixture: ComponentFixture<DetailsComponent>;
  let page: Page;
  let userStatus$: BehaviorSubject<UserStatusDTO>;
  let authService: Partial<jest.Mocked<AuthService>>;
  let activatedRouteStub: ActivatedRouteStub;

  class Page extends BasePage<DetailsComponent> {
    get heading() {
      return this.queryAll<HTMLHeadingElement>('h2');
    }

    get accountDetails() {
      return this.queryAll<HTMLElement>('dl dd:not(.govuk-summary-list__actions)');
    }

    get actions() {
      return this.queryAll<HTMLElement>('dl dd.govuk-summary-list__actions');
    }
  }

  const createModule = async () => {
    await TestBed.configureTestingModule({
      declarations: [DetailsComponent],
      imports: [RouterTestingModule, SharedModule, SharedUserModule],
      providers: [
        { provide: CountryService, useClass: CountryServiceStub },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: AuthService, useValue: authService },
      ],
    }).compileComponents();
  };

  const createComponent = () => {
    fixture = TestBed.createComponent(DetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('approved account for operators', () => {
    beforeEach(async () => {
      activatedRouteStub = new ActivatedRouteStub(undefined, undefined, {
        account: mockedAccount,
      });

      userStatus$ = new BehaviorSubject<UserStatusDTO>({
        loginStatus: 'ENABLED',
        roleType: 'OPERATOR',
        userId: 'opTestId',
      });

      authService = {
        userStatus: userStatus$,
        loadUserStatus: jest.fn(),
      };
    });

    beforeEach(createModule);
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the headings', () => {
      expect(page.heading.map((el) => el.textContent.trim())).toEqual([
        'Active permit',
        'Installation details',
        'Organisation details',
      ]);
    });

    it('should render the account details', () => {
      expect(page.accountDetails.map((dd) => dd.textContent.trim())).toEqual([
        '',
        '',
        '',
        'permitId',
        'GHGE',
        'Category A (Low emitter)',

        'accountName',
        'siteName',
        '222',
        '111',
        'A',
        'New Permit',
        'NN166712 linetown1231Greece',

        'leName',
        'Limited Company',
        '11111',
        'linetown1231Greece',
      ]);
    });

    it('should render the edit links', () => {
      expect(Array.from(page.actions).filter((action) => action.textContent.trim() === 'Change').length).toEqual(0);
    });
  });

  describe('unapproved account for operators', () => {
    beforeEach(async () => {
      const unapprovedAccount = { ...mockedAccount, status: 'UNAPPROVED' };
      activatedRouteStub = new ActivatedRouteStub(undefined, undefined, {
        account: unapprovedAccount,
      });

      userStatus$ = new BehaviorSubject<UserStatusDTO>({
        loginStatus: 'ENABLED',
        roleType: 'OPERATOR',
        userId: 'opTestId',
      });

      authService = {
        userStatus: userStatus$,
        loadUserStatus: jest.fn(),
      };
    });

    beforeEach(createModule);
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should not render the active permit', () => {
      expect(page.accountDetails.map((dd) => dd.textContent.trim())).toEqual([
        'accountName',
        'siteName',
        '222',
        '111',
        'A',
        'New Permit',
        'NN166712 linetown1231Greece',

        'leName',
        'Limited Company',
        '11111',
        'linetown1231Greece',
      ]);
    });

    it('should not render the edit links', () => {
      expect(Array.from(page.actions).filter((action) => action.textContent.trim() === 'Change').length).toEqual(0);
    });
  });

  describe('approved account for regulators', () => {
    beforeEach(async () => {
      activatedRouteStub = new ActivatedRouteStub(undefined, undefined, {
        account: mockedAccount,
      });

      userStatus$ = new BehaviorSubject<UserStatusDTO>({
        loginStatus: 'ENABLED',
        roleType: 'REGULATOR',
        userId: 'regUserId',
      });

      authService = {
        userStatus: userStatus$,
        loadUserStatus: jest.fn(),
      };
    });

    beforeEach(createModule);
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render the headings', () => {
      expect(page.heading.map((el) => el.textContent.trim())).toEqual([
        'Active permit',
        'Installation details',
        'Organisation details',
      ]);
    });

    it('should render the account details', () => {
      expect(page.accountDetails.map((dd) => dd.textContent.trim())).toEqual([
        '',
        '',
        '',
        'permitId',
        'GHGE',
        'Category A (Low emitter)',

        'accountName',
        'siteName',
        '222',
        '111',
        'A',
        'New Permit',
        'NN166712 linetown1231Greece',

        'leName',
        'Limited Company',
        '11111',
        'linetown1231Greece',
      ]);
    });

    it('should render the edit links', () => {
      expect(Array.from(page.actions).filter((action) => action.textContent.trim() === 'Change').length).toEqual(6);
    });
  });

  describe('unapproved account for operators', () => {
    beforeEach(async () => {
      const unapprovedAccount = { ...mockedAccount, status: 'UNAPPROVED' };
      activatedRouteStub = new ActivatedRouteStub(undefined, undefined, {
        account: unapprovedAccount,
      });

      userStatus$ = new BehaviorSubject<UserStatusDTO>({
        loginStatus: 'ENABLED',
        roleType: 'REGULATOR',
        userId: 'regUserId',
      });

      authService = {
        userStatus: userStatus$,
        loadUserStatus: jest.fn(),
      };
    });

    beforeEach(createModule);
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should not render the active permit', () => {
      expect(page.accountDetails.map((dd) => dd.textContent.trim())).toEqual([
        'accountName',
        'siteName',
        '222',
        '111',
        'A',
        'New Permit',
        'NN166712 linetown1231Greece',

        'leName',
        'Limited Company',
        '11111',
        'linetown1231Greece',
      ]);
    });

    it('should not render the edit links', () => {
      expect(Array.from(page.actions).filter((action) => action.textContent.trim() === 'Change').length).toEqual(0);
    });
  });

  describe('denied account for operators', () => {
    beforeEach(async () => {
      const unapprovedAccount = { ...mockedAccount, status: 'DENIED' };
      activatedRouteStub = new ActivatedRouteStub(undefined, undefined, {
        account: unapprovedAccount,
      });

      userStatus$ = new BehaviorSubject<UserStatusDTO>({
        loginStatus: 'ENABLED',
        roleType: 'REGULATOR',
        userId: 'regUserId',
      });

      authService = {
        userStatus: userStatus$,
        loadUserStatus: jest.fn(),
      };
    });

    beforeEach(createModule);
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should not render the active permit', () => {
      expect(page.accountDetails.map((dd) => dd.textContent.trim())).toEqual([
        'accountName',
        'siteName',
        '222',
        '111',
        'A',
        'New Permit',
        'NN166712 linetown1231Greece',

        'leName',
        'Limited Company',
        '11111',
        'linetown1231Greece',
      ]);
    });

    it('should not render the edit links', () => {
      expect(Array.from(page.actions).filter((action) => action.textContent.trim() === 'Change').length).toEqual(0);
    });
  });
});

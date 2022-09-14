import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject } from 'rxjs';

import { UserStatusDTO } from 'pmrv-api';

import { ActivatedRouteStub, BasePage } from '../../testing';
import { AuthService } from '../core/services/auth.service';
import { SharedModule } from '../shared/shared.module';
import { SharedUserModule } from '../shared-user/shared-user.module';
import { AccountComponent } from './account.component';
import { DetailsComponent } from './details/details.component';
import { mockOperatorListRouteData } from './testing/mock-data';
import { mockedAccount } from './testing/mock-data';

describe('AccountComponent', () => {
  let component: AccountComponent;
  let fixture: ComponentFixture<AccountComponent>;
  let page: Page;
  let userStatus$: BehaviorSubject<UserStatusDTO>;
  let authService: Partial<jest.Mocked<AuthService>>;

  const activatedRouteStub = new ActivatedRouteStub(undefined, undefined, {
    account: mockedAccount,
    users: mockOperatorListRouteData,
  });

  class Page extends BasePage<AccountComponent> {
    get heading() {
      return this.query<HTMLElement>('app-page-heading h1.govuk-heading-xl');
    }

    get status() {
      return this.heading.querySelector<HTMLSpanElement>('span.status');
    }

    get tabs() {
      return Array.from(this.queryAll<HTMLLIElement>('ul.govuk-tabs__list > li'));
    }
  }

  @Component({
    selector: 'app-workflows',
    template: '',
  })
  class MockWorkflowsComponent {}

  @Component({
    selector: 'app-operators',
    template: '',
  })
  class MockOperatorsComponent {}

  beforeEach(async () => {
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
      declarations: [AccountComponent, DetailsComponent, MockWorkflowsComponent, MockOperatorsComponent],
      imports: [RouterTestingModule, SharedModule, SharedUserModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: AuthService, useValue: authService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountComponent);
    component = fixture.componentInstance;
    window.history.pushState({ accountTypes: 'INSTALLATION', page: '1' }, 'yes');
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the account name', () => {
    expect(page.heading.textContent.trim()).toContain(mockedAccount.name);
  });

  it('should render the status', () => {
    expect(page.status.textContent.trim()).toEqual('Live');
  });

  it('should render the main tabs', () => {
    expect(page.tabs.map((tab) => tab.textContent.trim())).toEqual(['Details', 'Workflows', 'Users and contacts']);
  });
});

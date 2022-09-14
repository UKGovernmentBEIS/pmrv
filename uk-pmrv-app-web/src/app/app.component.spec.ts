import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { UserStatusDTO } from 'pmrv-api';

import { BasePage } from '../testing';
import { AppComponent } from './app.component';
import { AuthService } from './core/services/auth.service';
import { BREADCRUMB_ITEMS } from './shared/breadcrumbs/breadcrumb.factory';
import { BreadcrumbItem } from './shared/breadcrumbs/breadcrumb.interface';
import { SharedModule } from './shared/shared.module';
import { TimeoutModule } from './timeout/timeout.module';

describe('AppComponent', () => {
  let component: AppComponent;
  let fixture: ComponentFixture<AppComponent>;
  let page: Page;
  let breadcrumbItem$: BehaviorSubject<BreadcrumbItem[]>;

  const authService: Partial<jest.Mocked<AuthService>> = {
    userStatus: new BehaviorSubject<UserStatusDTO>(null),
    isLoggedIn: new BehaviorSubject<boolean>(true),
  };

  const setUser = (roleType: UserStatusDTO['roleType'], loginStatus?: UserStatusDTO['loginStatus']) => {
    authService.userStatus.next({ roleType, loginStatus });
    fixture.detectChanges();
  };

  class Page extends BasePage<AppComponent> {
    get footer() {
      return this.query<HTMLElement>('.govuk-footer');
    }

    get dashboardLink() {
      return this.query<HTMLAnchorElement>('a[href="/dashboard"]');
    }

    get regulatorsLink() {
      return this.query<HTMLAnchorElement>('a[href="/user/regulators"]');
    }

    get accountsLink() {
      return this.query<HTMLAnchorElement>('a[href="/accounts"]');
    }

    get templatesLink() {
      return this.query<HTMLAnchorElement>('a[href="/templates"]');
    }

    get navList() {
      return this.query<HTMLDivElement>('.hmcts-primary-navigation');
    }

    get breadcrumbs() {
      return this.queryAll<HTMLLIElement>('.govuk-breadcrumbs__list-item');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, TimeoutModule],
      declarations: [AppComponent],
      providers: [KeycloakService, { provide: AuthService, useValue: authService }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AppComponent);
    component = fixture.componentInstance;
    breadcrumbItem$ = TestBed.inject(BREADCRUMB_ITEMS);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create the app', () => {
    expect(component).toBeTruthy();
  });

  it('should render the footer', () => {
    expect(page.footer).toBeTruthy();
  });

  it('should not render the dashboard link for disabled users or an operator with no authority', () => {
    setUser('OPERATOR', 'NO_AUTHORITY');

    expect(page.dashboardLink).toBeFalsy();

    setUser('OPERATOR', 'ENABLED');

    expect(page.dashboardLink).toBeTruthy();

    setUser('REGULATOR', 'ENABLED');

    expect(page.dashboardLink).toBeTruthy();

    setUser('REGULATOR', 'DISABLED');

    expect(page.dashboardLink).toBeFalsy();
  });

  it('should render the regulators link only if the user is regulator', () => {
    setUser('OPERATOR', 'NO_AUTHORITY');

    expect(page.regulatorsLink).toBeFalsy();

    setUser('REGULATOR');

    expect(page.regulatorsLink).toBeTruthy();
  });

  it('should render the accounts link only if the user is regulator or authorized operator', () => {
    setUser('OPERATOR', 'NO_AUTHORITY');

    expect(page.accountsLink).toBeFalsy();

    setUser('VERIFIER');

    expect(page.accountsLink).toBeFalsy();

    setUser('REGULATOR');

    expect(page.accountsLink).toBeTruthy();

    setUser('OPERATOR', 'ENABLED');

    expect(page.accountsLink).toBeTruthy();
  });

  it('should render the templates link only if the user is a regulator', () => {
    setUser('OPERATOR');

    expect(page.templatesLink).toBeFalsy();

    setUser('VERIFIER');

    expect(page.templatesLink).toBeFalsy();

    setUser('REGULATOR');

    expect(page.templatesLink).toBeTruthy();
  });

  it('should not render the nav list if user is disabled', () => {
    expect(page.navList).toBeTruthy();

    authService.userStatus.next({ loginStatus: 'DISABLED', roleType: 'REGULATOR' });
    fixture.detectChanges();

    expect(page.navList).toBeFalsy();

    authService.userStatus.next({ loginStatus: 'TEMP_DISABLED', roleType: 'VERIFIER' });
    fixture.detectChanges();

    expect(page.navList).toBeFalsy();
  });

  it('should not render the nav list if user is not logged in', () => {
    setUser('OPERATOR', 'NO_AUTHORITY');

    expect(page.navList).toBeFalsy();

    setUser('OPERATOR', 'ENABLED');

    expect(page.navList).toBeTruthy();

    authService.isLoggedIn.next(false);
    fixture.detectChanges();

    expect(page.navList).toBeFalsy();
  });

  it('should display breadcrumbs', () => {
    expect(page.breadcrumbs).toEqual([]);

    breadcrumbItem$.next([{ text: 'Dashboard', link: ['/dashboard'] }, { text: 'Apply for a GHGE permit' }]);
    fixture.detectChanges();

    expect(Array.from(page.breadcrumbs).map((breacrumb) => breacrumb.textContent)).toEqual([
      'Dashboard',
      'Apply for a GHGE permit',
    ]);

    expect(page.breadcrumbs[0].querySelector<HTMLAnchorElement>('a').href).toContain('/dashboard');
    expect(page.breadcrumbs[1].querySelector<HTMLAnchorElement>('a')).toBeFalsy();

    breadcrumbItem$.next(null);
    fixture.detectChanges();

    expect(page.breadcrumbs).toEqual([]);
  });
});

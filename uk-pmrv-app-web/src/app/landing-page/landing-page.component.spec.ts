import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject } from 'rxjs';

import { KeycloakProfile } from 'keycloak-js';

import { GovukComponentsModule } from 'govuk-components';

import { UserStatusDTO } from 'pmrv-api';

import { BasePage } from '../../testing';
import { AuthService } from '../core/services/auth.service';
import { SharedModule } from '../shared/shared.module';
import { LandingPageComponent } from './landing-page.component';

describe('LandingPageComponent', () => {
  let component: LandingPageComponent;
  let fixture: ComponentFixture<LandingPageComponent>;
  let authService: Partial<jest.Mocked<AuthService>>;
  let page: Page;

  class Page extends BasePage<LandingPageComponent> {
    get notLoggedInLandingPageLinks() {
      return this.queryAll<HTMLAnchorElement>('.govuk-button--start');
    }

    get installationLink() {
      return this.query<HTMLAnchorElement>('a[href="/installation-account"]');
    }
  }

  const setUser = (roleType: UserStatusDTO['roleType'], loginStatus?: UserStatusDTO['loginStatus']) => {
    authService.userStatus.next({ roleType, loginStatus });
    fixture.detectChanges();
  };

  beforeEach(async () => {
    authService = {
      isLoggedIn: new BehaviorSubject<boolean>(false),
      userProfile: new BehaviorSubject<KeycloakProfile>(null),
      userStatus: new BehaviorSubject<UserStatusDTO>(null),
    };
    await TestBed.configureTestingModule({
      imports: [GovukComponentsModule, SharedModule, RouterTestingModule],
      declarations: [LandingPageComponent],
      providers: [{ provide: AuthService, useValue: authService }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LandingPageComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the landing page buttons if not logged in', () => {
    expect(page.installationLink).toBeFalsy();
    expect(page.notLoggedInLandingPageLinks).toHaveLength(2);
  });

  it('should only display installation application button to operators', () => {
    expect(page.installationLink).toBeFalsy();
    expect(page.notLoggedInLandingPageLinks).toHaveLength(2);

    authService.isLoggedIn.next(true);
    setUser('OPERATOR', 'NO_AUTHORITY');

    expect(page.installationLink).toBeTruthy();

    setUser('OPERATOR', 'DISABLED');

    expect(page.installationLink).toBeTruthy();

    setUser('REGULATOR', 'DISABLED');

    expect(page.installationLink).toBeFalsy();

    setUser('VERIFIER', 'TEMP_DISABLED');

    expect(page.installationLink).toBeFalsy();
  });
});

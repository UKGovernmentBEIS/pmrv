import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, of, throwError } from 'rxjs';

import { ApplicationUserDTO, RegulatorAuthoritiesService, UserStatusDTO } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, expectToHaveNavigatedTo, RouterStubComponent } from '../../../testing';
import { AuthService } from '../../core/services/auth.service';
import { ConcurrencyTestingModule, expectConcurrentErrorToBe } from '../../error/testing/concurrency-error';
import { SharedModule } from '../../shared/shared.module';
import { saveNotFoundRegulatorError } from '../errors/concurrency-error';
import { DeleteComponent } from './delete.component';

describe('DeleteComponent', () => {
  let component: DeleteComponent;
  let fixture: ComponentFixture<DeleteComponent>;
  let page: Page;

  class Page extends BasePage<DeleteComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h2');
    }

    get cancelLink() {
      return this.queryAll<HTMLAnchorElement>('a').find((element) => element.textContent.trim() === 'Cancel');
    }

    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }

    get panelTitle() {
      return this.query<HTMLDivElement>('.govuk-panel__title');
    }

    get returnLink() {
      return this.query<HTMLAnchorElement>('a');
    }
  }

  const user: ApplicationUserDTO = {
    email: 'alfyn-octo@pmrv.uk',
    firstName: 'Alfyn',
    lastName: 'Octo',
    termsVersion: 1,
  };

  let regulatorAuthoritiesService: Partial<jest.Mocked<RegulatorAuthoritiesService>>;

  let authService: Partial<jest.Mocked<AuthService>>;

  beforeEach(async () => {
    const activatedRoute = new ActivatedRouteStub({ userId: '1reg' }, null, { user });
    authService = {
      logout: jest.fn(),
      userStatus: new BehaviorSubject<UserStatusDTO>(null),
    };
    regulatorAuthoritiesService = {
      deleteRegulatorUserByCompetentAuthorityUsingDELETE: jest.fn().mockReturnValue(of(null)),
      deleteCurrentRegulatorUserByCompetentAuthorityUsingDELETE: jest.fn().mockReturnValue(of(null)),
    };

    await TestBed.configureTestingModule({
      declarations: [DeleteComponent, RouterStubComponent],
      imports: [
        RouterTestingModule.withRoutes([{ path: 'user/regulators', component: RouterStubComponent }]),
        SharedModule,
        ConcurrencyTestingModule,
      ],
      providers: [
        { provide: RegulatorAuthoritiesService, useValue: regulatorAuthoritiesService },
        { provide: AuthService, useValue: authService },
        { provide: ActivatedRoute, useValue: activatedRoute },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should contain a heading with regulator name', () => {
    expect(page.header.textContent).toContain(`${user.firstName} ${user.lastName}`);
  });

  it('should contain cancel and delete buttons', () => {
    expect(page.cancelLink.textContent.trim()).toEqual('Cancel');
    expect(page.submitButton.textContent.trim()).toEqual('Confirm delete');
  });

  it('should return without reload on cancel click', () => {
    page.cancelLink.click();

    expectToHaveNavigatedTo('/user/regulators#regulator-users');
  });

  it('should delete regulator and logout if current user', () => {
    authService.userStatus.next({ userId: '1reg' });

    page.submitButton.click();

    expect(regulatorAuthoritiesService.deleteCurrentRegulatorUserByCompetentAuthorityUsingDELETE).toHaveBeenCalled();
    expect(regulatorAuthoritiesService.deleteRegulatorUserByCompetentAuthorityUsingDELETE).not.toHaveBeenCalled();
    expect(authService.logout).toHaveBeenCalled();
  });

  it('should delete regulator on confirm delete click', () => {
    authService.userStatus.next({ userId: '1' });

    page.submitButton.click();

    expect(
      regulatorAuthoritiesService.deleteCurrentRegulatorUserByCompetentAuthorityUsingDELETE,
    ).not.toHaveBeenCalled();
    expect(regulatorAuthoritiesService.deleteRegulatorUserByCompetentAuthorityUsingDELETE).toHaveBeenCalledWith('1reg');
  });

  it('should show confirmation screen on delete', () => {
    authService.userStatus.next({ userId: '1' });

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.panelTitle.textContent).toContain(user.firstName);
    expect(page.panelTitle.textContent).toContain(user.lastName);

    page.returnLink.click();

    expectToHaveNavigatedTo('/user/regulators#regulator-users');
  });

  it('should dismiss with a message if error', async () => {
    authService.userStatus.next({ userId: '1' });
    regulatorAuthoritiesService.deleteRegulatorUserByCompetentAuthorityUsingDELETE.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'AUTHORITY1003' }, status: 400 })),
    );

    page.submitButton.click();

    await expectConcurrentErrorToBe(saveNotFoundRegulatorError);
  });
});

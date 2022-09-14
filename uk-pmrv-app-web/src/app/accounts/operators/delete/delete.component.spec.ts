import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, of, throwError } from 'rxjs';

import { OperatorAuthoritiesService, UserStatusDTO } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass, MockType } from '../../../../testing';
import { AuthService } from '../../../core/services/auth.service';
import { ConcurrencyTestingModule, expectConcurrentErrorToBe } from '../../../error/testing/concurrency-error';
import { SharedModule } from '../../../shared/shared.module';
import { operator } from '../../testing/mock-data';
import { saveNotFoundOperatorError } from '../errors/concurrency-error';
import { DeleteComponent } from './delete.component';

describe('DeleteComponent', () => {
  let component: DeleteComponent;
  let fixture: ComponentFixture<DeleteComponent>;
  let page: Page;

  class Page extends BasePage<DeleteComponent> {
    get confirmButton() {
      return this.query<HTMLButtonElement>('button');
    }

    get link() {
      return this.query<HTMLAnchorElement>('a');
    }
  }

  const authService: MockType<AuthService> = {
    userStatus: new BehaviorSubject<UserStatusDTO>({
      loginStatus: 'ENABLED',
      userId: 'test1',
    }),
    loadUserStatus: jest.fn(),
  };
  const operatorAuthoritiesService = mockClass(OperatorAuthoritiesService);
  const route = new ActivatedRouteStub({ accountId: '123', userId: 'test1' }, undefined, {
    user: { ...operator, userId: 'test1' },
  });

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DeleteComponent],
      imports: [RouterTestingModule, ConcurrencyTestingModule, SharedModule],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: OperatorAuthoritiesService, useValue: operatorAuthoritiesService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  afterEach(() => jest.clearAllMocks());

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should delete self and point to dashboard', () => {
    operatorAuthoritiesService.deleteCurrentUserAccountOperatorAuthorityUsingDELETE.mockReturnValueOnce(of(null));
    authService.loadUserStatus.mockReturnValueOnce(of(null));
    page.confirmButton.click();
    fixture.detectChanges();

    expect(page.link.textContent.trim()).toEqual('Go to my dashboard');
    expect(page.link.href).toMatch(/\/dashboard$/);
  });

  it('should delete self and point to welcome page', () => {
    operatorAuthoritiesService.deleteCurrentUserAccountOperatorAuthorityUsingDELETE.mockReturnValueOnce(of(null));
    authService.loadUserStatus.mockReturnValueOnce(of(null));
    authService.userStatus.next({ loginStatus: 'NO_AUTHORITY', userId: 'test1' });
    page.confirmButton.click();
    fixture.detectChanges();

    expect(page.link.textContent.trim()).toEqual('Go to PMRV welcome page');
    expect(page.link.href).not.toMatch(/\/dashboard$/);
  });

  it('should delete other user and point back to the list', () => {
    operatorAuthoritiesService.deleteCurrentUserAccountOperatorAuthorityUsingDELETE.mockReturnValueOnce(of(null));
    operatorAuthoritiesService.deleteAccountOperatorAuthorityUsingDELETE.mockReturnValueOnce(of(null));
    authService.loadUserStatus.mockReturnValueOnce(of(null));
    authService.userStatus.next({ loginStatus: 'NO_AUTHORITY', userId: 'test2' });
    page.confirmButton.click();
    fixture.detectChanges();

    expect(page.link.textContent.trim()).toEqual(`Return to the users, contacts and verifiers page`);
    expect(page.link.href).not.toMatch(/\/dashboard$/);
  });

  it('should navigate to save error when deleting an already deleted user', async () => {
    operatorAuthoritiesService.deleteCurrentUserAccountOperatorAuthorityUsingDELETE.mockReturnValueOnce(of(null));
    operatorAuthoritiesService.deleteAccountOperatorAuthorityUsingDELETE.mockReturnValueOnce(
      throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'AUTHORITY1004' } })),
    );
    authService.loadUserStatus.mockReturnValueOnce(of(null));
    authService.userStatus.next({ loginStatus: 'NO_AUTHORITY', userId: 'test2' });
    page.confirmButton.click();
    fixture.detectChanges();

    await expectConcurrentErrorToBe(saveNotFoundOperatorError(123));
  });
});

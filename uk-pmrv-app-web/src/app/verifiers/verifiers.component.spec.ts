import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, of, throwError } from 'rxjs';

import { cloneDeep } from 'lodash-es';

import { UserStatusDTO, VBSiteContactsService, VerifierAuthoritiesService } from 'pmrv-api';

import { ActivatedRouteStub, asyncData, BasePage, changeInputValue, MockType } from '../../testing';
import { AuthService } from '../core/services/auth.service';
import { ErrorCode } from '../error/business-errors';
import { ConcurrencyTestingModule, expectConcurrentErrorToBe } from '../error/testing/concurrency-error';
import { SharedModule } from '../shared/shared.module';
import { SharedUserModule } from '../shared-user/shared-user.module';
import { savePartiallyNotFoundSiteContactsError, savePartiallyNotFoundVerifierError } from './errors/concurrency-error';
import { SiteContactsComponent } from './site-contacts/site-contacts.component';
import { mockAccountContactVbInfoResponse, mockVerifiersRouteData } from './testing/mock-data';
import { VerifiersComponent } from './verifiers.component';

describe('VerifiersComponent', () => {
  let component: VerifiersComponent;
  let fixture: ComponentFixture<VerifiersComponent>;
  let router: Router;
  let route: ActivatedRoute;
  let page: Page;

  class Page extends BasePage<VerifiersComponent> {
    get rows() {
      return this.queryAll<HTMLTableRowElement>('form[id="verifiers"] tbody tr');
    }
    get saveBtn() {
      return this.query<HTMLButtonElement>('form[id="verifiers"] button[type="submit"]');
    }
    get discardChangesBtn() {
      return this.queryAll<HTMLButtonElement>('form[id="verifiers"] button[type="button"]')[1];
    }
    get addVerifierSelect() {
      return this.query<HTMLSelectElement>('form[id="add-verifier"] select');
    }
    get addVerifierSelectOptions() {
      return Array.from(this.addVerifierSelect.options).map((option: HTMLOptionElement) => ({
        text: option.textContent.trim(),
        value: option.value,
      }));
    }
    get authorityStatuses() {
      return this.rows.map((row) => row.querySelector<HTMLSelectElement>('select[name$=".authorityStatus"]'));
    }
    get authorityStatusValues() {
      return this.authorityStatuses.map((select) => (select ? this.getInputValue(select) : null));
    }
    set authorityStatusValues(value: string[]) {
      this.authorityStatuses.forEach((select, index) => {
        if (select && value[index] !== undefined) {
          this.setInputValue(`#${select.id}`, value[index]);
        }
      });
    }
    get addVerifierContinue() {
      return this.query<HTMLButtonElement>('form[id="add-verifier"] button[type="submit"]');
    }
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
  }

  const activatedRouteStub = new ActivatedRouteStub(null, null, { ...mockVerifiersRouteData });
  const authService: Partial<jest.Mocked<AuthService>> = {
    userStatus: new BehaviorSubject<UserStatusDTO>({
      loginStatus: 'ENABLED',
      roleType: 'VERIFIER',
      userId: 'verifierId',
    }),
  };
  const verifierAuthoritiesService: MockType<VerifierAuthoritiesService> = {
    getVerifierAuthoritiesUsingGET: jest.fn().mockReturnValue(asyncData(cloneDeep(mockVerifiersRouteData.verifiers))),
    updateVerifierAuthoritiesUsingPOST: jest.fn().mockReturnValue(of(null)),
  };
  const vbSiteContactsService: MockType<VBSiteContactsService> = {
    updateVbSiteContactsUsingPOST: jest.fn().mockReturnValue(of(null)),
    getVbSiteContactsUsingGET: jest.fn().mockReturnValue(of(mockAccountContactVbInfoResponse)),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConcurrencyTestingModule, SharedModule, SharedUserModule, RouterTestingModule],
      declarations: [VerifiersComponent, SiteContactsComponent],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: AuthService, useValue: authService },
        { provide: VerifierAuthoritiesService, useValue: verifierAuthoritiesService },
        { provide: VBSiteContactsService, useValue: vbSiteContactsService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VerifiersComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the add new user button and select for admin', () => {
    expect(page.addVerifierContinue).toBeTruthy();
    expect(page.addVerifierContinue.innerHTML.trim()).toEqual('Continue');
    expect(page.addVerifierSelect).toBeTruthy();

    expect(page.addVerifierSelectOptions).toEqual([
      { text: 'Verifier admin', value: '0: verifier_admin' },
      { text: 'Verifier', value: '1: verifier' },
    ]);
  });

  it('should navigate to add new user on button click', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    page.addVerifierContinue.click();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledWith(['add'], { queryParams: { roleCode: 'verifier' }, relativeTo: route });
  });

  it('should display a list of verifiers', () => {
    expect(page.rows).toHaveLength(4);
  });

  it('should render a save and a discard changes button', () => {
    expect(page.saveBtn).toBeTruthy();
    expect(page.saveBtn.innerHTML.trim()).toEqual('Save');

    expect(page.discardChangesBtn).toBeTruthy();
    expect(page.discardChangesBtn.innerHTML.trim()).toEqual('Discard changes');
  });

  it('should not accept submission without at least one active verifier admin', () => {
    page.authorityStatusValues = [undefined, undefined, 'DISABLED'];
    fixture.detectChanges();

    page.saveBtn.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();

    page.authorityStatusValues = [undefined, undefined, 'ACTIVE'];
    page.saveBtn.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
  });

  it('should update verifier status and refresh lists', async () => {
    changeInputValue(fixture, '#verifiersArray\\.1\\.authorityStatus', 'ACTIVE');
    fixture.detectChanges();

    expect(verifierAuthoritiesService.updateVerifierAuthoritiesUsingPOST).not.toHaveBeenCalled;

    page.saveBtn.click();
    fixture.detectChanges();

    expect(verifierAuthoritiesService.updateVerifierAuthoritiesUsingPOST).toHaveBeenCalledTimes(1);
  });

  it('should post only changed values on save', () => {
    page.authorityStatusValues = [undefined, 'ACTIVE'];
    fixture.detectChanges();

    page.saveBtn.click();
    fixture.detectChanges();

    expect(verifierAuthoritiesService.updateVerifierAuthoritiesUsingPOST).toHaveBeenCalledWith([
      {
        authorityStatus: 'ACTIVE',
        roleCode: 'verifier',
        userId: '1reg',
      },
    ]);
  });

  it('should show an error message when updated user does not exist', async () => {
    verifierAuthoritiesService.updateVerifierAuthoritiesUsingPOST.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'AUTHORITY1006' }, status: 400 })),
    );

    changeInputValue(fixture, '#verifiersArray\\.1\\.authorityStatus', 'DISABLED');
    fixture.detectChanges();

    page.saveBtn.click();
    fixture.detectChanges();

    await expectConcurrentErrorToBe(savePartiallyNotFoundVerifierError);
  });

  it('should save site contacts', () => {
    const mockSiteContacts = [
      { accountId: 1, userId: null },
      { accountId: 2, userId: '4reg' },
      { accountId: 3, userId: null },
    ];

    component.saveSiteContacts(mockSiteContacts);

    expect(vbSiteContactsService.updateVbSiteContactsUsingPOST).toHaveBeenCalledWith(mockSiteContacts);
  });

  it('should display cannot save error page if the body is no longer assigned to the account', () => {
    const mockSiteContacts = [
      { accountId: 1, userId: null },
      { accountId: 2, userId: '4reg' },
      { accountId: 3, userId: null },
    ];
    vbSiteContactsService.updateVbSiteContactsUsingPOST.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            error: { code: ErrorCode.ACCOUNT1005 },
            status: 400,
          }),
      ),
    );

    component.saveSiteContacts(mockSiteContacts);

    expectConcurrentErrorToBe(savePartiallyNotFoundSiteContactsError);
  });

  it('should display cannot save error page if the user is no longer a verifier', () => {
    const mockSiteContacts = [
      { accountId: 1, userId: null },
      { accountId: 2, userId: '4reg' },
      { accountId: 3, userId: null },
    ];
    vbSiteContactsService.updateVbSiteContactsUsingPOST.mockReturnValue(
      throwError(
        () =>
          new HttpErrorResponse({
            error: { code: ErrorCode.AUTHORITY1006 },
            status: 400,
          }),
      ),
    );

    component.saveSiteContacts(mockSiteContacts);

    expectConcurrentErrorToBe(savePartiallyNotFoundSiteContactsError);
  });
});

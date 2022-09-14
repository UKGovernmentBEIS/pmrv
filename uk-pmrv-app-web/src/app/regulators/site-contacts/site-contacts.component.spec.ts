import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { throwError } from 'rxjs';

import {
  AccountContactInfoResponse,
  CaSiteContactsService,
  RegulatorAuthoritiesService,
  RegulatorUsersAuthoritiesInfoDTO,
} from 'pmrv-api';

import { ActivatedRouteStub, asyncData, BasePage, MockType } from '../../../testing';
import { ConcurrencyTestingModule, expectConcurrentErrorToBe } from '../../error/testing/concurrency-error';
import { SharedModule } from '../../shared/shared.module';
import { savePartiallyNotFoundSiteContactError } from '../errors/concurrency-error';
import { SiteContactsComponent } from './site-contacts.component';

describe('SiteContactsComponent', () => {
  let component: SiteContactsComponent;
  let fixture: ComponentFixture<SiteContactsComponent>;
  let page: Page;
  let activatedRoute: ActivatedRouteStub;

  class Page extends BasePage<SiteContactsComponent> {
    get accounts() {
      return this.queryAll<HTMLTableHeaderCellElement>('tbody > tr > th');
    }

    get types() {
      return this.queryAll<HTMLTableCellElement>('tbody > tr > td').filter((_, index) => index % 2 === 0);
    }

    get assignees() {
      return this.queryAll<HTMLTableCellElement>('tbody > tr > td').filter((_, index) => index % 2 === 1);
    }

    get assigneeSelects() {
      return this.queryAll<HTMLSelectElement>('tbody select');
    }

    get assigneeSelectValues() {
      return this.assigneeSelects.map((select) => page.getInputValue(`#${select.id}`));
    }

    set assigneeSelectValues(values: string[]) {
      this.assigneeSelects.forEach((select, index) => this.setInputValue(`#${select.id}`, values[index]));
    }

    get currentPage() {
      return this.query<HTMLLIElement>('.hmcts-pagination__item--active');
    }

    get saveButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorList() {
      return this.queryAll<HTMLLIElement>('.govuk-error-summary__list li');
    }
  }

  const regulators: RegulatorUsersAuthoritiesInfoDTO = {
    caUsers: [
      {
        userId: 'ax6asd',
        authorityCreationDate: '2021-02-16T14:03:01.000Z',
        authorityStatus: 'ACTIVE',
        firstName: 'Bob',
        lastName: 'Squarepants',
        jobTitle: 'Swimmer',
        locked: false,
      },
      {
        userId: 'bsdfg3',
        authorityCreationDate: '2021-02-16T12:03:01.000Z',
        authorityStatus: 'ACTIVE',
        firstName: 'Patrick',
        lastName: 'Star',
        jobTitle: 'Funny guy',
        locked: false,
      },
      {
        userId: 'bGFDFG',
        authorityCreationDate: '2021-02-13T11:33:01.000Z',
        authorityStatus: 'PENDING',
        firstName: 'Tes',
        lastName: 'Locke',
        jobTitle: 'Officer',
        locked: false,
      },
    ],
    editable: true,
  };
  const siteContacts: AccountContactInfoResponse = {
    contacts: [
      { accountName: 'Test facility', accountId: 1, userId: regulators.caUsers[0].userId },
      { accountName: 'Dev facility', accountId: 2 },
    ],
    editable: true,
    totalItems: 2,
  };

  const siteContactsService: jest.Mocked<Partial<CaSiteContactsService>> = {
    getCaSiteContactsUsingGET: jest.fn().mockReturnValue(asyncData(siteContacts)),
    updateCaSiteContactsUsingPOST: jest.fn().mockReturnValue(asyncData(null)),
  };

  const regulatorAuthoritiesService: MockType<RegulatorAuthoritiesService> = {
    getCaRegulatorsUsingGET: jest.fn().mockReturnValue(asyncData(regulators)),
  };

  beforeEach(async () => {
    activatedRoute = new ActivatedRouteStub();

    await TestBed.configureTestingModule({
      imports: [ReactiveFormsModule, SharedModule, RouterTestingModule, ConcurrencyTestingModule],
      declarations: [SiteContactsComponent],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: CaSiteContactsService, useValue: siteContactsService },
        { provide: RegulatorAuthoritiesService, useValue: regulatorAuthoritiesService },
      ],
    }).compileComponents();
  });

  const createComponent = async () => {
    fixture = TestBed.createComponent(SiteContactsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(createComponent);

  afterEach(() => jest.clearAllMocks());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have called data services only if tab is active', () => {
    expect(siteContactsService.getCaSiteContactsUsingGET).not.toHaveBeenCalled();

    activatedRoute.setFragment('site-contacts');

    expect(siteContactsService.getCaSiteContactsUsingGET).toHaveBeenCalledTimes(1);
    expect(siteContactsService.getCaSiteContactsUsingGET).toHaveBeenCalledWith(0, 50);
  });

  it('should display the list of accounts with their assignees', async () => {
    activatedRoute.setFragment('site-contacts');
    await fixture.whenStable();
    fixture.detectChanges();

    expect(page.accounts.map((header) => header.textContent)).toEqual(['Dev facility', 'Test facility']);
    expect(page.types.map((cell) => cell.textContent).every((text) => text === 'Installation')).toBeTruthy();
    expect(page.assigneeSelectValues).toEqual(['0: null', '1: ax6asd']);
    expect(page.assigneeSelects.map((select) => select.selectedOptions[0].textContent.trim())).toEqual([
      'Unassigned',
      'Bob Squarepants',
    ]);
    expect(page.currentPage.textContent).toEqual('1');
  });

  it('should save the updated assignees', async () => {
    activatedRoute.setFragment('site-contacts');
    await fixture.whenStable();
    fixture.detectChanges();

    page.assigneeSelectValues = ['bsdfg3', null];
    fixture.detectChanges();

    page.saveButton.click();
    fixture.detectChanges();

    expect(siteContactsService.updateCaSiteContactsUsingPOST).toHaveBeenCalledWith([
      { accountId: 2, userId: 'bsdfg3' },
      { accountId: 1, userId: null },
    ]);
  });

  it('should show error page in case the authority has been deleted meanwhile', async () => {
    siteContactsService.updateCaSiteContactsUsingPOST.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'AUTHORITY1003' }, status: 400 })),
    );
    activatedRoute.setFragment('site-contacts');

    await fixture.whenStable();
    fixture.detectChanges();

    page.assigneeSelectValues = ['bsdfg3', null];
    fixture.detectChanges();
    page.saveButton.click();

    fixture.detectChanges();

    await expectConcurrentErrorToBe(savePartiallyNotFoundSiteContactError);
  });

  it('should show error page in case the user has been deleted meanwhile', async () => {
    siteContactsService.updateCaSiteContactsUsingPOST.mockReturnValue(
      throwError(() => new HttpErrorResponse({ error: { code: 'ACCOUNT1004' }, status: 400 })),
    );
    activatedRoute.setFragment('site-contacts');

    await fixture.whenStable();
    fixture.detectChanges();

    page.assigneeSelectValues = ['bsdfg3', null];
    fixture.detectChanges();
    page.saveButton.click();

    fixture.detectChanges();

    await expectConcurrentErrorToBe(savePartiallyNotFoundSiteContactError);
  });

  it('should display assignees as plain text if the user does not have permissions', async () => {
    siteContactsService.getCaSiteContactsUsingGET.mockReturnValueOnce(asyncData({ ...siteContacts, editable: false }));
    activatedRoute.setFragment('site-contacts');

    await fixture.whenStable();
    fixture.detectChanges();

    expect(page.assignees.map((assignee) => assignee.textContent)).toEqual(['Unassigned', 'Bob Squarepants']);
  });

  it('should display only active regulators', async () => {
    activatedRoute.setFragment('site-contacts');

    await fixture.whenStable();
    fixture.detectChanges();

    expect(Array.from(page.assigneeSelects[0].options).map((option) => option.textContent.trim())).toEqual([
      'Unassigned',
      'Bob Squarepants',
      'Patrick Star',
    ]);
  });
});

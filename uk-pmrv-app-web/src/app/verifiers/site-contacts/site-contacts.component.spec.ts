import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../testing';
import { SharedModule } from '../../shared/shared.module';
import { mockAccountContactVbInfoResponse, mockVerifiersRouteData } from '../testing/mock-data';
import { SiteContactsComponent } from './site-contacts.component';

describe('SiteContactsComponent', () => {
  let hostComponent: TestComponent;
  let component: SiteContactsComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  class Page extends BasePage<TestComponent> {
    get accounts() {
      return this.queryAll<HTMLTableCellElement>('tbody > tr > th');
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

  @Component({
    template: `
      <app-site-contacts
        [pageSize]="pageSize"
        [contacts]="contacts"
        [verifiers]="verifiers"
        (siteContactChange)="siteContactChange($event)"
      ></app-site-contacts>
    `,
  })
  class TestComponent {
    contacts = mockAccountContactVbInfoResponse;
    verifiers = mockVerifiersRouteData.verifiers.authorities;
    pageSize = 50;
    siteContactChange = jest.fn();
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [SiteContactsComponent, TestComponent],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(SiteContactsComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  afterEach(() => jest.clearAllMocks());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the list of accounts with their assignees', () => {
    expect(page.accounts.map((header) => header.textContent)).toEqual(['Account 1', 'Account 2', 'Account 3']);
    expect(page.types.map((cell) => cell.textContent).every((text) => text === 'UK ETS Installation')).toBeTruthy();
    expect(page.assigneeSelectValues).toEqual(['1: 2reg', '0: null', '0: null']);
    expect(page.assigneeSelects.map((select) => select.selectedOptions[0].textContent.trim())).toEqual([
      'Therion Path',
      'Unassigned',
      'Unassigned',
    ]);
    expect(page.currentPage.textContent).toEqual('1');
  });

  it('should submit the updated assignees', () => {
    page.assigneeSelectValues = [null, '4reg', null];
    fixture.detectChanges();

    page.saveButton.click();
    fixture.detectChanges();

    expect(hostComponent.siteContactChange).toHaveBeenCalledWith([
      { accountId: 1, userId: null },
      { accountId: 2, userId: '4reg' },
      { accountId: 3, userId: null },
    ]);
  });

  it('should display assignees as plain text if the user is not verifier admin', () => {
    const contacts = { ...mockAccountContactVbInfoResponse };
    contacts.editable = false;
    hostComponent.contacts = contacts;
    fixture.detectChanges();

    expect(page.assignees.map((assignee) => assignee.textContent)).toEqual([
      'Therion Path',
      'Unassigned',
      'Unassigned',
    ]);
  });

  it('should display only active regulators', () => {
    expect(Array.from(page.assigneeSelects[0].options).map((option) => option.textContent.trim())).toEqual([
      'Unassigned',
      'Therion Path',
      'Tyrion Lanister',
    ]);
  });
});

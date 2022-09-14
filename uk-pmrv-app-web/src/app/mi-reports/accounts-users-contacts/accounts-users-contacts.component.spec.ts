import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { asyncData, BasePage } from '@testing';

import { MiReportsService } from 'pmrv-api';

import { mockAccountsUsersContactsMiReportResult } from '../testing/mock-data';
import { AccountsUsersContactsComponent } from './accounts-users-contacts.component';

class Page extends BasePage<AccountsUsersContactsComponent> {
  get table() {
    return this.query<HTMLDivElement>('.govuk-table');
  }

  get noResults() {
    return this.query<HTMLDivElement>('.noResults p');
  }

  get executeButton() {
    return this.query<HTMLButtonElement>('button');
  }
}

describe('AccountsUsersContactsComponent', () => {
  let component: AccountsUsersContactsComponent;
  let fixture: ComponentFixture<AccountsUsersContactsComponent>;
  let page: Page;
  let miReportsService: Partial<jest.Mocked<MiReportsService>>;

  beforeEach(async () => {
    miReportsService = {
      generateReportUsingPOST: jest.fn().mockReturnValue(asyncData(mockAccountsUsersContactsMiReportResult)),
    };

    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [AccountsUsersContactsComponent],
      providers: [{ provide: MiReportsService, useValue: miReportsService }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AccountsUsersContactsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render table rows', () => {
    expect(page.noResults.textContent.trim()).toEqual('There are no results to show');
    page.executeButton.click();
    fixture.detectChanges();
    const cells = Array.from(page.table.querySelectorAll('td'));

    expect(cells.map((cell) => cell.textContent.trim())).toEqual([
      ...[
        'Installation',
        '1',
        'Installation name',
        'New',
        '',
        '',
        'Legal entity',
        'true',
        'false',
        'true',
        'true',
        'ACTIVE',
        'Obi Wan Kenobi',
        '+442345254566656565',
        '',
        'owk@mail.com',
        'Operator admin',
      ],
      ...[
        'Installation',
        '31',
        'Installation name 2',
        'Live',
        '',
        '',
        'Legal entity 2',
        'true',
        'false',
        'true',
        'false',
        'ACTIVE',
        'Darth Vader',
        '+442345254566656562',
        '',
        'dv@mail.gr',
        'Operator admin',
      ],
    ]);
  });
});

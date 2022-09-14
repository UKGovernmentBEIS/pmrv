import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { DocumentTemplatesService, NotificationTemplatesService, TemplateSearchResults } from 'pmrv-api';

import { BasePage, mockClass } from '../../testing';
import { SharedModule } from '../shared/shared.module';
import { OperatorTypePipe } from './operator-type.pipe';
import { TemplatesComponent } from './templates.component';

const operatorEmails: TemplateSearchResults = {
  templates: [
    {
      id: 1,
      name: 'Permit Rejected',
      operatorType: 'INSTALLATION',
      workflow: 'Permit Application',
      lastUpdatedDate: '2022-01-11T13:22:58.760561Z',
    },
    {
      id: 2,
      name: 'Batch RFI',
      operatorType: 'AVIATION',
      workflow: 'Permit Application',
      lastUpdatedDate: '2022-01-12T15:22:58.760561Z',
    },
  ],
  total: 2,
};

const regulatorEmails: TemplateSearchResults = {
  templates: [
    {
      id: 3,
      name: 'Permit Accepted',
      operatorType: 'INSTALLATION',
      workflow: 'Permit Application',
      lastUpdatedDate: '2022-01-11T13:22:58.760561Z',
    },
    {
      id: 4,
      name: 'Account Created',
      operatorType: 'INSTALLATION',
      workflow: 'Installation Account Opening',
      lastUpdatedDate: '2022-01-12T15:22:58.760561Z',
    },
  ],
  total: 2,
};

const operatorDocuments: TemplateSearchResults = {
  templates: [
    {
      id: 5,
      name: 'Permit Refused Document',
      operatorType: 'INSTALLATION',
      workflow: 'Permit Application',
      lastUpdatedDate: '2022-01-21T13:32:58.760561Z',
    },
  ],
  total: 1,
};

describe('TemplatesComponent', () => {
  let component: TemplatesComponent;
  let fixture: ComponentFixture<TemplatesComponent>;
  let hostElement: HTMLElement;
  let page: Page;
  const notificationTemplatesService = mockClass(NotificationTemplatesService);
  const documentTemplatesService = mockClass(DocumentTemplatesService);

  class Page extends BasePage<TemplatesComponent> {
    get regulatorEmailsTabLink() {
      return this.query<HTMLAnchorElement>('#tab_regulator-emails');
    }

    get operatorDocumentsTabLink() {
      return this.query<HTMLAnchorElement>('#tab_operator-documents');
    }

    get operatorEmailsTab() {
      return this.query<HTMLDivElement>('#operator-emails');
    }

    get regulatorEmailsTab() {
      return this.query<HTMLDivElement>('#regulator-emails');
    }

    get operatorDocumentsTab() {
      return this.query<HTMLDivElement>('#operator-documents');
    }

    get termValue() {
      return this.getInputValue('#term');
    }

    set termValue(value: string) {
      this.setInputValue('#term', value);
    }

    get searchButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get formErrorMessage() {
      return this.query<HTMLElement>('div[formcontrolname="term"] span.govuk-error-message');
    }
  }

  const createComponent = async () => {
    fixture = TestBed.createComponent(TemplatesComponent);
    component = fixture.componentInstance;
    hostElement = fixture.nativeElement;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  const createModule = async () => {
    await TestBed.configureTestingModule({
      declarations: [TemplatesComponent, OperatorTypePipe],
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: NotificationTemplatesService, useValue: notificationTemplatesService },
        { provide: DocumentTemplatesService, useValue: documentTemplatesService },
      ],
    }).compileComponents();
  };

  describe('for empty results', () => {
    beforeEach(async () => {
      notificationTemplatesService.getCurrentUserNotificationTemplatesUsingGET.mockReturnValue(
        of({
          templates: [],
          total: 0,
        }),
      );
      documentTemplatesService.getCurrentUserDocumentTemplatesUsingGET.mockReturnValue(
        of({
          templates: [],
          total: 0,
        }),
      );
    });

    beforeEach(createModule);
    beforeEach(createComponent);

    it('should render no results found', async () => {
      fixture.detectChanges();

      expect(hostElement.textContent).toContain('There are no results to show');

      expect(notificationTemplatesService.getCurrentUserNotificationTemplatesUsingGET).toHaveBeenLastCalledWith(
        'OPERATOR',
        0,
        30,
        null,
      );
    });
  });

  describe('for non empty results', () => {
    beforeEach(async () => {
      notificationTemplatesService.getCurrentUserNotificationTemplatesUsingGET.mockReturnValue(of(operatorEmails));
      documentTemplatesService.getCurrentUserDocumentTemplatesUsingGET.mockReturnValue(of(operatorDocuments));
    });
    beforeEach(createModule);
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should render operator emails', async () => {
      fixture.detectChanges();
      expect(notificationTemplatesService.getCurrentUserNotificationTemplatesUsingGET).toHaveBeenLastCalledWith(
        'OPERATOR',
        0,
        30,
        null,
      );

      const tableHeaders = Array.from(page.operatorEmailsTab.querySelectorAll('th'));
      const tableData = Array.from(page.operatorEmailsTab.querySelectorAll('td'));
      expect(tableHeaders.map((header) => header.textContent.trim())).toEqual([
        'Template name',
        'Operator',
        'Workflow',
        'Last changed',
      ]);
      expect(tableData.map((data) => data.textContent.trim())).toEqual([
        ...['Permit Rejected', 'Installations', 'Permit Application', '11 Jan 2022'],
        ...['Batch RFI', 'Aviations', 'Permit Application', '12 Jan 2022'],
      ]);

      page.termValue = 'pe';
      page.searchButton.click();
      fixture.detectChanges();
      expect(page.formErrorMessage.textContent.trim()).toContain('Enter at least 3 characters');

      page.termValue = 'bat';
      page.searchButton.click();
      fixture.detectChanges();
      expect(page.formErrorMessage).toBeFalsy();

      expect(notificationTemplatesService.getCurrentUserNotificationTemplatesUsingGET).toHaveBeenLastCalledWith(
        'OPERATOR',
        0,
        30,
        'bat',
      );
    });

    it('should render regulator emails', () => {
      notificationTemplatesService.getCurrentUserNotificationTemplatesUsingGET.mockReturnValue(of(regulatorEmails));
      page.regulatorEmailsTabLink.click();
      fixture.detectChanges();

      expect(notificationTemplatesService.getCurrentUserNotificationTemplatesUsingGET).toHaveBeenLastCalledWith(
        'REGULATOR',
        0,
        30,
        null,
      );

      const tableHeaders = Array.from(page.regulatorEmailsTab.querySelectorAll('th'));
      const tableData = Array.from(page.regulatorEmailsTab.querySelectorAll('td'));
      expect(tableHeaders.map((header) => header.textContent.trim())).toEqual([
        'Template name',
        'Workflow',
        'Last changed',
      ]);
      expect(tableData.map((data) => data.textContent.trim())).toEqual([
        ...['Permit Accepted', 'Permit Application', '11 Jan 2022'],
        ...['Account Created', 'Installation Account Opening', '12 Jan 2022'],
      ]);

      page.termValue = 'ac';
      page.searchButton.click();
      fixture.detectChanges();
      expect(page.formErrorMessage.textContent.trim()).toContain('Enter at least 3 characters');

      page.termValue = 'acc';
      page.searchButton.click();
      fixture.detectChanges();
      expect(page.formErrorMessage).toBeFalsy();

      expect(notificationTemplatesService.getCurrentUserNotificationTemplatesUsingGET).toHaveBeenLastCalledWith(
        'REGULATOR',
        0,
        30,
        'acc',
      );
    });

    it('should render operator documents', () => {
      page.operatorDocumentsTabLink.click();
      fixture.detectChanges();

      expect(documentTemplatesService.getCurrentUserDocumentTemplatesUsingGET).toHaveBeenLastCalledWith(0, 30, null);

      const tableHeaders = Array.from(page.operatorDocumentsTab.querySelectorAll('th'));
      const tableData = Array.from(page.operatorDocumentsTab.querySelectorAll('td'));
      expect(tableHeaders.map((header) => header.textContent.trim())).toEqual([
        'Template name',
        'Operator',
        'Workflow',
        'Last changed',
      ]);
      expect(tableData.map((data) => data.textContent.trim())).toEqual([
        ...['Permit Refused Document', 'Installations', 'Permit Application', '21 Jan 2022'],
      ]);

      page.termValue = 're';
      page.searchButton.click();
      fixture.detectChanges();
      expect(page.formErrorMessage.textContent.trim()).toContain('Enter at least 3 characters');

      page.termValue = 'ref';
      page.searchButton.click();
      fixture.detectChanges();
      expect(page.formErrorMessage).toBeFalsy();

      expect(documentTemplatesService.getCurrentUserDocumentTemplatesUsingGET).toHaveBeenLastCalledWith(0, 30, 'ref');
    });
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { PermitRevocationModule } from '@permit-revocation/permit-revocation.module';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { mockTaskState } from '@permit-revocation/testing/mock-state';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { PermitCessation, RequestActionsService } from 'pmrv-api';

import { CessationComponent } from './cessation.component';

describe('CessationComponent', () => {
  let component: CessationComponent;
  let fixture: ComponentFixture<CessationComponent>;
  let store: PermitRevocationStore;
  let page: Page;

  const requestActionsService = mockClass(RequestActionsService);
  requestActionsService.getRequestActionsByRequestIdUsingGET.mockReturnValue(
    of([
      {
        id: 13,
        type: 'PERMIT_REVOCATION_APPLICATION_SUBMITTED',
        creationDate: '2022-04-21T14:52:46.363672Z',
      },
    ]),
  );
  let route: ActivatedRouteStub;

  const authService = mockClass(AuthService);
  class Page extends BasePage<CessationComponent> {
    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('ul > li.app-task-list__item'));
    }

    get sectionAnchorNames(): HTMLAnchorElement[] {
      return this.sections.map((section) => section.querySelector<HTMLAnchorElement>('a'));
    }

    get sectionStatuses(): HTMLElement[] {
      return this.sections.map((section) => section.querySelector<HTMLElement>('.app-task-list__tag'));
    }

    get timelineItems() {
      return Array.from(this.queryAll<HTMLElement>('app-timeline-item'));
    }

    get relatedActionsLinks() {
      return this.queryAll<HTMLLinkElement>('aside li a');
    }

    get headingInfo() {
      return this.queryAll<HTMLParagraphElement>('.govuk-heading-xl p');
    }
  }

  beforeEach(async () => {
    route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, null, null, null);

    await TestBed.configureTestingModule({
      declarations: [CessationComponent],
      imports: [RouterTestingModule, SharedModule, PermitRevocationModule],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: ActivatedRoute, useValue: route },
        { provide: RequestActionsService, useValue: requestActionsService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitRevocationStore);
  });

  afterEach(() => jest.clearAllMocks());

  const createComponent = () => {
    fixture = TestBed.createComponent(CessationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  it('should create', () => {
    store.setState(mockTaskState);
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should display not started section when cessation not started yet', () => {
    store.setState({ ...mockTaskState, cessation: null, cessationCompleted: undefined });
    createComponent();
    expect(page.sectionAnchorNames.map((section) => section.textContent.trim())).toEqual([
      'Confirm cessation of regulated activities',
    ]);

    expect(page.sectionStatuses.map((section) => section.textContent.trim())).toEqual(['not started']);
  });

  it('should display in progress section when cessation exists but not completed', () => {
    const cessation: PermitCessation = {
      determinationOutcome: 'APPROVED',
      annualReportableEmissions: 2,
      subsistenceFeeRefunded: true,
      noticeType: 'SATISFIED_WITH_REQUIREMENTS_COMPLIANCE',
      notes: 'asdsad',
    };
    store.setState({ ...mockTaskState, cessation, cessationCompleted: false });
    createComponent();
    expect(page.sectionAnchorNames.map((section) => section.textContent.trim())).toEqual([
      'Confirm cessation of regulated activities',
    ]);

    expect(page.sectionStatuses.map((section) => section.textContent.trim())).toEqual(['in progress']);
  });

  it('should display completed section when cessation exists and completed', () => {
    store.setState({ ...mockTaskState, cessationCompleted: true });
    createComponent();
    expect(page.sectionAnchorNames.map((section) => section.textContent.trim())).toEqual([
      'Confirm cessation of regulated activities',
    ]);

    expect(page.sectionStatuses.map((section) => section.textContent.trim())).toEqual(['completed']);
  });

  it('should display timeline items', () => {
    store.setState(mockTaskState);
    createComponent();
    expect(page.timelineItems.length).toEqual(1);
  });

  it('should display related action links', () => {
    expect(page.relatedActionsLinks.map((el) => [el.href, el.textContent])).toEqual([
      ['http://localhost/tasks/13/change-assignee', 'Reassign task']
    ]);
  });

  it('should show heading info', () => {
    expect(page.headingInfo.map((el) => el.textContent.trim())).toEqual([
      'Assigned to: John Doe',
    ]);
  });
});

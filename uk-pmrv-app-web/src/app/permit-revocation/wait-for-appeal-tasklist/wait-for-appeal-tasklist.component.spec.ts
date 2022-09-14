import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { CessationModule } from '@permit-revocation/cessation/cessation.module';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { RequestActionsService } from 'pmrv-api';

import { PermitRevocationModule } from '../permit-revocation.module';
import { PermitRevocationStore } from '../store/permit-revocation-store';
import { mockTaskState } from '../testing/mock-state';
import { WaitForAppealTasklistComponent } from './wait-for-appeal-tasklist.component';

describe('Wait For Appeal Component', () => {
  let component: WaitForAppealTasklistComponent;
  let fixture: ComponentFixture<WaitForAppealTasklistComponent>;
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
  class Page extends BasePage<WaitForAppealTasklistComponent> {
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
      imports: [RouterTestingModule, PermitRevocationModule, CessationModule],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: ActivatedRoute, useValue: route },
        { provide: RequestActionsService, useValue: requestActionsService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitRevocationStore);
    store.setState(mockTaskState);
  });

  afterEach(() => jest.clearAllMocks());

  const createComponent = () => {
    fixture = TestBed.createComponent(WaitForAppealTasklistComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the section when revocation withdraw is not initiated yet', () => {
    store.setState({
      ...store.getState(),
      reason: null,
    });
    fixture.detectChanges();

    expect(page.sectionAnchorNames.map((section) => section.textContent)).toEqual([
      'Reason for withdrawing the revocation',
    ]);

    expect(page.sectionStatuses.map((section) => section.textContent.trim())).toEqual(['not started']);
  });

  it('should display timeline actions', () => {
    store.setState(mockTaskState);
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

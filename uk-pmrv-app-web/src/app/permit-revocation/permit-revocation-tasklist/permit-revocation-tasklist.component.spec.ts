import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { CessationModule } from '@permit-revocation/cessation/cessation.module';
import { ActivatedRouteStub, BasePage, mockClass, MockType } from '@testing';
import moment from 'moment';

import { AssigneeUserInfoDTO, TasksAssignmentService } from 'pmrv-api';

import { PermitRevocationModule } from '../permit-revocation.module';
import { PermitRevocationStore } from '../store/permit-revocation-store';
import { mockTaskState } from '../testing/mock-state';
import { PermitRevocationTasklistComponent } from './permit-revocation-tasklist.component';

describe('PermitRevocationTaskListComponent', () => {
  let component: PermitRevocationTasklistComponent;
  let fixture: ComponentFixture<PermitRevocationTasklistComponent>;
  let store: PermitRevocationStore;
  let page: Page;

  let tasksAssignmentService: MockType<TasksAssignmentService>;
  let route: ActivatedRouteStub;
  const authService = mockClass(AuthService);

  class Page extends BasePage<PermitRevocationTasklistComponent> {
    get actionButtons() {
      return Array.from(this.queryAll<HTMLButtonElement>('.govuk-button-group button'));
    }
    get sections(): HTMLUListElement[] {
      return Array.from(this.queryAll<HTMLUListElement>('ul > li.app-task-list__item'));
    }

    get sectionAnchorNames(): HTMLAnchorElement[] {
      return this.sections.map((section) => section.querySelector<HTMLAnchorElement>('a'));
    }

    get sectionStatuses(): HTMLElement[] {
      return this.sections.map((section) => section.querySelector<HTMLElement>('.app-task-list__tag'));
    }

    get relatedActionsLinks() {
      return this.queryAll<HTMLLinkElement>('aside li a');
    }

    get headingInfo() {
      return this.queryAll<HTMLParagraphElement>('.govuk-heading-xl p');
    }
  }

  const mockActivatedRouteParams = {
    taskId: mockTaskState.requestTaskId,
  };

  const candidateAssignees: Array<AssigneeUserInfoDTO> = [
    {
      firstName: 'John',
      lastName: 'Doe',
      id: '100',
    },
    {
      firstName: 'Mary',
      lastName: 'Doe',
      id: '50',
    },
  ];

  function createComponent() {
    fixture = TestBed.createComponent(PermitRevocationTasklistComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  }

  beforeEach(async () => {
    route = new ActivatedRouteStub(mockActivatedRouteParams, null, null, null, null);
    tasksAssignmentService = {
      getCandidateAssigneesByTaskIdUsingGET: jest.fn().mockReturnValue(of(candidateAssignees)),
    };
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, PermitRevocationModule, CessationModule],
      providers: [
        { provide: TasksAssignmentService, useValue: tasksAssignmentService },
        { provide: ActivatedRoute, useValue: route },
        { provide: AuthService, useValue: authService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitRevocationStore);
    store.setState(mockTaskState);
  });

  afterEach(() => jest.clearAllMocks());

  beforeEach(() => {
    createComponent();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the section when revocation is not initiated yet', () => {
    store.setState({
      ...store.getState(),
      permitRevocation: null,
      sectionsCompleted: {},
    });
    fixture.detectChanges();

    expect(page.sectionAnchorNames.map((section) => section.textContent)).toEqual(['Reason for revocation']);

    expect(page.sectionStatuses.map((section) => section.textContent.trim())).toEqual(['not started']);
  });

  it('should display notify operator of decision action button', () => {
    const revocationState = store.getState();
    const after28Days = moment().add(28, 'd').set({ hour: 0, minute: 0, second: 0, millisecond: 0 }).utc(true);
    store.setState({
      ...revocationState,
      sectionsCompleted: { REVOCATION_APPLY: true },
      permitRevocation: {
        reason: 'test',
        activitiesStopped: true,
        stoppedDate: '2022-04-20',
        annualEmissionsReportRequired: true,
        annualEmissionsReportDate: '2022-04-22',
        surrenderRequired: false,
        feeCharged: true,
        effectiveDate: after28Days.toISOString(),
        feeDate: after28Days.add(1, 'd').toISOString(),
      },
    });
    createComponent();

    expect(page.actionButtons.map((section) => section.textContent.trim())).toEqual(['Notify Operator of decision']);
  });

  it('should not display notify operator of decision action button', () => {
    store.setState({
      ...store.getState(),
      allowedRequestTaskActions: ['PERMIT_REVOCATION_SAVE_APPLICATION'],
    });

    createComponent();

    expect(page.actionButtons.map((section) => section.textContent.trim())).toEqual([]);
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

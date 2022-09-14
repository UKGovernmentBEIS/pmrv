import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { OperatorAuthoritiesService, TasksAssignmentService, TasksService, UserAuthorityInfoDTO } from 'pmrv-api';

import { ActivatedRouteStub, asyncData, BasePage, MockType } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { RdeModule } from '../../rde.module';
import { NotifyUsersComponent } from './notify-users.component';

describe('NotifyUsersComponent', () => {
  let page: Page;
  let component: NotifyUsersComponent;
  let fixture: ComponentFixture<NotifyUsersComponent>;

  let route: ActivatedRouteStub;
  let router: Router;
  let operatorAuthoritiesService: MockType<OperatorAuthoritiesService>;
  let tasksAssignmentService: MockType<TasksAssignmentService>;
  let tasksService: MockType<TasksService>;

  class Page extends BasePage<NotifyUsersComponent> {
    set assignees(value: string) {
      this.setInputValue('#assignees', value);
    }

    set users(value: string[]) {
      this.setInputValue('#users', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryLinks() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((item) =>
        item.textContent.trim(),
      );
    }
  }

  const mockAssignees = [
    {
      id: '45b2620b-c859-4296-bb58-e49f180f6137',
      firstName: 'Regulator5',
      lastName: 'User',
    },
    {
      id: 'eaa82cc8-0a7d-4f2d-bcf7-f54f612f59e5',
      firstName: 'newreg1',
      lastName: 'User',
    },
    {
      id: '44c7a770-18b2-40e8-85ee-5c92210618d7',
      firstName: 'newreg2',
      lastName: 'User',
    },
  ];

  const mockUsers = {
    authorities: [
      {
        userId: 'cceaad6d-4b09-48bf-9556-77e10f874028',
        firstName: 'newop1',
        lastName: 'User',
        roleName: 'Operator',
        roleCode: 'operator',
        authorityCreationDate: '2021-12-02T12:41:16.752923Z',
        authorityStatus: 'ACTIVE',
        locked: false,
      },
      {
        userId: 'a9f0621d-3097-46f5-b26b-7aeceb8ab146',
        firstName: 'emcon1',
        lastName: 'User',
        roleName: 'Emitter Contact',
        roleCode: 'emitter_contact',
        authorityCreationDate: '2021-12-02T12:52:09.505285Z',
        authorityStatus: 'ACTIVE',
        locked: true,
      },
    ] as UserAuthorityInfoDTO[],
    editable: true,
    contactTypes: {
      FINANCIAL: 'cceaad6d-4b09-48bf-9556-77e10f874028',
      SERVICE: 'cceaad6d-4b09-48bf-9556-77e10f874028',
      PRIMARY: 'a9f0621d-3097-46f5-b26b-7aeceb8ab146',
    },
  };

  beforeEach(async () => {
    tasksAssignmentService = {
      getCandidateAssigneesByTaskIdUsingGET: jest.fn().mockReturnValue(of(mockAssignees)),
    };

    operatorAuthoritiesService = {
      getAccountOperatorAuthoritiesUsingGET: jest.fn().mockReturnValue(asyncData(mockUsers)),
    };

    tasksService = {
      processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of({})),
    };

    route = new ActivatedRouteStub({ taskId: '237', index: '0' });

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, RdeModule],
      providers: [
        { provide: TasksAssignmentService, useValue: tasksAssignmentService },
        { provide: ActivatedRoute, useValue: route },
        { provide: OperatorAuthoritiesService, useValue: operatorAuthoritiesService },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NotifyUsersComponent);
    router = TestBed.inject(Router);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit a valid form and navigate to answers', () => {
    expect(page.errorSummary).toBeFalsy();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryLinks).toEqual(['Select a name to appear on the official notice document.']);

    page.users = [mockUsers.authorities[0].userId];
    page.assignees = `0: ${mockAssignees[0].id}`;
    const navigateSpy = jest.spyOn(router, 'navigate');
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['../answers'], { relativeTo: route });
  });
});

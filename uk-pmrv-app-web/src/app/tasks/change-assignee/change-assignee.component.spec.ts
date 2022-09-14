import { ComponentFixture, TestBed } from "@angular/core/testing";
import { RouterTestingModule } from "@angular/router/testing";

import { BehaviorSubject, of } from "rxjs";

import { AuthService } from "@core/services/auth.service";
import { CommonTasksState } from "@tasks/store/common-tasks.state";
import { CommonTasksStore } from "@tasks/store/common-tasks.store";
import { TasksModule } from "@tasks/tasks.module";
import { BasePage, mockClass } from "@testing";

import { TasksAssignmentService, UserStatusDTO } from "pmrv-api";

import { ChangeAssigneeComponent } from "./change-assignee.component";

describe('ChangeAssigneeComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: ChangeAssigneeComponent;
  let fixture: ComponentFixture<ChangeAssigneeComponent>;

  const authService: Partial<jest.Mocked<AuthService>> = {
    userStatus: new BehaviorSubject<UserStatusDTO>({
      roleType: 'OPERATOR',
    }),
  };
  const tasksAssignmentService = mockClass(TasksAssignmentService);
  tasksAssignmentService.assignTaskUsingPOST.mockReturnValue(of({}));
  tasksAssignmentService.getCandidateAssigneesByTaskIdUsingGET.mockReturnValue(
    of([
      { id: '100', firstName: 'John', lastName: 'Doe' },
    ]),
  );

  class Page extends BasePage<ChangeAssigneeComponent> {
    get select(): HTMLSelectElement {
      return this.query('select');
    }
    get options(): string[] {
      return Array.from(this.select.options).map((option) => option.textContent.trim());
    }

    get confirmationTitle() {
      return this.queryAll<HTMLHeadingElement>('h1');
    }

    get button(): HTMLButtonElement {
      return this.query('button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TasksModule, RouterTestingModule],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: TasksAssignmentService, useValue: tasksAssignmentService }
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState({
      requestTaskItem: {
        requestTask: {
          id: 1,
          assigneeUserId: '100',
          assigneeFullName: 'John Doe',
        },
        userAssignCapable: true,
      },
    } as CommonTasksState);

    fixture = TestBed.createComponent(ChangeAssigneeComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display confirmation', () => {
    expect(page.options).toEqual(['John Doe']);

    page.button.click();
    fixture.detectChanges();

    expect(page.confirmationTitle.map((el) => el.textContent)).toEqual(
      ['The task has been reassigned to'],
    );
  });
});
import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BehaviorSubject, of } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { AuthService } from '@core/services/auth.service';
import { BasePage, mockClass } from '@testing';

import { TasksAssignmentService, TasksReleaseService, UserStatusDTO } from 'pmrv-api';

import { ConcurrencyErrorService } from '../../../error/concurrency-error/concurrency-error.service';
import { SharedModule } from '../../shared.module';

describe('AssignmentComponent', () => {
  let page: Page;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  const authService: Partial<jest.Mocked<AuthService>> = {
    userStatus: new BehaviorSubject<UserStatusDTO>({
      roleType: 'OPERATOR',
    }),
  };
  const tasksAssignmentService = mockClass(TasksAssignmentService);
  tasksAssignmentService.assignTaskUsingPOST.mockReturnValue(of({}));
  tasksAssignmentService.getCandidateAssigneesByTaskIdUsingGET.mockReturnValue(
    of([
      { id: '123', firstName: 'Obi', lastName: 'Wan' },
      { id: '321', firstName: 'Darth', lastName: 'Vader' },
    ]),
  );

  @Component({
    template: `<app-assignment [info]="info" (submitted)="submitted($event)"></app-assignment>`,
    providers: [PendingRequestService],
  })
  class TestComponent {
    info = {
      requestTask: {
        assigneeUserId: '321',
        assignable: true,
        id: 856,
      },
      userAssignCapable: true,
    };
    mockSubmit;

    constructor(private readonly _: PendingRequestService) {}

    submitted(_: any): void {
      this.mockSubmit = _;
    }
  }

  class Page extends BasePage<TestComponent> {
    get select(): HTMLSelectElement {
      return this.query('select');
    }
    get button(): HTMLButtonElement {
      return this.query('button');
    }
    get options(): string[] {
      return Array.from(this.select.options).map((option) => option.textContent.trim());
    }

    get selectValue(): string {
      return this.select.value;
    }
    set selectValue(value: string) {
      this.setInputValue('select', value);
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule],
      providers: [
        { provide: AuthService, useValue: authService },
        { provide: TasksAssignmentService, useValue: tasksAssignmentService },
        { provide: TasksReleaseService, useValue: mockClass(TasksReleaseService) },
        { provide: PendingRequestService, useValue: mockClass(PendingRequestService) },
        { provide: ConcurrencyErrorService, useValue: mockClass(ConcurrencyErrorService) },
      ],
      declarations: [TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(hostComponent).toBeTruthy();
  });

  it('should populate the select', () => {
    expect(page.selectValue).toContain('321');
    expect(page.options).toEqual(['Obi Wan', 'Darth Vader']);
  });

  it('should post assignment and emit submitted', () => {
    const submitSpy = jest.spyOn(hostComponent, 'submitted');

    expect(tasksAssignmentService.assignTaskUsingPOST).toHaveBeenCalledTimes(0);
    expect(submitSpy).toHaveBeenCalledTimes(0);

    page.button.click();

    expect(tasksAssignmentService.assignTaskUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksAssignmentService.assignTaskUsingPOST).toHaveBeenCalledWith({
      taskId: 856,
      userId: '321',
    });
    expect(submitSpy).toHaveBeenCalledTimes(1);
    expect(submitSpy).toHaveBeenCalledWith('Darth Vader');
  });
});

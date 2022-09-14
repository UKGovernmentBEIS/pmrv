import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { RequestTaskActionPayload, TasksService } from 'pmrv-api';

import { RfiModule } from '../rfi.module';
import { RfiStore } from '../store/rfi.store';
import { ResponsesComponent } from './responses.component';

describe('ResponsesComponent', () => {
  let page: Page;
  let store: RfiStore;
  let component: ResponsesComponent;
  let fixture: ComponentFixture<ResponsesComponent>;
  let router: Router;

  const tasksService = mockClass(TasksService);

  const activatedRouteStub = new ActivatedRouteStub({ taskId: '279' });

  class Page extends BasePage<ResponsesComponent> {
    get answers() {
      return this.queryAll<HTMLInputElement>('textarea[id^="pairs"][id$="response"]').map((option) =>
        this.getInputValue(option),
      );
    }

    get confirmButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
    get headingInfo() {
      return this.queryAll<HTMLParagraphElement>('.govuk-heading-xl p');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RfiModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRouteStub },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RfiStore);
    router = TestBed.inject(Router);
    store.setState({
      ...store.getState(),
      daysRemaining: 13,
      assignee: {
        assigneeFullName: 'John Doe',
        assigneeUserId: '100',
      },
      rfiQuestionPayload: {
        ...store.getState().rfiSubmitPayload,
        questions: ['where', 'what'],
      },
      rfiResponsePayload: {
        answers: ['there', 'this'],
        files: ['file'],
      },
    });
    fixture = TestBed.createComponent(ResponsesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the answers', () => {
    expect(page.answers).toEqual(['there', 'this']);
  });

  it('should submit task', () => {
    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.confirmButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'RFI_RESPONSE_SUBMIT',
      requestTaskId: 279,
      requestTaskActionPayload: {
        payloadType: 'RFI_RESPONSE_SUBMIT_PAYLOAD',
        rfiResponsePayload: {
          answers: ['there', 'this'],
          files: ['file'],
        },
      } as RequestTaskActionPayload,
    });
    expect(navigateSpy).toHaveBeenCalledWith(['../respond-confirmation'], {
      relativeTo: TestBed.inject(ActivatedRoute),
    });
  });

  it('should display heading info', () => {
    expect(page.headingInfo.map((el) => el.textContent.trim())).toEqual([
      'Assigned to: John Doe', 'Days Remaining: 13'
    ]);
  });
});

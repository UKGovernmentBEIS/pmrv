import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../../testing';
import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { SharedModule } from '../../shared.module';

describe('DecisionComponent', () => {
  let page: Page;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  const tasksService = mockClass(TasksService);
  tasksService.processRequestTaskActionUsingPOST.mockReturnValue(of({}));

  @Component({
    template: `<app-decision [taskId]="taskId" (submitted)="submitted($event)"></app-decision>`,
    providers: [PendingRequestService],
  })
  class TestComponent {
    taskId = 856;
    mockSubmit;

    constructor(private readonly _: PendingRequestService) {}

    submitted(_: boolean): void {
      this.mockSubmit = _;
    }
  }

  class Page extends BasePage<TestComponent> {
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryErrorList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }
    get button(): HTMLButtonElement {
      return this.query('button');
    }
    get approveDecisionOption() {
      return this.query<HTMLInputElement>('#isAccepted-option0');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
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

  it('should post decision and emit submitted', () => {
    const submitSpy = jest.spyOn(hostComponent, 'submitted');

    expect(page.errorSummary).toBeFalsy();
    expect(submitSpy).toHaveBeenCalledTimes(0);

    page.button.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryErrorList).toEqual(['You need to approve or reject the application']);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(0);

    page.approveDecisionOption.click();
    page.button.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionPayload: {
        decision: 'ACCEPTED',
        payloadType: 'INSTALLATION_ACCOUNT_OPENING_SUBMIT_DECISION_PAYLOAD',
      },
      requestTaskActionType: 'INSTALLATION_ACCOUNT_OPENING_SUBMIT_DECISION',
      requestTaskId: 856,
    });
    expect(submitSpy).toHaveBeenCalledTimes(1);
    expect(submitSpy).toHaveBeenCalledWith(true);
  });
});

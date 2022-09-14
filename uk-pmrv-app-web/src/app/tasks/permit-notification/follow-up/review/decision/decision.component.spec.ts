import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PermitNotificationSharedModule } from '@shared/components/permit-notification/permit-notification-shared.module';
import { SharedModule } from '@shared/shared.module';
import { KeycloakService } from 'keycloak-angular';
import moment from 'moment';

import {
  PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
  PermitNotificationFollowUpReviewDecision,
  TasksService,
} from 'pmrv-api';

import { BasePage, MockType } from '../../../../../../testing';
import { TaskSharedModule } from '../../../../shared/task-shared-module';
import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { FollowUpReviewDecisionComponent } from './decision.component';

describe('DesicionComponent', () => {
  let component: FollowUpReviewDecisionComponent;
  let fixture: ComponentFixture<FollowUpReviewDecisionComponent>;
  let store: CommonTasksStore;
  let page: Page;

  const mockReviewAcceptedDecision: PermitNotificationFollowUpReviewDecision = {
    type: 'ACCEPTED',
    notes: 'some notes',
  };

  const mockReviewAmendsNeededDecision: PermitNotificationFollowUpReviewDecision = {
    type: 'AMENDS_NEEDED',
    changesRequired: 'zczxcvsdfzbxcv asDFZASc',
    dueDate: moment().add(2, 'd').utc(true).format('YYYY-MM-DD'),
    files: [],
    notes: 'some notes',
  };

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<FollowUpReviewDecisionComponent> {
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get summaryDetails() {
      return this.query<HTMLLinkElement>('.summaryDetails');
    }

    get decisionSummary() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get changeLink() {
      return this.query<HTMLLinkElement>('.govuk-heading-m a');
    }

    get acceptDecisionOption() {
      return this.query<HTMLInputElement>('#type-option0');
    }

    get amendsNeededDecisionOption() {
      return this.query<HTMLInputElement>('#type-option1');
    }

    get notes() {
      return this.getInputValue('#notes');
    }
    set notes(value: string) {
      this.setInputValue('#notes', value);
    }

    get changesRequired() {
      return this.getInputValue('#changesRequired');
    }
    set changesRequired(value: string) {
      this.setInputValue('#changesRequired', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('.submitButton');
    }

    get dueDateDay() {
      return this.getInputValue('#dueDate-day');
    }
    set dueDateDay(value: string) {
      this.setInputValue('#dueDate-day', value);
    }

    get dueDateMonth() {
      return this.getInputValue('#dueDate-month');
    }
    set dueDateMonth(value: string) {
      this.setInputValue('#dueDate-month', value);
    }

    get dueDateYear() {
      return this.getInputValue('#dueDate-year');
    }
    set dueDateYear(value: string) {
      this.setInputValue('#dueDate-year', value);
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(FollowUpReviewDecisionComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
      declarations: [FollowUpReviewDecisionComponent],
      imports: [SharedModule, RouterTestingModule, TaskSharedModule, PermitNotificationSharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: {
        requestTask: {
          id: 1,
          type: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW',
          payload: {
            payloadType: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW_PAYLOAD',
            followUpAttachments: {},
            followUpFiles: [],
            followUpRequest: 'sedfsdf',
            followUpResponse: 'the response 22',
            followUpResponseExpirationDate: moment().add(1, 'd').utc(true).format('YYYY-MM-DD'),
            permitNotificationType: 'OTHER_FACTOR',
            reviewDecision: mockReviewAcceptedDecision,
            reviewSectionsCompleted: {},
          } as PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
        },
      },
    });
  });

  beforeEach(createComponent);
  afterEach(() => jest.clearAllMocks());

  it('should create and show summary details', () => {
    expect(component).toBeTruthy();
    expect(page.summaryDetails).toBeTruthy();
  });

  it('should update and submit form decision as accepted', () => {
    let notes = '';

    page.changeLink.click();
    fixture.detectChanges();

    expect(page.acceptDecisionOption.checked).toBe(true);
    expect(page.amendsNeededDecisionOption.checked).toBe(false);
    expect(page.notes).toEqual('some notes');

    notes = 'new notes';

    page.notes = notes;
    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_REVIEW_DECISION',
      requestTaskId: 1,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_REVIEW_DECISION_PAYLOAD',
        reviewDecision: {
          ...mockReviewAcceptedDecision,
          notes,
        },
      },
    });
  });

  it('should update and submit form decision as amends needed', () => {
    let changesRequired = null;
    const oneWeekAfter = moment().add(7, 'd').utc(true).format('YYYY-MM-DD');
    const date = oneWeekAfter.split('-');
    const year = date[0];
    const month = date[1];
    const days = date[2];

    const state = store.getState();
    store.setState({
      ...state,
      requestTaskItem: {
        ...state.requestTaskItem,
        requestTask: {
          ...state.requestTaskItem.requestTask,
          payload: {
            ...state.requestTaskItem.requestTask.payload,
            reviewDecision: mockReviewAmendsNeededDecision,
          } as PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
        },
      },
    });

    createComponent();

    page.changeLink.click();
    fixture.detectChanges();

    expect(page.acceptDecisionOption.checked).toBe(false);
    expect(page.amendsNeededDecisionOption.checked).toBe(true);
    expect(page.changesRequired).toEqual('zczxcvsdfzbxcv asDFZASc');

    page.changesRequired = changesRequired;

    page.dueDateYear = year;
    page.dueDateMonth = month;
    page.dueDateDay = days;

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(0);
    expect(page.errorSummary).toBeTruthy();

    changesRequired = 'new changesRequired';

    page.changesRequired = changesRequired;
    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_REVIEW_DECISION',
      requestTaskId: 1,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_REVIEW_DECISION_PAYLOAD',
        reviewDecision: {
          ...mockReviewAmendsNeededDecision,
          dueDate: new Date(oneWeekAfter),
          changesRequired,
        },
      },
    });
  });
});

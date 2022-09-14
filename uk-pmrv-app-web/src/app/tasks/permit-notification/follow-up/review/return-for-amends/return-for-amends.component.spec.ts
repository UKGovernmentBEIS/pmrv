import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PermitNotificationSharedModule } from '@shared/components/permit-notification/permit-notification-shared.module';
import { SharedModule } from '@shared/shared.module';
import { BasePage, MockType } from '@testing';
import { KeycloakService } from 'keycloak-angular';
import moment from 'moment';

import {
  PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
  PermitNotificationFollowUpReviewDecision,
  TasksService,
} from 'pmrv-api';

import { TaskSharedModule } from '../../../../shared/task-shared-module';
import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { FollowUpReturnForAmendsComponent } from './return-for-amends.component';

describe('ReturnForAmendsComponent', () => {
  let component: FollowUpReturnForAmendsComponent;
  let fixture: ComponentFixture<FollowUpReturnForAmendsComponent>;
  let store: CommonTasksStore;
  let page: Page;

  class Page extends BasePage<FollowUpReturnForAmendsComponent> {
    get submitButton() {
      return this.query<HTMLButtonElement>('.submitButton');
    }
  }

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  const mockReviewAmendsNeededDecision: PermitNotificationFollowUpReviewDecision = {
    type: 'AMENDS_NEEDED',
    changesRequired: 'zczxcvsdfzbxcv asDFZASc',
    dueDate: moment().add(2, 'd').utc(true).format('YYYY-MM-DD'),
    files: [],
    notes: 'some notes',
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
      declarations: [FollowUpReturnForAmendsComponent],
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
            reviewDecision: mockReviewAmendsNeededDecision,
            reviewSectionsCompleted: {},
          } as PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
        },
      },
    });

    fixture = TestBed.createComponent(FollowUpReturnForAmendsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should confirm and send for amends to operator', () => {
    page.submitButton.click();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_NOTIFICATION_FOLLOW_UP_RETURN_FOR_AMENDS',
      requestTaskId: 1,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });
  });
});

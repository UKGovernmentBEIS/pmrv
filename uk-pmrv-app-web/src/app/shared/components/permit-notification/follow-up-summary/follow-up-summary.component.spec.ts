import { ChangeDetectorRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { SharedModule } from '@shared/shared.module';
import { KeycloakService } from 'keycloak-angular';
import moment from 'moment';

import {
  PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
  PermitNotificationFollowUpReviewDecision,
  PermitNotificationWaitForFollowUpRequestTaskPayload,
} from 'pmrv-api';

import { ActivatedRouteStub, BasePage } from '../../../../../testing';
import { SummaryList } from '../../../../tasks/permit-notification/follow-up/model/model';
import { CommonTasksStore } from '../../../../tasks/store/common-tasks.store';
import { TasksModule } from '../../../../tasks/tasks.module';
import { FollowUpSummaryComponent } from './follow-up-summary.component';

describe('FollowUpSummaryComponent', () => {
  let component: FollowUpSummaryComponent;
  let fixture: ComponentFixture<FollowUpSummaryComponent>;
  let store: CommonTasksStore;
  let page: Page;
  let govukDatePipe: GovukDatePipe;

  class Page extends BasePage<FollowUpSummaryComponent> {
    get details() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  const route = new ActivatedRouteStub({ taskId: 63 }, null, {
    pageTitle: '',
  });

  const followUpSummaryListMapper: Record<
    keyof { followUpRequest: string; followUpResponseExpirationDate: string },
    SummaryList
  > = {
    followUpRequest: { label: 'Provide a response', order: 1, type: 'string' },
    followUpResponseExpirationDate: { label: 'Due date', order: 2, type: 'date', url: 'due-date' },
  };

  const followUpDecisionSummaryListMapper: Record<
    keyof { type: string; changesRequired: string; files: string[]; dueDate: string; notes: string },
    SummaryList
  > = {
    type: { label: 'Decision status', order: 1, type: 'string' },
    changesRequired: { label: 'Changes required', order: 2, type: 'string' },
    files: { label: 'Uploaded files', order: 3, type: 'files', isArray: true },
    dueDate: { label: 'Changes due by', order: 4, type: 'date' },
    notes: { label: 'Notes', order: 5, type: 'string' },
  };

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

  const createComponent = () => {
    fixture = TestBed.createComponent(FollowUpSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    govukDatePipe = TestBed.inject(GovukDatePipe);
    fixture.detectChanges();
  };

  // Making Angular aware of changes in component tests With OnPush Change Detection
  async function runOnPushChangeDetection(fixture: ComponentFixture<any>): Promise<void> {
    const changeDetectorRef = fixture.debugElement.injector.get<ChangeDetectorRef>(ChangeDetectorRef);
    changeDetectorRef.detectChanges();
    return fixture.whenStable();
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, TasksModule, SharedModule],
      declarations: [FollowUpSummaryComponent],
      providers: [GovukDatePipe, KeycloakService, { provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
  });

  afterEach(() => jest.clearAllMocks());

  beforeEach(() => {
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: {
        requestTask: {
          id: 63,
          type: 'PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP',
          payload: {
            payloadType: 'PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP_PAYLOAD',
            followUpRequest: 'test',
            followUpResponseExpirationDate: '2022-06-02',
          } as PermitNotificationWaitForFollowUpRequestTaskPayload,
        },
      },
    });
  });

  beforeEach(() => createComponent());

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render summary details for wait for follow up', async () => {
    component.summaryListMapper = followUpSummaryListMapper;
    const state = store.getState();
    const payload = state.requestTaskItem.requestTask.payload as PermitNotificationWaitForFollowUpRequestTaskPayload;
    const data = (({ followUpRequest, followUpResponseExpirationDate }) => ({
      followUpRequest,
      followUpResponseExpirationDate,
    }))(payload);
    component.data = data;
    await runOnPushChangeDetection(fixture);
    expect(page.details).toEqual([
      ['Provide a response', 'test'],
      ['Due date', '2 Jun 2022'],
    ]);
  });

  it('should show decision summary when form is submitted as accepted for follow up review decision', async () => {
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

    component.summaryListMapper = followUpDecisionSummaryListMapper;
    const state = store.getState();
    const payload = state.requestTaskItem.requestTask.payload[
      'reviewDecision'
    ] as PermitNotificationFollowUpReviewDecision;
    const data = (({ type, changesRequired, dueDate, files, notes }) => ({
      type: type === 'ACCEPTED' ? 'Accepted' : 'Operator amends needed',
      changesRequired,
      dueDate,
      files,
      notes,
    }))(payload);
    component.data = data;

    await runOnPushChangeDetection(fixture);
    expect(page.details).toEqual([
      ['Decision status', 'Accepted'],
      ['Notes', 'some notes'],
    ]);
  });

  it('should show decision summary when form is submitted as amends needed', async () => {
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

    component.summaryListMapper = followUpDecisionSummaryListMapper;
    const state = store.getState();
    const payload = state.requestTaskItem.requestTask.payload[
      'reviewDecision'
    ] as PermitNotificationFollowUpReviewDecision;
    const data = (({ type, changesRequired, dueDate, files, notes }) => ({
      type: type === 'ACCEPTED' ? 'Accepted' : 'Operator amends needed',
      changesRequired,
      dueDate,
      files,
      notes,
    }))(payload);
    component.data = data;

    await runOnPushChangeDetection(fixture);

    expect(page.details).toEqual([
      ['Decision status', 'Operator amends needed'],
      ['Changes required', 'zczxcvsdfzbxcv asDFZASc'],
      ['Changes due by', govukDatePipe.transform(mockReviewAmendsNeededDecision.dueDate)],
      ['Notes', 'some notes'],
    ]);
  });
});

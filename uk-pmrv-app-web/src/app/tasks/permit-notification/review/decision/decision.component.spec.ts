import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { OtherFactor, PermitNotificationApplicationReviewRequestTaskPayload, TasksService } from 'pmrv-api';

import { BasePage, MockType } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { TaskSharedModule } from '../../../shared/task-shared-module';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { PermitNotificationModule } from '../../permit-notification.module';
import { DecisionComponent } from './decision.component';

describe('DecisionComponent', () => {
  let component: DecisionComponent;
  let fixture: ComponentFixture<DecisionComponent>;
  let store: CommonTasksStore;
  let page: Page;

  const mockReviewDecision = {
    type: 'ACCEPTED',
    officialNotice: 'officialNotice',
    followUp: {
      followUpResponseRequired: false,
    },
    notes: 'some notes',
  };

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<DecisionComponent> {
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get summaryDetails() {
      return this.query<HTMLLinkElement>('app-permit-notification-summary-details');
    }

    get gettest() {
      return this.query<any>('.gettest');
    }

    get decisionSummary() {
      return this.queryAll<HTMLDivElement>('#summary-review-decision .govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get changeLink() {
      return this.query<HTMLLinkElement>('.govuk-heading-m a');
    }

    get acceptDecisionOption() {
      return this.query<HTMLInputElement>('#type-option0');
    }

    get rejectDecisionOption() {
      return this.query<HTMLInputElement>('#type-option1');
    }

    get officialNotice() {
      return this.getInputValue('#officialNotice');
    }
    set officialNotice(value: string) {
      this.setInputValue('#officialNotice', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
      imports: [RouterTestingModule, PermitNotificationModule, SharedModule, TaskSharedModule],
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
          type: 'PERMIT_NOTIFICATION_APPLICATION_REVIEW',
          payload: {
            payloadType: 'PERMIT_NOTIFICATION_APPLICATION_REVIEW_PAYLOAD',
            permitNotification: {
              type: 'OTHER_FACTOR',
              description: 'sdfsd',
              reportingType: 'RENOUNCE_FREE_ALLOCATIONS',
            } as OtherFactor,
            reviewDecision: mockReviewDecision,
          } as PermitNotificationApplicationReviewRequestTaskPayload,
        },
      },
    });

    fixture = TestBed.createComponent(DecisionComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show summary details', () => {
    expect(page.summaryDetails).toBeTruthy();
  });

  it('should show decision summary when form is submitted', () => {
    expect(page.decisionSummary).toEqual([
      ['Decision status', 'Accepted'],
      ['Official Notice text', 'officialNotice'],
      ['Is an operator follow up required', 'No'],
      ['Notes', 'some notes'],
    ]);
  });

  it('should update and submit form decision', () => {
    let officialNotice = '';

    page.changeLink.click();
    fixture.detectChanges();

    expect(page.acceptDecisionOption.checked).toBe(true);
    expect(page.rejectDecisionOption.checked).toBe(false);
    expect(page.officialNotice).toEqual('officialNotice');

    page.officialNotice = officialNotice;
    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(0);
    expect(page.errorSummary).toBeTruthy();

    officialNotice = 'new officialNotice';

    page.officialNotice = officialNotice;
    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_NOTIFICATION_SAVE_REVIEW_GROUP_DECISION',
      requestTaskId: 1,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_NOTIFICATION_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
        reviewDecision: {
          ...mockReviewDecision,
          officialNotice: officialNotice,
        },
        reviewDeterminationCompleted: true,
      },
    });
  });
});

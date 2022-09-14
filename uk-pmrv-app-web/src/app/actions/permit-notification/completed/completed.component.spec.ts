import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitNotificationSharedModule } from '@shared/components/permit-notification/permit-notification-shared.module';
import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';

import { PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload } from 'pmrv-api';

import { ActionSharedModule } from '../../shared/action-shared-module';
import { CommonActionsStore } from '../../store/common-actions.store';
import { CompletedComponent } from './completed.component';

describe('CompletedComponent', () => {
  let component: CompletedComponent;
  let fixture: ComponentFixture<CompletedComponent>;
  let store: CommonActionsStore;
  let page: Page;

  class Page extends BasePage<CompletedComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [CompletedComponent],
      imports: [ActionSharedModule, SharedModule, RouterTestingModule, PermitNotificationSharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonActionsStore);
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        id: 1,
        type: 'PERMIT_NOTIFICATION_APPLICATION_COMPLETED',
        creationDate: '2022-07-27T17:11:29.82608Z',
        payload: {
          payloadType: 'PERMIT_NOTIFICATION_APPLICATION_COMPLETED_PAYLOAD',
          permitNotificationType: 'OTHER_FACTOR',
          request: 'request text',
          responseExpirationDate: '2022-08-10',
          response: 'response text',
          responseFiles: [],
          responseAttachments: {},
          responseSubmissionDate: '2022-07-27',
          reviewDecision: {
            type: 'ACCEPTED',
            notes: 'Decision notes',
          },
          reviewDecisionNotification: {
            operators: ['a51b012d-70ce-4341-8a86-60f078281f0e'],
            signatory: '69feaa9c-ef0f-4b85-86f1-e4f161b6ff22',
          },
          usersInfo: {
            '69feaa9c-ef0f-4b85-86f1-e4f161b6ff22': {
              name: 'RegulatorFirst1 RegulatorLast1',
            },
            'a51b012d-70ce-4341-8a86-60f078281f0e': {
              name: 'FirstName LastName',
              roleCode: 'operator',
              contactTypes: ['SECONDARY'],
            },
            '036c09e4-8f13-49ed-89b2-14d1832edf14': {
              name: 'FirstName2 LastName2',
              roleCode: 'operator_admin',
              contactTypes: ['FINANCIAL', 'SERVICE', 'PRIMARY'],
            },
          },
        } as PermitNotificationFollowUpApplicationReviewSubmittedDecisionRequestActionPayload,
      },
    });

    fixture = TestBed.createComponent(CompletedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the details', () => {
    expect(page.summaryListValues).toEqual([
      ['Request from the regulator', 'request text'],
      ['Due date', '10 Aug 2022'],
      ['Submission date', '27 Jul 2022'],
      ['Operators response', 'response text'],
      ['Decision status', 'Accepted'],
      ['Notes', 'Decision notes'],
      [
        'Users',
        'FirstName LastName, Operator - Secondary contact  FirstName2 LastName2, Operator admin - Financial contact, Service contact, Primary contact',
      ],
      ['Name and signature on the official notice', 'RegulatorFirst1 RegulatorLast1'],
    ]);
  });
});

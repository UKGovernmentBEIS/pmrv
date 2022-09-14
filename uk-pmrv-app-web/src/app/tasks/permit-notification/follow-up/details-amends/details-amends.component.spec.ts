import { ChangeDetectorRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PermitNotificationSharedModule } from '@shared/components/permit-notification/permit-notification-shared.module';
import { SharedModule } from '@shared/shared.module';
import { KeycloakService } from 'keycloak-angular';

import { PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../../testing';
import { TaskSharedModule } from '../../../shared/task-shared-module';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { DetailsAmendsComponent } from './details-amends.component';

describe('Details Amends Component', () => {
  let component: DetailsAmendsComponent;
  let fixture: ComponentFixture<DetailsAmendsComponent>;
  let store: CommonTasksStore;
  let page: Page;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<DetailsAmendsComponent> {
    get changes() {
      return this.query<HTMLInputElement>('#changes-0');
    }
    get changesValue() {
      return this.getInputValue(this.changes);
    }

    set changesValue(value: boolean) {
      this.setInputValue('input[name="changes"]', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('govuk-error-summary');
    }

    get errors() {
      return this.queryAll<HTMLLIElement>('ul.govuk-error-summary__list > li');
    }
  }

  const route = new ActivatedRouteStub({ taskId: 13 }, null, {
    pageTitle: 'Details of the amends needed',
  });

  // Making Angular aware of changes in component tests With OnPush Change Detection
  async function runOnPushChangeDetection(fixture: ComponentFixture<any>): Promise<void> {
    const changeDetectorRef = fixture.debugElement.injector.get<ChangeDetectorRef>(ChangeDetectorRef);
    changeDetectorRef.detectChanges();
    return fixture.whenStable();
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(DetailsAmendsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DetailsAmendsComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule, PermitNotificationSharedModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
    store = TestBed.inject(CommonTasksStore);
  });

  beforeEach(() => {
    store.setState({
      ...store.getState(),
      isEditable: true,
      requestTaskItem: {
        requestTask: {
          id: 13,
          type: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT',
          payload: {
            payloadType: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT_PAYLOAD',
            followUpRequest: 'ewew',
            followUpResponseExpirationDate: '2022-06-27',
            followUpResponse: 'test',
            followUpAttachments: {
              'db751dd3-651f-4b9f-a313-ceba28d5a00e': 'Digital Dashboard.docx',
            },
            permitNotificationType: 'OTHER_FACTOR',
            reviewDecision: {
              type: 'AMENDS_NEEDED',
              changesRequired: 'there is a thing about the thing',
              files: ['db751dd3-651f-4b9f-a313-ceba28d5a00e'],
              dueDate: '2022-06-27',
            },
            reviewSectionsCompleted: {},
            followUpSectionsCompleted: {
              AMENDS_NEEDED: false,
            },
            submissionDate: '',
          } as PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload,
        },
      },
    });
  });

  beforeEach(createComponent);

  afterEach(() => jest.clearAllMocks());

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(page.changesValue).toBeFalsy();
  });

  it('should render form with a value', async () => {
    page.changesValue = true;
    await runOnPushChangeDetection(fixture);
    expect(page.changesValue).toEqual('true');
  });

  it('should validate form and display an error message', () => {
    page.changesValue = null;
    page.submitButton.click();
    fixture.detectChanges();
    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual([
      'Check the box to confirm you have made changes',
    ]);
    expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();
  });

  it('Should submit form', async () => {
    page.changes.click();
    page.submitButton.click();
    await runOnPushChangeDetection(fixture);
    expect(page.errorSummary).toBeFalsy();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
  });
});

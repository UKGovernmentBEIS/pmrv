import { ChangeDetectorRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { SharedModule } from '@shared/shared.module';
import { KeycloakService } from 'keycloak-angular';

import { PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { BasePage, MockType } from '../../../../../../testing';
import { TaskSharedModule } from '../../../../shared/task-shared-module';
import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { SubmitAmendsContainerComponent } from './submit-amends.component';

describe('Submit Amends Container Component', () => {
  let component: SubmitAmendsContainerComponent;
  let fixture: ComponentFixture<SubmitAmendsContainerComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<SubmitAmendsContainerComponent> {
    get submitButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  async function runOnPushChangeDetection(fixture: ComponentFixture<any>): Promise<void> {
    const changeDetectorRef = fixture.debugElement.injector.get<ChangeDetectorRef>(ChangeDetectorRef);
    changeDetectorRef.detectChanges();
    return fixture.whenStable();
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SubmitAmendsContainerComponent],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }, DestroySubject],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    fixture = TestBed.createComponent(SubmitAmendsContainerComponent);
    component = fixture.componentInstance;

    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit when apply is complete', async () => {
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: {
        requestInfo: {
          id: 'AEMN134-2',
          type: 'PERMIT_NOTIFICATION',
          competentAuthority: 'ENGLAND',
          accountId: 134,
        },
        requestTask: {
          id: 1,
          type: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT',
          payload: {
            payloadType: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT_PAYLOAD',
            followUpResponse: 'test',
            followUpRequest: '',
            followUpResponseExpirationDate: '',
            followUpSectionsCompleted: {
              AMENDS_NEEDED: true,
            } as PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload['followUpSectionsCompleted'],
          } as PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload,
        },
      },
    });

    await runOnPushChangeDetection(fixture);
    page.submitButton.click();
    fixture.detectChanges();
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionPayload: {
        payloadType: 'PERMIT_NOTIFICATION_FOLLOW_UP_SUBMIT_APPLICATION_AMEND_PAYLOAD',
      },
      requestTaskActionType: 'PERMIT_NOTIFICATION_FOLLOW_UP_SUBMIT_APPLICATION_AMEND',
      requestTaskId: 1,
    });
  });
});

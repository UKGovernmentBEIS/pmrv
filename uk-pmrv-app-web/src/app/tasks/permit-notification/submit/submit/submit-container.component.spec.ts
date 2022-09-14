import { ChangeDetectorRef } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { KeycloakService } from 'keycloak-angular';

import { OtherFactor, PermitNotificationApplicationSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { BasePage, MockType } from '../../../../../testing';
import { TaskSharedModule } from '../../../shared/task-shared-module';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { SubmitContainerComponent } from './submit-container.component';

describe('Submit Container Component', () => {
  let component: SubmitContainerComponent;
  let fixture: ComponentFixture<SubmitContainerComponent>;
  let page: Page;
  let store: CommonTasksStore;

  class Page extends BasePage<SubmitContainerComponent> {
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
      declarations: [SubmitContainerComponent],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    fixture = TestBed.createComponent(SubmitContainerComponent);
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
          type: 'PERMIT_NOTIFICATION_APPLICATION_SUBMIT',
          payload: {
            payloadType: 'PERMIT_NOTIFICATION_APPLICATION_SUBMIT_PAYLOAD',
            permitNotification: {
              type: 'OTHER_FACTOR',
              description: 'description',
              reportingType: 'RENOUNCE_FREE_ALLOCATIONS',
            } as OtherFactor,
            sectionsCompleted: {
              DETAILS_CHANGE: true,
            },
          } as PermitNotificationApplicationSubmitRequestTaskPayload,
        },
      },
    });

    await runOnPushChangeDetection(fixture);
    page.submitButton.click();
    fixture.detectChanges();
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
      requestTaskActionType: 'PERMIT_NOTIFICATION_SUBMIT_APPLICATION',
      requestTaskId: 1,
    });
  });
});

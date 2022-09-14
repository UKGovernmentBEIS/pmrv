import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import {
  OtherFactor,
  PermitNotificationApplicationSubmitRequestTaskPayload,
  RequestTaskActionPayload,
  TasksService,
} from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { TaskSharedModule } from '../../../shared/task-shared-module';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { PermitNotificationModule } from '../../permit-notification.module';
import { DetailsOfChangeComponent } from './details-of-change.component';

describe('DetailsOfChangeComponent', () => {
  let component: DetailsOfChangeComponent;
  let fixture: ComponentFixture<DetailsOfChangeComponent>;
  let store: CommonTasksStore;
  let page: Page;
  let router: Router;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };
  const route = new ActivatedRouteStub();

  class Page extends BasePage<DetailsOfChangeComponent> {
    get typeRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="type"]');
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

  const createComponent = () => {
    fixture = TestBed.createComponent(DetailsOfChangeComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
      imports: [SharedModule, RouterTestingModule, TaskSharedModule, PermitNotificationModule],
    }).compileComponents();
  });

  describe('for new notification', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...store.getState(),
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
          requestTask: {
            id: 1,
            type: 'PERMIT_NOTIFICATION_APPLICATION_SUBMIT',
            payload: {
              payloadType: 'PERMIT_NOTIFICATION_APPLICATION_SUBMIT_PAYLOAD',
            } as PermitNotificationApplicationSubmitRequestTaskPayload,
          },
        },
      });
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit and navigate to description', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errors.map((error) => error.textContent.trim())).toEqual(['Select a notification type']);
      expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();

      page.typeRadios[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_NOTIFICATION_SAVE_APPLICATION',
        requestTaskId: 1,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_NOTIFICATION_SAVE_APPLICATION_PAYLOAD',
          permitNotification: {
            type: 'TEMPORARY_FACTOR',
          },
          sectionsCompleted: {
            DETAILS_CHANGE: false,
          },
        } as RequestTaskActionPayload,
      });

      expect(navigateSpy).toHaveBeenCalledWith(['../description'], { relativeTo: route });
    });
  });

  describe('for editing notification', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...store.getState(),
        storeInitialized: true,
        isEditable: true,
        requestTaskItem: {
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
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit and navigate to description', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.typeRadios[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_NOTIFICATION_SAVE_APPLICATION',
        requestTaskId: 1,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_NOTIFICATION_SAVE_APPLICATION_PAYLOAD',
          permitNotification: {
            type: 'TEMPORARY_FACTOR',
          },
          sectionsCompleted: {
            DETAILS_CHANGE: false,
          },
        } as RequestTaskActionPayload,
      });

      expect(navigateSpy).toHaveBeenCalledWith(['../description'], { relativeTo: route });
    });
  });
});

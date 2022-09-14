import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { OtherFactor, PermitNotificationApplicationSubmitRequestTaskPayload, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { TaskSharedModule } from '../../../shared/task-shared-module';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { PermitNotificationModule } from '../../permit-notification.module';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let store: CommonTasksStore;
  let page: Page;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };
  const route = new ActivatedRouteStub();

  class Page extends BasePage<SummaryComponent> {
    get summaryDefinitions() {
      return this.queryAll('dd').map((row) => row.textContent.trim());
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
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

  it('should display the summary details', () => {
    expect(page.summaryDefinitions).toEqual([
      'Some other factor',
      'Change',
      'Renounce free allocations',
      'Change',
      'description',
      'Change',
      'No',
      'Change',
    ]);
  });
});

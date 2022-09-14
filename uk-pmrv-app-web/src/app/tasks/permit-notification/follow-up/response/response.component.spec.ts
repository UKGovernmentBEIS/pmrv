import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';
import { KeycloakService } from 'keycloak-angular';

import { PermitNotificationFollowUpRequestTaskPayload, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../../testing';
import { TaskSharedModule } from '../../../shared/task-shared-module';
import { CommonTasksStore } from '../../../store/common-tasks.store';
import { ResponseComponent } from './response.component';

describe('Response Component', () => {
  let component: ResponseComponent;
  let fixture: ComponentFixture<ResponseComponent>;
  let store: CommonTasksStore;
  let page: Page;
  let router: Router;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<ResponseComponent> {
    get followUpResponse() {
      return this.getInputValue('#followUpResponse');
    }

    set followUpResponse(value: string) {
      this.setInputValue('#followUpResponse', value);
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

  const route = new ActivatedRouteStub({ taskId: 63 }, null, {
    pageTitle: 'Follow up response',
    keys: ['files', 'followUpResponse'],
    configFilesOptions: {
      type: 'PERMIT_NOTIFICATION_FOLLOW_UP_UPLOAD_ATTACHMENT',
      filesName: 'followUpFiles',
      attachmentsName: 'followUpAttachments',
    },
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(ResponseComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ResponseComponent],
      imports: [RouterTestingModule, SharedModule, TaskSharedModule],
      providers: [
        KeycloakService,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
    store = TestBed.inject(CommonTasksStore);
    router = TestBed.inject(Router);
  });

  beforeEach(() => {
    store.setState({
      ...store.getState(),
      isEditable: true,
      requestTaskItem: {
        requestTask: {
          id: 63,
          type: 'PERMIT_NOTIFICATION_FOLLOW_UP',
          payload: {
            payloadType: 'PERMIT_NOTIFICATION_FOLLOW_UP_PAYLOAD',
            followUpResponse: '',
          } as PermitNotificationFollowUpRequestTaskPayload,
        },
      },
    });
  });

  beforeEach(createComponent);

  afterEach(() => jest.clearAllMocks());

  it('should create', () => {
    expect(component).toBeTruthy();
    expect(page.followUpResponse).toBeFalsy();
  });

  it('should render form with a value', () => {
    component.form.get('followUpResponse').setValue('some response');

    expect(page.followUpResponse).toEqual('some response');
  });

  it('should validate form and display an error message', () => {
    page.followUpResponse = null;
    page.submitButton.click();
    fixture.detectChanges();
    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual(['Enter a response']);
    expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();
  });

  it('Should submit form and navigate to summary page', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    component.form.get('followUpResponse').setValue('some response');
    page.submitButton.click();
    fixture.detectChanges();
    expect(page.errorSummary).toBeFalsy();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'summary'], { relativeTo: route, state: { notification: true } });
  });
});

import { HttpErrorResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of, throwError } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../testing';
import { ConcurrencyTestingModule, expectConcurrentErrorToBe } from '../../error/testing/concurrency-error';
import { requestTaskReassignedError, taskNotFoundError } from '../../shared/errors/concurrency-error';
import { PermitApplicationModule } from '../permit-application.module';
import { TaskStatusPipe } from '../shared/pipes/task-status.pipe';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockPermitCompletePayload } from '../testing/mock-permit-apply-action';
import { mockState, mockStateBuild } from '../testing/mock-state';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let page: Page;
  let store: PermitApplicationStore;
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let route: ActivatedRouteStub;
  let router: Router;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<SummaryComponent> {
    get heading() {
      return this.query('h1.govuk-heading-l');
    }

    get pageBody() {
      return this.query('p[class="govuk-body"] > span');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    route = new ActivatedRouteStub({ taskId: 100 });

    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule, ConcurrencyTestingModule],
      providers: [
        TaskStatusPipe,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    store = TestBed.inject(PermitApplicationStore);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  };

  describe('for permit application amends submit task', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockState,
        permit: {
          ...mockPermitCompletePayload.permit,
        },
        reviewGroupDecisions: {
          INSTALLATION_DETAILS: {
            type: 'OPERATOR_AMENDS_NEEDED',
            changesRequired: 'Changes required for monitoring approaches',
            notes: 'notes',
          },
        },
        permitSectionsCompleted: {
          ...mockPermitCompletePayload.permitSectionsCompleted,
        },
        requestTaskType: 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT',
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display heading with permit type', () => {
      expect(page.heading.textContent.trim()).toBe('Submit your GHGE Permit application');
    });

    it('should display proper text when permit app is not ready for submission', () => {
      expect(page.pageBody.innerHTML).toBe('All tasks must be completed before you can submit your application.');
    });

    it('should display proper text and submit button when permit app is ready for submission!', () => {
      store.setState({
        ...mockState,
        permit: {
          ...mockPermitCompletePayload.permit,
        },
        reviewGroupDecisions: {
          INSTALLATION_DETAILS: {
            type: 'OPERATOR_AMENDS_NEEDED',
            changesRequired: 'Changes required for monitoring approaches',
            notes: 'notes',
          },
        },
        permitSectionsCompleted: {
          ...mockPermitCompletePayload.permitSectionsCompleted,
          AMEND_details: [true],
        },
        requestTaskType: 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT',
      });
      fixture.detectChanges();
      const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

      expect(page.pageBody.innerHTML).toContain(
        'By submitting this application you are confirming that the legal entity applying for this GHGE permit will be responsible for trading in the registry, and that to the best of your knowledge, the details you are providing are correct.',
      );

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_ISSUANCE_SUBMIT_APPLICATION_AMEND',
        requestTaskId: 100,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_ISSUANCE_SUBMIT_APPLICATION_AMEND_PAYLOAD',
          permitSectionsCompleted: {
            ...mockPermitCompletePayload.permitSectionsCompleted,
          },
        },
      });

      expect(navigateSpy).toHaveBeenCalledWith(['../application-submitted'], { relativeTo: route });
    });
  });

  describe('for task other than permit application amends submit', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display proper text when permit app is not ready for submission', () => {
      expect(page.pageBody.innerHTML).toBe('All tasks must be completed before you can submit your application.');
    });

    it('should display proper text and submit button when permit app is ready for submission!', () => {
      store.setState(
        mockStateBuild(
          { ...mockPermitCompletePayload.permit },
          { ...mockPermitCompletePayload.permitSectionsCompleted },
        ),
      );
      fixture.detectChanges();

      const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

      expect(page.pageBody.innerHTML.trim()).toContain(
        'By submitting this application you are confirming that the legal entity applying for this GHGE permit will be responsible for trading in the registry, and that to the best of your knowledge, the details you are providing are correct.',
      );

      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_ISSUANCE_SUBMIT_APPLICATION',
        requestTaskId: 100,
        requestTaskActionPayload: {
          payloadType: 'EMPTY_PAYLOAD',
        },
      });

      expect(navigateSpy).toHaveBeenCalledWith(['../application-submitted'], { relativeTo: route });
    });

    it('should navigate to not found error page when task not found', async () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(
        throwError(() => new HttpErrorResponse({ status: 404, error: { code: 'NOTFOUND1001' } })),
      );

      store.setState(
        mockStateBuild(
          { ...mockPermitCompletePayload.permit },
          { ...mockPermitCompletePayload.permitSectionsCompleted },
        ),
      );
      fixture.detectChanges();

      page.submitButton.click();
      fixture.detectChanges();

      await expectConcurrentErrorToBe(taskNotFoundError);
    });

    it('should navigate to reassigned task error page when task has been reassigned to a another user', async () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(
        throwError(() => new HttpErrorResponse({ status: 400, error: { code: 'REQUEST_TASK_ACTION1001' } })),
      );

      store.setState(
        mockStateBuild(
          { ...mockPermitCompletePayload.permit },
          { ...mockPermitCompletePayload.permitSectionsCompleted },
        ),
      );
      fixture.detectChanges();

      page.submitButton.click();
      fixture.detectChanges();

      await expectConcurrentErrorToBe(requestTaskReassignedError());
    });
  });
});

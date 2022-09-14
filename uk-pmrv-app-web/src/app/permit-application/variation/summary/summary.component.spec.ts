import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../testing';
import { ConcurrencyTestingModule } from '../../../error/testing/concurrency-error';
import { PermitApplicationModule } from '../../permit-application.module';
import { TaskStatusPipe } from '../../shared/pipes/task-status.pipe';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitCompletePayload } from '../../testing/mock-permit-apply-action';
import { mockState } from '../../testing/mock-state';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let store: PermitApplicationStore;
  let route: ActivatedRouteStub;
  let page: Page;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<SummaryComponent> {
    get heading() {
      return this.query('h1.govuk-heading-l');
    }

    get pageBodies() {
      return this.queryAll<HTMLParagraphElement>('p[class="govuk-body"]');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    route = new ActivatedRouteStub({ taskId: 237 });

    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule, ConcurrencyTestingModule],
      providers: [
        TaskStatusPipe,
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();

    store = TestBed.inject(PermitApplicationStore);
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  it('should create', () => {
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should not allow to submit if not completed', () => {
    store.setState({
      ...mockState,
      requestTaskType: 'PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT',
      payloadType: 'PERMIT_VARIATION_APPLICATION_SUBMIT_PAYLOAD',
      permit: mockPermitCompletePayload.permit,
      permitSectionsCompleted: { ...mockPermitCompletePayload.permitSectionsCompleted },
      permitVariationDetailsCompleted: false,
    });
    createComponent();
    expect(page.pageBodies[0].textContent.trim()).toEqual(
      'All tasks must be completed before you can submit your application.',
    );
  });

  it('should allow to submit if all completed', () => {
    store.setState({
      ...mockState,
      requestTaskType: 'PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT',
      payloadType: 'PERMIT_VARIATION_APPLICATION_SUBMIT_PAYLOAD',
      permit: mockPermitCompletePayload.permit,
      permitSectionsCompleted: { ...mockPermitCompletePayload.permitSectionsCompleted },
      permitVariationDetailsCompleted: true,
    });
    createComponent();
    expect(page.pageBodies[0].textContent.trim()).toEqual(
      'By submitting this application you are confirming that, to the best of your knowledge, the details you are providing are correct',
    );
  });

  it('should show submitted info upon pressing complete button', () => {
    store.setState({
      ...mockState,
      requestTaskType: 'PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT',
      payloadType: 'PERMIT_VARIATION_APPLICATION_SUBMIT_PAYLOAD',
      permit: mockPermitCompletePayload.permit,
      permitSectionsCompleted: { ...mockPermitCompletePayload.permitSectionsCompleted },
      permitVariationDetailsCompleted: true,
    });

    createComponent();

    page.submitButton.click();
    fixture.detectChanges();

    expect(Array.from(page.pageBodies).map((p) => p.textContent.trim())).toEqual([
      'We’ve sent your application to Environment Agency',
      'The regulator will make a decision and respond within 2 calendar months.',
    ]);

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_VARIATION_OPERATOR_SUBMIT_APPLICATION',
      requestTaskId: 237,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });
  });
});

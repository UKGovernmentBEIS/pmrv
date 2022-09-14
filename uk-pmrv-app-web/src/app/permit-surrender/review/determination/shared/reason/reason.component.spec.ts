import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { mockTaskState } from '../../../../testing/mock-state';
import { ReasonComponent } from './reason.component';

describe('ReasonComponent', () => {
  let component: ReasonComponent;
  let fixture: ComponentFixture<ReasonComponent>;
  let store: PermitSurrenderStore;
  let page: Page;
  let router: Router;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, null);

  class Page extends BasePage<ReasonComponent> {
    get reason() {
      return this.getInputValue('#reason');
    }

    set reason(value: string) {
      this.setInputValue('#reason', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryErrorList() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(ReasonComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ReasonComponent],
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitSurrenderStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should create', () => {
    store.setState(mockTaskState);
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should submit a valid granted form', () => {
    store.setState({ ...mockTaskState, reviewDetermination: { type: 'GRANTED', reason: undefined } });
    createComponent();

    const navigateSpy = jest.spyOn(router, 'navigate');

    expect(page.errorSummary).toBeFalsy();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryErrorList).toEqual(['Enter a note to support the grant decision']);

    page.reason = 'reason';
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION',
      requestTaskId: mockTaskState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION_PAYLOAD',
        reviewDetermination: { type: 'GRANTED', reason: 'reason' },
        reviewDeterminationCompleted: false,
      },
    });

    expect(navigateSpy).toHaveBeenCalledWith(['../stop-date'], { relativeTo: route });
  });

  it('should submit a valid rejected form', () => {
    store.setState({ ...mockTaskState, reviewDetermination: { type: 'REJECTED', reason: undefined } });
    createComponent();

    const navigateSpy = jest.spyOn(router, 'navigate');

    expect(page.errorSummary).toBeFalsy();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryErrorList).toEqual(['Enter a reason to support the reject decision']);

    page.reason = 'reason';
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION',
      requestTaskId: mockTaskState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION_PAYLOAD',
        reviewDetermination: { type: 'REJECTED', reason: 'reason' },
        reviewDeterminationCompleted: false,
      },
    });

    expect(navigateSpy).toHaveBeenCalledWith(['../refusal'], { relativeTo: route });
  });
});

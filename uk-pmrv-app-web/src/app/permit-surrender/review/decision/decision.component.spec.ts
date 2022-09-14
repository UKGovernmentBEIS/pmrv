import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitSurrenderModule } from '../../shared/shared-permit-surrender.module';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';
import { mockTaskState } from '../../testing/mock-state';
import { DecisionComponent } from './decision.component';
import { DecisionSummaryComponent } from './decision-summary/decision-summary.component';

describe('DecisionComponent', () => {
  let component: DecisionComponent;
  let fixture: ComponentFixture<DecisionComponent>;
  let store: PermitSurrenderStore;
  let page: Page;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<DecisionComponent> {
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryErrorList() {
      return Array.from(this.errorSummary.querySelectorAll('a')).map((anchor) => anchor.textContent.trim());
    }

    get summaryArea() {
      return this.query<HTMLElement>('app-permit-surrender-decision-summary');
    }

    get changeLink() {
      return this.query<HTMLLinkElement>('app-permit-surrender-decision-summary a');
    }

    get acceptDecisionOption() {
      return this.query<HTMLInputElement>('#decision-option0');
    }

    get rejectDecisionOption() {
      return this.query<HTMLInputElement>('#decision-option1');
    }

    get reviewNotesValue() {
      return this.getInputValue('#notes');
    }
    set reviewNotesValue(value: string) {
      this.setInputValue('#notes', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, null);

  const createComponent = () => {
    fixture = TestBed.createComponent(DecisionComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DecisionComponent, DecisionSummaryComponent],
      imports: [RouterTestingModule, SharedModule, SharedPermitSurrenderModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitSurrenderStore);
    store.setState({
      ...mockTaskState,
      reviewDecision: undefined,
      reviewDetermination: undefined,
      reviewDeterminationCompleted: undefined,
    });
  });

  afterEach(() => jest.clearAllMocks());

  it('should create', () => {
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should submit decision on empty review', () => {
    createComponent();

    expect(page.summaryArea).toBeNull();
    expect(page.changeLink).toBeFalsy();
    expect(page.errorSummary).toBeFalsy();
    expect(page.acceptDecisionOption.checked).toBe(false);
    expect(page.rejectDecisionOption.checked).toBe(false);

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(0);
    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryErrorList).toEqual([
      'Select a decision for this review group',
      'Enter notes for this review group',
    ]);

    page.acceptDecisionOption.click();
    page.submitButton.click();
    fixture.detectChanges();

    page.reviewNotesValue = 'Review notes';
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION',
      requestTaskId: mockTaskState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
        reviewDecision: { type: 'ACCEPTED', notes: 'Review notes' },
        reviewDeterminationCompleted: null,
      },
    });

    expect(page.summaryArea).toBeTruthy();
    expect(page.changeLink).toBeTruthy();
  });

  it('should submit a reject decision and reset determination completed status on an existing accepted decision and granted determination', () => {
    store.setState({
      ...store.getState(),
      reviewDecision: {
        type: 'ACCEPTED',
        notes: 'accepted notes',
      },
      reviewDetermination: {
        type: 'GRANTED',
        reason: 'granted reason',
      },
      reviewDeterminationCompleted: true,
    });
    createComponent();

    expect(page.summaryArea).toBeTruthy();
    expect(page.changeLink).toBeTruthy();

    page.changeLink.click();
    fixture.detectChanges();

    expect(page.acceptDecisionOption.checked).toBe(true);
    expect(page.rejectDecisionOption.checked).toBe(false);
    expect(page.reviewNotesValue).toEqual('accepted notes');

    page.rejectDecisionOption.click();
    page.reviewNotesValue = 'rejected notes';
    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION',
      requestTaskId: mockTaskState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
        reviewDecision: { type: 'REJECTED', notes: 'rejected notes' },
        reviewDeterminationCompleted: false,
      },
    });

    expect(page.summaryArea).toBeTruthy();
    expect(page.changeLink).toBeTruthy();
  });

  it('should submit a reject decision and keep determination completed status on an existing rejected decision and rejected determination', () => {
    store.setState({
      ...store.getState(),
      reviewDecision: {
        type: 'REJECTED',
        notes: 'rejected notes',
      },
      reviewDetermination: {
        type: 'REJECTED',
        reason: 'rejected reason',
      },
      reviewDeterminationCompleted: true,
    });
    createComponent();

    expect(page.summaryArea).toBeTruthy();
    expect(page.changeLink).toBeTruthy();

    page.changeLink.click();
    fixture.detectChanges();

    expect(page.acceptDecisionOption.checked).toBe(false);
    expect(page.rejectDecisionOption.checked).toBe(true);
    expect(page.reviewNotesValue).toEqual('rejected notes');

    page.rejectDecisionOption.click();
    page.reviewNotesValue = 'more rejected notes';
    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION',
      requestTaskId: mockTaskState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
        reviewDecision: { type: 'REJECTED', notes: 'more rejected notes' },
        reviewDeterminationCompleted: true,
      },
    });

    expect(page.summaryArea).toBeTruthy();
    expect(page.changeLink).toBeTruthy();
  });

  it('should submit an accepted decision and reset determination completed status on an existing rejected decision and rejected determination', () => {
    store.setState({
      ...store.getState(),
      reviewDecision: {
        type: 'REJECTED',
        notes: 'rejected notes',
      },
      reviewDetermination: {
        type: 'REJECTED',
        reason: 'rejected reason',
      },
      reviewDeterminationCompleted: true,
    });
    createComponent();

    expect(page.summaryArea).toBeTruthy();
    expect(page.changeLink).toBeTruthy();

    page.changeLink.click();
    fixture.detectChanges();

    expect(page.acceptDecisionOption.checked).toBe(false);
    expect(page.rejectDecisionOption.checked).toBe(true);
    expect(page.reviewNotesValue).toEqual('rejected notes');

    page.acceptDecisionOption.click();
    page.reviewNotesValue = 'accepted notes';
    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION',
      requestTaskId: mockTaskState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
        reviewDecision: { type: 'ACCEPTED', notes: 'accepted notes' },
        reviewDeterminationCompleted: false,
      },
    });

    expect(page.summaryArea).toBeTruthy();
    expect(page.changeLink).toBeTruthy();
  });

  it('should submit an accepted decision and keep determination completed status on an existing accepted decision and granted determination', () => {
    store.setState({
      ...store.getState(),
      reviewDecision: {
        type: 'ACCEPTED',
        notes: 'accepted notes',
      },
      reviewDetermination: {
        type: 'GRANTED',
        reason: 'granted reason',
      },
      reviewDeterminationCompleted: true,
    });
    createComponent();

    expect(page.summaryArea).toBeTruthy();
    expect(page.changeLink).toBeTruthy();

    page.changeLink.click();
    fixture.detectChanges();

    expect(page.reviewNotesValue).toEqual('accepted notes');

    page.reviewNotesValue = 'more accepted notes';
    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION',
      requestTaskId: mockTaskState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
        reviewDecision: { type: 'ACCEPTED', notes: 'more accepted notes' },
        reviewDeterminationCompleted: true,
      },
    });

    expect(page.summaryArea).toBeTruthy();
    expect(page.changeLink).toBeTruthy();
  });

  it('should submit an rejected decision and keep determination completed status on an existing decision and deemed determination', () => {
    store.setState({
      ...store.getState(),
      reviewDecision: {
        type: 'ACCEPTED',
        notes: 'accepted notes',
      },
      reviewDetermination: {
        type: 'DEEMED_WITHDRAWN',
        reason: 'deemed reason',
      },
      reviewDeterminationCompleted: true,
    });
    createComponent();

    expect(page.summaryArea).toBeTruthy();
    expect(page.changeLink).toBeTruthy();

    page.changeLink.click();
    fixture.detectChanges();

    expect(page.acceptDecisionOption.checked).toBe(true);
    expect(page.rejectDecisionOption.checked).toBe(false);
    expect(page.reviewNotesValue).toEqual('accepted notes');

    page.rejectDecisionOption.click();
    page.reviewNotesValue = 'rejected notes';
    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION',
      requestTaskId: mockTaskState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
        reviewDecision: { type: 'REJECTED', notes: 'rejected notes' },
        reviewDeterminationCompleted: true,
      },
    });

    expect(page.summaryArea).toBeTruthy();
    expect(page.changeLink).toBeTruthy();
  });
});

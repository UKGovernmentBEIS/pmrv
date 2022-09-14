import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PermitSurrenderReviewDeterminationReject, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { mockTaskState } from '../../../../testing/mock-state';
import { SummaryDetailsComponent } from '../summary/summary-details.component';
import { AnswersComponent } from './answers.component';

describe('AnswersComponent', () => {
  let component: AnswersComponent;
  let fixture: ComponentFixture<AnswersComponent>;
  let router: Router;
  let page: Page;
  let store: PermitSurrenderStore;

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, null);
  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<AnswersComponent> {
    get rows() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AnswersComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AnswersComponent, SummaryDetailsComponent],
      imports: [RouterTestingModule, SharedModule],
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
      reviewDecision: {
        type: 'REJECTED',
        notes: 'rejected notes',
      },
      reviewDetermination: {
        type: 'REJECTED',
        reason: 'reason',
        officialRefusalLetter: 'official refusal letter',
        shouldFeeBeRefundedToOperator: true,
      } as PermitSurrenderReviewDeterminationReject,
    });
  });
  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('show reject details', () => {
    expect(page.rows).toEqual([
      ['Decision', 'Reject'],
      ['Reason for decision', 'reason'],
      ['Official refusal letter', 'official refusal letter'],
      ['Operator refund', 'Yes'],
    ]);
  });

  it('should submit', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION',
      requestTaskId: mockTaskState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION_PAYLOAD',
        reviewDetermination: {
          type: 'REJECTED',
          reason: 'reason',
          officialRefusalLetter: 'official refusal letter',
          shouldFeeBeRefundedToOperator: true,
        } as PermitSurrenderReviewDeterminationReject,
        reviewDeterminationCompleted: true,
      },
    });

    expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: route, state: { notification: true } });
  });
});

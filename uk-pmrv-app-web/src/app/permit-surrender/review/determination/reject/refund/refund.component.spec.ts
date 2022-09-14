import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PermitSurrenderReviewDeterminationReject, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { mockTaskState } from '../../../../testing/mock-state';
import { RefundComponent } from './refund.component';

describe('RefundComponent', () => {
  let component: RefundComponent;
  let fixture: ComponentFixture<RefundComponent>;
  let store: PermitSurrenderStore;
  let page: Page;
  let router: Router;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };
  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, null);

  class Page extends BasePage<RefundComponent> {
    get radios() {
      return this.queryAll<HTMLInputElement>('input[name$="shouldFeeBeRefundedToOperator"]');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('govuk-error-summary');
    }

    get errors() {
      return this.queryAll<HTMLLIElement>('ul.govuk-error-summary__list > li').map((el) => el.textContent.trim());
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(RefundComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [RefundComponent],
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for new refund submit', () => {
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
        } as PermitSurrenderReviewDeterminationReject,
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit form', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errors).toEqual(['Select Yes or No']);
      expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();

      page.radios[0].click();
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
          },
          reviewDeterminationCompleted: false,
        },
      });

      expect(navigateSpy).toHaveBeenCalledWith(['../answers'], { relativeTo: route });
    });
  });

  describe('for editing refund submit', () => {
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

    it('should submit form', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.radios[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
        requestTaskActionType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION',
        requestTaskId: mockTaskState.requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION_PAYLOAD',
          reviewDetermination: {
            ...store.getState().reviewDetermination,
            shouldFeeBeRefundedToOperator: false,
          },
          reviewDeterminationCompleted: false,
        },
      });

      expect(navigateSpy).toHaveBeenCalledWith(['../answers'], { relativeTo: route });
    });
  });
});

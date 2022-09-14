import { Component, Input } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { Observable, of } from 'rxjs';

import { PermitSurrenderReviewDeterminationDeemWithdraw, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { mockTaskState } from '../../../../testing/mock-state';
import { AnswersComponent } from './answers.component';

describe('AnswersComponent', () => {
  @Component({
    selector: 'app-deem-withdraw-determination-summary-details',
    template: '<p>Mock deem details</p>',
  })
  class MockDeemWithdrawSummaryDetailsComponent {
    @Input() deemWithdrawDetermination$: Observable<PermitSurrenderReviewDeterminationDeemWithdraw>;
  }

  let component: AnswersComponent;
  let fixture: ComponentFixture<AnswersComponent>;

  let store: PermitSurrenderStore;
  let page: Page;
  let router: Router;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, null);

  class Page extends BasePage<AnswersComponent> {
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
      declarations: [AnswersComponent, MockDeemWithdrawSummaryDetailsComponent],
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
    store.setState({
      ...mockTaskState,
      reviewDetermination: {
        type: 'DEEMED_WITHDRAWN',
        reason: 'deemed reason',
      },
    });
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should submit form', () => {
    store.setState({
      ...mockTaskState,
      reviewDetermination: {
        type: 'DEEMED_WITHDRAWN',
        reason: 'deemed reason',
      },
    });
    createComponent();

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
          type: 'DEEMED_WITHDRAWN',
          reason: 'deemed reason',
        },
        reviewDeterminationCompleted: true,
      },
    });

    expect(navigateSpy).toHaveBeenCalledWith(['../summary'], { relativeTo: route, state: { notification: true } });
  });
});

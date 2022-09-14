import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PermitSurrenderReviewDeterminationGrant, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { mockTaskState } from '../../../../testing/mock-state';
import { StopDateComponent } from './stop-date.component';

describe('StopDateComponent', () => {
  let component: StopDateComponent;
  let fixture: ComponentFixture<StopDateComponent>;
  let store: PermitSurrenderStore;
  let page: Page;
  let router: Router;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, null);

  class Page extends BasePage<StopDateComponent> {
    get stopDateDay() {
      return this.getInputValue('#stopDate-day');
    }
    set stopDateDay(value: string) {
      this.setInputValue('#stopDate-day', value);
    }

    get stopDateMonth() {
      return this.getInputValue('#stopDate-month');
    }
    set stopDateMonth(value: string) {
      this.setInputValue('#stopDate-month', value);
    }

    get stopDateYear() {
      return this.getInputValue('#stopDate-year');
    }
    set stopDateYear(value: string) {
      this.setInputValue('#stopDate-year', value);
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
    fixture = TestBed.createComponent(StopDateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [StopDateComponent],
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

  it('should render form when stop date exists', () => {
    store.setState({
      ...mockTaskState,
      reviewDetermination: {
        type: 'GRANTED',
        reason: 'fdf',
        stopDate: '2012-12-21',
      } as PermitSurrenderReviewDeterminationGrant,
    });

    createComponent();
    fixture.detectChanges();

    expect(page.stopDateDay.trim()).toEqual('21');
    expect(page.stopDateMonth.trim()).toEqual('12');
    expect(page.stopDateYear.trim()).toEqual('2012');
  });

  it('should validate future date upon submitting', () => {
    store.setState(mockTaskState);
    createComponent();

    page.stopDateDay = '21';
    page.stopDateMonth = '12';
    page.stopDateYear = `${new Date(new Date().setDate(new Date().getDate() + 1)).getFullYear()}`;

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();
  });

  it('should submit upon valid date', () => {
    store.setState(mockTaskState);
    createComponent();

    const navigateSpy = jest.spyOn(router, 'navigate');

    page.stopDateDay = '24';
    page.stopDateMonth = '12';
    page.stopDateYear = `2020`;

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION',
      requestTaskId: mockTaskState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION_PAYLOAD',
        reviewDetermination: { ...mockTaskState.reviewDetermination, stopDate: new Date('2020-12-24') },
        reviewDeterminationCompleted: false,
      },
    });

    expect(navigateSpy).toHaveBeenCalledWith(['../notice-date'], { relativeTo: route });
  });
});

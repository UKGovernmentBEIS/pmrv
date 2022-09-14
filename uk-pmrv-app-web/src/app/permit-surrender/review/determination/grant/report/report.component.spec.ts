import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PermitSurrenderReviewDeterminationGrant, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { mockTaskState } from '../../../../testing/mock-state';
import { ReportComponent } from './report.component';

describe('ReportComponent', () => {
  let component: ReportComponent;
  let fixture: ComponentFixture<ReportComponent>;
  let store: PermitSurrenderStore;
  let page: Page;
  let router: Router;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, null);

  class Page extends BasePage<ReportComponent> {
    get reportRequiredRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="reportRequired"]');
    }

    get reportDateDay() {
      return this.getInputValue('#reportDate-day');
    }
    set reportDateDay(value: string) {
      this.setInputValue('#reportDate-day', value);
    }

    get reportDateMonth() {
      return this.getInputValue('#reportDate-month');
    }
    set reportDateMonth(value: string) {
      this.setInputValue('#reportDate-month', value);
    }

    get reportDateYear() {
      return this.getInputValue('#reportDate-year');
    }
    set reportDateYear(value: string) {
      this.setInputValue('#reportDate-year', value);
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
    fixture = TestBed.createComponent(ReportComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ReportComponent],
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

  it('should render form when data exists', () => {
    store.setState({
      ...mockTaskState,
      reviewDetermination: {
        ...mockTaskState.reviewDetermination,
        reportRequired: true,
        reportDate: '2030-12-21',
      } as PermitSurrenderReviewDeterminationGrant,
    });

    createComponent();
    fixture.detectChanges();

    expect(page.reportDateDay.trim()).toEqual('21');
    expect(page.reportDateMonth.trim()).toEqual('12');
    expect(page.reportDateYear.trim()).toEqual('2030');
  });

  it('should validate future date upon submitting', () => {
    store.setState(mockTaskState);
    createComponent();

    page.reportRequiredRadios[0].click();
    fixture.detectChanges();

    page.reportDateDay = '21';
    page.reportDateMonth = '12';
    page.reportDateYear = `${new Date(new Date().setFullYear(new Date().getFullYear() - 1))}`;

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();
  });

  it('should submit upon report not required', () => {
    store.setState(mockTaskState);
    createComponent();

    const navigateSpy = jest.spyOn(router, 'navigate');

    page.reportRequiredRadios[1].click();
    fixture.detectChanges();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION',
      requestTaskId: mockTaskState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION_PAYLOAD',
        reviewDetermination: {
          ...mockTaskState.reviewDetermination,
          reportRequired: false,
          reportDate: null,
        },
        reviewDeterminationCompleted: false,
      },
    });

    expect(navigateSpy).toHaveBeenCalledWith(['../allowances'], { relativeTo: route });
  });

  it('should submit upon valid date', () => {
    store.setState(mockTaskState);
    createComponent();

    const navigateSpy = jest.spyOn(router, 'navigate');

    page.reportRequiredRadios[0].click();
    fixture.detectChanges();

    page.reportDateDay = '21';
    page.reportDateMonth = '12';
    page.reportDateYear = '2030';

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION',
      requestTaskId: mockTaskState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION_PAYLOAD',
        reviewDetermination: {
          ...mockTaskState.reviewDetermination,
          reportRequired: true,
          reportDate: new Date('2030-12-21'),
        },
        reviewDeterminationCompleted: false,
      },
    });

    expect(navigateSpy).toHaveBeenCalledWith(['../allowances'], { relativeTo: route });
  });
});

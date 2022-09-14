import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../testing';
import { PermitSurrenderModule } from '../../permit-surrender.module';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';
import { mockTaskState } from '../../testing/mock-state';
import { StopDateComponent } from './stop-date.component';

describe('StopDateComponent', () => {
  let component: StopDateComponent;
  let fixture: ComponentFixture<StopDateComponent>;
  let store: PermitSurrenderStore;
  let page: Page;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

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

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, {
    statusKey: 'SURRENDER_APPLY',
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(StopDateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, PermitSurrenderModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitSurrenderStore);
  });

  afterEach(() => jest.clearAllMocks());

  beforeEach(() => {
    store.setState({
      ...store.getState(),
      requestTaskId: mockTaskState.requestTaskId,
      isEditable: true,
      permitSurrender: null,
      sectionsCompleted: {},
    });
  });

  it('should create', () => {
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should render empty form when surrender is empty', () => {
    createComponent();
    expect(page.stopDateDay.trim()).toBeFalsy();
    expect(page.stopDateMonth.trim()).toBeFalsy();
    expect(page.stopDateYear.trim()).toBeFalsy();
  });

  it('should render form when surrender exists', () => {
    store.setState({
      ...store.getState(),
      isEditable: true,
      permitSurrender: {
        stopDate: '2012-12-21',
        justification: 'justify',
        documentsExist: true,
        documents: ['2c30c8bf-3d5e-474d-98a0-123a87eb60dd'],
      },
      sectionsCompleted: {
        SURRENDER_APPLY: true,
      },
    });

    createComponent();
    fixture.detectChanges();

    expect(page.stopDateDay.trim()).toEqual('21');
    expect(page.stopDateMonth.trim()).toEqual('12');
    expect(page.stopDateYear.trim()).toEqual('2012');
  });

  it('should validate stop date upon submitting', () => {
    createComponent();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual([
      'Enter the date regulated activities at the installation stopped',
    ]);
    expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();
  });

  it('should validate future date upon submitting', () => {
    createComponent();

    page.stopDateDay = '21';
    page.stopDateMonth = '12';
    page.stopDateYear = `${new Date(new Date().setFullYear(new Date().getFullYear() + 1)).getFullYear()}`;

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.length).toEqual(1);
    expect(page.errors[0].textContent.trim()).toContain('This date must be the same as or before');
    expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();
  });

  it('should validate future date upon submitting', () => {
    createComponent();

    page.stopDateDay = '21';
    page.stopDateMonth = '12';
    page.stopDateYear = `${new Date(new Date().setFullYear(new Date().getFullYear() + 1)).getFullYear()}`;

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.length).toEqual(1);
    expect(page.errors[0].textContent.trim()).toContain('This date must be the same as or before');
    expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();
  });

  it('should submit upon valid date', () => {
    createComponent();

    page.stopDateDay = '21';
    page.stopDateMonth = '12';
    page.stopDateYear = `2020`;

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_SURRENDER_SAVE_APPLICATION',
      requestTaskId: mockTaskState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_SURRENDER_SAVE_APPLICATION_PAYLOAD',
        permitSurrender: {
          stopDate: new Date('2020-12-21'),
        },
        sectionsCompleted: {
          SURRENDER_APPLY: false,
        },
      },
    });
  });
});

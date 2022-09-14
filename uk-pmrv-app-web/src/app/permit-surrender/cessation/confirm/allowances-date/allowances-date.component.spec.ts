import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { PermitSurrenderStore } from '../../../store/permit-surrender.store';
import { mockTaskState } from '../../testing/mock-state';
import { AllowancesDateComponent } from './allowances-date.component';

describe('AllowancesDateComponent', () => {
  let component: AllowancesDateComponent;
  let fixture: ComponentFixture<AllowancesDateComponent>;
  let store: PermitSurrenderStore;
  let page: Page;
  let router: Router;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, null);

  class Page extends BasePage<AllowancesDateComponent> {
    get allowancesSurrenderDateDay() {
      return this.getInputValue('#allowancesSurrenderDate-day');
    }
    set allowancesSurrenderDateDay(value: string) {
      this.setInputValue('#allowancesSurrenderDate-day', value);
    }

    get allowancesSurrenderDateMonth() {
      return this.getInputValue('#allowancesSurrenderDate-month');
    }
    set allowancesSurrenderDateMonth(value: string) {
      this.setInputValue('#allowancesSurrenderDate-month', value);
    }

    get allowancesSurrenderDateYear() {
      return this.getInputValue('#allowancesSurrenderDate-year');
    }
    set allowancesSurrenderDateYear(value: string) {
      this.setInputValue('#allowancesSurrenderDate-year', value);
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
    fixture = TestBed.createComponent(AllowancesDateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AllowancesDateComponent],
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

  it('should render form when allowances date exists', () => {
    store.setState(mockTaskState);

    createComponent();
    fixture.detectChanges();

    expect(page.allowancesSurrenderDateDay.trim()).toEqual('13');
    expect(page.allowancesSurrenderDateMonth.trim()).toEqual('12');
    expect(page.allowancesSurrenderDateYear.trim()).toEqual('2012');
  });

  it('should validate future date upon submitting', () => {
    store.setState(mockTaskState);
    createComponent();

    page.allowancesSurrenderDateDay = '21';
    page.allowancesSurrenderDateMonth = '12';
    page.allowancesSurrenderDateYear = `${new Date(new Date().setDate(new Date().getDate() + 1)).getFullYear()}`;

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();
  });

  it('should submit upon valid date', () => {
    store.setState(mockTaskState);
    createComponent();

    const navigateSpy = jest.spyOn(router, 'navigate');

    page.allowancesSurrenderDateDay = '24';
    page.allowancesSurrenderDateMonth = '12';
    page.allowancesSurrenderDateYear = `2020`;

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_SURRENDER_SAVE_CESSATION',
      requestTaskId: mockTaskState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_SURRENDER_SAVE_CESSATION_PAYLOAD',
        cessation: { ...mockTaskState.cessation, allowancesSurrenderDate: new Date('2020-12-24') },
        cessationCompleted: false,
      },
    });

    expect(navigateSpy).toHaveBeenCalledWith(['../allowances-number'], { relativeTo: route });
  });
});

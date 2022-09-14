import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CessationModule } from '@permit-revocation/cessation/cessation.module';
import { PermitRevocationModule } from '@permit-revocation/permit-revocation.module';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import moment from 'moment';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../testing';
import { mockTaskState } from '../../testing/mock-state';
import { FeeComponent } from './fee.component';

describe('Fee Component', () => {
  let component: FeeComponent;
  let fixture: ComponentFixture<FeeComponent>;
  let store: PermitRevocationStore;
  let page: Page;
  let router: Router;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<FeeComponent> {
    set radioOption(value: boolean) {
      this.setInputValue('#feeCharged-option0', value);
    }

    get radioOption() {
      return this.getInputValue('#feeCharged-option0');
    }

    set radioOption1(value: boolean) {
      this.setInputValue('#feeCharged-option1', value);
    }

    get radioOption1() {
      return this.getInputValue('#feeCharged-option1');
    }

    get radioBtnOption() {
      return this.query<HTMLFormElement>('input[type="radio"]#feeCharged-option0');
    }

    get radioBtnOption1() {
      return this.query<HTMLButtonElement>('input[type="radio"]#feeCharged-option1');
    }

    get feeDateDay() {
      return this.getInputValue('#feeDate-day');
    }
    set feeDateDay(value: string) {
      this.setInputValue('#feeDate-day', value);
    }

    get feeDateMonth() {
      return this.getInputValue('#feeDate-month');
    }
    set feeDateMonth(value: string) {
      this.setInputValue('#feeDate-month', value);
    }

    get feeDateYear() {
      return this.getInputValue('#feeDate-year');
    }
    set feeDateYear(value: string) {
      this.setInputValue('#feeDate-year', value);
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
    statusKey: 'REVOCATION_APPLY',
    pageTitle: 'Do you need to charge the operator a fee?',
    keys: ['feeCharged', 'feeDate', 'feeDetails'],
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(FeeComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  const effectiveDate = (format: string, days: number): string => {
    const govukDatePipe = new GovukDatePipe();
    const add28Days = moment().add(days, 'd');
    const effectiveDate = govukDatePipe.transform(add28Days.toISOString(), 'date');

    return effectiveDate;
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, PermitRevocationModule, CessationModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
    store = TestBed.inject(PermitRevocationStore);
    router = TestBed.inject(Router);
  });

  afterEach(() => jest.clearAllMocks());

  beforeEach(() => {
    store.setState({
      ...store.getState(),
      requestTaskId: mockTaskState.requestTaskId,
      isEditable: true,
      permitRevocation: {
        reason: 'some reason',
        activitiesStopped: false,
        stoppedDate: null,
        effectiveDate: effectiveDate('YYYY-MM-DD', 28),
        surrenderRequired: false,
        surrenderDate: null,
        feeCharged: null,
        annualEmissionsReportRequired: null,
      },
      sectionsCompleted: {},
    });
  });

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render form', () => {
    expect(component.form.get('feeCharged').value).toEqual(null);
  });

  it('should validate form and display an error message', () => {
    page.submitButton.click();
    fixture.detectChanges();
    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual(['Select Yes or No']);
    expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();
  });

  it('should validate form by selecting "No" and navigate to next step', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    page.radioBtnOption1.click();
    fixture.detectChanges();
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'answers'], { relativeTo: route });
  });

  it('should validate form when a user selects option "Yes" without completing all the required fields', () => {
    page.radioBtnOption.click();
    fixture.detectChanges();
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual([
      `The payment due date must be after ${effectiveDate('D MMM YYYY', 28)}`,
      'Enter the date',
      'Explain why payment is required',
    ]);
    expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();
  });

  it('should validate form with an error message for minimum date limit', () => {
    const today = moment().add(26, 'd').format('YYYY-MM-DD');
    const date = today.split('-');
    const year = date[0];
    const month = date[1];
    const days = date[2];

    page.radioBtnOption.click();
    fixture.detectChanges();

    page.feeDateYear = year;
    page.feeDateMonth = month;
    page.feeDateDay = days;
    component.form.get('feeDetails').setValue('fee details');

    page.submitButton.click();
    fixture.detectChanges();
    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual([
      `The payment due date must be after ${effectiveDate('D MMM YYYY', 28)}`,
    ]);
    expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();
  });

  it('Should submit form and navigate to the next step', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const today = moment().add(29, 'd').format('YYYY-MM-DD');
    const date = today.split('-');
    const year = date[0];
    const month = date[1];
    const days = date[2];

    page.radioBtnOption.click();
    fixture.detectChanges();

    page.feeDateYear = year;
    page.feeDateMonth = month;
    page.feeDateDay = days;
    component.form.get('feeDetails').setValue('fee details');

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'answers'], { relativeTo: route });
  });
});

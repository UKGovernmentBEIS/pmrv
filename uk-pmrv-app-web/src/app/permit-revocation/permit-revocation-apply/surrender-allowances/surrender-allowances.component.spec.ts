import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CessationModule } from '@permit-revocation/cessation/cessation.module';
import { PermitRevocationModule } from '@permit-revocation/permit-revocation.module';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import moment from 'moment';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../testing';
import { mockTaskState } from '../../testing/mock-state';
import { SurrenderAllowancesComponent } from './surrender-allowances.component';

describe('Surrender Allowances Component', () => {
  let component: SurrenderAllowancesComponent;
  let fixture: ComponentFixture<SurrenderAllowancesComponent>;
  let store: PermitRevocationStore;
  let page: Page;
  let router: Router;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<SurrenderAllowancesComponent> {
    set radioOption(value: boolean) {
      this.setInputValue('#surrenderRequired-option0', value);
    }

    get radioOption() {
      return this.getInputValue('#surrenderRequired-option0');
    }

    set radioOption1(value: boolean) {
      this.setInputValue('#surrenderRequired-option1', value);
    }

    get radioOption1() {
      return this.getInputValue('#surrenderRequired-option1');
    }

    get radioBtnOption() {
      return this.query<HTMLFormElement>('input[type="radio"]#surrenderRequired-option0');
    }

    get radioBtnOption1() {
      return this.query<HTMLButtonElement>('input[type="radio"]#surrenderRequired-option1');
    }

    get surrenderDateDay() {
      return this.getInputValue('#surrenderDate-day');
    }
    set surrenderDateDay(value: string) {
      this.setInputValue('#surrenderDate-day', value);
    }

    get surrenderDateMonth() {
      return this.getInputValue('#surrenderDate-month');
    }
    set surrenderDateMonth(value: string) {
      this.setInputValue('#surrenderDate-month', value);
    }

    get surrenderDateYear() {
      return this.getInputValue('#surrenderDate-year');
    }
    set surrenderDateYear(value: string) {
      this.setInputValue('#surrenderDate-year', value);
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
    pageTitle: 'Is a surrender of allowances required?',
    keys: ['surrenderDate', 'surrenderRequired'],
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(SurrenderAllowancesComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
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
        effectiveDate: new Date().toISOString(),
        surrenderRequired: null,
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
    expect(component.form.get('surrenderRequired').value).toEqual(null);
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
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'fee'], { relativeTo: route });
  });

  it('should validate form when a user selects option "Yes" without completing all the required fields', () => {
    page.radioBtnOption.click();
    fixture.detectChanges();
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual([
      'Enter the date when the surrender of allowances is required',
    ]);
    expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();
  });

  it('should validate form with an error message for minimum date limit', () => {
    const today = moment().subtract(1, 'd').format('YYYY-MM-DD');
    const date = today.split('-');
    const year = date[0];
    const month = date[1];
    const days = date[2];

    page.radioBtnOption.click();
    fixture.detectChanges();

    page.surrenderDateYear = year;
    page.surrenderDateMonth = month;
    page.surrenderDateDay = days;

    page.submitButton.click();
    fixture.detectChanges();
    const dateForErrorMsg = moment().format('D MMMM YYYY');
    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual([
      `This date must be the same as or after ${dateForErrorMsg}`,
    ]);
    expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();
  });

  it('Should submit form and navigate to the next step', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const today = moment().format('YYYY-MM-DD');
    const date = today.split('-');
    const year = date[0];
    const month = date[1];
    const days = date[2];

    page.radioBtnOption.click();
    fixture.detectChanges();

    page.surrenderDateYear = year;
    page.surrenderDateMonth = month;
    page.surrenderDateDay = days;

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'fee'], { relativeTo: route });
  });
});

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
import { StopDateComponent } from './stop-date.component';

describe('Stop Date Component', () => {
  let component: StopDateComponent;
  let fixture: ComponentFixture<StopDateComponent>;
  let store: PermitRevocationStore;
  let page: Page;
  let router: Router;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<StopDateComponent> {
    set radioOption(value: boolean) {
      this.setInputValue('#activitiesStopped-option0', value);
    }

    get radioOption() {
      return this.getInputValue('#activitiesStopped-option0');
    }

    set radioOption1(value: boolean) {
      this.setInputValue('#activitiesStopped-option1', value);
    }

    get radioOption1() {
      return this.getInputValue('#activitiesStopped-option1');
    }

    get radioBtnOption() {
      return this.query<HTMLFormElement>('input[type="radio"]#activitiesStopped-option0');
    }

    get radioBtnOption1() {
      return this.query<HTMLButtonElement>('input[type="radio"]#activitiesStopped-option1');
    }

    get stoppedDateDay() {
      return this.getInputValue('#stoppedDate-day');
    }
    set stoppedDateDay(value: string) {
      this.setInputValue('#stoppedDate-day', value);
    }

    get stoppedDateMonth() {
      return this.getInputValue('#stoppedDate-month');
    }
    set stoppedDateMonth(value: string) {
      this.setInputValue('#stoppedDate-month', value);
    }

    get stoppedDateYear() {
      return this.getInputValue('#stoppedDate-year');
    }
    set stoppedDateYear(value: string) {
      this.setInputValue('#stoppedDate-year', value);
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
    pageTitle: 'Have the regulated activities at the installation stopped?',
    keys: ['activitiesStopped', 'stoppedDate'],
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(StopDateComponent);
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
        activitiesStopped: null,
        effectiveDate: null,
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
    expect(component.form.get('activitiesStopped').value).toEqual(null);
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
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'notice'], { relativeTo: route });
  });

  it('should validate form when a user selects option "Yes" without completing all the required fields', () => {
    page.radioBtnOption.click();
    fixture.detectChanges();
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual([
      'Enter the date when regulated activities stopped',
    ]);
    expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();
  });

  it('should validate form with an error message for maximum date limit', () => {
    const today = moment().add(1, 'd').set({ hour: 0, minute: 0, second: 0, millisecond: 0 }).format('YYYY-MM-DD');
    const date = today.split('-');
    const year = date[0];
    const month = date[1];
    const days = date[2];

    page.radioBtnOption.click();
    fixture.detectChanges();

    page.stoppedDateYear = year;
    page.stoppedDateMonth = month;
    page.stoppedDateDay = days;

    page.submitButton.click();
    fixture.detectChanges();
    const dateForErrorMsg = moment().subtract(1, 'days').format('D MMMM YYYY');
    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual([
      `This date must be the same as or before ${dateForErrorMsg}`,
    ]);
    expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();
  });

  it('Should submit form and navigate to the next step', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const today = moment().subtract(1, 'd').format('YYYY-MM-DD');
    const date = today.split('-');
    const year = date[0];
    const month = date[1];
    const days = date[2];

    page.radioBtnOption.click();
    fixture.detectChanges();

    page.stoppedDateYear = year;
    page.stoppedDateMonth = month;
    page.stoppedDateDay = days;

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'notice'], { relativeTo: route });
  });
});

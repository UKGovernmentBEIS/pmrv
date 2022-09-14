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
import { NoticeComponent } from './notice.component';

describe('Notice Component', () => {
  let component: NoticeComponent;
  let fixture: ComponentFixture<NoticeComponent>;
  let store: PermitRevocationStore;
  let page: Page;
  let router: Router;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<NoticeComponent> {
    get effectiveDateDay() {
      return this.getInputValue('#effectiveDate-day');
    }
    set effectiveDateDay(value: string) {
      this.setInputValue('#effectiveDate-day', value);
    }

    get effectiveDateMonth() {
      return this.getInputValue('#effectiveDate-month');
    }
    set effectiveDateMonth(value: string) {
      this.setInputValue('#effectiveDate-month', value);
    }

    get effectiveDateYear() {
      return this.getInputValue('#effectiveDate-year');
    }
    set effectiveDateYear(value: string) {
      this.setInputValue('#effectiveDate-year', value);
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
    pageTitle: 'Set the effective date of the permit revocation notice',
    keys: ['effectiveDate'],
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(NoticeComponent);
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
    });
  });

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render form', () => {
    expect(component.form.get('effectiveDate').value).toEqual(null);
  });

  it('should validate form and display an error message', () => {
    page.submitButton.click();
    fixture.detectChanges();
    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual([
      'The effective date of the notice must be at least 28 days after today',
      'Enter the effective date of the notice',
    ]);
    expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();
  });

  it('should validate form with an error message for minimum date limit', () => {
    const today = moment().add(27, 'd').format('YYYY-MM-DD');
    const date = today.split('-');
    const year = date[0];
    const month = date[1];
    const days = date[2];

    page.effectiveDateYear = year;
    page.effectiveDateMonth = month;
    page.effectiveDateDay = days;

    page.submitButton.click();
    fixture.detectChanges();
    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual([
      'The effective date of the notice must be at least 28 days after today',
    ]);
    expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();
  });

  it('Should submit form and navigate to the next step', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const today = moment().add(29, 'd').utc(true).format('YYYY-MM-DD');
    const date = today.split('-');
    const year = date[0];
    const month = date[1];
    const days = date[2];

    store.setState(mockTaskState);

    page.effectiveDateYear = year;
    page.effectiveDateMonth = month;
    page.effectiveDateDay = days;

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_REVOCATION_SAVE_APPLICATION',
      requestTaskId: mockTaskState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_REVOCATION_SAVE_APPLICATION_PAYLOAD',
        permitRevocation: {
          ...mockTaskState.permitRevocation,
          effectiveDate: new Date(component.form.get('effectiveDate').value),
        },
        sectionsCompleted: { REVOCATION_APPLY: false },
      },
    });
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'report'], { relativeTo: route });
  });
});

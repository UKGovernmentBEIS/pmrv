import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CessationModule } from '@permit-revocation/cessation/cessation.module';
import { PermitRevocationModule } from '@permit-revocation/permit-revocation.module';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../testing';
import { mockTaskState } from '../../testing/mock-state';
import { ReasonComponent } from './reason.component';

describe('ReasonComponent', () => {
  let component: ReasonComponent;
  let fixture: ComponentFixture<ReasonComponent>;
  let store: PermitRevocationStore;
  let page: Page;
  let router: Router;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<ReasonComponent> {
    get reason() {
      return this.getInputValue('#reason');
    }

    set reason(value: string) {
      this.setInputValue('#reason', value);
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
    pageTitle: 'Explain why the permit is being revoked',
    keys: ['reason'],
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(ReasonComponent);
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
        reason: null,
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
    expect(page.reason).toBeFalsy();
  });

  it('should render form with a value', () => {
    component.form.get('reason').setValue('some reason');

    expect(page.reason).toEqual('some reason');
  });

  it('should validate form and display an error message', () => {
    page.submitButton.click();
    fixture.detectChanges();
    expect(page.errorSummary).toBeTruthy();
    expect(page.errors.map((error) => error.textContent.trim())).toEqual([
      'Explain the reason why the permit is being revoked',
    ]);
    expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();
  });

  it('Should submit form and navigate to the next step', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    page.reason = 'some reason';
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['..', 'stop-date'], { relativeTo: route });
  });
});

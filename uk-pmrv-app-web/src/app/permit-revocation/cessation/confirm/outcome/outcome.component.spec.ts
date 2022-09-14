import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { PermitRevocationStore } from '../../../store/permit-revocation-store';
import { mockTaskState } from '../testing/mock-state';
import { OutcomeComponent } from './outcome.component';

describe('OutcomeComponent', () => {
  let component: OutcomeComponent;
  let fixture: ComponentFixture<OutcomeComponent>;
  let store: PermitRevocationStore;
  let page: Page;
  let router: Router;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, {
    statusKey: 'REVOCATION_CESSATION',
    pageTitle: 'Revocation cessation confirm - outcome',
    keys: ['determinationOutcome'],
  });

  class Page extends BasePage<OutcomeComponent> {
    get determinationOutcomeRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="determinationOutcome"]');
    }

    get continueButton() {
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
    fixture = TestBed.createComponent(OutcomeComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OutcomeComponent],
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitRevocationStore);
  });

  afterEach(() => jest.clearAllMocks());

  it('should create', () => {
    store.setState({ ...mockTaskState, cessation: undefined });
    createComponent();
    expect(component).toBeTruthy();
    expect(page.determinationOutcomeRadios.length).toEqual(2);
    expect(page.determinationOutcomeRadios[0].checked).toBeFalsy();
    expect(page.determinationOutcomeRadios[1].checked).toBeFalsy();
  });

  it('should render form when outcome exists', () => {
    store.setState(mockTaskState);

    createComponent();
    fixture.detectChanges();

    expect(page.determinationOutcomeRadios.length).toEqual(2);
    expect(page.determinationOutcomeRadios[0].checked).toBeTruthy();
    expect(page.determinationOutcomeRadios[1].checked).toBeFalsy();
  });

  it('should submit a valid form and navigate correctly', () => {
    store.setState(mockTaskState);
    createComponent();
    const navigateSpy = jest.spyOn(router, 'navigate');
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(page.determinationOutcomeRadios[0].checked).toBeTruthy();
    expect(page.determinationOutcomeRadios[1].checked).toBeFalsy();

    page.determinationOutcomeRadios[1].click();
    page.continueButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_REVOCATION_SAVE_CESSATION',
      requestTaskId: mockTaskState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_REVOCATION_SAVE_CESSATION_PAYLOAD',
        cessation: { ...mockTaskState.cessation, determinationOutcome: 'REJECTED' },
        cessationCompleted: false,
      },
    });

    expect(navigateSpy).toHaveBeenCalledWith(['../allowances-date'], { relativeTo: route });
  });
});

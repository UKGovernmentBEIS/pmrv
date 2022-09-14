import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { PermitRevocationStore } from '../../../store/permit-revocation-store';
import { mockTaskState } from '../testing/mock-state';
import { EmissionsComponent } from './emissions.component';

describe('EmissionsComponent', () => {
  let component: EmissionsComponent;
  let fixture: ComponentFixture<EmissionsComponent>;
  let store: PermitRevocationStore;
  let page: Page;
  let router: Router;

  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, {
    statusKey: 'REVOCATION_CESSATION',
    pageTitle: 'Revocation cessation confirm - emissions',
    keys: ['annualReportableEmissions'],
  });

  class Page extends BasePage<EmissionsComponent> {
    get annualReportableEmissions() {
      return this.getInputValue('#annualReportableEmissions');
    }

    set annualReportableEmissions(value: string) {
      this.setInputValue('#annualReportableEmissions', value);
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
    fixture = TestBed.createComponent(EmissionsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EmissionsComponent],
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
    store.setState(mockTaskState);
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should render form when notes exists', () => {
    store.setState(mockTaskState);

    createComponent();
    fixture.detectChanges();

    expect(+page.annualReportableEmissions).toEqual(mockTaskState.cessation.annualReportableEmissions);
  });

  it('should submit a valid form and navigate correctly', () => {
    store.setState(mockTaskState);
    createComponent();
    const navigateSpy = jest.spyOn(router, 'navigate');
    fixture.detectChanges();

    page.annualReportableEmissions = '20.45';

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PERMIT_REVOCATION_SAVE_CESSATION',
      requestTaskId: mockTaskState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'PERMIT_REVOCATION_SAVE_CESSATION_PAYLOAD',
        cessation: { ...mockTaskState.cessation, annualReportableEmissions: 20.45 },
        cessationCompleted: false,
      },
    });

    expect(navigateSpy).toHaveBeenCalledWith(['../refund'], { relativeTo: route });
  });
});

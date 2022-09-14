import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CalculationMonitoringApproach, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockPostBuild, mockState, mockStateBuild } from '../../../testing/mock-state';
import { CalculationModule } from '../calculation.module';
import { SamplingPlanComponent } from './sampling-plan.component';

describe('SamplingPlanComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore;
  let component: SamplingPlanComponent;
  let fixture: ComponentFixture<SamplingPlanComponent>;

  const tasksService = mockClass(TasksService);
  const mockSamplingPlan = (mockState.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach)
    .samplingPlan;
  const route = new ActivatedRouteStub(
    {},
    {},
    {
      taskKey: 'monitoringApproaches.CALCULATION.samplingPlan',
      statusKey: 'CALCULATION_Plan',
    },
  );

  class Page extends BasePage<SamplingPlanComponent> {
    get submitButton(): HTMLButtonElement {
      return this.query('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryErrorList() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }

    get existRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="exist"]');
    }

    get description(): string {
      return this.getInputValue('#analysis.procedureDescription');
    }
    set description(value: string) {
      this.setInputValue('#analysis.procedureDescription', value);
    }

    get name(): string {
      return this.getInputValue('#analysis.procedureDocumentName');
    }
    set name(value: string) {
      this.setInputValue('#analysis.procedureDocumentName', value);
    }

    get reference(): string {
      return this.getInputValue('#analysis.procedureReference');
    }
    set reference(value: string) {
      this.setInputValue('#analysis.procedureReference', value);
    }

    get department(): string {
      return this.getInputValue('#analysis.responsibleDepartmentOrRole');
    }
    set department(value: string) {
      this.setInputValue('#analysis.responsibleDepartmentOrRole', value);
    }

    get location(): string {
      return this.getInputValue('#analysis.locationOfRecords');
    }
    set location(value: string) {
      this.setInputValue('#analysis.locationOfRecords', value);
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(SamplingPlanComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalculationModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for new sampling plan analysis', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          monitoringApproaches: {
            CALCULATION: {
              type: 'CALCULATION',
            },
          },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to next step of wizard', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select Yes or No']);

      page.existRadios[0].click();
      fixture.detectChanges();

      expect(page.errorSummaryErrorList).toEqual([
        'Enter a brief description of the procedure',
        'Enter the name of the procedure document',
        'Enter a procedure reference',
        'Enter the name of the department or role responsible',
        'Enter the location of the records',
      ]);

      page.description = mockSamplingPlan.details.analysis.procedureDescription;
      page.name = mockSamplingPlan.details.analysis.procedureDocumentName;
      page.reference = mockSamplingPlan.details.analysis.procedureReference;
      page.department = mockSamplingPlan.details.analysis.responsibleDepartmentOrRole;
      page.location = mockSamplingPlan.details.analysis.locationOfRecords;

      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproaches: {
              CALCULATION: {
                type: 'CALCULATION',
                samplingPlan: {
                  exist: true,
                  details: {
                    analysis: {
                      appliedStandards: null,
                      diagramReference: null,
                      itSystemUsed: null,
                      procedureDescription: mockSamplingPlan.details.analysis.procedureDescription,
                      procedureDocumentName: mockSamplingPlan.details.analysis.procedureDocumentName,
                      procedureReference: mockSamplingPlan.details.analysis.procedureReference,
                      responsibleDepartmentOrRole: mockSamplingPlan.details.analysis.responsibleDepartmentOrRole,
                      locationOfRecords: mockSamplingPlan.details.analysis.locationOfRecords,
                    },
                  },
                },
              },
            },
          },
          {
            CALCULATION_Plan: [false],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['plan'], { relativeTo: route });
    });
  });

  describe('for changing sampling plan analysis', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should fill form from store', () => {
      expect(page.description).toEqual(mockSamplingPlan.details.analysis.procedureDescription);
      expect(page.name).toEqual(mockSamplingPlan.details.analysis.procedureDocumentName);
      expect(page.reference).toEqual(mockSamplingPlan.details.analysis.procedureReference);
      expect(page.department).toEqual(mockSamplingPlan.details.analysis.responsibleDepartmentOrRole);
      expect(page.location).toEqual(mockSamplingPlan.details.analysis.locationOfRecords);
    });

    it('should post a valid form and navigate back to answers', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

      page.description = 'Edited description';
      page.submitButton.click();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproaches: {
              ...mockState.permit.monitoringApproaches,
              CALCULATION: {
                ...mockState.permit.monitoringApproaches.CALCULATION,
                samplingPlan: {
                  ...mockSamplingPlan,
                  details: {
                    ...mockSamplingPlan.details,
                    analysis: {
                      ...mockSamplingPlan.details.analysis,
                      procedureDescription: 'Edited description',
                    },
                  },
                },
              },
            },
          },
          {
            CALCULATION_Plan: [false],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['plan'], { relativeTo: route });
    });

    it('should not post if not dirty', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.submitButton.click();

      expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['plan'], { relativeTo: route });
    });
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CalculationMonitoringApproach, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPostBuild, mockState, mockStateBuild } from '../../../../testing/mock-state';
import { CalculationModule } from '../../calculation.module';
import { AppropriatenessComponent } from './appropriateness.component';

describe('AppropriatenessComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore;
  let component: AppropriatenessComponent;
  let fixture: ComponentFixture<AppropriatenessComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub(
    {},
    {},
    { taskKey: 'monitoringApproaches.CALCULATION.samplingPlan.details', statusKey: 'CALCULATION_Plan' },
  );

  class Page extends BasePage<AppropriatenessComponent> {
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

    get description(): string {
      return this.getInputValue('#procedureDescription');
    }
    set description(value: string) {
      this.setInputValue('#procedureDescription', value);
    }

    get name(): string {
      return this.getInputValue('#procedureDocumentName');
    }
    set name(value: string) {
      this.setInputValue('#procedureDocumentName', value);
    }

    get reference(): string {
      return this.getInputValue('#procedureReference');
    }
    set reference(value: string) {
      this.setInputValue('#procedureReference', value);
    }

    get department(): string {
      return this.getInputValue('#responsibleDepartmentOrRole');
    }
    set department(value: string) {
      this.setInputValue('#responsibleDepartmentOrRole', value);
    }

    get location(): string {
      return this.getInputValue('#locationOfRecords');
    }
    set location(value: string) {
      this.setInputValue('#locationOfRecords', value);
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AppropriatenessComponent);
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

  describe('for new appropriateness', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          monitoringApproaches: {
            CALCULATION: {
              ...mockState.permit.monitoringApproaches.CALCULATION,
              samplingPlan: {
                exist: true,
                details: {},
              },
            },
          },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual([
        'Enter a brief description of the procedure',
        'Enter the name of the procedure document',
        'Enter a procedure reference',
        'Enter the name of the department or role responsible',
        'Enter the location of the records',
      ]);

      page.description = 'Appropriateness description';
      page.name = 'Appropriateness name';
      page.reference = 'Appropriateness reference';
      page.department = 'Appropriateness department';
      page.location = 'Appropriateness location';

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
                ...mockState.permit.monitoringApproaches.CALCULATION,
                samplingPlan: {
                  exist: true,
                  details: {
                    appropriateness: {
                      appliedStandards: null,
                      diagramReference: null,
                      itSystemUsed: null,
                      procedureDescription: 'Appropriateness description',
                      procedureDocumentName: 'Appropriateness name',
                      procedureReference: 'Appropriateness reference',
                      responsibleDepartmentOrRole: 'Appropriateness department',
                      locationOfRecords: 'Appropriateness location',
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
      expect(navigateSpy).toHaveBeenCalledWith(['../reconciliation'], { relativeTo: route });
    });
  });

  describe('for changing appropriateness', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should fill form from store', () => {
      const mockPlan = (mockState.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach)
        .samplingPlan;
      expect(page.description).toEqual(mockPlan.details.appropriateness.procedureDescription);
      expect(page.name).toEqual(mockPlan.details.appropriateness.procedureDocumentName);
      expect(page.reference).toEqual(mockPlan.details.appropriateness.procedureReference);
      expect(page.department).toEqual(mockPlan.details.appropriateness.responsibleDepartmentOrRole);
      expect(page.location).toEqual(mockPlan.details.appropriateness.locationOfRecords);
    });

    it('should post a valid form and navigate back to answers', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const mockPlan = (mockState.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach)
        .samplingPlan;

      page.description = 'Appropriateness edited description';
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
                  ...mockPlan,
                  details: {
                    ...mockPlan.details,
                    appropriateness: {
                      ...mockPlan.details.appropriateness,
                      procedureDescription: 'Appropriateness edited description',
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
      expect(navigateSpy).toHaveBeenCalledWith(['../reconciliation'], { relativeTo: route });
    });

    it('should not post if not dirty', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.submitButton.click();

      expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../reconciliation'], { relativeTo: route });
    });

    it('should navigate to reconsiliation if not complete', () => {
      const mockPlan = (mockState.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach)
        .samplingPlan;
      store.setState(
        mockStateBuild({
          monitoringApproaches: {
            CALCULATION: {
              ...mockState.permit.monitoringApproaches.CALCULATION,
              samplingPlan: {
                ...mockPlan,
                details: {
                  procedurePlan: mockPlan.details.procedurePlan,
                  appropriateness: mockPlan.details.appropriateness,
                },
              },
            },
          },
        }),
      );
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.submitButton.click();

      expect(tasksService.processRequestTaskActionUsingPOST).not.toHaveBeenCalled();
      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['../reconciliation'], { relativeTo: route });
    });
  });
});

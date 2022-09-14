import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CalculationMonitoringApproach, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../../../../testing/mock-state';
import { CalculationModule } from '../../calculation.module';
import { AnalysisMethodComponent } from './analysis-method.component';

describe('AnalysisMethodComponent', () => {
  let store: PermitApplicationStore;
  let component: AnalysisMethodComponent;
  let fixture: ComponentFixture<AnalysisMethodComponent>;
  let page: Page;
  let router: Router;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' }, null, {
    taskKey: 'monitoringApproaches.CALCULATION.sourceStreamCategoryAppliedTiers',
    statusKey: 'CALCULATION_Emission_Factor',
  });

  const sourceStreamCategory = (
    mockPermitApplyPayload.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach
  ).sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;

  class Page extends BasePage<AnalysisMethodComponent> {
    get frequencyMeetsMinRequirementsRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="frequencyMeetsMinRequirements"]');
    }
    get laboratoryAccreditedRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="laboratoryAccredited"]');
    }
    get analysis() {
      return this.getInputValue('#analysis');
    }
    set analysis(value: string) {
      this.setInputValue('#analysis', value);
    }
    get samplingFrequency(): string {
      return this.query('select').value;
    }
    set samplingFrequency(value: string) {
      this.setInputValue('select', value);
    }
    get laboratoryName(): string {
      return this.getInputValue('#laboratoryName');
    }
    set laboratoryName(value: string) {
      this.setInputValue('#laboratoryName', value);
    }

    get continueButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryErrorList() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AnalysisMethodComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalculationModule, RouterTestingModule, SharedModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for new analysis', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              CALCULATION: {
                type: 'CALCULATION',
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory,
                    emissionFactor: {
                      exist: true,
                      oneThirdRule: false,
                      tier: 'TIER_3',
                    },
                  },
                ],
              },
            },
          },
          {
            CALCULATION_Emission_Factor: [false],
          },
        ),
      );
    });

    beforeEach(() => {
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual([
        'Enter a method of analysis',
        'Select a sampling frequency',
        'Select Yes or No',
        'Enter laboratory name',
        'Select Yes or No',
      ]);

      page.analysis = 'analyze';
      page.samplingFrequency = 'WEEKLY';
      page.frequencyMeetsMinRequirementsRadios[1].click();
      page.laboratoryAccreditedRadios[1].click();
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Enter laboratory name']);

      page.laboratoryName = 'lab';
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproaches: {
              CALCULATION: {
                type: 'CALCULATION',
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory,
                    emissionFactor: {
                      exist: true,
                      oneThirdRule: false,
                      tier: 'TIER_3',
                      defaultValueApplied: undefined,
                      analysisMethodUsed: true,
                      analysisMethods: [
                        {
                          analysis: 'analyze',
                          files: [],
                          frequencyMeetsMinRequirements: false,
                          laboratoryAccredited: false,
                          laboratoryName: 'lab',
                          samplingFrequency: 'WEEKLY',
                          subParameter: null,
                        },
                      ],
                    },
                  },
                ],
              },
            },
          },
          {
            ...mockState.permitSectionsCompleted,
            CALCULATION_Emission_Factor: [false],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['./sampling-justification'], { relativeTo: route });
    });
  });

  describe('for existing analysis', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              CALCULATION: {
                type: 'CALCULATION',
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory,
                    emissionFactor: {
                      exist: true,
                      oneThirdRule: false,
                      tier: 'TIER_3',
                      defaultValueApplied: undefined,
                      analysisMethodUsed: true,
                      analysisMethods: [
                        {
                          analysis: 'analyze',
                          files: [],
                          frequencyMeetsMinRequirements: false,
                          laboratoryAccredited: false,
                          laboratoryName: 'lab',
                          samplingFrequency: 'WEEKLY',
                          subParameter: null,
                        },
                      ],
                    },
                  },
                ],
              },
            },
          },
          {
            CALCULATION_Emission_Factor: [false],
          },
        ),
      );
    });

    beforeEach(() => {
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should fill the form', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.analysis).toEqual('analyze');
      expect(page.samplingFrequency).toEqual('WEEKLY');
      expect(page.frequencyMeetsMinRequirementsRadios[0].checked).toBeFalsy();
      expect(page.frequencyMeetsMinRequirementsRadios[1].checked).toBeTruthy();
      expect(page.laboratoryAccreditedRadios[0].checked).toBeFalsy();
      expect(page.laboratoryAccreditedRadios[1].checked).toBeTruthy();
      expect(page.laboratoryName).toEqual('lab');
    });
  });
});

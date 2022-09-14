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
import { AnalysisMethodUsedComponent } from './analysis-method-used.component';

describe('AnalysisMethodUsedComponent', () => {
  let component: AnalysisMethodUsedComponent;
  let fixture: ComponentFixture<AnalysisMethodUsedComponent>;

  let store: PermitApplicationStore;

  let page: Page;
  let router: Router;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' }, null, {
    taskKey: 'monitoringApproaches.CALCULATION.sourceStreamCategoryAppliedTiers',
    statusKey: 'CALCULATION_Calorific',
  });
  const sourceStreamCategory = (
    mockPermitApplyPayload.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach
  ).sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;

  class Page extends BasePage<AnalysisMethodUsedComponent> {
    get paragraphs() {
      return this.queryAll<HTMLParagraphElement>('p');
    }

    get analysisMethodUsedRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="analysisMethodUsed"]');
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
    fixture = TestBed.createComponent(AnalysisMethodUsedComponent);
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

  describe('for new entry', () => {
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
                    netCalorificValue: {
                      exist: true,
                      tier: 'TIER_3',
                    },
                  },
                ],
              },
            },
          },
          {
            CALCULATION_Category: [true],
            CALCULATION_Calorific: [false],
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
      expect(page.errorSummaryErrorList).toEqual(['Select Yes or No']);

      page.analysisMethodUsedRadios[0].click();
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
                    sourceStreamCategory: (
                      mockState.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach
                    ).sourceStreamCategoryAppliedTiers[0].sourceStreamCategory,
                    netCalorificValue: {
                      exist: true,
                      tier: 'TIER_3',
                      analysisMethodUsed: true,
                    },
                  },
                ],
              },
            },
          },
          {
            ...mockState.permitSectionsCompleted,
            CALCULATION_Category: [true],
            CALCULATION_Calorific: [false],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['../analysis-method/0'], { relativeTo: route });
    });
  });

  describe('for existing entry', () => {
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
                    netCalorificValue: {
                      exist: true,
                      tier: 'TIER_3',
                      analysisMethodUsed: true,
                    },
                  },
                ],
              },
            },
          },
          {
            CALCULATION_Category: [true],
            CALCULATION_Calorific: [false],
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

      expect(page.analysisMethodUsedRadios.length).toEqual(2);
      expect(page.analysisMethodUsedRadios[0].checked).toBeTruthy();
      expect(page.analysisMethodUsedRadios[1].checked).toBeFalsy();
    });
  });
});

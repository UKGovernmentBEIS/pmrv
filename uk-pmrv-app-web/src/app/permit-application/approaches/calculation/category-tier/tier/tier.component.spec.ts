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
import { TierComponent } from './tier.component';

describe('TierComponent', () => {
  let store: PermitApplicationStore;
  let component: TierComponent;
  let fixture: ComponentFixture<TierComponent>;
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

  class Page extends BasePage<TierComponent> {
    get paragraphs() {
      return this.queryAll<HTMLParagraphElement>('p');
    }

    get tierRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="tier"]');
    }

    get continueButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get isHighestTierRequiredT1Options() {
      return this.queryAll<HTMLInputElement>('input[name$="isHighestRequiredTier_TIER_1"]');
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
    fixture = TestBed.createComponent(TierComponent);
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

  describe('for new calorific value', () => {
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
      expect(page.errorSummaryErrorList).toEqual(['Select a tier']);

      page.tierRadios[1].click();
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select Yes or No']);

      page.tierRadios[2].click();
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select Yes or No']);

      page.tierRadios[3].click();
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select Yes or No']);

      page.tierRadios[0].click();
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
      expect(navigateSpy).toHaveBeenCalledWith(['../default-value'], { relativeTo: route });
    });
  });

  describe('for existing calorific value', () => {
    beforeEach(() => {
      const sourceStreamCategory = (
        mockPermitApplyPayload.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach
      ).sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;

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
                      tier: 'TIER_1',
                      isHighestRequiredTier: true,
                      exist: true,
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

      expect(page.tierRadios.length).toEqual(5);
      expect(page.tierRadios[0].checked).toBeFalsy();
      expect(page.tierRadios[1].checked).toBeFalsy();
      expect(page.isHighestTierRequiredT1Options[0].checked).toBeTruthy();
      expect(page.isHighestTierRequiredT1Options[1].checked).toBeFalsy();
    });
  });
});

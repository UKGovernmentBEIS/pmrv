import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PFCMonitoringApproach, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockStateBuild } from '../../../../testing/mock-state';
import { PFCModule } from '../../pfc.module';
import { EmissionFactorComponent } from './emission-factor.component';

describe('EmissionFactorComponent', () => {
  let store: PermitApplicationStore;
  let component: EmissionFactorComponent;
  let fixture: ComponentFixture<EmissionFactorComponent>;
  let page: Page;
  let router: Router;
  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' }, null, {
    taskKey: 'monitoringApproaches.PFC.sourceStreamCategoryAppliedTiers',
    statusKey: 'PFC_Emission_Factor',
  });

  class Page extends BasePage<EmissionFactorComponent> {
    get paragraphs() {
      return this.queryAll<HTMLParagraphElement>('p');
    }

    get tierRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="tier"]');
    }

    get isHighestTierRequiredT1Options() {
      return this.queryAll<HTMLInputElement>('input[name$="isHighestRequiredTierT1"]');
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
    fixture = TestBed.createComponent(EmissionFactorComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PFCModule, RouterTestingModule, SharedModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('task prerequisites not completed', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          monitoringApproaches: {
            PFC: {
              sourceStreamCategoryAppliedTiers: [],
            },
          },
        }),
      );
    });

    beforeEach(() => {
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the can not start page', () => {
      expect(
        page.paragraphs.find((p) => p.textContent.trim() === 'Other tasks must be completed before you can start:'),
      ).toBeTruthy();
    });
  });

  describe('for new emission factor', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {},
          {
            PFC_Category: [true],
            PFC_Emission_Factor: [false],
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

    it('upon selecting TIER_2 and pressing continue should submit a valid form and navigate correctly', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select a tier']);

      page.tierRadios[0].click();
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproaches: {
              ...store.getState().permit.monitoringApproaches,
              PFC: {
                ...store.getState().permit.monitoringApproaches.PFC,
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: (store.getState().permit.monitoringApproaches.PFC as PFCMonitoringApproach)
                      .sourceStreamCategoryAppliedTiers[0].sourceStreamCategory,
                    activityData: (store.getState().permit.monitoringApproaches.PFC as PFCMonitoringApproach)
                      .sourceStreamCategoryAppliedTiers[0].activityData,
                    emissionFactor: {
                      tier: 'TIER_2',
                    },
                  },
                ],
              },
            },
          },
          {
            ...store.getState().permitSectionsCompleted,
            PFC_Emission_Factor: [false],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['justification'], { relativeTo: route });
    });

    it('upon selecting TIER_1 and after fillinh highest required tier, it should submit a valid form and navigate correctly', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.tierRadios[1].click();
      page.continueButton.click();
      fixture.detectChanges();

      page.isHighestTierRequiredT1Options[0].click();
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproaches: {
              ...store.getState().permit.monitoringApproaches,
              PFC: {
                ...store.getState().permit.monitoringApproaches.PFC,
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: (store.getState().permit.monitoringApproaches.PFC as PFCMonitoringApproach)
                      .sourceStreamCategoryAppliedTiers[0].sourceStreamCategory,
                    activityData: (store.getState().permit.monitoringApproaches.PFC as PFCMonitoringApproach)
                      .sourceStreamCategoryAppliedTiers[0].activityData,
                    emissionFactor: {
                      tier: 'TIER_1',
                      isHighestRequiredTier: true,
                    },
                  },
                ],
              },
            },
          },
          {
            ...store.getState().permitSectionsCompleted,
            PFC_Emission_Factor: [false],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['justification'], { relativeTo: route });
    });
  });

  describe('for existing emission factor', () => {
    beforeEach(() => {
      const sourceStreamCategory = (mockPermitApplyPayload.permit.monitoringApproaches.PFC as PFCMonitoringApproach)
        .sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;

      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              PFC: {
                type: 'PFC',
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory,
                    emissionFactor: {
                      tier: 'TIER_1',
                      isHighestRequiredTier: true,
                    },
                  },
                ],
              },
            },
          },
          {
            PFC_Category: [true],
            PFC_Emission_Factor: [false],
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

    it('should display exisitng form', () => {
      expect(page.errorSummary).toBeFalsy();

      expect(page.tierRadios.length).toEqual(2);
      expect(page.tierRadios[0].checked).toBeFalsy();
      expect(page.tierRadios[1].checked).toBeTruthy();
      expect(page.isHighestTierRequiredT1Options[0].checked).toBeTruthy();
      expect(page.isHighestTierRequiredT1Options[1].checked).toBeFalsy();
    });

    it('should submit a valid form and navigate correctly', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.tierRadios[0].click();
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            monitoringApproaches: {
              ...store.getState().permit.monitoringApproaches,
              PFC: {
                ...store.getState().permit.monitoringApproaches.PFC,
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: (store.getState().permit.monitoringApproaches.PFC as PFCMonitoringApproach)
                      .sourceStreamCategoryAppliedTiers[0].sourceStreamCategory,
                    activityData: (store.getState().permit.monitoringApproaches.PFC as PFCMonitoringApproach)
                      .sourceStreamCategoryAppliedTiers[0].activityData,
                    emissionFactor: {
                      tier: 'TIER_2',
                    },
                  },
                ],
              },
            },
          },
          {
            ...store.getState().permitSectionsCompleted,
            PFC_Emission_Factor: [false],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['justification'], { relativeTo: route });
    });
  });
});

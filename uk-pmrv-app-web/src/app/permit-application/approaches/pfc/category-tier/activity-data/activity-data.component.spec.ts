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
import { ActivityDataComponent } from './activity-data.component';

describe('ActivityDataComponent', () => {
  let store: PermitApplicationStore;
  let component: ActivityDataComponent;
  let fixture: ComponentFixture<ActivityDataComponent>;
  let page: Page;
  let router: Router;
  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ index: '0' }, null, {
    taskKey: 'monitoringApproaches.PFC.sourceStreamCategoryAppliedTiers',
    statusKey: 'PFC_Activity_Data',
  });
  const sourceStreamCategory = (mockPermitApplyPayload.permit.monitoringApproaches.PFC as PFCMonitoringApproach)
    .sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;

  class Page extends BasePage<ActivityDataComponent> {
    get paragraphs() {
      return this.queryAll<HTMLParagraphElement>('p');
    }

    get massBalanceApproachUsedRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="massBalanceApproachUsed"]');
    }

    get tierRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="tier"]');
    }

    get isHighestTierRequiredT1Options() {
      return this.queryAll<HTMLInputElement>('input[name$="isHighestRequiredTier_TIER_1"]');
    }

    get isHighestTierRequiredT2Options() {
      return this.queryAll<HTMLInputElement>('input[name$="isHighestRequiredTier_TIER_2"]');
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
    fixture = TestBed.createComponent(ActivityDataComponent);
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

  describe('for new activity data where mass balance is not used', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              PFC: {
                type: 'PFC',
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: sourceStreamCategory,
                  },
                ],
              },
            },
          },
          {
            PFC_Category: [true],
            PFC_Activity_Data: [false],
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

    it('should submit a valid form and navigate correctly', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.massBalanceApproachUsedRadios.every((option) => expect(option.checked).toBeFalsy());
      expect(page.tierRadios.length).toEqual(4);
      page.tierRadios.every((option) => expect(option.checked).toBeFalsy());

      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select Yes or No', 'Select a tier']);

      page.massBalanceApproachUsedRadios[1].click();
      fixture.detectChanges();
      expect(page.tierRadios.length).toEqual(2);
      page.tierRadios.every((option) => expect(option.checked).toBeFalsy());

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
                    sourceStreamCategory: sourceStreamCategory,
                    activityData: {
                      massBalanceApproachUsed: false,
                      tier: 'TIER_2',
                    },
                  },
                ],
              },
            },
          },
          {
            ...store.getState().permitSectionsCompleted,
            PFC_Activity_Data: [false],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['justification'], { relativeTo: route });
    });
  });

  describe('for new activity data where mass balance is used', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              PFC: {
                type: 'PFC',
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: sourceStreamCategory,
                  },
                ],
              },
            },
          },
          {
            PFC_Category: [true],
            PFC_Activity_Data: [false],
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

    it('should submit a valid form and navigate correctly', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.massBalanceApproachUsedRadios.every((option) => expect(option.checked).toBeFalsy());
      expect(page.tierRadios.length).toEqual(4);
      page.tierRadios.every((option) => expect(option.checked).toBeFalsy());

      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select Yes or No', 'Select a tier']);

      page.massBalanceApproachUsedRadios[0].click();
      fixture.detectChanges();
      expect(page.tierRadios.length).toEqual(4);
      page.tierRadios.every((option) => expect(option.checked).toBeFalsy());

      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select a tier']);

      page.tierRadios[2].click();
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select Yes or No']);

      expect(page.isHighestTierRequiredT2Options.length).toEqual(2);
      page.isHighestTierRequiredT2Options[1].click();
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
                    sourceStreamCategory: sourceStreamCategory,
                    activityData: {
                      massBalanceApproachUsed: true,
                      tier: 'TIER_2',
                      isHighestRequiredTier: false,
                      noHighestRequiredTierJustification: {
                        isCostUnreasonable: undefined,
                        isTechnicallyInfeasible: undefined,
                        technicalInfeasibilityExplanation: undefined,
                        files: undefined,
                      },
                    },
                  },
                ],
              },
            },
          },
          {
            ...store.getState().permitSectionsCompleted,
            PFC_Activity_Data: [false],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['justification'], { relativeTo: route });
    });
  });

  describe('for existing activity data', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              PFC: {
                type: 'PFC',
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: sourceStreamCategory,
                    activityData: {
                      massBalanceApproachUsed: true,
                      tier: 'TIER_2',
                      isHighestRequiredTier: false,
                    },
                  },
                ],
              },
            },
          },
          {
            PFC_Category: [true],
            PFC_Activity_Data: [false],
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

    it('should display exisitng activity data', () => {
      expect(page.errorSummary).toBeFalsy();

      expect(page.massBalanceApproachUsedRadios[0].checked).toBeTruthy();
      expect(page.massBalanceApproachUsedRadios[1].checked).toBeFalsy();
      expect(page.tierRadios.length).toEqual(4);
      expect(page.tierRadios[0].checked).toBeFalsy();
      expect(page.tierRadios[1].checked).toBeFalsy();
      expect(page.tierRadios[2].checked).toBeTruthy();
      expect(page.tierRadios[3].checked).toBeFalsy();
      expect(page.isHighestTierRequiredT2Options[0].checked).toBeFalsy();
      expect(page.isHighestTierRequiredT2Options[1].checked).toBeTruthy();
    });

    it('should submit a valid form and navigate correctly', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.massBalanceApproachUsedRadios[1].click();
      fixture.detectChanges();
      expect(page.tierRadios.length).toEqual(2);
      page.tierRadios.every((option) => expect(option.checked).toBeFalsy());

      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select a tier']);

      page.tierRadios[1].click();
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select Yes or No']);

      expect(page.isHighestTierRequiredT1Options.length).toEqual(2);
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
                    sourceStreamCategory: sourceStreamCategory,
                    activityData: {
                      massBalanceApproachUsed: false,
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
            PFC_Activity_Data: [false],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['justification'], { relativeTo: route });
    });
  });
});

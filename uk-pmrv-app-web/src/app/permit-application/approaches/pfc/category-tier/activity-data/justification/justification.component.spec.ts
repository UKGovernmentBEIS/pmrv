import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PFCMonitoringApproach, RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, asyncData, BasePage, mockClass } from '../../../../../../../testing';
import { SharedModule } from '../../../../../../shared/shared.module';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockStateBuild } from '../../../../../testing/mock-state';
import { PFCModule } from '../../../pfc.module';
import { JustificationComponent } from './justification.component';

describe('JustificationComponent', () => {
  let store: PermitApplicationStore;
  let component: JustificationComponent;
  let fixture: ComponentFixture<JustificationComponent>;
  let page: Page;
  let router: Router;

  const tasksService = mockClass(TasksService);
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);
  const route = new ActivatedRouteStub({ index: '0' }, null, {
    taskKey: 'monitoringApproaches.PFC.sourceStreamCategoryAppliedTiers',
    statusKey: 'PFC_Activity_Data',
  });
  const sourceStreamCategory = (mockPermitApplyPayload.permit.monitoringApproaches.PFC as PFCMonitoringApproach)
    .sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;
  const technicalInfeasibilityExplanation = 'technicalInfeasibilityExplanation';

  class Page extends BasePage<JustificationComponent> {
    get unreasonableCost() {
      return this.query<HTMLInputElement>('#justification-0');
    }
    get technicalInfeasibility() {
      return this.query<HTMLInputElement>('#justification-1');
    }
    get technicalInfeasibilityExplanation() {
      return this.getInputValue('#technicalInfeasibilityExplanation');
    }
    set technicalInfeasibilityExplanation(value: string) {
      this.setInputValue('#technicalInfeasibilityExplanation', value);
    }
    set fileValue(value: File[]) {
      this.setInputValue('input[type="file"]', value);
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
    fixture = TestBed.createComponent(JustificationComponent);
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
        { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
      ],
    }).compileComponents();
  });

  describe('for new justification', () => {
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
                      massBalanceApproachUsed: false,
                      tier: 'TIER_1',
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

    it('should submit a valid form', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      attachmentService.uploadRequestTaskAttachmentUsingPOST.mockReturnValue(
        asyncData<any>(new HttpResponse({ body: { uuid: 'e227ea8a-778b-4208-9545-e108ea66c114' } })),
      );
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select a justification']);

      page.technicalInfeasibility.click();
      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Explain why it is technically infeasible to meet the highest tier']);

      page.technicalInfeasibilityExplanation = technicalInfeasibilityExplanation;
      page.fileValue = [new File(['file'], 'file.txt')];
      fixture.detectChanges();

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
                      isHighestRequiredTier: false,
                      noHighestRequiredTierJustification: {
                        isCostUnreasonable: false,
                        isTechnicallyInfeasible: true,
                        technicalInfeasibilityExplanation: technicalInfeasibilityExplanation,
                        files: ['e227ea8a-778b-4208-9545-e108ea66c114'],
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
      expect(navigateSpy).toHaveBeenCalledWith(['../answers'], { relativeTo: route });
    });
  });

  describe('for existing justification', () => {
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
                      massBalanceApproachUsed: false,
                      tier: 'TIER_1',
                      isHighestRequiredTier: false,
                      noHighestRequiredTierJustification: {
                        isTechnicallyInfeasible: true,
                        technicalInfeasibilityExplanation: technicalInfeasibilityExplanation,
                      },
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

    it('should submit a valid form', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      expect(page.unreasonableCost.checked).toBeFalsy();
      expect(page.technicalInfeasibility.checked).toBeTruthy();

      page.unreasonableCost.click();
      page.technicalInfeasibility.click();

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
                      isHighestRequiredTier: false,
                      noHighestRequiredTierJustification: {
                        isCostUnreasonable: true,
                        isTechnicallyInfeasible: false,
                        files: [],
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
      expect(navigateSpy).toHaveBeenCalledWith(['../answers'], { relativeTo: route });
    });
  });
});

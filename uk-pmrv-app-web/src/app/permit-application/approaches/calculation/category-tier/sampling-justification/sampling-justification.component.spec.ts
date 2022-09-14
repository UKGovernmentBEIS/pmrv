import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CalculationMonitoringApproach, RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, asyncData, BasePage, mockClass } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../../../../testing/mock-state';
import { CalculationModule } from '../../calculation.module';
import { SamplingJustificationComponent } from './sampling-justification.component';

describe('SamplingJustificationComponent', () => {
  let store: PermitApplicationStore;
  let component: SamplingJustificationComponent;
  let fixture: ComponentFixture<SamplingJustificationComponent>;
  let page: Page;
  let router: Router;

  const tasksService = mockClass(TasksService);
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);
  const route = new ActivatedRouteStub({ index: '0' }, null, {
    taskKey: 'monitoringApproaches.CALCULATION.sourceStreamCategoryAppliedTiers',
    statusKey: 'CALCULATION_Calorific',
  });

  const sourceStreamCategory = (
    mockPermitApplyPayload.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach
  ).sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;

  class Page extends BasePage<SamplingJustificationComponent> {
    get isCostUnreasonable() {
      return this.query<HTMLInputElement>('#justification-0');
    }
    get frequencyLessThanRequired() {
      return this.query<HTMLInputElement>('#justification-1');
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
    fixture = TestBed.createComponent(SamplingJustificationComponent);
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
              CALCULATION: {
                type: 'CALCULATION',
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory,
                    netCalorificValue: {
                      exist: true,
                      tier: 'TIER_3',
                      analysisMethods: [
                        {
                          analysis: 'asdas',
                          frequencyMeetsMinRequirements: false,
                          laboratoryAccreditationEvidence: 'fdfd',
                          laboratoryAccredited: false,
                          laboratoryName: 'adasd',
                          samplingFrequency: 'WEEKLY',
                        },
                      ],
                    },
                  },
                ],
              },
            },
          },
          {
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
      attachmentService.uploadRequestTaskAttachmentUsingPOST.mockReturnValue(
        asyncData<any>(new HttpResponse({ body: { uuid: 'e227ea8a-778b-4208-9545-e108ea66c114' } })),
      );
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select a reason']);

      page.isCostUnreasonable.click();
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
                    netCalorificValue: {
                      exist: true,
                      tier: 'TIER_3',
                      analysisMethods: [
                        {
                          analysis: 'asdas',
                          frequencyMeetsMinRequirements: false,
                          laboratoryAccreditationEvidence: 'fdfd',
                          laboratoryAccredited: false,
                          laboratoryName: 'adasd',
                          samplingFrequency: 'WEEKLY',
                          reducedSamplingFrequencyJustification: {
                            files: [],
                            isCostUnreasonable: true,
                            isOneThirdRuleAndSampling: false,
                          },
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
            CALCULATION_Calorific: [false],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['../../../analysis-method-list'], { relativeTo: route });
    });
  });

  describe('for existing justification', () => {
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
                      analysisMethods: [
                        {
                          analysis: 'asdas',
                          frequencyMeetsMinRequirements: false,
                          laboratoryAccreditationEvidence: 'fdfd',
                          laboratoryAccredited: false,
                          laboratoryName: 'adasd',
                          samplingFrequency: 'WEEKLY',
                          reducedSamplingFrequencyJustification: {
                            files: [],
                            isCostUnreasonable: true,
                            isOneThirdRuleAndSampling: false,
                          },
                        },
                      ],
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
      expect(page.isCostUnreasonable.checked).toBeTruthy();
      expect(page.frequencyLessThanRequired.checked).toBeFalsy();
    });
  });
});

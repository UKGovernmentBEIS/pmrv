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
import { mockPostBuild, mockStateBuild } from '../../../../testing/mock-state';
import { CalculationModule } from '../../calculation.module';
import { OneThirdComponent } from './one-third.component';

describe('OneThirdComponent', () => {
  let component: OneThirdComponent;
  let fixture: ComponentFixture<OneThirdComponent>;
  let store: PermitApplicationStore;
  let page: Page;
  let router: Router;

  const route = new ActivatedRouteStub({ index: '0' }, null, {
    taskKey: 'monitoringApproaches.CALCULATION.sourceStreamCategoryAppliedTiers',
    statusKey: 'CALCULATION_Emission_Factor',
  });
  const tasksService = mockClass(TasksService);
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);
  attachmentService.uploadRequestTaskAttachmentUsingPOST.mockReturnValue(
    asyncData<any>(new HttpResponse({ body: { uuid: 'xzy' } })),
  );
  const sourceStreamCategory = (
    mockPermitApplyPayload.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach
  ).sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;

  class Page extends BasePage<OneThirdComponent> {
    get oneThirdRuleRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="oneThirdRule"]');
    }

    set fileValue(value: File[]) {
      this.setInputValue('input[type="file"]', value);
    }

    get submitButton() {
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
    get errors() {
      return this.queryAll<HTMLLIElement>('.govuk-error-summary__list > li');
    }

    get continueButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(OneThirdComponent);
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

  describe('for new one third rule', () => {
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
                      // oneThirdRule: true,
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
      attachmentService.uploadRequestTaskAttachmentUsingPOST.mockReturnValue(
        asyncData<any>(new HttpResponse({ body: { uuid: 'e227ea8a-778b-4208-9545-e108ea66c114' } })),
      );
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.continueButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select Yes or No']);

      page.oneThirdRuleRadios[0].click();
      page.fileValue = [new File(['file'], 'file.txt')];

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
                      tier: 'TIER_3',
                      oneThirdRule: true,
                      oneThirdRuleFiles: ['e227ea8a-778b-4208-9545-e108ea66c114'],
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
      expect(navigateSpy).toHaveBeenCalledWith(['../default-value'], { relativeTo: route });
    });
  });

  describe('for existng one third rule', () => {
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
                    emissionFactor: {
                      exist: true,
                      tier: 'TIER_3',
                      oneThirdRule: true,
                      oneThirdRuleFiles: ['e227ea8a-778b-4208-9545-e108ea66c114'],
                    },
                  },
                ],
              },
            },
          },
          {
            CALCULATION_Category: [true],
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

      expect(page.oneThirdRuleRadios.length).toEqual(2);
      expect(page.oneThirdRuleRadios[0].checked).toBeTruthy();
      expect(page.oneThirdRuleRadios[1].checked).toBeFalsy();
    });
  });
});

import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskAttachmentsHandlingService, TasksService, TransferredCO2MonitoringApproach } from 'pmrv-api';

import { ActivatedRouteStub, asyncData, BasePage, mockClass } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { SharedPermitModule } from '../../../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../../../../testing/mock-state';
import { JustificationComponent } from './justification.component';

describe('JustificationComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore;
  let component: JustificationComponent;
  let fixture: ComponentFixture<JustificationComponent>;
  const route = new ActivatedRouteStub(
    {},
    {},
    { taskKey: 'monitoringApproaches.TRANSFERRED_CO2.accountingEmissions', statusKey: 'TRANSFERRED_CO2_Accounting' },
  );

  const tasksService = mockClass(TasksService);
  const attachmentService = mockClass(RequestTaskAttachmentsHandlingService);

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
    get filesText() {
      return this.queryAll<HTMLDivElement>('.moj-multi-file-upload__message');
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
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      declarations: [JustificationComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
        { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
      ],
    }).compileComponents();
  });

  describe('for new accounting emissions', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select a justification']);

      page.technicalInfeasibility.click();
      fixture.detectChanges();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Explain why it is technically infeasible to meet the highest tier']);

      page.technicalInfeasibilityExplanation = 'explanation';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            ...store.getState().permit,
            monitoringApproaches: {
              ...store.getState().permit.monitoringApproaches,
              TRANSFERRED_CO2: {
                ...store.getState().permit.monitoringApproaches.TRANSFERRED_CO2,
                accountingEmissions: {
                  chemicallyBound: false,
                  accountingEmissionsDetails: {
                    ...(
                      store.getState().permit.monitoringApproaches.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach
                    ).accountingEmissions.accountingEmissionsDetails,
                    noHighestRequiredTierJustification: {
                      isCostUnreasonable: false,
                      isTechnicallyInfeasible: true,
                      technicalInfeasibilityExplanation: 'explanation',
                      files: [],
                    },
                  },
                },
              } as TransferredCO2MonitoringApproach,
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            TRANSFERRED_CO2_Accounting: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../answers'], { relativeTo: route });
    });

    it('should submit a valid form with upload file', async () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      attachmentService.uploadRequestTaskAttachmentUsingPOST.mockReturnValue(
        asyncData<any>(new HttpResponse({ body: { uuid: '60fe9548-ac65-492a-b057-60033b0fbbed' } })),
      );
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.unreasonableCost.click();
      fixture.detectChanges();
      page.fileValue = [new File(['test content'], 'new-file.txt')];
      fixture.detectChanges();

      await fixture.whenStable();
      fixture.detectChanges();

      expect(page.filesText.map((row) => row.textContent.trim())).toEqual(['new-file.txt has been uploaded']);

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            ...store.getState().permit,
            monitoringApproaches: {
              ...store.getState().permit.monitoringApproaches,
              TRANSFERRED_CO2: {
                ...store.getState().permit.monitoringApproaches.TRANSFERRED_CO2,
                accountingEmissions: {
                  chemicallyBound: false,
                  accountingEmissionsDetails: {
                    ...(
                      store.getState().permit.monitoringApproaches.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach
                    ).accountingEmissions.accountingEmissionsDetails,
                    noHighestRequiredTierJustification: {
                      isCostUnreasonable: true,
                      isTechnicallyInfeasible: false,
                      files: ['60fe9548-ac65-492a-b057-60033b0fbbed'],
                    },
                  },
                },
              } as TransferredCO2MonitoringApproach,
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            TRANSFERRED_CO2_Accounting: [false],
          },
        ),
      );
      expect(TestBed.inject(PermitApplicationStore).getValue().permitAttachments).toMatchObject({
        '60fe9548-ac65-492a-b057-60033b0fbbed': 'new-file.txt',
      });
      expect(navigateSpy).toHaveBeenCalledWith(['../answers'], { relativeTo: route });
    });
  });

  describe('for editing accounting emissions', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          monitoringApproaches: {
            TRANSFERRED_CO2: {
              accountingEmissions: {
                chemicallyBound: false,
                accountingEmissionsDetails: {
                  ...(mockState.permit.monitoringApproaches.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach)
                    .accountingEmissions.accountingEmissionsDetails,
                  tier: 'TIER_3',
                  isHighestRequiredTier: false,
                  noHighestRequiredTierJustification: {
                    isCostUnreasonable: true,
                    isTechnicallyInfeasible: false,
                    files: [],
                  },
                },
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

    it('should submit a valid form', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.technicalInfeasibility.click();
      fixture.detectChanges();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Explain why it is technically infeasible to meet the highest tier']);

      page.technicalInfeasibilityExplanation = 'explanation';
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            ...store.getState().permit,
            monitoringApproaches: {
              ...store.getState().permit.monitoringApproaches,
              TRANSFERRED_CO2: {
                ...store.getState().permit.monitoringApproaches.TRANSFERRED_CO2,
                accountingEmissions: {
                  chemicallyBound: false,
                  accountingEmissionsDetails: {
                    ...(
                      store.getState().permit.monitoringApproaches.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach
                    ).accountingEmissions.accountingEmissionsDetails,
                    noHighestRequiredTierJustification: {
                      isCostUnreasonable: true,
                      isTechnicallyInfeasible: true,
                      technicalInfeasibilityExplanation: 'explanation',
                      files: [],
                    },
                  },
                },
              } as TransferredCO2MonitoringApproach,
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            TRANSFERRED_CO2_Accounting: [false],
          },
        ),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['../answers'], { relativeTo: route });
    });
  });
});

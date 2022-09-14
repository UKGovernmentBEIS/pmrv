import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService, TransferredCO2MonitoringApproach } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { SharedPermitModule } from '../../../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../../../../testing/mock-state';
import { DetailsComponent } from './details.component';

describe('DetailsComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore;
  let component: DetailsComponent;
  let fixture: ComponentFixture<DetailsComponent>;
  const route = new ActivatedRouteStub(
    {},
    {},
    { taskKey: 'monitoringApproaches.TRANSFERRED_CO2.accountingEmissions', statusKey: 'TRANSFERRED_CO2_Accounting' },
  );

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<DetailsComponent> {
    get measurementDevicesOrMethods() {
      return this.fixture.componentInstance.form.get('measurementDevicesOrMethods').value;
    }
    set measurementDevicesOrMethods(values: string[]) {
      this.fixture.componentInstance.form.get('measurementDevicesOrMethods').setValue(values);
    }
    get samplingFrequencyRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="samplingFrequency"]');
    }
    get otherSamplingFrequency() {
      return this.getInputValue('#otherSamplingFrequency');
    }
    set otherSamplingFrequency(value: string) {
      this.setInputValue('#otherSamplingFrequency', value);
    }
    get tierRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="tier"]');
    }
    get isHighestTierRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="isHighestRequiredTierT3"]');
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
    fixture = TestBed.createComponent(DetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      declarations: [DetailsComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for new accounting emissions', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          monitoringApproaches: {
            TRANSFERRED_CO2: {
              accountingEmissions: {
                chemicallyBound: false,
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

    it('should submit a valid form with high tier', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual([
        'Select at least one measurement device',
        'Select a frequency',
        'Select a tier',
      ]);

      page.measurementDevicesOrMethods = [store.getState().permit.measurementDevicesOrMethods[0].id];
      page.samplingFrequencyRadios[6].click();
      page.tierRadios[1].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Enter details', 'Select Yes or No']);

      page.tierRadios[4].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual([
        'Enter details',
        'Enter how emissions from transferred CO2 will be estimated',
      ]);

      page.samplingFrequencyRadios[0].click();
      page.tierRadios[0].click();
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
                    measurementDevicesOrMethods: [store.getState().permit.measurementDevicesOrMethods[0].id],
                    samplingFrequency: page.samplingFrequencyRadios[0].value,
                    tier: page.tierRadios[0].value,
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

      expect(navigateSpy).toHaveBeenCalledWith(['../justification'], { relativeTo: route });
    });

    it('should submit a valid form', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.measurementDevicesOrMethods = [store.getState().permit.measurementDevicesOrMethods[0].id];
      page.samplingFrequencyRadios[0].click();
      page.tierRadios[1].click();
      fixture.detectChanges();
      page.isHighestTierRadios[1].click();
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
                    measurementDevicesOrMethods: [store.getState().permit.measurementDevicesOrMethods[0].id],
                    samplingFrequency: page.samplingFrequencyRadios[0].value,
                    tier: page.tierRadios[1].value,
                    isHighestRequiredTier: false,
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

      expect(navigateSpy).toHaveBeenCalledWith(['../justification'], { relativeTo: route });
    });
  });

  describe('for editing accounting emissions', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form with high tier', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.tierRadios[4].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Enter how emissions from transferred CO2 will be estimated']);

      page.tierRadios[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(undefined, {
          ...mockPermitApplyPayload.permitSectionsCompleted,
          TRANSFERRED_CO2_Accounting: [false],
        }),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../justification'], { relativeTo: route });
    });

    it('should submit a valid form', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.measurementDevicesOrMethods = [store.getState().permit.measurementDevicesOrMethods[0].id];
      page.samplingFrequencyRadios[0].click();
      page.tierRadios[1].click();
      fixture.detectChanges();
      page.isHighestTierRadios[1].click();
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
                    measurementDevicesOrMethods: [store.getState().permit.measurementDevicesOrMethods[0].id],
                    samplingFrequency: page.samplingFrequencyRadios[0].value,
                    tier: page.tierRadios[1].value,
                    isHighestRequiredTier: false,
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

      expect(navigateSpy).toHaveBeenCalledWith(['../justification'], { relativeTo: route });
    });
  });

  describe('for editing accounting emissions with all steps confirmed', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              TRANSFERRED_CO2: {
                accountingEmissions: {
                  chemicallyBound: false,
                  accountingEmissionsDetails: {
                    ...(mockState.permit.monitoringApproaches.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach)
                      .accountingEmissions.accountingEmissionsDetails,
                    tier: 'TIER_1',
                    isHighestRequiredTier: false,
                    noHighestRequiredTierJustification: { isCostUnreasonable: true },
                  },
                },
              },
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            TRANSFERRED_CO2_Accounting: [true],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form with high tier', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.samplingFrequencyRadios[0].click();
      page.tierRadios[0].click();
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
                    measurementDevicesOrMethods: [store.getState().permit.measurementDevicesOrMethods[0].id],
                    samplingFrequency: page.samplingFrequencyRadios[0].value,
                    tier: page.tierRadios[0].value,
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

      expect(navigateSpy).toHaveBeenCalledWith(['../justification'], { relativeTo: route });
    });

    it('should submit a valid form', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.tierRadios[1].click();
      fixture.detectChanges();
      page.isHighestTierRadios[1].click();
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
                    tier: page.tierRadios[1].value,
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

      expect(navigateSpy).toHaveBeenCalledWith(['../justification'], { relativeTo: route });
    });
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService, TransferredCO2MonitoringApproach } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { SharedPermitModule } from '../../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../../../testing/mock-state';
import { AccountingComponent } from './accounting.component';

describe('AccountingComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore;
  let component: AccountingComponent;
  let fixture: ComponentFixture<AccountingComponent>;
  const route = new ActivatedRouteStub(
    {},
    {},
    { taskKey: 'monitoringApproaches.TRANSFERRED_CO2.accountingEmissions', statusKey: 'TRANSFERRED_CO2_Accounting' },
  );

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<AccountingComponent> {
    get dependentTasks() {
      return this.queryAll<HTMLLIElement>('li');
    }
    get chemicallyBoundRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="chemicallyBound"]');
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
    fixture = TestBed.createComponent(AccountingComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      declarations: [AccountingComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for cannot start accounting emissions', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          monitoringApproaches: {
            TRANSFERRED_CO2: { accountingEmissions: undefined },
          },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show the cannot start page', () => {
      expect(page.chemicallyBoundRadios).toEqual([]);
      expect(page.dependentTasks.map((el) => el.textContent.trim())).toEqual(['Measurement devices']);
    });
  });

  describe('for new accounting emissions', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              TRANSFERRED_CO2: { accountingEmissions: undefined },
            },
          },
          {
            measurementDevicesOrMethods: [true],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form as chemically bound true', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.dependentTasks).toEqual([]);
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select Yes or No']);

      page.chemicallyBoundRadios[0].click();
      fixture.detectChanges();
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
                  chemicallyBound: true,
                  accountingEmissionsDetails: null,
                },
              } as TransferredCO2MonitoringApproach,
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            measurementDevicesOrMethods: [true],
            TRANSFERRED_CO2_Accounting: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['details'], { relativeTo: route });
    });

    it('should submit a valid form', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.dependentTasks).toEqual([]);
      expect(page.errorSummary).toBeFalsy();

      page.chemicallyBoundRadios[1].click();
      fixture.detectChanges();
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
                },
              } as TransferredCO2MonitoringApproach,
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            measurementDevicesOrMethods: [true],
            TRANSFERRED_CO2_Accounting: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['details'], { relativeTo: route });
    });
  });

  describe('for editing accounting emissions', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(mockState, {
          measurementDevicesOrMethods: [true],
          TRANSFERRED_CO2_Accounting: [true],
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form as chemically bound true', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.dependentTasks).toEqual([]);
      expect(page.errorSummary).toBeFalsy();

      page.chemicallyBoundRadios[0].click();
      fixture.detectChanges();
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
                  chemicallyBound: true,
                  accountingEmissionsDetails: null,
                },
              } as TransferredCO2MonitoringApproach,
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            measurementDevicesOrMethods: [true],
            TRANSFERRED_CO2_Accounting: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['details'], { relativeTo: route });
    });

    it('should submit a valid form', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.dependentTasks).toEqual([]);
      expect(page.errorSummary).toBeFalsy();

      page.chemicallyBoundRadios[1].click();
      fixture.detectChanges();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(0);
      expect(navigateSpy).toHaveBeenCalledWith(['details'], { relativeTo: route });
    });
  });
});

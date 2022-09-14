import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService, TransferredCO2MonitoringApproach } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../../../testing/mock-state';
import { TransferredCO2Module } from '../transferred-co2.module';
import { OptionalComponent } from './optional.component';

describe('OptionalFormComponent', () => {
  let page: Page;
  let router: Router;
  let component: OptionalComponent;
  let store: PermitApplicationStore;
  let fixture: ComponentFixture<OptionalComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    {
      taskKey: 'monitoringApproaches.TRANSFERRED_CO2.deductionsToAmountOfTransferredCO2',
      statusKey: 'transferredCo2Deductions',
    },
  );
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<OptionalComponent> {
    get existRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="exist"]');
    }

    set description(value: string) {
      this.setInputValue('#procedureForm.procedureDescription', value);
    }

    set documentName(value: string) {
      this.setInputValue('#procedureForm.procedureDocumentName', value);
    }

    set procedureReference(value: string) {
      this.setInputValue('#procedureForm.procedureReference', value);
    }

    set department(value: string) {
      this.setInputValue('#procedureForm.responsibleDepartmentOrRole', value);
    }

    set location(value: string) {
      this.setInputValue('#procedureForm.locationOfRecords', value);
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

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, TransferredCO2Module],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(OptionalComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  describe('for new procedure', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          ...mockPermitApplyPayload.permit,
          monitoringApproaches: {
            ...mockPermitApplyPayload.permit.monitoringApproaches,
            TRANSFERRED_CO2: {
              ...mockPermitApplyPayload.permit.monitoringApproaches.TRANSFERRED_CO2,
              deductionsToAmountOfTransferredCO2: undefined,
            } as TransferredCO2MonitoringApproach,
          },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select Yes or No']);

      page.existRadios[0].click();
      fixture.detectChanges();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummaryErrorList).toEqual([
        'Enter a brief description of the procedure',
        'Enter the name of the procedure document',
        'Enter a procedure reference',
        'Enter the name of the department or role responsible',
        'Enter the location of the records',
      ]);

      page.description = 'procedureDescriptionValue';
      page.documentName = 'procedureDocumentNameValue';
      page.procedureReference = 'procedureReferenceValue';
      page.department = 'responsibleDepartmentOrRoleValue';
      page.location = 'locationOfRecordsValue';

      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
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
                deductionsToAmountOfTransferredCO2: {
                  exist: true,
                  procedureForm: {
                    procedureDescription: 'procedureDescriptionValue',
                    procedureDocumentName: 'procedureDocumentNameValue',
                    procedureReference: 'procedureReferenceValue',
                    responsibleDepartmentOrRole: 'responsibleDepartmentOrRoleValue',
                    locationOfRecords: 'locationOfRecordsValue',
                    appliedStandards: null,
                    diagramReference: null,
                    itSystemUsed: null,
                  },
                },
              } as TransferredCO2MonitoringApproach,
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            transferredCo2Deductions: [true],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: route,
        state: { notification: true },
      });
    });
  });

  describe('for existing procedure', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);

      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      expect(page.errorSummary).toBeFalsy();

      page.existRadios[0].click();
      fixture.detectChanges();

      page.description = 'newDescr';

      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

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
                deductionsToAmountOfTransferredCO2: {
                  exist: true,
                  procedureForm: {
                    appliedStandards: 'appliedStandards',
                    diagramReference: 'diagramReference',
                    itSystemUsed: 'itSystemUsed',
                    locationOfRecords: 'locationOfRecords',
                    procedureDescription: 'newDescr',
                    procedureDocumentName: 'procedureDocumentName',
                    procedureReference: 'procedureReference',
                    responsibleDepartmentOrRole: 'responsibleDepartmentOrRole',
                  },
                },
              } as TransferredCO2MonitoringApproach,
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            transferredCo2Deductions: [true],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: route,
        state: { notification: true },
      });
    });
  });
});

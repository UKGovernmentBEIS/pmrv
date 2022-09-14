import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { N2OMonitoringApproach, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../../../testing/mock-state';
import { N2oModule } from '../n2o.module';
import { GasComponent } from './gas.component';

describe('GasComponent', () => {
  let page: Page;
  let router: Router;
  let component: GasComponent;
  let store: PermitApplicationStore;
  let fixture: ComponentFixture<GasComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub(
    {},
    {},
    { taskKey: 'monitoringApproaches.N2O.gasFlowCalculation', statusKey: 'n2oGas' },
  );

  class Page extends BasePage<GasComponent> {
    get existRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="exist"]');
    }

    set procedureDescriptionValue(value: string) {
      this.setInputValue('#procedureForm.procedureDescription', value);
    }

    set procedureDocumentNameValue(value: string) {
      this.setInputValue('#procedureForm.procedureDocumentName', value);
    }

    set procedureReferenceValue(value: string) {
      this.setInputValue('#procedureForm.procedureReference', value);
    }

    set responsibleDepartmentOrRoleValue(value: string) {
      this.setInputValue('#procedureForm.responsibleDepartmentOrRole', value);
    }

    set locationOfRecordsValue(value: string) {
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
      imports: [N2oModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(GasComponent);
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
            N2O: {
              ...mockPermitApplyPayload.permit.monitoringApproaches.N2O,
              gasFlowCalculation: undefined,
            } as N2OMonitoringApproach,
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

      page.procedureDescriptionValue = 'procedureDescriptionValue';
      page.procedureDocumentNameValue = 'procedureDocumentNameValue';
      page.procedureReferenceValue = 'procedureReferenceValue';
      page.responsibleDepartmentOrRoleValue = 'responsibleDepartmentOrRoleValue';
      page.locationOfRecordsValue = 'locationOfRecordsValue';

      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            ...store.getState().permit,
            monitoringApproaches: {
              ...store.getState().permit.monitoringApproaches,
              N2O: {
                ...store.getState().permit.monitoringApproaches.N2O,
                gasFlowCalculation: {
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
              } as N2OMonitoringApproach,
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            n2oGas: [true],
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

      page.procedureDescriptionValue = 'newDescr';

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
              N2O: {
                ...store.getState().permit.monitoringApproaches.N2O,
                gasFlowCalculation: {
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
              } as N2OMonitoringApproach,
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            n2oGas: [true],
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

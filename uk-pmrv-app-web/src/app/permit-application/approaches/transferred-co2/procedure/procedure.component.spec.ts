import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService, TransferredCO2MonitoringApproach } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { ApproachReturnLinkComponent } from '../../../shared/approach-return-link/approach-return-link.component';
import { PermitTaskComponent } from '../../../shared/permit-task/permit-task.component';
import { ProcedureFormComponent } from '../../../shared/procedure-form/procedure-form.component';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../../../testing/mock-state';
import { ProcedureComponent } from './procedure.component';

describe('ProcedureComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore;
  let component: ProcedureComponent;
  let fixture: ComponentFixture<ProcedureComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    { taskKey: 'monitoringApproaches.TRANSFERRED_CO2.transferOfCO2', statusKey: 'transferredCo2Transfer' },
  );
  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ProcedureComponent> {
    set description(value: string) {
      this.setInputValue('#procedureDescription', value);
    }

    set name(value: string) {
      this.setInputValue('#procedureDocumentName', value);
    }

    set reference(value: string) {
      this.setInputValue('#procedureReference', value);
    }

    set department(value: string) {
      this.setInputValue('#responsibleDepartmentOrRole', value);
    }

    set location(value: string) {
      this.setInputValue('#locationOfRecords', value);
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
      imports: [SharedModule, RouterTestingModule],
      declarations: [ApproachReturnLinkComponent, PermitTaskComponent, ProcedureComponent, ProcedureFormComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(ProcedureComponent);
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
              transferOfCO2: undefined,
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
      expect(page.errorSummaryErrorList).toEqual([
        'Enter a brief description of the procedure',
        'Enter the name of the procedure document',
        'Enter a procedure reference',
        'Enter the name of the department or role responsible',
        'Enter the location of the records',
      ]);

      page.description = 'procedureDescriptionValue';
      page.name = 'procedureDocumentNameValue';
      page.reference = 'procedureReferenceValue';
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
                transferOfCO2: {
                  procedureDescription: 'procedureDescriptionValue',
                  procedureDocumentName: 'procedureDocumentNameValue',
                  procedureReference: 'procedureReferenceValue',
                  responsibleDepartmentOrRole: 'responsibleDepartmentOrRoleValue',
                  locationOfRecords: 'locationOfRecordsValue',
                  appliedStandards: null,
                  diagramReference: null,
                  itSystemUsed: null,
                },
              } as TransferredCO2MonitoringApproach,
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            transferredCo2Transfer: [true],
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

      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      page.description = 'newDescr';
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
                transferOfCO2: {
                  appliedStandards: 'appliedStandards',
                  diagramReference: 'diagramReference',
                  itSystemUsed: 'itSystemUsed',
                  locationOfRecords: 'locationOfRecords',
                  procedureDescription: 'newDescr',
                  procedureDocumentName: 'procedureDocumentName',
                  procedureReference: 'procedureReference',
                  responsibleDepartmentOrRole: 'responsibleDepartmentOrRole',
                },
              } as TransferredCO2MonitoringApproach,
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            transferredCo2Transfer: [true],
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

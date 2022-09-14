import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { MeasMonitoringApproach, TasksService } from 'pmrv-api';

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
  let component: ProcedureComponent;
  let fixture: ComponentFixture<ProcedureComponent>;
  let page: Page;
  let router: Router;
  const route = new ActivatedRouteStub(
    {},
    {},
    { taskKey: 'monitoringApproaches.MEASUREMENT.emissionDetermination', statusKey: 'measurementEmission' },
  );
  let store: PermitApplicationStore;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ProcedureComponent> {
    set procedureDescriptionValue(value: string) {
      this.setInputValue('#procedureDescription', value);
    }
    set procedureDocumentNameValue(value: string) {
      this.setInputValue('#procedureDocumentName', value);
    }
    set procedureReferenceValue(value: string) {
      this.setInputValue('#procedureReference', value);
    }
    set responsibleDepartmentOrRoleValue(value: string) {
      this.setInputValue('#responsibleDepartmentOrRole', value);
    }
    set locationOfRecordsValue(value: string) {
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
      imports: [RouterTestingModule, SharedModule],
      declarations: [ProcedureComponent, ApproachReturnLinkComponent, PermitTaskComponent, ProcedureFormComponent],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
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
            MEASUREMENT: {
              ...mockPermitApplyPayload.permit.monitoringApproaches.TRANSFERRED_CO2,
              emissionDetermination: undefined,
            } as MeasMonitoringApproach,
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
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(
          {
            ...store.getState().permit,
            monitoringApproaches: {
              ...store.getState().permit.monitoringApproaches,
              MEASUREMENT: {
                ...store.getState().permit.monitoringApproaches.MEASUREMENT,
                emissionDetermination: {
                  procedureDescription: 'procedureDescriptionValue',
                  procedureDocumentName: 'procedureDocumentNameValue',
                  procedureReference: 'procedureReferenceValue',
                  responsibleDepartmentOrRole: 'responsibleDepartmentOrRoleValue',
                  locationOfRecords: 'locationOfRecordsValue',
                  appliedStandards: null,
                  diagramReference: null,
                  itSystemUsed: null,
                },
              } as MeasMonitoringApproach,
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            measurementEmission: [true],
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
              MEASUREMENT: {
                ...store.getState().permit.monitoringApproaches.MEASUREMENT,
                emissionDetermination: {
                  appliedStandards: 'appliedStandards',
                  diagramReference: 'diagramReference',
                  itSystemUsed: 'itSystemUsed',
                  locationOfRecords: 'locationOfRecords',
                  procedureDescription: 'newDescr',
                  procedureDocumentName: 'procedureDocumentName',
                  procedureReference: 'procedureReference',
                  responsibleDepartmentOrRole: 'responsibleDepartmentOrRole',
                },
              } as MeasMonitoringApproach,
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            measurementEmission: [true],
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

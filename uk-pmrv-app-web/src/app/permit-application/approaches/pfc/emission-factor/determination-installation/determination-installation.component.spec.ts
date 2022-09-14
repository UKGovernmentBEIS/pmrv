import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { PFCMonitoringApproach, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { SharedPermitModule } from '../../../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../../../../testing/mock-state';
import { DeterminationInstallationComponent } from './determination-installation.component';

describe('DeterminationInstallationComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore;
  let component: DeterminationInstallationComponent;
  let fixture: ComponentFixture<DeterminationInstallationComponent>;
  const route = new ActivatedRouteStub(
    {},
    {},
    { taskKey: 'monitoringApproaches.PFC.tier2EmissionFactor', statusKey: 'PFC_Tier2EmissionFactor' },
  );

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<DeterminationInstallationComponent> {
    get procedureDescription() {
      return this.getInputValue('#procedureDescription');
    }
    set procedureDescriptionValue(value: string) {
      this.setInputValue('#procedureDescription', value);
    }
    get procedureDocumentName() {
      return this.getInputValue('#procedureDocumentName');
    }
    set procedureDocumentNameValue(value: string) {
      this.setInputValue('#procedureDocumentName', value);
    }
    get procedureReference() {
      return this.getInputValue('#procedureReference');
    }
    set procedureReferenceValue(value: string) {
      this.setInputValue('#procedureReference', value);
    }
    get responsibleDepartmentOrRole() {
      return this.getInputValue('#responsibleDepartmentOrRole');
    }
    set responsibleDepartmentOrRoleValue(value: string) {
      this.setInputValue('#responsibleDepartmentOrRole', value);
    }
    get locationOfRecords() {
      return this.getInputValue('#locationOfRecords');
    }
    set locationOfRecordsValue(value: string) {
      this.setInputValue('#locationOfRecords', value);
    }
    get appliedStandards() {
      return this.getInputValue('#appliedStandards');
    }
    get diagramReference() {
      return this.getInputValue('#diagramReference');
    }
    get itSystemUsed() {
      return this.getInputValue('#itSystemUsed');
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
    fixture = TestBed.createComponent(DeterminationInstallationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      declarations: [DeterminationInstallationComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for new determination installation', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          monitoringApproaches: {
            PFC: {
              tier2EmissionFactor: {
                exist: true,
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

    it('should submit a valid form, update the store and navigate to schedule measurements', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

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

      const determination = (mockPermitApplyPayload.permit.monitoringApproaches.PFC as PFCMonitoringApproach)
        .tier2EmissionFactor.determinationInstallation;
      page.procedureDescriptionValue = determination.procedureDescription;
      page.procedureDocumentNameValue = determination.procedureDocumentName;
      page.procedureReferenceValue = determination.procedureReference;
      page.responsibleDepartmentOrRoleValue = determination.responsibleDepartmentOrRole;
      page.locationOfRecordsValue = determination.locationOfRecords;

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
              PFC: {
                ...store.getState().permit.monitoringApproaches.PFC,
                tier2EmissionFactor: {
                  exist: true,
                  determinationInstallation: {
                    ...determination,
                    appliedStandards: null,
                    diagramReference: null,
                    itSystemUsed: null,
                  },
                },
              } as PFCMonitoringApproach,
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            PFC_Tier2EmissionFactor: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../schedule-measurements'], { relativeTo: route });
    });
  });

  describe('for editing determination installation', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(mockState, {
          PFC_Tier2EmissionFactor: [true],
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to schedule measurements', () => {
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');

      expect(page.errorSummary).toBeFalsy();

      page.procedureDescriptionValue = 'new procedureDescription';

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
              PFC: {
                ...store.getState().permit.monitoringApproaches.PFC,
                tier2EmissionFactor: {
                  ...(mockPermitApplyPayload.permit.monitoringApproaches.PFC as PFCMonitoringApproach)
                    .tier2EmissionFactor,
                  determinationInstallation: {
                    ...(mockPermitApplyPayload.permit.monitoringApproaches.PFC as PFCMonitoringApproach)
                      .tier2EmissionFactor.determinationInstallation,
                    procedureDescription: 'new procedureDescription',
                    appliedStandards: null,
                    diagramReference: null,
                    itSystemUsed: null,
                  },
                },
              } as PFCMonitoringApproach,
            },
          },
          {
            ...mockPermitApplyPayload.permitSectionsCompleted,
            PFC_Tier2EmissionFactor: [false],
          },
        ),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['../schedule-measurements'], { relativeTo: route });
    });
  });
});

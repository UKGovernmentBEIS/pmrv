import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { FallbackMonitoringApproach, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState } from '../../../testing/mock-state';
import { FallbackModule } from '../fallback.module';
import { UncertaintyComponent } from './uncertainty.component';

describe('UncertaintyComponent', () => {
  let page: Page;
  let router: Router;
  let store: PermitApplicationStore;
  let component: UncertaintyComponent;
  let fixture: ComponentFixture<UncertaintyComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub(
    {},
    {},
    { taskKey: 'monitoringApproaches.FALLBACK.annualUncertaintyAnalysis', statusKey: 'fallbackUncertainty' },
  );
  const annualUncertaintyAnalysis = (
    mockPermitApplyPayload.permit.monitoringApproaches.FALLBACK as FallbackMonitoringApproach
  ).annualUncertaintyAnalysis;

  class Page extends BasePage<UncertaintyComponent> {
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
    fixture = TestBed.createComponent(UncertaintyComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FallbackModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for new fallback annual uncertainty analysis', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockState,
        permit: {
          ...mockState.permit,
          monitoringApproaches: {
            ...mockPermitApplyPayload.permit.monitoringApproaches,
            FALLBACK: {
              ...mockPermitApplyPayload.permit.monitoringApproaches.FALLBACK,
              annualUncertaintyAnalysis: undefined,
            } as FallbackMonitoringApproach,
          },
        },
      });
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

      page.procedureDescriptionValue = annualUncertaintyAnalysis.procedureDescription;
      page.procedureDocumentNameValue = annualUncertaintyAnalysis.procedureDocumentName;
      page.procedureReferenceValue = annualUncertaintyAnalysis.procedureReference;
      page.responsibleDepartmentOrRoleValue = annualUncertaintyAnalysis.responsibleDepartmentOrRole;

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Enter the location of the records']);

      page.locationOfRecordsValue = annualUncertaintyAnalysis.locationOfRecords;

      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild({}, { ...mockPermitApplyPayload.permitSectionsCompleted, fallbackUncertainty: [true] }),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: route,
        state: { notification: true },
      });
    });
  });

  describe('for existing fallback annual uncertainty analysis', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should fill the form from the store', () => {
      expect(page.procedureDescription).toEqual(annualUncertaintyAnalysis.procedureDescription);
      expect(page.procedureDocumentName).toEqual(annualUncertaintyAnalysis.procedureDocumentName);
      expect(page.procedureReference).toEqual(annualUncertaintyAnalysis.procedureReference);
      expect(page.responsibleDepartmentOrRole).toEqual(annualUncertaintyAnalysis.responsibleDepartmentOrRole);
      expect(page.locationOfRecords).toEqual(annualUncertaintyAnalysis.locationOfRecords);
      expect(page.appliedStandards).toEqual(annualUncertaintyAnalysis.appliedStandards);
      expect(page.diagramReference).toEqual(annualUncertaintyAnalysis.diagramReference);
      expect(page.itSystemUsed).toEqual(annualUncertaintyAnalysis.itSystemUsed);
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      expect(page.errorSummary).toBeFalsy();

      page.procedureReferenceValue = '';

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Enter a procedure reference']);

      page.procedureReferenceValue = annualUncertaintyAnalysis.procedureReference;

      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
      const navigateSpy = jest.spyOn(router, 'navigate');
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(undefined, {
          ...mockPermitApplyPayload.permitSectionsCompleted,
          fallbackUncertainty: [true],
        }),
      );

      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: route,
        state: { notification: true },
      });
    });
  });
});

import { HttpResponse } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, asyncData, BasePage, mockClass } from '../../../testing';
import { PermitApplicationModule } from '../permit-application.module';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockPermitApplyPayload } from '../testing/mock-permit-apply-action';
import { mockPostBuild, mockState, mockStateBuild } from '../testing/mock-state';
import { ManagementProceduresComponent } from './management-procedures.component';

describe('ManagementProceduresFormComponent', () => {
  let component: ManagementProceduresComponent;
  let store: PermitApplicationStore;
  let router: Router;
  let tasksService: jest.Mocked<TasksService>;
  let attachmentService: jest.Mocked<RequestTaskAttachmentsHandlingService>;
  const taskId = mockState.requestTaskId;

  const errorSummaryErrorList = [
    'Enter the name of the procedure document',
    'Enter a procedure reference',
    'Enter a brief description of the procedure',
    'Enter the name of the department or role responsible',
    'Enter the location of the records',
  ];

  const creationTest = () => {
    expect(component).toBeTruthy();
  };

  describe('for assignment of responsibilities', () => {
    let fixture: ComponentFixture<ManagementProceduresComponent>;
    let page: Page;
    const activatedRoute = new ActivatedRouteStub({ taskId: taskId }, null, {
      permitTask: 'assignmentOfResponsibilities',
    });

    class Page extends BasePage<ManagementProceduresComponent> {
      get heading() {
        return this.query<HTMLHeadingElement>('h1');
      }

      get caption() {
        return this.heading.previousElementSibling;
      }

      get subtitles() {
        return this.queryAll<HTMLParagraphElement>('p.govuk-body');
      }

      get errorSummary() {
        return this.query<HTMLDivElement>('.govuk-error-summary');
      }

      get errorSummaryErrorList() {
        return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((anchor) =>
          anchor.textContent.trim(),
        );
      }

      get submitButton() {
        return this.query<HTMLButtonElement>('button[type="submit"]');
      }

      get procedureDocumentName() {
        return this.getInputValue('#procedureDocumentName');
      }

      set procedureDocumentName(value: string) {
        this.setInputValue('#procedureDocumentName', value);
      }

      get procedureReference() {
        return this.getInputValue('#procedureReference');
      }

      set procedureReference(value: string) {
        this.setInputValue('#procedureReference', value);
      }

      get diagramReference() {
        return this.getInputValue('#diagramReference');
      }

      set diagramReference(value: string) {
        this.setInputValue('#diagramReference', value);
      }

      get procedureDescription() {
        return this.getInputValue('#procedureDescription');
      }

      set procedureDescription(value: string) {
        this.setInputValue('#procedureDescription', value);
      }

      get responsibleDepartmentOrRole() {
        return this.getInputValue('#responsibleDepartmentOrRole');
      }

      set responsibleDepartmentOrRole(value: string) {
        this.setInputValue('#responsibleDepartmentOrRole', value);
      }

      get locationOfRecords() {
        return this.getInputValue('#locationOfRecords');
      }

      set locationOfRecords(value: string) {
        this.setInputValue('#locationOfRecords', value);
      }

      get itSystemUsed() {
        return this.getInputValue('#itSystemUsed');
      }

      set itSystemUsed(value: string) {
        this.setInputValue('#itSystemUsed', value);
      }

      get appliedStandards() {
        return this.getInputValue('#appliedStandards');
      }

      set appliedStandards(value: string) {
        this.setInputValue('#appliedStandards', value);
      }
    }

    beforeEach(async () => {
      tasksService = mockClass(TasksService);

      await TestBed.configureTestingModule({
        imports: [RouterTestingModule, PermitApplicationModule],
        providers: [
          { provide: TasksService, useValue: tasksService },
          { provide: ActivatedRoute, useValue: activatedRoute },
        ],
      }).compileComponents();
    });

    const createComponent = () => {
      fixture = TestBed.createComponent(ManagementProceduresComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      router = TestBed.inject(Router);
      fixture.detectChanges();
      jest.clearAllMocks();
    };

    const headingTest = () => {
      expect(page.heading.textContent).toEqual('Assignment of responsibilities');
      expect(page.caption.textContent).toEqual('Management procedures');
      expect(page.subtitles.length).toEqual(1);
      expect(page.subtitles[0].textContent).toEqual(
        'Provide details of how the organisation assigns monitoring and reporting responsibilities. Include review processes and any training provided.',
      );
    };

    describe('for new assignment of responsibilities', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState(mockStateBuild({ ...mockPermitApplyPayload.permit, assignmentOfResponsibilities: undefined }));
      });
      beforeEach(createComponent);

      it('should create', creationTest);

      it('should display heading, caption and subtitle', headingTest);

      it('should submit a valid form, update the store and navigate to summary', () => {
        expect(page.errorSummary).toBeFalsy();

        tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
        const navigateSpy = jest.spyOn(router, 'navigate');
        page.submitButton.click();
        fixture.detectChanges();

        expect(page.errorSummary).toBeTruthy();
        expect(page.errorSummaryErrorList).toEqual(errorSummaryErrorList);

        const assignmentOfResponsibilities = mockPermitApplyPayload.permit.assignmentOfResponsibilities;
        page.procedureDocumentName = assignmentOfResponsibilities.procedureDocumentName;
        page.procedureReference = assignmentOfResponsibilities.procedureReference;
        page.diagramReference = assignmentOfResponsibilities.diagramReference;
        page.procedureDescription = assignmentOfResponsibilities.procedureDescription;
        page.responsibleDepartmentOrRole = assignmentOfResponsibilities.responsibleDepartmentOrRole;
        page.locationOfRecords = assignmentOfResponsibilities.locationOfRecords;
        page.itSystemUsed = assignmentOfResponsibilities.itSystemUsed;
        page.appliedStandards = assignmentOfResponsibilities.appliedStandards;

        page.submitButton.click();
        fixture.detectChanges();
        expect(page.errorSummary).toBeFalsy();

        expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
        expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
          mockPostBuild(
            {},
            {
              ...mockPermitApplyPayload.permitSectionsCompleted,
              ...{ assignmentOfResponsibilities: [true] },
            },
          ),
        );
        expect(store.permit.assignmentOfResponsibilities).toEqual(assignmentOfResponsibilities);
        expect(store.payload.permitSectionsCompleted.assignmentOfResponsibilities).toEqual([true]);
        expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
          relativeTo: activatedRoute,
          state: { notification: true },
        });
      });
    });

    describe('for existing assignment of responsibilities', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState(mockState);
      });

      beforeEach(createComponent);

      it('should fill the form from the store', () => {
        const mock = mockPermitApplyPayload.permit.assignmentOfResponsibilities;
        expect(page.procedureDocumentName).toEqual(mock.procedureDocumentName);
        expect(page.procedureReference).toEqual(mock.procedureReference);
        expect(page.diagramReference).toBeNull();
        expect(page.procedureDescription).toEqual(mock.procedureDescription);
        expect(page.responsibleDepartmentOrRole).toEqual(mock.responsibleDepartmentOrRole);
        expect(page.locationOfRecords).toEqual(mock.locationOfRecords);
        expect(page.itSystemUsed).toEqual(mock.itSystemUsed);
        expect(page.appliedStandards).toEqual(mock.appliedStandards);
      });

      it('should submit a valid form, update the store and navigate to summary', () => {
        tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
        const navigateSpy = jest.spyOn(router, 'navigate');

        page.submitButton.click();

        expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);

        expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
          mockPostBuild(
            {
              ...mockPermitApplyPayload.permit,
              assignmentOfResponsibilities: {
                ...mockPermitApplyPayload.permit.assignmentOfResponsibilities,
                diagramReference: null,
              },
            },
            {
              ...mockPermitApplyPayload.permitSectionsCompleted,
              ...{ assignmentOfResponsibilities: [true] },
            },
          ),
        );
        expect(store.permit.assignmentOfResponsibilities).toEqual({
          ...mockPermitApplyPayload.permit.assignmentOfResponsibilities,
          diagramReference: null,
        });
        expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
          relativeTo: activatedRoute,
          state: { notification: true },
        });
      });
    });
  });

  describe('for monitoring plan appropriateness', () => {
    let fixture: ComponentFixture<ManagementProceduresComponent>;
    let page: Page;
    const activatedRoute = new ActivatedRouteStub({ taskId: taskId }, null, {
      permitTask: 'monitoringPlanAppropriateness',
    });

    class Page extends BasePage<ManagementProceduresComponent> {
      get heading() {
        return this.query<HTMLHeadingElement>('h1');
      }

      get caption() {
        return this.heading.previousElementSibling;
      }

      get subtitles() {
        return this.queryAll<HTMLParagraphElement>('p.govuk-body');
      }

      get errorSummary() {
        return this.query<HTMLDivElement>('.govuk-error-summary');
      }

      get errorSummaryErrorList() {
        return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((anchor) =>
          anchor.textContent.trim(),
        );
      }

      get submitButton() {
        return this.query<HTMLButtonElement>('button[type="submit"]');
      }

      get procedureDocumentName() {
        return this.getInputValue('#procedureDocumentName');
      }

      set procedureDocumentName(value: string) {
        this.setInputValue('#procedureDocumentName', value);
      }

      get procedureReference() {
        return this.getInputValue('#procedureReference');
      }

      set procedureReference(value: string) {
        this.setInputValue('#procedureReference', value);
      }

      get diagramReference() {
        return this.getInputValue('#diagramReference');
      }

      set diagramReference(value: string) {
        this.setInputValue('#diagramReference', value);
      }

      get procedureDescription() {
        return this.getInputValue('#procedureDescription');
      }

      set procedureDescription(value: string) {
        this.setInputValue('#procedureDescription', value);
      }

      get responsibleDepartmentOrRole() {
        return this.getInputValue('#responsibleDepartmentOrRole');
      }

      set responsibleDepartmentOrRole(value: string) {
        this.setInputValue('#responsibleDepartmentOrRole', value);
      }

      get locationOfRecords() {
        return this.getInputValue('#locationOfRecords');
      }

      set locationOfRecords(value: string) {
        this.setInputValue('#locationOfRecords', value);
      }

      get itSystemUsed() {
        return this.getInputValue('#itSystemUsed');
      }

      set itSystemUsed(value: string) {
        this.setInputValue('#itSystemUsed', value);
      }

      get appliedStandards() {
        return this.getInputValue('#appliedStandards');
      }

      set appliedStandards(value: string) {
        this.setInputValue('#appliedStandards', value);
      }
    }

    beforeEach(async () => {
      tasksService = mockClass(TasksService);

      await TestBed.configureTestingModule({
        imports: [RouterTestingModule, PermitApplicationModule],
        providers: [
          { provide: TasksService, useValue: tasksService },
          { provide: ActivatedRoute, useValue: activatedRoute },
        ],
      }).compileComponents();
    });

    const createComponent = () => {
      fixture = TestBed.createComponent(ManagementProceduresComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      router = TestBed.inject(Router);
      fixture.detectChanges();
      jest.clearAllMocks();
    };

    const headingTest = () => {
      expect(page.heading.textContent).toEqual('Monitoring plan appropriateness');
      expect(page.caption.textContent).toEqual('Management procedures');
      expect(page.subtitles.length).toEqual(1);
      expect(page.subtitles[0].textContent).toEqual(
        'Provide details of how the organisation evaluates the appropriateness of the monitoring plan. Include any potential measures to improve the monitoring plan.',
      );
    };

    describe('for new monitoring plan appropriateness', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState(mockStateBuild({ ...mockPermitApplyPayload.permit, monitoringPlanAppropriateness: undefined }));
      });
      beforeEach(createComponent);

      it('should create', creationTest);

      it('should display heading, caption and subtitle', headingTest);

      it('should submit a valid form, update the store and navigate to summary', () => {
        expect(page.errorSummary).toBeFalsy();

        tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
        const navigateSpy = jest.spyOn(router, 'navigate');
        page.submitButton.click();
        fixture.detectChanges();

        expect(page.errorSummary).toBeTruthy();
        expect(page.errorSummaryErrorList).toEqual(errorSummaryErrorList);

        const monitoringPlanAppropriateness = mockPermitApplyPayload.permit.monitoringPlanAppropriateness;
        page.procedureDocumentName = monitoringPlanAppropriateness.procedureDocumentName;
        page.procedureReference = monitoringPlanAppropriateness.procedureReference;
        page.diagramReference = monitoringPlanAppropriateness.diagramReference;
        page.procedureDescription = monitoringPlanAppropriateness.procedureDescription;
        page.responsibleDepartmentOrRole = monitoringPlanAppropriateness.responsibleDepartmentOrRole;
        page.locationOfRecords = monitoringPlanAppropriateness.locationOfRecords;
        page.itSystemUsed = monitoringPlanAppropriateness.itSystemUsed;
        page.appliedStandards = monitoringPlanAppropriateness.appliedStandards;

        page.submitButton.click();
        fixture.detectChanges();
        expect(page.errorSummary).toBeFalsy();

        expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);

        expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
          mockPostBuild(
            {},
            {
              ...mockPermitApplyPayload.permitSectionsCompleted,
              ...{ monitoringPlanAppropriateness: [true] },
            },
          ),
        );
        expect(store.permit.monitoringPlanAppropriateness).toEqual(monitoringPlanAppropriateness);
        expect(store.payload.permitSectionsCompleted.monitoringPlanAppropriateness).toEqual([true]);
        expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
          relativeTo: activatedRoute,
          state: { notification: true },
        });
      });
    });

    describe('for existing monitoring plan appropriateness', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState(mockState);
      });
      beforeEach(createComponent);

      it('should fill the form from the store', () => {
        const mock = mockPermitApplyPayload.permit.monitoringPlanAppropriateness;
        expect(page.procedureDocumentName).toEqual(mock.procedureDocumentName);
        expect(page.procedureReference).toEqual(mock.procedureReference);
        expect(page.diagramReference).toBeNull();
        expect(page.procedureDescription).toEqual(mock.procedureDescription);
        expect(page.responsibleDepartmentOrRole).toEqual(mock.responsibleDepartmentOrRole);
        expect(page.locationOfRecords).toEqual(mock.locationOfRecords);
        expect(page.itSystemUsed).toEqual(mock.itSystemUsed);
        expect(page.appliedStandards).toEqual(mock.appliedStandards);
      });

      it('should submit a valid form, update the store and navigate to summary', () => {
        tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
        const navigateSpy = jest.spyOn(router, 'navigate');

        page.submitButton.click();

        expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);

        expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
          mockPostBuild(
            {
              ...mockPermitApplyPayload.permit,
              monitoringPlanAppropriateness: {
                ...mockPermitApplyPayload.permit.monitoringPlanAppropriateness,
                diagramReference: null,
              },
            },
            {
              ...mockPermitApplyPayload.permitSectionsCompleted,
              ...{ monitoringPlanAppropriateness: [true] },
            },
          ),
        );

        expect(store.permit.monitoringPlanAppropriateness).toEqual({
          ...mockPermitApplyPayload.permit.monitoringPlanAppropriateness,
          diagramReference: null,
        });
        expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
          relativeTo: activatedRoute,
          state: { notification: true },
        });
      });
    });
  });

  describe('for data flow activities', () => {
    let fixture: ComponentFixture<ManagementProceduresComponent>;
    let page: Page;
    const activatedRoute = new ActivatedRouteStub({ taskId: taskId }, null, {
      permitTask: 'dataFlowActivities',
    });

    class Page extends BasePage<ManagementProceduresComponent> {
      get heading() {
        return this.query<HTMLHeadingElement>('h1');
      }

      get caption() {
        return this.heading.previousElementSibling;
      }

      get subtitles() {
        return this.queryAll<HTMLParagraphElement>('p.govuk-body');
      }

      get errorSummary() {
        return this.query<HTMLDivElement>('.govuk-error-summary');
      }

      get errorSummaryErrorList() {
        return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((anchor) =>
          anchor.textContent.trim(),
        );
      }

      get submitButton() {
        return this.query<HTMLButtonElement>('button[type="submit"]');
      }

      get procedureDocumentName() {
        return this.getInputValue('#procedureDocumentName');
      }

      set procedureDocumentName(value: string) {
        this.setInputValue('#procedureDocumentName', value);
      }

      get procedureReference() {
        return this.getInputValue('#procedureReference');
      }

      set procedureReference(value: string) {
        this.setInputValue('#procedureReference', value);
      }

      get diagramReference() {
        return this.getInputValue('#diagramReference');
      }

      set diagramReference(value: string) {
        this.setInputValue('#diagramReference', value);
      }

      get procedureDescription() {
        return this.getInputValue('#procedureDescription');
      }

      set procedureDescription(value: string) {
        this.setInputValue('#procedureDescription', value);
      }

      get responsibleDepartmentOrRole() {
        return this.getInputValue('#responsibleDepartmentOrRole');
      }

      set responsibleDepartmentOrRole(value: string) {
        this.setInputValue('#responsibleDepartmentOrRole', value);
      }

      get locationOfRecords() {
        return this.getInputValue('#locationOfRecords');
      }

      set locationOfRecords(value: string) {
        this.setInputValue('#locationOfRecords', value);
      }

      get itSystemUsed() {
        return this.getInputValue('#itSystemUsed');
      }

      set itSystemUsed(value: string) {
        this.setInputValue('#itSystemUsed', value);
      }

      get appliedStandards() {
        return this.getInputValue('#appliedStandards');
      }

      set appliedStandards(value: string) {
        this.setInputValue('#appliedStandards', value);
      }

      get primaryDataSources() {
        return this.getInputValue('#primaryDataSources');
      }

      set primaryDataSources(value: string) {
        this.setInputValue('#primaryDataSources', value);
      }

      get processingSteps() {
        return this.getInputValue('#processingSteps');
      }

      set processingSteps(value: string) {
        this.setInputValue('#processingSteps', value);
      }

      get file() {
        return (
          this.query<HTMLSpanElement>('.moj-multi-file-upload__filename') ??
          this.query<HTMLSpanElement>('.moj-multi-file-upload__success')
        );
      }

      set fileValue(value: File) {
        this.setInputValue('input[type="file"]', value);
      }
    }

    beforeEach(async () => {
      tasksService = mockClass(TasksService);
      attachmentService = mockClass(RequestTaskAttachmentsHandlingService);

      await TestBed.configureTestingModule({
        imports: [RouterTestingModule, PermitApplicationModule],
        providers: [
          { provide: TasksService, useValue: tasksService },
          { provide: RequestTaskAttachmentsHandlingService, useValue: attachmentService },
          { provide: ActivatedRoute, useValue: activatedRoute },
        ],
      }).compileComponents();
    });

    const createComponent = () => {
      fixture = TestBed.createComponent(ManagementProceduresComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      router = TestBed.inject(Router);
      fixture.detectChanges();
      jest.clearAllMocks();
    };

    describe('for new data flow activities', () => {
      const errorSummaryErrorListDataFlowActivities = [
        ...errorSummaryErrorList,
        'List the primary data sources',
        'Enter a description of the processing steps for each data flow activity',
      ];
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState(mockStateBuild({ ...mockPermitApplyPayload.permit, dataFlowActivities: undefined }));
      });
      beforeEach(createComponent);

      it('should create', creationTest);

      it('should display heading, caption and subtitle', () => {
        expect(page.heading.textContent).toEqual('Data flow activities');
        expect(page.caption.textContent).toEqual('Management procedures');
        expect(page.subtitles.map((p) => p.textContent.trim())).toEqual([
          'Provide details about the procedures used to manage data flow activities.',
          'If there are several procedures, provide details of the overarching one that covers the main steps of data flow activities and attach a diagram showing how the data management procedures link together.',
        ]);
      });

      it('should submit a valid form with no file, update the store and navigate to summary', () => {
        expect(page.errorSummary).toBeFalsy();

        tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
        const navigateSpy = jest.spyOn(router, 'navigate');
        page.submitButton.click();
        fixture.detectChanges();

        expect(page.errorSummary).toBeTruthy();
        expect(page.errorSummaryErrorList).toEqual(errorSummaryErrorListDataFlowActivities);

        const dataFlowActivities = mockPermitApplyPayload.permit.dataFlowActivities;
        page.procedureDocumentName = dataFlowActivities.procedureDocumentName;
        page.procedureReference = dataFlowActivities.procedureReference;
        page.diagramReference = dataFlowActivities.diagramReference;
        page.procedureDescription = dataFlowActivities.procedureDescription;
        page.responsibleDepartmentOrRole = dataFlowActivities.responsibleDepartmentOrRole;
        page.locationOfRecords = dataFlowActivities.locationOfRecords;
        page.itSystemUsed = dataFlowActivities.itSystemUsed;
        page.appliedStandards = dataFlowActivities.appliedStandards;
        page.primaryDataSources = dataFlowActivities.primaryDataSources;
        page.processingSteps = dataFlowActivities.processingSteps;

        page.submitButton.click();
        fixture.detectChanges();
        expect(page.errorSummary).toBeFalsy();

        expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
        expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
          mockPostBuild(
            {},
            {
              ...mockPermitApplyPayload.permitSectionsCompleted,
              ...{ dataFlowActivities: [true] },
            },
          ),
        );

        expect(store.permit.dataFlowActivities).toEqual(dataFlowActivities);
        expect(store.payload.permitSectionsCompleted.dataFlowActivities).toEqual([true]);
        expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
          relativeTo: activatedRoute,
          state: { notification: true },
        });
      });
    });

    describe('for existing data flow activities', () => {
      beforeEach(() => {
        store = TestBed.inject(PermitApplicationStore);
        store.setState(mockState);
      });
      beforeEach(createComponent);

      it('should fill the form from the store', () => {
        const mock = mockPermitApplyPayload.permit.dataFlowActivities;
        expect(page.procedureDocumentName).toEqual(mock.procedureDocumentName);
        expect(page.procedureReference).toEqual(mock.procedureReference);
        expect(page.diagramReference).toBeNull();
        expect(page.procedureDescription).toEqual(mock.procedureDescription);
        expect(page.responsibleDepartmentOrRole).toEqual(mock.responsibleDepartmentOrRole);
        expect(page.locationOfRecords).toEqual(mock.locationOfRecords);
        expect(page.itSystemUsed).toEqual(mock.itSystemUsed);
        expect(page.appliedStandards).toEqual(mock.appliedStandards);
        expect(page.primaryDataSources).toEqual(mock.primaryDataSources);
        expect(page.processingSteps).toEqual(mock.processingSteps);
      });

      it('should submit a valid form with no file, update the store and navigate to summary', () => {
        tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
        const navigateSpy = jest.spyOn(router, 'navigate');

        const diagramRef = 'diagramRef';
        page.diagramReference = diagramRef;
        fixture.detectChanges();

        page.submitButton.click();

        expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);

        expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
          mockPostBuild(
            {
              ...mockPermitApplyPayload.permit,
              dataFlowActivities: {
                ...mockPermitApplyPayload.permit.dataFlowActivities,
                diagramReference: diagramRef,
              },
            },
            {
              ...mockPermitApplyPayload.permitSectionsCompleted,
              ...{ dataFlowActivities: [true] },
            },
          ),
        );

        expect(store.permit.dataFlowActivities).toEqual({
          ...mockPermitApplyPayload.permit.dataFlowActivities,
          diagramReference: diagramRef,
        });
        expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
          relativeTo: activatedRoute,
          state: { notification: true },
        });
      });
    });

    describe('for submitting data flow activities diagram', () => {
      const diagramId = '8a18a8b6-272f-4d09-bc84-cf384276d7db';
      const diagramFileName = 'data_flow_activities_diagram.pdf';

      beforeEach(() => {
        attachmentService.uploadRequestTaskAttachmentUsingPOST.mockReturnValue(
          asyncData<any>(new HttpResponse({ body: { uuid: diagramId } })),
        );
        store = TestBed.inject(PermitApplicationStore);
        store.setState(mockState);
      });
      beforeEach(createComponent);

      it('should attach diagram file and submit a valid form, update the store and navigate to summary', async () => {
        const file = new File(['some content'], diagramFileName);
        page.fileValue = file;
        fixture.detectChanges();
        await fixture.whenStable();
        fixture.detectChanges();

        expect(page.file.textContent.trim()).toEqual(diagramFileName + ' has been uploaded');
        expect(attachmentService.uploadRequestTaskAttachmentUsingPOST).toHaveBeenCalledWith(
          file,
          { requestTaskActionType: 'PERMIT_ISSUANCE_UPLOAD_SECTION_ATTACHMENT', requestTaskId: taskId },
          'events',
          true,
        );

        page.submitButton.click();
        fixture.detectChanges();

        expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);

        expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
          mockPostBuild(
            {
              ...mockPermitApplyPayload.permit,
              dataFlowActivities: {
                ...mockPermitApplyPayload.permit.dataFlowActivities,
                diagramAttachmentId: diagramId,
              },
            },
            {
              ...mockPermitApplyPayload.permitSectionsCompleted,
              ...{ dataFlowActivities: [true] },
            },
          ),
        );
      });
    });
  });
});

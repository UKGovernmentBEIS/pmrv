import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockState } from '../../testing/mock-state';
import { ManagementProceduresSummaryComponent } from './management-procedures-summary.component';

describe('ManagementProceduresSummaryComponent', () => {
  let component: ManagementProceduresSummaryComponent;
  let store: PermitApplicationStore;
  const taskId = mockState.requestTaskId;

  describe('for assignment of responsibilities', () => {
    let fixture: ComponentFixture<ManagementProceduresSummaryComponent>;
    let page: Page;
    const activatedRoute = new ActivatedRouteStub({ taskId }, null, {
      permitTask: 'assignmentOfResponsibilities',
    });

    class Page extends BasePage<ManagementProceduresSummaryComponent> {
      get summaryListValues() {
        return Array.from(this.queryAll<HTMLDivElement>('.govuk-summary-list__row'))
          .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
          .map((pair) => pair.map((element) => element.textContent.trim()));
      }

      get heading() {
        return this.query<HTMLHeadingElement>('h1');
      }

      get caption() {
        return this.heading.previousElementSibling;
      }

      get subtitle() {
        return this.query<HTMLParagraphElement>('p');
      }

      get notificationBanner() {
        return this.query('.govuk-notification-banner');
      }
    }

    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [RouterTestingModule, PermitApplicationModule],
        providers: [
          { provide: ActivatedRoute, useValue: activatedRoute },
          { provide: TasksService, useValue: mockClass(TasksService) },
        ],
      }).compileComponents();
    });

    beforeEach(() => {
      const router = TestBed.inject(Router);
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { notification: true } } } as any);

      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
      fixture = TestBed.createComponent(ManagementProceduresSummaryComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the list of data', () => {
      const mock = mockPermitApplyPayload.permit.assignmentOfResponsibilities;

      expect(page.summaryListValues).toEqual([
        ['Procedure document', mock.procedureDocumentName],
        ['Procedure reference', mock.procedureReference],
        ['Procedure description', mock.procedureDescription],
        [
          'Department or role that’s responsible for the procedure and the data generated',
          mock.responsibleDepartmentOrRole,
        ],
        ['Location of records', mock.locationOfRecords],
        ['IT system used', mock.itSystemUsed],
        ['European or other standards applied', mock.appliedStandards],
      ]);
    });

    it('should display the notification banner', () => {
      expect(page.notificationBanner).toBeTruthy();
    });
  });

  describe('for monitoring plan appropriateness', () => {
    let fixture: ComponentFixture<ManagementProceduresSummaryComponent>;
    let page: Page;
    const activatedRoute = new ActivatedRouteStub({ taskId: 1 }, null, {
      permitTask: 'monitoringPlanAppropriateness',
    });

    class Page extends BasePage<ManagementProceduresSummaryComponent> {
      get summaryListValues() {
        return Array.from(this.queryAll<HTMLDivElement>('.govuk-summary-list__row'))
          .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
          .map((pair) => pair.map((element) => element.textContent.trim()));
      }

      get heading() {
        return this.query<HTMLHeadingElement>('h1');
      }

      get caption() {
        return this.heading.previousElementSibling;
      }

      get subtitle() {
        return this.query<HTMLParagraphElement>('p');
      }

      get notificationBanner() {
        return this.query('.govuk-notification-banner');
      }
    }

    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [RouterTestingModule, SharedModule, PermitApplicationModule],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: activatedRoute,
          },
        ],
      }).compileComponents();
    });

    beforeEach(() => {
      const router = TestBed.inject(Router);
      jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { notification: true } } } as any);

      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
      fixture = TestBed.createComponent(ManagementProceduresSummaryComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the list of data', () => {
      const mock = mockPermitApplyPayload.permit.monitoringPlanAppropriateness;

      expect(page.summaryListValues).toEqual([
        ['Procedure document', mock.procedureDocumentName],
        ['Procedure reference', mock.procedureReference],
        ['Procedure description', mock.procedureDescription],
        [
          'Department or role that’s responsible for the procedure and the data generated',
          mock.responsibleDepartmentOrRole,
        ],
        ['Location of records', mock.locationOfRecords],
        ['IT system used', mock.itSystemUsed],
        ['European or other standards applied', mock.appliedStandards],
      ]);
    });

    it('should display the notification banner', () => {
      expect(page.notificationBanner).toBeTruthy();
    });
  });

  describe('for data flow activities', () => {
    let fixture: ComponentFixture<ManagementProceduresSummaryComponent>;
    let page: Page;
    const activatedRoute = new ActivatedRouteStub({ taskId: taskId }, null, {
      permitTask: 'dataFlowActivities',
    });

    class Page extends BasePage<ManagementProceduresSummaryComponent> {
      get summaryListValues() {
        return Array.from(this.queryAll<HTMLDivElement>('.govuk-summary-list__row'))
          .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
          .map((pair) => pair.map((element) => element.textContent.trim()));
      }

      get heading() {
        return this.query<HTMLHeadingElement>('h1');
      }

      get caption() {
        return this.heading.previousElementSibling;
      }

      get subtitles() {
        return this.queryAll<HTMLParagraphElement>('p.govuk-body');
      }

      get file() {
        const rows = this.queryAll<HTMLDListElement>('dl dd.govuk-summary-list__value');
        return rows.slice(rows.length - 1)[0];
      }
    }

    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [RouterTestingModule, SharedModule, PermitApplicationModule],
        providers: [
          {
            provide: ActivatedRoute,
            useValue: activatedRoute,
          },
        ],
      }).compileComponents();
    });

    const fileId = 'fileId';
    const fileName = 'fileName.pdf';

    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockState,
        permit: {
          ...mockState.permit,
          dataFlowActivities: {
            ...mockState.permit.dataFlowActivities,
            diagramAttachmentId: fileId,
          },
        },
        permitAttachments: {
          [fileId]: fileName,
        },
      });
      fixture = TestBed.createComponent(ManagementProceduresSummaryComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the list of data', () => {
      const mock = mockPermitApplyPayload.permit.dataFlowActivities;

      expect(page.summaryListValues).toEqual([
        ['Procedure document', mock.procedureDocumentName],
        ['Procedure reference', mock.procedureReference],
        ['Procedure description', mock.procedureDescription],
        [
          'Department or role that’s responsible for the procedure and the data generated',
          mock.responsibleDepartmentOrRole,
        ],
        ['Location of records', mock.locationOfRecords],
        ['IT system used', mock.itSystemUsed],
        ['European or other standards applied', mock.appliedStandards],
        ['Primary data sources', mock.primaryDataSources],
        ['Processing steps for each data flow activity', mock.processingSteps],
        ['Diagram', fileName],
      ]);
    });

    it('should display heading, caption and subtitle', () => {
      expect(page.heading.textContent.trim()).toEqual('Data flow activities');
      expect(page.caption.textContent.trim()).toEqual('Management procedures');
      expect(page.subtitles.map((p) => p.textContent.trim())).toEqual([
        'Provide details about the procedures used to manage data flow activities.',
        'If there are several procedures, provide details of the overarching one that covers the main steps of data flow activities and attach a diagram showing how the data management procedures link together.',
      ]);
    });

    it('should mention the attachment', () => {
      expect(page.file.textContent).toEqual(fileName);
    });
  });
});

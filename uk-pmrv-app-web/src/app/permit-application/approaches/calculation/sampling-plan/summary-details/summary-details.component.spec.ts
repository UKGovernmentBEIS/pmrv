import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { CalculationMonitoringApproach, TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../../../../testing';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockState } from '../../../../testing/mock-state';
import { CalculationModule } from '../../calculation.module';
import { SummaryDetailsComponent } from './summary-details.component';

describe('SummaryDetailsComponent', () => {
  let page: Page;
  let store: PermitApplicationStore;
  let component: SummaryDetailsComponent;
  let fixture: ComponentFixture<SummaryDetailsComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SummaryDetailsComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalculationModule, RouterTestingModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(SummaryDetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the list of data', () => {
    const samplingPlan = (mockState.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach)
      .samplingPlan;

    expect(page.summaryListValues).toEqual([
      ['Do you undertake sampling and analysis?', 'Yes'],
      ['Procedure description', samplingPlan.details.analysis.procedureDescription],
      ['Procedure document', samplingPlan.details.analysis.procedureDocumentName],
      ['Procedure reference', samplingPlan.details.analysis.procedureReference],
      ['Diagram reference', samplingPlan.details.analysis.diagramReference],
      ['Department or role that’s responsible', samplingPlan.details.analysis.responsibleDepartmentOrRole],
      ['Location of records', samplingPlan.details.analysis.locationOfRecords],
      ['IT system used', samplingPlan.details.analysis.itSystemUsed],
      ['European or other standards applied', samplingPlan.details.analysis.appliedStandards],
      ['Procedure description', samplingPlan.details.procedurePlan.procedureDescription],
      ['Procedure document', samplingPlan.details.procedurePlan.procedureDocumentName],
      ['Procedure reference', samplingPlan.details.procedurePlan.procedureReference],
      ['Diagram reference', samplingPlan.details.procedurePlan.diagramReference],
      ['Department or role that’s responsible', samplingPlan.details.procedurePlan.responsibleDepartmentOrRole],
      ['Location of records', samplingPlan.details.procedurePlan.locationOfRecords],
      ['IT system used', samplingPlan.details.procedurePlan.itSystemUsed],
      ['European or other standards applied', samplingPlan.details.procedurePlan.appliedStandards],
      ['Sampling plan procedure', 'samplingPlan.pdf'],
      ['Procedure description', samplingPlan.details.appropriateness.procedureDescription],
      ['Procedure document', samplingPlan.details.appropriateness.procedureDocumentName],
      ['Procedure reference', samplingPlan.details.appropriateness.procedureReference],
      ['Diagram reference', samplingPlan.details.appropriateness.diagramReference],
      ['Department or role that’s responsible', samplingPlan.details.appropriateness.responsibleDepartmentOrRole],
      ['Location of records', samplingPlan.details.appropriateness.locationOfRecords],
      ['IT system used', samplingPlan.details.appropriateness.itSystemUsed],
      ['European or other standards applied', samplingPlan.details.appropriateness.appliedStandards],
      ['Are stock estimates carried out as part of the emission calculations?', 'Yes'],
      ['Procedure description', samplingPlan.details.yearEndReconciliation.procedureForm.procedureDescription],
      ['Procedure document', samplingPlan.details.yearEndReconciliation.procedureForm.procedureDocumentName],
      ['Procedure reference', samplingPlan.details.yearEndReconciliation.procedureForm.procedureReference],
      ['Diagram reference', samplingPlan.details.yearEndReconciliation.procedureForm.diagramReference],
      [
        'Department or role that’s responsible',
        samplingPlan.details.yearEndReconciliation.procedureForm.responsibleDepartmentOrRole,
      ],
      ['Location of records', samplingPlan.details.yearEndReconciliation.procedureForm.locationOfRecords],
      ['IT system used', samplingPlan.details.yearEndReconciliation.procedureForm.itSystemUsed],
      [
        'European or other standards applied',
        samplingPlan.details.yearEndReconciliation.procedureForm.appliedStandards,
      ],
    ]);
  });
});

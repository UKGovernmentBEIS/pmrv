import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { N2OMonitoringApproach, TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../../../../testing';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../../../testing/mock-state';
import { N2oModule } from '../../n2o.module';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let page: Page;
  let component: SummaryComponent;
  let store: PermitApplicationStore;
  let fixture: ComponentFixture<SummaryComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SummaryComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDListElement>('dl').map((installation) =>
        Array.from(installation.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [N2oModule, RouterTestingModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);

    store.setState(
      mockStateBuild(
        {},
        {
          ...mockPermitApplyPayload.permitSectionsCompleted,
          n2oGas: [true],
        },
      ),
    );

    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the details', () => {
    const deductions = (mockPermitApplyPayload.permit.monitoringApproaches.N2O as N2OMonitoringApproach)
      .gasFlowCalculation;
    expect(page.summaryListValues).toEqual([
      [
        deductions.procedureForm.procedureDescription,
        deductions.procedureForm.procedureDocumentName,
        deductions.procedureForm.procedureReference,
        deductions.procedureForm.diagramReference,
        deductions.procedureForm.responsibleDepartmentOrRole,
        deductions.procedureForm.locationOfRecords,
        deductions.procedureForm.itSystemUsed,
        deductions.procedureForm.appliedStandards,
      ],
    ]);
  });
});

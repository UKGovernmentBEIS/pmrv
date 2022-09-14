import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { MeasMonitoringApproach, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../../../testing/mock-state';
import { MeasurementModule } from '../../measurement.module';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let page: Page;
  let component: SummaryComponent;
  let store: PermitApplicationStore;
  let fixture: ComponentFixture<SummaryComponent>;

  const route = new ActivatedRouteStub(
    {},
    {},
    { taskKey: 'monitoringApproaches.MEASUREMENT.gasFlowCalculation', statusKey: 'measurementGasflow' },
  );
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
      imports: [MeasurementModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(
      mockStateBuild(
        {},
        {
          ...mockPermitApplyPayload.permitSectionsCompleted,
          MEASUREMENT_Gasflow: [true],
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
    const gasFlowCalculation = (
      mockPermitApplyPayload.permit.monitoringApproaches.MEASUREMENT as MeasMonitoringApproach
    ).gasFlowCalculation;

    expect(page.summaryListValues).toEqual([
      [
        gasFlowCalculation.procedureForm.procedureDescription,
        gasFlowCalculation.procedureForm.procedureDocumentName,
        gasFlowCalculation.procedureForm.procedureReference,
        gasFlowCalculation.procedureForm.diagramReference,
        gasFlowCalculation.procedureForm.responsibleDepartmentOrRole,
        gasFlowCalculation.procedureForm.locationOfRecords,
        gasFlowCalculation.procedureForm.itSystemUsed,
        gasFlowCalculation.procedureForm.appliedStandards,
      ],
    ]);
  });
});

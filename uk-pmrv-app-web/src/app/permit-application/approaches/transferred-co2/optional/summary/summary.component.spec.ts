import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { TasksService, TransferredCO2MonitoringApproach } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../../../testing/mock-state';
import { TransferredCO2Module } from '../../transferred-co2.module';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let page: Page;
  let component: SummaryComponent;
  let store: PermitApplicationStore;
  let fixture: ComponentFixture<SummaryComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub(
    {},
    {},
    {
      taskKey: 'monitoringApproaches.TRANSFERRED_CO2.deductionsToAmountOfTransferredCO2',
      statusKey: 'transferredCo2Deductions',
    },
  );

  class Page extends BasePage<SummaryComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDListElement>('dl').map((installation) =>
        Array.from(installation.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, TransferredCO2Module],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
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
          TRANSFERRED_CO2_Deductions: [true],
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
    const deductions = (
      mockPermitApplyPayload.permit.monitoringApproaches.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach
    ).deductionsToAmountOfTransferredCO2;
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

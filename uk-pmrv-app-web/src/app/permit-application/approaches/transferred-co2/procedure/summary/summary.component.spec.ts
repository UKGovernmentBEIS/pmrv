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
  const route = new ActivatedRouteStub({}, {}, { taskKey: 'monitoringApproaches.TRANSFERRED_CO2.transferOfCO2' });

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
          TRANSFERRED_CO2_Transfer: [true],
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
    const transferOfCO2 = (
      mockPermitApplyPayload.permit.monitoringApproaches.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach
    ).transferOfCO2;
    expect(page.summaryListValues).toEqual([
      [
        transferOfCO2.procedureDescription,
        transferOfCO2.procedureDocumentName,
        transferOfCO2.procedureReference,
        transferOfCO2.diagramReference,
        transferOfCO2.responsibleDepartmentOrRole,
        transferOfCO2.locationOfRecords,
        transferOfCO2.itSystemUsed,
        transferOfCO2.appliedStandards,
      ],
    ]);
  });
});

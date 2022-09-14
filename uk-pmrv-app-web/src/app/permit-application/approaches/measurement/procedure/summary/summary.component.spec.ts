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

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({}, {}, { taskKey: 'monitoringApproaches.MEASUREMENT.emissionDetermination' });

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
          MEASUREMENT_Emission: [true],
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
    const emissionDetermination = (
      mockPermitApplyPayload.permit.monitoringApproaches.MEASUREMENT as MeasMonitoringApproach
    ).emissionDetermination;

    expect(page.summaryListValues).toEqual([
      [
        emissionDetermination.procedureDescription,
        emissionDetermination.procedureDocumentName,
        emissionDetermination.procedureReference,
        emissionDetermination.diagramReference,
        emissionDetermination.responsibleDepartmentOrRole,
        emissionDetermination.locationOfRecords,
        emissionDetermination.itSystemUsed,
        emissionDetermination.appliedStandards,
      ],
    ]);
  });
});

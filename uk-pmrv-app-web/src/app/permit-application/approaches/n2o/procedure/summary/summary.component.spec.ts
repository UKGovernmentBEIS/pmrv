import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { N2OMonitoringApproach, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
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
  const route = new ActivatedRouteStub({}, {}, { taskKey: 'monitoringApproaches.N2O.emissionDetermination' });

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
          N2O_Emission: [true],
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
    const emissionDetermination = (mockPermitApplyPayload.permit.monitoringApproaches.N2O as N2OMonitoringApproach)
      .emissionDetermination;
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

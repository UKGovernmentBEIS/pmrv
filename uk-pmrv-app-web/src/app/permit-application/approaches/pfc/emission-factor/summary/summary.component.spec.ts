import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { SharedPermitModule } from '../../../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockState, mockStateBuild } from '../../../../testing/mock-state';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let page: Page;
  let store: PermitApplicationStore;
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  const route = new ActivatedRouteStub(
    {},
    {},
    { taskKey: 'monitoringApproaches.PFC.tier2EmissionFactor', statusKey: 'PFC_Tier2EmissionFactor' },
  );

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SummaryComponent> {
    get summaryDefinitions() {
      return this.queryAll('dd').map((row) => row.textContent.trim());
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      declarations: [SummaryComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for tier2 emission factor with one step', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          monitoringApproaches: {
            PFC: {
              tier2EmissionFactor: {
                exist: false,
              },
            },
          },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the tier2 emission factor summary', () => {
      expect(page.summaryDefinitions).toEqual(['No']);
    });
  });

  describe('for tier2 emission factor with all steps', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the tier2 emission factor summary', () => {
      expect(page.summaryDefinitions).toEqual([
        'Yes',
        'procedureDescription2',
        'procedureDocumentName2',
        'procedureReference2',
        'responsibleDepartmentOrRole2',
        'locationOfRecords2',
        'procedureDescription1',
        'procedureDocumentName1',
        'procedureReference1',
        'responsibleDepartmentOrRole1',
        'locationOfRecords1',
      ]);
    });
  });
});

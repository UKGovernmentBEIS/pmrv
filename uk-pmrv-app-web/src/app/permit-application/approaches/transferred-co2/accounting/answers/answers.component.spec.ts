import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { SharedPermitModule } from '../../../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockState, mockStateBuild } from '../../../../testing/mock-state';
import { AnswersComponent } from './answers.component';

describe('AnswersComponent', () => {
  let page: Page;
  let store: PermitApplicationStore;
  let component: AnswersComponent;
  let fixture: ComponentFixture<AnswersComponent>;
  const route = new ActivatedRouteStub(
    {},
    {},
    { taskKey: 'monitoringApproaches.TRANSFERRED_CO2.accountingEmissions', statusKey: 'TRANSFERRED_CO2_Accounting' },
  );

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<AnswersComponent> {
    get summaryDefinitions() {
      return this.queryAll('dd').map((row) => row.textContent.trim());
    }
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryErrorList() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AnswersComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      declarations: [AnswersComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for accounting emissions with one step', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          monitoringApproaches: {
            TRANSFERRED_CO2: {
              accountingEmissions: {
                chemicallyBound: true,
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

    it('should display the accounting emissions summary', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.summaryDefinitions).toEqual(['Yes']);
    });
  });

  describe('for accounting emissions with two steps', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the accounting emissions summary', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.summaryDefinitions).toEqual([
        'No',
        'ref1, Ultrasonic meter , Specified uncertainty ±2',
        'Daily',
        'Tier 4',
      ]);
    });
  });

  describe('for accounting emissions with two steps and no tier', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          monitoringApproaches: {
            TRANSFERRED_CO2: {
              accountingEmissions: {
                chemicallyBound: false,
                accountingEmissionsDetails: {
                  measurementDevicesOrMethods: ['16236817394240.1574963093314700'],
                  samplingFrequency: 'DAILY',
                  tier: 'NO_TIER',
                  noTierJustification: 'explain',
                },
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

    it('should display the accounting emissions summary', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.summaryDefinitions).toEqual([
        'No',
        'ref1, Ultrasonic meter , Specified uncertainty ±2',
        'Daily',
        'No tier',
        'explain',
      ]);
    });
  });

  describe('for accounting emissions with three steps', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          monitoringApproaches: {
            TRANSFERRED_CO2: {
              accountingEmissions: {
                chemicallyBound: false,
                accountingEmissionsDetails: {
                  measurementDevicesOrMethods: ['16236817394240.1574963093314700'],
                  samplingFrequency: 'DAILY',
                  tier: 'TIER_3',
                  isHighestRequiredTier: false,
                  noHighestRequiredTierJustification: {
                    isCostUnreasonable: true,
                    isTechnicallyInfeasible: true,
                    technicalInfeasibilityExplanation: 'technicalInfeasibilityExplanation',
                  },
                },
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

    it('should display the accounting emissions summary', () => {
      expect(page.errorSummary).toBeFalsy();
      expect(page.summaryDefinitions).toEqual([
        'No',
        'ref1, Ultrasonic meter , Specified uncertainty ±2',
        'Daily',
        'Tier 3',
        'No',
        'Unreasonable cost  Technical infeasibility',
        'technicalInfeasibilityExplanation',
      ]);
    });
  });

  describe('for accounting emissions with deleted measurement devices', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          measurementDevicesOrMethods: [],
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the accounting emissions summary', () => {
      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Select at least one measurement device']);
      expect(page.summaryDefinitions).toEqual(['No', 'Select at least one measurement device', 'Daily', 'Tier 4']);
    });
  });

  describe('for accounting emissions with measurement device not exist', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockStateBuild({
          monitoringApproaches: {
            TRANSFERRED_CO2: {
              type: 'TRANSFERRED_CO2',
              accountingEmissions: {
                chemicallyBound: false,
                accountingEmissionsDetails: {
                  measurementDevicesOrMethods: ['16236817394240.1574963093314700', 'unspecified'],
                  samplingFrequency: 'DAILY',
                  tier: 'TIER_4',
                },
              },
            },
          },
        }),
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the accounting emissions summary', () => {
      expect(page.errorSummary).toBeTruthy();
      expect(page.errorSummaryErrorList).toEqual(['Check your Answer - Error']);
      expect(page.summaryDefinitions).toEqual(['No', 'Check your Answer - Error', 'Daily', 'Tier 4']);
    });
  });
});

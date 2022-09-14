import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { CalculationModule } from '../../approaches/calculation/calculation.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../testing/mock-state';
import { CalculationComponent } from './calculation.component';

describe('CalculationComponent', () => {
  let page: Page;
  let store: PermitApplicationStore;
  let component: CalculationComponent;
  let fixture: ComponentFixture<CalculationComponent>;

  class Page extends BasePage<CalculationComponent> {
    get tasks() {
      return this.queryAll<HTMLLIElement>('li');
    }
    get tables() {
      return this.queryAll<HTMLTableElement>('govuk-table');
    }
    get rows() {
      return this.queryAll<HTMLTableRowElement>('govuk-table tr')
        .filter((el) => !el.querySelector('th'))
        .map((el) => Array.from(el.querySelectorAll('td')).map((td) => td.textContent.trim()));
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(CalculationComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, CalculationModule, SharedPermitModule, RouterTestingModule],
      declarations: [CalculationComponent],
    }).compileComponents();
  });

  describe('without source stream categories', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          ...mockPermitApplyPayload.permit,
          monitoringApproaches: {
            CALCULATION: {},
          },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the tasks', () => {
      expect(page.tables.length).toEqual(1);
      expect(page.rows).toEqual([['Calculation', '0t', '0t', '0t', '0t']]);

      expect(page.tasks).toBeTruthy();
      expect(
        page.tasks.map((el) => [
          el.querySelector('a').textContent.trim(),
          el.querySelector('govuk-tag').textContent?.trim(),
        ]),
      ).toEqual([
        ['Add a source stream category', 'cannot start yet'],
        ['Approach description', 'not started'],
        ['Sampling plan', 'not started'],
      ]);
    });
  });

  describe('with source stream categories', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockStateBuild({
          monitoringApproaches: {
            CALCULATION: {
              type: 'CALCULATION',
              approachDescription: 'Calculation approach description',
              samplingPlan: {
                exist: true,
                details: {
                  procedurePlan: {
                    appliedStandards: 'Plan standards',
                    diagramReference: 'Plan reference',
                    itSystemUsed: 'Plan it',
                    procedureDescription: 'pro1',
                    procedureDocumentName: 'pro1',
                    procedureReference: 'pro1',
                    responsibleDepartmentOrRole: 'pro1',
                    locationOfRecords: 'pro1',
                    procedurePlanIds: ['6e52fd76-4416-4f1f-b7c6-c07b3231d075'],
                  },
                  appropriateness: {
                    appliedStandards: 'Appropriateness standards',
                    diagramReference: 'Appropriateness reference',
                    itSystemUsed: 'Appropriateness it',
                    procedureDescription: 'Appropriateness description',
                    procedureDocumentName: 'Appropriateness name',
                    procedureReference: 'Appropriateness proc ref',
                    responsibleDepartmentOrRole: 'Appropriateness department',
                    locationOfRecords: 'Appropriateness location',
                  },
                  yearEndReconciliation: {
                    exist: true,
                    procedureForm: {
                      appliedStandards: 'Reconciliation standards',
                      diagramReference: 'Reconciliation reference',
                      itSystemUsed: 'Reconciliation it',
                      procedureDescription: 'Reconciliation description',
                      procedureDocumentName: 'Reconciliation name',
                      procedureReference: 'Reconciliation proc ref',
                      responsibleDepartmentOrRole: 'Reconciliation department',
                      locationOfRecords: 'Reconciliation location',
                    },
                  },
                },
              },
              sourceStreamCategoryAppliedTiers: [
                {
                  sourceStreamCategory: {
                    sourceStream: '16236817394240.1574963093314663',
                    emissionSources: ['16245246343280.27155194483385103'],
                    annualEmittedCO2Tonnes: 30,
                    calculationMethod: 'STANDARD',
                    categoryType: 'MAJOR',
                  },
                  emissionFactor: {
                    exist: true,
                    tier: 'TIER_1',
                    standardReferenceSource: {
                      applyDefaultValue: true,
                      defaultValue: '10',
                      type: 'BRITISH_CERAMIC_CONFEDERATION',
                    },
                    isHighestRequiredTier: true,
                  },
                },
              ],
            },
          },
        }),
        permitSectionsCompleted: {
          CALCULATION_Description: [true],
          CALCULATION_Plan: [true],
          CALCULATION_Category: [true],
          CALCULATION_Emission_Factor: [true],
          emissionPoints: [true],
          emissionSources: [true],
          sourceStreams: [true],
        },
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the tasks', () => {
      expect(page.tables.length).toEqual(2);
      expect(page.rows).toEqual([
        ['Calculation', '0t', '0t', '0t', '30t'],
        [
          '13123124 White Spirit & SBP: Major',
          '30',
          'Not used',
          'Not used',
          'Tier 1',
          'Not used',
          'Not used',
          'Not used',
          'Not used',
          'in progress',
        ],
      ]);

      expect(page.tasks).toBeTruthy();
      expect(
        page.tasks.map((el) => [
          el.querySelector('a').textContent.trim(),
          el.querySelector('govuk-tag').textContent?.trim(),
        ]),
      ).toEqual([
        ['Approach description', 'completed'],
        ['Sampling plan', 'completed'],
      ]);
    });
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { PFCModule } from '../../approaches/pfc/pfc.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../testing/mock-state';
import { PfcComponent } from './pfc.component';

describe('PfcComponent', () => {
  let page: Page;
  let store: PermitApplicationStore;
  let component: PfcComponent;
  let fixture: ComponentFixture<PfcComponent>;

  class Page extends BasePage<PfcComponent> {
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
    fixture = TestBed.createComponent(PfcComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, PFCModule, SharedPermitModule, RouterTestingModule],
      declarations: [PfcComponent],
    }).compileComponents();
  });

  describe('without source stream categories', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          ...mockPermitApplyPayload.permit,
          monitoringApproaches: {
            PFC: {},
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
      expect(page.rows).toEqual([['Perfluorocarbons (PFC)', '0t', '0t', '0t', '0t']]);

      expect(page.tasks).toBeTruthy();
      expect(
        page.tasks.map((el) => [
          el.querySelector('a').textContent.trim(),
          el.querySelector('govuk-tag').textContent?.trim(),
        ]),
      ).toEqual([
        ['Add a source stream category', 'cannot start yet'],
        ['Approach description', 'not started'],
        ['Cell and anode types', 'not started'],
        ['Collection efficiency', 'not started'],
        ['Tier 2 - Emission factor', 'not started'],
      ]);
    });
  });

  describe('with source stream categories', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockStateBuild({
          monitoringApproaches: {
            PFC: {
              type: 'PFC',
              approachDescription: 'PFC approach description',
              cellAndAnodeTypes: [
                {
                  cellType: 'cell type one',
                  anodeType: 'anode type one',
                },
              ],
              collectionEfficiency: {
                procedureDescription: 'procedureDescription',
                procedureDocumentName: 'procedureDocumentName',
                procedureReference: 'procedureReference',
                diagramReference: 'diagramReference',
                responsibleDepartmentOrRole: 'responsibleDepartmentOrRole',
                locationOfRecords: 'locationOfRecords',
                itSystemUsed: 'itSystemUsed',
                appliedStandards: 'appliedStandards',
              },
              tier2EmissionFactor: {
                exist: true,
                determinationInstallation: {
                  procedureDescription: 'procedureDescription1',
                  procedureDocumentName: 'procedureDocumentName1',
                  procedureReference: 'procedureReference1',
                  responsibleDepartmentOrRole: 'responsibleDepartmentOrRole1',
                  locationOfRecords: 'locationOfRecords1',
                },
                scheduleMeasurements: {
                  procedureDescription: 'procedureDescription2',
                  procedureDocumentName: 'procedureDocumentName2',
                  procedureReference: 'procedureReference2',
                  responsibleDepartmentOrRole: 'responsibleDepartmentOrRole2',
                  locationOfRecords: 'locationOfRecords2',
                },
              },
              sourceStreamCategoryAppliedTiers: [
                {
                  sourceStreamCategory: {
                    sourceStream: '16236817394240.1574963093314663',
                    emissionSources: ['16245246343280.27155194483385103'],
                    emissionPoints: ['16363790610230.8369404469603225'],
                    annualEmittedCO2Tonnes: 23.5,
                    calculationMethod: 'OVERVOLTAGE',
                    categoryType: 'MAJOR',
                  },
                },
              ],
            },
          },
        }),
        permitSectionsCompleted: {
          PFC_Description: [true],
          PFC_Types: [true],
          PFC_Efficiency: [true],
          PFC_Category: [true],
          PFC_Activity_Data: [true],
          PFC_Emission_Factor: [true],
          PFC_Tier2EmissionFactor: [true],
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
        ['Perfluorocarbons (PFC)', '0t', '0t', '0t', '23.5t'],
        ['13123124 White Spirit & SBP: Major', '23.5 t (100%)', 'completed'],
      ]);

      expect(page.tasks).toBeTruthy();
      expect(
        page.tasks.map((el) => [
          el.querySelector('a').textContent.trim(),
          el.querySelector('govuk-tag').textContent?.trim(),
        ]),
      ).toEqual([
        ['Approach description', 'completed'],
        ['Cell and anode types', 'completed'],
        ['Collection efficiency', 'completed'],
        ['Tier 2 - Emission factor', 'completed'],
      ]);
    });
  });
});

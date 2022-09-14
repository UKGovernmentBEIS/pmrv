import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../testing/mock-state';
import { N2oComponent } from './n2o.component';
import { N2oModule } from './n2o.module';

describe('N2oComponent', () => {
  let page: Page;
  let store: PermitApplicationStore;
  let component: N2oComponent;
  let fixture: ComponentFixture<N2oComponent>;

  class Page extends BasePage<N2oComponent> {
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
    fixture = TestBed.createComponent(N2oComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, N2oModule, RouterTestingModule],
    }).compileComponents();
  });

  describe('without source stream categories', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          ...mockPermitApplyPayload.permit,
          monitoringApproaches: {
            N2O: {},
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
      expect(page.rows).toEqual([['Nitrous oxide (N2O)', '0t', '0t', '0t', '0t']]);

      expect(page.tasks).toBeTruthy();
      expect(
        page.tasks.map((el) => [
          el.querySelector('a').textContent.trim(),
          el.querySelector('govuk-tag').textContent?.trim(),
        ]),
      ).toEqual([
        ['Add a source stream category', 'cannot start yet'],
        ['Approach description', 'not started'],
        ['Emission determination procedure', 'not started'],
        ['Determination of reference period', 'not started'],
        ['Operational management', 'not started'],
        ['Determination of nitrous oxide emissions', 'not started'],
        ['Determination of nitrous oxide concentration', 'not started'],
        ['Determination of the quantity of product produced', 'not started'],
        ['Quantity of materials', 'not started'],
        ['Calculation of gas flow', 'not started'],
      ]);
    });
  });

  describe('with source stream categories', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockStateBuild({
          monitoringApproaches: {
            N2O: {
              type: 'N2O',
              sourceStreamCategoryAppliedTiers: [
                {
                  sourceStreamCategory: {
                    sourceStream: '16236817394240.1574963093314663',
                    emissionSources: ['16245246343280.27155194483385103'],
                    emissionPoints: ['16363790610230.8369404469603225'],
                    emissionType: 'ABATED',
                    monitoringApproachType: 'CALCULATION',
                    annualEmittedCO2Tonnes: 23.5,
                    categoryType: 'MAJOR',
                  },
                },
                {
                  sourceStreamCategory: {
                    sourceStream: 'unknown',
                    emissionSources: ['16245246343280.27155194483385103'],
                    emissionPoints: ['16363790610230.8369404469603225'],
                    emissionType: 'ABATED',
                    monitoringApproachType: 'CALCULATION',
                    annualEmittedCO2Tonnes: 23.5,
                    categoryType: 'MAJOR',
                  },
                },
              ],
            },
          },
        }),
        permitSectionsCompleted: {
          N2O_Category: [true, true],
          N2O_Measured_Emissions: [true, true],
          N2O_Applied_Standard: [true, true],
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
        ['Nitrous oxide (N2O)', '0t', '0t', '0t', '47t'],
        ['13123124 White Spirit & SBP: Major', '23.5 t (50%)', 'completed'],
        ['UNDEFINED: Major', '23.5 t (50%)', 'needs review'],
      ]);

      expect(page.tasks).toBeTruthy();
      expect(
        page.tasks.map((el) => [
          el.querySelector('a').textContent.trim(),
          el.querySelector('govuk-tag').textContent?.trim(),
        ]),
      ).toEqual([
        ['Approach description', 'not started'],
        ['Emission determination procedure', 'not started'],
        ['Determination of reference period', 'not started'],
        ['Operational management', 'not started'],
        ['Determination of nitrous oxide emissions', 'not started'],
        ['Determination of nitrous oxide concentration', 'not started'],
        ['Determination of the quantity of product produced', 'not started'],
        ['Quantity of materials', 'not started'],
        ['Calculation of gas flow', 'not started'],
      ]);
    });
  });
});

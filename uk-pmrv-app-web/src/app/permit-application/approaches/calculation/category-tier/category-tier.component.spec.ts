import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../testing/mock-permit-apply-action';
import { mockState, mockStateBuild } from '../../../testing/mock-state';
import { CalculationModule } from '../calculation.module';
import { CategoryTierComponent } from './category-tier.component';

describe('CategoryTierComponent', () => {
  let page: Page;
  let store: PermitApplicationStore;
  let component: CategoryTierComponent;
  let fixture: ComponentFixture<CategoryTierComponent>;

  class Page extends BasePage<CategoryTierComponent> {
    get heading() {
      return this.query<HTMLElement>('h1');
    }
    get tasks() {
      return this.queryAll<HTMLLIElement>('li');
    }
    get summaryDefinitions() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(CategoryTierComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, CalculationModule, RouterTestingModule],
    }).compileComponents();
  });

  describe('for source stream categories that cannot start', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          ...mockPermitApplyPayload.permit,
          monitoringApproaches: {
            CALCULATION: {},
          },
          permitSectionsCompleted: {
            sourceStreams: [false],
          },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the not started page', () => {
      expect(page.heading.textContent.trim()).toEqual('Add a source stream category');
      expect(page.tasks.map((el) => el.textContent.trim())).toEqual([
        'Source streams (fuels and materials)',
        'Emission sources',
      ]);
    });
  });

  describe('for source stream categories that can start', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(mockState, {
          ...mockState.permitSectionsCompleted,
          CALCULATION_Category: [true],
          CALCULATION_Activity_Data: [false],
          CALCULATION_Calorific: [false],
          CALCULATION_Emission_Factor: [false],
          CALCULATION_Oxidation_Factor: [false],
          CALCULATION_Carbon_Content: [false],
          CALCULATION_Conversion_Factor: [false],
          CALCULATION_Biomass_Fraction: [false],
          emissionSources: [true],
          sourceStreams: [true],
          measurementDevicesOrMethods: [true],
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the tasks with status', () => {
      expect(page.heading.textContent.trim()).toEqual('13123124 White Spirit & SBP: Major  Delete');
      expect(
        page.tasks.map((el) => [
          el.querySelector('a').textContent.trim(),
          el.querySelector('govuk-tag').textContent.trim(),
        ]),
      ).toEqual([
        ['Source stream category', 'completed'],
        ['Activity data', 'not started'],
        ['Net calorific value', 'not started'],
        ['Emission factor', 'not started'],
        ['Oxidation factor', 'not started'],
        ['Carbon content', 'not started'],
        ['Conversion factor', 'not started'],
        ['Biomass fraction', 'not started'],
      ]);

      expect(page.summaryDefinitions).toEqual([
        ['Source stream category', '13123124 White Spirit & SBP: Major'],
        ['Emission sources', 'S1 Boiler'],
        ['Estimated CO2 emitted', '23.5 tonnes'],
        ['Calculation method', 'Standard calculation'],
      ]);
    });
  });

  describe('for source stream categories that can start but emission sources are in pending status', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(mockState, {
          ...mockState.permitSectionsCompleted,
          CALCULATION_Category: [true],
          CALCULATION_Activity_Data: [false],
          CALCULATION_Calorific: [false],
          CALCULATION_Emission_Factor: [false],
          CALCULATION_Oxidation_Factor: [false],
          CALCULATION_Carbon_Content: [false],
          CALCULATION_Conversion_Factor: [false],
          CALCULATION_Biomass_Fraction: [false],
          emissionSources: [false],
          sourceStreams: [true],
          measurementDevicesOrMethods: [true],
        }),
      );
    });
    beforeEach(createComponent);

    it('should display the tasks with status', () => {
      expect(page.heading.textContent.trim()).toEqual('13123124 White Spirit & SBP: Major  Delete');
      expect(
        page.tasks.map((el) => [
          el.querySelector('a').textContent.trim(),
          el.querySelector('govuk-tag').textContent.trim(),
        ]),
      ).toEqual([
        ['Source stream category', 'completed'],
        ['Activity data', 'not started'],
        ['Net calorific value', 'not started'],
        ['Emission factor', 'not started'],
        ['Oxidation factor', 'not started'],
        ['Carbon content', 'not started'],
        ['Conversion factor', 'not started'],
        ['Biomass fraction', 'not started'],
      ]);
    });
  });

  describe('for source stream categories that needs review', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              CALCULATION: {
                type: 'CALCULATION',
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: {
                      sourceStream: 'unknown',
                      emissionSources: ['16245246343280.27155194483385103'],
                      annualEmittedCO2Tonnes: 23.5,
                      calculationMethod: 'STANDARD',
                      categoryType: 'MAJOR',
                    },
                  },
                ],
              },
            },
          },
          {
            ...mockState.permitSectionsCompleted,
            CALCULATION_Category: [true],
            CALCULATION_Activity_Data: [false],
            CALCULATION_Calorific: [false],
            CALCULATION_Emission_Factor: [false],
            CALCULATION_Oxidation_Factor: [false],
            CALCULATION_Carbon_Content: [false],
            CALCULATION_Conversion_Factor: [false],
            CALCULATION_Biomass_Fraction: [false],
            measurementDevicesOrMethods: [true],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should display the tasks with status', () => {
      expect(page.heading.textContent.trim()).toEqual('UNDEFINED: Major  Delete');
      expect(
        page.tasks.map((el) => [
          el.querySelector('a').textContent.trim(),
          el.querySelector('govuk-tag').textContent.trim(),
        ]),
      ).toEqual([
        ['Source stream category', 'needs review'],
        ['Activity data', 'cannot start yet'],
        ['Net calorific value', 'not started'],
        ['Emission factor', 'not started'],
        ['Oxidation factor', 'not started'],
        ['Carbon content', 'not started'],
        ['Conversion factor', 'not started'],
        ['Biomass fraction', 'not started'],
      ]);
    });
  });

  describe('for source stream categories with deleted emission sources', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            emissionSources: [],
          },
          {
            ...mockState.permitSectionsCompleted,
            CALCULATION_Category: [true],
            CALCULATION_Activity_Data: [false],
            CALCULATION_Calorific: [false],
            CALCULATION_Emission_Factor: [false],
            CALCULATION_Oxidation_Factor: [false],
            CALCULATION_Carbon_Content: [false],
            CALCULATION_Conversion_Factor: [false],
            CALCULATION_Biomass_Fraction: [false],
            measurementDevicesOrMethods: [true],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should display the tasks with status', () => {
      expect(page.heading.textContent.trim()).toEqual('13123124 White Spirit & SBP: Major  Delete');
      expect(
        page.tasks.map((el) => [
          el.querySelector('a').textContent.trim(),
          el.querySelector('govuk-tag').textContent.trim(),
        ]),
      ).toEqual([
        ['Source stream category', 'needs review'],
        ['Activity data', 'cannot start yet'],
        ['Net calorific value', 'not started'],
        ['Emission factor', 'not started'],
        ['Oxidation factor', 'not started'],
        ['Carbon content', 'not started'],
        ['Conversion factor', 'not started'],
        ['Biomass fraction', 'not started'],
      ]);
    });
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockStateBuild } from '../../../testing/mock-state';
import { PFCModule } from '../pfc.module';
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
      imports: [SharedModule, PFCModule, RouterTestingModule],
    }).compileComponents();
  });

  describe('for source stream categories that cannot start', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          monitoringApproaches: {
            PFC: {},
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
        'Emission points',
      ]);
    });
  });

  describe('for source stream categories that can start', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {},
          {
            PFC_Category: [true],
            PFC_Activity_Data: [false],
            PFC_Emission_Factor: [false],
            emissionPoints: [true],
            emissionSources: [true],
            sourceStreams: [true],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the tasks with status', () => {
      expect(page.heading.textContent.trim()).toEqual('13123124 White Spirit & SBP: Major  Delete');
      expect(page.tasks.map((el) => el.querySelector('a').textContent.trim())).toEqual([
        'Source stream category',
        'Activity data',
        'Emission factor',
      ]);

      expect(page.tasks.map((el) => el.querySelector('govuk-tag').textContent.trim())).toEqual([
        'completed',
        'not started',
        'not started',
      ]);

      expect(page.summaryDefinitions).toEqual([
        ['Source stream category', '13123124 White Spirit & SBP: Major'],
        ['Emission sources', 'S1 Boiler'],
        ['Emission points', 'The big Ref Emission point 1'],
        ['Estimated CO2 emitted', '23.5 tonnes'],
        ['Calculation method', 'Overvoltage method'],
      ]);
    });
  });

  describe('for source stream categories that can start but emission sources are in pending status', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {},
          {
            PFC_Category: [true],
            PFC_Activity_Data: [false],
            PFC_Emission_Factor: [false],
            emissionPoints: [true],
            emissionSources: [false],
            sourceStreams: [true],
          },
        ),
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
        ['Emission factor', 'not started'],
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
              PFC: {
                type: 'PFC',
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: {
                      sourceStream: 'unknown',
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
          },
          {
            PFC_Category: [true],
            PFC_Activity_Data: [false],
            PFC_Emission_Factor: [false],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the tasks with status', () => {
      expect(page.heading.textContent.trim()).toEqual('UNDEFINED: Major  Delete');
      expect(page.tasks.map((el) => el.textContent.trim())).toEqual([
        'Source stream category needs review',
        'Activity data not started',
        'Emission factor not started',
      ]);
    });
  });

  describe('for source stream categories with deleted source streams', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            emissionSources: [],
          },
          {
            PFC_Category: [true],
            PFC_Activity_Data: [false],
            PFC_Emission_Factor: [false],
          },
        ),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the tasks with status', () => {
      expect(page.heading.textContent.trim()).toEqual('13123124 White Spirit & SBP: Major  Delete');
      expect(page.tasks.map((el) => el.textContent.trim())).toEqual([
        'Source stream category needs review',
        'Activity data not started',
        'Emission factor not started',
      ]);
    });
  });
});

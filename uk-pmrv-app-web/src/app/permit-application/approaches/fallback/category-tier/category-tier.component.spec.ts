import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../testing/mock-permit-apply-action';
import { mockState, mockStateBuild } from '../../../testing/mock-state';
import { FallbackModule } from '../fallback.module';
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
      imports: [SharedModule, FallbackModule, RouterTestingModule],
    }).compileComponents();
  });

  describe('for source stream categories that cannot start', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          ...mockPermitApplyPayload.permit,
          monitoringApproaches: {
            FALLBACK: {},
          },
          applyStatusesCompleted: {
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
        'Measurement devices',
      ]);
    });
  });

  describe('for source stream categories that can start', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(mockState, {
          ...mockState.permitSectionsCompleted,
          FALLBACK_Category: [true],
          measurementDevicesOrMethods: [true],
          emissionSources: [true],
          sourceStreams: [true],
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the tasks with status', () => {
      expect(page.heading.textContent.trim()).toEqual('13123124 White Spirit & SBP: Major  Delete');
      expect(page.tasks.map((el) => el.querySelector('a').textContent.trim())).toEqual(['Source stream category']);

      expect(page.tasks.map((el) => el.querySelector('govuk-tag').textContent.trim())).toEqual(['completed']);
    });
  });

  describe('for source stream categories that can start but emission sources are in pending status', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(mockState, {
          ...mockState.permitSectionsCompleted,
          FALLBACK_Category: [true],
          measurementDevicesOrMethods: [true],
          emissionSources: [false],
          sourceStreams: [true],
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
      ).toEqual([['Source stream category', 'completed']]);
    });
  });

  describe('for source stream categories that needs review', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              FALLBACK: {
                type: 'FALLBACK',
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: {
                      sourceStream: 'unknown',
                      emissionSources: ['16245246343280.27155194483385103'],
                      measurementDevicesOrMethods: ['16236817394240.1574963093314700'],
                      annualEmittedCO2Tonnes: 23.5,
                      categoryType: 'MAJOR',
                    },
                  },
                ],
              },
            },
          },
          {
            ...mockState.permitSectionsCompleted,
            FALLBACK_Category: [true],
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
      expect(page.tasks.map((el) => el.textContent.trim())).toEqual(['Source stream category needs review']);
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
            ...mockState.permitSectionsCompleted,
            FALLBACK_Category: [true],
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
      expect(page.tasks.map((el) => el.textContent.trim())).toEqual(['Source stream category needs review']);
    });
  });
});

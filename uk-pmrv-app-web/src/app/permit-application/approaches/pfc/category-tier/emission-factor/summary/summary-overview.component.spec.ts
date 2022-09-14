import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { PFCMonitoringApproach } from 'pmrv-api';

import { ActivatedRouteStub, BasePage } from '../../../../../../../testing';
import { SharedModule } from '../../../../../../shared/shared.module';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../../../../testing/mock-state';
import { PFCModule } from '../../../pfc.module';
import { SummaryOverviewComponent } from './summary-overview.component';

describe('SummaryOverviewComponent', () => {
  let store: PermitApplicationStore;
  let component: SummaryOverviewComponent;
  let fixture: ComponentFixture<SummaryOverviewComponent>;
  let page: Page;

  const route = new ActivatedRouteStub({ index: '0' }, null, { statusKey: 'PFC_Emission_Factor' });

  const sourceStreamCategory = (mockPermitApplyPayload.permit.monitoringApproaches.PFC as PFCMonitoringApproach)
    .sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;
  const technicalInfeasibilityExplanation = 'fff';

  class Page extends BasePage<SummaryOverviewComponent> {
    get answers() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(SummaryOverviewComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PFCModule, RouterTestingModule, SharedModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  describe('for emission factor with highest required tier', () => {
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
                    sourceStreamCategory,
                    emissionFactor: {
                      tier: 'TIER_2',
                    },
                  },
                ],
              },
            },
          },
          {},
        ),
      );
    });

    beforeEach(() => {
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display summary list', () => {
      expect(page.answers).toEqual([['Tier applied', 'Tier 2']]);
    });
  });

  describe('for emission factor with no highest required tier', () => {
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
                    sourceStreamCategory: sourceStreamCategory,
                    emissionFactor: {
                      tier: 'TIER_1',
                      isHighestRequiredTier: false,
                      noHighestRequiredTierJustification: {
                        isCostUnreasonable: false,
                        isTechnicallyInfeasible: true,
                        technicalInfeasibilityExplanation,
                      },
                    },
                  },
                ],
              },
            },
          },
          {},
        ),
      );
    });

    beforeEach(() => {
      createComponent();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display summary list', () => {
      expect(page.answers).toEqual([
        ['Tier applied', 'Tier 1'],
        ['Highest required tier?', 'No'],
        ['Reasons for not applying the highest required tier', 'Technical infeasibility'],
        ['Explanation of technical infeasibility', technicalInfeasibilityExplanation],
      ]);
    });
  });
});

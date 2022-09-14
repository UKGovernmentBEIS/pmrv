import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { CalculationMonitoringApproach } from 'pmrv-api';

import { ActivatedRouteStub, BasePage } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../../../testing/mock-state';
import { CalculationModule } from '../../calculation.module';
import { SummaryTemplateComponent } from './summary-template.component';

describe('SummaryTemplateComponent', () => {
  let store: PermitApplicationStore;
  let component: SummaryTemplateComponent;
  let fixture: ComponentFixture<SummaryTemplateComponent>;
  let page: Page;

  const route = new ActivatedRouteStub({ index: '0' }, null, {
    taskKey: 'monitoringApproaches.CALCULATION.sourceStreamCategoryAppliedTiers',
    statusKey: 'CALCULATION_Emission_Factor',
  });

  const sourceStreamCategory = (
    mockPermitApplyPayload.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach
  ).sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;

  class Page extends BasePage<SummaryTemplateComponent> {
    get answers() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CalculationModule, RouterTestingModule, SharedModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

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
                  sourceStreamCategory,
                  emissionFactor: {
                    exist: true,
                    tier: 'TIER_2B',
                    isHighestRequiredTier: true,
                    defaultValueApplied: true,
                    standardReferenceSource: {
                      type: 'MONITORING_REPORTING_REGULATION_ARTICLE_36_3',
                    },
                    analysisMethodUsed: false,
                  },
                },
              ],
            },
          },
        },
        {
          CALCULATION_Emission_Factor: [false],
        },
      ),
    );
    fixture = TestBed.createComponent(SummaryTemplateComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display analysis method list', () => {
    expect(page.answers).toEqual([
      ['Details', ''],
      ['Using emission factor as part of your calculation?', 'Yes'],
      ['Tier applied', 'Tier 2b'],
      ['Applying the highest required tier?', 'Yes'],
      ['Applying a default value', 'Yes'],
      ['Standard reference source', 'Monitoring and Reporting Regulation, Article 36(3)'],
      ['Using an analysis method?', 'No'],
    ]);
  });
});

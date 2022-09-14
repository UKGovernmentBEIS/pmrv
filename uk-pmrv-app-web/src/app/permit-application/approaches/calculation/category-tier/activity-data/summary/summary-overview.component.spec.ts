import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { CalculationMonitoringApproach } from 'pmrv-api';

import { ActivatedRouteStub, BasePage } from '../../../../../../../testing';
import { SharedModule } from '../../../../../../shared/shared.module';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../../../../testing/mock-state';
import { CalculationModule } from '../../../calculation.module';
import { SummaryOverviewComponent } from './summary-overview.component';

describe('SummaryOverviewComponent', () => {
  let store: PermitApplicationStore;
  let component: SummaryOverviewComponent;
  let fixture: ComponentFixture<SummaryOverviewComponent>;
  let page: Page;

  const route = new ActivatedRouteStub({ index: '0' }, null, { statusKey: 'CALCULATION_Activity_Data' });
  const sourceStreamCategory = (
    mockPermitApplyPayload.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach
  ).sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;

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
      imports: [CalculationModule, RouterTestingModule, SharedModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  describe('for activity data with highest required tier', () => {
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
                    sourceStreamCategory: sourceStreamCategory,
                    activityData: {
                      measurementDevicesOrMethods: ['16236817394240.1574963093314700'],
                      tier: 'TIER_4',
                      uncertainty: 'LESS_OR_EQUAL_5_0',
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
        ['Measurement Devices Or Methods', 'ref1, Ultrasonic meter, Specified uncertainty ±2%'],
        ['Overall metering uncertainty', '5% or less'],
        ['Tier', 'Tier 4'],
      ]);
    });
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { CalculationMonitoringApproach } from 'pmrv-api';

import { ActivatedRouteStub } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../../../testing/mock-state';
import { CalculationModule } from '../../calculation.module';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let store: PermitApplicationStore;
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;

  const route = new ActivatedRouteStub({ index: '0' }, null, {
    taskKey: 'monitoringApproaches.CALCULATION.sourceStreamCategoryAppliedTiers',
    statusKey: 'CALCULATION_Emission_Factor',
  });

  const sourceStreamCategory = (
    mockPermitApplyPayload.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach
  ).sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;

  const createComponent = () => {
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    jest.clearAllMocks();
  };

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
                    oneThirdRule: false,
                    tier: 'TIER_3',
                    defaultValueApplied: undefined,
                    analysisMethodUsed: true,
                    analysisMethods: [
                      {
                        analysis: 'analyze',
                        files: [],
                        frequencyMeetsMinRequirements: false,
                        laboratoryAccredited: false,
                        laboratoryName: 'lab',
                        samplingFrequency: 'WEEKLY',
                        subParameter: null,
                      },
                    ],
                  },
                },
              ],
            },
          },
        },
        {
          CALCULATION_Emission_Factor: [true],
        },
      ),
    );
  });

  beforeEach(() => {
    createComponent();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

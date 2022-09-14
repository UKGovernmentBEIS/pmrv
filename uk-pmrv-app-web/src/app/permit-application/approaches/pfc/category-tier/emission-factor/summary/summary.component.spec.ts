import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { PFCMonitoringApproach } from 'pmrv-api';

import { ActivatedRouteStub } from '../../../../../../../testing';
import { SharedModule } from '../../../../../../shared/shared.module';
import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../../../../testing/mock-state';
import { PFCModule } from '../../../pfc.module';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let store: PermitApplicationStore;
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;

  const route = new ActivatedRouteStub({ index: '0' }, null, { statusKey: 'PFC_Emission_Factor' });

  const sourceStreamCategory = (mockPermitApplyPayload.permit.monitoringApproaches.PFC as PFCMonitoringApproach)
    .sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;

  const createComponent = () => {
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PFCModule, RouterTestingModule, SharedModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

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
});

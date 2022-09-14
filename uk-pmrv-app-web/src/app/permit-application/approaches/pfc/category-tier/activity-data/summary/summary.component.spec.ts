import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PFCMonitoringApproach } from 'pmrv-api';

import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../../../../testing/mock-state';
import { PFCModule } from '../../../pfc.module';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;
  let store: PermitApplicationStore;

  const mockPfcApproach = mockPermitApplyPayload.permit.monitoringApproaches.PFC as PFCMonitoringApproach;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PFCModule, RouterTestingModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    store = TestBed.inject(PermitApplicationStore);
    store.setState(
      mockStateBuild(
        {
          monitoringApproaches: {
            PFC: {
              ...mockPfcApproach,
              sourceStreamCategoryAppliedTiers: [
                {
                  ...mockPfcApproach.sourceStreamCategoryAppliedTiers[0],
                  activityData: {
                    ...mockPfcApproach.sourceStreamCategoryAppliedTiers[0].activityData,
                    isHighestRequiredTier: true,
                  },
                },
              ],
            },
          },
        },
        {},
      ),
    );
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

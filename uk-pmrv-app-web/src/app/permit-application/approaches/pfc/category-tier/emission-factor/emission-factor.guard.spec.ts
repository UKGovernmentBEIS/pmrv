import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PFCEmissionFactor, PFCMonitoringApproach } from 'pmrv-api';

import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../../../testing/mock-state';
import { EmissionFactorGuard } from './emission-factor.guard';

describe('EmissionFactorGuard', () => {
  let store: PermitApplicationStore;
  let guard: EmissionFactorGuard;
  let router: Router;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 1, index: 0 };
  activatedRouteSnapshot.data = { statusKey: 'PFC_Emission_Factor' };

  const sourceStreamCategory = (mockPermitApplyPayload.permit.monitoringApproaches.PFC as PFCMonitoringApproach)
    .sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;

  const emissionFactor: PFCEmissionFactor = {
    tier: 'TIER_1',
    isHighestRequiredTier: false,
    noHighestRequiredTierJustification: { isTechnicallyInfeasible: true, technicalInfeasibilityExplanation: 'test' },
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
    });
    guard = TestBed.inject(EmissionFactorGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitApplicationStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate if subtask does not exist yet', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          PFC: {
            sourceStreamCategoryAppliedTiers: [],
          },
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should activate if subtask is changing', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: true } } } as any);
    await expect(guard.canActivate(activatedRouteSnapshot)).toEqual(true);
  });

  it('should redirect to answers page if subtask in progress', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(
      mockStateBuild(
        {
          monitoringApproaches: {
            PFC: {
              sourceStreamCategoryAppliedTiers: [
                {
                  sourceStreamCategory,
                  emissionFactor,
                },
              ],
            },
          },
        },
        {
          PFC_Category: [true],
          PFC_Emission_Factor: [false],
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(
        `/permit-application/${activatedRouteSnapshot.params.taskId}/pfc/category-tier/${activatedRouteSnapshot.params.index}/emission-factor/answers`,
      ),
    );
  });

  it('should redirect to summary page if task completed', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(
      mockStateBuild(
        {
          monitoringApproaches: {
            PFC: {
              sourceStreamCategoryAppliedTiers: [
                {
                  sourceStreamCategory,
                  emissionFactor,
                },
              ],
            },
          },
        },
        {
          PFC_Category: [true],
          PFC_Emission_Factor: [true],
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(
        `/permit-application/${activatedRouteSnapshot.params.taskId}/pfc/category-tier/${activatedRouteSnapshot.params.index}/emission-factor/summary`,
      ),
    );
  });
});

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PFCEmissionFactor, PFCMonitoringApproach } from 'pmrv-api';

import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../../../../testing/mock-state';
import { AnswersGuard } from './answers.guard';

describe('AnswersGuard', () => {
  let store: PermitApplicationStore;
  let guard: AnswersGuard;
  let router: Router;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 333, index: 0 };
  activatedRouteSnapshot.data = { statusKey: 'PFC_Emission_Factor' };

  const sourceStreamCategory = (mockPermitApplyPayload.permit.monitoringApproaches.PFC as PFCMonitoringApproach)
    .sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;

  const emissionFactor: PFCEmissionFactor = {
    tier: 'TIER_2',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
    });
    guard = TestBed.inject(AnswersGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitApplicationStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to emission-factor if subtask cannot start yet', async () => {
    store.setState(
      mockStateBuild(
        {
          monitoringApproaches: {
            PFC: {
              sourceStreamCategoryAppliedTiers: [
                {
                  sourceStreamCategory: [],
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
        `/permit-application/${activatedRouteSnapshot.params.taskId}/pfc/category-tier/${activatedRouteSnapshot.params.index}/emission-factor`,
      ),
    );
  });

  it('should redirect to emission-factor if subtask not started', async () => {
    store.setState(
      mockStateBuild(
        {
          monitoringApproaches: {
            PFC: {
              sourceStreamCategoryAppliedTiers: [
                {
                  sourceStreamCategory: sourceStreamCategory,
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
        `/permit-application/${activatedRouteSnapshot.params.taskId}/pfc/category-tier/${activatedRouteSnapshot.params.index}/emission-factor`,
      ),
    );
  });

  it('should activate if subtask in progress', async () => {
    store.setState(
      mockStateBuild(
        {
          monitoringApproaches: {
            PFC: {
              sourceStreamCategoryAppliedTiers: [
                {
                  sourceStreamCategory: sourceStreamCategory,
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
    ).resolves.toEqual(true);
  });

  it('should redirect to summary page if subtask completed', async () => {
    store.setState(
      mockStateBuild(
        {
          monitoringApproaches: {
            PFC: {
              sourceStreamCategoryAppliedTiers: [
                {
                  sourceStreamCategory: sourceStreamCategory,
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

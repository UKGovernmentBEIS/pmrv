import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { CalculationMonitoringApproach } from 'pmrv-api';

import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockStateBuild } from '../../../../testing/mock-state';
import { AnswersGuard } from './answers.guard';

describe('AnswersGuard', () => {
  let store: PermitApplicationStore;
  let guard: AnswersGuard;
  let router: Router;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 333, index: 0 };
  activatedRouteSnapshot.data = { statusKey: 'CALCULATION_Biomass_Fraction' };

  const sourceStreamCategory = (
    mockPermitApplyPayload.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach
  ).sourceStreamCategoryAppliedTiers[0].sourceStreamCategory;

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

  it('should redirect to biomass-fraction if task cannot start yet', async () => {
    store.setState(
      mockStateBuild(
        {
          monitoringApproaches: {
            CALCULATION: {
              sourceStreamCategoryAppliedTiers: [
                {
                  sourceStreamCategory: [],
                },
              ],
            },
          },
        },
        {
          CALCULATION_Biomass_Fraction: [false],
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(
        `/permit-application/${activatedRouteSnapshot.params.taskId}/calculation/category-tier/${activatedRouteSnapshot.params.index}/biomass-fraction`,
      ),
    );
  });

  it('should redirect to biomass-fraction if task not started', async () => {
    store.setState(
      mockStateBuild(
        {
          monitoringApproaches: {
            CALCULATION: {
              sourceStreamCategoryAppliedTiers: [
                {
                  sourceStreamCategory: sourceStreamCategory,
                },
              ],
            },
          },
        },
        {
          CALCULATION_Biomass_Fraction: [false],
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(
        `/permit-application/${activatedRouteSnapshot.params.taskId}/calculation/category-tier/${activatedRouteSnapshot.params.index}/biomass-fraction`,
      ),
    );
  });

  it('should allow if wizard is complete', async () => {
    store.setState(
      mockStateBuild(
        {
          monitoringApproaches: {
            CALCULATION: {
              sourceStreamCategoryAppliedTiers: [
                {
                  sourceStreamCategory: sourceStreamCategory,
                  biomassFraction: {
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
          CALCULATION_Biomass_Fraction: [false],
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to summary page if task completed', async () => {
    store.setState(
      mockStateBuild(
        {
          monitoringApproaches: {
            CALCULATION: {
              sourceStreamCategoryAppliedTiers: [
                {
                  sourceStreamCategory: sourceStreamCategory,
                  biomassFraction: {
                    exist: true,
                    tier: 'TIER_1',
                    isHighestRequiredTier: true,
                    standardReferenceSource: {
                      applyDefaultValue: true,
                      defaultValue: 'test',
                      type: 'BRITISH_CERAMIC_CONFEDERATION',
                    },
                  },
                },
              ],
            },
          },
        },
        {
          CALCULATION_Category: [true],
          CALCULATION_Biomass_Fraction: [true],
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(
        `/permit-application/${activatedRouteSnapshot.params.taskId}/calculation/category-tier/${activatedRouteSnapshot.params.index}/biomass-fraction/summary`,
      ),
    );
  });
});

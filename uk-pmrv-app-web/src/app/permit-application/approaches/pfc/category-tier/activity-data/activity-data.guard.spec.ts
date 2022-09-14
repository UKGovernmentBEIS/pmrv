import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockStateBuild } from '../../../../testing/mock-state';
import { ActivityDataGuard } from './activity-data.guard';

describe('ActivityDataGuard', () => {
  let store: PermitApplicationStore;
  let guard: ActivityDataGuard;
  let router: Router;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 333, index: 0 };
  activatedRouteSnapshot.data = { statusKey: 'PFC_Activity_Data' };

  const sourceStreamCategory = {
    sourceStream: '16236817394240.1574963093314663',
    emissionSources: ['16245246343280.27155194483385103'],
    emissionPoints: ['16236817394240.1574963093314663'],
    annualEmittedCO2Tonnes: 23.5,
    calculationMethod: 'OVERVOLTAGE',
    categoryType: 'MAJOR',
  };

  const activityData = {
    massBalanceApproachUsed: true,
    tier: 'TIER_4',
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
    });
    guard = TestBed.inject(ActivityDataGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitApplicationStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate if task cannot start yet', async () => {
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

  it('should activate if activity data does not exist', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

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
          PFC_Activity_Data: [false],
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should activate if task is changing', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: true } } } as any);
    await expect(guard.canActivate(activatedRouteSnapshot)).toEqual(true);
  });

  it('should redirect to answers page if task in progress', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(
      mockStateBuild(
        {
          monitoringApproaches: {
            PFC: {
              sourceStreamCategoryAppliedTiers: [
                {
                  sourceStreamCategory: sourceStreamCategory,
                  activityData: activityData,
                },
              ],
            },
          },
        },
        {
          PFC_Category: [true],
          PFC_Activity_Data: [false],
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(
        `/permit-application/${activatedRouteSnapshot.params.taskId}/pfc/category-tier/${activatedRouteSnapshot.params.index}/activity-data/answers`,
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
                  sourceStreamCategory: sourceStreamCategory,
                  activityData: activityData,
                },
              ],
            },
          },
        },
        {
          PFC_Category: [true],
          PFC_Activity_Data: [true],
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(
        `/permit-application/${activatedRouteSnapshot.params.taskId}/pfc/category-tier/${activatedRouteSnapshot.params.index}/activity-data/summary`,
      ),
    );
  });
});

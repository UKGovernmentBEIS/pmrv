import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../../../../store/permit-application.store';
import { mockStateBuild } from '../../../../../testing/mock-state';
import { AnswersGuard } from './answers.guard';

describe('AnswersGuard', () => {
  let store: PermitApplicationStore;
  let guard: AnswersGuard;
  let router: Router;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 333, index: 0 };
  activatedRouteSnapshot.data = { statusKey: 'CALCULATION_Activity_Data' };

  const sourceStreamCategory = {
    sourceStream: '16236817394240.1574963093314663',
    emissionSources: ['16245246343280.27155194483385103'],
    emissionPoints: ['16236817394240.1574963093314663'],
    annualEmittedCO2Tonnes: 23.5,
    calculationMethod: 'OVERVOLTAGE',
    categoryType: 'MAJOR',
  };

  const activityData = {
    measurementDevicesOrMethods: ['16236817394240.1574963093314700'],
    tier: 'TIER_4',
    uncertainty: 'LESS_OR_EQUAL_5_0',
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

  it('should redirect to activity data if task cannot start yet', async () => {
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
          measurementDevicesOrMethods: [true],
          CALCULATION_Category: [true],
          CALCULATION_Activity_Data: [false],
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(
        `/permit-application/${activatedRouteSnapshot.params.taskId}/calculation/category-tier/${activatedRouteSnapshot.params.index}/activity-data`,
      ),
    );
  });

  it('should redirect to activity data if task not started', async () => {
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
          measurementDevicesOrMethods: [true],
          CALCULATION_Category: [true],
          CALCULATION_Activity_Data: [false],
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(
        `/permit-application/${activatedRouteSnapshot.params.taskId}/calculation/category-tier/${activatedRouteSnapshot.params.index}/activity-data`,
      ),
    );
  });

  it('should activate if task in progress', async () => {
    store.setState(
      mockStateBuild(
        {
          monitoringApproaches: {
            CALCULATION: {
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
          measurementDevicesOrMethods: [true],
          CALCULATION_Category: [true],
          CALCULATION_Activity_Data: [false],
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
                  activityData: activityData,
                },
              ],
            },
          },
        },
        {
          measurementDevicesOrMethods: [true],
          CALCULATION_Category: [true],
          CALCULATION_Activity_Data: [true],
        },
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(
        `/permit-application/${activatedRouteSnapshot.params.taskId}/calculation/category-tier/${activatedRouteSnapshot.params.index}/activity-data/summary`,
      ),
    );
  });
});

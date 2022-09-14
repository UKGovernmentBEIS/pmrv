import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockStateBuild } from '../../../testing/mock-state';
import { AnswersGuard } from './answers.guard';

describe('AnswersGuard', () => {
  let guard: AnswersGuard;
  let router: Router;
  let store: PermitApplicationStore;

  const taskKeys = ['N2O', 'MEASUREMENT'];
  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 276, index: 0 };

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

  it('should redirect to emissions  if task cannot start yet ', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    for (const taskKey of taskKeys) {
      activatedRouteSnapshot.data = { taskKey };
      const urlFragment = taskKey === 'MEASUREMENT' ? 'measurement' : 'nitrous-oxide';

      store.setState(
        mockStateBuild(
          {
            measurementDevicesOrMethods: [],
            monitoringApproaches: {
              [taskKey]: {
                type: taskKey,
                sourceStreamCategoryAppliedTiers: [],
              },
            },
          },
          {
            measurementDevicesOrMethods: [false],
            [`${taskKey}_Category`]: [true],
            [`${taskKey}_Measured_Emissions`]: [true],
          },
        ),
      );

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/permit-application/276/${urlFragment}/category-tier/0/emissions`));
    }
  });

  it('should redirect to emissions if task is not started ', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);
    for (const taskKey of taskKeys) {
      activatedRouteSnapshot.data = { taskKey };
      const urlFragment = taskKey === 'MEASUREMENT' ? 'measurement' : 'nitrous-oxide';
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              [taskKey]: {
                type: taskKey,
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: {
                      sourceStream: '16236817394240.1574963093314663',
                      emissionSources: ['16245246343280.27155194483385103'],
                      emissionPoints: ['16363790610230.8369404469603225'],
                      emissionType: 'ABATED',
                      monitoringApproachType: 'CALCULATION',
                      annualEmittedCO2Tonnes: 23.5,
                      categoryType: 'MAJOR',
                    },
                  },
                ],
              },
            },
          },
          {
            measurementDevicesOrMethods: [true],
            [`${taskKey}_Category`]: [true],
            [`${taskKey}_Measured_Emissions`]: [false],
          },
        ),
      );

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/permit-application/276/${urlFragment}/category-tier/0/emissions`));
    }
  });

  it('should activate if task  needs review ', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);
    for (const taskKey of taskKeys) {
      activatedRouteSnapshot.data = { taskKey };

      store.setState(
        mockStateBuild({
          measurementDevicesOrMethods: [],
          monitoringApproaches: {
            [taskKey]: {
              type: taskKey,
              sourceStreamCategoryAppliedTiers: [
                {
                  sourceStreamCategory: {
                    sourceStream: '16236817394240.1574963093314663',
                    emissionSources: ['16245246343280.27155194483385103'],
                    emissionPoints: ['16363790610230.8369404469603225'],
                    emissionType: 'ABATED',
                    monitoringApproachType: 'CALCULATION',
                    annualEmittedCO2Tonnes: 23.5,
                    categoryType: 'MAJOR',
                  },
                  measuredEmissions: {
                    measurementDevicesOrMethods: ['16236817394240.1574963093314700'],
                    samplingFrequency: 'MONTHLY',
                    tier: taskKey === 'MEASUREMENT' ? 'TIER_4' : 'TIER_3',
                  },
                },
              ],
            },
          },
        }),
      );

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    }
  });

  it('should activate if task is in progress ', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);
    for (const taskKey of taskKeys) {
      activatedRouteSnapshot.data = { taskKey };

      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              [taskKey]: {
                type: taskKey,
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: {
                      sourceStream: '16236817394240.1574963093314663',
                      emissionSources: ['16245246343280.27155194483385103'],
                      emissionPoints: ['16363790610230.8369404469603225'],
                      emissionType: 'ABATED',
                      monitoringApproachType: 'CALCULATION',
                      annualEmittedCO2Tonnes: 23.5,
                      categoryType: 'MAJOR',
                    },
                    measuredEmissions: {
                      measurementDevicesOrMethods: ['16236817394240.1574963093314700'],
                      samplingFrequency: 'MONTHLY',
                      tier: taskKey === 'MEASUREMENT' ? 'TIER_4' : 'TIER_3',
                    },
                  },
                ],
              },
            },
          },
          {
            measurementDevicesOrMethods: [true],
            [`${taskKey}_Category`]: [true],
            [`${taskKey}_Measured_Emissions`]: [false],
          },
        ),
      );

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    }
  });

  it('should redirect to summary if task is complete', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);
    for (const taskKey of taskKeys) {
      activatedRouteSnapshot.data = { taskKey };

      const urlFragment = taskKey === 'MEASUREMENT' ? 'measurement' : 'nitrous-oxide';
      store.setState(
        mockStateBuild(
          {
            monitoringApproaches: {
              [taskKey]: {
                type: taskKey,
                sourceStreamCategoryAppliedTiers: [
                  {
                    sourceStreamCategory: {
                      sourceStream: '16236817394240.1574963093314663',
                      emissionSources: ['16245246343280.27155194483385103'],
                      emissionPoints: ['16363790610230.8369404469603225'],
                      emissionType: 'ABATED',
                      monitoringApproachType: 'CALCULATION',
                      annualEmittedCO2Tonnes: 23.5,
                      categoryType: 'MAJOR',
                    },
                    measuredEmissions: {
                      measurementDevicesOrMethods: ['16236817394240.1574963093314700'],
                      samplingFrequency: 'MONTHLY',
                      tier: 'TIER_1',
                      isHighestRequiredTier: false,
                    },
                  },
                ],
              },
            },
          },
          {
            [`${taskKey}_Category`]: [true],
            [`${taskKey}_Measured_Emissions`]: [true],
          },
        ),
      );

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(router.parseUrl(`/permit-application/276/${urlFragment}/category-tier/0/emissions/summary`));
    }
  });
});

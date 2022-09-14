import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockStateBuild } from '../../testing/mock-state';
import { EmissionsGuard } from './emissions.guard';

describe('EmissionsGuard', () => {
  let guard: EmissionsGuard;
  let router: Router;
  let store: PermitApplicationStore;

  const taskKeys = ['N2O', 'MEASUREMENT'];
  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 276, index: 0 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
    });
    guard = TestBed.inject(EmissionsGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitApplicationStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate if task cannot start yet ', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    for (const taskKey of taskKeys) {
      activatedRouteSnapshot.data = { taskKey };

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
            [`${taskKey}_Category`]: [false],
            [`${taskKey}_Measured_Emissions`]: [false],
          },
        ),
      );

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    }
  });

  it('should activate if task is not started ', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    const taskKeys = ['N2O', 'MEASUREMENT'];

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
                  },
                ],
              },
            },
          },
          {
            measurementDevicesOrMethods: [true],
            [`${taskKey}_Category`]: [false],
            [`${taskKey}_Measured_Emissions`]: [false],
          },
        ),
      );

      await expect(
        firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
      ).resolves.toEqual(true);
    }
  });

  it('should activate if task is changing', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: true } } } as any);
    await expect(guard.canActivate(activatedRouteSnapshot)).toEqual(true);
  });

  it('should allow if task  needs review and not changing ', async () => {
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
                    tier: 'TIER_1',
                    isHighestRequiredTier: false,
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

  it('should activate if task is in progress and not changing', async () => {
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
      ).resolves.toEqual(router.parseUrl(`/permit-application/276/${urlFragment}/category-tier/0/emissions/answers`));
    }
  });

  it('should redirect to summary if task is complete and not changing', async () => {
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

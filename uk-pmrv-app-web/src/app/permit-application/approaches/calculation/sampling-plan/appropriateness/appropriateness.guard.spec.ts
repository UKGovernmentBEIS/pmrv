import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { CalculationMonitoringApproach, TasksService } from 'pmrv-api';

import { mockClass } from '../../../../../../testing';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockState, mockStateBuild } from '../../../../testing/mock-state';
import { AppropriatenessGuard } from './appropriateness.guard';

describe('AppropriatenessGuard', () => {
  let router: Router;
  let guard: AppropriatenessGuard;
  let store: PermitApplicationStore;

  const tasksService = mockClass(TasksService);
  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('appropriateness', null)];
  const routerStateSnapshot = {
    url: '/permit-application/276/calculation/sampling-plan/appropriateness',
  } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    });
    guard = TestBed.inject(AppropriatenessGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitApplicationStore);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to summary if status is completed', async () => {
    store.setState(mockStateBuild(undefined, { CALCULATION_Plan: [true] }));

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/permit-application/276/calculation/sampling-plan/summary'));
  });

  it('should activate if plan exists and wizard is not completed', async () => {
    const mockCalculation = mockState.permit.monitoringApproaches.CALCULATION as CalculationMonitoringApproach;
    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          CALCULATION: {
            type: 'CALCULATION',
          },
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/permit-application/276/calculation/sampling-plan'));

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          CALCULATION: {
            ...mockCalculation,
            samplingPlan: {
              exist: true,
              details: {
                analysis: {
                  ...mockCalculation.samplingPlan.details.analysis,
                },
                procedurePlan: {
                  ...mockCalculation.samplingPlan.details.procedurePlan,
                },
              },
            },
          } as CalculationMonitoringApproach,
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          CALCULATION: {
            ...mockCalculation,
            samplingPlan: {
              exist: false,
            },
          } as CalculationMonitoringApproach,
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/permit-application/276/calculation/sampling-plan/answers'));
  });

  it('should activate if forced to change', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValueOnce({ extras: { state: { changing: true } } } as any);

    await expect(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot)).toEqual(true);
  });
});

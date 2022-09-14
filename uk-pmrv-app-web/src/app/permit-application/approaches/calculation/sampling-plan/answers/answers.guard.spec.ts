import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlSegment } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { CalculationMonitoringApproach, TasksService } from 'pmrv-api';

import { mockClass } from '../../../../../../testing';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockState, mockStateBuild } from '../../../../testing/mock-state';
import { AnswersGuard } from './answers.guard';

describe('AnswersGuard', () => {
  let router: Router;
  let guard: AnswersGuard;
  let store: PermitApplicationStore;

  const tasksService = mockClass(TasksService);
  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('answers', null)];
  const routerStateSnapshot = {
    url: '/permit-application/276/calculation/sampling-plan/answers',
  } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    });
    guard = TestBed.inject(AnswersGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitApplicationStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to summary if status is completed', async () => {
    store.setState(mockStateBuild(undefined, { CALCULATION_Plan: [true] }));

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot))).resolves.toEqual(
      router.parseUrl('/permit-application/276/calculation/sampling-plan/summary'),
    );
  });

  it('should allow if all step are completed', async () => {
    store.setState(mockStateBuild(undefined, { CALCULATION_Plan: [false] }));

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot))).resolves.toEqual(true);
  });

  it('should redirect to start if at least one step is not completed', async () => {
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

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot))).resolves.toEqual(
      router.parseUrl('/permit-application/276/calculation/sampling-plan'),
    );

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

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot))).resolves.toEqual(true);

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          CALCULATION: {
            ...mockCalculation,
            samplingPlan: {
              exist: true,
              details: {
                procedurePlan: {
                  ...mockCalculation.samplingPlan.details.procedurePlan,
                },
              },
            },
          } as CalculationMonitoringApproach,
        },
      }),
    );

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot))).resolves.toEqual(
      router.parseUrl('/permit-application/276/calculation/sampling-plan'),
    );

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          CALCULATION: {
            ...mockCalculation,
            samplingPlan: {
              exist: true,
              details: {
                analysis: { ...mockCalculation.samplingPlan.details.analysis },
                procedurePlan: { ...mockCalculation.samplingPlan.details.procedurePlan },
                appropriateness: { ...mockCalculation.samplingPlan.details.appropriateness },
              },
            },
          } as CalculationMonitoringApproach,
        },
      }),
    );

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot))).resolves.toEqual(
      router.parseUrl('/permit-application/276/calculation/sampling-plan'),
    );

    store.setState(
      mockStateBuild({
        monitoringApproaches: {
          CALCULATION: {
            ...mockCalculation,
            samplingPlan: {
              exist: true,
              details: {
                analysis: { ...mockCalculation.samplingPlan.details.analysis },
                procedurePlan: { ...mockCalculation.samplingPlan.details.procedurePlan },
                appropriateness: { ...mockCalculation.samplingPlan.details.appropriateness },
                yearEndReconciliation: { exist: false },
              },
            },
          } as CalculationMonitoringApproach,
        },
      }),
    );

    await expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot))).resolves.toEqual(true);
  });
});

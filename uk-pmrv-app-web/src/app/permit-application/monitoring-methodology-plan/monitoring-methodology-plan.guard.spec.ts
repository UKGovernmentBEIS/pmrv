import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { mockClass } from '../../../testing';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockStateBuild } from '../testing/mock-state';
import { MonitoringMethodologyPlanGuard } from './monitoring-methodology-plan.guard';

describe('MonitoringMethodologyPlanGuard', () => {
  let router: Router;
  let guard: MonitoringMethodologyPlanGuard;
  let store: PermitApplicationStore;

  const tasksService = mockClass(TasksService);
  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  const routerStateSnapshot = {
    url: '/permit-application/276/monitoring-methodology-plan',
  } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    });
    guard = TestBed.inject(MonitoringMethodologyPlanGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitApplicationStore);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to summary if status is completed', async () => {
    store.setState(mockStateBuild(undefined, { monitoringMethodologyPlans: [true] }));

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/permit-application/276/monitoring-methodology-plan/summary'));
  });

  it('should allow if status is not started', async () => {
    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: {},
        },
        {},
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should allow if status is in progress and no files are selected', async () => {
    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: { exist: true },
        },
        {},
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to summary if status is in progress and files are selected', async () => {
    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: { exist: true, plans: ['e227ea8a-778b-4208-9545-e108ea66c114'] },
        },
        {},
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/permit-application/276/monitoring-methodology-plan/answers'));
  });

  it('should redirect to answers if status is in progress and selection is No', async () => {
    store.setState(
      mockStateBuild(
        {
          monitoringMethodologyPlans: { exist: false },
        },
        {},
      ),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/permit-application/276/monitoring-methodology-plan/answers'));
  });
});

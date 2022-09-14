import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { SummaryGuard } from '@tasks/aer/submit/prtr/summary/summary.guard';
import { mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { mockStateBuild } from '@tasks/aer/submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

describe('SummaryGuard', () => {
  let router: Router;
  let guard: SummaryGuard;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();

  const routerStateSnapshot = {
    url: '/tasks/1/aer/submit/prtr/summary',
  } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });
    guard = TestBed.inject(SummaryGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow', async () => {
    store.setState(mockState);

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);

    store.setState(
      mockStateBuild({
        pollutantRegisterActivities: {
          exist: true,
          activities: ['_1_A_2_C_CHEMICALS', '_1_A_3_C_RAILWAYS', '_1_B_2_A_OIL'],
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);

    store.setState({
      ...mockState,
      isEditable: false,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should not allow', async () => {
    store.setState(
      mockStateBuild({
        pollutantRegisterActivities: undefined,
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/aer/submit/prtr`));

    store.setState(
      mockStateBuild({
        pollutantRegisterActivities: {
          exist: true,
          activities: [],
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/aer/submit/prtr`));
  });
});

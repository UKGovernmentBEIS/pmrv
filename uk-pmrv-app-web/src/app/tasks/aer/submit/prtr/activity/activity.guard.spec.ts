import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { ActivityGuard } from '@tasks/aer/submit/prtr/activity/activity.guard';
import { mockStateBuild } from '@tasks/aer/submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

describe('ActivityGuard', () => {
  let router: Router;
  let guard: ActivityGuard;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [];
  activatedRouteSnapshot.params = { taskId: 276, index: 2 };

  const activatedRouteSnapshotDelete = new ActivatedRouteSnapshot();
  activatedRouteSnapshotDelete.url = [new UrlSegment('delete', null)];
  activatedRouteSnapshotDelete.params = { taskId: 276, index: 4 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });
    guard = TestBed.inject(ActivityGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow', async () => {
    store.setState(
      mockStateBuild({
        pollutantRegisterActivities: {
          exist: true,
          activities: ['_1_A_2_C_CHEMICALS', '_1_A_3_C_RAILWAYS'],
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should not allow', async () => {
    store.setState(
      mockStateBuild({
        pollutantRegisterActivities: undefined,
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/276/aer/submit/prtr/summary`));

    store.setState(
      mockStateBuild({
        pollutantRegisterActivities: {
          exist: true,
          activities: ['_1_A_2_C_CHEMICALS'],
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/276/aer/submit/prtr/summary`));

    store.setState({
      ...mockStateBuild({
        pollutantRegisterActivities: {
          exist: true,
          activities: ['_1_A_2_C_CHEMICALS', '_1_A_3_C_RAILWAYS', '_1_B_2_A_OIL'],
        },
      }),
      isEditable: false,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/276/aer/submit/prtr/summary`));

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshotDelete) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/276/aer/submit/prtr/summary`));
  });
});

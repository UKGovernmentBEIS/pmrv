import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { mockClass } from '../../../../testing';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { PermitTypeSummaryGuard } from './permit-type-summary.guard';

describe('PermitTypeSummaryGuard', () => {
  let router: Router;
  let guard: PermitTypeSummaryGuard;
  let store: PermitApplicationStore;

  const tasksService = mockClass(TasksService);
  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('summary', null)];
  const routerStateSnapshot = {
    url: '/permit-application/276/permit-type/summary',
  } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    });

    guard = TestBed.inject(PermitTypeSummaryGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitApplicationStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should allow if permit type is set', async () => {
    store.setState(mockState);

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to permit type if permit not set', async () => {
    store.setState({
      ...mockState,
      permitType: undefined,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, routerStateSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl('/permit-application/276/permit-type'));
  });
});

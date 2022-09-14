import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitRevocationStore } from '../store/permit-revocation-store';
import { mockTaskState } from '../testing/mock-state';
import { NotifyOperatorGuard } from './notify-operator.guard';

describe('NotifyOperatorGuard', () => {
  let guard: NotifyOperatorGuard;
  let router: Router;
  let store: PermitRevocationStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('determination', null)];
  activatedRouteSnapshot.params = { taskId: mockTaskState.requestTaskId };
  activatedRouteSnapshot.data = { requestTaskActionType: 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION' };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, RouterTestingModule],
    });
    store = TestBed.inject(PermitRevocationStore);
    router = TestBed.inject(Router);
    guard = TestBed.inject(NotifyOperatorGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should  activate if prerequisites are met for permit revocation flow', async () => {
    store.setState({
      ...mockTaskState,
      sectionsCompleted: { REVOCATION_APPLY: true },
      allowedRequestTaskActions: ['PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION'],
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should not activate if determination is not completed', async () => {
    store.setState({
      ...mockTaskState,
      sectionsCompleted: { REVOCATION_APPLY: false },
      allowedRequestTaskActions: ['PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION'],
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-revocation/${activatedRouteSnapshot.params.taskId}`));
  });

  it('should  activate if prerequisites are met for withdrawn permit revocation flow', async () => {
    activatedRouteSnapshot.data = { requestTaskActionType: 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL' };
    store.setState({
      ...mockTaskState,
      reason: 'Because i have to..',
      allowedRequestTaskActions: ['PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL'],
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(true);
  });
});

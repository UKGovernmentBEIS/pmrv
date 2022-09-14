import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitSurrenderStore } from '../../store/permit-surrender.store';
import { mockTaskState } from '../../testing/mock-state';
import { NotifyOperatorGuard } from './notify-operator.guard';

describe('NotifyOperatorGuard', () => {
  let guard: NotifyOperatorGuard;
  let router: Router;
  let store: PermitSurrenderStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('determination', null)];
  activatedRouteSnapshot.params = { taskId: mockTaskState.requestTaskId };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, RouterTestingModule],
    });
    store = TestBed.inject(PermitSurrenderStore);
    router = TestBed.inject(Router);
    guard = TestBed.inject(NotifyOperatorGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should  activate if prerequisites are met', async () => {
    store.setState({
      ...mockTaskState,
      allowedRequestTaskActions: ['PERMIT_SURRENDER_NOTIFY_OPERATOR_FOR_DECISION'],
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should not activate if determination is not completed', async () => {
    store.setState({
      ...mockTaskState,
      reviewDeterminationCompleted: false,
      allowedRequestTaskActions: ['PERMIT_SURRENDER_NOTIFY_OPERATOR_FOR_DECISION'],
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-surrender/${activatedRouteSnapshot.params.taskId}/review`));
  });
});

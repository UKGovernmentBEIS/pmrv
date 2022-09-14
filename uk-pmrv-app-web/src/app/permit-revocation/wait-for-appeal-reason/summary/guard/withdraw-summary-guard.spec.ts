import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { mockTaskState } from '@permit-revocation/testing/mock-state';

import { WithdrawSummaryGuard } from './withdraw-summary-guard';

describe('Withdraw Summary Guard', () => {
  let router: Router;
  let guard: WithdrawSummaryGuard;
  let store: PermitRevocationStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('summary', null)];
  activatedRouteSnapshot.params = { taskId: 13 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
    });
    guard = TestBed.inject(WithdrawSummaryGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitRevocationStore);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return true when reason is completed', async () => {
    store.setState({
      ...store.getState(),
      isEditable: true,
      reason: 'reason for withdrawing revocation',
    });

    expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>)).resolves.toEqual(
      true,
    );
  });

  it('should redirect to edit state', async () => {
    store.setState({
      ...mockTaskState,
    });
    expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>)).resolves.toEqual(
      router.parseUrl(`/permit-revocation/${activatedRouteSnapshot.params.taskId}/wait-for-appeal/reason`),
    );
  });
});

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { mockTaskState } from '@permit-revocation/testing/mock-state';

import { WithdrawReasonGuard } from './withdraw-reason.guard';

describe('Withdraw reason Guard', () => {
  let router: Router;
  let guard: WithdrawReasonGuard;
  let store: PermitRevocationStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('summary', null)];
  activatedRouteSnapshot.params = { taskId: 13 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
    });
    guard = TestBed.inject(WithdrawReasonGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PermitRevocationStore);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return to summary page when reason is completed', async () => {
    store.setState({
      ...mockTaskState,
      isEditable: true,
      reason: 'Because i have to'       
    });

    expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>)).resolves.toEqual(
      router.parseUrl(`/permit-revocation/${activatedRouteSnapshot.params.taskId}/wait-for-appeal/summary`),
    );
  });

  it('should return true when navigated in changing state', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({
      extras: {
        state: {
          changing: true,
        },
      },
    } as any);

    store.setState({
      ...mockTaskState,
      isEditable: false,
      reason: 'Because i have to',
       
    });

    expect(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>).toEqual(true);
  });
});

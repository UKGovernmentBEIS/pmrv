import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockReviewStateBuild } from '../../testing/mock-state';
import { NotifyOperatorGuard } from './notify-operator.guard';

describe('NotifyOperatorGuard', () => {
  let guard: NotifyOperatorGuard;
  let router: Router;
  let store: PermitApplicationStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 276, index: 0 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, RouterTestingModule],
    });
    store = TestBed.inject(PermitApplicationStore);
    router = TestBed.inject(Router);
    guard = TestBed.inject(NotifyOperatorGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should  activate if prerequisites are met', async () => {
    const mockReviewState = mockReviewStateBuild(
      {
        type: 'GRANTED',
        reason: 'reason',
        activationDate: '1-1-2030',
      },
      {
        determination: true,
      },
    );
    store.setState({
      ...mockReviewState,
      allowedRequestTaskActions: ['PERMIT_ISSUANCE_NOTIFY_OPERATOR_FOR_DECISION'],
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should not activate if determination is not completed', async () => {
    const mockReviewState = mockReviewStateBuild(
      {
        type: 'GRANTED',
        reason: 'reason',
        activationDate: '1-1-2030',
      },
      {
        determination: false,
      },
    );
    store.setState({
      ...mockReviewState,
      allowedRequestTaskActions: ['PERMIT_ISSUANCE_NOTIFY_OPERATOR_FOR_DECISION'],
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-application/${activatedRouteSnapshot.params.taskId}/review`));
  });
});

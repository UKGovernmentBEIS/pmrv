import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { PermitApplicationStore } from '../../../permit-application/store/permit-application.store';
import { mockReviewStateBuild } from '../../../permit-application/testing/mock-state';
import { CommonTasksStore } from '../../../tasks/store/common-tasks.store';
import { PeerReviewDecisionGuard } from './peer-review-decision.guard';

describe('PeerReviewDecisionGuard', () => {
  let guard: PeerReviewDecisionGuard;
  let router: Router;
  let store: PermitApplicationStore;
  let commonStore: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 276, index: 0 };
  const state = {
    url: '/permit-application/279/review/peer-review-decision/',
  } as RouterStateSnapshot;

  const activatedRouteSnapshotNotification = new ActivatedRouteSnapshot();
  activatedRouteSnapshotNotification.params = { taskId: 276, index: 0 };
  const stateNotification = {
    url: '/tasks/279/permit-notification/peer-review/decision/',
  } as RouterStateSnapshot;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [KeycloakService],
      imports: [HttpClientModule, RouterTestingModule],
    });
    store = TestBed.inject(PermitApplicationStore);
    commonStore = TestBed.inject(CommonTasksStore);
    router = TestBed.inject(Router);
    guard = TestBed.inject(PeerReviewDecisionGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should  activate if prerequisites are met', async () => {
    const mockReviewState = mockReviewStateBuild();
    store.setState({
      ...mockReviewState,
      allowedRequestTaskActions: ['PERMIT_ISSUANCE_REVIEW_SUBMIT_PEER_REVIEW_DECISION'],
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, state) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should not activate if action is not allowed', async () => {
    const mockReviewState = mockReviewStateBuild();
    store.setState({
      ...mockReviewState,
      allowedRequestTaskActions: ['PERMIT_ISSUANCE_REQUEST_PEER_REVIEW'],
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot, state) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/permit-application/${activatedRouteSnapshot.params.taskId}/review`));
  });

  it('should  activate if prerequisites for notification are met', async () => {
    commonStore.setState({
      ...commonStore.getState(),
      requestTaskItem: {
        allowedRequestTaskActions: ['PERMIT_NOTIFICATION_REVIEW_SUBMIT_PEER_REVIEW_DECISION'],
      },
    });

    await expect(
      firstValueFrom(
        guard.canActivate(activatedRouteSnapshotNotification, stateNotification) as Observable<boolean | UrlTree>,
      ),
    ).resolves.toEqual(true);
  });
});

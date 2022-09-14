import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { PermitNotificationFollowUpApplicationReviewRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { FollowUpReviewNotifyOperatorGuard } from './notify-operator.guard';

describe('NotifyOperatorGuard', () => {
  let guard: FollowUpReviewNotifyOperatorGuard;
  let router: Router;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('determination', null)];
  activatedRouteSnapshot.params = { taskId: 1 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [KeycloakService],
      imports: [HttpClientModule, RouterTestingModule],
    });
    store = TestBed.inject(CommonTasksStore);
    router = TestBed.inject(Router);
    guard = TestBed.inject(FollowUpReviewNotifyOperatorGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should not activate if decision is not taken', async () => {
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: {
        requestTask: {
          payload: {
            payloadType: 'PERMIT_NOTIFICATION_APPLICATION_REVIEW_PAYLOAD',
            reviewDecision: {},
          } as PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
          id: 1,
          type: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW',
        },
        allowedRequestTaskActions: ['PERMIT_NOTIFICATION_FOLLOW_UP_NOTIFY_OPERATOR_FOR_DECISION'],
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(`/tasks/${activatedRouteSnapshot.params.taskId}/permit-notification/follow-up/review`),
    );
  });

  it('should  activate if prerequisites are met', async () => {
    store.setState({
      ...store.getState(),
      storeInitialized: true,
      isEditable: true,
      requestTaskItem: {
        requestTask: {
          id: 1,
          type: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW',
          payload: {
            payloadType: 'PERMIT_NOTIFICATION_APPLICATION_REVIEW_PAYLOAD',
            reviewDecision: {
              type: 'ACCEPTED',
              notes: 'some notes',
            },
          } as PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
        },
        allowedRequestTaskActions: ['PERMIT_NOTIFICATION_FOLLOW_UP_NOTIFY_OPERATOR_FOR_DECISION'],
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<boolean | UrlTree>),
    ).resolves.toEqual(true);
  });
});

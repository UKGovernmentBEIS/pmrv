import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { PermitNotificationFollowUpRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { SummaryGuard } from './summary.guard';

describe('Summary Guard', () => {
  let router: Router;
  let guard: SummaryGuard;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('summary', null)];
  activatedRouteSnapshot.params = { taskId: 63 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [KeycloakService],
      imports: [RouterTestingModule, HttpClientTestingModule],
    });
    guard = TestBed.inject(SummaryGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return true when response is completed', async () => {
    store.setState({
      ...store.getState(),
      isEditable: true,
      requestTaskItem: {
        requestTask: {
          id: 63,
          type: 'PERMIT_NOTIFICATION_FOLLOW_UP',
          payload: {
            payloadType: 'PERMIT_NOTIFICATION_FOLLOW_UP_PAYLOAD',
            followUpResponse: 'test',
          } as PermitNotificationFollowUpRequestTaskPayload,
        },
      },
    });

    expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>)).resolves.toEqual(
      true,
    );
  });

  it('should redirect to edit state', async () => {
    store.setState({
      ...store.getState(),
      isEditable: true,
      requestTaskItem: {
        requestTask: {
          id: 63,
          type: 'PERMIT_NOTIFICATION_FOLLOW_UP',
          payload: {
            payloadType: 'PERMIT_NOTIFICATION_FOLLOW_UP_PAYLOAD',
          } as PermitNotificationFollowUpRequestTaskPayload,
        },
      },
    });
    expect(firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>)).resolves.toEqual(
      router.parseUrl(`/tasks/${activatedRouteSnapshot.params.taskId}/permit-notification/follow-up/response`),
    );
  });
});

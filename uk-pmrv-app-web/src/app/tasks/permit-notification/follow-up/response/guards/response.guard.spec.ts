import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { PermitNotificationFollowUpRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { ResponseGuard } from './response.guard';

describe('Response Guard', () => {
  let router: Router;
  let guard: ResponseGuard;
  let store: CommonTasksStore;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('summary', null)];
  activatedRouteSnapshot.params = { taskId: 63 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });
    guard = TestBed.inject(ResponseGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(CommonTasksStore);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return to summary page when reason is completed', async () => {
    store.setState({
      ...store.getState(),
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
      router.parseUrl(`/tasks/${activatedRouteSnapshot.params.taskId}/permit-notification/follow-up/summary`),
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

    expect(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>).toEqual(true);
  });
});

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable, of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { OtherFactor } from 'pmrv-api';

import { PermitNotificationService } from '../../core/permit-notification.service';
import { AnswersGuard } from './answers.guard';

describe('AnswersGuard', () => {
  let router: Router;
  let guard: AnswersGuard;
  let permitNotificationService: PermitNotificationService;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('answers', null)];
  activatedRouteSnapshot.params = { taskId: 1 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });
    guard = TestBed.inject(AnswersGuard);
    router = TestBed.inject(Router);
    permitNotificationService = TestBed.inject(PermitNotificationService);

    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({} as any);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should redirect to summary if wizard is complete', async () => {
    jest.spyOn(permitNotificationService, 'getPayload').mockReturnValue(
      of({
        payloadType: 'PERMIT_NOTIFICATION_APPLICATION_SUBMIT_PAYLOAD',
        permitNotification: {
          type: 'OTHER_FACTOR',
          description: 'description',
          reportingType: 'RENOUNCE_FREE_ALLOCATIONS',
        } as OtherFactor,
        sectionsCompleted: {
          DETAILS_CHANGE: true,
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/permit-notification/submit/summary`));
  });

  it('should allow if wizard is completed but not submitted', async () => {
    jest.spyOn(permitNotificationService, 'getPayload').mockReturnValue(
      of({
        payloadType: 'PERMIT_NOTIFICATION_APPLICATION_SUBMIT_PAYLOAD',
        permitNotification: {
          type: 'OTHER_FACTOR',
          description: 'description',
          reportingType: 'RENOUNCE_FREE_ALLOCATIONS',
        } as OtherFactor,
        sectionsCompleted: {
          DETAILS_CHANGE: false,
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to details-of-change in any other case', async () => {
    jest.spyOn(permitNotificationService, 'getPayload').mockReturnValue(
      of({
        payloadType: 'PERMIT_NOTIFICATION_APPLICATION_SUBMIT_PAYLOAD',
        permitNotification: {
          type: 'OTHER_FACTOR',
        } as OtherFactor,
        sectionsCompleted: {
          DETAILS_CHANGE: false,
        },
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/tasks/1/permit-notification/submit/details-of-change`));
  });
});

import { HttpClientTestingModule } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlSegment, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable, of } from 'rxjs';

import { KeycloakService } from 'keycloak-angular';

import { OtherFactor } from 'pmrv-api';

import { PermitNotificationService } from '../../core/permit-notification.service';
import { DetailsOfChangeGuard } from './details-of-change.guard';

describe('DetailsOfChangeGuard', () => {
  let router: Router;
  let guard: DetailsOfChangeGuard;
  let permitNotificationService: PermitNotificationService;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.url = [new UrlSegment('details-of-change', null)];
  activatedRouteSnapshot.params = { taskId: 1 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [KeycloakService],
    });
    guard = TestBed.inject(DetailsOfChangeGuard);
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

  it('should redirect to answers if wizard is completed but not submitted', async () => {
    jest.spyOn(permitNotificationService, 'getIsEditable').mockReturnValue(of(true));

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
    ).resolves.toEqual(router.parseUrl(`/tasks/1/permit-notification/submit/answers`));
  });

  it('should allow in any other case', async () => {
    jest.spyOn(permitNotificationService, 'getIsEditable').mockReturnValue(of(true));
    jest.spyOn(permitNotificationService, 'getPayload').mockReturnValue(of(null));

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });
});

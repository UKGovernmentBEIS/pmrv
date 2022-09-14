import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable, of } from 'rxjs';

import { RequestItemsService } from 'pmrv-api';

import { mockClass } from '../../../../testing';
import { QuestionsGuard } from './questions.guard';

describe('QuestionsGuard', () => {
  let router: Router;
  let guard: QuestionsGuard;

  const requestItemsService = mockClass(RequestItemsService);
  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 123 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [{ provide: RequestItemsService, useValue: requestItemsService }],
    });
    guard = TestBed.inject(QuestionsGuard);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should be true if no rfi rde exists', async () => {
    requestItemsService.getItemsByRequestUsingGET.mockReturnValueOnce(
      of({
        totalItems: 1,
        items: [
          {
            taskType: 'PERMIT_ISSUANCE_WAIT_FOR_AMENDS',
          },
        ],
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should redirect to not allowed if rfi rde exists', async () => {
    requestItemsService.getItemsByRequestUsingGET.mockReturnValueOnce(
      of({
        totalItems: 2,
        items: [
          {
            taskType: 'PERMIT_ISSUANCE_WAIT_FOR_AMENDS',
          },
          {
            taskType: 'PERMIT_ISSUANCE_WAIT_FOR_RFI_RESPONSE',
          },
        ],
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/rfi/${activatedRouteSnapshot.params.taskId}/not-allowed`));

    requestItemsService.getItemsByRequestUsingGET.mockReturnValueOnce(
      of({
        totalItems: 2,
        items: [
          {
            taskType: 'PERMIT_ISSUANCE_WAIT_FOR_AMENDS',
          },
          {
            taskType: 'PERMIT_ISSUANCE_WAIT_FOR_RDE_RESPONSE',
          },
        ],
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/rfi/${activatedRouteSnapshot.params.taskId}/not-allowed`));

    requestItemsService.getItemsByRequestUsingGET.mockReturnValueOnce(
      of({
        totalItems: 2,
        items: [
          {
            taskType: 'PERMIT_SURRENDER_WAIT_FOR_REVIEW',
          },
          {
            taskType: 'PERMIT_SURRENDER_WAIT_FOR_RFI_RESPONSE',
          },
        ],
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/rfi/${activatedRouteSnapshot.params.taskId}/not-allowed`));

    requestItemsService.getItemsByRequestUsingGET.mockReturnValueOnce(
      of({
        totalItems: 2,
        items: [
          {
            taskType: 'PERMIT_SURRENDER_WAIT_FOR_REVIEW',
          },
          {
            taskType: 'PERMIT_SURRENDER_WAIT_FOR_RDE_RESPONSE',
          },
        ],
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/rfi/${activatedRouteSnapshot.params.taskId}/not-allowed`));

    requestItemsService.getItemsByRequestUsingGET.mockReturnValueOnce(
      of({
        totalItems: 2,
        items: [
          {
            taskType: 'PERMIT_NOTIFICATION_RFI_RESPONSE_SUBMIT',
          },
          {
            taskType: 'PERMIT_NOTIFICATION_WAIT_FOR_RFI_RESPONSE',
          },
        ],
      }),
    );

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/rfi/${activatedRouteSnapshot.params.taskId}/not-allowed`));
  });
});

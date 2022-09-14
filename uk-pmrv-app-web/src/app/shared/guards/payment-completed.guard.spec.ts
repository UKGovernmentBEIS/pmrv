import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { PaymentCompletedGuard } from '@shared/guards/payment-completed.guard';

import { RequestItemsService } from 'pmrv-api';

import { mockClass } from '../../../testing';

describe('PaymentCompletedGuard', () => {
  let guard: PaymentCompletedGuard;
  let router: Router;

  const requestItemsService = mockClass(RequestItemsService);

  const routerPermitStateSnapshot = { url: '/permit-application/15/review' } as RouterStateSnapshot;
  const routerSurrenderStateSnapshot = { url: '/permit-surrender/15/review' } as RouterStateSnapshot;
  const routerRFIStateSnapshot = { url: '/rfi/15' } as RouterStateSnapshot;
  const routerRDEStateSnapshot = { url: '/rde/15' } as RouterStateSnapshot;
  const routerPermitRevocationStateSnapshot = { url: '/permit-revocation/15' } as RouterStateSnapshot;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 123 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientModule, RouterTestingModule],
      providers: [{ provide: RequestItemsService, useValue: requestItemsService }],
    });
    guard = TestBed.inject(PaymentCompletedGuard);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should be true if no payment task exists', async () => {
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

    await expect(lastValueFrom(guard.canActivate(activatedRouteSnapshot, routerPermitStateSnapshot))).resolves.toEqual(
      true,
    );
  });

  it('should redirect to payment not completed page if payment task exists', async () => {
    requestItemsService.getItemsByRequestUsingGET.mockReturnValueOnce(
      of({
        totalItems: 1,
        items: [
          {
            taskType: 'PERMIT_ISSUANCE_TRACK_PAYMENT',
          },
        ],
      }),
    );
    await expect(lastValueFrom(guard.canActivate(activatedRouteSnapshot, routerPermitStateSnapshot))).resolves.toEqual(
      router.parseUrl(`/permit-application/${activatedRouteSnapshot.params.taskId}/review/payment-not-completed`),
    );

    requestItemsService.getItemsByRequestUsingGET.mockReturnValueOnce(
      of({
        totalItems: 1,
        items: [
          {
            taskType: 'PERMIT_ISSUANCE_CONFIRM_PAYMENT',
          },
        ],
      }),
    );
    await expect(lastValueFrom(guard.canActivate(activatedRouteSnapshot, routerPermitStateSnapshot))).resolves.toEqual(
      router.parseUrl(`/permit-application/${activatedRouteSnapshot.params.taskId}/review/payment-not-completed`),
    );

    requestItemsService.getItemsByRequestUsingGET.mockReturnValueOnce(
      of({
        totalItems: 1,
        items: [
          {
            taskType: 'PERMIT_SURRENDER_TRACK_PAYMENT',
          },
        ],
      }),
    );
    await expect(
      lastValueFrom(guard.canActivate(activatedRouteSnapshot, routerSurrenderStateSnapshot)),
    ).resolves.toEqual(
      router.parseUrl(`/permit-surrender/${activatedRouteSnapshot.params.taskId}/review/payment-not-completed`),
    );

    requestItemsService.getItemsByRequestUsingGET.mockReturnValueOnce(
      of({
        totalItems: 1,
        items: [
          {
            taskType: 'PERMIT_SURRENDER_CONFIRM_PAYMENT',
          },
        ],
      }),
    );
    await expect(
      lastValueFrom(guard.canActivate(activatedRouteSnapshot, routerSurrenderStateSnapshot)),
    ).resolves.toEqual(
      router.parseUrl(`/permit-surrender/${activatedRouteSnapshot.params.taskId}/review/payment-not-completed`),
    );

    requestItemsService.getItemsByRequestUsingGET.mockReturnValueOnce(
      of({
        totalItems: 1,
        items: [
          {
            taskType: 'PERMIT_SURRENDER_CONFIRM_PAYMENT',
          },
        ],
      }),
    );
    await expect(lastValueFrom(guard.canActivate(activatedRouteSnapshot, routerRFIStateSnapshot))).resolves.toEqual(
      router.parseUrl(`/rfi/${activatedRouteSnapshot.params.taskId}/payment-not-completed`),
    );

    requestItemsService.getItemsByRequestUsingGET.mockReturnValueOnce(
      of({
        totalItems: 1,
        items: [
          {
            taskType: 'PERMIT_SURRENDER_CONFIRM_PAYMENT',
          },
        ],
      }),
    );
    await expect(lastValueFrom(guard.canActivate(activatedRouteSnapshot, routerRDEStateSnapshot))).resolves.toEqual(
      router.parseUrl(`/rde/${activatedRouteSnapshot.params.taskId}/payment-not-completed`),
    );

    requestItemsService.getItemsByRequestUsingGET.mockReturnValueOnce(
      of({
        totalItems: 1,
        items: [
          {
            taskType: 'PERMIT_REVOCATION_TRACK_PAYMENT',
          },
        ],
      }),
    );
    await expect(
      lastValueFrom(guard.canActivate(activatedRouteSnapshot, routerPermitRevocationStateSnapshot)),
    ).resolves.toEqual(
      router.parseUrl(`/permit-revocation/${activatedRouteSnapshot.params.taskId}/payment-not-completed`),
    );
  });
});

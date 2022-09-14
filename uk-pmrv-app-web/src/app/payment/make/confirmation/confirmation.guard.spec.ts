import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PaymentStore } from '../../store/payment.store';
import { mockPaymentState } from '../../testing/mock-state';
import { ConfirmationGuard } from './confirmation.guard';

describe('ConfirmationGuard', () => {
  let store: PaymentStore;
  let guard: ConfirmationGuard;
  let router: Router;

  const activatedRouteSnapshotBank = new ActivatedRouteSnapshot();
  activatedRouteSnapshotBank.params = { taskId: 1 };
  activatedRouteSnapshotBank.queryParams = { method: 'BANK_TRANSFER' };

  const activatedRouteSnapshotCredit = new ActivatedRouteSnapshot();
  activatedRouteSnapshotCredit.queryParams = { method: 'CREDIT_OR_DEBIT_CARD' };

  const activatedRouteSnapshotNoQueryParam = new ActivatedRouteSnapshot();
  activatedRouteSnapshotNoQueryParam.params = { taskId: 1 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientModule],
    });
    guard = TestBed.inject(ConfirmationGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PaymentStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState({
      ...mockPaymentState,
      selectedPaymentMethod: 'BANK_TRANSFER',
      markedAsPaid: true,
      completed: true,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshotBank) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshotCredit) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should not activate', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState({
      ...mockPaymentState,
      selectedPaymentMethod: 'BANK_TRANSFER',
      completed: true,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshotBank) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/payment/${activatedRouteSnapshotBank.params.taskId}/make/details`));

    store.setState({
      ...mockPaymentState,
      selectedPaymentMethod: 'BANK_TRANSFER',
      markedAsPaid: true,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshotBank) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/payment/${activatedRouteSnapshotBank.params.taskId}/make/details`));

    store.setState({
      ...mockPaymentState,
      selectedPaymentMethod: 'BANK_TRANSFER',
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshotBank) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/payment/${activatedRouteSnapshotBank.params.taskId}/make/details`));

    store.setState(mockPaymentState);

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshotBank) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/payment/${activatedRouteSnapshotBank.params.taskId}/make/details`));

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshotNoQueryParam) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/payment/${activatedRouteSnapshotNoQueryParam.params.taskId}/make/details`));
  });
});

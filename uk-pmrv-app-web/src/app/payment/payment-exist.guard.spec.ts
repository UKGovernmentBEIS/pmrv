import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PaymentExistGuard } from './payment-exist.guard';
import { PaymentStore } from './store/payment.store';
import { mockPaymentState } from './testing/mock-state';

describe('PaymentExistGuard', () => {
  let store: PaymentStore;
  let guard: PaymentExistGuard;
  let router: Router;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 500 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientModule],
    });
    guard = TestBed.inject(PaymentExistGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PaymentStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState(mockPaymentState);

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);

    store.setState({
      ...mockPaymentState,
      isEditable: false,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should not activate', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState({
      ...mockPaymentState,
      paymentDetails: {
        ...mockPaymentState.paymentDetails,
        externalPaymentId: 'payment id',
      },
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(
      router.parseUrl(`/payment/${activatedRouteSnapshot.params.taskId}/make/confirmation?method=CREDIT_OR_DEBIT_CARD`),
    );
  });
});

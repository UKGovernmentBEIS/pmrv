import { HttpClientModule } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom, Observable } from 'rxjs';

import { PaymentStore } from '../../store/payment.store';
import { mockPaymentState } from '../../testing/mock-state';
import { MarkPaidGuard } from './mark-paid.guard';

describe('MarkPaidGuard', () => {
  let store: PaymentStore;
  let guard: MarkPaidGuard;
  let router: Router;

  const activatedRouteSnapshot = new ActivatedRouteSnapshot();
  activatedRouteSnapshot.params = { taskId: 1 };

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule, HttpClientModule],
    });
    guard = TestBed.inject(MarkPaidGuard);
    router = TestBed.inject(Router);
    store = TestBed.inject(PaymentStore);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should activate if bank transfer and marked as paid', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState({
      ...mockPaymentState,
      selectedPaymentMethod: 'BANK_TRANSFER',
      markedAsPaid: true,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(true);
  });

  it('should not activate if not bank transfer or not marked as paid', async () => {
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { changing: false } } } as any);

    store.setState({
      ...mockPaymentState,
      selectedPaymentMethod: 'CREDIT_OR_DEBIT_CARD',
      markedAsPaid: true,
    });

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/payment/${activatedRouteSnapshot.params.taskId}/make/details`));

    store.setState(mockPaymentState);

    await expect(
      firstValueFrom(guard.canActivate(activatedRouteSnapshot) as Observable<true | UrlTree>),
    ).resolves.toEqual(router.parseUrl(`/payment/${activatedRouteSnapshot.params.taskId}/make/details`));
  });
});

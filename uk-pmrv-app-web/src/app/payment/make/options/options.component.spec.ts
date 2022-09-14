import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';

import { PaymentsService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../../testing';
import { ReturnLinkComponent } from '../../shared/components/return-link/return-link.component';
import { PaymentMethodDescriptionPipe } from '../../shared/pipes/payment-method-description.pipe';
import { PaymentStore } from '../../store/payment.store';
import { mockPaymentState } from '../../testing/mock-state';
import { OptionsComponent } from './options.component';

describe('OptionsComponent', () => {
  let component: OptionsComponent;
  let fixture: ComponentFixture<OptionsComponent>;
  let store: PaymentStore;
  let page: Page;
  let router: Router;
  let activatedRoute: ActivatedRoute;

  const paymentsService = mockClass(PaymentsService);

  class Page extends BasePage<OptionsComponent> {
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get payOptions() {
      return Array.from(this.query<HTMLDivElement>('.govuk-radios').querySelectorAll('input[type="radio"]'));
    }

    get payOption0() {
      return this.query<HTMLInputElement>('input[type="radio"]#paymentMethod-option0');
    }

    get payOption1() {
      return this.query<HTMLInputElement>('input[type="radio"]#paymentMethod-option1');
    }

    get continueButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [OptionsComponent, PaymentMethodDescriptionPipe, ReturnLinkComponent],
      providers: [{ provide: PaymentsService, useValue: paymentsService }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PaymentStore);
    store.setState(mockPaymentState);
    fixture = TestBed.createComponent(OptionsComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to bank transfer page', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    expect(page.payOptions).toHaveLength(2);
    page.continueButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummary.textContent).toContain('Select a payment method');

    page.payOption1.click();
    page.continueButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(navigateSpy).toHaveBeenCalledWith(['../bank-transfer'], { relativeTo: activatedRoute });
  });

  it('should navigate to confirmation page when payment exist', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    paymentsService.createCardPaymentUsingPOST.mockReturnValueOnce(
      of({
        pendingPaymentExist: true,
      }),
    );

    page.payOption0.click();
    page.continueButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();

    expect(navigateSpy).toHaveBeenCalledWith(['../confirmation'], {
      relativeTo: activatedRoute,
      queryParams: { method: 'CREDIT_OR_DEBIT_CARD' },
    });
  });

  it('should navigate to govuk page when payment not exist', () => {
    Object.defineProperty(window, 'location', {
      writable: true,
      value: { assign: jest.fn() },
    });

    const navigateSpy = jest.spyOn(router, 'navigate');
    paymentsService.createCardPaymentUsingPOST.mockReturnValueOnce(
      of({
        nextUrl: 'http://govuk',
        pendingPaymentExist: false,
      }),
    );

    page.payOption0.click();
    page.continueButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();

    expect(navigateSpy).not.toHaveBeenCalled();
    expect(window.location.assign).toBeCalledWith('http://govuk');
  });
});

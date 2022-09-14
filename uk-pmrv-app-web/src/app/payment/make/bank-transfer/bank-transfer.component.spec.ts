import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { BasePage } from '../../../../testing';
import { ReturnLinkComponent } from '../../shared/components/return-link/return-link.component';
import { PaymentStore } from '../../store/payment.store';
import { mockPaymentState } from '../../testing/mock-state';
import { BankTransferComponent } from './bank-transfer.component';

describe('BankTransferComponent', () => {
  let component: BankTransferComponent;
  let fixture: ComponentFixture<BankTransferComponent>;
  let store: PaymentStore;
  let page: Page;
  let router: Router;
  let activatedRoute: ActivatedRoute;

  class Page extends BasePage<BankTransferComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get markAsPaidButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [BankTransferComponent, ReturnLinkComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PaymentStore);
    store.setState(mockPaymentState);
    fixture = TestBed.createComponent(BankTransferComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to confirm page', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    expect(page.summaryListValues).toEqual([
      ['Sort code', 'sortCode'],
      ['Account number', 'accountNumber'],
      ['Account name', 'accountName'],
      ['Your payment reference', 'AEM-323-1'],
      ['Amount to pay', '£2,500.20'],

      ['Bank identifier code (BIC)', 'swiftCode'],
      ['Account number (IBAN)', 'iban'],
      ['Account name', 'accountName'],
    ]);

    page.markAsPaidButton.click();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledWith(['../mark-paid'], { relativeTo: activatedRoute });
  });
});

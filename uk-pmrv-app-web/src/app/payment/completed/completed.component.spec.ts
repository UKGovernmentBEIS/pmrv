import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { BasePage } from '../../../testing';
import { PaymentModule } from '../payment.module';
import { PaymentStore } from '../store/payment.store';
import { mockPaymentActionState } from '../testing/mock-state';
import { CompletedComponent } from './completed.component';

describe('CompletedComponent', () => {
  let component: CompletedComponent;
  let fixture: ComponentFixture<CompletedComponent>;
  let store: PaymentStore;
  let page: Page;

  class Page extends BasePage<CompletedComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, PaymentModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PaymentStore);
    store.setState({
      ...mockPaymentActionState,
      actionPayload: {
        payloadType: 'PAYMENT_COMPLETED_PAYLOAD',
        paymentRefNum: 'AEM-323-1',
        paymentDate: '2022-05-05',
        paidByFullName: 'First Last',
        amount: 2500.2,
        status: 'COMPLETED',
        paymentMethod: 'CREDIT_OR_DEBIT_CARD',
      },
    });
    fixture = TestBed.createComponent(CompletedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the summary page', () => {
    expect(page.summaryListValues).toEqual([
      ['Payment status', 'Completed'],
      ['Date paid', '5 May 2022'],
      ['Paid by', 'First Last'],
      ['Payment method', 'Debit card or credit card'],
      ['Reference number', 'AEM-323-1'],
      ['Amount', '£2,500.20'],
    ]);
  });
});

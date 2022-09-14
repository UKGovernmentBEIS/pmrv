import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { BasePage } from '../../../testing';
import { PaymentModule } from '../payment.module';
import { PaymentStore } from '../store/payment.store';
import { mockPaymentActionState } from '../testing/mock-state';
import { ReceivedComponent } from './received.component';

describe('ReceivedComponent', () => {
  let component: ReceivedComponent;
  let fixture: ComponentFixture<ReceivedComponent>;
  let store: PaymentStore;
  let page: Page;

  class Page extends BasePage<ReceivedComponent> {
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
        ...mockPaymentActionState.actionPayload,
        payloadType: 'PAYMENT_MARKED_AS_RECEIVED_PAYLOAD',
        status: 'MARK_AS_RECEIVED',
        receivedDate: '2022-05-07',
      },
    });
    fixture = TestBed.createComponent(ReceivedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the summary page', () => {
    expect(page.summaryListValues).toEqual([
      ['Payment status', 'Marked as received'],
      ['Date marked as paid', '5 May 2022'],
      ['Date received', '7 May 2022'],
      ['Paid by', 'First Last'],
      ['Payment method', 'Bank Transfer (BACS)'],
      ['Reference number', 'AEM-323-1'],
      ['Amount', '£2,500.20'],
    ]);
  });
});

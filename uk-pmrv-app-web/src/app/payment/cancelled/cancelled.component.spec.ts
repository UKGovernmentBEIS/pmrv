import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { BasePage } from '../../../testing';
import { PaymentModule } from '../payment.module';
import { PaymentStore } from '../store/payment.store';
import { mockPaymentActionState } from '../testing/mock-state';
import { CancelledComponent } from './cancelled.component';

describe('CancelledComponent', () => {
  let component: CancelledComponent;
  let fixture: ComponentFixture<CancelledComponent>;
  let store: PaymentStore;
  let page: Page;

  class Page extends BasePage<CancelledComponent> {
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
        status: 'CANCELLED',
        cancellationReason: 'Reason of cancellation',
      },
    });
    fixture = TestBed.createComponent(CancelledComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the summary page', () => {
    expect(page.summaryListValues).toEqual([
      ['Payment status', 'Cancelled'],
      ['Reason', 'Reason of cancellation'],
    ]);
  });
});

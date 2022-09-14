import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { BasePage } from '../../../testing';
import { PaymentModule } from '../payment.module';
import { PaymentStore } from '../store/payment.store';
import { mockPaymentActionState } from '../testing/mock-state';
import { PaidComponent } from './paid.component';

describe('PaidComponent', () => {
  let component: PaidComponent;
  let fixture: ComponentFixture<PaidComponent>;
  let store: PaymentStore;
  let page: Page;

  class Page extends BasePage<PaidComponent> {
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
    store.setState(mockPaymentActionState);
    fixture = TestBed.createComponent(PaidComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show the summary page', () => {
    expect(page.summaryListValues).toEqual([
      ['Payment status', 'Marked as paid'],
      ['Date marked as paid', '5 May 2022'],
      ['Paid by', 'First Last'],
      ['Payment method', 'Bank Transfer (BACS)'],
      ['Reference number', 'AEM-323-1'],
      ['Amount', '£2,500.20'],
    ]);
  });
});

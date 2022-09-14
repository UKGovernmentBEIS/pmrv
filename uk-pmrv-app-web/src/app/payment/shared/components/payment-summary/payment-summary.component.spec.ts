import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';

import { BasePage } from '../../../../../testing';
import { PaymentDetails } from '../../../core/payment.map';
import { PaymentMethodDescriptionPipe } from '../../pipes/payment-method-description.pipe';
import { PaymentStatusPipe } from '../../pipes/payment-status.pipe';
import { PaymentSummaryComponent } from './payment-summary.component';

describe('PaymentSummaryComponent', () => {
  let component: PaymentSummaryComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: `<app-payment-summary [details]="details"></app-payment-summary>`,
  })
  class TestComponent {
    details = {
      amount: 2500.2,
      paidByFullName: 'First Last',
      paymentDate: '2022-05-05',
      paymentMethod: 'CREDIT_OR_DEBIT_CARD',
      paymentRefNum: 'AEM-323-1',
      receivedDate: '2022-05-06',
      status: 'MARK_AS_RECEIVED',
    } as PaymentDetails;
  }

  class Page extends BasePage<TestComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [TestComponent, PaymentSummaryComponent, PaymentStatusPipe, PaymentMethodDescriptionPipe],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(PaymentSummaryComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the payment summary', () => {
    expect(page.summaryListValues).toEqual([
      ['Payment status', 'Marked as received'],
      ['Date paid', '5 May 2022'],
      ['Paid by', 'First Last'],
      ['Payment method', 'Debit card or credit card'],
      ['Amount', '£2,500.20'],
    ]);
  });
});

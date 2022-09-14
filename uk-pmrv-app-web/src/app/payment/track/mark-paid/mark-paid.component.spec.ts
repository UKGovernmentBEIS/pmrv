import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../../testing';
import { PaymentModule } from '../../payment.module';
import { PaymentStore } from '../../store/payment.store';
import { mockPaymentState } from '../../testing/mock-state';
import { MarkPaidComponent } from './mark-paid.component';

describe('MarkPaidComponent', () => {
  let component: MarkPaidComponent;
  let fixture: ComponentFixture<MarkPaidComponent>;
  let store: PaymentStore;
  let page: Page;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<MarkPaidComponent> {
    set receivedDateDay(value: string) {
      this.setInputValue('#receivedDate-day', value);
    }
    set receivedDateMonth(value: string) {
      this.setInputValue('#receivedDate-month', value);
    }
    set receivedDateYear(value: string) {
      this.setInputValue('#receivedDate-year', value);
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }
    get errorSummaryErrorList() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((anchor) =>
        anchor.textContent.trim(),
      );
    }
    get confirmationTitle() {
      return this.query<HTMLHeadingElement>('h1.govuk-panel__title');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, PaymentModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PaymentStore);
    store.setState({
      ...mockPaymentState,
      requestTaskItem: {
        ...mockPaymentState.requestTaskItem,
        allowedRequestTaskActions: ['PAYMENT_MARK_AS_RECEIVED', 'PAYMENT_CANCEL'],
        requestTask: {
          type: 'PERMIT_ISSUANCE_TRACK_PAYMENT',
        },
      },
      paymentDetails: {
        amount: 2500.2,
        paymentRefNum: 'AEM-323-1',
      },
      markedAsPaid: true,
    });
    fixture = TestBed.createComponent(MarkPaidComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit a valid form and show confirm notification', () => {
    expect(page.errorSummary).toBeFalsy();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryErrorList).toEqual(['Enter the received payment date']);

    const date = new Date('2020-05-29');
    page.receivedDateDay = '29';
    page.receivedDateMonth = '5';
    page.receivedDateYear = '2020';

    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PAYMENT_MARK_AS_RECEIVED',
      requestTaskId: 500,
      requestTaskActionPayload: {
        payloadType: 'PAYMENT_MARK_AS_RECEIVED_PAYLOAD',
        receivedDate: date,
      },
    });

    expect(page.confirmationTitle).toBeTruthy();
    expect(page.confirmationTitle.textContent).toEqual('Payment marked as received');
  });
});

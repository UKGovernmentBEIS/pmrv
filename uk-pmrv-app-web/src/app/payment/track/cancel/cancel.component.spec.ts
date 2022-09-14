import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';

import { PaymentCancelRequestTaskActionPayload, TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../../testing';
import { PaymentModule } from '../../payment.module';
import { PaymentStore } from '../../store/payment.store';
import { mockPaymentState } from '../../testing/mock-state';
import { CancelComponent } from './cancel.component';

describe('CancelComponent', () => {
  let component: CancelComponent;
  let fixture: ComponentFixture<CancelComponent>;
  let store: PaymentStore;
  let page: Page;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<CancelComponent> {
    set reasonValue(value: string) {
      this.setInputValue('#reason', value);
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
    fixture = TestBed.createComponent(CancelComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit a valid form and show cancel notification ', () => {
    expect(page.errorSummary).toBeFalsy();
    expect(page.confirmationTitle).toBeFalsy();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryErrorList).toEqual(['Enter the reason that no payment is required']);

    page.reasonValue = 'A reason';

    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeFalsy();
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PAYMENT_CANCEL',
      requestTaskId: 500,
      requestTaskActionPayload: {
        payloadType: 'PAYMENT_CANCEL_PAYLOAD',
        reason: 'A reason',
      } as PaymentCancelRequestTaskActionPayload,
    });

    expect(page.confirmationTitle).toBeTruthy();
    expect(page.confirmationTitle.textContent).toEqual('Payment task cancelled');
  });
});

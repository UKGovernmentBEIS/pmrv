import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';

import { RequestTaskActionEmptyPayload, TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../../testing';
import { ReturnLinkComponent } from '../../shared/components/return-link/return-link.component';
import { PaymentStore } from '../../store/payment.store';
import { mockPaymentState } from '../../testing/mock-state';
import { MarkPaidComponent } from './mark-paid.component';

describe('MarkPaidComponent', () => {
  let component: MarkPaidComponent;
  let fixture: ComponentFixture<MarkPaidComponent>;
  let store: PaymentStore;
  let page: Page;
  let router: Router;
  let activatedRoute: ActivatedRoute;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<MarkPaidComponent> {
    get submitButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [MarkPaidComponent, ReturnLinkComponent],
      providers: [{ provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PaymentStore);
    store.setState(mockPaymentState);
    fixture = TestBed.createComponent(MarkPaidComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit and navigate to confirmation page', () => {
    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'PAYMENT_MARK_AS_PAID',
      requestTaskId: mockPaymentState.requestTaskId,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      } as RequestTaskActionEmptyPayload,
    });

    expect(navigateSpy).toHaveBeenCalledWith(['../confirmation'], {
      relativeTo: activatedRoute,
      queryParams: { method: 'BANK_TRANSFER' },
    });
  });
});

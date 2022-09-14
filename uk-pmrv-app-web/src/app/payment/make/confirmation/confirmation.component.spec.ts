import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { BehaviorSubject, of } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { SharedModule } from '@shared/shared.module';
import { KeycloakService } from 'keycloak-angular';
import { KeycloakProfile } from 'keycloak-js';

import { PaymentsService } from 'pmrv-api';

import { BasePage, mockClass, MockType } from '../../../../testing';
import { PaymentModule } from '../../payment.module';
import { PaymentStore } from '../../store/payment.store';
import { mockPaymentState } from '../../testing/mock-state';
import { ConfirmationComponent } from './confirmation.component';

describe('ConfirmationComponent', () => {
  let component: ConfirmationComponent;
  let fixture: ComponentFixture<ConfirmationComponent>;
  let store: PaymentStore;
  let page: Page;

  const keycloakService = mockClass(KeycloakService);
  const authService: MockType<AuthService> = {
    userProfile: new BehaviorSubject<KeycloakProfile>({ firstName: 'First', lastName: 'Last' }),
  };
  const paymentsService = mockClass(PaymentsService);

  const activatedRouteBank = { queryParams: of({ method: 'BANK_TRANSFER' }) };
  const activatedRouteCard = {
    paramMap: of(convertToParamMap({ taskId: '500' })),
    queryParams: of({ method: 'CREDIT_OR_DEBIT_CARD' }),
  };

  class Page extends BasePage<ConfirmationComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  describe('for bank method', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [SharedModule, RouterTestingModule, PaymentModule],
        providers: [
          { provide: ActivatedRoute, useValue: activatedRouteBank },
          { provide: KeycloakService, useValue: keycloakService },
          { provide: AuthService, useValue: authService },
        ],
      }).compileComponents();
    });

    beforeEach(() => {
      store = TestBed.inject(PaymentStore);
      store.setState({
        ...mockPaymentState,
        markedAsPaid: true,
      });
      fixture = TestBed.createComponent(ConfirmationComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show the summary page', () => {
      const govukDatePipe = new GovukDatePipe();
      const currentDate = govukDatePipe.transform(new Date().toISOString(), 'date');

      expect(page.summaryListValues).toEqual([
        ['Payment status', 'Marked as paid'],
        ['Date marked as paid', currentDate],
        ['Paid by', 'First Last'],
        ['Payment method', 'Bank Transfer (BACS)'],
        ['Amount', '£2,500.20'],
      ]);
    });
  });

  describe('for card method', () => {
    beforeEach(async () => {
      await TestBed.configureTestingModule({
        imports: [SharedModule, RouterTestingModule, PaymentModule],
        providers: [
          { provide: ActivatedRoute, useValue: activatedRouteCard },
          { provide: KeycloakService, useValue: keycloakService },
          { provide: AuthService, useValue: authService },
          { provide: PaymentsService, useValue: paymentsService },
        ],
      }).compileComponents();
    });

    beforeEach(() => {
      store = TestBed.inject(PaymentStore);
      store.setState(mockPaymentState);
      fixture = TestBed.createComponent(ConfirmationComponent);
      component = fixture.componentInstance;
      page = new Page(fixture);
      paymentsService.processExistingCardPaymentUsingPOST.mockReturnValueOnce(
        of({
          state: {
            finished: true,
            status: 'success',
          },
        }),
      );
      fixture.detectChanges();
    });

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show the summary page', () => {
      const govukDatePipe = new GovukDatePipe();
      const currentDate = govukDatePipe.transform(new Date().toISOString(), 'date');

      expect(page.summaryListValues).toEqual([
        ['Payment status', 'Completed'],
        ['Date paid', currentDate],
        ['Paid by', 'First Last'],
        ['Payment method', 'Debit card or credit card'],
        ['Amount', '£2,500.20'],
      ]);
    });
  });
});

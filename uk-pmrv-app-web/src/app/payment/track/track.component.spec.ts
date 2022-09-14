import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { PaymentModule } from '../payment.module';
import { PaymentStore } from '../store/payment.store';
import { mockPaymentState } from '../testing/mock-state';
import { TrackComponent } from './track.component';

describe('TrackComponent', () => {
  let component: TrackComponent;
  let fixture: ComponentFixture<TrackComponent>;
  let store: PaymentStore;
  let page: Page;
  let router: Router;
  let activatedRoute: ActivatedRoute;

  class Page extends BasePage<TrackComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get heading() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get buttons() {
      return this.queryAll<HTMLButtonElement>('button');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule, PaymentModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PaymentStore);
    store.setState({
      ...mockPaymentState,
      requestTaskItem: {
        ...mockPaymentState.requestTaskItem,
        allowedRequestTaskActions: ['PAYMENT_MARK_AS_RECEIVED', 'PAYMENT_CANCEL'],
        userAssignCapable: false,
      },
      paymentDetails: {
        amount: 2500.2,
        paymentRefNum: 'AEM-323-1',
      },
      markedAsPaid: true,
    });
    fixture = TestBed.createComponent(TrackComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should navigate to mark as paid page', () => {
    expect(page.heading.textContent.trim())
      .toEqual('Pay permit application fee Assigned to: Foo BarDays Remaining: 10');

    expect(page.summaryListValues).toEqual([
      ['Payment status', 'Not paid'],
      ['Reference number', 'AEM-323-1'],
      ['Amount', '£2,500.20'],
    ]);

    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
    page.buttons[0].click();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledWith(['mark-paid'], { relativeTo: activatedRoute });
  });

  it('should navigate to cancel page', () => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
    page.buttons[1].click();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledWith(['cancel'], { relativeTo: activatedRoute });
  });
});

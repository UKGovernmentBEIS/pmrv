import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '@shared/shared.module';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { PaymentStore } from '../../store/payment.store';
import { mockPaymentState } from '../../testing/mock-state';
import { DetailsComponent } from './details.component';

describe('DetailsComponent', () => {
  let component: DetailsComponent;
  let fixture: ComponentFixture<DetailsComponent>;
  let store: PaymentStore;
  let page: Page;
  let router: Router;
  let activatedRoute: ActivatedRoute;

  class Page extends BasePage<DetailsComponent> {
    get paymentDetails() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get heading() {
      return this.query<HTMLHeadingElement>('h1');
    }

    get makePaymentButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [DetailsComponent],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PaymentStore);
    store.setState(mockPaymentState);
    fixture = TestBed.createComponent(DetailsComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display payment details', () => {
    expect(page.heading.textContent.trim())
      .toEqual('Pay permit application fee Assigned to: Foo BarDays Remaining: 10');
    expect(page.paymentDetails).toEqual([
      ['Date created', '5 May 2022'],
      ['Reference number', 'AEM-323-1'],
      ['Amount to pay', '£2,500.20'],
    ]);

    const navigateSpy = jest.spyOn(router, 'navigate').mockImplementation();
    page.makePaymentButton.click();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledWith(['../options'], { relativeTo: activatedRoute });
  });
});

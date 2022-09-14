import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { ReturnLinkComponent } from '../../shared/components/return-link/return-link.component';
import { PaymentStore } from '../../store/payment.store';
import { mockPaymentState } from '../../testing/mock-state';
import { NotSuccessComponent } from './not-success.component';

describe('NotSuccessComponent', () => {
  let component: NotSuccessComponent;
  let fixture: ComponentFixture<NotSuccessComponent>;
  let store: PaymentStore;
  let page: Page;

  const activatedRoute = new ActivatedRouteStub();

  class Page extends BasePage<NotSuccessComponent> {
    get content(): HTMLHeadElement {
      return this.query('h1');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [NotSuccessComponent, ReturnLinkComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            ...activatedRoute,
            queryParams: of({ message: 'Payment cancelled by user' }),
          },
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PaymentStore);
    store.setState(mockPaymentState);
    fixture = TestBed.createComponent(NotSuccessComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show content', () => {
    expect(page.content.textContent.trim()).toEqual('Payment cancelled by user');
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { PaymentNotCompletedComponent } from '@shared/components/payment-not-completed/payment-not-completed.component';
import { PageHeadingComponent } from '@shared/page-heading/page-heading.component';

import { BasePage } from '../../../../testing';

describe('PaymentNotCompletedComponent', () => {
  let page: Page;
  let component: PaymentNotCompletedComponent;
  let fixture: ComponentFixture<PaymentNotCompletedComponent>;

  class Page extends BasePage<PaymentNotCompletedComponent> {
    get content(): HTMLHeadElement {
      return this.query('h1');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [PaymentNotCompletedComponent, PageHeadingComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PaymentNotCompletedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show content', () => {
    expect(page.content.textContent.trim()).toEqual('The payment task must be closed before you can proceed');
  });
});

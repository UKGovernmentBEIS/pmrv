import { PaymentMethodDescriptionPipe } from './payment-method-description.pipe';

describe('PaymentMethodDescriptionPipe', () => {
  let pipe: PaymentMethodDescriptionPipe;

  beforeEach(() => (pipe = new PaymentMethodDescriptionPipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should map payment method to appropriate description', () => {
    expect(pipe.transform('BANK_TRANSFER')).toEqual('Bank Transfer (BACS)');
    expect(pipe.transform('CREDIT_OR_DEBIT_CARD')).toEqual('Debit card or credit card');
    expect(pipe.transform(null)).toBeNull();
  });
});

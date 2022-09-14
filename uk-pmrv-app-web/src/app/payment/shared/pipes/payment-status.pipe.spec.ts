import { PaymentStatusPipe } from './payment-status.pipe';

describe('PaymentStatusPipe', () => {
  let pipe: PaymentStatusPipe;

  beforeEach(() => (pipe = new PaymentStatusPipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should map payment status', () => {
    expect(pipe.transform('CANCELLED')).toEqual('Cancelled');
    expect(pipe.transform('COMPLETED')).toEqual('Completed');
    expect(pipe.transform('MARK_AS_PAID')).toEqual('Marked as paid');
    expect(pipe.transform('MARK_AS_RECEIVED')).toEqual('Marked as received');
    expect(pipe.transform(null)).toEqual('Not paid');
  });
});

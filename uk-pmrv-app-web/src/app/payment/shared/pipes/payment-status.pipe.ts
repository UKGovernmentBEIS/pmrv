import { Pipe, PipeTransform } from '@angular/core';

import { PaymentCancelledRequestActionPayload, PaymentProcessedRequestActionPayload } from 'pmrv-api';

@Pipe({ name: 'paymentStatus' })
export class PaymentStatusPipe implements PipeTransform {
  transform(
    value: PaymentProcessedRequestActionPayload['status'] | PaymentCancelledRequestActionPayload['status'],
  ): string {
    switch (value) {
      case 'CANCELLED':
        return 'Cancelled';
      case 'COMPLETED':
        return 'Completed';
      case 'MARK_AS_PAID':
        return 'Marked as paid';
      case 'MARK_AS_RECEIVED':
        return 'Marked as received';
      default:
        return 'Not paid';
    }
  }
}

import { Pipe, PipeTransform } from '@angular/core';

import { PaymentProcessedRequestActionPayload } from 'pmrv-api';

@Pipe({ name: 'paymentMethodDescription' })
export class PaymentMethodDescriptionPipe implements PipeTransform {
  transform(value: PaymentProcessedRequestActionPayload['paymentMethod']): string {
    switch (value) {
      case 'BANK_TRANSFER':
        return 'Bank Transfer (BACS)';
      case 'CREDIT_OR_DEBIT_CARD':
        return 'Debit card or credit card';
      default:
        return null;
    }
  }
}

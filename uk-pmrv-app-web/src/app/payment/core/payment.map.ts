import { KeycloakProfile } from 'keycloak-js';

import { PaymentConfirmRequestTaskPayload, RequestTaskDTO } from 'pmrv-api';

import { PaymentState } from '../store/payment.state';

export interface PaymentDetails {
  amount?: number;
  paidByFullName?: string;
  paymentDate?: string;
  paymentMethod?: 'BANK_TRANSFER' | 'CREDIT_OR_DEBIT_CARD';
  paymentRefNum?: string;
  receivedDate?: string;
  status?: 'CANCELLED' | 'COMPLETED' | 'MARK_AS_PAID' | 'MARK_AS_RECEIVED';
}

export function mapMakePaymentToPaymentDetails(userProfile: KeycloakProfile, state: PaymentState): PaymentDetails {
  return {
    amount: state.paymentDetails.amount,
    paidByFullName: userProfile.firstName + ' ' + userProfile.lastName,
    paymentDate: new Date().toISOString(),
    paymentMethod: 'BANK_TRANSFER',
    paymentRefNum: state.paymentDetails.paymentRefNum,
    status: state.markedAsPaid ? 'MARK_AS_PAID' : null,
  };
}

export function mapGOVUKToPaymentDetails(
  userProfile: KeycloakProfile,
  state: PaymentState,
  status: 'CANCELLED' | 'COMPLETED' | 'MARK_AS_PAID' | 'MARK_AS_RECEIVED',
): PaymentDetails {
  return {
    amount: state.paymentDetails.amount,
    paidByFullName: userProfile.firstName + ' ' + userProfile.lastName,
    paymentDate: new Date().toISOString(),
    paymentMethod: 'CREDIT_OR_DEBIT_CARD',
    paymentRefNum: state.paymentDetails.paymentRefNum,
    status: status,
  };
}

export function mapTrackPaymentToPaymentDetails(state: PaymentState): PaymentDetails {
  return [
    'PERMIT_ISSUANCE_CONFIRM_PAYMENT',
    'PERMIT_SURRENDER_CONFIRM_PAYMENT',
    'PERMIT_REVOCATION_CONFIRM_PAYMENT',
  ].includes(state.requestTaskItem.requestTask.type)
    ? (state.paymentDetails as PaymentConfirmRequestTaskPayload)
    : {
        amount: state.paymentDetails.amount,
        paymentRefNum: state.paymentDetails.paymentRefNum,
        status: null,
      };
}

export const headingMap: Partial<Record<RequestTaskDTO['type'], string>> = {
  PERMIT_ISSUANCE_TRACK_PAYMENT: 'Payment for permit application',
  PERMIT_ISSUANCE_MAKE_PAYMENT: 'Pay permit application fee',
  PERMIT_ISSUANCE_CONFIRM_PAYMENT: 'Payment for permit application',

  PERMIT_SURRENDER_TRACK_PAYMENT: 'Payment for surrender permit application',
  PERMIT_SURRENDER_MAKE_PAYMENT: 'Pay surrender permit application fee',
  PERMIT_SURRENDER_CONFIRM_PAYMENT: 'Payment for surrender permit application',

  PERMIT_REVOCATION_TRACK_PAYMENT: 'Payment for permit revocation',
  PERMIT_REVOCATION_MAKE_PAYMENT: 'Pay permit revocation fee',
  PERMIT_REVOCATION_CONFIRM_PAYMENT: 'Payment for permit revocation',
};

export const contentMap: Partial<Record<RequestTaskDTO['type'], string>> = {
  PERMIT_ISSUANCE_TRACK_PAYMENT: 'The permit determination cannot be completed until the payment has been received',
  PERMIT_ISSUANCE_MAKE_PAYMENT: 'Your permit application cannot be processed until this payment is received',
  PERMIT_ISSUANCE_CONFIRM_PAYMENT: 'The permit determination cannot be completed until the payment has been received',

  PERMIT_SURRENDER_TRACK_PAYMENT:
    'The surrender permit determination cannot be completed until the payment has been received',
  PERMIT_SURRENDER_MAKE_PAYMENT: 'Your surrender permit application cannot be processed until this payment is received',
  PERMIT_SURRENDER_CONFIRM_PAYMENT:
    'The surrender permit determination cannot be completed until the payment has been received',

  PERMIT_REVOCATION_TRACK_PAYMENT: 'The permit revocation cannot be completed until the payment has been received',
  PERMIT_REVOCATION_MAKE_PAYMENT: 'Your permit revocation cannot be processed until this payment is received',
  PERMIT_REVOCATION_CONFIRM_PAYMENT: 'The permit revocation cannot be completed until the payment has been received',
};

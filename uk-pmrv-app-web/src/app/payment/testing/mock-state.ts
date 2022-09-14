import { PaymentState } from '../store/payment.state';

export const mockPaymentState: PaymentState = {
  requestTaskId: 500,
  requestId: 'AEM-323-1',
  requestTaskItem: {
    allowedRequestTaskActions: ['PAYMENT_MARK_AS_PAID'],
    requestInfo: {
      accountId: 121,
      competentAuthority: 'ENGLAND',
      id: 'AEM-323-1',
      type: 'PERMIT_ISSUANCE',
    },
    requestTask: {
      assignable: true,
      assigneeFullName: 'Foo Bar',
      assigneeUserId: '232',
      id: 500,
      type: 'PERMIT_ISSUANCE_MAKE_PAYMENT',
      daysRemaining: 10,
    },
    userAssignCapable: true,
  },
  isEditable: true,
  paymentDetails: {
    amount: 2500.2,
    bankAccountDetails: {
      accountName: 'accountName',
      accountNumber: 'accountNumber',
      iban: 'iban',
      sortCode: 'sortCode',
      swiftCode: 'swiftCode',
    },
    creationDate: '2022-05-05',
    payloadType: 'PAYMENT_MAKE_PAYLOAD',
    paymentMethodTypes: ['BANK_TRANSFER', 'CREDIT_OR_DEBIT_CARD'],
    paymentRefNum: 'AEM-323-1',
  },
};

export const mockPaymentActionState: PaymentState = {
  actionPayload: {
    payloadType: 'PAYMENT_MARKED_AS_PAID_PAYLOAD',
    paymentRefNum: 'AEM-323-1',
    paymentDate: '2022-05-05',
    paidByFullName: 'First Last',
    amount: 2500.2,
    status: 'MARK_AS_PAID',
    paymentMethod: 'BANK_TRANSFER',
  },
  requestActionId: 13,
  isEditable: false,
  creationDate: '2022-04-21T14:52:46.363672Z',
};

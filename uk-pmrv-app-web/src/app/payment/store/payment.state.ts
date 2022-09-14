import {
  PaymentCancelledRequestActionPayload,
  PaymentConfirmRequestTaskPayload,
  PaymentMakeRequestTaskPayload,
  PaymentProcessedRequestActionPayload,
  PaymentTrackRequestTaskPayload,
  RequestTaskItemDTO,
} from 'pmrv-api';

export interface PaymentState {
  requestTaskId?: number;
  requestId?: string;
  requestTaskItem?: RequestTaskItemDTO;
  isEditable: boolean;
  paymentDetails?: PaymentMakeRequestTaskPayload | PaymentTrackRequestTaskPayload | PaymentConfirmRequestTaskPayload;
  selectedPaymentMethod?: 'BANK_TRANSFER' | 'CREDIT_OR_DEBIT_CARD';
  markedAsPaid?: boolean;
  completed?: boolean;

  // Request action info
  requestActionId?: number;
  creationDate?: string;
  actionPayload?: PaymentProcessedRequestActionPayload | PaymentCancelledRequestActionPayload;
}

export const initialState: PaymentState = {
  isEditable: true,
};

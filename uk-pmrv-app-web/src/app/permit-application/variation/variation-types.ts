import { RequestActionDTO, RequestTaskDTO } from 'pmrv-api';

export function getVariationRequestTaskTypes(): RequestTaskDTO['type'][] {
  return [
    'PERMIT_VARIATION_APPLICATION_REVIEW',
    'PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT',
    'PERMIT_VARIATION_RDE_RESPONSE_SUBMIT',
    'PERMIT_VARIATION_REGULATOR_APPLICATION_SUBMIT',
    'PERMIT_VARIATION_RFI_RESPONSE_SUBMIT',
    'PERMIT_VARIATION_WAIT_FOR_RDE_RESPONSE',
    'PERMIT_VARIATION_WAIT_FOR_REVIEW',
    'PERMIT_VARIATION_WAIT_FOR_RFI_RESPONSE',
  ];
}

export function getVariationRequestActionTypes(): RequestActionDTO['type'][] {
  return ['PERMIT_VARIATION_APPLICATION_SUBMITTED'];
}

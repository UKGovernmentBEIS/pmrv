import { GovukSelectOption } from 'govuk-components';

import { RequestTaskActionProcessDTO, RequestTaskItemDTO } from 'pmrv-api';

export function requestTaskAllowedActions(allowedRequestTaskActions: RequestTaskItemDTO['allowedRequestTaskActions']) {
  const currentAllowedRequestTaskActions = allowedRequestTaskActions
    .filter((action) => requestTaskAllowedActionsMap[action])
    .map((action) => requestTaskAllowedActionsMap[action]);
  return currentAllowedRequestTaskActions.length > 0 ? currentAllowedRequestTaskActions : undefined;
}

const requestTaskAllowedActionsMap: Partial<
  Record<RequestTaskActionProcessDTO['requestTaskActionType'], GovukSelectOption<string>>
> = {
  RFI_SUBMIT: { text: 'Request for information', value: 'rfi' },
  PERMIT_NOTIFICATION_FOLLOW_UP_RECALL_FROM_AMENDS: { text: 'Recall your response', value: 'recall' },
};

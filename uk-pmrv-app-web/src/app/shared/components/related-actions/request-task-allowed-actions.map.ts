import { RequestTaskActionProcessDTO } from 'pmrv-api';

const relatedRequestTaskActions: Array<RequestTaskActionProcessDTO['requestTaskActionType']> = [
  'RFI_SUBMIT',
  'RDE_SUBMIT',
  'PERMIT_ISSUANCE_RECALL_FROM_AMENDS',
  'PERMIT_NOTIFICATION_FOLLOW_UP_RECALL_FROM_AMENDS',
  'RFI_CANCEL',
  'PERMIT_NOTIFICATION_CANCEL_APPLICATION',
  'PERMIT_VARIATION_CANCEL_APPLICATION',
  'PERMIT_REVOCATION_CANCEL_APPLICATION',
  'PERMIT_SURRENDER_CANCEL_APPLICATION',
];

export function hasRequestTaskAllowedActions(
  allowedRequestTaskActions: Array<RequestTaskActionProcessDTO['requestTaskActionType']>,
) {
  return allowedRequestTaskActions?.some((action) => relatedRequestTaskActions.includes(action));
}

export function requestTaskAllowedActions(
  allowedRequestTaskActions: Array<RequestTaskActionProcessDTO['requestTaskActionType']>,
  taskId: number,
) {
  return (
    allowedRequestTaskActions
      ?.filter((action) => relatedRequestTaskActions.includes(action))
      .map((action) => actionDetails(action, taskId)) ?? []
  );
}

function actionDetails(action: RequestTaskActionProcessDTO['requestTaskActionType'], taskId: number) {
  switch (action) {
    case 'RFI_SUBMIT':
      return { text: 'Request for information', link: ['/rfi', taskId, 'questions'] };
    case 'RDE_SUBMIT':
      return { text: 'Request deadline extension', link: ['/rde', taskId, 'extend-determination'] };
    case 'PERMIT_ISSUANCE_RECALL_FROM_AMENDS':
      return { text: 'Recall the permit', link: ['recall-from-amends'] };
    case 'PERMIT_NOTIFICATION_FOLLOW_UP_RECALL_FROM_AMENDS':
      return { text: 'Recall your response', link: ['recall-from-amends'] };
    case 'RFI_CANCEL':
      return { text: 'Cancel request', link: ['/rfi', taskId, 'cancel-verify'] };
    case 'PERMIT_NOTIFICATION_CANCEL_APPLICATION':
    case 'PERMIT_VARIATION_CANCEL_APPLICATION':
    case 'PERMIT_REVOCATION_CANCEL_APPLICATION':
    case 'PERMIT_SURRENDER_CANCEL_APPLICATION':
      return { text: 'Cancel task', link: ['/tasks', taskId, 'cancel'] };
    default:
      return null;
  }
}

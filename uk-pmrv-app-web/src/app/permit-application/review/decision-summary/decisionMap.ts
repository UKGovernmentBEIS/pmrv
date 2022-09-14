import { PermitIssuanceGrantDetermination } from 'pmrv-api';

export const applicationDecisionNameMap: Record<PermitIssuanceGrantDetermination['type'], string> = {
  DEEMED_WITHDRAWN: 'Permit application deemed withdrawn',
  GRANTED: 'Permit application approved',
  REJECTED: 'Permit application rejected',
};

export const determinationTypeMap: Record<PermitIssuanceGrantDetermination['type'], string> = {
  DEEMED_WITHDRAWN: 'Deem withdrawn',
  GRANTED: 'Grant',
  REJECTED: 'Reject',
};

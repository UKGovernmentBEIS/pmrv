import { PermitSurrenderReviewDeterminationGrant } from 'pmrv-api';

export function isWizardComplete(determination: PermitSurrenderReviewDeterminationGrant): boolean {
  return determination !== undefined && !!determination?.type && !!determination?.reason;
}

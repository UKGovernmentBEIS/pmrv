import { PermitSurrenderReviewDeterminationReject } from 'pmrv-api';

export function isWizardComplete(determination: PermitSurrenderReviewDeterminationReject): boolean {
  return (
    determination !== undefined &&
    !!determination?.type &&
    !!determination?.reason &&
    !!determination?.officialRefusalLetter &&
    determination?.shouldFeeBeRefundedToOperator !== undefined
  );
}

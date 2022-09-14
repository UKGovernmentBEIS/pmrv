import { PermitSurrenderReviewDeterminationGrant } from 'pmrv-api';

export function isWizardComplete(determination: PermitSurrenderReviewDeterminationGrant): boolean {
  return (
    determination !== undefined &&
    !!determination?.type &&
    !!determination?.reason &&
    !!determination?.stopDate &&
    !!determination?.noticeDate &&
    ((determination?.reportRequired === true && !!determination?.reportDate) ||
      determination?.reportRequired === false) &&
    ((determination?.allowancesSurrenderRequired === true && !!determination?.allowancesSurrenderDate) ||
      determination?.allowancesSurrenderRequired === false)
  );
}

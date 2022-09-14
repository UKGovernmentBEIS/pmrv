import { PermitContainer, PermitIssuanceGrantDetermination, PermitIssuanceRejectDetermination } from 'pmrv-api';

export function isWizardComplete(determination: any, permitType?: PermitContainer['permitType']): boolean {
  if (determination?.type === 'GRANTED') {
    const grantDetermination = determination as PermitIssuanceGrantDetermination;
    return (
      !!grantDetermination.reason &&
      !!grantDetermination.activationDate &&
      (permitType === 'GHGE' || (permitType === 'HSE' && !!grantDetermination.annualEmissionsTargets))
    );
  } else if (determination?.type === 'REJECTED') {
    const rejectDetermination = determination as PermitIssuanceRejectDetermination;
    return !!rejectDetermination.reason && !!rejectDetermination.officialNotice;
  } else if (determination?.type === 'DEEMED_WITHDRAWN') {
    return !!determination.reason;
  }
  return false;
}

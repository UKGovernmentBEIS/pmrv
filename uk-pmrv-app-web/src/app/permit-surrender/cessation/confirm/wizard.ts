import { PermitCessation } from 'pmrv-api';

export const initialStep = 'outcome';

export function isWizardComplete(cessation: PermitCessation, allowancesSurrenderRequired: boolean): boolean {
  return (
    cessation !== undefined &&
    !!cessation?.determinationOutcome &&
    allowancesSurrenderStepsAreValid(cessation, allowancesSurrenderRequired) &&
    cessation?.annualReportableEmissions !== undefined &&
    cessation?.subsistenceFeeRefunded != undefined &&
    cessation?.noticeType != undefined &&
    !!cessation?.notes
  );
}

export function areAllowancesDatePreviousStepsValid(cessation: PermitCessation): boolean {
  return cessation !== undefined && !!cessation?.determinationOutcome;
}

export function areAllowancesNumberPreviousStepsValid(cessation: PermitCessation): boolean {
  return areAllowancesDatePreviousStepsValid(cessation) && !!cessation?.allowancesSurrenderDate;
}

export function areEmissionsPreviousStepsValid(
  cessation: PermitCessation,
  allowancesSurrenderRequired: boolean,
): boolean {
  return (
    cessation !== undefined &&
    !!cessation?.determinationOutcome &&
    allowancesSurrenderStepsAreValid(cessation, allowancesSurrenderRequired)
  );
}

export function areRefundPreviousStepsValid(cessation: PermitCessation, allowancesSurrenderRequired: boolean): boolean {
  return (
    areEmissionsPreviousStepsValid(cessation, allowancesSurrenderRequired) &&
    cessation?.annualReportableEmissions !== undefined
  );
}

export function areNoticePreviousStepsValid(cessation: PermitCessation, allowancesSurrenderRequired: boolean): boolean {
  return (
    areRefundPreviousStepsValid(cessation, allowancesSurrenderRequired) &&
    cessation?.subsistenceFeeRefunded != undefined
  );
}

export function areNotesPreviousStepsValid(cessation: PermitCessation, allowancesSurrenderRequired: boolean): boolean {
  return areNoticePreviousStepsValid(cessation, allowancesSurrenderRequired) && cessation?.noticeType != undefined;
}

function allowancesSurrenderStepsAreValid(cessation: PermitCessation, allowancesSurrenderRequired: boolean) {
  return (
    (allowancesSurrenderRequired === true &&
      !!cessation?.allowancesSurrenderDate &&
      cessation?.numberOfSurrenderAllowances !== undefined) ||
    allowancesSurrenderRequired === false
  );
}

import { PermitSurrender } from 'pmrv-api';

export function allStepsAreValid(permitSurrender: PermitSurrender): boolean {
  return (
    stopDateStepIsValid(permitSurrender) &&
    justificationStepIsValid(permitSurrender) &&
    documentsExistStepIsValid(permitSurrender) &&
    documentsStepIsValid(permitSurrender)
  );
}

export function stopDateStepIsValid(permitSurrender: PermitSurrender): boolean {
  return !!permitSurrender?.stopDate;
}

export function justificationStepIsValid(permitSurrender: PermitSurrender): boolean {
  return !!permitSurrender?.justification;
}

export function documentsExistStepIsValid(permitSurrender: PermitSurrender): boolean {
  return permitSurrender?.documentsExist !== undefined;
}

export function documentsStepIsValid(permitSurrender: PermitSurrender): boolean {
  return (
    permitSurrender?.documentsExist === false ||
    (permitSurrender?.documentsExist === true && permitSurrender?.documents?.length > 0)
  );
}

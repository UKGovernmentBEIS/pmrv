import { PermitCessation } from 'pmrv-api';

export const stepStatus = (
  step: number,
  permitCessation: PermitCessation,
  allowancesSurrenderRequired?: boolean,
): boolean => {
  switch (step) {
    case 0:
      return true;
    case 1:
      return step1(permitCessation);
    case 2:
      return step2(permitCessation, allowancesSurrenderRequired);
    case 3:
      return step3(permitCessation, allowancesSurrenderRequired);
    case 4:
      return step4(permitCessation);
    case 5:
      return step5(permitCessation);
    case 6:
      return step6(permitCessation);
    case 7:
      return step6(permitCessation);
  }
};

export const completed = (permitCessation: PermitCessation): boolean => {
  return (
    step1(permitCessation) &&
    step2(permitCessation) &&
    step3(permitCessation) &&
    step4(permitCessation) &&
    step5(permitCessation) &&
    step6(permitCessation) &&
    step7(permitCessation)
  );
};

const step1 = (permitCessation: PermitCessation): boolean => !!permitCessation?.determinationOutcome;
const step2 = (permitCessation: PermitCessation, allowancesSurrenderRequired?: boolean): boolean =>
  allowancesSurrenderRequired ? !!permitCessation?.allowancesSurrenderDate : !!permitCessation?.determinationOutcome;
const step3 = (permitCessation: PermitCessation, allowancesSurrenderRequired?: boolean): boolean =>
  allowancesSurrenderRequired
    ? !!permitCessation?.numberOfSurrenderAllowances
    : !!permitCessation?.determinationOutcome;
const step4 = (permitCessation: PermitCessation): boolean => !!permitCessation?.annualReportableEmissions;
const step5 = (permitCessation: PermitCessation): boolean => permitCessation?.subsistenceFeeRefunded !== undefined;
const step6 = (permitCessation: PermitCessation): boolean => !!permitCessation?.noticeType;
const step7 = (permitCessation: PermitCessation): boolean => !!permitCessation?.notes;

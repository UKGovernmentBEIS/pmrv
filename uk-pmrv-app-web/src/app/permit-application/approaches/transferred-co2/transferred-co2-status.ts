import { TransferredCO2MonitoringApproach } from 'pmrv-api';

import { TaskItemStatus } from '../../../shared/task-list/task-list.interface';
import { PermitApplicationState } from '../../store/permit-application.state';

export function TRANSFERRED_CO2Status(state: PermitApplicationState): TaskItemStatus {
  return TRANSFERRED_CO2_AccountingStatus(state) === 'needs review'
    ? 'needs review'
    : transferredCo2Statuses.every((status) => state.permitSectionsCompleted[status]?.[0])
    ? 'complete'
    : transferredCo2Statuses.some((status) => state.permitSectionsCompleted[status]?.[0]) ||
      TRANSFERRED_CO2_InstallationsStatus(state) === 'in progress' ||
      TRANSFERRED_CO2_AccountingStatus(state) === 'in progress'
    ? 'in progress'
    : 'not started';
}

export function TRANSFERRED_CO2_InstallationsStatus(state: PermitApplicationState): TaskItemStatus {
  return state.permitSectionsCompleted.TRANSFERRED_CO2_Installations?.[0]
    ? 'complete'
    : state.permit.monitoringApproaches?.TRANSFERRED_CO2?.['receivingTransferringInstallations']?.length > 0
    ? 'in progress'
    : 'not started';
}

export function TRANSFERRED_CO2_AccountingStatus(state: PermitApplicationState): TaskItemStatus {
  return state.permitSectionsCompleted.TRANSFERRED_CO2_Accounting?.[0]
    ? isTRANSFERRED_CO2_AccountingValidForComplete(state)
      ? 'complete'
      : 'needs review'
    : (state.permit.monitoringApproaches?.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach)?.accountingEmissions
    ? isTRANSFERRED_CO2_AccountingValid(state)
      ? 'in progress'
      : 'needs review'
    : state.permit.measurementDevicesOrMethods && state.permitSectionsCompleted.measurementDevicesOrMethods?.[0]
    ? 'not started'
    : 'cannot start yet';
}

/** Returns true if accounting emissions exist with no 'chemically bound' or if yes and measurement devices are in correct place */
function isTRANSFERRED_CO2_AccountingValidForComplete(state: PermitApplicationState): boolean {
  const emissions = (state.permit.monitoringApproaches.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach)
    .accountingEmissions;
  return (
    emissions.chemicallyBound ||
    (emissions.accountingEmissionsDetails &&
      state.permit.measurementDevicesOrMethods &&
      emissions.accountingEmissionsDetails.measurementDevicesOrMethods.every((id) =>
        state.permit.measurementDevicesOrMethods.map((device) => device.id).includes(id),
      ))
  );
}

/** Returns true if accounting emissions exist with no 'chemically bound' or if yes and measurement devices exist or emission details undefined */
function isTRANSFERRED_CO2_AccountingValid(state: PermitApplicationState): boolean {
  const emissions = (state.permit.monitoringApproaches.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach)
    .accountingEmissions;
  return (
    emissions.chemicallyBound ||
    (!emissions.chemicallyBound && !emissions.accountingEmissionsDetails) ||
    (emissions.accountingEmissionsDetails && (state.permit.measurementDevicesOrMethods?.length > 0 ?? false))
  );
}

export const transferredCo2Statuses = [
  'TRANSFERRED_CO2_Installations',
  'TRANSFERRED_CO2_Accounting',
  'TRANSFERRED_CO2_Temperature',
  'TRANSFERRED_CO2_Deductions',
  'TRANSFERRED_CO2_Leakage',
  'TRANSFERRED_CO2_Transfer',
  'TRANSFERRED_CO2_Quantification',
  'TRANSFERRED_CO2_Description',
] as const;

export type TransferredCo2Status = typeof transferredCo2Statuses[number];

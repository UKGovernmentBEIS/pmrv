import { PermitMonitoringApproachSection } from 'pmrv-api';

export const monitoringApproachTypeOptions: PermitMonitoringApproachSection['type'][] = [
  'CALCULATION',
  'MEASUREMENT',
  'FALLBACK',
  'N2O',
  'PFC',
  'INHERENT_CO2',
  'TRANSFERRED_CO2',
];

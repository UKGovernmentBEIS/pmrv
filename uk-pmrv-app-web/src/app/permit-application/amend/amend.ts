import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

const amendSectionToReviewGroupsMap = new Map<
  string,
  PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'][]
>([
  ['permit-type', ['PERMIT_TYPE']],
  ['details', ['INSTALLATION_DETAILS']],
  ['fuels', ['FUELS_AND_EQUIPMENT']],
  [
    'monitoring-approaches',
    [
      'DEFINE_MONITORING_APPROACHES',
      'CALCULATION',
      'MEASUREMENT',
      'FALLBACK',
      'N2O',
      'PFC',
      'INHERENT_CO2',
      'TRANSFERRED_CO2',
      'UNCERTAINTY_ANALYSIS',
    ],
  ],
  ['management-procedures', ['MANAGEMENT_PROCEDURES']],
  ['monitoring-methodology-plan', ['MONITORING_METHODOLOGY_PLAN']],
  ['additional-info', ['ADDITIONAL_INFORMATION']],
  ['confidentiality', ['CONFIDENTIALITY_STATEMENT']],
]);

export const amendTaskStatusKeys = [
  'AMEND_permit_type',
  'AMEND_details',
  'AMEND_fuels',
  'AMEND_monitoring_approaches',
  'AMEND_management_procedures',
  'AMEND_monitoring_methodology_plan',
  'AMEND_additional_info',
  'AMEND_confidentiality',
];

export type AmendTaskStatuses = typeof amendTaskStatusKeys[number];

export function findReviewGroupsBySection(section: string): string[] {
  return amendSectionToReviewGroupsMap.get(section);
}

export function findAmendSectionByReviewGroup(
  reviewGroup: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'],
) {
  return [...amendSectionToReviewGroupsMap.keys()].find((key) =>
    amendSectionToReviewGroupsMap.get(key).includes(reviewGroup),
  );
}

export function getAmendTaskStatusKey(section: string): AmendTaskStatuses[number] {
  return 'AMEND_'.concat(section.split('-').join('_'));
}

export const amendTaskHeading: Record<keyof typeof amendSectionToReviewGroupsMap.keys, string> = {
  'permit-type': 'permit type',
  details: 'installation details',
  fuels: 'fuels and equipment inventory',
  'monitoring-approaches': 'monitoring approaches',
  'management-procedures': 'management procedures',
  'monitoring-methodology-plan': 'monitoring methodology plan',
  'additional-info': 'additional information',
  confidentiality: 'confidentiality statement',
};

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { TaskKey } from '../shared/types/permit-task.type';
import { PermitApplicationState } from '../store/permit-application.state';

export type ReviewGroupStatus = 'undecided' | 'accepted' | 'rejected' | 'operator to amend' | 'needs review';
export type ReviewDeterminationStatus = 'undecided' | 'granted' | 'rejected' | 'deemed withdrawn';

export function resolveDeterminationStatus(state: PermitApplicationState): ReviewDeterminationStatus {
  const reviewGroups = mandatoryGroups.concat(
    Object.keys(state.permit.monitoringApproaches ?? {}) as Array<
      PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group']
    >,
  );

  const areAllReviewGroupsCompleted = reviewGroups.every((mg) => state.reviewSectionsCompleted[mg]);

  return state?.reviewSectionsCompleted?.determination
    ? state?.determination?.type !== 'DEEMED_WITHDRAWN'
      ? areAllReviewGroupsCompleted
        ? state?.determination?.type === 'GRANTED'
          ? 'granted'
          : 'rejected'
        : 'undecided'
      : 'deemed withdrawn'
    : 'undecided';
}

export const mandatoryGroups: Array<PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group']> = [
  'PERMIT_TYPE',
  'CONFIDENTIALITY_STATEMENT',
  'FUELS_AND_EQUIPMENT',
  'INSTALLATION_DETAILS',
  'MANAGEMENT_PROCEDURES',
  'MONITORING_METHODOLOGY_PLAN',
  'ADDITIONAL_INFORMATION',
  'DEFINE_MONITORING_APPROACHES',
  'UNCERTAINTY_ANALYSIS',
];

export const reviewGroupsTasks: {
  [key in PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group']]: string[];
} = {
  PERMIT_TYPE: ['permitType'],
  INSTALLATION_DETAILS: [
    'environmentalPermitsAndLicences',
    'installationDescription',
    'regulatedActivities',
    'estimatedAnnualEmissions',
  ],
  FUELS_AND_EQUIPMENT: [
    'sourceStreams',
    'emissionSources',
    'emissionPoints',
    'emissionSummaries',
    'measurementDevicesOrMethods',
    'siteDiagrams',
  ],
  DEFINE_MONITORING_APPROACHES: ['monitoringApproachesPrepare', 'monitoringApproaches'],
  CALCULATION: ['CALCULATION'],
  MEASUREMENT: ['MEASUREMENT'],
  FALLBACK: ['FALLBACK'],
  N2O: ['N2O'],
  PFC: ['PFC'],
  INHERENT_CO2: ['INHERENT_CO2'],
  TRANSFERRED_CO2: ['TRANSFERRED_CO2'],
  MANAGEMENT_PROCEDURES: [
    'managementProceduresExist',
    'monitoringReporting',
    'assignmentOfResponsibilities',
    'monitoringPlanAppropriateness',
    'dataFlowActivities',
    'qaDataFlowActivities',
    'reviewAndValidationOfData',
    'assessAndControlRisk',
    'qaMeteringAndMeasuringEquipment',
    'correctionsAndCorrectiveActions',
    'controlOfOutsourcedActivities',
    'recordKeepingAndDocumentation',
    'environmentalManagementSystem',
  ],
  MONITORING_METHODOLOGY_PLAN: ['monitoringMethodologyPlans'],
  ADDITIONAL_INFORMATION: ['abbreviations', 'additionalDocuments'],
  CONFIDENTIALITY_STATEMENT: ['confidentialityStatement'],
  UNCERTAINTY_ANALYSIS: ['uncertaintyAnalysis'],
};

export function findReviewGroupByTaskKey(taskKey: TaskKey): string {
  const key = taskKey.includes('.') ? taskKey.split('.').slice(1, 2).join('.') : taskKey;
  return Object.keys(reviewGroupsTasks).find((reviewGroup) => reviewGroupsTasks[reviewGroup].includes(key));
}

export function findTasksByReviewGroupName(
  reviewGroupName: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'],
): string[] {
  return reviewGroupsTasks[reviewGroupName];
}

export function isValidForAmends(state: PermitApplicationState): boolean {
  return Object.keys(state?.reviewGroupDecisions ?? []).some(
    (key) => state.reviewGroupDecisions[key].type === 'OPERATOR_AMENDS_NEEDED' && state.reviewSectionsCompleted[key],
  );
}

export const reviewGroupHeading: Record<
  PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'],
  string
> = {
  PERMIT_TYPE: 'Permit type',
  INSTALLATION_DETAILS: 'Installation details',
  FUELS_AND_EQUIPMENT: 'Fuels and equipment',
  DEFINE_MONITORING_APPROACHES: 'Define monitoring approaches',
  CALCULATION: 'Calculation approach',
  MEASUREMENT: 'Measurement approach',
  FALLBACK: 'Fall-back approach',
  N2O: 'Nitrous oxide (N2O) approach',
  PFC: 'Perfluorocarbons PFC approach',
  INHERENT_CO2: 'Inherent CO2',
  TRANSFERRED_CO2: 'Transferred CO2',
  UNCERTAINTY_ANALYSIS: 'Uncertainty analysis',
  MANAGEMENT_PROCEDURES: 'Management procedures',
  MONITORING_METHODOLOGY_PLAN: 'Monitoring methodology',
  ADDITIONAL_INFORMATION: 'Additional information',
  CONFIDENTIALITY_STATEMENT: 'Confidentiality',
};

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { findAmendSectionByReviewGroup, getAmendTaskStatusKey } from '../../amend/amend';
import { PermitApplicationState } from '../../store/permit-application.state';

export const getAvailableSections = (state: PermitApplicationState): string[] => {
  return [
    ...getTasks(state),
    ...Object.keys(state.permit?.monitoringApproaches ?? {}),
    ...(state.requestTaskType === 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT'
      ? (
          Object.keys((state as PermitApplicationState).reviewGroupDecisions) as Array<
            PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group']
          >
        ).map((key) => getAmendTaskStatusKey(findAmendSectionByReviewGroup(key)))
      : []),
  ];
};

const getTasks = (state: PermitApplicationState): string[] => {
  let tasks = [
    'abbreviations',
    'additionalDocuments',
    'confidentialityStatement',
    'emissionPoints',
    'emissionSources',
    'emissionSummaries',
    'environmentalPermitsAndLicences',
    'estimatedAnnualEmissions',
    'installationDescription',
    'managementProceduresExist',
    'measurementDevicesOrMethods',
    'monitoringApproaches',
    'monitoringMethodologyPlans',
    'regulatedActivities',
    'siteDiagrams',
    'sourceStreams',
    'monitoringApproachesPrepare',
    'uncertaintyAnalysis',
  ];

  if (state.permit.managementProceduresExist) {
    const managementProceduresTasks = [
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
    ];

    tasks = tasks.concat(managementProceduresTasks);
  }

  return tasks;
};

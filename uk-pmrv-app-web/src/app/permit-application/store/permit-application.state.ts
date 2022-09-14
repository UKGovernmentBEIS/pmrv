import {
  DecisionNotification,
  PermitIssuanceApplicationReviewRequestTaskPayload,
  PermitVariationDetails,
  PermitVariationReviewDecision,
  RequestActionDTO,
  RequestActionUserInfo,
  RequestInfoDTO,
  RequestTaskDTO,
  RequestTaskItemDTO,
} from 'pmrv-api';

export interface PermitApplicationState extends PermitIssuanceApplicationReviewRequestTaskPayload {
  requestTaskId?: number;
  actionId?: number;
  requestId?: string;
  requestTaskType: RequestTaskDTO['type'];
  requestActionType: RequestActionDTO['type'];
  allowedRequestTaskActions?: RequestTaskItemDTO['allowedRequestTaskActions'];
  isRequestTask: boolean; //true if request task, false if request action
  daysRemaining?: number;
  creationDate?: string;

  competentAuthority: RequestInfoDTO['competentAuthority'];
  accountId: RequestInfoDTO['accountId'];

  isEditable: boolean;

  assignee: Pick<RequestTaskDTO, 'assigneeUserId' | 'assigneeFullName'>;
  userAssignCapable: boolean;
  assignable?: boolean;

  // permit determination/decision
  reviewGroupDecisions?: {
    [key: string]: any; // PermitIssuanceReviewDecision | PermitVariationReviewDecision
  };
  determination?: any; // PermitIssuanceGrantDetermination | PermitIssuanceRejectDetermination | PermitIssuanceDeemedWithdrawnDetermination
  permitDecisionNotification?: DecisionNotification;
  usersInfo?: { [key: string]: RequestActionUserInfo };

  userViewRole?: 'OPERATOR' | 'REGULATOR' | 'VERIFIER'; //TODO remove me from here, not part of permit application state

  // Permit variation
  isVariation: boolean;
  permitVariationDetails?: PermitVariationDetails;
  permitVariationDetailsCompleted?: boolean;
  permitVariationDetailsReviewDecision?: PermitVariationReviewDecision;
  permitVariationDetailsReviewCompleted?: boolean;

  returnUrl?: string;
}

export const initialState: PermitApplicationState = {
  installationOperatorDetails: undefined,
  assignee: undefined,
  competentAuthority: undefined,
  accountId: undefined,
  daysRemaining: undefined,
  permit: {
    abbreviations: undefined,
    additionalDocuments: undefined,
    assessAndControlRisk: undefined,
    assignmentOfResponsibilities: undefined,
    confidentialityStatement: undefined,
    controlOfOutsourcedActivities: undefined,
    correctionsAndCorrectiveActions: undefined,
    dataFlowActivities: undefined,
    emissionPoints: [],
    emissionSources: [],
    emissionSummaries: [],
    environmentalManagementSystem: undefined,
    environmentalPermitsAndLicences: undefined,
    estimatedAnnualEmissions: undefined,
    installationDescription: undefined,
    managementProceduresExist: undefined,
    measurementDevicesOrMethods: [],
    monitoringApproaches: undefined,
    monitoringMethodologyPlans: undefined,
    monitoringPlanAppropriateness: undefined,
    monitoringReporting: undefined,
    qaDataFlowActivities: undefined,
    qaMeteringAndMeasuringEquipment: undefined,
    recordKeepingAndDocumentation: undefined,
    regulatedActivities: [],
    reviewAndValidationOfData: undefined,
    siteDiagrams: undefined,
    sourceStreams: [],
    uncertaintyAnalysis: undefined,
  },
  permitSectionsCompleted: {},
  reviewSectionsCompleted: {},
  permitAttachments: {},
  permitType: undefined,
  userAssignCapable: undefined,
  isEditable: undefined,
  requestTaskType: undefined,
  requestActionType: undefined,
  isRequestTask: undefined,
  isVariation: undefined,
  returnUrl: undefined,
};

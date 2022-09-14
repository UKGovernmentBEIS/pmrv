import { PermitRouteData } from '../permit-route.interface';

export interface ManagementProceduresDefinitionData extends PermitRouteData {
  permitTask:
    | 'assignmentOfResponsibilities'
    | 'monitoringPlanAppropriateness'
    | 'reviewAndValidationOfData'
    | 'controlOfOutsourcedActivities'
    | 'qaDataFlowActivities'
    | 'qaMeteringAndMeasuringEquipment'
    | 'assessAndControlRisk'
    | 'correctionsAndCorrectiveActions'
    | 'recordKeepingAndDocumentation'
    | 'dataFlowActivities';
}

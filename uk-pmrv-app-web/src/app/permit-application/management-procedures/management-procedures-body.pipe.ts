import { Pipe, PipeTransform } from '@angular/core';

import { Permit } from 'pmrv-api';

@Pipe({ name: 'managementProceduresBody' })
export class ManagementProceduresBodyPipe implements PipeTransform {
  transform(value: keyof Permit): string {
    switch (value) {
      case 'assignmentOfResponsibilities':
        return 'Provide details of how the organisation assigns monitoring and reporting responsibilities. Include review processes and any training provided.';
      case 'monitoringPlanAppropriateness':
        return 'Provide details of how the organisation evaluates the appropriateness of the monitoring plan. Include any potential measures to improve the monitoring plan.';
      case 'dataFlowActivities':
        return 'Provide details about the procedures used to manage data flow activities.';
      case 'reviewAndValidationOfData':
        return 'Provide details about the procedures for ensuring the data is regularly reviewed and validated internally.';
      case 'assessAndControlRisk':
        return 'Provide details of how the organisation assesses and controls inherent risks when establishing an effective control system.';
      case 'controlOfOutsourcedActivities':
        return 'Provide details about the procedures used to control the outsourced data flow and control activities. Include how the organization reviews the quality of the resulting data.';
      case 'qaMeteringAndMeasuringEquipment':
        return "Provide details about how the metering and measurement equipment is calibrated and regularly checked. Include details about what the organisation does if the equipment doesn't meet the required performance levels.";
      case 'correctionsAndCorrectiveActions':
        return 'Provide details about what actions are undertaken if data flow activities and control activities are found to not function effectively.';
      case 'recordKeepingAndDocumentation':
        return "Provide details about the organisation's document retention process, especially for data and information. Include how the data is stored so that the information can be made available when requested by the regulator or verifier.";
      case 'qaDataFlowActivities':
        return 'Provide details about how the information technology is tested and controlled, including access control, back-up, recovery and security.';
      default:
        return '';
    }
  }
}

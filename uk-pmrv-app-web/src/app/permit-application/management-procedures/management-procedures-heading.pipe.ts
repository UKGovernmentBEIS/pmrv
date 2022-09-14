import { Pipe, PipeTransform } from '@angular/core';

import { Permit } from 'pmrv-api';

@Pipe({ name: 'managementProceduresHeading' })
export class ManagementProceduresHeadingPipe implements PipeTransform {
  transform(value: keyof Permit): string {
    switch (value) {
      case 'assignmentOfResponsibilities':
        return 'Assignment of responsibilities';
      case 'monitoringPlanAppropriateness':
        return 'Monitoring plan appropriateness';
      case 'dataFlowActivities':
        return 'Data flow activities';
      case 'reviewAndValidationOfData':
        return 'Review and validation of data';
      case 'assessAndControlRisk':
        return 'Assessing and controlling risks';
      case 'controlOfOutsourcedActivities':
        return 'Control of outsourced activities';
      case 'qaMeteringAndMeasuringEquipment':
        return 'Quality assurance of metering and measuring equipment';
      case 'correctionsAndCorrectiveActions':
        return 'Corrections and corrective actions';
      case 'recordKeepingAndDocumentation':
        return 'Record keeping and documentation';
      case 'qaDataFlowActivities':
        return 'Quality assurance of IT used for data flow activities';
      default:
        return '';
    }
  }
}

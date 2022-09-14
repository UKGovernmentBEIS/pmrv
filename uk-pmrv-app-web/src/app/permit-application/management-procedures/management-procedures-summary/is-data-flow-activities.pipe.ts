import { Pipe, PipeTransform } from '@angular/core';

import { DataFlowActivities, ManagementProceduresDefinition, Permit } from 'pmrv-api';

@Pipe({ name: 'isDataFlowActivities' })
export class IsDataFlowActivitiesPipe implements PipeTransform {
  transform(task: ManagementProceduresDefinition | DataFlowActivities, key: keyof Permit): task is DataFlowActivities {
    return key === 'dataFlowActivities';
  }
}

import { Pipe, PipeTransform } from '@angular/core';

import { OtherFactor } from 'pmrv-api';

@Pipe({ name: 'reportingType' })
export class ReportingTypePipe implements PipeTransform {
  transform(type: OtherFactor['reportingType']): string {
    switch (type) {
      case 'EXCEEDED_THRESHOLD_STATED_GHGE_PERMIT':
        return 'Exceeded a threshold stated in the GHGE Permit';
      case 'EXCEEDED_THRESHOLD_STATED_HSE_PERMIT':
        return 'Exceeded a threshold stated in the HSE Permit';
      case 'RENOUNCE_FREE_ALLOCATIONS':
        return 'Renounce free allocations';
      case 'OTHER_ISSUE':
        return 'Some other issue';
      default:
        return '';
    }
  }
}

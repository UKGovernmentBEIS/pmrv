import { Pipe, PipeTransform } from '@angular/core';

import { PermitNotification } from 'pmrv-api';

@Pipe({ name: 'notificationTypeDescription' })
export class NotificationTypeDescriptionPipe implements PipeTransform {
  transform(type: PermitNotification['type']): string {
    switch (type) {
      case 'TEMPORARY_FACTOR':
        return 'For a temporary deviation from your current monitoring plan.  For example, a broken meter makes it impossible to report emissions as specified in your plan';
      case 'TEMPORARY_CHANGE':
        return 'For a temporary change not directly related to the contents of your monitoring plan. For example, wanting to trial a new fuel or temporary generator over a short term period';
      case 'TEMPORARY_SUSPENSION':
        return '';
      case 'NON_SIGNIFICANT_CHANGE':
        return 'For non-significant changes to the monitoring plan or monitoring methodology plan - those which do not change what and how you monitor or report your emissions.  For example, the name of a meter or procedure has changed';
      case 'OTHER_FACTOR':
        return 'For example, <br/> - GHGE low emitter permit holder who exceeds a threshold <br/> - HSE permit holder who exceeds a threshold <br/> - renounce free allocations <br/> - any other issue not covered in one of the other categories listed above <br/> ';
      default:
        return '';
    }
  }
}

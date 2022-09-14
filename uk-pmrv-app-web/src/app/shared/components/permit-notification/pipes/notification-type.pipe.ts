import { Pipe, PipeTransform } from '@angular/core';

import { PermitNotification } from 'pmrv-api';

@Pipe({ name: 'notificationType' })
export class NotificationTypePipe implements PipeTransform {
  transform(type: PermitNotification['type']): string {
    switch (type) {
      case 'TEMPORARY_FACTOR':
        return 'Temporary factor preventing compliance with a permit condition';
      case 'TEMPORARY_CHANGE':
        return 'Temporary change to the permitted installation';
      case 'TEMPORARY_SUSPENSION':
        return 'Temporary suspension of a regulated activity';
      case 'NON_SIGNIFICANT_CHANGE':
        return 'Non-significant change';
      case 'OTHER_FACTOR':
        return 'Some other factor';
      default:
        return '';
    }
  }
}

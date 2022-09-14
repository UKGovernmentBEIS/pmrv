import { Pipe, PipeTransform } from '@angular/core';

import { RequestDetailsDTO } from 'pmrv-api';

@Pipe({ name: 'workflowType' })
export class WorkflowTypePipe implements PipeTransform {
  transform(type: RequestDetailsDTO['requestType']): string {
    switch (type) {
      case 'AER':
        return 'AER';
      case 'INSTALLATION_ACCOUNT_OPENING':
        return 'Account creation';
      case 'PERMIT_ISSUANCE':
        return 'Permit Application';
      case 'PERMIT_NOTIFICATION':
        return 'Permit Notification';
      case 'PERMIT_NOTIFICATION_FOLLOW_UP':
        return 'Notification follow-up';
      case 'PERMIT_REVOCATION':
        return 'Permit Revocation';
      case 'PERMIT_SURRENDER':
        return 'Permit Surrender';
      case 'PERMIT_TRANSFER':
        return 'Permit Transfer';
      case 'PERMIT_VARIATION':
        return 'Permit Variation';
      case 'SYSTEM_MESSAGE_NOTIFICATION':
        return 'System Message Notification';
      default:
        return null;
    }
  }
}

import { Pipe, PipeTransform } from '@angular/core';

import { ItemDTO } from 'pmrv-api';

@Pipe({ name: 'itemType' })
export class ItemTypePipe implements PipeTransform {
  transform(value: ItemDTO['requestType']): string {
    switch (value) {
      case 'INSTALLATION_ACCOUNT_OPENING':
        return 'Installation account';
      case 'SYSTEM_MESSAGE_NOTIFICATION':
        return 'Message';
      case 'PERMIT_REVOCATION':
      case 'PERMIT_ISSUANCE':
      case 'PERMIT_SURRENDER':
      case 'PERMIT_NOTIFICATION':
      case 'PERMIT_VARIATION':
        return 'Permit';
      default:
        return null;
    }
  }
}

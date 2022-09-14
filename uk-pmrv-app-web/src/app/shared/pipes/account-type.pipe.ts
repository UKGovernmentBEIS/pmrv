import { Pipe, PipeTransform } from '@angular/core';

import { AccountDetailsDTO } from 'pmrv-api';

@Pipe({ name: 'accountType' })
export class AccountTypePipe implements PipeTransform {
  transform(type: AccountDetailsDTO['accountType']): string {
    switch (type) {
      case 'INSTALLATION':
        return 'Installation';
      case 'AVIATION':
        return 'Aviation';
      default:
        return null;
    }
  }
}

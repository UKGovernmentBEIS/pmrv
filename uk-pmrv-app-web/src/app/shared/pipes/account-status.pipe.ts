import { Pipe, PipeTransform } from '@angular/core';

import { AccountDetailsDTO } from 'pmrv-api';

@Pipe({ name: 'accountStatus' })
export class AccountStatusPipe implements PipeTransform {
  transform(status?: AccountDetailsDTO['status']): string {
    if (!status) {
      return null;
    }

    switch (status) {
      case 'AWAITING_RATIONALISATION':
        return 'Awaiting Rationalisation';
      case 'AWAITING_REVOCATION':
        return 'Awaiting Revocation';
      case 'AWAITING_SURRENDER':
        return 'Awaiting surrender';
      case 'AWAITING_TRANSFER':
        return 'Awaiting transfer';
      case 'CEASED_OPERATIONS':
        return 'Ceased operations';
      case 'COMMISSION_LIST':
        return 'Commission list';
      case 'DEEMED_WITHDRAWN':
        return 'Deemed Withdrawn';
      case 'DENIED':
        return 'Denied';
      case 'EXEMPT':
        return 'Exempt';
      case 'EXEMPT_COMMERCIAL':
        return 'Exempt commercial';
      case 'EXEMPT_NON_COMMERCIAL':
        return 'Exempt non commercial';
      case 'LIVE':
        return 'Live';
      case 'NEW':
        return 'New';
      case 'PERMIT_REFUSED':
        return 'Permit refused';
      case 'PRIOR_COMPLIANCE_LIST':
        return 'Prior compliance list';
      case 'RATIONALISED':
        return 'Rationalised';
      case 'REMOVED_FROM_COMMISSION_LIST':
        return 'Removed from commission list';
      case 'REVOKED':
        return 'Revoked';
      case 'SURRENDERED':
        return 'Surrendered';
      case 'TRANSFERRED':
        return 'Transferred';
      case 'UNAPPROVED':
        return 'Unapproved';

      default:
        return null;
    }
  }
}

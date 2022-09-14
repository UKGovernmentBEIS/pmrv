import { AccountDetailsDTO } from 'pmrv-api';

export function accountFinalStatuses(status: AccountDetailsDTO['status']): boolean {
  return status !== 'UNAPPROVED' && status !== 'DENIED';
}

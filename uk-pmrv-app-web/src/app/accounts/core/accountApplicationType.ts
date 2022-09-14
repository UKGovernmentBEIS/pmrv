import { AccountDetailsDTO } from 'pmrv-api';

export const applicationTypeMap: Partial<Record<AccountDetailsDTO['applicationType'], string>> = {
  NEW_PERMIT: 'New Permit',
  TRANSFER: 'Transfer',
};

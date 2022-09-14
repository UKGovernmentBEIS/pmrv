import { MiReportResult, MiReportSearchResult } from 'pmrv-api';

export const pageSize = 20;

export const miReportTypeDescriptionMap: Record<MiReportSearchResult['miReportType'], string> = {
  LIST_OF_ACCOUNTS_USERS_CONTACTS: 'List of Accounts, Users and Contacts',
  LIST_OF_ACCOUNTS_REGULATORS: 'List of Accounts and assigned Regulator site contacts',
  LIST_OF_VERIFICATION_BODIES_AND_USERS: 'List of Verification bodies and Users',
  LIST_OF_ACCOUNTS: 'List of Accounts',
  COMPLETED_WORK: 'Completed work',
};

export const miReportTypeLinkMap: Partial<Record<MiReportResult['reportType'], string[]>> = {
  LIST_OF_ACCOUNTS_USERS_CONTACTS: ['./', 'accounts-users-contacts'],
  COMPLETED_WORK: ['./', 'completed-work'],
};

export function createTablePage(currentPage: number, pageSize: number, data: any[]): any[] {
  const firstIndex = (currentPage - 1) * pageSize;
  const lastIndex = Math.min(firstIndex + pageSize, data.length);
  return data.length > firstIndex ? data.slice(firstIndex, lastIndex) : [];
}
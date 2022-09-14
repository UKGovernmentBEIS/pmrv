import { AccountsUsersContactsMiReportResult, ExecutedRequestActionsMiReportResult } from 'pmrv-api';

export const mockAccountsUsersContactsMiReportResult: AccountsUsersContactsMiReportResult = {
  reportType: 'LIST_OF_ACCOUNTS_USERS_CONTACTS',
  payload: [
    {
      accountType: 'INSTALLATION',
      accountId: 1,
      accountName: 'Installation name',
      accountStatus: 'NEW',
      legalEntityName: 'Legal entity',
      isPrimaryContact: true,
      isSecondaryContact: false,
      isFinancialContact: true,
      isServiceContact: true,
      authorityStatus: 'ACTIVE',
      name: 'Obi Wan Kenobi',
      telephone: '+442345254566656565',
      email: 'owk@mail.com',
      role: 'Operator admin',
    },
    {
      accountType: 'INSTALLATION',
      accountId: 31,
      accountName: 'Installation name 2',
      accountStatus: 'LIVE',
      legalEntityName: 'Legal entity 2',
      isPrimaryContact: true,
      isSecondaryContact: false,
      isFinancialContact: true,
      isServiceContact: false,
      authorityStatus: 'ACTIVE',
      name: 'Darth Vader',
      telephone: '+442345254566656562',
      email: 'dv@mail.gr',
      role: 'Operator admin',
    },
  ],
};

export const mockExecutedRequestActionMiReportResult: ExecutedRequestActionsMiReportResult = {
  reportType: 'COMPLETED_WORK',
  actions: [
    {
      accountType: 'INSTALLATION',
      accountId: 1,
      accountName: 'Installation name',
      accountStatus: 'NEW',
      legalEntityName: 'Legal entity Name',
      permitId: 'UK-W-15',
      requestId: 'REQ-123',
      requestStatus: 'IN_PROGRESS',
      requestType: 'PERMIT_ISSUANCE',
      requestActionCompletionDate: '2022-08-12',
      requestActionSubmitter: 'Teo James',
      requestActionType: 'PERMIT_ISSUANCE_APPLICATION_GRANTED',
    },
  ],
};

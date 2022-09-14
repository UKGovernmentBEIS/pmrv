import { PermitCessation } from 'pmrv-api';

export type SectionStatus = 'not started' | 'in progress' | 'complete';

export const OfficialNoticeTypeMap: Record<PermitCessation['noticeType'], string> = {
  SATISFIED_WITH_REQUIREMENTS_COMPLIANCE:
    'Satisfied that the requirements set out in the schedule have been compiled with',
  NO_PROSPECT_OF_FURTHER_ALLOWANCES: 'No prospect of further being surrendered',
};

import { InstallationAccountOpeningSubmitApplicationCreateActionPayload } from 'pmrv-api';

export type CompetentAuthority = Exclude<
  InstallationAccountOpeningSubmitApplicationCreateActionPayload['competentAuthority'],
  'OPRED'
>;

export const competentAuthorityMap: Record<CompetentAuthority, string> = {
  ENGLAND: 'England',
  WALES: 'Wales',
  SCOTLAND: 'Scotland',
  NORTHERN_IRELAND: 'Northern Ireland',
};

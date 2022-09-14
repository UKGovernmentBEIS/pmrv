import { InstallationAccountOpeningSubmitApplicationCreateActionPayload } from 'pmrv-api';

export interface EtsScheme {
  etsSchemeType: Exclude<
    InstallationAccountOpeningSubmitApplicationCreateActionPayload['emissionTradingScheme'],
    'UK_ETS_AVIATION' | 'CORSIA'
  >;
}

export const etsSchemeMap: Record<EtsScheme['etsSchemeType'], string> = {
  UK_ETS_INSTALLATIONS: 'UK ETS',
  EU_ETS_INSTALLATIONS: 'EU ETS',
};

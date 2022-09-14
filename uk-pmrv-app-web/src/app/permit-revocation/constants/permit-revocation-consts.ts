import { PermitRevocation } from 'pmrv-api';

export const permitRevocationMapper: Record<
  keyof PermitRevocation,
  { label: string; url: string; step: number; prefix?: string; error?: string }
> = {
  reason: {
    label: 'Reason for revoking permit',
    url: 'reason',
    step: 1,
  },
  stoppedDate: {
    label: 'Have the regulated activities at the installation stopped?',
    url: 'stop-date',
    step: 2,
  },
  activitiesStopped: {
    label: 'Have the regulated activities at the installation stopped?',
    url: 'stop-date',
    step: 2,
  },
  effectiveDate: {
    label: 'Effective date of notice',
    url: 'notice',
    error: 'invalidEffectiveDate',
    step: 3,
  },
  annualEmissionsReportRequired: {
    label: 'Annual emissions of monitoring revocation report required',
    url: 'report',
    prefix: 'Required by',
    step: 4,
  },
  annualEmissionsReportDate: {
    label: 'Annual emissions of monitoring revocation report required',
    url: 'report',
    prefix: 'Required by',
    step: 4,
  },
  surrenderRequired: {
    label: 'Surrender of allowances required',
    url: 'surrender-allowances',
    prefix: 'Required by',
    step: 5,
  },
  surrenderDate: {
    label: 'Surrender of allowances required',
    url: 'surrender-allowances',
    prefix: 'Required by',
    step: 5,
  },
  feeCharged: {
    label: 'Operator fee required',
    url: 'fee',
    error: 'invalidFeeDate',
    step: 6,
  },
  feeDate: {
    label: 'Operator fee required',
    url: 'fee',
    error: 'invalidFeeDate',
    step: 6,
  },
  feeDetails: {
    label: 'Operator fee required',
    url: 'fee',
    error: 'invalidFeeDate',
    step: 6,
  },
};

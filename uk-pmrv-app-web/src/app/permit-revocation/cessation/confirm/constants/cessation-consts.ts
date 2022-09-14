import { PermitCessation } from 'pmrv-api';

export const cessationMapper: Record<
  keyof PermitCessation,
  { label: string; url: string; step: number; prefix?: string; error?: string }
> = {
  determinationOutcome: {
    label: 'What was the outcome of the emission report determination?',
    url: 'outcome',
    step: 1,
  },
  allowancesSurrenderDate: {
    label: 'When were the installation allowances surrendered?',
    url: 'allowances-date',
    step: 2,
  },
  numberOfSurrenderAllowances: {
    label: 'How many allowances were surrendered by the installation?',
    url: 'allowances-number',
    step: 3,
  },
  annualReportableEmissions: {
    label: 'What is the installations annual reportable emissions?',
    url: 'emissions',
    step: 4,
  },
  subsistenceFeeRefunded: {
    label: 'Should the installations subsistence fee be refunded?',
    url: 'refund',
    prefix: 'Required by',
    step: 5,
  },
  noticeType: {
    label: 'What text should appear in the official notice following the cessation?',
    url: 'notice',
    prefix: 'Required by',
    step: 6,
  },
  notes: {
    label: 'Notes about the cessation',
    url: 'notes',
    prefix: 'Required by',
    step: 7,
  },
};

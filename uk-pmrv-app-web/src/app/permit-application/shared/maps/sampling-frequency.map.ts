import { CalculationAnalysisMethod } from 'pmrv-api';

export const samplingFrequencyMap: Record<CalculationAnalysisMethod['samplingFrequency'], string> = {
  CONTINUOUS: 'Continuous',
  DAILY: 'Daily',
  WEEKLY: 'Weekly',
  MONTHLY: 'Monthly',
  QUARTERLY: 'Quarterly',
  BI_ANNUALLY: 'Bi annually',
  ANNUALLY: 'Annually',
  OTHER: 'Other',
};

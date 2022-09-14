import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'samplingFrequency',
})
export class SamplingFrequencyPipe implements PipeTransform {
  transform(value: unknown): string {
    switch (value) {
      case 'CONTINUOUS':
        return 'Continuous';
      case 'DAILY':
        return 'Daily';
      case 'WEEKLY':
        return 'Weekly';
      case 'MONTHLY':
        return 'Monthly';
      case 'QUARTERLY':
        return 'Quarterly';
      case 'BI_ANNUALLY':
        return 'Biannual';
      case 'ANNUALLY':
        return 'Annual';
      case 'OTHER':
        return 'Other';
      default:
        return null;
    }
  }
}

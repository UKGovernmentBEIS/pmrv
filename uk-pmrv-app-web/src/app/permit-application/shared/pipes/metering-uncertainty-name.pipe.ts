import { Pipe, PipeTransform } from '@angular/core';

import { CalculationActivityData, FallbackSourceStreamCategory } from 'pmrv-api';

@Pipe({ name: 'meteringUncertaintyName' })
export class MeteringUncertaintyNamePipe implements PipeTransform {
  transform(value: FallbackSourceStreamCategory['uncertainty'] | CalculationActivityData['uncertainty']): string {
    switch (value) {
      case 'LESS_OR_EQUAL_1_5':
        return '1.5% or less';
      case 'LESS_OR_EQUAL_2_5':
        return '2.5% or less';
      case 'LESS_OR_EQUAL_5_0':
        return '5% or less';
      case 'LESS_OR_EQUAL_7_5':
        return '7.5% or less';
      case 'LESS_OR_EQUAL_10_0':
        return '10% or less';
      case 'LESS_OR_EQUAL_12_5':
        return '12.5% or less';
      case 'LESS_OR_EQUAL_15_0':
        return '15% or less';
      case 'LESS_OR_EQUAL_17_5':
        return '17.5% or less';
      case 'N_A':
        return 'N/A';
      default:
        return '';
    }
  }
}

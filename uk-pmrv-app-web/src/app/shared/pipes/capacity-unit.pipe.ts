import { Pipe, PipeTransform } from '@angular/core';

import { RegulatedActivity } from 'pmrv-api';

@Pipe({ name: 'capacityUnit' })
export class CapacityUnitPipe implements PipeTransform {
  transform(value: RegulatedActivity['capacityUnit']): string {
    switch (value) {
      case 'MW_TH':
        return 'MW(th)';
      case 'KW_TH':
        return 'kW(th)';
      case 'MVA':
        return 'MVA';
      case 'KVA':
        return 'kVA';
      case 'KW':
        return 'kW';
      case 'MW':
        return 'MW';
      case 'TONNES_PER_DAY':
        return 'tonnes/day';
      case 'TONNES_PER_HOUR':
        return 'tonnes/hour';
      case 'TONNES_PER_ANNUM':
        return 'tonnes/annum';
      case 'KG_PER_DAY':
        return 'kg/day';
      case 'KG_PER_HOUR':
        return 'kg/hour';
      default:
        return '';
    }
  }
}

import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'appliedTier',
})
export class AppliedTierPipe implements PipeTransform {
  transform(value: string): unknown {
    switch (value) {
      case 'NO_TIER':
        return 'No tier';
      case 'TIER_1':
        return 'Tier 1';
      case 'TIER_2':
        return 'Tier 2';
      case 'TIER_2A':
        return 'Tier 2a';
      case 'TIER_2B':
        return 'Tier 2b';
      case 'TIER_3':
        return 'Tier 3';
      case 'TIER_4':
        return 'Tier 4';
      default:
        return null;
    }
  }
}

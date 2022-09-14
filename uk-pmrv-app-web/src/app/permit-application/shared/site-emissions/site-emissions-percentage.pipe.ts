import { Pipe, PipeTransform } from '@angular/core';

import { SiteEmissionItem } from './site-emission-item';

@Pipe({ name: 'siteEmissionPercentage' })
export class SiteEmissionsPercentagePipe implements PipeTransform {
  transform(value: number, total: SiteEmissionItem): number {
    const totalEmissions = total.marginal + total.major + total.minor + total.minimis;
    return totalEmissions && totalEmissions > 0 ? value / totalEmissions : 0;
  }
}

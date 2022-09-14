import { Pipe, PipeTransform } from '@angular/core';

import { etsSchemeMap } from '../../../installation-account-application/ets-scheme/ets-scheme';

@Pipe({
  name: 'etsScheme',
})
export class EtsSchemePipe implements PipeTransform {
  transform(value: string): string {
    return Object.entries(etsSchemeMap).find(([target]) => target === value)?.[1];
  }
}

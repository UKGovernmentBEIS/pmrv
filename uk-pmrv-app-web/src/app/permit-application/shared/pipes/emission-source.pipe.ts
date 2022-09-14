import { Pipe, PipeTransform } from '@angular/core';

import { EmissionSource } from 'pmrv-api';

@Pipe({ name: 'emissionSource' })
export class EmissionSourcePipe implements PipeTransform {
  transform(emissionSources: EmissionSource[], emissionSourceId: string): EmissionSource {
    return emissionSources.find((source) => source.id === emissionSourceId);
  }
}

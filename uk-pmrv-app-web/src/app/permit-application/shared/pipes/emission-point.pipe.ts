import { Pipe, PipeTransform } from '@angular/core';

import { EmissionPoint } from 'pmrv-api';

@Pipe({ name: 'emissionPoint' })
export class EmissionPointPipe implements PipeTransform {
  transform(emissionPoints: EmissionPoint[], emissionPointId: string): EmissionPoint {
    return emissionPoints.find((point) => point.id === emissionPointId);
  }
}

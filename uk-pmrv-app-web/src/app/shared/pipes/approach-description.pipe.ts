import { Pipe, PipeTransform } from '@angular/core';

import { PermitMonitoringApproachSection } from 'pmrv-api';

@Pipe({ name: 'monitoringApproachDescription' })
export class ApproachDescriptionPipe implements PipeTransform {
  transform(value: PermitMonitoringApproachSection['type']): string {
    switch (value) {
      case 'CALCULATION':
        return 'Calculation';
      case 'MEASUREMENT':
        return 'Measurement';
      case 'FALLBACK':
        return 'Fall-back';
      case 'N2O':
        return 'Nitrous oxide (N2O)';
      case 'PFC':
        return 'Perfluorocarbons (PFC)';
      case 'INHERENT_CO2':
        return 'Inherent CO2';
      case 'TRANSFERRED_CO2':
        return 'Transferred CO2';
    }
  }
}

import { Pipe, PipeTransform } from '@angular/core';

import { StatusKey } from '../../../../shared/types/permit-task.type';

@Pipe({
  name: 'subtaskName',
})
export class SubtaskNamePipe implements PipeTransform {
  transform(value: StatusKey): string {
    switch (value) {
      case 'CALCULATION_Calorific':
        return 'Net calorific value';
      case 'CALCULATION_Emission_Factor':
        return 'Emission factor';
      case 'CALCULATION_Oxidation_Factor':
        return 'Oxidation factor';
      case 'CALCULATION_Carbon_Content':
        return 'Carbon content';
      case 'CALCULATION_Conversion_Factor':
        return 'Conversion factor';
      case 'CALCULATION_Biomass_Fraction':
        return 'Biomass fraction';
      default:
        return null;
    }
  }
}

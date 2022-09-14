import { Pipe, PipeTransform } from '@angular/core';

import { TemplateInfoDTO } from 'pmrv-api';

@Pipe({ name: 'operatorType' })
export class OperatorTypePipe implements PipeTransform {
  transform(value: TemplateInfoDTO['operatorType']): string {
    switch (value) {
      case 'INSTALLATION':
        return 'Installations';
      case 'AVIATION':
        return 'Aviations';
      default:
        return null;
    }
  }
}

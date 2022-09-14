import { Pipe, PipeTransform } from '@angular/core';

import { MeasSourceStreamCategory, N2OSourceStreamCategory } from 'pmrv-api';

@Pipe({ name: 'categoryTypeName' })
export class CategoryTypeNamePipe implements PipeTransform {
  transform(value: N2OSourceStreamCategory['categoryType'] | MeasSourceStreamCategory['categoryType']): string {
    switch (value) {
      case 'DE_MINIMIS':
        return 'De-minimis';
      case 'MAJOR':
        return 'Major';
      case 'MARGINAL':
        return 'Marginal';
      case 'MINOR':
        return 'Minor';
      default:
        return '';
    }
  }
}

import { Pipe, PipeTransform } from '@angular/core';

import { competentAuthorityMap } from '../interfaces/competent-authority';

@Pipe({ name: 'competentAuthorityLocation' })
export class CompetentAuthorityLocationPipe implements PipeTransform {
  transform(value: string): string {
    return Object.entries(competentAuthorityMap).find(([target]) => target === value)[1];
  }
}

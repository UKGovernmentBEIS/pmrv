import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'installationCategoryType',
})
export class InstallationCategoryTypePipe implements PipeTransform {
  transform(value: number): string {
    if (value >= 0.1 && value <= 25000) {
      return 'A_LOW_EMITTER';
    } else if (value >= 25000.1 && value <= 50000) {
      return 'A';
    } else if (value >= 50000.1 && value <= 500000) {
      return 'B';
    } else if (value >= 500000.1) {
      return 'C';
    } else {
      return null;
    }
  }
}

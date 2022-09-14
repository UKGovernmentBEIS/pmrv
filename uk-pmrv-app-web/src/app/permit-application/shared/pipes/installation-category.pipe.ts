import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'installationCategory',
})
export class InstallationCategoryPipe implements PipeTransform {
  transform(value: string): string {
    switch (value) {
      case 'A_LOW_EMITTER':
        return 'Category A (Low emitter)';
      case 'A':
        return 'Category A';
      case 'B':
        return 'Category B';
      case 'C':
        return 'Category C';
      case 'N_A':
        return '-';
      default:
        return null;
    }
  }
}

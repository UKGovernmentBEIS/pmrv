import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'determinationType',
})
export class DeterminationTypePipe implements PipeTransform {
  transform(value: string): string {
    switch (value) {
      case 'GRANTED':
        return 'Grant';
      case 'REJECTED':
        return 'Reject';
      case 'DEEMED_WITHDRAWN':
        return 'Deem withdrawn';
      default:
        return null;
    }
  }
}

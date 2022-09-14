import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'secondsToMinutes',
})
export class SecondsToMinutesPipe implements PipeTransform {
  transform(value: number): string {
    if (typeof value === 'number') {
      const minutes = Math.floor(value / 60);
      switch (minutes) {
        case 0:
          return 'less than a minute';
        case 1:
          return '1 minute';
        default:
          return `${minutes} minutes`;
      }
    } else {
      return '';
    }
  }
}

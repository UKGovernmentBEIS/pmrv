import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'includesAny' })
export class IncludesAnyPipe implements PipeTransform {
  transform(value: string[], target: string[]): boolean {
    return value !== null && value.some((item) => target.includes(item));
  }
}

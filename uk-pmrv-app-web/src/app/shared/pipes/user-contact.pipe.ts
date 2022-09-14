import { Injectable, Pipe, PipeTransform } from '@angular/core';

@Injectable({ providedIn: 'root' })
@Pipe({ name: 'userContact' })
export class UserContactPipe implements PipeTransform {
  transform(contact): string {
    switch (contact) {
      case 'PRIMARY':
        return 'Primary contact';
      case 'SECONDARY':
        return 'Secondary contact';
      case 'SERVICE':
        return 'Service contact';
      case 'FINANCIAL':
        return 'Financial contact';
    }
  }
}

import { Injectable, Pipe, PipeTransform } from '@angular/core';

@Injectable({ providedIn: 'root' })
@Pipe({ name: 'userRole' })
export class UserRolePipe implements PipeTransform {
  transform(role): string {
    switch (role) {
      case 'operator_admin':
        return 'Operator admin';
      case 'operator':
        return 'Operator';
      case 'consultant_agent':
        return 'Consultant';
    }
  }
}

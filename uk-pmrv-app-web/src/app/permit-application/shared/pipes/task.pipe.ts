import { Pipe, PipeTransform } from '@angular/core';

import { Observable } from 'rxjs';

import { Permit } from 'pmrv-api';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Pipe({ name: 'task' })
export class TaskPipe implements PipeTransform {
  constructor(private readonly store: PermitApplicationStore) {}

  transform<K extends keyof Permit>(key: K): Observable<Permit[K]> {
    return this.store.getTask(key);
  }
}

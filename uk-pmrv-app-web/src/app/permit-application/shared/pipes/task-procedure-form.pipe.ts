import { Pipe, PipeTransform } from '@angular/core';

import { Observable } from 'rxjs';

import { Permit, ProcedureForm } from 'pmrv-api';

import { PermitApplicationStore } from '../../store/permit-application.store';
import { Path } from '../types/permit-task.type';

@Pipe({ name: 'taskProcedureForm' })
export class TaskProcedureFormPipe implements PipeTransform {
  constructor(private readonly store: PermitApplicationStore) {}

  transform<P extends Path<Permit>>(taskKey: P): Observable<ProcedureForm> {
    return this.store.findTask<ProcedureForm>(taskKey);
  }
}

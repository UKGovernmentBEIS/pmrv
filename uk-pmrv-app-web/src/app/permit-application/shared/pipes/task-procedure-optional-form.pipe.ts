import { Pipe, PipeTransform } from '@angular/core';

import { Observable } from 'rxjs';

import { Permit, ProcedureOptionalForm } from 'pmrv-api';

import { PermitApplicationStore } from '../../store/permit-application.store';
import { Path } from '../types/permit-task.type';

@Pipe({ name: 'taskProcedureOptionalForm' })
export class TaskProcedureOptionalFormPipe implements PipeTransform {
  constructor(private readonly store: PermitApplicationStore) {}

  transform<P extends Path<Permit>>(taskKey: P): Observable<ProcedureOptionalForm> {
    return this.store.findTask<ProcedureOptionalForm>(taskKey);
  }
}

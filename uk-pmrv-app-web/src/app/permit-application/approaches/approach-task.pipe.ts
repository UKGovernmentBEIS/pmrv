import { Pipe, PipeTransform } from '@angular/core';

import { Observable } from 'rxjs';

import { Permit, PermitMonitoringApproachSection } from 'pmrv-api';

import { Path } from '../shared/types/permit-task.type';
import { PermitApplicationStore } from '../store/permit-application.store';

@Pipe({ name: 'monitoringApproachTask' })
export class ApproachTaskPipe implements PipeTransform {
  constructor(private readonly store: PermitApplicationStore) {}

  transform(type: PermitMonitoringApproachSection['type']): Observable<any> {
    return this.store.findTask(`monitoringApproaches.${type}` as Path<Permit>);
  }
}

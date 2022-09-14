import { Pipe, PipeTransform } from '@angular/core';

import { map, Observable } from 'rxjs';

import { TaskItemStatus } from '../../shared/task-list/task-list.interface';
import { PermitRevocationStore } from '../store/permit-revocation-store';
import { resolveApplyStatus, resolveWithDrawStatus } from './section-status';

@Pipe({
  name: 'taskStatus',
})
export class TaskStatusPipe implements PipeTransform {
  constructor(private readonly store: PermitRevocationStore) {}

  transform(key: string): Observable<TaskItemStatus> {
    return this.store.pipe(
      map((state) => {
        switch (key) {
          case 'REVOCATION_APPLY':
            return resolveApplyStatus(state);
          case 'REVOCATION_WITHDRAW':
            return resolveWithDrawStatus(state);
        }
      }),
    );
  }
}

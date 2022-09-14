import { Pipe, PipeTransform } from '@angular/core';

import { map, Observable } from 'rxjs';

import { TaskItemStatus } from '../../shared/task-list/task-list.interface';
import { PermitSurrenderStore } from '../store/permit-surrender.store';
import { StatusKey } from './permit-surrender.type';
import { resolveApplyStatus, resolveSubmitStatus } from './section-status';

@Pipe({
  name: 'taskStatus',
})
export class TaskStatusPipe implements PipeTransform {
  constructor(private readonly store: PermitSurrenderStore) {}

  transform(key: StatusKey): Observable<TaskItemStatus> {
    return this.store.pipe(
      map((state) => {
        switch (key) {
          case 'SURRENDER_APPLY':
            return resolveApplyStatus(state);
          case 'SURRENDER_SUBMIT':
            return resolveSubmitStatus(state);
        }
      }),
    );
  }
}

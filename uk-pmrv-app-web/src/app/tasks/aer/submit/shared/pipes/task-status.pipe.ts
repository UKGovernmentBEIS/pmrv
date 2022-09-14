import { Pipe, PipeTransform } from '@angular/core';

import { map, Observable } from 'rxjs';

import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { AerService } from '@tasks/aer/core/aer.service';
import { StatusKey } from '@tasks/aer/core/aer-task.type';

@Pipe({ name: 'taskStatus' })
export class TaskStatusPipe implements PipeTransform {
  constructor(private readonly aerService: AerService) {}

  transform(key: StatusKey): Observable<TaskItemStatus> {
    return this.aerService.getPayload().pipe(
      map((payload) => {
        switch (key) {
          case 'sourceStreams':
          case 'emissionSources':
          case 'monitoringApproachTypes':
          case 'emissionPoints':
          case 'regulatedActivities':
            return payload.aerSectionsCompleted[key]?.[0]
              ? 'complete'
              : payload.aer[key]?.length > 0
              ? 'in progress'
              : 'not started';
          case 'pollutantRegisterActivities':
            return payload.aerSectionsCompleted[key]?.[0]
              ? 'complete'
              : payload.aer[key]?.exist === false || payload.aer[key]?.activities?.length > 0
              ? 'in progress'
              : 'not started';
          case 'naceCodes':
            return payload.aerSectionsCompleted[key]?.[0]
              ? 'complete'
              : payload.aer[key]?.codes?.length > 0
              ? 'in progress'
              : 'not started';
          default:
            return payload?.aerSectionsCompleted[key]?.[0] ? 'complete' : 'not started';
        }
      }),
    );
  }
}

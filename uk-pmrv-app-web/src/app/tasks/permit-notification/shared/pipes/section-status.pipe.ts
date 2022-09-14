import { Pipe, PipeTransform } from '@angular/core';

import { first, map, Observable } from 'rxjs';

import { TaskItemStatus } from '../../../../shared/task-list/task-list.interface';
import { PermitNotificationService } from '../../core/permit-notification.service';
import {
  resolveDetailsChangeStatus,
  resolveFollowUpAmendsSubmitStatus,
  resolveFollowUpDetailsOfAmendsStatus,
  resolveFollowUpStatus,
  resolveFollowUpSubmitStatus,
  resolveSubmitStatus,
  StatusKey,
} from '../../core/section-status';

@Pipe({
  name: 'sectionStatus',
})
export class SectionStatusPipe implements PipeTransform {
  constructor(private readonly permitNotificationService: PermitNotificationService) {}

  transform(key: StatusKey): Observable<TaskItemStatus> {
    return this.permitNotificationService.getPayload().pipe(
      first(),
      map((state) => {
        switch (key) {
          case 'DETAILS_CHANGE':
            return resolveDetailsChangeStatus(state);
          case 'SUBMIT':
            return resolveSubmitStatus(state);
          case 'FOLLOW_UP':
            return resolveFollowUpStatus(state);
          case 'FOLLOW_UP_SUBMIT':
            return resolveFollowUpSubmitStatus(state);
          case 'FOLLOW_UP_AMENDS':
            return resolveFollowUpDetailsOfAmendsStatus(state);
          case 'FOLLOW_UP_AMENDS_SUBMIT':
            return resolveFollowUpAmendsSubmitStatus(state);
          default:
            return 'not started';
        }
      }),
    );
  }
}

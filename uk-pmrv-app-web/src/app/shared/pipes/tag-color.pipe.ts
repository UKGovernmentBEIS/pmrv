import { Pipe, PipeTransform } from '@angular/core';

import { TagColor } from 'govuk-components';

import { RequestDetailsDTO } from 'pmrv-api';

import { ReviewDeterminationStatus, ReviewGroupStatus } from '../../permit-application/review/review';
import { TaskItemStatus } from '../task-list/task-list.interface';

@Pipe({ name: 'tagColor' })
export class TagColorPipe implements PipeTransform {
  transform(
    status: TaskItemStatus | ReviewGroupStatus | ReviewDeterminationStatus | RequestDetailsDTO['requestStatus'],
  ): TagColor {
    switch (status) {
      case 'not started':
      case 'undecided':
      case 'cannot start yet':
        return 'grey';
      case 'granted':
      case 'accepted':
      case 'COMPLETED':
      case 'APPROVED':
        return 'green';
      case 'operator to amend':
      case 'in progress':
      case 'IN_PROGRESS':
        return 'blue';
      case 'rejected':
      case 'deemed withdrawn':
      case 'incomplete':
      case 'CANCELLED':
      case 'WITHDRAWN':
      case 'REJECTED':
        return 'red';
      case 'needs review':
        return 'yellow';
      case 'complete':
        return null;
      default:
        return null;
    }
  }
}

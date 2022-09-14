import { Pipe, PipeTransform } from '@angular/core';

import { PeerReviewDecision } from 'pmrv-api';

@Pipe({ name: 'determinationAssessment' })
export class DeterminationAssessmentPipe implements PipeTransform {
  transform(value: PeerReviewDecision['type']): string {
    switch (value) {
      case 'AGREE':
        return 'Agreed with the determination';
      case 'DISAGREE':
        return 'Disagreed with the determination';
      default:
        return '';
    }
  }
}

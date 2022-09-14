import { Pipe, PipeTransform } from '@angular/core';

import { PermitIssuanceReviewDecision, PermitVariationReviewDecision } from 'pmrv-api';

@Pipe({ name: 'reviewGroupDecision' })
export class ReviewGroupDecisionPipe implements PipeTransform {
  transform(value: PermitIssuanceReviewDecision['type'] | PermitVariationReviewDecision['type']): string {
    switch (value) {
      case 'ACCEPTED':
        return 'Accepted';
      case 'REJECTED':
        return 'Rejected';
      case 'OPERATOR_AMENDS_NEEDED':
        return 'Operator amends needed';
    }
  }
}

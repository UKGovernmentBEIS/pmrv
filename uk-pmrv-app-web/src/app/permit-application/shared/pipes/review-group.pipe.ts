import { Pipe, PipeTransform } from '@angular/core';

import { map, Observable } from 'rxjs';

import { PermitIssuanceReviewDecision, PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Pipe({ name: 'reviewGroup' })
export class ReviewGroupPipe implements PipeTransform {
  constructor(private readonly store: PermitApplicationStore) {}

  transform(
    key: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'],
  ): Observable<PermitIssuanceReviewDecision> {
    return this.store.pipe(map((state) => state.reviewGroupDecisions[key] ?? null));
  }
}

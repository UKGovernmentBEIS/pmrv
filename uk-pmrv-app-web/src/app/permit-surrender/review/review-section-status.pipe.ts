import { Pipe, PipeTransform } from '@angular/core';

import { map, Observable } from 'rxjs';

import { PermitSurrenderStore } from '../store/permit-surrender.store';
import { DecisionStatus, DeterminationStatus, ReviewSectionKey } from './core/review';
import { resolveDecisionStatus, resolveDeterminationStatus } from './core/review-status';

@Pipe({
  name: 'reviewSectionStatus',
})
export class ReviewSectionStatusPipe implements PipeTransform {
  constructor(private readonly store: PermitSurrenderStore) {}

  transform(key: ReviewSectionKey): Observable<DecisionStatus | DeterminationStatus> {
    return this.store.pipe(
      map((state) => {
        switch (key) {
          case 'DECISION':
            return resolveDecisionStatus(state);
          case 'DETERMINATION':
            return resolveDeterminationStatus(state);
        }
      }),
    );
  }
}

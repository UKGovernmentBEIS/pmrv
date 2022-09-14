import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { SharedStore } from '@shared/store/shared.store';

import { getRequestType } from '../peer-review-decision-type-resolver';

@Injectable({
  providedIn: 'root',
})
export class AnswersGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly sharedStore: SharedStore) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    const requestType = getRequestType(state.url);
    const splitedUrl = state.url.split('/');
    const workflowType = splitedUrl[1];
    const peerReviewDecisionUrl = !['permit-revocation'].includes(workflowType)
      ? workflowType !== 'tasks'
        ? 'review/peer-review-decision'
        : 'peer-review/decision'
      : 'peer-review-decision';

    return this.sharedStore.pipe(
      first(),
      map(
        (state) =>
          (!!state.peerReviewDecision?.type && !!state.peerReviewDecision?.notes) ||
          this.router.parseUrl(
            workflowType !== 'tasks'
              ? `${requestType}/${route.paramMap.get('taskId')}/${peerReviewDecisionUrl}`
              : `${workflowType}/${route.paramMap.get('taskId')}/${requestType}/${peerReviewDecisionUrl}`,
          ),
      ),
    );
  }
}

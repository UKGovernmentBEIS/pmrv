import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map } from 'rxjs';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-list-return-link',
  template: '<a govukLink [routerLink]="link$ | async">Return to: {{ title }}</a>',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ListReturnLinkComponent {
  @Input() reviewGroupUrl: string;
  @Input() reviewGroupTitle: string;

  title: string;

  link$ = combineLatest([this.route.url, this.store]).pipe(
    map(([url, state]) => {
      const isIncludedInUrl = url.some(
        (segment) =>
          segment.path.includes('summary') ||
          ((this.reviewGroupUrl === 'monitoring-methodology-plan' || this.reviewGroupUrl === 'uncertainty-analysis') &&
            (segment.path.includes('answers') || segment.path.includes('upload-file'))),
      );

      if (
        [
          'PERMIT_ISSUANCE_APPLICATION_REVIEW',
          'PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW',
          'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW',
          'PERMIT_ISSUANCE_WAIT_FOR_AMENDS',
          'PERMIT_VARIATION_APPLICATION_REVIEW',
        ].some((type) => type === state.requestTaskType) ||
        ['PERMIT_ISSUANCE_APPLICATION_GRANTED'].some((type) => type === state.requestActionType)
      ) {
        this.title = state.userViewRole === 'REGULATOR' ? this.reviewGroupTitle : this.typeText(state);
        return state.userViewRole === 'REGULATOR'
          ? isIncludedInUrl
            ? `../../review/${this.reviewGroupUrl}`
            : `../review/${this.reviewGroupUrl}`
          : isIncludedInUrl
          ? '../..'
          : '..';
      }

      if (['PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT'].some((type) => type === state.requestTaskType)) {
        this.title = 'Apply for a permit variation';
        return `/tasks/${state.requestTaskId}/permit-variation`;
      }

      //default
      this.title = this.typeText(state);
      return isIncludedInUrl ? '../..' : '..';
    }),
  );

  constructor(private readonly route: ActivatedRoute, private readonly store: PermitApplicationStore) {}

  private typeText(state: PermitApplicationState): string {
    return state.isVariation ? 'Make a change to your permit' : 'Apply for a permit';
  }
}

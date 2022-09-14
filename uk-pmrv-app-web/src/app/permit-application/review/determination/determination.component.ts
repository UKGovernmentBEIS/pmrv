import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../core/interfaces/pending-request.interface';
import { BackLinkService } from '../../../shared/back-link/back-link.service';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mandatoryGroups } from '../review';

@Component({
  selector: 'app-determination',
  templateUrl: './determination.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class DeterminationComponent implements OnInit, PendingRequest {
  isGrantDisplayed$ = this.store.pipe(
    map((state) => {
      const groups = mandatoryGroups.concat(
        Object.keys(state.permit.monitoringApproaches) as Array<
          PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group']
        >,
      );
      return (
        groups.every((mg) => state.reviewSectionsCompleted[mg]) &&
        groups.every((mg) => state.reviewGroupDecisions[mg]?.type === 'ACCEPTED')
      );
    }),
  );

  isRejectDisplayed$ = this.store.pipe(
    map((state) => {
      const groups = mandatoryGroups.concat(
        Object.keys(state.permit.monitoringApproaches) as Array<
          PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group']
        >,
      );
      return (
        groups.every((mg) => state.reviewSectionsCompleted[mg]) &&
        groups.every((mg) => !!state.reviewGroupDecisions[mg]?.type) &&
        groups.some((mg) => state.reviewGroupDecisions[mg]?.type === 'REJECTED') &&
        !groups.some((mg) => state.reviewGroupDecisions[mg]?.type === 'OPERATOR_AMENDS_NEEDED')
      );
    }),
  );

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly backLinkService: BackLinkService,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onContinue(type: string): void {
    if (!this.determinationChanged(type)) {
      this.router.navigate(['reason'], { relativeTo: this.route });
    } else {
      this.store
        .pipe(
          first(),
          switchMap(() => this.store.postDetermination({ type }, false)),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate(['reason'], { relativeTo: this.route }));
    }
  }

  determinationChanged(type: string): boolean {
    return this.store.getState().determination?.type !== type;
  }
}

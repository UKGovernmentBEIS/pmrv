import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { PermitSurrenderReviewDetermination } from 'pmrv-api';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { PendingRequest } from '../../../core/interfaces/pending-request.interface';
import { BackLinkService } from '../../../shared/back-link/back-link.service';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';
import { DeterminationTypeUrlMap, isGrantActionAllowed, isRejectActionAllowed } from '../core/review';

@Component({
  selector: 'app-determination',
  templateUrl: './determination.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class DeterminationComponent implements OnInit, PendingRequest {
  isGrantActionAllowed$ = this.store.pipe(map((state) => isGrantActionAllowed(state)));
  isRejectActionAllowed$ = this.store.pipe(map((state) => isRejectActionAllowed(state)));

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly backLinkService: BackLinkService,
    readonly store: PermitSurrenderStore,
    readonly pendingRequest: PendingRequestService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onDeterminationTypeSelected(type: PermitSurrenderReviewDetermination['type']): void {
    if (!this.determinationChanged(type)) {
      this.router.navigate([`${DeterminationTypeUrlMap[type]}/reason`], { relativeTo: this.route });
    } else {
      this.store
        .pipe(
          first(),
          switchMap(() => this.store.postReviewDetermination({ type }, false)),
          this.pendingRequest.trackRequest(),
        )
        .subscribe(() => this.router.navigate([`${DeterminationTypeUrlMap[type]}/reason`], { relativeTo: this.route }));
    }
  }

  private determinationChanged(type: PermitSurrenderReviewDetermination['type']): boolean {
    return this.store.getState()?.reviewDetermination?.type !== type;
  }
}

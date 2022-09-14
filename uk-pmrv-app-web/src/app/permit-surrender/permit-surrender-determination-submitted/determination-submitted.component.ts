import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { combineLatest, filter, first, map, Observable, pluck } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { BackLinkService } from '@shared/back-link/back-link.service';

import {
  PermitSurrenderReviewDeterminationDeemWithdraw,
  PermitSurrenderReviewDeterminationGrant,
  PermitSurrenderReviewDeterminationReject,
  RequestActionUserInfo,
} from 'pmrv-api';

import { PermitSurrenderStore } from '../store/permit-surrender.store';

@Component({
  selector: 'app-review-decision',
  templateUrl: './determination-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class DeterminationSubmittedComponent implements OnInit {
  userRoleType$: Observable<any>;
  reviewDeterminationGrant$: Observable<PermitSurrenderReviewDeterminationGrant>;
  reviewDeterminationReject$: Observable<PermitSurrenderReviewDeterminationReject>;
  reviewDeterminationDeemWithdrown$: Observable<PermitSurrenderReviewDeterminationDeemWithdraw>;
  signatory$: Observable<string>;
  usersInfo$: Observable<{
    [key: string]: RequestActionUserInfo;
  }>;
  operators$: Observable<string[]>;

  constructor(
    readonly store: PermitSurrenderStore,
    private readonly authService: AuthService,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
    this.userRoleType$ = this.authService.userStatus.pipe(pluck('roleType'));

    this.reviewDeterminationGrant$ = this.store.pipe(
      filter((state) => state.reviewDetermination?.type === 'GRANTED'),
      map((state) => state.reviewDetermination as PermitSurrenderReviewDeterminationGrant),
    );

    this.reviewDeterminationReject$ = this.store.pipe(
      filter((state) => state.reviewDetermination?.type === 'REJECTED'),
      map((state) => state.reviewDetermination as PermitSurrenderReviewDeterminationReject),
    );

    this.reviewDeterminationDeemWithdrown$ = this.store.pipe(
      filter((state) => state.reviewDetermination?.type === 'DEEMED_WITHDRAWN'),
      map((state) => state.reviewDetermination as PermitSurrenderReviewDeterminationDeemWithdraw),
    );

    this.signatory$ = this.store.pipe(pluck('reviewDecisionNotification', 'signatory'));
    this.usersInfo$ = this.store.pipe(pluck('usersInfo')) as Observable<{ [key: string]: RequestActionUserInfo }>;
    this.operators$ = combineLatest([this.usersInfo$, this.signatory$]).pipe(
      first(),
      map(([usersInfo, signatory]) => Object.keys(usersInfo).filter((userId) => userId !== signatory)),
    );
  }
}

import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map, Observable, pluck } from 'rxjs';

import { DecisionNotification, FileInfoDTO, PermitNotificationReviewDecision, RequestActionUserInfo } from 'pmrv-api';

import { CommonActionsStore } from '../../store/common-actions.store';
import { PermitNotificationService } from '../core/permit-notification.service';

@Component({
  selector: 'app-permit-notification-review-decision',
  templateUrl: './review-decision.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReviewDecisionComponent implements OnInit {
  reviewDecision$: Observable<PermitNotificationReviewDecision>;
  actionId$: Observable<number>;
  reviewDecisionNotification$: Observable<DecisionNotification>;
  usersInfo$: Observable<{
    [key: string]: RequestActionUserInfo;
  }>;
  officialNotice$: Observable<FileInfoDTO>;
  signatory$: Observable<string>;
  operators$: Observable<string[]>;

  constructor(
    readonly route: ActivatedRoute,
    public readonly permitNotificationService: PermitNotificationService,
    readonly store: CommonActionsStore,
  ) {}

  ngOnInit(): void {
    this.reviewDecision$ = this.permitNotificationService.reviewDecision$;
    this.actionId$ = this.store.pipe(pluck('action', 'id'));
    this.reviewDecisionNotification$ = this.store.pipe(
      pluck('action', 'payload', 'reviewDecisionNotification'),
    ) as Observable<DecisionNotification>;
    this.usersInfo$ = this.store.pipe(pluck('action', 'payload', 'usersInfo')) as Observable<{
      [key: string]: RequestActionUserInfo;
    }>;
    this.officialNotice$ = this.store.pipe(pluck('action', 'payload', 'officialNotice')) as Observable<FileInfoDTO>;
    this.signatory$ = this.store.pipe(
      pluck('action', 'payload', 'reviewDecisionNotification', 'signatory'),
    ) as Observable<string>;
    this.operators$ = combineLatest([this.usersInfo$, this.signatory$]).pipe(
      map(([usersInfo, signatory]) => Object.keys(usersInfo).filter((userId) => userId !== signatory)),
    );
  }
}

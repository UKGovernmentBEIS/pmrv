import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { PaymentNotCompletedComponent } from '@shared/components/payment-not-completed/payment-not-completed.component';
import { PeerReviewComponent } from '@shared/components/peer-review/peer-review.component';
import { PeerReviewDecisionComponent } from '@shared/components/peer-review-decision/peer-review-decision.component';
import { PeerReviewDecisionGuard } from '@shared/components/peer-review-decision/peer-review-decision.guard';
import { PeerReviewSubmittedComponent } from '@shared/components/peer-review-decision/timeline/peer-review-submitted.component';
import { PaymentCompletedGuard } from '@shared/guards/payment-completed.guard';

import { AnswersComponent as PeerReviewDecisionAnswersComponent } from '../../shared/components/peer-review-decision/answers/answers.component';
import { AnswersGuard as PeerReviewDecisionAnswersGuard } from '../../shared/components/peer-review-decision/answers/answers.guard';
import { ConfirmationComponent as PeerReviewDecisionConfirmationComponent } from '../../shared/components/peer-review-decision/confirmation/confirmation.component';
import { PermitSurrenderRoute } from '../core/permit-surrender-route.interface';
import { DecisionComponent } from './decision/decision.component';
import { AnswersComponent as AnswersDeemWithdrawComponent } from './determination/deem-withdraw/answers/answers.component';
import { AnswersGuard as AnswersDeemWithdrawGuard } from './determination/deem-withdraw/answers/answers.guard';
import { SummaryComponent as SummaryDeemWithdrawComponent } from './determination/deem-withdraw/summary/summary.component';
import { DeterminationComponent } from './determination/determination.component';
import { DeterminationGuard } from './determination/determination.guard';
import { AllowancesComponent } from './determination/grant/allowances/allowances.component';
import { AllowancesGuard } from './determination/grant/allowances/allowances.guard';
import { AnswersComponent as AnswersGrantComponent } from './determination/grant/answers/answers.component';
import { AnswersGuard as AnswersGrantGuard } from './determination/grant/answers/answers.guard';
import { NoticeDateComponent } from './determination/grant/notice-date/notice-date.component';
import { NoticeDateGuard } from './determination/grant/notice-date/notice-date.guard';
import { ReportComponent } from './determination/grant/report/report.component';
import { ReportGuard } from './determination/grant/report/report.guard';
import { StopDateComponent } from './determination/grant/stop-date/stop-date.component';
import { StopDateGuard } from './determination/grant/stop-date/stop-date.guard';
import { SummaryComponent as SummaryGrantComponent } from './determination/grant/summary/summary.component';
import { AnswersComponent as RejectAnswersComponent } from './determination/reject/answers/answers.component';
import { AnswersGuard as RejectAnswersGuard } from './determination/reject/answers/answers.guard';
import { RefundComponent } from './determination/reject/refund/refund.component';
import { RefundGuard } from './determination/reject/refund/refund.guard';
import { RefusalComponent } from './determination/reject/refusal/refusal.component';
import { RefusalGuard } from './determination/reject/refusal/refusal.guard';
import { SummaryComponent as RejectSummaryComponent } from './determination/reject/summary/summary.component';
import { ReasonComponent } from './determination/shared/reason/reason.component';
import { ReasonGuard } from './determination/shared/reason/reason.guard';
import { SummaryGuard } from './determination/summary.guard';
import { InvalidDataComponent } from './invalid-data/invalid-data.component';
import { NotifyOperatorComponent } from './notify-operator/notify-operator.component';
import { NotifyOperatorGuard } from './notify-operator/notify-operator.guard';
import { ReviewComponent } from './review.component';
import { WaitReviewComponent } from './wait/wait-review.component';

const routes: PermitSurrenderRoute[] = [
  {
    path: '',
    data: { pageTitle: 'Review permit surrender' },
    component: ReviewComponent,
  },
  {
    path: 'decision',
    data: { pageTitle: 'Surrender permit request review task' },
    component: DecisionComponent,
  },
  {
    path: 'determination',
    children: [
      {
        path: '',
        data: { pageTitle: 'Surrender permit determination' },
        component: DeterminationComponent,
        canActivate: [DeterminationGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'grant',
        data: { pageTitle: 'Surrender permit determination - Grant' },
        children: [
          {
            path: 'reason',
            data: { pageTitle: 'Surrender permit request task - Notes' },
            component: ReasonComponent,
            canActivate: [ReasonGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'stop-date',
            data: { pageTitle: 'Surrender permit request task - Stop date' },
            component: StopDateComponent,
            canActivate: [StopDateGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'notice-date',
            data: { pageTitle: 'Surrender permit request task - Notice date' },
            component: NoticeDateComponent,
            canActivate: [NoticeDateGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'report',
            data: { pageTitle: 'Surrender permit request task - Report' },
            component: ReportComponent,
            canActivate: [ReportGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'allowances',
            data: { pageTitle: 'Surrender permit request task - Allowances' },
            component: AllowancesComponent,
            canActivate: [AllowancesGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'answers',
            data: { pageTitle: 'Surrender permit request task - Answers' },
            component: AnswersGrantComponent,
            canActivate: [AnswersGrantGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Surrender permit request task - Summary' },
            component: SummaryGrantComponent,
            canActivate: [SummaryGuard],
          },
        ],
      },
      {
        path: 'reject',
        data: { pageTitle: 'Surrender permit determination - Reject' },
        children: [
          {
            path: 'reason',
            data: { pageTitle: 'Surrender permit request task - Notes' },
            component: ReasonComponent,
            canActivate: [ReasonGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'refusal',
            data: { pageTitle: 'Surrender permit request task - Official refusal' },
            component: RefusalComponent,
            canActivate: [RefusalGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'refund',
            data: { pageTitle: 'Surrender permit request task - Operator fee' },
            component: RefundComponent,
            canActivate: [RefundGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'answers',
            data: { pageTitle: 'Surrender permit request task - Answers' },
            component: RejectAnswersComponent,
            canActivate: [RejectAnswersGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Surrender permit request task - Summary' },
            component: RejectSummaryComponent,
            canActivate: [SummaryGuard],
          },
        ],
      },
      {
        path: 'deem-withdraw',
        data: { pageTitle: 'Surrender permit determination - Deem withdraw' },
        children: [
          {
            path: 'reason',
            data: { pageTitle: 'Surrender permit request task - Notes' },
            component: ReasonComponent,
            canActivate: [ReasonGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'answers',
            data: { pageTitle: 'Surrender permit request task - Answers' },
            component: AnswersDeemWithdrawComponent,
            canActivate: [AnswersDeemWithdrawGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Surrender permit request task - Summary' },
            component: SummaryDeemWithdrawComponent,
            canActivate: [SummaryGuard],
          },
        ],
      },
    ],
  },
  {
    path: 'notify-operator',
    data: { pageTitle: 'Notify operator' },
    component: NotifyOperatorComponent,
    canActivate: [NotifyOperatorGuard, PaymentCompletedGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'wait',
    data: { pageTitle: 'Wait for review permit surrender' },
    component: WaitReviewComponent,
  },
  {
    path: 'peer-review',
    canActivate: [PaymentCompletedGuard],
    children: [
      {
        path: '',
        data: { pageTitle: 'Peer review' },
        component: PeerReviewComponent,
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'peer-review-decision',
    children: [
      {
        path: '',
        data: { pageTitle: 'Peer review decision' },
        component: PeerReviewDecisionComponent,
        canActivate: [PeerReviewDecisionGuard],
      },
      {
        path: 'answers',
        data: { pageTitle: 'Peer review decision answers' },
        component: PeerReviewDecisionAnswersComponent,
        canActivate: [PeerReviewDecisionAnswersGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'confirmation',
        data: { pageTitle: 'Peer review decision confirmation' },
        component: PeerReviewDecisionConfirmationComponent,
      },
    ],
  },
  {
    path: 'peer-reviewer-submitted',
    data: { pageTitle: 'Peer review submitted' },
    component: PeerReviewSubmittedComponent,
  },
  {
    path: 'invalid-data',
    data: { pageTitle: 'Invalid data' },
    component: InvalidDataComponent,
  },
  {
    path: 'payment-not-completed',
    component: PaymentNotCompletedComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class SurrenderReviewRoutingModule {}

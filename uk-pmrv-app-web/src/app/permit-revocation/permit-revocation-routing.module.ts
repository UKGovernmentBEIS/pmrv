import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { PermitRevocationActionGuard } from '@permit-revocation/permit-revocation-action.guard';
import { ReasonComponent } from '@permit-revocation/permit-revocation-apply/reason/reason.component';
import { StopDateComponent } from '@permit-revocation/permit-revocation-apply/stop-date/stop-date.component';
import { PermitRevocationTaskGuard } from '@permit-revocation/permit-revocation-task.guard';
import { PermitRevocationTasklistComponent } from '@permit-revocation/permit-revocation-tasklist/permit-revocation-tasklist.component';
import { PaymentNotCompletedComponent } from '@shared/components/payment-not-completed/payment-not-completed.component';
import { PeerReviewComponent } from '@shared/components/peer-review/peer-review.component';
import { AnswersComponent as PeerReviewDecisionAnswersComponent } from '@shared/components/peer-review-decision/answers/answers.component';
import { AnswersGuard as PeerReviewDecisionAnswersGuard } from '@shared/components/peer-review-decision/answers/answers.guard';
import { ConfirmationComponent as PeerReviewDecisionConfirmationComponent } from '@shared/components/peer-review-decision/confirmation/confirmation.component';
import { PeerReviewDecisionComponent } from '@shared/components/peer-review-decision/peer-review-decision.component';
import { PeerReviewDecisionGuard } from '@shared/components/peer-review-decision/peer-review-decision.guard';
import { PeerReviewSubmittedComponent } from '@shared/components/peer-review-decision/timeline/peer-review-submitted.component';
import { FileDownloadComponent } from '@shared/file-download/file-download.component';

import { ConfirmSubmitGuard } from './core/guards/confirm-submit-guard';
import { SummaryGuard } from './core/guards/summary-guard';
import { WizardStepsGuard } from './core/guards/wizard-steps-guard';
import { InvalidDataComponent } from './invalid-data/invalid-data.component';
import { NotifyOperatorComponent } from './notify-operator/notify-operator.component';
import { NotifyOperatorGuard } from './notify-operator/notify-operator.guard';
import { ConfirmAnswersComponent } from './permit-revocation-apply/confirm-answers/confirm-answers.component';
import { FeeComponent } from './permit-revocation-apply/fee/fee.component';
import { NoticeComponent } from './permit-revocation-apply/notice/notice.component';
import { ReportComponent } from './permit-revocation-apply/report/report.component';
import { SummaryContainerComponent } from './permit-revocation-apply/summary';
import { SurrenderAllowancesComponent } from './permit-revocation-apply/surrender-allowances/surrender-allowances.component';
import { PermitRevocationSubmittedComponent } from './permit-revocation-submitted/permit-revocation-submitted.component';
import { WithdrawReasonGuard } from './wait-for-appeal-reason/guard/withdraw-reason.guard';
import { WithdrawSummaryContainerComponent } from './wait-for-appeal-reason/summary';
import { WithdrawSummaryGuard } from './wait-for-appeal-reason/summary/guard/withdraw-summary-guard';
import { WaitForAppealReasonComponent } from './wait-for-appeal-reason/wait-for-appeal-reason.component';
import { WaitForAppealTasklistComponent } from './wait-for-appeal-tasklist/wait-for-appeal-tasklist.component';
import { WithdrawCompletedComponent } from './withdraw-completed/withdraw-completed.component';

const commonRoutes = [
  {
    path: 'cessation',
    loadChildren: () => import('./cessation/cessation.module').then((m) => m.CessationModule),
  },
  {
    path: 'file-download/:fileType/:uuid',
    component: FileDownloadComponent,
  },
];

const taskRoutes = [
  {
    path: '',
    component: PermitRevocationTasklistComponent,
  },
  {
    path: 'notify-operator',
    data: { pageTitle: 'Notify operator', requestTaskActionType: 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION' },
    component: NotifyOperatorComponent,
    canActivate: [NotifyOperatorGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'invalid-data',
    data: { pageTitle: 'Invalid data', keys: ['effectiveDate', 'feeDate'] },
    component: InvalidDataComponent,
  },
  {
    path: 'peer-review',
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
    path: 'apply',
    data: { statusKey: 'REVOCATION_APPLY' },

    children: [
      {
        path: 'reason',
        data: { pageTitle: 'Explain why the permit is being revoked', keys: ['reason'] },
        canActivate: [WizardStepsGuard],
        canDeactivate: [PendingRequestGuard],
        component: ReasonComponent,
      },
      {
        path: 'stop-date',
        data: {
          pageTitle: 'Have the regulated activities at the installation stopped?',
          keys: ['activitiesStopped', 'stoppedDate'],
        },
        canActivate: [WizardStepsGuard],
        canDeactivate: [PendingRequestGuard],
        component: StopDateComponent,
      },
      {
        path: 'notice',
        data: { pageTitle: 'Set the effective date of the permit revocation notice', keys: ['effectiveDate'] },
        canActivate: [WizardStepsGuard],
        canDeactivate: [PendingRequestGuard],
        component: NoticeComponent,
      },

      {
        path: 'report',
        data: {
          pageTitle: 'Is an annual emissions monitoring revocation report required?',
          keys: ['annualEmissionsReportDate', 'annualEmissionsReportRequired'],
        },
        canActivate: [WizardStepsGuard],
        canDeactivate: [PendingRequestGuard],
        component: ReportComponent,
      },
      {
        path: 'surrender-allowances',
        data: { pageTitle: 'Is a surrender of allowances required?', keys: ['surrenderDate', 'surrenderRequired'] },
        canActivate: [WizardStepsGuard],
        canDeactivate: [PendingRequestGuard],
        component: SurrenderAllowancesComponent,
      },
      {
        path: 'fee',
        data: { pageTitle: 'Do you need to charge the operator a fee?', keys: ['feeCharged', 'feeDate', 'feeDetails'] },
        canActivate: [WizardStepsGuard],
        canDeactivate: [PendingRequestGuard],
        component: FeeComponent,
      },
      {
        path: 'answers',
        data: { pageTitle: 'Confirm your answers', keys: ['feeDate', 'effectiveDate'], skipValidators: true },
        canActivate: [ConfirmSubmitGuard],
        canDeactivate: [PendingRequestGuard],
        component: ConfirmAnswersComponent,
      },
      {
        path: 'summary',
        data: { pageTitle: 'Permit revocation', keys: ['feeDate', 'effectiveDate'], skipValidators: true },
        component: SummaryContainerComponent,
        canActivate: [SummaryGuard],
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
    path: 'wait-for-appeal',
    children: [
      {
        path: '',
        data: { pageTitle: 'Withdraw permit revocation' },
        component: WaitForAppealTasklistComponent,
      },
      {
        path: 'reason',
        data: { pageTitle: 'Reason for withdrawing the revocation', caption: 'Withdraw permit revocation' },
        component: WaitForAppealReasonComponent,
        canActivate: [WithdrawReasonGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Provide a reason for withdrawing a revocation' },
        canActivate: [WithdrawSummaryGuard],
        component: WithdrawSummaryContainerComponent,
      },
      {
        path: 'withdraw-notify-operator',
        data: {
          pageTitle: 'Notify operator',
          requestTaskActionType: 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL',
        },
        component: NotifyOperatorComponent,
        canActivate: [NotifyOperatorGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'payment-not-completed',
    component: PaymentNotCompletedComponent,
  },
  ...commonRoutes,
];

const routes: Routes = [
  {
    path: ':taskId',
    children: taskRoutes,
    canActivate: [PermitRevocationTaskGuard],
    canDeactivate: [PermitRevocationTaskGuard],
  },
  {
    path: 'action/:actionId',
    canActivate: [PermitRevocationActionGuard],
    canDeactivate: [PermitRevocationActionGuard],
    children: [
      {
        path: 'revocation-submitted',
        data: { pageTitle: 'Permit revocation submitted' },
        component: PermitRevocationSubmittedComponent,
      },
      {
        path: 'withdraw-completed',
        data: { pageTitle: 'Revocation request withdrawn' },
        component: WithdrawCompletedComponent,
      },
      {
        path: 'peer-reviewer-submitted',
        data: { pageTitle: 'Peer review submitted' },
        component: PeerReviewSubmittedComponent,
      },
      ...commonRoutes,
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PermitRevocationRoutingModule {}

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { NotifyOperatorComponent } from '@permit-revocation/notify-operator/notify-operator.component';
import { NotifyOperatorGuard } from '@permit-revocation/notify-operator/notify-operator.guard';
import { PermitRevocationTaskGuard } from '@permit-revocation/permit-revocation-task.guard';

import { PaymentCompletedGuard } from '../../shared/guards/payment-completed.guard';
import { CessationComponent } from './cessation.component';
import { AllowancesDateComponent } from './confirm/allowances-date/allowances-date.component';
import { AllowancesNumberComponent } from './confirm/allowances-number/allowances-number.component';
import { AnswersComponent } from './confirm/answers/answers.component';
import { CompletedComponent } from './confirm/completed/completed.component';
import { ConfirmSubmitGuard } from './confirm/core/guards/confirm-submit-guard';
import { SummaryGuard } from './confirm/core/guards/summary-guard';
import { WizardStepsGuard } from './confirm/core/guards/wizard-steps-guard';
import { EmissionsComponent } from './confirm/emissions/emissions.component';
import { NotesComponent } from './confirm/notes/notes.component';
import { NoticeComponent } from './confirm/notice/notice.component';
import { OutcomeComponent } from './confirm/outcome/outcome.component';
import { RefundComponent } from './confirm/refund/refund.component';
import { SummaryComponent } from './confirm/summary/summary.component';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Revocation cessation' },
    component: CessationComponent,
  },
  {
    path: 'confirm',
    data: { pageTitle: 'Revocation cessation confirm' },
    canActivate: [PermitRevocationTaskGuard],
    canDeactivate: [PermitRevocationTaskGuard],
    children: [
      {
        path: 'outcome',
        data: { pageTitle: 'Revocation cessation confirm - outcome', keys: ['determinationOutcome'] },
        component: OutcomeComponent,
        canActivate: [WizardStepsGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'allowances-date',
        data: { pageTitle: 'Revocation cessation confirm - allowances date', keys: ['allowancesSurrenderDate'] },
        component: AllowancesDateComponent,
        canActivate: [WizardStepsGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'allowances-number',
        data: {
          pageTitle: 'Revocation cessation confirm - number of allowances',
          keys: ['numberOfSurrenderAllowances'],
        },
        component: AllowancesNumberComponent,
        canActivate: [WizardStepsGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'emissions',
        data: { pageTitle: 'Revocation cessation confirm - emissions', keys: ['annualReportableEmissions'] },
        component: EmissionsComponent,
        canActivate: [WizardStepsGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'refund',
        data: { pageTitle: 'Revocation cessation confirm - refund', keys: ['subsistenceFeeRefunded'] },
        component: RefundComponent,
        canActivate: [WizardStepsGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'notice',
        data: { pageTitle: 'Revocation cessation confirm - notice', keys: ['noticeType'] },
        component: NoticeComponent,
        canActivate: [WizardStepsGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'notes',
        data: { pageTitle: 'Revocation cessation confirm - notes', keys: ['notes'] },
        component: NotesComponent,
        canActivate: [WizardStepsGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'answers',
        data: { pageTitle: 'Revocation cessation confirm - Answers' },
        component: AnswersComponent,
        canActivate: [ConfirmSubmitGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Revocation cessation confirm - Summary' },
        component: SummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'completed',
    data: { pageTitle: 'Cessation completed' },
    component: CompletedComponent,
  },
  {
    path: 'notify-operator',
    data: { pageTitle: 'Notify operator', requestTaskActionType: 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_CESSATION' },
    component: NotifyOperatorComponent,
    canActivate: [NotifyOperatorGuard, PaymentCompletedGuard],
    canDeactivate: [PendingRequestGuard],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CessationRoutingModule {}

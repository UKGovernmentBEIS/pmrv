import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { TaskStatusPipe } from './core/task-status.pipe';
import { InvalidDataComponent } from './invalid-data/invalid-data.component';
import { NotifyOperatorComponent } from './notify-operator/notify-operator.component';
import { ConfirmAnswersComponent } from './permit-revocation-apply/confirm-answers/confirm-answers.component';
import { FeeComponent } from './permit-revocation-apply/fee/fee.component';
import { NoticeComponent } from './permit-revocation-apply/notice/notice.component';
import { ReasonComponent } from './permit-revocation-apply/reason/reason.component';
import { ReportComponent } from './permit-revocation-apply/report/report.component';
import { StopDateComponent } from './permit-revocation-apply/stop-date/stop-date.component';
import { SummaryComponent, SummaryContainerComponent } from './permit-revocation-apply/summary';
import { SurrenderAllowancesComponent } from './permit-revocation-apply/surrender-allowances/surrender-allowances.component';
import { PermitRevocationRoutingModule } from './permit-revocation-routing.module';
import { PermitRevocationSubmittedComponent } from './permit-revocation-submitted/permit-revocation-submitted.component';
import { PermitRevocationTasklistComponent } from './permit-revocation-tasklist/permit-revocation-tasklist.component';
import { OfficialNoticeRecipientsComponent } from './shared/official-notice-recipients/official-notice-recipients.component';
import { WithdrawSummaryComponent, WithdrawSummaryContainerComponent } from './wait-for-appeal-reason/summary';
import { WaitForAppealReasonComponent } from './wait-for-appeal-reason/wait-for-appeal-reason.component';
import { WaitForAppealTasklistComponent } from './wait-for-appeal-tasklist/wait-for-appeal-tasklist.component';
import { WithdrawCompletedComponent } from './withdraw-completed/withdraw-completed.component';

@NgModule({
  declarations: [
    ConfirmAnswersComponent,
    FeeComponent,
    InvalidDataComponent,
    NoticeComponent,
    NotifyOperatorComponent,
    OfficialNoticeRecipientsComponent,
    PermitRevocationSubmittedComponent,
    PermitRevocationTasklistComponent,
    ReasonComponent,
    ReportComponent,
    StopDateComponent,
    SummaryComponent,
    SummaryContainerComponent,
    SurrenderAllowancesComponent,
    TaskStatusPipe,
    WaitForAppealReasonComponent,
    WaitForAppealTasklistComponent,
    WithdrawCompletedComponent,
    WithdrawSummaryComponent,
    WithdrawSummaryContainerComponent,
  ],
  imports: [PermitRevocationRoutingModule, SharedModule],
})
export class PermitRevocationModule {}

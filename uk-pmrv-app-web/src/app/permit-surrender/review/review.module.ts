import { NgModule } from '@angular/core';

import { SharedPermitModule } from '../../permit-application/shared/shared-permit.module';
import { SharedModule } from '../../shared/shared.module';
import { SharedPermitSurrenderModule } from '../shared/shared-permit-surrender.module';
import { DecisionComponent } from './decision/decision.component';
import { DecisionSummaryComponent } from './decision/decision-summary/decision-summary.component';
import { AnswersComponent as AnswersDeemWithdraw } from './determination/deem-withdraw/answers/answers.component';
import { SummaryComponent as SummaryDeemWithdrawComponent } from './determination/deem-withdraw/summary/summary.component';
import { SummaryDetailsComponent as SummaryDetailsDeemWithdrawComponent } from './determination/deem-withdraw/summary/summary-details.component';
import { DeterminationComponent } from './determination/determination.component';
import { AllowancesComponent } from './determination/grant/allowances/allowances.component';
import { AnswersComponent as AnswersGrant } from './determination/grant/answers/answers.component';
import { NoticeDateComponent } from './determination/grant/notice-date/notice-date.component';
import { ReportComponent } from './determination/grant/report/report.component';
import { StopDateComponent } from './determination/grant/stop-date/stop-date.component';
import { SummaryComponent as SummaryGrantComponent } from './determination/grant/summary/summary.component';
import { SummaryDetailsComponent as SummaryDetailsGrantComponent } from './determination/grant/summary/summary-details.component';
import { AnswersComponent as RejectAnswersComponent } from './determination/reject/answers/answers.component';
import { RefundComponent } from './determination/reject/refund/refund.component';
import { RefusalComponent } from './determination/reject/refusal/refusal.component';
import { SummaryComponent as RejectSummaryComponent } from './determination/reject/summary/summary.component';
import { SummaryDetailsComponent as RejectSummaryDetailsComponent } from './determination/reject/summary/summary-details.component';
import { SharedPermitSurrenderReviewDeterminationModule } from './determination/shared/shared-permit-surrender-review-determination.module';
import { InvalidDataComponent } from './invalid-data/invalid-data.component';
import { NotifyOperatorComponent } from './notify-operator/notify-operator.component';
import { ReviewComponent } from './review.component';
import { SurrenderReviewRoutingModule } from './review-routing.module';
import { ReviewSectionStatusPipe } from './review-section-status.pipe';
import { WaitReviewComponent } from './wait/wait-review.component';

@NgModule({
  declarations: [
    AllowancesComponent,
    AnswersDeemWithdraw,
    AnswersGrant,
    DecisionComponent,
    DecisionSummaryComponent,
    DeterminationComponent,
    InvalidDataComponent,
    NoticeDateComponent,
    NotifyOperatorComponent,
    RefundComponent,
    RefusalComponent,
    RejectAnswersComponent,
    RejectSummaryComponent,
    RejectSummaryDetailsComponent,
    ReportComponent,
    ReviewComponent,
    ReviewSectionStatusPipe,
    StopDateComponent,
    SummaryDeemWithdrawComponent,
    SummaryDetailsDeemWithdrawComponent,
    SummaryDetailsGrantComponent,
    SummaryGrantComponent,
    WaitReviewComponent,
  ],
  imports: [
    SharedModule,
    SharedPermitModule,
    SharedPermitSurrenderModule,
    SharedPermitSurrenderReviewDeterminationModule,
    SurrenderReviewRoutingModule,
  ],
})
export class ReviewModule {}

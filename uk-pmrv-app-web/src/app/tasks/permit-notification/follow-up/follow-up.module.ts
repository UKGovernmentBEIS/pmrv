import { NgModule } from '@angular/core';

import { PermitNotificationSharedModule } from '@shared/components/permit-notification/permit-notification-shared.module';
import { SharedModule } from '@shared/shared.module';

import { TaskSharedModule } from '../../shared/task-shared-module';
import { DetailsAmendsComponent } from './details-amends/details-amends.component';
import { FollowUpComponent } from './follow-up.component';
import { FollowUpRoutingModule } from './follow-up-routing.module';
import { RecallComponent } from './recall/recall.component';
import { ResponseComponent } from './response/response.component';
import { SubmitContainerComponent } from './response/submit/submit-container.component';
import { SubmitAmendsContainerComponent } from './response/submit-amends/submit-amends.component';
import { SummaryContainerComponent } from './response/summary-container/summary-container.component';
import { FollowUpReviewDecisionComponent } from './review/decision/decision.component';
import { FollowUpReviewNotifyOperatorComponent } from './review/notify-operator/notify-operator.component';
import { FollowUpReturnForAmendsComponent } from './review/return-for-amends/return-for-amends.component';
import { FollowUpReviewComponent } from './review/review.component';
import { FollowUpReviewWaitComponent } from './review-wait/review-wait.component';
import { DueDateComponent } from './shared/wait/due-date/due-date.component';
import { WaitComponent } from './shared/wait/wait.component';

@NgModule({
  declarations: [
    DetailsAmendsComponent,
    DueDateComponent,
    FollowUpComponent,
    FollowUpReturnForAmendsComponent,
    FollowUpReviewComponent,
    FollowUpReviewDecisionComponent,
    FollowUpReviewNotifyOperatorComponent,
    FollowUpReviewWaitComponent,
    RecallComponent,
    ResponseComponent,
    SubmitAmendsContainerComponent,
    SubmitContainerComponent,
    SummaryContainerComponent,
    WaitComponent,
  ],
  imports: [FollowUpRoutingModule, PermitNotificationSharedModule, SharedModule, TaskSharedModule],
})
export class FollowUpModule {}

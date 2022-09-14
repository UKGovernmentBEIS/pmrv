import { NgModule } from '@angular/core';

import { PermitNotificationSharedModule } from '@shared/components/permit-notification/permit-notification-shared.module';
import { SharedModule } from '@shared/shared.module';

import { ActionSharedModule } from '../shared/action-shared-module';
import { CompletedComponent } from './completed/completed.component';
import { FollowUpResponseComponent } from './follow-up-response/follow-up-response.component';
import { FollowUpReturnForAmendsComponent } from './follow-up-return-for-amends/follow-up-return-for-amends.component';
import { PeerReviewDecisionComponent } from './peer-review-decision/peer-review-decision.component';
import { PermitNotificationComponent } from './permit-notification.component';
import { PermitNotificationRoutingModule } from './permit-notification-routing.module';
import { ReviewDecisionComponent } from './review-decision/review-decision.component';
import { SubmittedComponent } from './submitted/submitted.component';

@NgModule({
  declarations: [
    CompletedComponent,
    FollowUpResponseComponent,
    FollowUpReturnForAmendsComponent,
    PeerReviewDecisionComponent,
    PermitNotificationComponent,
    ReviewDecisionComponent,
    SubmittedComponent,
  ],
  imports: [ActionSharedModule, PermitNotificationRoutingModule, PermitNotificationSharedModule, SharedModule],
})
export class PermitNotificationModule {}

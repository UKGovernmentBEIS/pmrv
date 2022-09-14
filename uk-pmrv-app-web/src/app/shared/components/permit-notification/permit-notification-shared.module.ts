import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { FollowUpSummaryComponent } from '@shared/components/permit-notification/follow-up-summary/follow-up-summary.component';
import { ReviewDecisionSummaryComponent } from '@shared/components/permit-notification/review-decision-summary/review-decision-summary.component';
import { SummaryDetailsComponent } from '@shared/components/permit-notification/submit-summary/summary-details.component';
import { SharedModule } from '@shared/shared.module';

@NgModule({
  imports: [RouterModule, SharedModule],
  declarations: [FollowUpSummaryComponent, ReviewDecisionSummaryComponent, SummaryDetailsComponent],
  exports: [FollowUpSummaryComponent, ReviewDecisionSummaryComponent, SummaryDetailsComponent],
})
export class PermitNotificationSharedModule {}

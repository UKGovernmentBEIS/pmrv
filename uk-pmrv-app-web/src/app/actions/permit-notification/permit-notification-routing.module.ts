import { NgModule } from '@angular/core';
import { Route, RouterModule } from '@angular/router';

import { CompletedComponent } from './completed/completed.component';
import { FollowUpResponseComponent } from './follow-up-response/follow-up-response.component';
import { FollowUpReturnForAmendsComponent } from './follow-up-return-for-amends/follow-up-return-for-amends.component';
import { PeerReviewDecisionComponent } from './peer-review-decision/peer-review-decision.component';
import { PermitNotificationComponent } from './permit-notification.component';
import { ReviewDecisionComponent } from './review-decision/review-decision.component';
import { SubmittedComponent } from './submitted/submitted.component';

const routes: Route[] = [
  {
    path: '',
    component: PermitNotificationComponent,
    data: { pageTitle: 'Notify the regulator of a change' },
    children: [
      {
        path: 'submitted',
        data: { pageTitle: 'Notify the regulator of a change' },
        component: SubmittedComponent,
      },
      {
        path: 'decision',
        component: ReviewDecisionComponent,
        data: { pageTitle: 'Review notification of a change' },
      },
      {
        path: 'peer-review-decision',
        component: PeerReviewDecisionComponent,
        data: { pageTitle: 'Review notification of a change' },
      },
      {
        path: 'follow-up-response',
        component: FollowUpResponseComponent,
        data: { pageTitle: 'Follow up response submitted' },
      },
      {
        path: 'follow-up-return-for-amends',
        component: FollowUpReturnForAmendsComponent,
        data: { pageTitle: 'Follow up response returned for amends' },
      },
      {
        path: 'completed',
        component: CompletedComponent,
        data: { pageTitle: 'Notification completed' },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PermitNotificationRoutingModule {}

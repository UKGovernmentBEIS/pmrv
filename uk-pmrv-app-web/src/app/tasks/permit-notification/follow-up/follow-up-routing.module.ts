import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';

import { DetailsAmendsComponent } from './details-amends/details-amends.component';
import { FollowUpComponent } from './follow-up.component';
import { RecallComponent } from './recall/recall.component';
import { RecallGuard } from './recall/recall.guard';
import { ResponseGuard } from './response/guards/response.guard';
import { SummaryGuard } from './response/guards/summary.guard';
import { ResponseComponent } from './response/response.component';
import { SubmitContainerComponent as SubmitFollowUpResponseComponent } from './response/submit/submit-container.component';
import { SubmitAmendsContainerComponent as SubmitFollowUpAmendsComponent } from './response/submit-amends/submit-amends.component';
import { SummaryContainerComponent } from './response/summary-container/summary-container.component';
import { FollowUpReviewDecisionComponent } from './review/decision/decision.component';
import { FollowUpReviewNotifyOperatorComponent } from './review/notify-operator/notify-operator.component';
import { FollowUpReviewNotifyOperatorGuard } from './review/notify-operator/notify-operator.guard';
import { FollowUpReturnForAmendsComponent } from './review/return-for-amends/return-for-amends.component';
import { FollowUpReturnForAmendsGuard } from './review/return-for-amends/return-for-amends.guard';
import { FollowUpReviewComponent } from './review/review.component';
import { FollowUpReviewWaitComponent } from './review-wait/review-wait.component';
import { DueDateComponent } from './shared/wait/due-date/due-date.component';
import { WaitComponent } from './shared/wait/wait.component';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Follow up response to a notification' },
    component: FollowUpComponent,
  },
  {
    path: 'response',
    data: {
      pageTitle: 'Follow up response',
      keys: ['files', 'followUpResponse'],
      configFilesOptions: {
        type: 'PERMIT_NOTIFICATION_FOLLOW_UP_UPLOAD_ATTACHMENT',
        filesName: 'followUpFiles',
        attachmentsName: 'followUpAttachments',
      },
    },
    canActivate: [ResponseGuard],
    canDeactivate: [PendingRequestGuard],
    component: ResponseComponent,
  },
  {
    path: 'summary',
    data: { pageTitle: 'Follow up response' },
    canActivate: [SummaryGuard],
    component: SummaryContainerComponent,
  },
  {
    path: 'submit',
    data: {
      pageTitle: 'Submit application',
      caption: 'Submit your follow up response',
      titleSubmitted: 'Response submitted',
    },
    canDeactivate: [PendingRequestGuard],
    component: SubmitFollowUpResponseComponent,
  },
  {
    path: 'submit-amends',
    data: {
      pageTitle: 'Submit application',
      caption: 'Submit response',
      titleSubmitted: 'Response submitted',
    },
    canDeactivate: [PendingRequestGuard],
    component: SubmitFollowUpAmendsComponent,
  },
  {
    path: 'review',
    children: [
      {
        path: '',
        component: FollowUpReviewComponent,
        data: { pageTitle: 'Review follow up response to a notification' },
      },
      {
        path: 'decision',
        component: FollowUpReviewDecisionComponent,
        data: { pageTitle: 'Operator response' },
      },
      {
        path: 'notify-operator',
        data: { pageTitle: 'Notify operator' },
        component: FollowUpReviewNotifyOperatorComponent,
        canActivate: [FollowUpReviewNotifyOperatorGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'return-for-amends',
        children: [
          {
            path: '',
            data: { pageTitle: 'Return for amends' },
            component: FollowUpReturnForAmendsComponent,
            canActivate: [FollowUpReturnForAmendsGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
    ],
  },
  {
    path: 'review-wait',
    component: FollowUpReviewWaitComponent,
    data: { pageTitle: 'Follow up response submitted' },
  },
  {
    path: 'wait',
    children: [
      {
        path: '',
        data: { pageTitle: 'Awaiting follow up response to notification', caption: 'Follow up response details' },
        component: WaitComponent,
      },
      {
        path: 'due-date',
        data: {
          pageTitle: 'Edit follow up response deadline',
          keys: ['followUpResponseExpirationDate'],
          canDeactivate: [PendingRequestGuard],
        },
        component: DueDateComponent,
      },
      {
        path: 'recall-from-amends',
        data: { pageTitle: 'Recall from amends' },
        component: RecallComponent,
        canActivate: [RecallGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'details-amends-needed',
    data: { pageTitle: 'Details of the amends needed' },
    canDeactivate: [PendingRequestGuard],
    component: DetailsAmendsComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class FollowUpRoutingModule {}

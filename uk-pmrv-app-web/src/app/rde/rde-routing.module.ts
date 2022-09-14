import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PaymentNotCompletedComponent } from '@shared/components/payment-not-completed/payment-not-completed.component';
import { PaymentCompletedGuard } from '@shared/guards/payment-completed.guard';

import { PendingRequestGuard } from '../core/guards/pending-request.guard';
import { FileDownloadComponent } from '../shared/file-download/file-download.component';
import { ConfirmationComponent as ForceDecisionConfirmationComponent } from './force-decision/confirmation/confirmation.component';
import { ForceDecisionComponent } from './force-decision/force-decision.component';
import { TimelineComponent as ForceDecisionTimeline } from './force-decision/timeline/timeline.component';
import { RdeGuard } from './rde.guard';
import { RdeActionGuard } from './rde-action.guard';
import { ConfirmationComponent as RespondConfirmationComponent } from './respond/confirmation/confirmation.component';
import { ResponsesComponent } from './respond/responses.component';
import { TimelineComponent as RespondTimelineComponent } from './respond/timeline/timeline.component';
import { AnswersComponent } from './submit/answers/answers.component';
import { AnswersGuard } from './submit/answers/answers.guard';
import { ConfirmationComponent } from './submit/confirmation/confirmation.component';
import { ExtendDeterminationComponent } from './submit/extend-determination/extend-determination.component';
import { ExtendDeterminationGuard } from './submit/extend-determination/extend-determination.guard';
import { NotAllowedComponent } from './submit/not-allowed/not-allowed.component';
import { NotifyUsersComponent } from './submit/notify-users/notify-users.component';
import { NotifyUsersGuard } from './submit/notify-users/notify-users.guard';
import { TimelineComponent as SubmitTimelineComponent } from './submit/timeline/timeline.component';

const routes: Routes = [
  {
    path: 'action/:actionId',
    canActivate: [RdeActionGuard],
    canDeactivate: [RdeActionGuard],
    children: [
      {
        path: 'rde-submitted',
        component: SubmitTimelineComponent,
        data: { pageTitle: 'Request for determination extension' },
      },
      {
        path: 'rde-response-submitted',
        component: RespondTimelineComponent,
        data: { pageTitle: 'Response to request for determination extension' },
      },
      {
        path: 'rde-manual-approval-submitted',
        component: ForceDecisionTimeline,
        data: { pageTitle: 'Response to request for determination extension' },
      },
      {
        path: 'file-download/:fileType/:uuid',
        component: FileDownloadComponent,
      },
    ],
  },
  {
    path: ':taskId',
    canActivate: [RdeGuard],
    canDeactivate: [RdeGuard],
    children: [
      {
        path: 'responses',
        component: ResponsesComponent,
        data: { pageTitle: 'Determination extension response' },
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'respond-confirmation',
        component: RespondConfirmationComponent,
        data: { pageTitle: 'Respond to request for determination extension confirmation' },
      },
      {
        path: 'extend-determination',
        data: { pageTitle: 'Determination extension' },
        component: ExtendDeterminationComponent,
        canActivate: [ExtendDeterminationGuard, PaymentCompletedGuard],
      },
      {
        path: 'notify-users',
        data: { pageTitle: 'Determination extension - Notify users' },
        component: NotifyUsersComponent,
        canActivate: [NotifyUsersGuard],
      },
      {
        path: 'answers',
        data: { pageTitle: 'Determination extension - Answers' },
        component: AnswersComponent,
        canActivate: [AnswersGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'confirmation',
        data: { pageTitle: 'Determination extension confirmation' },
        component: ConfirmationComponent,
      },
      {
        path: 'not-allowed',
        data: { pageTitle: 'Determination extension - You can only have one active request' },
        component: NotAllowedComponent,
      },
      {
        path: 'manual-approval',
        data: { pageTitle: 'Determination extension - Force decision' },
        component: ForceDecisionComponent,
      },
      {
        path: 'manual-approval-confirmation',
        component: ForceDecisionConfirmationComponent,
        data: { pageTitle: 'Respond to request for determination extension confirmation' },
      },
      {
        path: 'file-download/:uuid',
        component: FileDownloadComponent,
      },
      {
        path: 'payment-not-completed',
        component: PaymentNotCompletedComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class RdeRoutingModule {}

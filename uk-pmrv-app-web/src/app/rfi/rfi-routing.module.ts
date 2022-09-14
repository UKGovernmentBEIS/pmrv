import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PaymentNotCompletedComponent } from '@shared/components/payment-not-completed/payment-not-completed.component';
import { PaymentCompletedGuard } from '@shared/guards/payment-completed.guard';

import { PendingRequestGuard } from '../core/guards/pending-request.guard';
import { FileDownloadComponent } from '../shared/file-download/file-download.component';
import { ConfirmationComponent as RespondConfirmationComponent } from './respond/confirmation/confirmation.component';
import { ResponsesComponent } from './respond/responses.component';
import { TimelineComponent as RespondTimelineComponent } from './respond/timeline/timeline.component';
import { RfiGuard } from './rfi.guard';
import { RfiActionGuard } from './rfi-action.guard';
import { AnswersComponent } from './submit/answers/answers.component';
import { AnswersGuard } from './submit/answers/answers.guard';
import { ConfirmationComponent as SubmitConfirmationComponent } from './submit/confirmation/confirmation.component';
import { NotAllowedComponent } from './submit/not-allowed/not-allowed.component';
import { NotifyComponent } from './submit/notify/notify.component';
import { NotifyGuard } from './submit/notify/notify.guard';
import { QuestionsComponent } from './submit/questions/questions.component';
import { QuestionsGuard } from './submit/questions/questions.guard';
import { ConfirmationComponent as CancelConfirmationComponent } from './wait/confirmation/confirmation.component';
import { VerifyComponent as CancelVerifyComponent } from './wait/verify/verify.component';
import { WaitComponent } from './wait/wait.component';

const routes: Routes = [
  {
    path: 'action/:actionId',
    canActivate: [RfiActionGuard],
    canDeactivate: [RfiActionGuard],
    children: [
      {
        path: 'rfi-submitted',
        component: AnswersComponent,
        data: { pageTitle: 'Request for information' },
      },
      {
        path: 'rfi-response-submitted',
        component: RespondTimelineComponent,
        data: { pageTitle: 'Response to request for information' },
      },
      {
        path: 'file-download/:fileType/:uuid',
        component: FileDownloadComponent,
      },
    ],
  },
  {
    path: ':taskId',
    canActivate: [RfiGuard],
    canDeactivate: [RfiGuard],
    children: [
      {
        path: 'responses',
        component: ResponsesComponent,
        data: { pageTitle: 'Request for information response' },
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'respond-confirmation',
        component: RespondConfirmationComponent,
        data: { pageTitle: 'Respond to request for information confirmation' },
      },
      {
        path: 'questions',
        component: QuestionsComponent,
        data: { pageTitle: 'Request for information' },
        canActivate: [QuestionsGuard, PaymentCompletedGuard],
      },
      {
        path: 'notify',
        component: NotifyComponent,
        data: { pageTitle: 'Request for information notify' },
        canActivate: [NotifyGuard],
      },
      {
        path: 'answers',
        component: AnswersComponent,
        data: { pageTitle: 'Request for information answers' },
        canActivate: [AnswersGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'submit-confirmation',
        component: SubmitConfirmationComponent,
        data: { pageTitle: 'Request for information confirmation' },
      },
      {
        path: 'wait',
        component: WaitComponent,
        data: { pageTitle: 'Request for information' },
      },
      {
        path: 'cancel-verify',
        component: CancelVerifyComponent,
        data: { pageTitle: 'Request for information verify cancel' },
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'cancel-confirmation',
        component: CancelConfirmationComponent,
        data: { pageTitle: 'Request for information confirmation' },
      },
      {
        path: 'file-download/:uuid',
        component: FileDownloadComponent,
      },
      {
        path: 'not-allowed',
        component: NotAllowedComponent,
        data: { pageTitle: 'Request for information not allowed' },
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
export class RfiRoutingModule {}

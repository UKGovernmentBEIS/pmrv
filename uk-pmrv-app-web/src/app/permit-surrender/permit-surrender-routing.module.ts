import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { FileDownloadComponent } from '@shared/file-download/file-download.component';

import { PermitSurrenderRoute } from './core/permit-surrender-route.interface';
import { PermitSurrenderActionGuard } from './permit-surrender-action.guard';
import { AnswersComponent } from './permit-surrender-apply/answers/answers.component';
import { AnswersGuard } from './permit-surrender-apply/answers/answers.guard';
import { JustificationComponent } from './permit-surrender-apply/justification/justification.component';
import { JustificationGuard } from './permit-surrender-apply/justification/justification.guard';
import { StopDateComponent } from './permit-surrender-apply/stop-date/stop-date.component';
import { StopDateGuard } from './permit-surrender-apply/stop-date/stop-date.guard';
import { SummaryComponent } from './permit-surrender-apply/summary/summary.component';
import { SummaryGuard } from './permit-surrender-apply/summary/summary.guard';
import { SupportDocumentsComponent } from './permit-surrender-apply/support-documents/support-documents.component';
import { SupportDocumentsGuard } from './permit-surrender-apply/support-documents/support-documents.guard';
import { UploadDocumentsComponent } from './permit-surrender-apply/upload-documents/upload-documents.component';
import { UploadDocumentsGuard } from './permit-surrender-apply/upload-documents/upload-documents.guard';
import { DeterminationSubmittedComponent } from './permit-surrender-determination-submitted/determination-submitted.component';
import { SubmitComponent } from './permit-surrender-submit/submit.component';
import { SubmittedComponent } from './permit-surrender-submitted/submitted.component';
import { PermitSurrenderTaskGuard } from './permit-surrender-task.guard';
import { PermitSurrenderTaskListComponent } from './permit-surrender-task-list/permit-surrender-task-list.component';

const commonRoutes: PermitSurrenderRoute[] = [
  {
    path: 'review',
    loadChildren: () => import('./review/review.module').then((m) => m.ReviewModule),
  },
  {
    path: 'cessation',
    loadChildren: () => import('./cessation/cessation.module').then((m) => m.CessationModule),
  },
];

const taskRoutes: PermitSurrenderRoute[] = [
  {
    path: '',
    data: { pageTitle: 'Permit surrender task list' },
    component: PermitSurrenderTaskListComponent,
  },
  ...commonRoutes,
  {
    path: 'file-download/:uuid',
    component: FileDownloadComponent,
  },
  {
    path: 'apply',
    data: { statusKey: 'SURRENDER_APPLY' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Surrender permit request task - Stop date' },
        component: StopDateComponent,
        canActivate: [StopDateGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'justification',
        data: { pageTitle: 'Surrender permit request task - Justification' },
        component: JustificationComponent,
        canActivate: [JustificationGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'support-documents',
        data: { pageTitle: 'Surrender permit request task - Supporting documents' },
        component: SupportDocumentsComponent,
        canActivate: [SupportDocumentsGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'upload-documents',
        data: { pageTitle: 'Surrender permit request task - Upload documents' },
        component: UploadDocumentsComponent,
        canActivate: [UploadDocumentsGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'answers',
        data: { pageTitle: 'Surrender permit request task - Confirm answers' },
        component: AnswersComponent,
        canActivate: [AnswersGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Surrender permit request task - Summary' },
        component: SummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'submit',
    data: { statusKey: 'SURRENDER_SUBMIT' },
    component: SubmitComponent,
    canDeactivate: [PendingRequestGuard],
  },
];

const actionRoutes: PermitSurrenderRoute[] = [
  ...commonRoutes,
  {
    path: 'file-download/:fileType/:uuid',
    component: FileDownloadComponent,
  },
  {
    path: 'surrender-submitted',
    data: { pageTitle: 'Surrender permit submitted' },
    component: SubmittedComponent,
  },
  {
    path: 'determination-submitted',
    data: { pageTitle: 'Surrender determintation submitted' },
    component: DeterminationSubmittedComponent,
  },
];

const routes: PermitSurrenderRoute[] = [
  {
    path: ':taskId',
    canActivate: [PermitSurrenderTaskGuard],
    canDeactivate: [PermitSurrenderTaskGuard],
    children: taskRoutes,
  },
  {
    path: 'action/:actionId',
    canActivate: [PermitSurrenderActionGuard],
    canDeactivate: [PermitSurrenderActionGuard],
    children: actionRoutes,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PermitSurrenderRoutingModule {}

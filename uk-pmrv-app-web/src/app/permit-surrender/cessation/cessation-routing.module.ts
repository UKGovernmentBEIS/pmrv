import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '../../core/guards/pending-request.guard';
import { CessationComponent } from './cessation.component';
import { CompletedComponent } from './completed/completed.component';
import { AllowancesDateComponent } from './confirm/allowances-date/allowances-date.component';
import { AllowancesDateGuard } from './confirm/allowances-date/allowances-date.guard';
import { AllowancesNumberComponent } from './confirm/allowances-number/allowances-number.component';
import { AllowancesNumberGuard } from './confirm/allowances-number/allowances-number.guard';
import { AnswersComponent } from './confirm/answers/answers.component';
import { AnswersGuard } from './confirm/answers/answers.guard';
import { EmissionsComponent } from './confirm/emissions/emissions.component';
import { EmissionsGuard } from './confirm/emissions/emissions.guard';
import { NotesComponent } from './confirm/notes/notes.component';
import { NotesGuard } from './confirm/notes/notes.guard';
import { NoticeComponent } from './confirm/notice/notice.component';
import { NoticeGuard } from './confirm/notice/notice.guard';
import { OutcomeComponent } from './confirm/outcome/outcome.component';
import { OutcomeGuard } from './confirm/outcome/outcome.guard';
import { RefundComponent } from './confirm/refund/refund.component';
import { RefundGuard } from './confirm/refund/refund.guard';
import { SummaryComponent } from './confirm/summary/summary.component';
import { SummaryGuard } from './confirm/summary/summary.guard';
import { NotifyOperatorComponent } from './notify-operator/notify-operator.component';
import { NotifyOperatorGuard } from './notify-operator/notify-operator.guard';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Surrender cessation' },
    component: CessationComponent,
  },
  {
    path: 'confirm',
    data: { pageTitle: 'Surrender cessation confirm' },
    children: [
      {
        path: 'outcome',
        data: { pageTitle: 'Surrender cessation confirm - outcome' },
        component: OutcomeComponent,
        canActivate: [OutcomeGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'allowances-date',
        data: { pageTitle: 'Surrender cessation confirm - allowances date' },
        component: AllowancesDateComponent,
        canActivate: [AllowancesDateGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'allowances-number',
        data: { pageTitle: 'Surrender cessation confirm - number of allowances' },
        component: AllowancesNumberComponent,
        canActivate: [AllowancesNumberGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'emissions',
        data: { pageTitle: 'Surrender cessation confirm - emissions' },
        component: EmissionsComponent,
        canActivate: [EmissionsGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'refund',
        data: { pageTitle: 'Surrender cessation confirm - refund' },
        component: RefundComponent,
        canActivate: [RefundGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'notice',
        data: { pageTitle: 'Surrender cessation confirm - notice' },
        component: NoticeComponent,
        canActivate: [NoticeGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'notes',
        data: { pageTitle: 'Surrender cessation confirm - notes' },
        component: NotesComponent,
        canActivate: [NotesGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'answers',
        data: { pageTitle: 'Surrender cessation confirm - Answers' },
        component: AnswersComponent,
        canActivate: [AnswersGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Surrender cessation confirm - Summary' },
        component: SummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'notify-operator',
    data: { pageTitle: 'Surrender cessation - Notify operator' },
    component: NotifyOperatorComponent,
    canActivate: [NotifyOperatorGuard],
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'completed',
    data: { pageTitle: 'Cessation completed' },
    component: CompletedComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CessationRoutingModule {}

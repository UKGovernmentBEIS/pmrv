import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { TaskStatusPipe } from './core/task-status.pipe';
import { AnswersComponent } from './permit-surrender-apply/answers/answers.component';
import { JustificationComponent } from './permit-surrender-apply/justification/justification.component';
import { StopDateComponent } from './permit-surrender-apply/stop-date/stop-date.component';
import { SummaryComponent } from './permit-surrender-apply/summary/summary.component';
import { SummaryDetailsComponent } from './permit-surrender-apply/summary/summary-details.component';
import { SupportDocumentsComponent } from './permit-surrender-apply/support-documents/support-documents.component';
import { UploadDocumentsComponent } from './permit-surrender-apply/upload-documents/upload-documents.component';
import { DeterminationSubmittedComponent } from './permit-surrender-determination-submitted/determination-submitted.component';
import { PermitSurrenderRoutingModule } from './permit-surrender-routing.module';
import { SubmitComponent } from './permit-surrender-submit/submit.component';
import { SubmittedComponent } from './permit-surrender-submitted/submitted.component';
import { PermitSurrenderTaskListComponent } from './permit-surrender-task-list/permit-surrender-task-list.component';
import { SharedPermitSurrenderModule } from './shared/shared-permit-surrender.module';

@NgModule({
  declarations: [
    AnswersComponent,
    DeterminationSubmittedComponent,
    JustificationComponent,
    PermitSurrenderTaskListComponent,
    StopDateComponent,
    SubmitComponent,
    SubmittedComponent,
    SummaryComponent,
    SummaryDetailsComponent,
    SupportDocumentsComponent,
    TaskStatusPipe,
    UploadDocumentsComponent,
  ],
  imports: [PermitSurrenderRoutingModule, SharedModule, SharedPermitSurrenderModule],
})
export class PermitSurrenderModule {}

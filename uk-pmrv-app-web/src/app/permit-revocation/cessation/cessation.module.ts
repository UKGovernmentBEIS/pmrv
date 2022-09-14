import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { CessationComponent } from './cessation.component';
import { CessationRoutingModule } from './cessation-routing.module';
import { AllowancesDateComponent } from './confirm/allowances-date/allowances-date.component';
import { AllowancesNumberComponent } from './confirm/allowances-number/allowances-number.component';
import { AnswersComponent } from './confirm/answers/answers.component';
import { CompletedComponent } from './confirm/completed/completed.component';
import { EmissionsComponent } from './confirm/emissions/emissions.component';
import { NotesComponent } from './confirm/notes/notes.component';
import { NoticeComponent } from './confirm/notice/notice.component';
import { OutcomeComponent } from './confirm/outcome/outcome.component';
import { RefundComponent } from './confirm/refund/refund.component';
import { SummaryComponent } from './confirm/summary/summary.component';
import { SummaryDetailsComponent } from './confirm/summary/summary-details.component';

@NgModule({
  declarations: [
    AllowancesDateComponent,
    AllowancesNumberComponent,
    AnswersComponent,
    CessationComponent,
    CompletedComponent,
    EmissionsComponent,
    NotesComponent,
    NoticeComponent,
    OutcomeComponent,
    RefundComponent,
    SummaryComponent,
    SummaryDetailsComponent,
  ],
  imports: [CessationRoutingModule, SharedModule],
})
export class CessationModule {}

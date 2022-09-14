import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { SharedPermitSurrenderModule } from '../shared/shared-permit-surrender.module';
import { CessationComponent } from './cessation.component';
import { CessationRoutingModule } from './cessation-routing.module';
import { CompletedComponent } from './completed/completed.component';
import { AllowancesDateComponent } from './confirm/allowances-date/allowances-date.component';
import { AllowancesNumberComponent } from './confirm/allowances-number/allowances-number.component';
import { AnswersComponent } from './confirm/answers/answers.component';
import { EmissionsComponent } from './confirm/emissions/emissions.component';
import { NotesComponent } from './confirm/notes/notes.component';
import { NoticeComponent } from './confirm/notice/notice.component';
import { OutcomeComponent } from './confirm/outcome/outcome.component';
import { RefundComponent } from './confirm/refund/refund.component';
import { SummaryComponent } from './confirm/summary/summary.component';
import { SummaryDetailsComponent } from './confirm/summary/summary-details.component';
import { NotifyOperatorComponent } from './notify-operator/notify-operator.component';

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
    NotifyOperatorComponent,
    OutcomeComponent,
    RefundComponent,
    SummaryComponent,
    SummaryDetailsComponent,
  ],
  imports: [CessationRoutingModule, SharedModule, SharedPermitSurrenderModule],
})
export class CessationModule {}

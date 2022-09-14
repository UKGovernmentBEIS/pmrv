import { NgModule } from '@angular/core';

import { SharedModule } from '../shared/shared.module';
import { ConfirmationComponent as RespondConfirmationComponent } from './respond/confirmation/confirmation.component';
import { ResponsesComponent } from './respond/responses.component';
import { TimelineComponent as RespondTimelineComponent } from './respond/timeline/timeline.component';
import { RfiRoutingModule } from './rfi-routing.module';
import { ReturnLinkComponent } from './shared/return-link.component';
import { AnswersComponent } from './submit/answers/answers.component';
import { ConfirmationComponent as SubmitConfirmationComponent } from './submit/confirmation/confirmation.component';
import { NotAllowedComponent } from './submit/not-allowed/not-allowed.component';
import { NotifyComponent } from './submit/notify/notify.component';
import { QuestionsComponent } from './submit/questions/questions.component';
import { ConfirmationComponent as CancelConfirmationComponent } from './wait/confirmation/confirmation.component';
import { VerifyComponent as CancelVerifyComponent } from './wait/verify/verify.component';
import { WaitComponent } from './wait/wait.component';

@NgModule({
  declarations: [
    AnswersComponent,
    CancelConfirmationComponent,
    CancelVerifyComponent,
    NotAllowedComponent,
    NotifyComponent,
    QuestionsComponent,
    RespondConfirmationComponent,
    RespondTimelineComponent,
    ResponsesComponent,
    ReturnLinkComponent,
    SubmitConfirmationComponent,
    WaitComponent,
  ],
  imports: [RfiRoutingModule, SharedModule],
})
export class RfiModule {}

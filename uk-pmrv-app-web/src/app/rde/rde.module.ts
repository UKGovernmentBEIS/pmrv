import { NgModule } from '@angular/core';

import { SharedModule } from '../shared/shared.module';
import { ConfirmationComponent as ForceDecisionConfirmationComponent } from './force-decision/confirmation/confirmation.component';
import { ForceDecisionComponent } from './force-decision/force-decision.component';
import { TimelineComponent as ForceDecisionTimeline } from './force-decision/timeline/timeline.component';
import { RdeRoutingModule } from './rde-routing.module';
import { ConfirmationComponent as RespondConfirmationComponent } from './respond/confirmation/confirmation.component';
import { ResponsesComponent } from './respond/responses.component';
import { TimelineComponent as RespondTimelineComponent } from './respond/timeline/timeline.component';
import { ReturnLinkComponent } from './shared/return-link.component';
import { AnswersComponent } from './submit/answers/answers.component';
import { AnswersTemplateComponent } from './submit/answers/answers-template.component';
import { ConfirmationComponent } from './submit/confirmation/confirmation.component';
import { ExtendDeterminationComponent } from './submit/extend-determination/extend-determination.component';
import { NotAllowedComponent } from './submit/not-allowed/not-allowed.component';
import { NotifyUsersComponent } from './submit/notify-users/notify-users.component';
import { TimelineComponent as SubmitTimelineComponent } from './submit/timeline/timeline.component';
@NgModule({
  declarations: [
    AnswersComponent,
    AnswersTemplateComponent,
    ConfirmationComponent,
    ExtendDeterminationComponent,
    ForceDecisionComponent,
    ForceDecisionConfirmationComponent,
    ForceDecisionTimeline,
    NotAllowedComponent,
    NotifyUsersComponent,
    RespondConfirmationComponent,
    RespondTimelineComponent,
    ResponsesComponent,
    ReturnLinkComponent,
    SubmitTimelineComponent,
  ],
  imports: [RdeRoutingModule, SharedModule],
})
export class RdeModule {}

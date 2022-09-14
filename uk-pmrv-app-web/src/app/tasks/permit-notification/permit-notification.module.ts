import { NgModule } from '@angular/core';

import { PermitNotificationSharedModule } from '@shared/components/permit-notification/permit-notification-shared.module';

import { SharedModule } from '../../shared/shared.module';
import { TaskSharedModule } from '../shared/task-shared-module';
import { PeerReviewComponent } from './peer-review/peer-review.component';
import { PeerReviewWaitComponent } from './peer-review-wait/peer-review-wait.component';
import { PermitNotificationRoutingModule } from './permit-notification-routing.module';
import { DecisionComponent } from './review/decision/decision.component';
import { NotifyOperatorComponent } from './review/notify-operator/notify-operator.component';
import { ReviewComponent } from './review/review.component';
import { ReviewWaitComponent } from './review-wait/review-wait.component';
import { NonSignificantChangeComponent } from './shared/components/non-significant-change/non-significant-change.component';
import { OtherFactorComponent } from './shared/components/other-factor/other-factor.component';
import { TemporaryChangeComponent } from './shared/components/temporary-change/temporary-change.component';
import { TemporaryFactorComponent } from './shared/components/temporary-factor/temporary-factor.component';
import { TemporarySuspensionComponent } from './shared/components/temporary-suspension/temporary-suspension.component';
import { NotificationTypeDescriptionPipe } from './shared/pipes/notification-type-description.pipe';
import { AnswersComponent } from './submit/answers/answers.component';
import { DescriptionComponent } from './submit/description/description.component';
import { DetailsOfChangeComponent } from './submit/details-of-change/details-of-change.component';
import { SubmitContainerComponent as SubmitNotificationContainerComponent } from './submit/submit/submit-container.component';
import { SubmitContainerComponent } from './submit/submit-container.component';
import { SummaryComponent } from './submit/summary/summary.component';

@NgModule({
  declarations: [
    AnswersComponent,
    DecisionComponent,
    DescriptionComponent,
    DetailsOfChangeComponent,
    NonSignificantChangeComponent,
    NotificationTypeDescriptionPipe,
    NotifyOperatorComponent,
    OtherFactorComponent,
    PeerReviewComponent,
    PeerReviewWaitComponent,
    ReviewComponent,
    ReviewWaitComponent,
    SubmitContainerComponent,
    SubmitNotificationContainerComponent,
    SummaryComponent,
    TemporaryChangeComponent,
    TemporaryFactorComponent,
    TemporarySuspensionComponent,
  ],
  imports: [PermitNotificationRoutingModule, PermitNotificationSharedModule, SharedModule, TaskSharedModule],
})
export class PermitNotificationModule {}

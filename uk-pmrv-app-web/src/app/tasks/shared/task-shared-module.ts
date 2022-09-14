import { NgModule } from '@angular/core';

import { ReviewNotificationStatusPipe } from '@permit-notification/shared/pipes/review-notification-status.pipe';
import { SectionStatusPipe } from '@permit-notification/shared/pipes/section-status.pipe';
import { SharedModule } from '@shared/shared.module';

import { BaseTaskContainerComponent } from './components/base-task-container-component/base-task-container.component';
import { TaskLayoutComponent } from './components/task-layout/task-layout.component';

@NgModule({
  declarations: [
    BaseTaskContainerComponent,
    ReviewNotificationStatusPipe,
    SectionStatusPipe,
    TaskLayoutComponent,
  ],
  exports: [
    BaseTaskContainerComponent,
    ReviewNotificationStatusPipe,
    SectionStatusPipe,
    TaskLayoutComponent,
  ],
  imports: [SharedModule],
})
export class TaskSharedModule {}

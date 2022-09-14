import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { ChangeAssigneeComponent } from "@tasks/change-assignee/change-assignee.component";

import { CancelComponent } from './cancel/cancel.component';
import { ConfirmationComponent } from './cancel/confirmation/confirmation.component';
import { TaskComponent } from './task.component';
import { TasksRoutingModule } from './tasks-routing.module';

@NgModule({
  declarations: [CancelComponent, ChangeAssigneeComponent, ConfirmationComponent, TaskComponent],
  imports: [SharedModule, TasksRoutingModule],
})
export class TasksModule {}

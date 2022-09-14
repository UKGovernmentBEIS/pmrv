import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { TaskInfoGuard } from '../shared/guards/task-info.guard';
import { SystemNotificationComponent } from './system-notification/system-notification.component';

const routes: Routes = [
  {
    path: ':taskId',
    component: SystemNotificationComponent,
    canActivate: [TaskInfoGuard],
    resolve: { review: TaskInfoGuard },
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class MessageRoutingModule {}

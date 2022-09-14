import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { FileDownloadComponent } from '@shared/file-download/file-download.component';
import { ChangeAssigneeComponent } from "@tasks/change-assignee/change-assignee.component";

import { CancelComponent } from './cancel/cancel.component';
import { ConfirmationComponent } from './cancel/confirmation/confirmation.component';
import { TaskComponent } from './task.component';
import { TaskGuard } from './task.guard';

const routes: Routes = [
  { path: '', pathMatch: 'full', component: TaskComponent },
  {
    path: ':taskId',
    component: TaskComponent,
    canActivate: [TaskGuard],
    canDeactivate: [TaskGuard],
    children: [
      {
        path: 'permit-notification',
        loadChildren: () =>
          import('./permit-notification/permit-notification.module').then((m) => m.PermitNotificationModule),
      },
      {
        path: 'permit-variation',
        loadChildren: () => import('./permit-variation/permit-variation.module').then((m) => m.PermitVariationModule),
      },
      {
        path: 'aer',
        loadChildren: () => import('./aer/aer.module').then((m) => m.AerModule),
      },
      {
        path: 'change-assignee',
        data: { pageTitle: 'Change assignee' },
        component: ChangeAssigneeComponent,
      },
      {
        path: 'cancel',
        children: [
          {
            path: '',
            component: CancelComponent,
            data: { pageTitle: 'Task cancellation' },
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'confirmation',
            component: ConfirmationComponent,
            data: { pageTitle: 'Task cancellation confirmation' },
          },
        ],
      },
      {
        path: 'file-download/:uuid',
        component: FileDownloadComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TasksRoutingModule {}

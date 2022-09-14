import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { FileDownloadComponent } from '@shared/file-download/file-download.component';

import { ActionComponent } from './action.component';
import { ActionGuard } from './action.guard';

const routes: Routes = [
  { path: '', pathMatch: 'full', component: ActionComponent },
  {
    path: ':actionId',
    component: ActionComponent,
    canActivate: [ActionGuard],
    children: [
      {
        path: 'permit-notification',
        loadChildren: () =>
          import('./permit-notification/permit-notification.module').then((m) => m.PermitNotificationModule),
      },
      {
        path: 'file-download/:fileType/:uuid',
        component: FileDownloadComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ActionsRoutingModule {}

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '../core/guards/pending-request.guard';
import { DeleteComponent } from './delete/delete.component';
import { DeleteGuard } from './delete/delete.guard';
import { DetailsComponent } from './details/details.component';
import { DetailsGuard } from './details/details.guard';
import { VerifiersComponent } from './verifiers.component';
import { VerifiersGuard } from './verifiers.guard';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Manage verifier users' },
    component: VerifiersComponent,
    resolve: { verifiers: VerifiersGuard },
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'add',
    data: { pageTitle: 'Add a new user' },
    component: DetailsComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: ':userId',
    children: [
      {
        path: 'delete',
        data: { pageTitle: 'Confirm that this user account will be deleted' },
        component: DeleteComponent,
        canActivate: [DeleteGuard],
        canDeactivate: [PendingRequestGuard],
        resolve: { user: DeleteGuard },
      },
      {
        path: '',
        pathMatch: 'full',
        data: { pageTitle: 'User details' },
        component: DetailsComponent,
        canActivate: [DetailsGuard],
        canDeactivate: [PendingRequestGuard],
        resolve: { user: DetailsGuard },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class VerifiersRoutingModule {}

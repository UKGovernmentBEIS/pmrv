import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '../core/guards/pending-request.guard';
import { AddComponent } from './add/add.component';
import { AddComponent as ContactsAddComponent } from './contacts/add/add.component';
import { AddGuard } from './contacts/add/add.guard';
import { DeleteComponent } from './delete/delete.component';
import { DeleteGuard } from './delete/delete.guard';
import { DetailsComponent } from './details/details.component';
import { DetailsGuard } from './details/details.guard';
import { VerificationBodiesComponent } from './verification-bodies.component';
import { VerificationBodiesResolver } from './verification-bodies.resolver';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Manage verification bodies' },
    component: VerificationBodiesComponent,
    resolve: { verificationBodies: VerificationBodiesResolver },
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: 'add',
    data: { pageTitle: 'Verification body details - Add a verification body' },
    component: AddComponent,
    canDeactivate: [PendingRequestGuard],
  },
  {
    path: ':verificationBodyId',
    children: [
      {
        path: '',
        pathMatch: 'full',
        component: DetailsComponent,
        canActivate: [DetailsGuard],
        resolve: { verificationBody: DetailsGuard },
      },
      {
        path: 'delete',
        data: { pageTitle: 'Confirm that this verification body will be deleted' },
        component: DeleteComponent,
        canActivate: [DeleteGuard],
      },
      {
        path: 'add-contact',
        data: { pageTitle: 'Add a new verifier admin' },
        component: ContactsAddComponent,
        canActivate: [AddGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class VerificationBodiesRoutingModule {}

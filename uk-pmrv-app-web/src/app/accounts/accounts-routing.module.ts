import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '../core/guards/pending-request.guard';
import { AccountComponent } from './account.component';
import { AccountGuard } from './account.guard';
import { AccountsComponent } from './accounts.component';
import { AddressComponent } from './edit/address/address.component';
import { OperatorAddressComponent } from './edit/operator-address/operator-address.component';
import { OperatorNameComponent } from './edit/operator-name/operator-name.component';
import { RegistryIdComponent } from './edit/registry-id/registry-id.component';
import { SiteNameComponent } from './edit/site-name/site-name.component';
import { SopIdComponent } from './edit/sop-id/sop-id.component';
import { UserRoleTypeGuard } from './edit/user-role-type.guard';
import { AccountStatusGuard } from './operators/add/account-status.guard';
import { AddComponent as OperatorAddComponent } from './operators/add/add.component';
import { DeleteComponent as OperatorDeleteComponent } from './operators/delete/delete.component';
import { DeleteGuard as OperatorDeleteGuard } from './operators/delete/delete.guard';
import { DetailsComponent as OperatorDetailsComponent } from './operators/details/details.component';
import { DetailsGuard as OperatorDetailsGuard } from './operators/details/details.guard';
import { OperatorsGuard } from './operators/operators.guard';
import { AppointComponent } from './operators/verification-body/appoint/appoint.component';
import { AppointGuard } from './operators/verification-body/appoint/appoint.guard';
import { ReplaceGuard } from './operators/verification-body/appoint/replace.guard';
import { ProcessActionsComponent } from './process-actions/process-actions.component';
import { WorkflowItemComponent } from './workflows/item/workflow-item.component';

const routes: Routes = [
  {
    path: '',
    component: AccountsComponent,
  },
  {
    path: ':accountId',
    canActivate: [AccountGuard],
    canDeactivate: [AccountGuard],
    children: [
      {
        path: '',
        data: { pageTitle: 'Account' },
        component: AccountComponent,
        canDeactivate: [PendingRequestGuard],
        resolve: { account: AccountGuard, users: OperatorsGuard },
      },
      {
        path: 'verification-body',
        children: [
          {
            path: 'appoint',
            data: { pageTitle: 'Users, contacts and verifiers - Appoint a verifier' },
            component: AppointComponent,
            canActivate: [AppointGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'replace',
            data: { pageTitle: 'Users, contacts and verifiers - Replace a verifier' },
            component: AppointComponent,
            canActivate: [ReplaceGuard],
            canDeactivate: [PendingRequestGuard],
            resolve: { verificationBody: ReplaceGuard },
          },
        ],
      },
      {
        path: 'users',
        children: [
          {
            path: ':userId',
            children: [
              {
                path: '',
                pathMatch: 'full',
                data: { pageTitle: 'User details' },
                component: OperatorDetailsComponent,
                canActivate: [OperatorDetailsGuard],
                canDeactivate: [PendingRequestGuard],
                resolve: { user: OperatorDetailsGuard },
              },
              {
                path: 'delete',
                data: { pageTitle: 'Confirm that this user account will be deleted' },
                component: OperatorDeleteComponent,
                canActivate: [OperatorDeleteGuard],
                canDeactivate: [PendingRequestGuard],
                resolve: { user: OperatorDeleteGuard },
              },
            ],
          },
          {
            path: 'add/:userType',
            data: { pageTitle: 'Users, contacts and verifiers - Add user' },
            component: OperatorAddComponent,
            canActivate: [AccountStatusGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'edit',
        resolve: { account: AccountGuard },
        children: [
          {
            path: 'registry-id',
            data: { pageTitle: 'Edit UK ETS Registry ID' },
            component: RegistryIdComponent,
            canActivate: [UserRoleTypeGuard, AccountStatusGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'sop-id',
            data: { pageTitle: 'Edit SOP ID' },
            component: SopIdComponent,
            canActivate: [UserRoleTypeGuard, AccountStatusGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'site-name',
            data: { pageTitle: 'Edit site name' },
            component: SiteNameComponent,
            canActivate: [UserRoleTypeGuard, AccountStatusGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'address',
            data: { pageTitle: 'Edit address' },
            component: AddressComponent,
            canActivate: [UserRoleTypeGuard, AccountStatusGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'operator-address',
            data: { pageTitle: 'Edit operator address' },
            component: OperatorAddressComponent,
            canActivate: [AccountStatusGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'operator-name',
            data: { pageTitle: 'Edit operator name' },
            component: OperatorNameComponent,
            canActivate: [UserRoleTypeGuard, AccountStatusGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'process-actions',
        data: { pageTitle: 'Account process actions' },
        component: ProcessActionsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'workflows/:request-id',
        data: { pageTitle: 'Workflow item' },
        component: WorkflowItemComponent,
        resolve: { account: AccountGuard },
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AccountsRoutingModule {}

import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { AccountsUsersContactsComponent } from './accounts-users-contacts/accounts-users-contacts.component';
import { CompletedWorkComponent } from './completed-work/completed-work.component';
import { MiReportsListGuard } from './core/mi-reports-list.guard';
import { MiReportsComponent } from './mi-reports.component';

const routes: Routes = [
  {
    path: '',
    component: MiReportsComponent,
    canActivate: [MiReportsListGuard],
    resolve: { miReports: MiReportsListGuard },
  },
  {
    path: 'accounts-users-contacts',
    component: AccountsUsersContactsComponent,
  },
  {
    path: 'completed-work',
    component: CompletedWorkComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class MiReportsRoutingModule {}

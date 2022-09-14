import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { AccountsUsersContactsComponent } from './accounts-users-contacts/accounts-users-contacts.component';
import { CompletedWorkComponent } from './completed-work/completed-work.component';
import { MiReportsComponent } from './mi-reports.component';
import { MiReportsRoutingModule } from './mi-reports-routing.module';

@NgModule({
  declarations: [AccountsUsersContactsComponent, CompletedWorkComponent, MiReportsComponent],
  imports: [CommonModule, MiReportsRoutingModule, RouterModule, SharedModule],
})
export class MiReportsModule {}

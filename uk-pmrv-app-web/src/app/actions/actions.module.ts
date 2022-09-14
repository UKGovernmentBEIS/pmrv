import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { ActionComponent } from './action.component';
import { ActionsRoutingModule } from './actions-routing.module';

@NgModule({
  declarations: [ActionComponent],
  imports: [ActionsRoutingModule, SharedModule],
})
export class ActionsModule {}

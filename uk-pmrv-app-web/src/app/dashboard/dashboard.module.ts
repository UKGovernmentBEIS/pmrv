import { NgModule } from '@angular/core';

import { SharedModule } from '../shared/shared.module';
import { DashboardComponent } from './dashboard.component';
import { DashboardRoutingModule } from './dashboard-routing.module';
import { ItemTypePipe } from './item-type.pipe';

@NgModule({
  imports: [DashboardRoutingModule, SharedModule],
  declarations: [DashboardComponent, ItemTypePipe],
})
export class DashboardModule {}

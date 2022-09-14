import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { DescriptionComponent } from './description/description.component';
import { SummaryComponent } from './description/summary/summary.component';
import { InherentCO2Component } from './inherent-co2.component';
import { InherentCo2RoutingModule } from './inherent-co2-routing.module';

@NgModule({
  declarations: [DescriptionComponent, InherentCO2Component, SummaryComponent],
  imports: [InherentCo2RoutingModule, SharedModule, SharedPermitModule],
})
export class InherentCo2Module {}

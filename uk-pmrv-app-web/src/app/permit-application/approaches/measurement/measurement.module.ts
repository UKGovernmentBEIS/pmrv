import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { AppliedStandardComponent } from './category-tier/applied-standard/applied-standard.component';
import { SummaryComponent } from './category-tier/applied-standard/summary/summary.component';
import { CategoryComponent } from './category-tier/category/category.component';
import { CategorySummaryComponent } from './category-tier/category/summary/category-summary.component';
import { CategorySummaryOverviewComponent } from './category-tier/category/summary/category-summary-overview.component';
import { CategoryTierComponent } from './category-tier/category-tier.component';
import { DeleteComponent } from './category-tier/delete/delete.component';
import { DescriptionComponent } from './description/description.component';
import { HintComponent } from './description/hint.component';
import { SummaryComponent as DescriptionSummaryComponent } from './description/summary/summary.component';
import { MeasurementComponent } from './measurement.component';
import { MeasurementRoutingModule } from './measurement-routing.module';
import { OptionalComponent } from './optional/optional.component';
import { SummaryComponent as OptionalSummaryComponent } from './optional/summary/summary.component';
import { ProcedureComponent } from './procedure/procedure.component';
import { SummaryComponent as ProcedureSummaryComponent } from './procedure/summary/summary.component';

@NgModule({
  declarations: [
    AppliedStandardComponent,
    CategoryComponent,
    CategorySummaryComponent,
    CategorySummaryOverviewComponent,
    CategoryTierComponent,
    DeleteComponent,
    DescriptionComponent,
    DescriptionSummaryComponent,
    HintComponent,
    MeasurementComponent,
    OptionalComponent,
    OptionalSummaryComponent,
    ProcedureComponent,
    ProcedureSummaryComponent,
    SummaryComponent,
  ],
  imports: [MeasurementRoutingModule, SharedModule, SharedPermitModule],
})
export class MeasurementModule {}

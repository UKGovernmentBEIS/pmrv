import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { AppliedStandardComponent } from './category-tier/applied-standard/applied-standard.component';
import { SummaryComponent as AppliedStandardSummaryComponent } from './category-tier/applied-standard/summary/summary.component';
import { CategoryComponent } from './category-tier/category/category.component';
import { CategorySummaryComponent } from './category-tier/category/summary/category-summary.component';
import { CategorySummaryOverviewComponent } from './category-tier/category/summary/category-summary-overview.component';
import { CategoryTierComponent } from './category-tier/category-tier.component';
import { DeleteComponent } from './category-tier/delete/delete.component';
import { DescriptionComponent } from './description/description.component';
import { SummaryComponent as DescriptionSummaryComponent } from './description/summary/summary.component';
import { GasComponent } from './gas/gas.component';
import { SummaryComponent as GasSummaryComponent } from './gas/summary/summary.component';
import { N2oComponent } from './n2o.component';
import { N2oRoutingModule } from './n2o-routing.module';
import { ProcedureComponent } from './procedure/procedure.component';
import { SummaryComponent as ProcedureSummaryComponent } from './procedure/summary/summary.component';
import { TemplateComponent } from './procedure/template.component';

@NgModule({
  declarations: [
    AppliedStandardComponent,
    AppliedStandardSummaryComponent,
    CategoryComponent,
    CategorySummaryComponent,
    CategorySummaryOverviewComponent,
    CategoryTierComponent,
    DeleteComponent,
    DescriptionComponent,
    DescriptionSummaryComponent,
    GasComponent,
    GasSummaryComponent,
    N2oComponent,
    ProcedureComponent,
    ProcedureSummaryComponent,
    TemplateComponent,
  ],
  imports: [N2oRoutingModule, SharedModule, SharedPermitModule],
})
export class N2oModule {}

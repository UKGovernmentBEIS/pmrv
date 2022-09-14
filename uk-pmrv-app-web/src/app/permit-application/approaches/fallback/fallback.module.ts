import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { CategoryComponent } from './category-tier/category/category.component';
import { CategorySummaryComponent } from './category-tier/category/summary/category-summary.component';
import { CategorySummaryOverviewComponent } from './category-tier/category/summary/category-summary-overview.component';
import { CategoryTierComponent } from './category-tier/category-tier.component';
import { DeleteComponent } from './category-tier/delete/delete.component';
import { DescriptionComponent } from './description/description.component';
import { SummaryComponent as DescriptionSummaryComponent } from './description/summary/summary.component';
import { FallbackComponent } from './fallback.component';
import { FallbackRoutingModule } from './fallback-routing.module';
import { SummaryComponent as UncertaintySummaryComponent } from './uncertainty/summary/summary.component';
import { UncertaintyComponent } from './uncertainty/uncertainty.component';
@NgModule({
  declarations: [
    CategoryComponent,
    CategorySummaryComponent,
    CategorySummaryOverviewComponent,
    CategoryTierComponent,
    DeleteComponent,
    DescriptionComponent,
    DescriptionSummaryComponent,
    FallbackComponent,
    UncertaintyComponent,
    UncertaintySummaryComponent,
  ],
  imports: [FallbackRoutingModule, SharedModule, SharedPermitModule],
})
export class FallbackModule {}

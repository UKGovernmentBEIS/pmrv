import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { ActivityDataComponent } from './category-tier/activity-data/activity-data.component';
import { ActivityDataTemplateComponent } from './category-tier/activity-data/activity-data-template.component';
import { AnswersComponent as ActivityDataAnswersComponent } from './category-tier/activity-data/answers/answers.component';
import { JustificationComponent as ActivityDataJustificationComponent } from './category-tier/activity-data/justification/justification.component';
import { SummaryComponent as ActivityDataSummaryComponent } from './category-tier/activity-data/summary/summary.component';
import { SummaryOverviewComponent as ActivityDataSummaryOverviewComponent } from './category-tier/activity-data/summary/summary-overview.component';
import { CategoryComponent } from './category-tier/category/category.component';
import { CategorySummaryComponent } from './category-tier/category/summary/category-summary.component';
import { CategorySummaryOverviewComponent } from './category-tier/category/summary/category-summary-overview.component';
import { CategoryTierComponent } from './category-tier/category-tier.component';
import { DeleteComponent } from './category-tier/delete/delete.component';
import { AnswersComponent as EmissionFactorAnswersComponent } from './category-tier/emission-factor/answers/answers.component';
import { EmissionFactorComponent } from './category-tier/emission-factor/emission-factor.component';
import { JustificationComponent } from './category-tier/emission-factor/justification/justification.component';
import { SummaryComponent as EmissionFactorSummaryComponent } from './category-tier/emission-factor/summary/summary.component';
import { SummaryOverviewComponent as EmissionFactorSummaryOverviewComponent } from './category-tier/emission-factor/summary/summary-overview.component';
import { DescriptionComponent } from './description/description.component';
import { SummaryComponent as DescriptionSummaryComponent } from './description/summary/summary.component';
import { EfficiencyComponent } from './efficiency/efficiency.component';
import { SummaryComponent as EfficiencySummaryComponent } from './efficiency/summary/summary.component';
import { AnswersComponent as Tier2EmissionFactorAnswersComponent } from './emission-factor/answers/answers.component';
import { DeterminationInstallationComponent } from './emission-factor/determination-installation/determination-installation.component';
import { EmissionFactorComponent as Tier2EmissionFactorComponent } from './emission-factor/emission-factor.component';
import { ScheduleMeasurementsComponent } from './emission-factor/schedule-measurements/schedule-measurements.component';
import { SummaryComponent as Tier2EmissionFactorSummaryComponent } from './emission-factor/summary/summary.component';
import { PfcComponent } from './pfc.component';
import { PFCRoutingModule } from './pfc-routing.module';
import { SummaryComponent as TypesSummaryComponent } from './types/summary/summary.component';
import { TypesComponent } from './types/types.component';

@NgModule({
  declarations: [
    ActivityDataAnswersComponent,
    ActivityDataComponent,
    ActivityDataJustificationComponent,
    ActivityDataSummaryComponent,
    ActivityDataSummaryOverviewComponent,
    ActivityDataTemplateComponent,
    CategoryComponent,
    CategorySummaryComponent,
    CategorySummaryOverviewComponent,
    CategoryTierComponent,
    DeleteComponent,
    DescriptionComponent,
    DescriptionSummaryComponent,
    DeterminationInstallationComponent,
    EfficiencyComponent,
    EfficiencySummaryComponent,
    EmissionFactorAnswersComponent,
    EmissionFactorComponent,
    EmissionFactorSummaryComponent,
    EmissionFactorSummaryOverviewComponent,
    JustificationComponent,
    PfcComponent,
    ScheduleMeasurementsComponent,
    Tier2EmissionFactorAnswersComponent,
    Tier2EmissionFactorComponent,
    Tier2EmissionFactorSummaryComponent,
    TypesComponent,
    TypesSummaryComponent,
  ],
  imports: [PFCRoutingModule, SharedModule, SharedPermitModule],
})
export class PFCModule {}

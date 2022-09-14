import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { CalculationComponent } from './calculation.component';
import { CalculationRoutingModule } from './calculation-routing.module';
import { ActivityDataComponent } from './category-tier/activity-data/activity-data.component';
import { AnswersComponent as ActivityDataAnswersComponent } from './category-tier/activity-data/answers/answers.component';
import { ActivityDataHelpComponent } from './category-tier/activity-data/help/activity-data-help.component';
import { MeteringUncertaintyHelpComponent } from './category-tier/activity-data/help/metering-uncertainty-help.component';
import { JustificationComponent as ActivityDataJustificationComponent } from './category-tier/activity-data/justification/justification.component';
import { SummaryComponent as ActivityDataSummaryComponent } from './category-tier/activity-data/summary/summary.component';
import { SummaryOverviewComponent as ActivityDataSummaryOverviewComponent } from './category-tier/activity-data/summary/summary-overview.component';
import { AnalysisMethodComponent } from './category-tier/analysis-method/analysis-method.component';
import { AnalysisMethodDeleteComponent } from './category-tier/analysis-method-delete/analysis-method-delete.component';
import { AnalysisMethodListComponent } from './category-tier/analysis-method-list/analysis-method-list.component';
import { AnalysisMethodListTemplateComponent } from './category-tier/analysis-method-list/analysis-method-list-template.component';
import { AnalysisMethodUsedComponent } from './category-tier/analysis-method-used/analysis-method-used.component';
import { AnswersComponent } from './category-tier/answers/answers.component';
import { CategoryComponent } from './category-tier/category/category.component';
import { CategorySummaryComponent } from './category-tier/category/summary/category-summary.component';
import { CategorySummaryOverviewComponent } from './category-tier/category/summary/category-summary-overview.component';
import { CategoryTierComponent } from './category-tier/category-tier.component';
import { DefaultValueComponent } from './category-tier/default-value/default-value.component';
import { DeleteComponent } from './category-tier/delete/delete.component';
import { EntryStepComponent } from './category-tier/entry-step/entry-step.component';
import { CalculationApproachHelpComponent } from './category-tier/help/calculation-approach-help.component';
import { OneThirdComponent } from './category-tier/one-third/one-third.component';
import { SubtaskNamePipe } from './category-tier/pipes/subtask-name.pipe';
import { ReferenceComponent } from './category-tier/reference/reference.component';
import { SamplingJustificationComponent } from './category-tier/sampling-justification/sampling-justification.component';
import { SubtaskHelpComponent } from './category-tier/subtask-help/subtask-help.component';
import { SummaryComponent } from './category-tier/summary/summary.component';
import { SummaryTemplateComponent } from './category-tier/summary/summary-template.component';
import { TierComponent } from './category-tier/tier/tier.component';
import { TierJustificationComponent } from './category-tier/tier-justification/tier-justification.component';
import { DescriptionComponent } from './description/description.component';
import { SummaryComponent as DescriptionSummaryComponent } from './description/summary/summary.component';
import { AnswersComponent as SamplingPlanAnswersComponent } from './sampling-plan/answers/answers.component';
import { AppropriatenessComponent } from './sampling-plan/appropriateness/appropriateness.component';
import { PlanComponent } from './sampling-plan/plan/plan.component';
import { ReconciliationComponent } from './sampling-plan/reconciliation/reconciliation.component';
import { SamplingPlanComponent } from './sampling-plan/sampling-plan.component';
import { SummaryComponent as SamplingPlanSummaryComponent } from './sampling-plan/summary/summary.component';

@NgModule({
  declarations: [
    ActivityDataAnswersComponent,
    ActivityDataComponent,
    ActivityDataHelpComponent,
    ActivityDataJustificationComponent,
    ActivityDataSummaryComponent,
    ActivityDataSummaryOverviewComponent,
    AnalysisMethodComponent,
    AnalysisMethodDeleteComponent,
    AnalysisMethodListComponent,
    AnalysisMethodListTemplateComponent,
    AnalysisMethodUsedComponent,
    AnswersComponent,
    AppropriatenessComponent,
    CalculationApproachHelpComponent,
    CalculationComponent,
    CategoryComponent,
    CategorySummaryComponent,
    CategorySummaryOverviewComponent,
    CategoryTierComponent,
    DefaultValueComponent,
    DeleteComponent,
    DescriptionComponent,
    DescriptionSummaryComponent,
    EntryStepComponent,
    MeteringUncertaintyHelpComponent,
    OneThirdComponent,
    PlanComponent,
    ReconciliationComponent,
    ReferenceComponent,
    SamplingJustificationComponent,
    SamplingPlanAnswersComponent,
    SamplingPlanComponent,
    SamplingPlanSummaryComponent,
    SubtaskHelpComponent,
    SubtaskNamePipe,
    SummaryComponent,
    SummaryTemplateComponent,
    TierComponent,
    TierJustificationComponent,
  ],
  imports: [CalculationRoutingModule, SharedModule, SharedPermitModule],
})
export class CalculationModule {}

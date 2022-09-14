import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PendingRequestGuard } from '../../../core/guards/pending-request.guard';
import { PermitRoute } from '../../permit-route.interface';
import { SummaryGuard } from '../../shared/summary.guard';
import { SourceStreamHelpComponent } from '../approaches-help/source-stream-help.component';
import { AnswersComponent as ActivityDataAnswersComponent } from '../calculation/category-tier/activity-data/answers/answers.component';
import { AnswersGuard as ActivityDataAnswersGuard } from '../calculation/category-tier/activity-data/answers/answers.guard';
import { JustificationComponent as ActivityDataJustificationComponent } from '../calculation/category-tier/activity-data/justification/justification.component';
import { SummaryComponent as ActivityDataSummaryComponent } from '../calculation/category-tier/activity-data/summary/summary.component';
import { CalculationComponent } from './calculation.component';
import { ActivityDataComponent } from './category-tier/activity-data/activity-data.component';
import { ActivityDataGuard } from './category-tier/activity-data/activity-data.guard';
import { ActivityDataHelpComponent } from './category-tier/activity-data/help/activity-data-help.component';
import { MeteringUncertaintyHelpComponent } from './category-tier/activity-data/help/metering-uncertainty-help.component';
import { JustificationGuard as ActivityDataJustificationGuard } from './category-tier/activity-data/justification/justification.guard';
import { AnalysisMethodComponent } from './category-tier/analysis-method/analysis-method.component';
import { AnalysisMethodDeleteComponent } from './category-tier/analysis-method-delete/analysis-method-delete.component';
import { AnalysisMethodListComponent } from './category-tier/analysis-method-list/analysis-method-list.component';
import { AnalysisMethodUsedComponent } from './category-tier/analysis-method-used/analysis-method-used.component';
import { AnswersComponent } from './category-tier/answers/answers.component';
import { CategoryComponent } from './category-tier/category/category.component';
import { CategorySummaryComponent } from './category-tier/category/summary/category-summary.component';
import { CategorySummaryGuard } from './category-tier/category/summary/category-summary.guard';
import { CategoryTierComponent } from './category-tier/category-tier.component';
import { CategoryTierGuard } from './category-tier/category-tier.guard';
import { DefaultValueComponent } from './category-tier/default-value/default-value.component';
import { DeleteComponent } from './category-tier/delete/delete.component';
import { DeleteGuard } from './category-tier/delete/delete.guard';
import { EntryStepComponent } from './category-tier/entry-step/entry-step.component';
import { AnswersGuard } from './category-tier/guards/answers.guard';
import { WizardStepGuard } from './category-tier/guards/wizard-step.guard';
import { CalculationApproachHelpComponent } from './category-tier/help/calculation-approach-help.component';
import { OneThirdComponent } from './category-tier/one-third/one-third.component';
import { ReferenceComponent } from './category-tier/reference/reference.component';
import { SamplingJustificationComponent } from './category-tier/sampling-justification/sampling-justification.component';
import { SubtaskHelpComponent } from './category-tier/subtask-help/subtask-help.component';
import { SummaryComponent } from './category-tier/summary/summary.component';
import { TierComponent } from './category-tier/tier/tier.component';
import { TierJustificationComponent } from './category-tier/tier-justification/tier-justification.component';
import { DescriptionComponent } from './description/description.component';
import { SummaryComponent as DescriptionSummaryComponent } from './description/summary/summary.component';
import { AnswersComponent as SamplingPlanAnswersComponent } from './sampling-plan/answers/answers.component';
import { AnswersGuard as SamplingPlanAnswersGuard } from './sampling-plan/answers/answers.guard';
import { AppropriatenessComponent } from './sampling-plan/appropriateness/appropriateness.component';
import { AppropriatenessGuard } from './sampling-plan/appropriateness/appropriateness.guard';
import { PlanComponent } from './sampling-plan/plan/plan.component';
import { PlanGuard } from './sampling-plan/plan/plan.guard';
import { ReconciliationComponent } from './sampling-plan/reconciliation/reconciliation.component';
import { ReconciliationGuard } from './sampling-plan/reconciliation/reconciliation.guard';
import { SamplingPlanComponent } from './sampling-plan/sampling-plan.component';
import { SamplingPlanGuard } from './sampling-plan/sampling-plan.guard';
import { SummaryComponent as SamplingPlanSummaryComponent } from './sampling-plan/summary/summary.component';

function getSubtaskChildrenRoutes(pageTitle: string, includeOneThirdRoute: boolean = false) {
  const routes: PermitRoute[] = [
    {
      path: '',
      data: { pageTitle: `${pageTitle}` },
      component: EntryStepComponent,
      canActivate: [WizardStepGuard],
      canDeactivate: [PendingRequestGuard],
    },
    {
      path: 'tier',
      data: { pageTitle: `${pageTitle} - Tier type` },
      component: TierComponent,
      canActivate: [WizardStepGuard],
      canDeactivate: [PendingRequestGuard],
    },
    {
      path: 'tier-justification',
      data: { pageTitle: `${pageTitle} - No highest required tier justification` },
      component: TierJustificationComponent,
      canActivate: [WizardStepGuard],
      canDeactivate: [PendingRequestGuard],
    },
    {
      path: 'default-value',
      data: { pageTitle: `${pageTitle} - Default value applied` },
      component: DefaultValueComponent,
      canActivate: [WizardStepGuard],
      canDeactivate: [PendingRequestGuard],
    },
    {
      path: 'reference',
      data: { pageTitle: `${pageTitle} - Standard reference source` },
      component: ReferenceComponent,
      canActivate: [WizardStepGuard],
      canDeactivate: [PendingRequestGuard],
    },
    {
      path: 'analysis-method-used',
      data: { pageTitle: `${pageTitle} - Using an analysis method?` },
      component: AnalysisMethodUsedComponent,
      canActivate: [WizardStepGuard],
      canDeactivate: [PendingRequestGuard],
    },
    {
      path: 'analysis-method/:methodIndex',
      children: [
        {
          path: '',
          data: { pageTitle: `${pageTitle} - Analysis method` },
          component: AnalysisMethodComponent,
          canActivate: [WizardStepGuard],
        },
        {
          path: 'sampling-justification',
          data: { pageTitle: `${pageTitle} - Reduced sampling frequency justification` },
          component: SamplingJustificationComponent,
          canActivate: [WizardStepGuard],
          canDeactivate: [PendingRequestGuard],
        },
        {
          path: 'delete',
          data: { pageTitle: `${pageTitle} - Delete` },
          component: AnalysisMethodDeleteComponent,
          canActivate: [WizardStepGuard],
          canDeactivate: [PendingRequestGuard],
        },
      ],
    },
    {
      path: 'analysis-method-list',
      data: { pageTitle: `${pageTitle} - Analysis method list` },
      component: AnalysisMethodListComponent,
      canActivate: [WizardStepGuard],
      canDeactivate: [PendingRequestGuard],
    },

    {
      path: 'answers',
      data: { pageTitle: `${pageTitle} - Answers` },
      component: AnswersComponent,
      canActivate: [AnswersGuard],
      canDeactivate: [PendingRequestGuard],
    },
    {
      path: 'summary',
      data: { pageTitle: `${pageTitle} - Summary` },
      component: SummaryComponent,
      canActivate: [SummaryGuard],
    },
    {
      path: 'help',
      data: { pageTitle: `${pageTitle} - Help` },
      component: SubtaskHelpComponent,
    },
  ];

  const oneThirdRoute: PermitRoute[] = [
    {
      path: 'one-third',
      data: { pageTitle: `${pageTitle} - One third rule` },
      component: OneThirdComponent,
      canActivate: [WizardStepGuard],
      canDeactivate: [PendingRequestGuard],
    },
  ];

  return includeOneThirdRoute ? routes.concat(oneThirdRoute) : routes;
}

const routes: PermitRoute[] = [
  {
    path: '',
    data: { pageTitle: 'Monitoring approaches - Calculation approach' },
    component: CalculationComponent,
  },
  {
    path: 'category-tier/:index',
    data: {
      taskKey: 'monitoringApproaches.CALCULATION.sourceStreamCategoryAppliedTiers',
    },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring approaches - Calculation source stream category tier' },
        component: CategoryTierComponent,
        canActivate: [CategoryTierGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'delete',
        data: { pageTitle: 'Are you sure you want to delete this source stream category?' },
        component: DeleteComponent,
        canActivate: [DeleteGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'category',
        children: [
          {
            path: '',
            data: {
              pageTitle: 'Monitoring approaches - Calculation source stream category',
            },
            component: CategoryComponent,
            canActivate: [CategoryTierGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Monitoring approaches - Calculation source stream category - Summary page' },
            component: CategorySummaryComponent,
            canActivate: [CategorySummaryGuard],
          },
        ],
      },
      {
        path: 'activity-data',
        data: { statusKey: 'CALCULATION_Activity_Data' },
        children: [
          {
            path: '',
            data: {
              pageTitle: 'Monitoring approaches - Calculation source stream category - Activity data',
            },
            component: ActivityDataComponent,
            canActivate: [ActivityDataGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'justification',
            data: {
              pageTitle:
                'Monitoring approaches - Calculation source stream category - Activity data tier justification',
            },
            component: ActivityDataJustificationComponent,
            canActivate: [ActivityDataJustificationGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'answers',
            data: {
              pageTitle: 'Monitoring approaches - Calculation source stream category - Activity data answers',
            },
            component: ActivityDataAnswersComponent,
            canActivate: [ActivityDataAnswersGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: {
              pageTitle: 'Monitoring approaches - Calculation source stream category - Activity data summary page',
            },
            component: ActivityDataSummaryComponent,
            canActivate: [SummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'help',
            data: {
              pageTitle: 'Monitoring approaches - Calculation source stream category - Activity data content',
            },
            component: ActivityDataHelpComponent,
          },
          {
            path: 'help-metering-uncertainty',
            data: {
              pageTitle: 'Monitoring approaches - Calculation source stream category - Metering uncertainty content',
            },
            component: MeteringUncertaintyHelpComponent,
          },
        ],
      },
      {
        path: 'calorific-value',
        data: { statusKey: 'CALCULATION_Calorific' },
        children: getSubtaskChildrenRoutes('Net calorific value'),
      },
      {
        path: 'emission-factor',
        data: { statusKey: 'CALCULATION_Emission_Factor' },
        children: getSubtaskChildrenRoutes('Emission factor', true),
      },
      {
        path: 'oxidation-factor',
        data: { statusKey: 'CALCULATION_Oxidation_Factor' },
        children: getSubtaskChildrenRoutes('Oxidation factor'),
      },
      {
        path: 'carbon-content',
        data: { statusKey: 'CALCULATION_Carbon_Content' },
        children: getSubtaskChildrenRoutes('Carbon content', true),
      },
      {
        path: 'conversion-factor',
        data: { statusKey: 'CALCULATION_Conversion_Factor' },
        children: getSubtaskChildrenRoutes('Conversion factor'),
      },
      {
        path: 'biomass-fraction',
        data: { statusKey: 'CALCULATION_Biomass_Fraction' },
        children: getSubtaskChildrenRoutes('Biomass fraction'),
      },
      {
        path: 'help-with-source-stream-categories',
        data: {
          pageTitle:
            'Monitoring approaches - Calculation source stream category tier - Get help with source stream categories',
        },
        component: SourceStreamHelpComponent,
      },
      {
        path: 'help-with-calculation-approach',
        data: {
          pageTitle:
            'Monitoring approaches - Calculation source stream category tier - Get help with calculation approach and calculation parameters.',
        },
        component: CalculationApproachHelpComponent,
      },
    ],
  },
  {
    path: 'description',
    data: { taskKey: 'monitoringApproaches.CALCULATION', statusKey: 'CALCULATION_Description' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring approaches - Calculation approach description' },
        component: DescriptionComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Monitoring approaches - Calculation approach description - Summary page',
        },
        component: DescriptionSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'sampling-plan',
    data: { taskKey: 'monitoringApproaches.CALCULATION.samplingPlan', statusKey: 'CALCULATION_Plan' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring approaches - Sampling plan analysis' },
        component: SamplingPlanComponent,
        canActivate: [SamplingPlanGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'plan',
        data: {
          taskKey: 'monitoringApproaches.CALCULATION.samplingPlan.details',
          pageTitle: 'Monitoring approaches - Sampling plan',
        },
        component: PlanComponent,
        canActivate: [PlanGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'appropriateness',
        data: {
          taskKey: 'monitoringApproaches.CALCULATION.samplingPlan.details',
          pageTitle: 'Monitoring approaches - Sampling plan appropriateness',
        },
        component: AppropriatenessComponent,
        canActivate: [AppropriatenessGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'reconciliation',
        data: {
          taskKey: 'monitoringApproaches.CALCULATION.samplingPlan.details.yearEndReconciliation',
          pageTitle: 'Monitoring approaches - Sampling plan year end reconciliation',
        },
        component: ReconciliationComponent,
        canActivate: [ReconciliationGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'answers',
        data: { pageTitle: 'Monitoring approaches - Sampling plan - Check your answers' },
        component: SamplingPlanAnswersComponent,
        canActivate: [SamplingPlanAnswersGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Monitoring approaches - Calculation sampling plan - Summary page',
        },
        component: SamplingPlanSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CalculationRoutingModule {}

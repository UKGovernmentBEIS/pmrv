import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PendingRequestGuard } from '../../../core/guards/pending-request.guard';
import { PermitRoute } from '../../permit-route.interface';
import { SummaryGuard } from '../../shared/summary.guard';
import { ActivityDataComponent } from './category-tier/activity-data/activity-data.component';
import { ActivityDataGuard } from './category-tier/activity-data/activity-data.guard';
import { AnswersComponent as ActivityDataAnswersComponent } from './category-tier/activity-data/answers/answers.component';
import { AnswersGuard as ActivityDataAnswersGuard } from './category-tier/activity-data/answers/answers.guard';
import { JustificationComponent as ActivityDataJustificationComponent } from './category-tier/activity-data/justification/justification.component';
import { SummaryComponent as ActivityDataSummaryComponent } from './category-tier/activity-data/summary/summary.component';
import { CategoryComponent } from './category-tier/category/category.component';
import { CategorySummaryComponent } from './category-tier/category/summary/category-summary.component';
import { CategorySummaryGuard } from './category-tier/category/summary/category-summary.guard';
import { CategoryTierComponent } from './category-tier/category-tier.component';
import { CategoryTierGuard } from './category-tier/category-tier.guard';
import { DeleteComponent } from './category-tier/delete/delete.component';
import { DeleteGuard } from './category-tier/delete/delete.guard';
import { AnswersComponent as CategoryTierEmissionFactorAnswersComponent } from './category-tier/emission-factor/answers/answers.component';
import { AnswersGuard as CategoryTierEmissionFactorAnswersGuard } from './category-tier/emission-factor/answers/answers.guard';
import { EmissionFactorComponent as CategoryTierEmissionFactorComponent } from './category-tier/emission-factor/emission-factor.component';
import { EmissionFactorGuard as CategoryTierEmissionFactorGuard } from './category-tier/emission-factor/emission-factor.guard';
import { JustificationComponent as CategoryTierEmissionFactorJustificationComponent } from './category-tier/emission-factor/justification/justification.component';
import { SummaryComponent as CategoryTierEmissionFactorSummaryComponent } from './category-tier/emission-factor/summary/summary.component';
import { DescriptionComponent } from './description/description.component';
import { SummaryComponent as DescriptionSummaryComponent } from './description/summary/summary.component';
import { EfficiencyComponent } from './efficiency/efficiency.component';
import { SummaryComponent as EfficiencySummaryComponent } from './efficiency/summary/summary.component';
import { AnswersComponent as Tier2EmissionFactorAnswersComponent } from './emission-factor/answers/answers.component';
import { AnswersGuard as Tier2EmissionFactorAnswersGuard } from './emission-factor/answers/answers.guard';
import { DeterminationInstallationComponent } from './emission-factor/determination-installation/determination-installation.component';
import { EmissionFactorComponent as Tier2EmissionFactorComponent } from './emission-factor/emission-factor.component';
import { EmissionFactorGuard as Tier2EmissionFactorGuard } from './emission-factor/emission-factor.guard';
import { ScheduleMeasurementsComponent } from './emission-factor/schedule-measurements/schedule-measurements.component';
import { SummaryComponent as Tier2EmissionFactorSummaryComponent } from './emission-factor/summary/summary.component';
import { PfcComponent } from './pfc.component';
import { SummaryComponent as TypesSummaryComponent } from './types/summary/summary.component';
import { TypesComponent } from './types/types.component';

const routes: PermitRoute[] = [
  {
    path: '',
    data: { pageTitle: 'Monitoring approaches - Perfluorocarbons' },
    component: PfcComponent,
  },
  {
    path: 'category-tier/:index',
    data: {
      taskKey: 'monitoringApproaches.PFC.sourceStreamCategoryAppliedTiers',
    },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring approaches - Perfluorocarbons (PFC) source stream category tier' },
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
              pageTitle: 'Monitoring approaches - Perfluorocarbons (PFC) source stream category',
            },
            component: CategoryComponent,
            canActivate: [CategoryTierGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Monitoring approaches - Perfluorocarbons (PFC) source stream category - Summary page' },
            component: CategorySummaryComponent,
            canActivate: [CategorySummaryGuard],
          },
        ],
      },
      {
        path: 'activity-data',
        data: { statusKey: 'PFC_Activity_Data' },
        children: [
          {
            path: '',
            data: {
              pageTitle: 'Monitoring approaches - Perflurocarbon (PFC) source stream category - Activity data',
            },
            component: ActivityDataComponent,
            canActivate: [ActivityDataGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'justification',
            data: {
              pageTitle:
                'Monitoring approaches - Perflurocarbon (PFC) source stream category - Activity data justification',
            },
            component: ActivityDataJustificationComponent,
            canActivate: [ActivityDataGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'answers',
            data: {
              pageTitle: 'Monitoring approaches - Perflurocarbon (PFC) source stream category - Activity data answers',
            },
            component: ActivityDataAnswersComponent,
            canActivate: [ActivityDataAnswersGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: {
              pageTitle:
                'Monitoring approaches - Perflurocarbon (PFC) source stream category - Activity data summary page',
            },
            component: ActivityDataSummaryComponent,
            canActivate: [SummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'emission-factor',
        data: { statusKey: 'PFC_Emission_Factor' },
        children: [
          {
            path: '',
            data: {
              pageTitle: 'Monitoring approaches - Perflurocarbon (PFC) source stream category - Emission factor',
            },
            component: CategoryTierEmissionFactorComponent,
            canActivate: [CategoryTierEmissionFactorGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'justification',
            data: {
              pageTitle:
                'Monitoring approaches - Perflurocarbon (PFC) source stream category - Emission factor justification',
            },
            component: CategoryTierEmissionFactorJustificationComponent,
            canActivate: [CategoryTierEmissionFactorGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'answers',
            data: {
              pageTitle:
                'Monitoring approaches - Perflurocarbon (PFC) source stream category - Emission factor answers',
            },
            component: CategoryTierEmissionFactorAnswersComponent,
            canActivate: [CategoryTierEmissionFactorAnswersGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: {
              pageTitle:
                'Monitoring approaches - Perflurocarbon (PFC) source stream category - Emission factor summary page',
            },
            component: CategoryTierEmissionFactorSummaryComponent,
            canActivate: [SummaryGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
    ],
  },
  {
    path: 'description',
    data: { taskKey: 'monitoringApproaches.PFC', statusKey: 'PFC_Description' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring approaches - Perfluorocarbons (PFC) approach description' },
        component: DescriptionComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Monitoring approaches - Perfluorocarbons (PFC) approach description - Summary page' },
        component: DescriptionSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'types',
    data: { taskKey: 'monitoringApproaches.PFC.cellAndAnodeTypes', statusKey: 'PFC_Types' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring approaches - Perfluorocarbons (PFC) cell and anode types' },
        component: TypesComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Monitoring approaches - Perfluorocarbons (PFC) cell and anode types - Summary page' },
        component: TypesSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'efficiency',
    data: { taskKey: 'monitoringApproaches.PFC.collectionEfficiency', statusKey: 'PFC_Efficiency' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring approaches - Perfluorocarbons (PFC) collection efficiency' },
        component: EfficiencyComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Monitoring approaches - Perfluorocarbons (PFC) collection efficiency - Summary page' },
        component: EfficiencySummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'emission-factor',
    data: { taskKey: 'monitoringApproaches.PFC.tier2EmissionFactor', statusKey: 'PFC_Tier2EmissionFactor' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring approaches - Perfluorocarbons (PFC) tier 2 emission factor' },
        component: Tier2EmissionFactorComponent,
        canActivate: [Tier2EmissionFactorGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'determination-installation',
        data: {
          pageTitle:
            'Monitoring approaches - Perfluorocarbons (PFC) tier 2 emission factor - Determination installation page',
        },
        component: DeterminationInstallationComponent,
        canActivate: [Tier2EmissionFactorGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'schedule-measurements',
        data: {
          pageTitle:
            'Monitoring approaches - Perfluorocarbons (PFC) tier 2 emission factor - Schedule of measurements page',
        },
        component: ScheduleMeasurementsComponent,
        canActivate: [Tier2EmissionFactorGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'answers',
        data: {
          pageTitle: 'Monitoring approaches - Perfluorocarbons (PFC) tier 2 emission factor - Check your answers page',
        },
        component: Tier2EmissionFactorAnswersComponent,
        canActivate: [Tier2EmissionFactorAnswersGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Monitoring approaches - Perfluorocarbons (PFC) tier 2 emission factor - Summary page' },
        component: Tier2EmissionFactorSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PFCRoutingModule {}

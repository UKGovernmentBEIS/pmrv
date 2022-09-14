import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PendingRequestGuard } from '../../../core/guards/pending-request.guard';
import { PermitRoute } from '../../permit-route.interface';
import { AnswersComponent } from '../../shared/emissions/answers/answers.component';
import { AnswersGuard } from '../../shared/emissions/answers/answers.guard';
import { EmissionsComponent } from '../../shared/emissions/emissions.component';
import { EmissionsGuard } from '../../shared/emissions/emissions.guard';
import { JustificationComponent } from '../../shared/emissions/justification/justification.component';
import { SummaryComponent } from '../../shared/emissions/summary/summary.component';
import { SummaryGuard } from '../../shared/summary.guard';
import { AppliedStandardComponent } from './category-tier/applied-standard/applied-standard.component';
import { SummaryComponent as AppliedStandardSummaryComponent } from './category-tier/applied-standard/summary/summary.component';
import { CategoryComponent } from './category-tier/category/category.component';
import { CategorySummaryComponent } from './category-tier/category/summary/category-summary.component';
import { CategorySummaryGuard } from './category-tier/category/summary/category-summary.guard';
import { CategoryTierComponent } from './category-tier/category-tier.component';
import { CategoryTierGuard } from './category-tier/category-tier.guard';
import { DeleteComponent } from './category-tier/delete/delete.component';
import { DeleteGuard } from './category-tier/delete/delete.guard';
import { DescriptionComponent } from './description/description.component';
import { SummaryComponent as DescriptionSummaryComponent } from './description/summary/summary.component';
import { MeasurementComponent } from './measurement.component';
import { OptionalComponent } from './optional/optional.component';
import { SummaryComponent as OptionalSummaryComponent } from './optional/summary/summary.component';
import { ProcedureComponent } from './procedure/procedure.component';
import { SummaryComponent as ProcedureSummaryComponent } from './procedure/summary/summary.component';

const routes: PermitRoute[] = [
  {
    path: '',
    data: { pageTitle: 'Monitoring approaches - Measurement approach' },
    component: MeasurementComponent,
  },
  {
    path: 'category-tier/:index',
    data: {
      taskKey: 'monitoringApproaches.MEASUREMENT.sourceStreamCategoryAppliedTiers',
    },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring approaches - Measurement (MSR) source stream category tier' },
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
              pageTitle: 'Monitoring approaches - Measurement (MSR) source stream category',
            },
            component: CategoryComponent,
            canActivate: [CategoryTierGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Monitoring approaches - Measurement (MSR) source stream category - Summary page' },
            component: CategorySummaryComponent,
            canActivate: [CategorySummaryGuard],
          },
        ],
      },
      {
        path: 'applied-standard',
        data: {
          statusKey: 'MEASUREMENT_Applied_Standard',
        },
        children: [
          {
            path: '',
            data: {
              pageTitle: 'Monitoring approaches - Measurement source stream category applied standard',
            },
            component: AppliedStandardComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Monitoring approaches - Measurement source stream category - summary' },
            component: AppliedStandardSummaryComponent,
            canActivate: [SummaryGuard],
          },
        ],
      },
      {
        path: 'emissions',
        data: {
          taskKey: 'MEASUREMENT',
          statusKey: 'MEASUREMENT_Measured_Emissions',
        },
        children: [
          {
            path: '',
            data: { pageTitle: 'Measured emissions' },
            component: EmissionsComponent,
            canActivate: [EmissionsGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'justification',
            data: { pageTitle: 'Measured emissions' },
            component: JustificationComponent,
            canActivate: [EmissionsGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'answers',
            data: { pageTitle: 'Confirm your answers' },
            component: AnswersComponent,
            canActivate: [AnswersGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Measured emissions' },
            component: SummaryComponent,
            canActivate: [SummaryGuard],
          },
        ],
      },
    ],
  },
  {
    path: 'description',
    data: { taskKey: 'monitoringApproaches.MEASUREMENT', statusKey: 'MEASUREMENT_Description' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring approaches - Measurement approach description' },
        component: DescriptionComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Monitoring approaches - Measurement approach description - Summary page',
        },
        component: DescriptionSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'emissions',
    data: { taskKey: 'monitoringApproaches.MEASUREMENT.emissionDetermination', statusKey: 'MEASUREMENT_Emission' },
    children: [
      {
        path: '',
        component: ProcedureComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        component: ProcedureSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'reference',
    data: {
      taskKey: 'monitoringApproaches.MEASUREMENT.referencePeriodDetermination',
      statusKey: 'MEASUREMENT_Reference',
    },
    children: [
      {
        path: '',
        component: ProcedureComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        component: ProcedureSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'gasflow',
    data: { taskKey: 'monitoringApproaches.MEASUREMENT.gasFlowCalculation', statusKey: 'MEASUREMENT_Gasflow' },
    children: [
      {
        path: '',
        component: OptionalComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        component: OptionalSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'biomass',
    data: { taskKey: 'monitoringApproaches.MEASUREMENT.biomassEmissions', statusKey: 'MEASUREMENT_Biomass' },
    children: [
      {
        path: '',
        component: OptionalComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        component: OptionalSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'corroborating',
    data: {
      taskKey: 'monitoringApproaches.MEASUREMENT.corroboratingCalculations',
      statusKey: 'MEASUREMENT_Corroborating',
    },
    children: [
      {
        path: '',
        component: ProcedureComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        component: ProcedureSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class MeasurementRoutingModule {}

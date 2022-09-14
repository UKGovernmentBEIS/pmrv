import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PendingRequestGuard } from '../../../core/guards/pending-request.guard';
import { PermitRoute } from '../../permit-route.interface';
import { SummaryGuard } from '../../shared/summary.guard';
import { CategoryComponent } from './category-tier/category/category.component';
import { CategorySummaryComponent } from './category-tier/category/summary/category-summary.component';
import { CategorySummaryGuard } from './category-tier/category/summary/category-summary.guard';
import { CategoryTierComponent } from './category-tier/category-tier.component';
import { CategoryTierGuard } from './category-tier/category-tier.guard';
import { DeleteComponent } from './category-tier/delete/delete.component';
import { DeleteGuard } from './category-tier/delete/delete.guard';
import { DescriptionComponent } from './description/description.component';
import { SummaryComponent as DescriptionSummaryComponent } from './description/summary/summary.component';
import { FallbackComponent } from './fallback.component';
import { SummaryComponent as UncertaintySummaryComponent } from './uncertainty/summary/summary.component';
import { UncertaintyComponent } from './uncertainty/uncertainty.component';

const routes: PermitRoute[] = [
  {
    path: '',
    data: { pageTitle: 'Monitoring approaches - Fall-back approach' },
    component: FallbackComponent,
  },
  {
    path: 'category-tier/:index',
    data: {
      taskKey: 'monitoringApproaches.FALLBACK.sourceStreamCategoryAppliedTiers',
    },
    children: [
      {
        path: '',
        data: { pageTitle: 'Fall-back source stream category tier' },
        component: CategoryTierComponent,
        canActivate: [CategoryTierGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'delete',
        data: { pageTitle: 'Delete source stream category' },
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
              pageTitle: 'Fall-back source stream category',
            },
            component: CategoryComponent,
            canActivate: [CategoryTierGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Fall-back source stream category summary' },
            component: CategorySummaryComponent,
            canActivate: [CategorySummaryGuard],
          },
        ],
      },
    ],
  },
  {
    path: 'description',
    data: { taskKey: 'monitoringApproaches.FALLBACK', statusKey: 'FALLBACK_Description' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring approaches - Fall-back approach description and justification' },
        component: DescriptionComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Monitoring approaches - Fall-back approach description and justification - Summary page' },
        component: DescriptionSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'uncertainty',
    data: { taskKey: 'monitoringApproaches.FALLBACK.annualUncertaintyAnalysis', statusKey: 'FALLBACK_Uncertainty' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring approaches - Fall-back approach annual uncertainty analysis' },
        component: UncertaintyComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Monitoring approaches - Fall-back approach annual uncertainty analysis - Summary page' },
        component: UncertaintySummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class FallbackRoutingModule {}

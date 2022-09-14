import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PendingRequestGuard } from '../../../core/guards/pending-request.guard';
import { PermitRoute } from '../../permit-route.interface';
import { SummaryGuard } from '../../shared/summary.guard';
import { DescriptionComponent } from './description/description.component';
import { SummaryComponent } from './description/summary/summary.component';
import { InherentCO2Component } from './inherent-co2.component';

const routes: PermitRoute[] = [
  {
    path: '',
    data: { pageTitle: 'Monitoring approaches - Inherent CO2' },
    component: InherentCO2Component,
  },
  {
    path: 'description',
    data: { taskKey: 'monitoringApproaches.INHERENT_CO2', statusKey: 'INHERENT_CO2_Description' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring approaches - Inherent CO2 approach description' },
        component: DescriptionComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Monitoring approaches - Inherent CO2 approach description - Summary',
        },
        component: SummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class InherentCo2RoutingModule {}

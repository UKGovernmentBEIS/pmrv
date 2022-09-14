import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PermitVariationGuard } from './permit-variation.guard';
import { PermitVariationTasklistComponent } from './permit-variation-tasklist/permit-variation-tasklist.component';

const routes: Routes = [
  {
    path: '',
    data: { pageTitle: 'Make a change to your permit' },
    component: PermitVariationTasklistComponent,
    canActivate: [PermitVariationGuard],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PermitVariationRoutingModule {}

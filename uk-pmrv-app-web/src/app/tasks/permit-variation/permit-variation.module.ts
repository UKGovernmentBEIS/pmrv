import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { SharedPermitModule } from '@permit-application/shared/shared-permit.module';
import { SharedModule } from '@shared/shared.module';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { PermitVariationRoutingModule } from './permit-variation-routing.module';
import { PermitVariationTasklistComponent } from './permit-variation-tasklist/permit-variation-tasklist.component';

@NgModule({
  declarations: [PermitVariationTasklistComponent],
  imports: [CommonModule, PermitVariationRoutingModule, SharedModule, SharedPermitModule, TaskSharedModule],
})
export class PermitVariationModule {}

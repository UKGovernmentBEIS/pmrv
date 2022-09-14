import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from '../../../../shared/shared.module';
import { ReasonComponent } from './reason/reason.component';

const declarations = [ReasonComponent];

@NgModule({
  declarations: declarations,
  exports: declarations,
  imports: [RouterModule, SharedModule],
})
export class SharedPermitSurrenderReviewDeterminationModule {}

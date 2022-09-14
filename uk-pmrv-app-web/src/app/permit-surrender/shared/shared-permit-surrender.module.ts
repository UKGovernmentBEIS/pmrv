import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from '../../shared/shared.module';
import { PermitSurrenderSummaryComponent } from './permit-surrender-summary/permit-surrender-summary.component';

const declarations = [PermitSurrenderSummaryComponent];

@NgModule({
  declarations: declarations,
  exports: declarations,
  imports: [RouterModule, SharedModule],
})
export class SharedPermitSurrenderModule {}

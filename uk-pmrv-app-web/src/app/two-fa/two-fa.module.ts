import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { SharedModule } from '../shared/shared.module';
import { Change2faComponent } from './change-2fa/change-2fa.component';
import { Delete2faComponent } from './delete-2fa/delete-2fa.component';
import { InvalidCodeComponent } from './invalid-code/invalid-code.component';
import { TwoFaRoutingModule } from './two-fa-routing.module';

@NgModule({
  declarations: [Change2faComponent, Delete2faComponent, InvalidCodeComponent],
  imports: [CommonModule, SharedModule, TwoFaRoutingModule],
})
export class TwoFaModule {}

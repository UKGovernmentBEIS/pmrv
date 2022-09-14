import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { SharedModule } from '../shared/shared.module';
import { TimedOutComponent } from './timed-out/timed-out.component';
import { TimeoutBannerComponent } from './timeout-banner/timeout-banner.component';

@NgModule({
  declarations: [TimedOutComponent, TimeoutBannerComponent],
  imports: [CommonModule, SharedModule],
  exports: [TimeoutBannerComponent],
})
export class TimeoutModule {}

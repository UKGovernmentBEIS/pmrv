import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { ActionLayoutComponent } from './components/action-layout/action-layout.component';
import { BaseActionContainerComponent } from './components/base-task-container-component/base-action-container.component';

@NgModule({
  declarations: [ActionLayoutComponent, BaseActionContainerComponent],
  exports: [ActionLayoutComponent, BaseActionContainerComponent],
  imports: [SharedModule],
})
export class ActionSharedModule {}

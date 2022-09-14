import { NgModule } from '@angular/core';

import { MarkdownModule } from 'ngx-markdown';

import { SharedModule } from '../shared/shared.module';
import { MessageRoutingModule } from './message-routing.module';
import { SystemNotificationComponent } from './system-notification/system-notification.component';

@NgModule({
  imports: [MarkdownModule.forChild(), MessageRoutingModule, SharedModule],
  declarations: [SystemNotificationComponent],
})
export class MessageModule {}

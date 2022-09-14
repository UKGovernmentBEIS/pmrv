import { NgModule } from '@angular/core';

import { SharedModule } from '../shared/shared.module';
import { DocumentTemplateDetailsTemplateComponent } from './document/document-template-details-template.component';
import { DocumentTemplateOverviewComponent } from './document/document-template-overview.component';
import { DocumentTemplateComponent } from './document/edit/document-template.component';
import { EmailTemplateComponent } from './email/edit/email-template.component';
import { EmailTemplateDetailsTemplateComponent } from './email/email-template-details-template.component';
import { EmailTemplateOverviewComponent } from './email/email-template-overview.component';
import { OperatorTypePipe } from './operator-type.pipe';
import { TemplatesComponent } from './templates.component';
import { TemplatesRoutingModule } from './templates-routing.module';

@NgModule({
  declarations: [
    DocumentTemplateComponent,
    DocumentTemplateDetailsTemplateComponent,
    DocumentTemplateOverviewComponent,
    EmailTemplateComponent,
    EmailTemplateDetailsTemplateComponent,
    EmailTemplateOverviewComponent,
    OperatorTypePipe,
    TemplatesComponent,
  ],
  imports: [SharedModule, TemplatesRoutingModule],
})
export class TemplatesModule {}

import { NgModule } from '@angular/core';

import { SharedModule } from '../shared/shared.module';
import { SharedUserModule } from '../shared-user/shared-user.module';
import { DeleteComponent } from './delete/delete.component';
import { DetailsComponent } from './details/details.component';
import { DeleteComponent as ExternalContactsDeleteComponent } from './external-contacts/delete/delete.component';
import { DetailsComponent as ExternalContactsDetailsComponent } from './external-contacts/details/details.component';
import { ExternalContactsComponent } from './external-contacts/external-contacts.component';
import { RegulatorsComponent } from './regulators.component';
import { RegulatorRoutingModule } from './regulators-routing.module';
import { SiteContactsComponent } from './site-contacts/site-contacts.component';

@NgModule({
  declarations: [
    DeleteComponent,
    DetailsComponent,
    ExternalContactsComponent,
    ExternalContactsDeleteComponent,
    ExternalContactsDetailsComponent,
    RegulatorsComponent,
    SiteContactsComponent,
  ],
  imports: [RegulatorRoutingModule, SharedModule, SharedUserModule],
})
export class RegulatorsModule {}

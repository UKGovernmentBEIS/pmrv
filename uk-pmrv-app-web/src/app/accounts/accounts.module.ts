import { NgModule } from '@angular/core';

import { ItemLinkPipe } from '../shared/pipes/item-link.pipe';
import { SharedModule } from '../shared/shared.module';
import { SharedUserModule } from '../shared-user/shared-user.module';
import { AccountComponent } from './account.component';
import { AccountsComponent } from './accounts.component';
import { AccountsRoutingModule } from './accounts-routing.module';
import { DetailsComponent } from './details/details.component';
import { AddressComponent } from './edit/address/address.component';
import { OperatorAddressComponent } from './edit/operator-address/operator-address.component';
import { OperatorNameComponent } from './edit/operator-name/operator-name.component';
import { RegistryIdComponent } from './edit/registry-id/registry-id.component';
import { SiteNameComponent } from './edit/site-name/site-name.component';
import { SopIdComponent } from './edit/sop-id/sop-id.component';
import { AddComponent } from './operators/add/add.component';
import { DeleteComponent } from './operators/delete/delete.component';
import { DetailsComponent as OperatorDetailsComponent } from './operators/details/details.component';
import { OperatorsComponent } from './operators/operators.component';
import { AppointComponent } from './operators/verification-body/appoint/appoint.component';
import { ConfirmationComponent } from './operators/verification-body/confirmation/confirmation.component';
import { ProcessActionsComponent } from './process-actions/process-actions.component';
import { WorkflowItemComponent } from './workflows/item/workflow-item.component';
import { WorkflowsComponent } from './workflows/workflows.component';

@NgModule({
  declarations: [
    AccountComponent,
    AccountsComponent,
    AddComponent,
    AddressComponent,
    AppointComponent,
    ConfirmationComponent,
    DeleteComponent,
    DetailsComponent,
    OperatorAddressComponent,
    OperatorDetailsComponent,
    OperatorNameComponent,
    OperatorsComponent,
    ProcessActionsComponent,
    RegistryIdComponent,
    SiteNameComponent,
    SopIdComponent,
    WorkflowItemComponent,
    WorkflowsComponent,
  ],
  imports: [AccountsRoutingModule, SharedModule, SharedUserModule],
  providers: [ItemLinkPipe],
})
export class AccountsModule {}

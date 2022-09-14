import { NgModule } from '@angular/core';

import { SharedModule } from '../shared/shared.module';
import { SharedUserModule } from '../shared-user/shared-user.module';
import { InvalidLinkComponent } from './invalid-link/invalid-link.component';
import { InvitationConfirmationComponent } from './invitation-confirmation/invitation-confirmation.component';
import { InvitationRouting } from './invitation-routing.module';
import { RegulatorInvitationComponent } from './regulator-invitation/regulator-invitation.component';
import { VerifierInvitationComponent } from './verifier-invitation/verifier-invitation.component';

@NgModule({
  declarations: [
    InvalidLinkComponent,
    InvitationConfirmationComponent,
    RegulatorInvitationComponent,
    VerifierInvitationComponent,
  ],
  imports: [InvitationRouting, SharedModule, SharedUserModule],
})
export class InvitationModule {}

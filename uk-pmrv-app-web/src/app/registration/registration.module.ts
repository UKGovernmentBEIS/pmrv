import { NgModule } from '@angular/core';

import { SharedModule } from '../shared/shared.module';
import { SharedUserModule } from '../shared-user/shared-user.module';
import { ChoosePasswordComponent } from './choose-password/choose-password.component';
import { ContactDetailsComponent } from './contact-details/contact-details.component';
import { EmailComponent } from './email/email.component';
import { EmailConfirmedComponent } from './email-confirmed/email-confirmed.component';
import { InvalidEmailLinkComponent } from './invalid-email-link/invalid-email-link.component';
import { InvalidInvitationLinkComponent } from './invalid-invitation-link/invalid-invitation-link.component';
import { InvitationComponent } from './invitation/invitation.component';
import { RegistrationRoutingModule } from './registration-routing.module';
import { StartProcessComponent } from './start-process/start-process.component';
import { SuccessComponent } from './success/success.component';
import { SummaryComponent } from './summary/summary.component';
import { UserRegistrationComponent } from './user-registration/user-registration.component';
import { VerificationSentComponent } from './verification-sent/verification-sent.component';

@NgModule({
  declarations: [
    ChoosePasswordComponent,
    ContactDetailsComponent,
    EmailComponent,
    EmailConfirmedComponent,
    InvalidEmailLinkComponent,
    InvalidInvitationLinkComponent,
    InvitationComponent,
    StartProcessComponent,
    SuccessComponent,
    SummaryComponent,
    UserRegistrationComponent,
    VerificationSentComponent,
  ],
  imports: [RegistrationRoutingModule, SharedModule, SharedUserModule],
})
export class RegistrationModule {}

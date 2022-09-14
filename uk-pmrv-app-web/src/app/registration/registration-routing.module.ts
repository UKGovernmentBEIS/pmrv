import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { NonAuthGuard } from '../core/guards/non-auth.guard';
import { PendingRequestGuard } from '../core/guards/pending-request.guard';
import { ChoosePasswordComponent } from './choose-password/choose-password.component';
import { ContactDetailsComponent } from './contact-details/contact-details.component';
import { EmailComponent } from './email/email.component';
import { EmailConfirmedComponent } from './email-confirmed/email-confirmed.component';
import { ClaimOperatorGuard } from './guards/claim-operator.guard';
import { ConfirmedEmailGuard } from './guards/confirmed-email.guard';
import { InvalidEmailLinkComponent } from './invalid-email-link/invalid-email-link.component';
import { InvalidInvitationLinkComponent } from './invalid-invitation-link/invalid-invitation-link.component';
import { InvitationComponent } from './invitation/invitation.component';
import { StartProcessComponent } from './start-process/start-process.component';
import { SuccessComponent } from './success/success.component';
import { SummaryComponent } from './summary/summary.component';
import { UserRegistrationComponent } from './user-registration/user-registration.component';

const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    data: { pageTitle: 'Start the registration process' },
    component: StartProcessComponent,
    canActivate: [NonAuthGuard],
  },
  {
    path: 'email',
    data: { pageTitle: 'What is your email address?' },
    component: EmailComponent,
    canDeactivate: [PendingRequestGuard],
    canActivate: [NonAuthGuard],
  },
  {
    path: 'invalid-link',
    component: InvalidEmailLinkComponent,
  },
  {
    path: 'email-confirmed',
    data: { pageTitle: 'Email address confirmed' },
    component: EmailConfirmedComponent,
    canActivate: [ConfirmedEmailGuard],
  },
  {
    path: 'invitation',
    data: { blockSignInRedirect: true },
    children: [
      {
        path: '',
        canActivate: [ClaimOperatorGuard],
        resolve: { installation: ClaimOperatorGuard },
        data: { pageTitle: 'You have been added as a user to this installation account' },
        component: InvitationComponent,
        pathMatch: 'full',
      },
      {
        path: 'invalid-link',
        component: InvalidInvitationLinkComponent,
      },
    ],
  },
  {
    path: 'user',
    component: UserRegistrationComponent,
    canActivate: [ConfirmedEmailGuard],
    children: [
      {
        path: 'contact-details',
        data: { pageTitle: 'Enter your details' },
        component: ContactDetailsComponent,
      },
      {
        path: 'choose-password',
        data: { pageTitle: 'Choose a password' },
        component: ChoosePasswordComponent,
      },
      {
        path: 'summary',
        data: { pageTitle: 'Check your answers' },
        component: SummaryComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'success',
        data: { pageTitle: "You've successfully created a user account" },
        component: SuccessComponent,
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class RegistrationRoutingModule {}

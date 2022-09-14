import { NgModule } from '@angular/core';
import { ExtraOptions, RouterModule, Routes } from '@angular/router';

import { AccessibilityComponent } from './accessibility/accessibility.component';
import { ContactUsComponent } from './contact-us/contact-us.component';
import { AuthGuard } from './core/guards/auth.guard';
import { NonAuthGuard } from './core/guards/non-auth.guard';
import { PendingRequestGuard } from './core/guards/pending-request.guard';
import { FeedbackComponent } from './feedback/feedback.component';
import { FeedbackGuard } from './feedback/feedback.guard';
import { InstallationAccountApplicationGuard } from './installation-account-application/installation-account-application.guard';
import { LandingPageComponent } from './landing-page/landing-page.component';
import { LandingPageGuard } from './landing-page/landing-page.guard';
import { PrivacyNoticeComponent } from './privacy-notice/privacy-notice.component';
import { TermsAndConditionsComponent } from './terms-and-conditions/terms-and-conditions.component';
import { TermsAndConditionsGuard } from './terms-and-conditions/terms-and-conditions.guard';
import { TimedOutComponent } from './timeout/timed-out/timed-out.component';
import { VersionComponent } from './version/version.component';

const routes: Routes = [
  {
    path: '',
    resolve: { resolver: AuthGuard },
    children: [
      {
        path: '',
        data: { pageTitle: 'Start Registration' },
        component: LandingPageComponent,
        canActivate: [LandingPageGuard],
      },
      {
        path: 'dashboard',
        canActivate: [AuthGuard],
        loadChildren: () => import('./dashboard/dashboard.module').then((m) => m.DashboardModule),
      },
      {
        path: 'message',
        canActivate: [AuthGuard],
        loadChildren: () => import('./message/message.module').then((m) => m.MessageModule),
      },
      {
        path: 'user',
        canActivate: [AuthGuard],
        children: [
          {
            path: 'regulators',
            loadChildren: () => import('./regulators/regulators.module').then((m) => m.RegulatorsModule),
          },
          {
            path: 'verifiers',
            loadChildren: () => import('./verifiers/verifiers.module').then((m) => m.VerifiersModule),
          },
        ],
      },
      {
        path: 'accounts',
        canActivate: [AuthGuard],
        loadChildren: () => import('./accounts/accounts.module').then((m) => m.AccountsModule),
      },
      {
        path: 'verification-bodies',
        data: { pageTitle: 'Manage verification bodies' },
        canActivate: [AuthGuard],
        loadChildren: () =>
          import('./verification-bodies/verification-bodies.module').then((m) => m.VerificationBodiesModule),
      },
      {
        path: 'installation-account',
        canActivate: [InstallationAccountApplicationGuard],
        loadChildren: () =>
          import('./installation-account-application/installation-account-application.module').then(
            (m) => m.InstallationAccountApplicationModule,
          ),
      },
      {
        path: 'permit-application',
        canActivate: [AuthGuard],
        loadChildren: () =>
          import('./permit-application/permit-application.module').then((m) => m.PermitApplicationModule),
      },
      {
        path: 'permit-revocation',
        canActivate: [AuthGuard],
        loadChildren: () =>
          import('./permit-revocation/permit-revocation.module').then((m) => m.PermitRevocationModule),
      },
      {
        path: 'permit-surrender',
        canActivate: [AuthGuard],
        loadChildren: () => import('./permit-surrender/permit-surrender.module').then((m) => m.PermitSurrenderModule),
      },
      {
        path: 'rfi',
        canActivate: [AuthGuard],
        loadChildren: () => import('./rfi/rfi.module').then((m) => m.RfiModule),
      },
      {
        path: 'rde',
        canActivate: [AuthGuard],
        loadChildren: () => import('./rde/rde.module').then((m) => m.RdeModule),
      },
      {
        path: 'payment',
        canActivate: [AuthGuard],
        loadChildren: () => import('./payment/payment.module').then((m) => m.PaymentModule),
      },
      {
        path: 'terms',
        data: { pageTitle: 'Accept terms and conditions' },
        component: TermsAndConditionsComponent,
        canActivate: [TermsAndConditionsGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'about',
        data: { pageTitle: 'About' },
        component: VersionComponent,
      },
      {
        path: 'privacy-notice',
        data: { pageTitle: 'Privacy notice' },
        component: PrivacyNoticeComponent,
      },
      {
        path: 'accessibility',
        data: { pageTitle: 'Accessibility Statement' },
        component: AccessibilityComponent,
      },
      {
        path: 'contact-us',
        data: { pageTitle: 'Contact us' },
        component: ContactUsComponent,
      },
      {
        path: 'feedback',
        data: { pageTitle: 'Feedback' },
        component: FeedbackComponent,
        canActivate: [FeedbackGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'registration',
        loadChildren: () => import('./registration/registration.module').then((m) => m.RegistrationModule),
      },
      {
        path: 'error',
        loadChildren: () => import('./error/error.module').then((m) => m.ErrorModule),
      },
      {
        path: 'invitation',
        loadChildren: () => import('./invitation/invitation.module').then((m) => m.InvitationModule),
      },
      {
        path: '2fa',
        loadChildren: () => import('./two-fa/two-fa.module').then((m) => m.TwoFaModule),
      },
      {
        path: 'templates',
        canActivate: [AuthGuard],
        loadChildren: () => import('./templates/templates.module').then((m) => m.TemplatesModule),
      },
      {
        path: 'tasks',
        canActivate: [AuthGuard],
        loadChildren: () => import('./tasks/tasks.module').then((m) => m.TasksModule),
      },
      {
        path: 'actions',
        canActivate: [AuthGuard],
        loadChildren: () => import('./actions/actions.module').then((m) => m.ActionsModule),
      },
      {
        path: 'mi-reports',
        canActivate: [AuthGuard],
        loadChildren: () => import('./mi-reports/mi-reports.module').then((m) => m.MiReportsModule),
      },
      {
        path: 'timed-out',
        data: { pageTitle: 'Session Timeout' },
        canActivate: [NonAuthGuard],
        component: TimedOutComponent,
      },
      // The route below handles all unknown routes / Page Not Found functionality.
      // Please keep this route last else there will be unexpected behavior.
      {
        path: '**',
        redirectTo: 'error/404',
      },
    ],
  },
];

const routerOptions: ExtraOptions = {
  paramsInheritanceStrategy: 'always',
};

@NgModule({
  imports: [RouterModule.forRoot(routes, routerOptions)],
  exports: [RouterModule],
  providers: [InstallationAccountApplicationGuard, LandingPageGuard],
})
export class AppRoutingModule {}

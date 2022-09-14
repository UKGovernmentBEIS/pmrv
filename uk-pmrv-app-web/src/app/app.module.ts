import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { ApplicationRef, DoBootstrap, ErrorHandler, NgModule } from '@angular/core';
import { BrowserModule, Title } from '@angular/platform-browser';

import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { MarkdownModule } from 'ngx-markdown';

import { ApiModule, Configuration } from 'pmrv-api';

import { environment } from '../environments/environment';
import { AccessibilityComponent } from './accessibility/accessibility.component';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { ContactUsComponent } from './contact-us/contact-us.component';
import { HttpErrorInterceptor } from './core/interceptors/http-error.interceptor';
import { PendingRequestInterceptor } from './core/interceptors/pending-request.interceptor';
import { GlobalErrorHandlingService } from './core/services/global-error-handling.service';
import { FeedbackComponent } from './feedback/feedback.component';
import { LandingPageComponent } from './landing-page/landing-page.component';
import { PrivacyNoticeComponent } from './privacy-notice/privacy-notice.component';
import { markdownModuleConfig } from './shared/markdown/markdown-options';
import { SharedModule } from './shared/shared.module';
import { TermsAndConditionsComponent } from './terms-and-conditions/terms-and-conditions.component';
import { TimeoutModule } from './timeout/timeout.module';
import { VersionComponent } from './version/version.component';

const keycloakService = new KeycloakService();

@NgModule({
  declarations: [
    AccessibilityComponent,
    AppComponent,
    ContactUsComponent,
    FeedbackComponent,
    LandingPageComponent,
    PrivacyNoticeComponent,
    TermsAndConditionsComponent,
    VersionComponent,
  ],
  imports: [
    ApiModule.forRoot(() => new Configuration({ basePath: environment.apiOptions.baseUrl })),
    AppRoutingModule,
    BrowserModule,
    KeycloakAngularModule,
    MarkdownModule.forRoot(markdownModuleConfig),
    SharedModule,
    TimeoutModule,
  ],
  providers: [
    {
      provide: KeycloakService,
      useValue: keycloakService,
    },
    {
      provide: ErrorHandler,
      useClass: GlobalErrorHandlingService,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: HttpErrorInterceptor,
      multi: true,
    },
    {
      provide: HTTP_INTERCEPTORS,
      useClass: PendingRequestInterceptor,
      multi: true,
    },
    Title,
  ],
})
export class AppModule implements DoBootstrap {
  ngDoBootstrap(appRef: ApplicationRef): void {
    keycloakService
      .init(environment.keycloakOptions)
      .then(() => appRef.bootstrap(AppComponent))
      .catch((error) => console.error('[ngDoBootstrap] init Keycloak failed', error));
  }
}

import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { ApplicationSubmittedComponent } from './application-submitted/application-submitted.component';
import { CancelApplicationComponent } from './cancel-application/cancel-application.component';
import { CommencementComponent } from './commencement/commencement.component';
import { ConfirmResponsibilityComponent } from './confirm-responsibility/confirm-responsibility.component';
import { EtsSchemeComponent } from './ets-scheme/ets-scheme.component';
import { installationFormFactory } from './factories/installation-form.factory';
import { legalEntityFormFactory } from './factories/legal-entity-form.factory';
import { GasEmissionsDetailsComponent } from './gas-emissions-details/gas-emissions-details.component';
import { GasEmissionsDetailsGuard } from './gas-emissions-details/gas-emissions-details.guard';
import { ApplicationGuard } from './guards/application.guard';
import { FormGuard } from './guards/form.guard';
import { InstallationAccountApplicationRoutingModule } from './installation-account-application-routing.module';
import { InstallationTypeComponent } from './installation-type/installation-type.component';
import { LegalEntityDetailsComponent } from './legal-entity-details/legal-entity-details.component';
import { LegalEntityDetailsGuard } from './legal-entity-details/legal-entity-details.guard';
import { LegalEntitySelectComponent } from './legal-entity-select/legal-entity-select.component';
import { LegalEntitySelectGuard } from './legal-entity-select/legal-entity-select.guard';
import { OffshoreGuard } from './offshore-details/offshore.guard';
import { OffshoreDetailsComponent } from './offshore-details/offshore-details.component';
import { OnshoreGuard } from './onshore-details/onshore.guard';
import { OnshoreDetailsComponent } from './onshore-details/onshore-details.component';
import { OperatorApplicationComponent } from './operator-application/operator-application.component';
import { ReviewComponent } from './review/review.component';
import { ReviewSummaryComponent } from './review/review-summary.component';
import { SubmittedDecisionComponent } from './submitted-decision/submitted-decision.component';
import { SummaryComponent } from './summary/summary.component';
import { TaskListComponent } from './task-list/task-list.component';

@NgModule({
  declarations: [
    ApplicationSubmittedComponent,
    CancelApplicationComponent,
    CommencementComponent,
    ConfirmResponsibilityComponent,
    EtsSchemeComponent,
    GasEmissionsDetailsComponent,
    InstallationTypeComponent,
    LegalEntityDetailsComponent,
    LegalEntitySelectComponent,
    OffshoreDetailsComponent,
    OnshoreDetailsComponent,
    OperatorApplicationComponent,
    ReviewComponent,
    ReviewSummaryComponent,
    SubmittedDecisionComponent,
    SummaryComponent,
    TaskListComponent,
  ],
  imports: [InstallationAccountApplicationRoutingModule, SharedModule],
  providers: [
    ApplicationGuard,
    FormGuard,
    GasEmissionsDetailsGuard,
    installationFormFactory,
    LegalEntityDetailsGuard,
    legalEntityFormFactory,
    LegalEntitySelectGuard,
    OffshoreGuard,
    OnshoreGuard,
  ],
})
export class InstallationAccountApplicationModule {}

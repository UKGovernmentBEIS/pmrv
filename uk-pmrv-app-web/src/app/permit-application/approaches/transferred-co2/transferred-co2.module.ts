import { NgModule } from '@angular/core';

import { SharedModule } from '../../../shared/shared.module';
import { SharedPermitModule } from '../../shared/shared-permit.module';
import { AccountingComponent } from './accounting/accounting.component';
import { AnswersComponent as AccountingAnswersComponent } from './accounting/answers/answers.component';
import { DetailsComponent as AccountingDetailsComponent } from './accounting/details/details.component';
import { JustificationComponent as AccountingJustificationComponent } from './accounting/justification/justification.component';
import { SummaryComponent as AccountingSummaryComponent } from './accounting/summary/summary.component';
import { DescriptionComponent } from './description/description.component';
import { SummaryComponent as DescriptionSummaryComponent } from './description/summary/summary.component';
import { DeleteComponent } from './installations/delete/delete.component';
import { DetailsComponent } from './installations/details/details.component';
import { InstallationsComponent } from './installations/installations.component';
import { SummaryComponent as InstallationSummaryComponent } from './installations/summary/summary.component';
import { OptionalComponent } from './optional/optional.component';
import { SummaryComponent as ProcedureOptionalSummaryComponent } from './optional/summary/summary.component';
import { ProcedureComponent } from './procedure/procedure.component';
import { SummaryComponent as ProcedureSummaryComponent } from './procedure/summary/summary.component';
import { SummaryComponent as TemperatureSummaryComponent } from './temperature/summary/summary.component';
import { TemperatureComponent } from './temperature/temperature.component';
import { TransferredCO2Component } from './transferred-co2.component';
import { TransferredCO2RoutingModule } from './transferred-co2-routing.module';

@NgModule({
  declarations: [
    AccountingAnswersComponent,
    AccountingComponent,
    AccountingDetailsComponent,
    AccountingJustificationComponent,
    AccountingSummaryComponent,
    DeleteComponent,
    DescriptionComponent,
    DescriptionSummaryComponent,
    DetailsComponent,
    InstallationsComponent,
    InstallationSummaryComponent,
    OptionalComponent,
    ProcedureComponent,
    ProcedureOptionalSummaryComponent,
    ProcedureSummaryComponent,
    TemperatureComponent,
    TemperatureSummaryComponent,
    TransferredCO2Component,
  ],
  imports: [SharedModule, SharedPermitModule, TransferredCO2RoutingModule],
})
export class TransferredCO2Module {}

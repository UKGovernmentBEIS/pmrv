import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PendingRequestGuard } from '../../../core/guards/pending-request.guard';
import { PermitRoute } from '../../permit-route.interface';
import { SummaryGuard } from '../../shared/summary.guard';
import { AccountingComponent } from './accounting/accounting.component';
import { AccountingGuard } from './accounting/accounting.guard';
import { AccountingAnswersGuard } from './accounting/answers/accounting-answers.guard';
import { AnswersComponent as AccountingAnswersComponent } from './accounting/answers/answers.component';
import { DetailsComponent as AccountingDetailsComponent } from './accounting/details/details.component';
import { JustificationComponent as AccountingJustificationComponent } from './accounting/justification/justification.component';
import { SummaryComponent as AccountingSummaryComponent } from './accounting/summary/summary.component';
import { DescriptionComponent } from './description/description.component';
import { SummaryComponent as DescriptionSummaryComponent } from './description/summary/summary.component';
import { DeleteComponent } from './installations/delete/delete.component';
import { DetailsComponent } from './installations/details/details.component';
import { DetailsGuard } from './installations/details/details.guard';
import { InstallationsComponent } from './installations/installations.component';
import { SummaryComponent as InstallationSummaryComponent } from './installations/summary/summary.component';
import { OptionalComponent } from './optional/optional.component';
import { SummaryComponent as ProcedureOptionalSummaryComponent } from './optional/summary/summary.component';
import { ProcedureComponent } from './procedure/procedure.component';
import { SummaryComponent as ProcedureSummaryComponent } from './procedure/summary/summary.component';
import { SummaryComponent as TemperatureSummaryComponent } from './temperature/summary/summary.component';
import { TemperatureComponent } from './temperature/temperature.component';
import { TransferredCO2Component } from './transferred-co2.component';

const routes: PermitRoute[] = [
  {
    path: '',
    data: { pageTitle: 'Monitoring approaches - Transferred CO2' },
    component: TransferredCO2Component,
  },
  {
    path: 'installations',
    data: {
      taskKey: 'monitoringApproaches.TRANSFERRED_CO2.receivingTransferringInstallations',
      statusKey: 'TRANSFERRED_CO2_Installations',
    },
    children: [
      {
        path: '',
        data: { pageTitle: 'Receiving or transferring installations' },
        component: InstallationsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'add',
        data: { pageTitle: 'Add an installation' },
        component: DetailsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'delete/:index',
        data: { pageTitle: 'Are you sure you want to delete this installation?' },
        component: DeleteComponent,
        canActivate: [DetailsGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Receiving or transferring installations - Summary page' },
        component: InstallationSummaryComponent,
        canActivate: [SummaryGuard],
      },
      {
        path: ':index',
        data: { pageTitle: 'Edit installation' },
        component: DetailsComponent,
        canActivate: [DetailsGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'accounting',
    data: {
      taskKey: 'monitoringApproaches.TRANSFERRED_CO2.accountingEmissions',
      statusKey: 'TRANSFERRED_CO2_Accounting',
    },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring approaches - Transferred CO2 accounting for emissions' },
        component: AccountingComponent,
        canActivate: [AccountingGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'details',
        data: { pageTitle: 'Monitoring approaches - Transferred CO2 accounting for emissions - Emission details page' },
        component: AccountingDetailsComponent,
        canActivate: [AccountingGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'justification',
        data: {
          pageTitle: 'Monitoring approaches - Transferred CO2 accounting for emissions - Emission justification page',
        },
        component: AccountingJustificationComponent,
        canActivate: [AccountingGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'answers',
        data: {
          pageTitle: 'Monitoring approaches - Transferred CO2 accounting for emissions - Confirm your answers page',
        },
        component: AccountingAnswersComponent,
        canActivate: [AccountingAnswersGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Monitoring approaches - Transferred CO2 accounting for emissions - Summary page' },
        component: AccountingSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'temperature',
    data: {
      taskKey: 'monitoringApproaches.TRANSFERRED_CO2.temperaturePressure',
      statusKey: 'TRANSFERRED_CO2_Temperature',
    },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring approaches - Transferred CO2 temperature and pressure' },
        component: TemperatureComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Monitoring approaches - Transferred CO2 temperature and pressure - Summary page' },
        component: TemperatureSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'deductions',
    data: {
      taskKey: 'monitoringApproaches.TRANSFERRED_CO2.deductionsToAmountOfTransferredCO2',
      statusKey: 'TRANSFERRED_CO2_Deductions',
    },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring approaches - Transferred CO2 deductions to amount' },
        component: OptionalComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Monitoring approaches - Transferred CO2 deductions to amount - Summary page' },
        component: ProcedureOptionalSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'leakage',
    data: {
      taskKey: 'monitoringApproaches.TRANSFERRED_CO2.procedureForLeakageEvents',
      statusKey: 'TRANSFERRED_CO2_Leakage',
    },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring approaches - Transferred CO2 procedure leakage events' },
        component: OptionalComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Monitoring approaches - Transferred CO2 procedure leakage events - Summary page' },
        component: ProcedureOptionalSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'transfer',
    data: { taskKey: 'monitoringApproaches.TRANSFERRED_CO2.transferOfCO2', statusKey: 'TRANSFERRED_CO2_Transfer' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring approaches - Transferred CO2 transfer of CO2' },
        component: ProcedureComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Monitoring approaches - Transferred CO2 transfer of CO2 - Summary page' },
        component: ProcedureSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'quantification',
    data: {
      taskKey: 'monitoringApproaches.TRANSFERRED_CO2.quantificationMethodologies',
      statusKey: 'TRANSFERRED_CO2_Quantification',
    },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring approaches - Transferred CO2 quantification methodologies' },
        component: ProcedureComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: {
          pageTitle: 'Monitoring approaches - Transferred CO2 quantification methodologies - Summary page',
        },
        component: ProcedureSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'description',
    data: { taskKey: 'monitoringApproaches.TRANSFERRED_CO2', statusKey: 'TRANSFERRED_CO2_Description' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring approaches - Transferred CO2 approach description' },
        component: DescriptionComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Monitoring approaches - Transferred CO2 approach description - Summary page' },
        component: DescriptionSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class TransferredCO2RoutingModule {}

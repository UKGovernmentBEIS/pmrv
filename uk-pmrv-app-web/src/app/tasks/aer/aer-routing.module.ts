import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { AbbreviationsComponent } from '@tasks/aer/submit/abbreviations/abbreviations.component';
import { SummaryComponent as AbbreviationsSummaryComponent } from '@tasks/aer/submit/abbreviations/summary/summary.component';
import { ApproachesComponent } from '@tasks/aer/submit/approaches/approaches.component';
import { ApproachesAddComponent } from '@tasks/aer/submit/approaches/approaches-add/approaches-add.component';
import { ApproachesDeleteComponent } from '@tasks/aer/submit/approaches/approaches-delete/approaches-delete.component';
import { ApproachesHelpComponent } from '@tasks/aer/submit/approaches/approaches-help/approaches-help.component';
import { SourceStreamHelpComponent } from '@tasks/aer/submit/approaches/approaches-help/source-stream-help.component';
import { ConfidentialityStatementComponent } from '@tasks/aer/submit/confidentiality-statement/confidentiality-statement.component';
import { ConfidentialityStatementSummaryComponent } from '@tasks/aer/submit/confidentiality-statement/confidentiality-statement-summary/confidentiality-statement-summary.component';
import { EmissionPointDeleteComponent } from '@tasks/aer/submit/emission-points/emission-point-delete/emission-point-delete.component';
import { EmissionPointDetailsComponent } from '@tasks/aer/submit/emission-points/emission-point-details/emission-point-details.component';
import { EmissionPointDetailsGuard } from '@tasks/aer/submit/emission-points/emission-point-details/emission-point-details.guard';
import { EmissionPointsComponent } from '@tasks/aer/submit/emission-points/emission-points.component';
import { EmissionSourceDeleteComponent } from '@tasks/aer/submit/emission-sources/emission-source-delete/emission-source-delete.component';
import { EmissionSourceDetailsComponent } from '@tasks/aer/submit/emission-sources/emission-source-details/emission-source-details.component';
import { EmissionSourceDetailsGuard } from '@tasks/aer/submit/emission-sources/emission-source-details/emission-source-details.guard';
import { EmissionSourcesComponent } from '@tasks/aer/submit/emission-sources/emission-sources.component';
import { NaceCodeDeleteComponent } from '@tasks/aer/submit/nace-codes/nace-code-delete/nace-code-delete.component';
import { NaceCodesComponent } from '@tasks/aer/submit/nace-codes/nace-codes.component';
import { NaceCodeInstallationActivityComponent } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-installation-activity/nace-code-installation-activity.component';
import { NaceCodeInstallationActivityGuard } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-installation-activity/nace-code-installation-activity.guard';
import { NaceCodeMainActivityComponent } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-main-activity/nace-code-main-activity.component';
import { NaceCodeSubCategoryComponent } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-sub-category/nace-code-sub-category.component';
import { NaceCodeSubCategoryGuard } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-sub-category/nace-code-sub-category.guard';
import { ActivityComponent } from '@tasks/aer/submit/prtr/activity/activity.component';
import { ActivityGuard } from '@tasks/aer/submit/prtr/activity/activity.guard';
import { DeleteComponent as PrtrDeleteComponent } from '@tasks/aer/submit/prtr/activity/delete/delete.component';
import { PrtrComponent } from '@tasks/aer/submit/prtr/prtr.component';
import { SummaryComponent as PrtrSummaryComponent } from '@tasks/aer/submit/prtr/summary/summary.component';
import { SummaryGuard as PrtrSummaryGuard } from '@tasks/aer/submit/prtr/summary/summary.guard';
import { CapacityComponent } from '@tasks/aer/submit/regulated-activities/add/capacity/capacity.component';
import { CrfCodesComponent } from '@tasks/aer/submit/regulated-activities/add/crf-codes/crf-codes.component';
import { EnergyCrfCodeComponent } from '@tasks/aer/submit/regulated-activities/add/energy-crf-code/energy-crf-code.component';
import { IndustrialCrfCodeComponent } from '@tasks/aer/submit/regulated-activities/add/industrial-crf-code/industrial-crf-code.component';
import { RegulatedActivityComponent } from '@tasks/aer/submit/regulated-activities/add/regulated-activity.component';
import { RegulatedActivityGuard } from '@tasks/aer/submit/regulated-activities/add/regulated-activity.guard';
import { RegulatedActivityDeleteComponent } from '@tasks/aer/submit/regulated-activities/delete/regulated-activity-delete.component';
import { RegulatedActivitiesComponent } from '@tasks/aer/submit/regulated-activities/regulated-activities.component';
import { SummaryGuard } from '@tasks/aer/submit/shared/guards/summary.guard';
import { SourceStreamDeleteComponent } from '@tasks/aer/submit/source-streams/source-stream-delete/source-stream-delete.component';
import { SourceStreamDetailsGuard } from '@tasks/aer/submit/source-streams/source-stream-details/source-stream-details.guard';
import { SourceStreamDetailsComponent } from '@tasks/aer/submit/source-streams/source-stream-details/source-streams-details.component';
import { SourceStreamsComponent } from '@tasks/aer/submit/source-streams/source-streams.component';

import { AdditionalDocumentsComponent } from './submit/additional-documents/additional-documents.component';
import { SummaryComponent } from './submit/additional-documents/summary/summary.component';
import { InstallationDetailsComponent } from './submit/installation-details/installation-details.component';
import { SubmitContainerComponent } from './submit/submit-container.component';

const routes: Routes = [
  {
    path: 'submit',
    children: [
      {
        path: '',
        data: { pageTitle: 'Emissions report' },
        component: SubmitContainerComponent,
      },
      {
        path: 'installation-details',
        data: { pageTitle: 'Installation and operator details' },
        component: InstallationDetailsComponent,
      },
      {
        path: 'prtr',
        data: { aerTask: 'pollutantRegisterActivities' },
        children: [
          {
            path: '',
            data: { pageTitle: 'European Pollutant Release and Transfer Register codes (PRTR)' },
            component: PrtrComponent,
          },
          {
            path: 'activity/:index',
            children: [
              {
                path: '',
                data: { pageTitle: 'PRTR - Select the relevant sector for the main activity at the installation' },
                component: ActivityComponent,
                canActivate: [ActivityGuard],
              },
              {
                path: 'delete',
                data: { pageTitle: 'Are you sure you want to delete this activity?' },
                component: PrtrDeleteComponent,
                canActivate: [ActivityGuard],
                canDeactivate: [PendingRequestGuard],
              },
            ],
          },
          {
            path: 'summary',
            data: { pageTitle: 'PRTR - Summary page' },
            component: PrtrSummaryComponent,
            canDeactivate: [PendingRequestGuard],
            canActivate: [PrtrSummaryGuard],
          },
        ],
      },
      {
        path: 'abbreviations',
        data: { aerTask: 'abbreviations' },
        children: [
          {
            path: '',
            data: { pageTitle: 'Definitions of abbreviations, acronyms and terminology' },
            component: AbbreviationsComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Definitions of abbreviations, acronyms and terminology - Summary page' },
            component: AbbreviationsSummaryComponent,
            canActivate: [SummaryGuard],
          },
        ],
      },
      {
        path: 'source-streams',
        data: { aerTask: 'source-streams' },
        children: [
          {
            path: '',
            data: { pageTitle: 'Source streams (fuels and materials)' },
            component: SourceStreamsComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'add',
            data: { pageTitle: 'Add a source stream' },
            component: SourceStreamDetailsComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'delete/:streamId',
            data: { pageTitle: 'Are you sure you want to delete this source stream?' },
            component: SourceStreamDeleteComponent,
            canActivate: [SourceStreamDetailsGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: ':streamId',
            data: { pageTitle: 'Edit source stream' },
            component: SourceStreamDetailsComponent,
            canActivate: [SourceStreamDetailsGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'monitoring-approaches',
        data: { permitTask: 'monitoringApproachTypes' },
        children: [
          {
            path: '',
            data: { pageTitle: 'Define monitoring approaches' },
            component: ApproachesComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'add',
            data: { pageTitle: 'Define monitoring approaches' },
            component: ApproachesAddComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'delete/:monitoringApproach',
            data: { pageTitle: 'Are you sure you want to delete this monitoring approach?' },
            component: ApproachesDeleteComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'help-with-monitoring-approaches',
            data: { pageTitle: 'Define monitoring approaches - Get help with monitoring approaches' },
            component: ApproachesHelpComponent,
          },
          {
            path: 'help-with-source-stream-categories',
            data: { pageTitle: 'Define monitoring approaches - Get help with source stream categories' },
            component: SourceStreamHelpComponent,
          },
        ],
      },
      {
        path: 'emission-points',
        data: { aerTask: 'emission-points' },
        children: [
          {
            path: '',
            data: { pageTitle: 'Emission points' },
            component: EmissionPointsComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'add',
            data: { pageTitle: 'Add an emission point' },
            component: EmissionPointDetailsComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'delete/:emissionPointId',
            data: { pageTitle: 'Are you sure you want to delete this emission point?' },
            component: EmissionPointDeleteComponent,
            canActivate: [EmissionPointDetailsGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: ':emissionPointId',
            data: { pageTitle: 'Edit emission point' },
            component: EmissionPointDetailsComponent,
            canActivate: [EmissionPointDetailsGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'confidentiality-statement',
        data: { aerTask: 'confidentialityStatement' },
        children: [
          {
            path: '',
            data: { pageTitle: 'Confidentiality statement' },
            component: ConfidentialityStatementComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Confidentiality statement - Summary page' },
            component: ConfidentialityStatementSummaryComponent,
            canActivate: [SummaryGuard],
          },
        ],
      },
      {
        path: 'emission-sources',
        data: { permitTask: 'emissionSources' },
        children: [
          {
            path: '',
            data: { pageTitle: 'Emission sources' },
            component: EmissionSourcesComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'add',
            data: { pageTitle: 'Add an emission source' },
            component: EmissionSourceDetailsComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'delete/:sourceId',
            data: { pageTitle: 'Are you sure you want to delete this emission source?' },
            component: EmissionSourceDeleteComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: ':sourceId',
            data: { pageTitle: 'Edit emission source' },
            component: EmissionSourceDetailsComponent,
            canActivate: [EmissionSourceDetailsGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'nace-codes',
        data: { aerTask: 'naceCodes' },
        children: [
          {
            path: '',
            data: { pageTitle: 'NACE codes' },
            component: NaceCodesComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'add',
            data: { pageTitle: 'Add a NACE code' },
            children: [
              {
                path: '',
                component: NaceCodeMainActivityComponent,
              },
              {
                path: 'sub-category',
                component: NaceCodeSubCategoryComponent,
                canActivate: [NaceCodeSubCategoryGuard],
              },
              {
                path: 'installation-activity',
                component: NaceCodeInstallationActivityComponent,
                canActivate: [NaceCodeInstallationActivityGuard],
                canDeactivate: [PendingRequestGuard],
              },
            ],
          },
          {
            path: 'delete/:naceCode',
            data: { pageTitle: 'Are you sure you want to delete this NACE code?' },
            component: NaceCodeDeleteComponent,
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
      {
        path: 'additional-documents',
        data: { aerTask: 'additionalDocuments' },
        children: [
          {
            path: '',
            data: { pageTitle: 'Additional documents and information' },
            component: AdditionalDocumentsComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'summary',
            data: { pageTitle: 'Additional documents and information - Summary page' },
            component: SummaryComponent,
            canActivate: [SummaryGuard],
          },
        ],
      },
      {
        path: 'regulated-activities',
        data: { permitTask: 'regulatedActivities' },
        children: [
          {
            path: '',
            data: { pageTitle: 'Regulated activities' },
            component: RegulatedActivitiesComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'add',
            data: { pageTitle: 'Add regulated activity' },
            component: RegulatedActivityComponent,
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: ':activityId',
            data: { pageTitle: 'Add regulated activity' },
            component: RegulatedActivityComponent,
            canActivate: [RegulatedActivityGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: ':activityId/capacity',
            data: { pageTitle: 'Add regulated activity capacity' },
            component: CapacityComponent,
            canActivate: [RegulatedActivityGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: ':activityId/crf-codes',
            data: { pageTitle: 'Add regulated activity crf codes' },
            component: CrfCodesComponent,
            canActivate: [RegulatedActivityGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: ':activityId/energy-crf-code',
            data: { pageTitle: 'Add regulated activity energy crf code' },
            component: EnergyCrfCodeComponent,
            canActivate: [RegulatedActivityGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: ':activityId/industrial-crf-code',
            data: { pageTitle: 'Add regulated activity industrial crf code' },
            component: IndustrialCrfCodeComponent,
            canActivate: [RegulatedActivityGuard],
            canDeactivate: [PendingRequestGuard],
          },
          {
            path: 'delete/:activityId',
            data: { pageTitle: 'Are you sure you want to delete this regulated activity?' },
            component: RegulatedActivityDeleteComponent,
            canActivate: [RegulatedActivityGuard],
            canDeactivate: [PendingRequestGuard],
          },
        ],
      },
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class AerRoutingModule {}

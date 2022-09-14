import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { PendingRequestGuard } from '@core/guards/pending-request.guard';
import { FileDownloadComponent } from '@shared/file-download/file-download.component';

import { AbbreviationsComponent } from './abbreviations/abbreviations.component';
import { AbbreviationsSummaryComponent } from './abbreviations/abbreviations-summary/abbreviations-summary.component';
import { AdditionalDocumentsComponent } from './additional-documents/additional-documents.component';
import { AdditionalDocumentsSummaryComponent } from './additional-documents/additional-documents-summary/additional-documents-summary.component';
import { AmendComponent } from './amend/amend.component';
import { AmendGuard } from './amend/amend.guard';
import { AmendSummaryComponent } from './amend/summary/amend-summary.component';
import { AmendSummaryGuard } from './amend/summary/amend-summary.guard';
import { ApplicationSubmittedComponent } from './application-submitted/application-submitted.component';
import { ApproachesComponent } from './approaches/approaches.component';
import { ApproachesAddComponent } from './approaches/approaches-add/approaches-add.component';
import { ApproachesDeleteComponent } from './approaches/approaches-delete/approaches-delete.component';
import { ApproachesHelpComponent } from './approaches/approaches-help/approaches-help.component';
import { SourceStreamHelpComponent } from './approaches/approaches-help/source-stream-help.component';
import { ApproachesPrepareComponent } from './approaches/approaches-prepare/approaches-prepare.component';
import { ApproachesPrepareSummaryComponent } from './approaches/approaches-prepare/approaches-prepare-summary/approaches-prepare-summary.component';
import { ApproachesSummaryComponent } from './approaches/approaches-summary/approaches-summary.component';
import { AnswersComponent } from './approaches/uncertainty-analysis/answers/answers.component';
import { AnswersGuard } from './approaches/uncertainty-analysis/answers/answers.guard';
import { SummaryComponent as UncertaintyAnalysisSummaryComponent } from './approaches/uncertainty-analysis/summary/summary.component';
import { UncertaintyAnalysisComponent } from './approaches/uncertainty-analysis/uncertainty-analysis.component';
import { UncertaintyAnalysisGuard } from './approaches/uncertainty-analysis/uncertainty-analysis.guard';
import { UploadFileComponent } from './approaches/uncertainty-analysis/upload-file/upload-file.component';
import { UploadFileGuard } from './approaches/uncertainty-analysis/upload-file/upload-file.guard';
import { ConfidentialityStatementComponent } from './confidentiality-statement/confidentiality-statement.component';
import { ConfidentialityStatementSummaryComponent } from './confidentiality-statement/confidentiality-statement-summary/confidentiality-statement-summary.component';
import { DetailsComponent } from './details/details.component';
import { EmissionPointDeleteComponent } from './emission-points/emission-point-delete/emission-point-delete.component';
import { EmissionPointDetailsComponent } from './emission-points/emission-point-details/emission-point-details.component';
import { EmissionPointDetailsGuard } from './emission-points/emission-point-details/emission-point-details.guard';
import { EmissionPointsComponent } from './emission-points/emission-points.component';
import { EmissionPointsSummaryComponent } from './emission-points/emission-points-summary/emission-points-summary.component';
import { EmissionSourceDeleteComponent } from './emission-sources/emission-source-delete/emission-source-delete.component';
import { EmissionSourceDetailsComponent } from './emission-sources/emission-source-details/emission-source-details.component';
import { EmissionSourceDetailsGuard } from './emission-sources/emission-source-details/emission-source-details.guard';
import { EmissionSourcesComponent } from './emission-sources/emission-sources.component';
import { EmissionSourcesSummaryComponent } from './emission-sources/emission-sources-summary/emission-sources-summary.component';
import { EmissionSummariesComponent } from './emission-summaries/emission-summaries.component';
import { EmissionSummariesSummaryComponent } from './emission-summaries/emission-summaries-summary/emission-summaries-summary.component';
import { EmissionSummariesSummaryGuard } from './emission-summaries/emission-summaries-summary/emission-summaries-summary.guard';
import { EmissionSummaryDeleteComponent } from './emission-summaries/emission-summary-delete/emission-summary-delete.component';
import { EmissionSummaryDetailsComponent } from './emission-summaries/emission-summary-details/emission-summary-details.component';
import { EmissionSummaryDetailsGuard } from './emission-summaries/emission-summary-details/emission-summary-details.guard';
import { EnvironmentalSystemComponent } from './environmental-system/environmental-system.component';
import { EnvironmentalSystemSummaryComponent } from './environmental-system/environmental-system-summary/environmental-system-summary.component';
import { EstimatedEmissionsComponent } from './estimated-emissions/estimated-emissions.component';
import { EstimatedEmissionsSummaryComponent } from './estimated-emissions/estimated-emissions-summary/estimated-emissions-summary.component';
import { InstallationDescriptionComponent } from './installation-description/installation-description.component';
import { InstallationDescriptionSummaryComponent } from './installation-description/installation-description-summary/installation-description-summary.component';
import { ManagementProceduresComponent } from './management-procedures/management-procedures.component';
import { ManagementProceduresSummaryComponent } from './management-procedures/management-procedures-summary/management-procedures-summary.component';
import { ManagementProceduresExistComponent } from './management-procedures-exist/management-procedures-exist.component';
import { ManagementProceduresExistSummaryComponent } from './management-procedures-exist/management-procedures-exist-summary/management-procedures-exist-summary.component';
import { MeasurementDeviceDeleteComponent } from './measurement-devices/measurement-device-delete/measurement-device-delete.component';
import { MeasurementDeviceDetailsComponent } from './measurement-devices/measurement-device-details/measurement-device-details.component';
import { MeasurementDeviceDetailsGuard } from './measurement-devices/measurement-device-details/measurement-device-details.guard';
import { MeasurementDevicesComponent } from './measurement-devices/measurement-devices.component';
import { MeasurementDevicesSummaryComponent } from './measurement-devices/measurement-devices-summary/measurement-devices-summary.component';
import { AnswersComponent as MonitoringMethodologyPlanAnswersComponent } from './monitoring-methodology-plan/answers/answers.component';
import { AnswersGuard as MonitoringMethodologyPlanAnswersGuard } from './monitoring-methodology-plan/answers/answers.guard';
import { MonitoringMethodologyPlanComponent } from './monitoring-methodology-plan/monitoring-methodology-plan.component';
import { MonitoringMethodologyPlanGuard } from './monitoring-methodology-plan/monitoring-methodology-plan.guard';
import { MonitoringMethodologyPlanSummaryComponent } from './monitoring-methodology-plan/summary/summary.component';
import { UploadFileComponent as MonitoringMethodologyPlanUploadFileComponent } from './monitoring-methodology-plan/upload-file/upload-file.component';
import { UploadFileGuard as MonitoringMethodologyPlanUploadFileGuard } from './monitoring-methodology-plan/upload-file/upload-file.guard';
import { MonitoringRolesComponent } from './monitoring-roles/monitoring-roles.component';
import { MonitoringRolesSummaryComponent } from './monitoring-roles/monitoring-roles-summary/monitoring-roles-summary.component';
import { OtherPermitsComponent } from './other-permits/other-permits.component';
import { OtherPermitsSummaryComponent } from './other-permits/other-permits-summary/other-permits-summary.component';
import { PermitApplicationGuard } from './permit-application.guard';
import { PermitApplicationActionGuard } from './permit-application-action.guard';
import { PermitRoute } from './permit-route.interface';
import { PermitTaskListContainerComponent } from './permit-task-list/permit-task-list-container.component';
import { PermitTypeComponent } from './permit-type/permit-type.component';
import { PermitTypeSummaryComponent } from './permit-type/permit-type-summary/permit-type-summary.component';
import { PermitTypeSummaryGuard } from './permit-type/permit-type-summary/permit-type-summary.guard';
import { RegulatedActivitiesComponent } from './regulated-activities/regulated-activities.component';
import { RegulatedActivitiesSummaryComponent } from './regulated-activities/regulated-activities-summary/regulated-activities-summary.component';
import { SummaryGuard } from './shared/summary.guard';
import { SiteDiagramComponent } from './site-diagram/site-diagram.component';
import { SiteDiagramSummaryComponent } from './site-diagram/site-diagram-summary/site-diagram-summary.component';
import { SourceStreamDeleteComponent } from './source-streams/source-stream-delete/source-stream-delete.component';
import { SourceStreamDetailsGuard } from './source-streams/source-stream-details/source-stream-details.guard';
import { SourceStreamDetailsComponent } from './source-streams/source-stream-details/source-streams-details.component';
import { SourceStreamsComponent } from './source-streams/source-streams.component';
import { SourceStreamsSummaryComponent } from './source-streams/source-streams-summary/source-streams-summary.component';
import { SummaryComponent } from './summary/summary.component';
import { AboutVariationComponent } from './variation/about-variation/about-variation.component';
import { AboutVariationGuard } from './variation/about-variation/about-variation.guard';
import { AnswersComponent as AboutVariationAnswersComponent } from './variation/about-variation/answers/answers.component';
import { AnswersGuard as PermitVariationDetailsAnswersGuard } from './variation/about-variation/answers/answers.guard';
import { ChangesComponent } from './variation/about-variation/changes/changes.component';
import { ChangesGuard } from './variation/about-variation/changes/changes.guard';
import { SummaryComponent as AboutVariationSummaryComponent } from './variation/about-variation/summary/summary.component';
import { SummaryGuard as PermitVariationDetailsSummaryGuard } from './variation/about-variation/summary/summary.guard';
import { SummaryComponent as SummaryVariationComponent } from './variation/summary/summary.component';

const permitRoutes: PermitRoute[] = [
  {
    path: '',
    data: { pageTitle: 'Apply for a permit' },
    component: PermitTaskListContainerComponent,
  },
  {
    path: 'review',
    loadChildren: () => import('./review/review.module').then((m) => m.ReviewModule),
  },
  {
    path: 'summary',
    component: SummaryComponent,
  },
  {
    path: 'variation-summary',
    component: SummaryVariationComponent,
  },
  {
    path: 'application-submitted',
    component: ApplicationSubmittedComponent,
  },
  {
    path: 'permit-type',
    data: { permitTask: 'permitType' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Permit type' },
        component: PermitTypeComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Permit type - Summary page' },
        component: PermitTypeSummaryComponent,
        canActivate: [PermitTypeSummaryGuard],
      },
    ],
  },
  {
    path: 'details/summary',
    data: { pageTitle: 'Installation and operator details' },
    component: DetailsComponent,
  },
  {
    path: 'other-permits',
    data: { permitTask: 'environmentalPermitsAndLicences' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Other environmental permits or licences' },
        component: OtherPermitsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Other environmental permits or licences - Summary page' },
        component: OtherPermitsSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'description',
    data: { permitTask: 'installationDescription' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Description of the installation' },
        component: InstallationDescriptionComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Description of the installation - Summary page' },
        component: InstallationDescriptionSummaryComponent,
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
        data: { pageTitle: 'Select the regulated activities that happen at the installation' },
        component: RegulatedActivitiesComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'All the regulated activities carried out at the installation - Summary page' },
        component: RegulatedActivitiesSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'category',
    data: { permitTask: 'estimatedAnnualEmissions' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Estimated annual CO2e' },
        component: EstimatedEmissionsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Estimated annual CO2e - Summary page' },
        component: EstimatedEmissionsSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'management-procedures-exist',
    data: { permitTask: 'managementProceduresExist' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Management procedures exist ' },
        component: ManagementProceduresExistComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Management procedures exist - Summary page' },
        component: ManagementProceduresExistSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'monitoring-roles',
    data: { permitTask: 'monitoringReporting' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring and reporting roles' },
        component: MonitoringRolesComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Monitoring and reporting roles - Summary page' },
        component: MonitoringRolesSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'responsibilities',
    data: { permitTask: 'assignmentOfResponsibilities' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Assignment of responsibilities' },
        component: ManagementProceduresComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Assignment of responsibilities - Summary page' },
        component: ManagementProceduresSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'appropriateness',
    data: { permitTask: 'monitoringPlanAppropriateness' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring plan appropriateness' },
        component: ManagementProceduresComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Monitoring plan appropriateness - Summary page' },
        component: ManagementProceduresSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'data-flow-activities',
    data: { permitTask: 'dataFlowActivities' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Data flow activities' },
        component: ManagementProceduresComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Data flow activities - Summary page' },
        component: ManagementProceduresSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'review-validation',
    data: { permitTask: 'reviewAndValidationOfData' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Review and validation of data' },
        component: ManagementProceduresComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Review and validation of data - Summary page' },
        component: ManagementProceduresSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'environmental-system',
    data: { permitTask: 'environmentalManagementSystem' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Environmental management system' },
        component: EnvironmentalSystemComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Environmental management system - Summary page' },
        component: EnvironmentalSystemSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'outsourced-activities',
    data: { permitTask: 'controlOfOutsourcedActivities' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Control of outsourced activities' },
        component: ManagementProceduresComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Control of outsourced activities - Summary page' },
        component: ManagementProceduresSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'qa-activities',
    data: { permitTask: 'qaDataFlowActivities' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Quality assurance of IT used for data flow activities' },
        component: ManagementProceduresComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Quality assurance of IT used for data flow activities - Summary page' },
        component: ManagementProceduresSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'qa-metering-measuring',
    data: { permitTask: 'qaMeteringAndMeasuringEquipment' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Quality assurance of metering and measuring equipment' },
        component: ManagementProceduresComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Quality assurance of metering and measuring equipment - Summary page' },
        component: ManagementProceduresSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'assess-control-risk',
    data: { permitTask: 'assessAndControlRisk' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Assessing and controlling risks' },
        component: ManagementProceduresComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Assessing and controlling risks - Summary page' },
        component: ManagementProceduresSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'corrections',
    data: { permitTask: 'correctionsAndCorrectiveActions' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Corrections and corrective actions' },
        component: ManagementProceduresComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Corrections and corrective actions - Summary page' },
        component: ManagementProceduresSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'record-keeping',
    data: { permitTask: 'recordKeepingAndDocumentation' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Record keeping and documentation' },
        component: ManagementProceduresComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Record keeping and documentation - Summary page' },
        component: ManagementProceduresSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'source-streams',
    data: { permitTask: 'sourceStreams' },
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
        data: { pageTitle: 'Are you sure you want to delete this soource stream?' },
        component: SourceStreamDeleteComponent,
        canActivate: [SourceStreamDetailsGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Source streams (fuels and materials) - Summary page' },
        component: SourceStreamsSummaryComponent,
        canActivate: [SummaryGuard],
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
        path: 'summary',
        data: { pageTitle: 'Emission sources - Summary page' },
        component: EmissionSourcesSummaryComponent,
        canActivate: [SummaryGuard],
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
    path: 'emission-points',
    data: { permitTask: 'emissionPoints' },
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
        path: 'summary',
        data: { pageTitle: 'Emission points - Summary page' },
        component: EmissionPointsSummaryComponent,
        canActivate: [SummaryGuard],
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
    path: 'emission-summaries',
    data: { permitTask: 'emissionSummaries' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Emissions summaries and regulated activities' },
        component: EmissionSummariesComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'add',
        data: { pageTitle: 'Add an emission summary' },
        component: EmissionSummaryDetailsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'delete/:emissionSummaryIndex',
        data: { pageTitle: 'Are you sure you want to delete this emission summary?' },
        component: EmissionSummaryDeleteComponent,
        canActivate: [EmissionSummaryDetailsGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Emissions summaries and regulated activities - Summary page' },
        component: EmissionSummariesSummaryComponent,
        canActivate: [SummaryGuard, EmissionSummariesSummaryGuard],
      },
      {
        path: ':emissionSummaryIndex',
        data: { pageTitle: 'Edit emission summary' },
        component: EmissionSummaryDetailsComponent,
        canActivate: [EmissionSummaryDetailsGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'measurement-devices',
    data: { permitTask: 'measurementDevicesOrMethods' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Measurement devices or methods' },
        component: MeasurementDevicesComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'add',
        data: { pageTitle: 'Add a measurement device or method' },
        component: MeasurementDeviceDetailsComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'delete/:deviceId',
        data: { pageTitle: 'Are you sure you want to delete this measurement device or method?' },
        component: MeasurementDeviceDeleteComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Measurement devices or methods - Summary page' },
        component: MeasurementDevicesSummaryComponent,
        canActivate: [SummaryGuard],
      },
      {
        path: ':deviceId',
        data: { pageTitle: 'Edit measurement device or method' },
        component: MeasurementDeviceDetailsComponent,
        canActivate: [MeasurementDeviceDetailsGuard],
        canDeactivate: [PendingRequestGuard],
      },
    ],
  },
  {
    path: 'site-diagram',
    data: { permitTask: 'siteDiagrams' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Site diagram' },
        component: SiteDiagramComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Site diagram - Summary page' },
        component: SiteDiagramSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'confidentiality-statement',
    data: { permitTask: 'confidentialityStatement' },
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
    path: 'abbreviations',
    data: { permitTask: 'abbreviations' },
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
    path: 'monitoring-methodology-plan',
    data: { permitTask: 'monitoringMethodologyPlans' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Monitoring Methodology Plan' },
        component: MonitoringMethodologyPlanComponent,
        canActivate: [MonitoringMethodologyPlanGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'upload-file',
        data: { pageTitle: 'Monitoring Methodology Plan - Upload file' },
        component: MonitoringMethodologyPlanUploadFileComponent,
        canActivate: [MonitoringMethodologyPlanUploadFileGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'answers',
        data: { pageTitle: 'Monitoring Methodology Plan - Confirm answers' },
        component: MonitoringMethodologyPlanAnswersComponent,
        canActivate: [MonitoringMethodologyPlanAnswersGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Monitoring Methodology Plan - Summary page' },
        component: MonitoringMethodologyPlanSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'additional-documents',
    data: { permitTask: 'additionalDocuments' },
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
        component: AdditionalDocumentsSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'file-download/:uuid',
    component: FileDownloadComponent,
  },
  {
    path: 'monitoring-approaches-prepare',
    data: { permitTask: 'monitoringApproachesPrepare' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Preparing to define monitoring approaches' },
        component: ApproachesPrepareComponent,
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Preparing to define monitoring approaches - Summary page' },
        component: ApproachesPrepareSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'monitoring-approaches',
    data: { permitTask: 'monitoringApproaches' },
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
        path: 'summary',
        data: { pageTitle: 'Define monitoring approaches - Summary page' },
        component: ApproachesSummaryComponent,
        canActivate: [SummaryGuard],
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
    path: 'calculation',
    data: { permitTask: 'CALCULATION' },
    loadChildren: () => import('./approaches/calculation/calculation.module').then((m) => m.CalculationModule),
  },
  {
    path: 'nitrous-oxide',
    data: { permitTask: 'N2O' },
    loadChildren: () => import('./approaches/n2o/n2o.module').then((m) => m.N2oModule),
  },
  {
    path: 'inherent-co2',
    data: { permitTask: 'INHERENT_CO2' },
    loadChildren: () => import('./approaches/inherent-co2/inherent-co2.module').then((m) => m.InherentCo2Module),
  },
  {
    path: 'transferred-co2',
    data: { permitTask: 'TRANSFERRED_CO2' },
    loadChildren: () =>
      import('./approaches/transferred-co2/transferred-co2.module').then((m) => m.TransferredCO2Module),
  },
  {
    path: 'fall-back',
    data: { permitTask: 'FALLBACK' },
    loadChildren: () => import('./approaches/fallback/fallback.module').then((m) => m.FallbackModule),
  },
  {
    path: 'measurement',
    data: { permitTask: 'MEASUREMENT' },
    loadChildren: () => import('./approaches/measurement/measurement.module').then((m) => m.MeasurementModule),
  },
  {
    path: 'pfc',
    data: { permitTask: 'PFC' },
    loadChildren: () => import('./approaches/pfc/pfc.module').then((m) => m.PFCModule),
  },
  {
    path: 'uncertainty-analysis',
    data: { permitTask: 'uncertaintyAnalysis' },
    children: [
      {
        path: '',
        data: { pageTitle: 'Uncertainty analysis' },
        component: UncertaintyAnalysisComponent,
        canActivate: [UncertaintyAnalysisGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'upload-file',
        data: { pageTitle: 'Uncertainty analysis' },
        component: UploadFileComponent,
        canActivate: [UploadFileGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'answers',
        data: { pageTitle: 'Uncertainty analysis - Confirm answers' },
        component: AnswersComponent,
        canActivate: [AnswersGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Uncertainty analysis - Summary page' },
        component: UncertaintyAnalysisSummaryComponent,
        canActivate: [SummaryGuard],
      },
    ],
  },
  {
    path: 'about',
    children: [
      {
        path: '',
        data: { pageTitle: 'About variation - Describe the details' },
        component: AboutVariationComponent,
        canActivate: [AboutVariationGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'changes',
        data: { pageTitle: 'About variation - What type of changes are you making?' },
        component: ChangesComponent,
        canActivate: [ChangesGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'answers',
        data: { pageTitle: 'About variation - Answers' },
        component: AboutVariationAnswersComponent,
        canActivate: [PermitVariationDetailsAnswersGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'About variation - Summary' },
        canActivate: [PermitVariationDetailsSummaryGuard],
        component: AboutVariationSummaryComponent,
      },
    ],
  },
  {
    path: 'amend/:section',
    children: [
      {
        path: '',
        data: { pageTitle: 'Amend information' },
        component: AmendComponent,
        canActivate: [AmendGuard],
        canDeactivate: [PendingRequestGuard],
      },
      {
        path: 'summary',
        data: { pageTitle: 'Amend information - Summary page' },
        component: AmendSummaryComponent,
        canActivate: [AmendSummaryGuard],
      },
    ],
  },
];

const routes: PermitRoute[] = [
  {
    path: 'action/:actionId',
    canActivate: [PermitApplicationActionGuard],
    canDeactivate: [PermitApplicationActionGuard],
    children: permitRoutes,
  },
  {
    path: ':taskId',
    canActivate: [PermitApplicationGuard],
    canDeactivate: [PermitApplicationGuard],
    children: permitRoutes,
  },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class PermitApplicationRoutingModule {}

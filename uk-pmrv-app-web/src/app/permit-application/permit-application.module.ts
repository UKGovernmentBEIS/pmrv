import { NgModule } from '@angular/core';

import { SharedModule } from '../shared/shared.module';
import { AbbreviationsComponent } from './abbreviations/abbreviations.component';
import { AbbreviationsSummaryComponent } from './abbreviations/abbreviations-summary/abbreviations-summary.component';
import { AdditionalDocumentsComponent } from './additional-documents/additional-documents.component';
import { AdditionalDocumentsSummaryComponent } from './additional-documents/additional-documents-summary/additional-documents-summary.component';
import { AmendComponent } from './amend/amend.component';
import { AmendSummaryComponent } from './amend/summary/amend-summary.component';
import { AmendSummaryTemplateComponent } from './amend/summary/amend-summary-template.component';
import { ApplicationSubmittedComponent } from './application-submitted/application-submitted.component';
import { ApproachesComponent } from './approaches/approaches.component';
import { ApproachesAddComponent } from './approaches/approaches-add/approaches-add.component';
import { ApproachesDeleteComponent } from './approaches/approaches-delete/approaches-delete.component';
import { ApproachesHelpComponent } from './approaches/approaches-help/approaches-help.component';
import { ApproachesPrepareComponent } from './approaches/approaches-prepare/approaches-prepare.component';
import { ApproachesPrepareSummaryComponent } from './approaches/approaches-prepare/approaches-prepare-summary/approaches-prepare-summary.component';
import { ApproachesPrepareTemplateComponent } from './approaches/approaches-prepare/approaches-prepare-template.component';
import { ApproachesSummaryComponent } from './approaches/approaches-summary/approaches-summary.component';
import { AnswersComponent } from './approaches/uncertainty-analysis/answers/answers.component';
import { SummaryComponent as UncertaintyAnalysisSummaryComponent } from './approaches/uncertainty-analysis/summary/summary.component';
import { UncertaintyAnalysisComponent } from './approaches/uncertainty-analysis/uncertainty-analysis.component';
import { UploadFileComponent } from './approaches/uncertainty-analysis/upload-file/upload-file.component';
import { ConfidentialityStatementComponent } from './confidentiality-statement/confidentiality-statement.component';
import { ConfidentialityStatementSummaryComponent } from './confidentiality-statement/confidentiality-statement-summary/confidentiality-statement-summary.component';
import { DetailsComponent } from './details/details.component';
import { EmissionPointDeleteComponent } from './emission-points/emission-point-delete/emission-point-delete.component';
import { EmissionPointDetailsComponent } from './emission-points/emission-point-details/emission-point-details.component';
import { EmissionPointsComponent } from './emission-points/emission-points.component';
import { EmissionPointsSummaryComponent } from './emission-points/emission-points-summary/emission-points-summary.component';
import { EmissionSourceDeleteComponent } from './emission-sources/emission-source-delete/emission-source-delete.component';
import { EmissionSourceDetailsComponent } from './emission-sources/emission-source-details/emission-source-details.component';
import { EmissionSourcesComponent } from './emission-sources/emission-sources.component';
import { EmissionSourcesSummaryComponent } from './emission-sources/emission-sources-summary/emission-sources-summary.component';
import { EmissionSummariesComponent } from './emission-summaries/emission-summaries.component';
import { EmissionSummariesSummaryComponent } from './emission-summaries/emission-summaries-summary/emission-summaries-summary.component';
import { EmissionSummaryDeleteComponent } from './emission-summaries/emission-summary-delete/emission-summary-delete.component';
import { EmissionSummaryDetailsComponent } from './emission-summaries/emission-summary-details/emission-summary-details.component';
import { EnvironmentalSystemComponent } from './environmental-system/environmental-system.component';
import { EnvironmentalSystemSummaryComponent } from './environmental-system/environmental-system-summary/environmental-system-summary.component';
import { EstimatedEmissionsComponent } from './estimated-emissions/estimated-emissions.component';
import { EstimatedEmissionsSummaryComponent } from './estimated-emissions/estimated-emissions-summary/estimated-emissions-summary.component';
import { InstallationDescriptionComponent } from './installation-description/installation-description.component';
import { InstallationDescriptionSummaryComponent } from './installation-description/installation-description-summary/installation-description-summary.component';
import { ManagementProceduresComponent } from './management-procedures/management-procedures.component';
import { ManagementProceduresBodyPipe } from './management-procedures/management-procedures-body.pipe';
import { ManagementProceduresHeadingPipe } from './management-procedures/management-procedures-heading.pipe';
import { ManagementProceduresSummaryComponent } from './management-procedures/management-procedures-summary/management-procedures-summary.component';
import { ManagementProceduresExistComponent } from './management-procedures-exist/management-procedures-exist.component';
import { ManagementProceduresExistSummaryComponent } from './management-procedures-exist/management-procedures-exist-summary/management-procedures-exist-summary.component';
import { MeasurementDeviceDeleteComponent } from './measurement-devices/measurement-device-delete/measurement-device-delete.component';
import { MeasurementDeviceDetailsComponent } from './measurement-devices/measurement-device-details/measurement-device-details.component';
import { MeasurementDevicesComponent } from './measurement-devices/measurement-devices.component';
import { MeasurementDevicesSummaryComponent } from './measurement-devices/measurement-devices-summary/measurement-devices-summary.component';
import { AnswersComponent as MonitoringMethodologyPlanAnswersComponent } from './monitoring-methodology-plan/answers/answers.component';
import { MonitoringMethodologyPlanComponent } from './monitoring-methodology-plan/monitoring-methodology-plan.component';
import { MonitoringMethodologyPlanSummaryComponent } from './monitoring-methodology-plan/summary/summary.component';
import { UploadFileComponent as MonitoringMethodologyPlanUploadFileComponent } from './monitoring-methodology-plan/upload-file/upload-file.component';
import { MonitoringRolesComponent } from './monitoring-roles/monitoring-roles.component';
import { MonitoringRolesSummaryComponent } from './monitoring-roles/monitoring-roles-summary/monitoring-roles-summary.component';
import { OtherPermitsComponent } from './other-permits/other-permits.component';
import { OtherPermitsSummaryComponent } from './other-permits/other-permits-summary/other-permits-summary.component';
import { PermitApplicationRoutingModule } from './permit-application-routing.module';
import { PermitTaskListContainerComponent } from './permit-task-list/permit-task-list-container.component';
import { PermitTypeComponent } from './permit-type/permit-type.component';
import { PermitTypeSummaryComponent } from './permit-type/permit-type-summary/permit-type-summary.component';
import { RegulatedActivitiesComponent } from './regulated-activities/regulated-activities.component';
import { RegulatedActivitiesSummaryComponent } from './regulated-activities/regulated-activities-summary/regulated-activities-summary.component';
import { SharedPermitModule } from './shared/shared-permit.module';
import { SiteDiagramComponent } from './site-diagram/site-diagram.component';
import { SiteDiagramSummaryComponent } from './site-diagram/site-diagram-summary/site-diagram-summary.component';
import { SourceStreamDeleteComponent } from './source-streams/source-stream-delete/source-stream-delete.component';
import { SourceStreamDetailsComponent } from './source-streams/source-stream-details/source-streams-details.component';
import { SourceStreamsComponent } from './source-streams/source-streams.component';
import { SourceStreamsSummaryComponent } from './source-streams/source-streams-summary/source-streams-summary.component';
import { SummaryComponent } from './summary/summary.component';
import { AboutVariationComponent } from './variation/about-variation/about-variation.component';
import { AnswersComponent as AboutVariationAnswersComponent } from './variation/about-variation/answers/answers.component';
import { ChangesComponent as AboutVariationChangesComponent } from './variation/about-variation/changes/changes.component';
import { SummaryComponent as AboutVariationSummaryComponent } from './variation/about-variation/summary/summary.component';
import { SummaryDetailsComponent as AboutVariationSummaryDetailsComponent } from './variation/about-variation/summary/summary-details.component';
import { SummaryComponent as SummaryVariationComponent } from './variation/summary/summary.component';

@NgModule({
  declarations: [
    AbbreviationsComponent,
    AbbreviationsSummaryComponent,
    AboutVariationAnswersComponent,
    AboutVariationChangesComponent,
    AboutVariationComponent,
    AboutVariationSummaryComponent,
    AboutVariationSummaryDetailsComponent,
    AdditionalDocumentsComponent,
    AdditionalDocumentsSummaryComponent,
    AmendComponent,
    AmendSummaryComponent,
    AmendSummaryTemplateComponent,
    AnswersComponent,
    ApplicationSubmittedComponent,
    ApproachesAddComponent,
    ApproachesComponent,
    ApproachesDeleteComponent,
    ApproachesHelpComponent,
    ApproachesPrepareComponent,
    ApproachesPrepareSummaryComponent,
    ApproachesPrepareTemplateComponent,
    ApproachesSummaryComponent,
    ConfidentialityStatementComponent,
    ConfidentialityStatementSummaryComponent,
    DetailsComponent,
    EmissionPointDeleteComponent,
    EmissionPointDetailsComponent,
    EmissionPointsComponent,
    EmissionPointsSummaryComponent,
    EmissionSourceDeleteComponent,
    EmissionSourceDetailsComponent,
    EmissionSourcesComponent,
    EmissionSourcesSummaryComponent,
    EmissionSummariesComponent,
    EmissionSummariesSummaryComponent,
    EmissionSummaryDeleteComponent,
    EmissionSummaryDetailsComponent,
    EnvironmentalSystemComponent,
    EnvironmentalSystemSummaryComponent,
    EstimatedEmissionsComponent,
    EstimatedEmissionsSummaryComponent,
    InstallationDescriptionComponent,
    InstallationDescriptionSummaryComponent,
    ManagementProceduresBodyPipe,
    ManagementProceduresComponent,
    ManagementProceduresExistComponent,
    ManagementProceduresExistSummaryComponent,
    ManagementProceduresHeadingPipe,
    ManagementProceduresSummaryComponent,
    MeasurementDeviceDeleteComponent,
    MeasurementDeviceDetailsComponent,
    MeasurementDevicesComponent,
    MeasurementDevicesSummaryComponent,
    MonitoringMethodologyPlanAnswersComponent,
    MonitoringMethodologyPlanComponent,
    MonitoringMethodologyPlanSummaryComponent,
    MonitoringMethodologyPlanUploadFileComponent,
    MonitoringRolesComponent,
    MonitoringRolesSummaryComponent,
    OtherPermitsComponent,
    OtherPermitsSummaryComponent,
    PermitTaskListContainerComponent,
    PermitTypeComponent,
    PermitTypeSummaryComponent,
    RegulatedActivitiesComponent,
    RegulatedActivitiesSummaryComponent,
    SiteDiagramComponent,
    SiteDiagramSummaryComponent,
    SourceStreamDeleteComponent,
    SourceStreamDetailsComponent,
    SourceStreamsComponent,
    SourceStreamsSummaryComponent,
    SummaryComponent,
    SummaryVariationComponent,
    UncertaintyAnalysisComponent,
    UncertaintyAnalysisSummaryComponent,
    UploadFileComponent,
  ],
  imports: [PermitApplicationRoutingModule, SharedModule, SharedPermitModule],
})
export class PermitApplicationModule {}

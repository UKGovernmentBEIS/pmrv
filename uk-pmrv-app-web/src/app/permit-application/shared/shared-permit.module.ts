import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from '@shared/shared.module';

import { ApproachTaskPipe } from '../approaches/approach-task.pipe';
import { SourceStreamHelpComponent } from '../approaches/approaches-help/source-stream-help.component';
import { ApproachesSummaryTemplateComponent } from '../approaches/approaches-summary/approaches-summary-template.component';
import { SummaryTemplateComponent as CalculationDescriptionSummaryTemplateComponent } from '../approaches/calculation/description/summary/summary-template.component';
import { SummaryDetailsComponent as CalculationPlanSummaryDetailsComponent } from '../approaches/calculation/sampling-plan/summary-details/summary-details.component';
import { SummaryTemplateComponent as FallbackDescriptionSummaryTemplateComponent } from '../approaches/fallback/description/summary/summary-template.component';
import { InherentCO2DescriptionSummaryTemplateComponent } from '../approaches/inherent-co2/description/summary/inherent-co2-description-summary-template.component';
import { SummaryTemplateComponent as MeasurementDescriptionSummaryTemplateComponent } from '../approaches/measurement/description/summary/summary-template.component';
import { OptionalSummaryTemplateComponent as MeasurementOptionalSummaryTemplateComponent } from '../approaches/measurement/optional/summary/optional-summary-template.component';
import { SummaryTemplateComponent as N2ODescriptionSummaryTemplateComponent } from '../approaches/n2o/description/summary/summary-template.component';
import { SummaryTemplateComponent as N2OGasSummaryTemplateComponent } from '../approaches/n2o/gas/summary/summary-template.component';
import { SummaryTemplateComponent as PfcDescriptionSummaryTemplateComponent } from '../approaches/pfc/description/summary/summary-template.component';
import { SummaryDetailsComponent as Tier2EmissionFactorSummaryDetailsComponent } from '../approaches/pfc/emission-factor/summary/summary-details.component';
import { SummaryTemplateComponent as PfcTypesSummaryTemplateComponent } from '../approaches/pfc/types/summary/summary-template.component';
import { SummaryTemplateComponent as AccountingSummaryTemplateComponent } from '../approaches/transferred-co2/accounting/summary/summary-template.component';
import { SummaryTemplateComponent as TransferredCO2DescriptionSummaryTemplateComponent } from '../approaches/transferred-co2/description/summary/summary-template.component';
import { InstallationTypePipe } from '../approaches/transferred-co2/installations/installation-type.pipe';
import { SummaryTemplateComponent as TransferredCO2InstallationsSummaryTemplateComponent } from '../approaches/transferred-co2/installations/summary/summary-template.component';
import { SummaryTemplateComponent as TemperatureSummaryTemplateComponent } from '../approaches/transferred-co2/temperature/summary/summary-template.component';
import { SummaryDetailsComponent as UncertaintyAnalysisSummaryDetailsComponent } from '../approaches/uncertainty-analysis/summary/summary-details.component';
import { ConfidentialityStatementSummaryTemplateComponent } from '../confidentiality-statement/confidentiality-statement-summary/confidentiality-statement-summary-template.component';
import { RegulatedActivityPipe } from '../emission-summaries/emission-summaries-summary/regulated-activity.pipe';
import { SourceStreamPipe } from '../emission-summaries/emission-summaries-summary/source-stream.pipe';
import { EnvironmentalSystemSummaryTemplateComponent } from '../environmental-system/environmental-system-summary/environmental-system-summary-template.component';
import { EmissionsSummaryTemplateComponent } from '../estimated-emissions/estimated-emissions-summary/emissions-summary-template.component';
import { DescriptionSummaryTemplateComponent } from '../installation-description/installation-description-summary/description-summary-template.component';
import { IsDataFlowActivitiesPipe } from '../management-procedures/management-procedures-summary/is-data-flow-activities.pipe';
import { ManagementProceduresSummaryTemplateComponent } from '../management-procedures/management-procedures-summary/management-procedures-summary-template.component';
import { ManagementProceduresExistTemplateComponent } from '../management-procedures-exist/management-procedures-exist-summary/management-procedures-exist-template.component';
import { MeasurementDevicesTypePipe } from '../measurement-devices/measurement-devices-summary/measurement-devices-type.pipe';
import { MonitoringMethodologyPlanSummaryDetailsComponent } from '../monitoring-methodology-plan/summary/summary-details.component';
import { MonitoringRolesSummaryTemplateComponent } from '../monitoring-roles/monitoring-roles-summary/monitoring-roles-summary-template.component';
import { PermitsSummaryTemplateComponent } from '../other-permits/other-permits-summary/permits-summary-template.component';
import { PermitTypeSummaryTemplateComponent } from '../permit-type/permit-type-summary/permit-type-summary-template.component';
import { RegulatedActivitiesSummaryTemplateComponent } from '../regulated-activities/regulated-activities-summary/regulated-activities-summary-template.component';
import { SiteDiagramSummaryTemplateComponent } from '../site-diagram/site-diagram-summary/site-diagram-summary-template.component';
import { ApproachReturnLinkComponent } from './approach-return-link/approach-return-link.component';
import { AppliedStandardFormComponent } from './approaches/applied-standard-form.component';
import { AppliedStandardFormSummaryComponent } from './approaches/applied-standard-form-summary.component';
import { ReviewGroupDecisionComponent } from './components/decision/review-group-decision.component';
import { AnswersComponent } from './emissions/answers/answers.component';
import { EmissionsComponent } from './emissions/emissions.component';
import { JustificationComponent } from './emissions/justification/justification.component';
import { MeasuredEmissionsOverviewComponent } from './emissions/overview/measured-emissions-overview.component';
import { SummaryComponent } from './emissions/summary/summary.component';
import { ListReturnLinkComponent } from './list-return-link/list-return-link.component';
import { PermitTaskComponent } from './permit-task/permit-task.component';
import { PermitTaskListComponent } from './permit-task-list/permit-task-list.component';
import { PermitTaskReviewComponent } from './permit-task-review/permit-task-review.component';
import { AppliedTierPipe } from './pipes/applied-tier.pipe';
import { CategoryTypeNamePipe } from './pipes/category-type-name.pipe';
import { DecisionFilesPipe } from './pipes/decision-files.pipe';
import { EmissionPointPipe } from './pipes/emission-point.pipe';
import { EmissionSourcePipe } from './pipes/emission-source.pipe';
import { FindSourceStreamPipe } from './pipes/find-source-stream.pipe';
import { InstallationCategoryTypePipe } from './pipes/installation-category-type.pipe';
import { MeasurementDevicesLabelPipe } from './pipes/measurement-devices-label.pipe';
import { MeasurementDeviceOrMethodPipe } from './pipes/measurement-devices-or-methods.pipe';
import { MeasurementDeviceOrMethodNamePipe } from './pipes/measurement-devices-or-methods-name.pipe';
import { MeteringUncertaintyNamePipe } from './pipes/metering-uncertainty-name.pipe';
import { ReviewGroupPipe } from './pipes/review-group.pipe';
import { ReviewGroupStatusPipe } from './pipes/review-group-status.pipe';
import { SamplingFrequencyPipe } from './pipes/sampling-frequency.pipe';
import { SourceStreamCategoryNamePipe } from './pipes/source-stream-category-name.pipe';
import { TaskPipe } from './pipes/task.pipe';
import { TaskProcedureFormPipe } from './pipes/task-procedure-form.pipe';
import { TaskProcedureOptionalFormPipe } from './pipes/task-procedure-optional-form.pipe';
import { TaskStatusPipe } from './pipes/task-status.pipe';
import { TierSourceStreamNamePipe } from './pipes/tier-source-stream-name.pipe';
import { ProcedureFormComponent } from './procedure-form/procedure-form.component';
import { ProcedureFormSummaryComponent } from './procedure-form-summary/procedure-form-summary.component';
import { SiteEmissionsComponent } from './site-emissions/site-emissions.component';
import { SiteEmissionsPercentagePipe } from './site-emissions/site-emissions-percentage.pipe';

const declarations = [
  RegulatedActivityPipe,
  AppliedStandardFormComponent,
  AppliedStandardFormSummaryComponent,
  InstallationTypePipe,
  ListReturnLinkComponent,
  MeasurementDevicesTypePipe,
  SourceStreamPipe,
  ApproachReturnLinkComponent,
  ApproachTaskPipe,
  PermitTaskComponent,
  PermitTaskReviewComponent,
  TaskPipe,
  TaskProcedureFormPipe,
  TaskProcedureOptionalFormPipe,
  TaskStatusPipe,
  PermitTaskListComponent,
  ProcedureFormComponent,
  ProcedureFormSummaryComponent,
  EmissionSourcePipe,
  EmissionPointPipe,
  CategoryTypeNamePipe,
  FindSourceStreamPipe,
  SourceStreamCategoryNamePipe,
  TierSourceStreamNamePipe,
  AnswersComponent,
  MeasuredEmissionsOverviewComponent,
  SummaryComponent,
  SamplingFrequencyPipe,
  MeasurementDevicesLabelPipe,
  AppliedTierPipe,
  JustificationComponent,
  EmissionsComponent,
  PermitsSummaryTemplateComponent,
  PermitTypeSummaryTemplateComponent,
  RegulatedActivitiesSummaryTemplateComponent,
  InstallationCategoryTypePipe,
  DescriptionSummaryTemplateComponent,
  EmissionsSummaryTemplateComponent,
  MonitoringMethodologyPlanSummaryDetailsComponent,
  ReviewGroupDecisionComponent,
  ConfidentialityStatementSummaryTemplateComponent,
  SiteDiagramSummaryTemplateComponent,
  InherentCO2DescriptionSummaryTemplateComponent,
  IsDataFlowActivitiesPipe,
  ManagementProceduresSummaryTemplateComponent,
  MonitoringRolesSummaryTemplateComponent,
  EnvironmentalSystemSummaryTemplateComponent,
  ApproachesSummaryTemplateComponent,
  ReviewGroupPipe,
  MeasurementDescriptionSummaryTemplateComponent,
  MeasurementOptionalSummaryTemplateComponent,
  N2ODescriptionSummaryTemplateComponent,
  N2OGasSummaryTemplateComponent,
  PfcDescriptionSummaryTemplateComponent,
  PfcTypesSummaryTemplateComponent,
  SiteEmissionsComponent,
  SiteEmissionsPercentagePipe,
  ReviewGroupStatusPipe,
  FallbackDescriptionSummaryTemplateComponent,
  TransferredCO2DescriptionSummaryTemplateComponent,
  TemperatureSummaryTemplateComponent,
  AccountingSummaryTemplateComponent,
  TransferredCO2InstallationsSummaryTemplateComponent,
  Tier2EmissionFactorSummaryDetailsComponent,
  MeasurementDeviceOrMethodNamePipe,
  MeasurementDeviceOrMethodPipe,
  MeteringUncertaintyNamePipe,
  CalculationDescriptionSummaryTemplateComponent,
  CalculationPlanSummaryDetailsComponent,
  SourceStreamHelpComponent,
  DecisionFilesPipe,
  UncertaintyAnalysisSummaryDetailsComponent,
  ManagementProceduresExistTemplateComponent,
];

@NgModule({
  declarations: declarations,
  exports: declarations,
  imports: [RouterModule, SharedModule],
  providers: [CategoryTypeNamePipe, FindSourceStreamPipe, MeasurementDevicesTypePipe],
})
export class SharedPermitModule {}

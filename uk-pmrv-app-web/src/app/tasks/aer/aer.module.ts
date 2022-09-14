import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';
import { AbbreviationsComponent } from '@tasks/aer/submit/abbreviations/abbreviations.component';
import { SummaryComponent as AbbreviationsSummaryComponent } from '@tasks/aer/submit/abbreviations/summary/summary.component';
import { ApproachesComponent } from '@tasks/aer/submit/approaches/approaches.component';
import { ApproachesAddComponent } from '@tasks/aer/submit/approaches/approaches-add/approaches-add.component';
import { ApproachesDeleteComponent } from '@tasks/aer/submit/approaches/approaches-delete/approaches-delete.component';
import { ApproachesHelpComponent } from '@tasks/aer/submit/approaches/approaches-help/approaches-help.component';
import { SourceStreamHelpComponent } from '@tasks/aer/submit/approaches/approaches-help/source-stream-help.component';
import { ConfidentialityStatementComponent } from '@tasks/aer/submit/confidentiality-statement/confidentiality-statement.component';
import { ConfidentialityStatementSummaryComponent } from '@tasks/aer/submit/confidentiality-statement/confidentiality-statement-summary/confidentiality-statement-summary.component';
import { ConfidentialityStatementSummaryTemplateComponent } from '@tasks/aer/submit/confidentiality-statement/confidentiality-statement-summary/confidentiality-statement-summary-template.component';
import { EmissionPointDeleteComponent } from '@tasks/aer/submit/emission-points/emission-point-delete/emission-point-delete.component';
import { EmissionPointDetailsComponent } from '@tasks/aer/submit/emission-points/emission-point-details/emission-point-details.component';
import { EmissionPointsComponent } from '@tasks/aer/submit/emission-points/emission-points.component';
import { EmissionSourceDeleteComponent } from '@tasks/aer/submit/emission-sources/emission-source-delete/emission-source-delete.component';
import { EmissionSourceDetailsComponent } from '@tasks/aer/submit/emission-sources/emission-source-details/emission-source-details.component';
import { EmissionSourcesComponent } from '@tasks/aer/submit/emission-sources/emission-sources.component';
import { NaceCodeDeleteComponent } from '@tasks/aer/submit/nace-codes/nace-code-delete/nace-code-delete.component';
import { NaceCodesComponent } from '@tasks/aer/submit/nace-codes/nace-codes.component';
import { NaceCodeInstallationActivityComponent } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-installation-activity/nace-code-installation-activity.component';
import { NaceCodeMainActivityComponent } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-main-activity/nace-code-main-activity.component';
import { NaceCodeSubCategoryComponent } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-sub-category/nace-code-sub-category.component';
import { ActivityComponent } from '@tasks/aer/submit/prtr/activity/activity.component';
import { DeleteComponent as PrtrDeleteComponent } from '@tasks/aer/submit/prtr/activity/delete/delete.component';
import { ActivityItemNamePipe } from '@tasks/aer/submit/prtr/activity-item-name.pipe';
import { PrtrComponent } from '@tasks/aer/submit/prtr/prtr.component';
import { SummaryComponent as PrtrSummaryComponent } from '@tasks/aer/submit/prtr/summary/summary.component';
import { CapacityComponent } from '@tasks/aer/submit/regulated-activities/add/capacity/capacity.component';
import { CrfCodesComponent } from '@tasks/aer/submit/regulated-activities/add/crf-codes/crf-codes.component';
import { EnergyCrfCodeComponent } from '@tasks/aer/submit/regulated-activities/add/energy-crf-code/energy-crf-code.component';
import { IndustrialCrfCodeComponent } from '@tasks/aer/submit/regulated-activities/add/industrial-crf-code/industrial-crf-code.component';
import { RegulatedActivityComponent } from '@tasks/aer/submit/regulated-activities/add/regulated-activity.component';
import { RegulatedActivityDeleteComponent } from '@tasks/aer/submit/regulated-activities/delete/regulated-activity-delete.component';
import { RegulatedActivitiesComponent } from '@tasks/aer/submit/regulated-activities/regulated-activities.component';
import { RegulatedActivitiesSortPipe } from '@tasks/aer/submit/regulated-activities/regulated-activities-sort.pipe';
import { AerTaskComponent } from '@tasks/aer/submit/shared/components/aer-task/aer-task.component';
import { ReturnLinkComponent } from '@tasks/aer/submit/shared/components/return-link/return-link.component';
import { TaskPipe } from '@tasks/aer/submit/shared/pipes/task.pipe';
import { TaskStatusPipe } from '@tasks/aer/submit/shared/pipes/task-status.pipe';
import { SourceStreamDeleteComponent } from '@tasks/aer/submit/source-streams/source-stream-delete/source-stream-delete.component';
import { SourceStreamDetailsComponent } from '@tasks/aer/submit/source-streams/source-stream-details/source-streams-details.component';
import { SourceStreamsComponent } from '@tasks/aer/submit/source-streams/source-streams.component';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';

import { AerRoutingModule } from './aer-routing.module';
import { AdditionalDocumentsComponent } from './submit/additional-documents/additional-documents.component';
import { SummaryComponent } from './submit/additional-documents/summary/summary.component';
import { InstallationDetailsComponent } from './submit/installation-details/installation-details.component';
import { SubmitContainerComponent } from './submit/submit-container.component';

@NgModule({
  declarations: [
    AbbreviationsComponent,
    AbbreviationsSummaryComponent,
    ActivityComponent,
    ActivityItemNamePipe,
    AdditionalDocumentsComponent,
    AerTaskComponent,
    ApproachesAddComponent,
    ApproachesComponent,
    ApproachesDeleteComponent,
    ApproachesHelpComponent,
    CapacityComponent,
    ConfidentialityStatementComponent,
    ConfidentialityStatementSummaryComponent,
    ConfidentialityStatementSummaryTemplateComponent,
    CrfCodesComponent,
    EmissionPointDeleteComponent,
    EmissionPointDetailsComponent,
    EmissionPointsComponent,
    EmissionSourceDeleteComponent,
    EmissionSourceDetailsComponent,
    EmissionSourcesComponent,
    EnergyCrfCodeComponent,
    IndustrialCrfCodeComponent,
    InstallationDetailsComponent,
    NaceCodeDeleteComponent,
    NaceCodeInstallationActivityComponent,
    NaceCodeMainActivityComponent,
    NaceCodesComponent,
    NaceCodeSubCategoryComponent,
    PrtrComponent,
    PrtrDeleteComponent,
    PrtrSummaryComponent,
    RegulatedActivitiesComponent,
    RegulatedActivitiesSortPipe,
    RegulatedActivityComponent,
    RegulatedActivityDeleteComponent,
    ReturnLinkComponent,
    SourceStreamDeleteComponent,
    SourceStreamDetailsComponent,
    SourceStreamHelpComponent,
    SourceStreamsComponent,
    SubmitContainerComponent,
    SummaryComponent,
    TaskPipe,
    TaskStatusPipe,
  ],
  imports: [AerRoutingModule, SharedModule, TaskSharedModule],
})
export class AerModule {}

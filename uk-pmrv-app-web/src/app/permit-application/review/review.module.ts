import { NgModule } from '@angular/core';

import { SharedModule } from '../../shared/shared.module';
import { SharedPermitModule } from '../shared/shared-permit.module';
import { AdditionalInfoComponent } from './additional-info/additional-info.component';
import { CalculationComponent } from './calculation/calculation.component';
import { ConfidentialityComponent } from './confidentiality/confidentiality.component';
import { DecisionSummaryComponent } from './decision-summary/decision-summary.component';
import { DetailsComponent } from './details/details.component';
import { ActivationDateComponent as DeterminationActivationDateComponent } from './determination/activation-date/activation-date.component';
import { AnswersComponent as DeterminationAnswersComponent } from './determination/answers/answers.component';
import { DeterminationComponent } from './determination/determination.component';
import { EmissionsComponent as DeterminationAnnualEmissionsComponent } from './determination/emissions/emissions.component';
import { OfficialNoticeComponent as DeterminationOfficialNoticeComponent } from './determination/official-notice/official-notice.component';
import { ReasonComponent as DeterminationReasonComponent } from './determination/reason/reason.component';
import { SummaryComponent as DeterminationSummaryComponent } from './determination/summary/summary.component';
import { SummaryDetailsComponent as DeterminationSummaryDetailsComponent } from './determination/summary/summary-details.component';
import { FallbackComponent } from './fallback/fallback.component';
import { FuelsComponent } from './fuels/fuels.component';
import { InherentCO2Component } from './inherent-co2/inherent-co2.component';
import { ManagementProceduresComponent } from './management-procedures/management-procedures.component';
import { MeasurementComponent } from './measurement/measurement.component';
import { MonitoringApproachesComponent } from './monitoring-approaches/monitoring-approaches.component';
import { MonitoringMethodologyPlanComponent } from './monitoring-methodology-plan/monitoring-methodology-plan.component';
import { NitrousOxideComponent } from './nitrous-oxide/nitrous-oxide.component';
import { NotifyOperatorComponent } from './notify-operator/notify-operator.component';
import { PermitTypeComponent } from './permit-type/permit-type.component';
import { PfcComponent } from './pfc/pfc.component';
import { ConfirmationComponent as RecallConfirmationComponent } from './recall/confirmation/confirmation.component';
import { RecallComponent } from './recall/recall.component';
import { ConfirmationComponent as ReturnForAmendsConfirmationComponent } from './return-for-amends/confirmation/confirmation.component';
import { ReturnForAmendsComponent } from './return-for-amends/return-for-amends.component';
import { ReviewComponent } from './review.component';
import { ReviewRoutingModule } from './review-routing.module';
import { TransferredCO2Component } from './transferred-co2/transferred-co2.component';
import { UncertaintyAnalysisComponent } from './uncertainty-analysis/uncertainty-analysis.component';

@NgModule({
  declarations: [
    AdditionalInfoComponent,
    CalculationComponent,
    ConfidentialityComponent,
    DecisionSummaryComponent,
    DetailsComponent,
    DeterminationActivationDateComponent,
    DeterminationAnnualEmissionsComponent,
    DeterminationAnswersComponent,
    DeterminationComponent,
    DeterminationOfficialNoticeComponent,
    DeterminationReasonComponent,
    DeterminationSummaryComponent,
    DeterminationSummaryDetailsComponent,
    FallbackComponent,
    FuelsComponent,
    InherentCO2Component,
    ManagementProceduresComponent,
    MeasurementComponent,
    MonitoringApproachesComponent,
    MonitoringMethodologyPlanComponent,
    NitrousOxideComponent,
    NotifyOperatorComponent,
    PermitTypeComponent,
    PfcComponent,
    RecallComponent,
    RecallConfirmationComponent,
    ReturnForAmendsComponent,
    ReturnForAmendsConfirmationComponent,
    ReviewComponent,
    TransferredCO2Component,
    UncertaintyAnalysisComponent,
  ],
  imports: [ReviewRoutingModule, SharedModule, SharedPermitModule],
})
export class ReviewModule {}

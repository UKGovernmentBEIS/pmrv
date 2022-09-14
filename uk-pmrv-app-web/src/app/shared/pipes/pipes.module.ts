import { NgModule } from '@angular/core';

import { DeterminationAssessmentPipe } from '@shared/components/peer-review-decision/determination-assessment.pipe';
import { NotificationTypePipe } from '@shared/components/permit-notification/pipes/notification-type.pipe';
import { ReportingTypePipe } from '@shared/components/permit-notification/pipes/reporting-type.pipe';
import { ApproachDescriptionPipe } from '@shared/pipes/approach-description.pipe';
import { CapacityUnitPipe } from '@shared/pipes/capacity-unit.pipe';
import { CompetentAuthorityLocationPipe } from '@shared/pipes/competent-authority-location.pipe';
import { CoordinatePipe } from '@shared/pipes/coordinate.pipe';
import { DaysRemainingPipe } from '@shared/pipes/days-remaining.pipe';
import { DefaultIfEmptyPipe } from '@shared/pipes/default-if-empty.pipe';
import { DeterminationTypePipe } from '@shared/pipes/determination-type.pipe';
import { EtsSchemePipe } from '@shared/pipes/ets-scheme/ets-scheme.pipe';
import { GasPipe } from '@shared/pipes/gas.pipe';
import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { IncludesPipe } from '@shared/pipes/include.pipe';
import { IncludesAnyPipe } from '@shared/pipes/includes-any.pipe';
import { ItemActionHeaderPipe } from '@shared/pipes/item-action-header.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { LegalEntityTypePipe } from '@shared/pipes/legal-entity-type.pipe';
import { PhoneNumberPipe } from '@shared/pipes/phone-number.pipe';
import { RegulatedActivityTypePipe } from '@shared/pipes/regulated-activity-type.pipe';
import { ReviewGroupDecisionPipe } from '@shared/pipes/review-group-decision.pipe';
import { SecondsToMinutesPipe } from '@shared/pipes/seconds-to-minutes.pipe';
import { SourceStreamDescriptionPipe } from '@shared/pipes/source-streams-description.pipe';
import { SourceStreamTypePipe } from '@shared/pipes/source-streams-type.pipe';
import { TagColorPipe } from '@shared/pipes/tag-color.pipe';
import { TemplateFilePipe } from '@shared/pipes/template-file.pipe';
import { TimelineItemLinkPipe } from '@shared/pipes/timeline-item-link.pipe';
import { UserContactPipe } from '@shared/pipes/user-contact.pipe';
import { UserFullNamePipe } from '@shared/pipes/user-full-name.pipe';
import { UserInfoResolverPipe } from '@shared/pipes/user-info-resolver.pipe';
import { UserRolePipe } from '@shared/pipes/user-role.pipe';

import { InstallationCategoryPipe } from '../../permit-application/shared/pipes/installation-category.pipe';
import { AccountStatusPipe } from './account-status.pipe';
import { AccountTypePipe } from './account-type.pipe';
import { CapitalizeFirstPipe } from './capitalize-first.pipe';
import { CompetentAuthorityPipe } from './competent-authority.pipe';
import { CountryPipe } from './country.pipe';
import { ItemActionTypePipe } from './item-action-type.pipe';
import { ItemLinkPipe } from './item-link.pipe';
import { WorkflowStatusPipe } from './workflow-status.pipe';
import { WorkflowTypePipe } from './workflow-type.pipe';

@NgModule({
  imports: [],
  declarations: [
    AccountStatusPipe,
    AccountTypePipe,
    ApproachDescriptionPipe,
    CapacityUnitPipe,
    CapitalizeFirstPipe,
    CompetentAuthorityLocationPipe,
    CompetentAuthorityPipe,
    CoordinatePipe,
    CountryPipe,
    DaysRemainingPipe,
    DefaultIfEmptyPipe,
    DeterminationAssessmentPipe,
    DeterminationTypePipe,
    EtsSchemePipe,
    GasPipe,
    GovukDatePipe,
    IncludesAnyPipe,
    IncludesPipe,
    InstallationCategoryPipe,
    ItemActionHeaderPipe,
    ItemActionTypePipe,
    ItemLinkPipe,
    ItemNamePipe,
    LegalEntityTypePipe,
    NotificationTypePipe,
    PhoneNumberPipe,
    RegulatedActivityTypePipe,
    ReportingTypePipe,
    ReviewGroupDecisionPipe,
    SecondsToMinutesPipe,
    SourceStreamDescriptionPipe,
    SourceStreamTypePipe,
    TagColorPipe,
    TemplateFilePipe,
    TimelineItemLinkPipe,
    UserContactPipe,
    UserFullNamePipe,
    UserInfoResolverPipe,
    UserRolePipe,
    WorkflowStatusPipe,
    WorkflowTypePipe,
  ],
  exports: [
    AccountStatusPipe,
    AccountTypePipe,
    ApproachDescriptionPipe,
    CapacityUnitPipe,
    CapitalizeFirstPipe,
    CompetentAuthorityLocationPipe,
    CompetentAuthorityPipe,
    CoordinatePipe,
    CountryPipe,
    DaysRemainingPipe,
    DefaultIfEmptyPipe,
    DeterminationAssessmentPipe,
    DeterminationTypePipe,
    EtsSchemePipe,
    GasPipe,
    GovukDatePipe,
    IncludesAnyPipe,
    IncludesPipe,
    InstallationCategoryPipe,
    ItemActionHeaderPipe,
    ItemActionTypePipe,
    ItemLinkPipe,
    ItemNamePipe,
    LegalEntityTypePipe,
    NotificationTypePipe,
    PhoneNumberPipe,
    RegulatedActivityTypePipe,
    ReportingTypePipe,
    ReviewGroupDecisionPipe,
    SecondsToMinutesPipe,
    SourceStreamDescriptionPipe,
    SourceStreamTypePipe,
    TagColorPipe,
    TemplateFilePipe,
    TimelineItemLinkPipe,
    UserContactPipe,
    UserFullNamePipe,
    UserInfoResolverPipe,
    UserRolePipe,
    WorkflowStatusPipe,
    WorkflowTypePipe,
  ],
})
export class PipesModule {}

import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { AbbreviationsSummaryTemplateComponent } from '@shared/components/abbreviations/abbreviations-summary-template.component';
import { AbbreviationsTemplateComponent } from '@shared/components/abbreviations/abbreviations-template.component';
import { ApproachesAddTemplateComponent } from '@shared/components/approaches/approaches-add/approaches-add-template.component';
import { ApproachesDeleteTemplateComponent } from '@shared/components/approaches/approaches-delete/approaches-delete-template.component';
import { ApproachesHelpTemplateComponent } from '@shared/components/approaches/approaches-help/approaches-help-template.component';
import { SourceStreamHelpTemplateComponent } from '@shared/components/approaches/approaches-help/source-stream-help-template.component';
import { ApproachesTemplateComponent } from '@shared/components/approaches/approaches-template.component';
import { ConfidentialityStatementTemplateComponent } from '@shared/components/confidentiality-statement/confidentiality-statement-template.component';
import { EmissionPointDeleteTemplateComponent } from '@shared/components/emission-points/emission-point-delete/emission-point-delete-template.component';
import { EmissionPointDetailsTemplateComponent } from '@shared/components/emission-points/emission-point-details/emission-point-details-template.component';
import { EmissionSourceDeleteTemplateComponent } from '@shared/components/emission-sources/emission-source-delete/emission-source-delete-template.component';
import { EmissionSourceDetailsTemplateComponent } from '@shared/components/emission-sources/emission-source-detail/emission-source-details-template.component';
import { PaymentNotCompletedComponent } from '@shared/components/payment-not-completed/payment-not-completed.component';
import { RelatedActionsComponent } from "@shared/components/related-actions/related-actions.component";
import { SelectOtherComponent } from '@shared/components/select-other/select-other.component';
import { SourceStreamDeleteTemplateComponent } from '@shared/components/source-streams/source-stream-delete/source-stream-delete-template.component';
import { SourceStreamDetailsTemplateComponent } from '@shared/components/source-streams/source-stream-details/source-streams-details-template.component';
import { TaskHeaderInfoComponent } from "@shared/components/task-header-info/task-header-info.component";
import { PipesModule } from '@shared/pipes/pipes.module';
import { SourceStreamDescriptionPipe } from '@shared/pipes/source-streams-description.pipe';
import { SubmitComponent } from '@shared/submit/submit.component';
import { MarkdownModule } from 'ngx-markdown';

import { GovukComponentsModule } from 'govuk-components';

import { AddAnotherDirective } from './add-another/add-another.directive';
import { AddressInputComponent } from './address-input/address-input.component';
import { AssignmentConfirmationComponent } from './assignment-confirmation/assignment-confirmation.component';
import { AttachmentComponent } from './attachment/attachment.component';
import { BackLinkComponent } from './back-link/back-link.component';
import { BackToTopComponent } from './back-to-top/back-to-top.component';
import { BooleanRadioGroupComponent } from './boolean-radio-group/boolean-radio-group.component';
import { BreadcrumbsComponent } from './breadcrumbs/breadcrumbs.component';
import { AdditionalDocumentsSharedComponent } from './components/additional-documents/additional-documents.component';
import { AdditionalDocumentsSummaryTemplateComponent } from './components/additional-documents/additional-documents-summary/documents-summary-template.component';
import { ArchiveComponent } from './components/archive/archive.component';
import { AssignmentComponent } from './components/assignment/assignment.component';
import { ConfirmationComponent } from './components/confirmation/confirmation.component';
import { DecisionComponent } from './components/decision/decision.component';
import { DecisionConfirmationComponent } from './components/decision/decision-confirmation/decision-confirmation.component';
import { InstallationDetailsSummaryComponent } from './components/installation-details/installation-details-summary.component';
import { NotifyOperatorComponent } from './components/notify-operator/notify-operator.component';
import { PeerReviewComponent } from './components/peer-review/peer-review.component';
import { AnswersComponent as PeerReviewDecisionAnswersComponent } from './components/peer-review-decision/answers/answers.component';
import { ConfirmationComponent as PeerReviewDecisionConfirmationComponent } from './components/peer-review-decision/confirmation/confirmation.component';
import { PeerReviewDecisionComponent } from './components/peer-review-decision/peer-review-decision.component';
import { PeerReviewSubmittedComponent } from './components/peer-review-decision/timeline/peer-review-submitted.component';
import { RelatedContentComponent } from './components/related-content/related-content.component';
import { RelatedTasksComponent } from './components/related-tasks/related-tasks.component';
import { SummaryDownloadFilesComponent } from './components/summary-download-files/summary-download-files.component';
import { TimelineComponent } from './components/timeline/timeline.component';
import { TimelineItemComponent } from './components/timeline/timeline-item.component';
import { AsyncValidationFieldDirective } from './directives/async-validation-field.directive';
import { CountriesDirective } from './directives/countries.directive';
import { DropdownButtonComponent } from './dropdown-button/dropdown-button.component';
import { DropdownButtonItemComponent } from './dropdown-button/item/dropdown-button-item.component';
import { ErrorPageComponent } from './error-page/error-page.component';
import { FileDownloadComponent } from './file-download/file-download.component';
import { FileInputComponent } from './file-input/file-input.component';
import { FileUploadListComponent } from './file-upload-list/file-upload-list.component';
import { GroupedSummaryListDirective } from './grouped-summary-list/grouped-summary-list.directive';
import { IdentityBarComponent } from './identity-bar/identity-bar.component';
import { IncorporateHeaderComponent } from './incorporate-header/incorporate-header.component';
import { ConvertLinksDirective } from './markdown/convert-links.directive';
import { RouterLinkComponent } from './markdown/router-link.component';
import { MultiSelectComponent } from './multi-select/multi-select.component';
import { MultiSelectItemComponent } from './multi-select/multi-select-item/multi-select-item.component';
import { MultipleFileInputComponent } from './multiple-file-input/multiple-file-input.component';
import { NavigationComponent } from './navigation/navigation.component';
import { NavigationLinkDirective } from './navigation/navigation-link.directive';
import { PageHeadingComponent } from './page-heading/page-heading.component';
import { PaginationComponent } from './pagination/pagination.component';
import { PendingButtonDirective } from './pending-button.directive';
import { PhaseBarComponent } from './phase-bar/phase-bar.component';
import { PhoneInputComponent } from './phone-input/phone-input.component';
import { RadioOptionComponent } from './radio-option/radio-option.component';
import { SkipLinkFocusDirective } from './skip-link-focus.directive';
import { SummaryHeaderComponent } from './summary-header/summary-header.component';
import { TaskItemComponent } from './task-list/task-item/task-item.component';
import { TaskItemListComponent } from './task-list/task-item-list/task-item-list.component';
import { TaskListComponent } from './task-list/task-list.component';
import { TaskSectionComponent } from './task-list/task-section/task-section.component';
import { UserInputComponent } from './user-input/user-input.component';
import { WizardStepComponent } from './wizard/wizard-step.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    GovukComponentsModule,
    PipesModule,
    ReactiveFormsModule,
    MarkdownModule.forChild(),
    RouterModule,
  ],
  declarations: [
    AbbreviationsSummaryTemplateComponent,
    AbbreviationsTemplateComponent,
    AddAnotherDirective,
    AdditionalDocumentsSharedComponent,
    AdditionalDocumentsSummaryTemplateComponent,
    AddressInputComponent,
    ApproachesAddTemplateComponent,
    ApproachesDeleteTemplateComponent,
    ApproachesHelpTemplateComponent,
    ApproachesTemplateComponent,
    ArchiveComponent,
    AssignmentComponent,
    AssignmentConfirmationComponent,
    AsyncValidationFieldDirective,
    AttachmentComponent,
    BackLinkComponent,
    BackToTopComponent,
    BooleanRadioGroupComponent,
    BreadcrumbsComponent,
    ConfidentialityStatementTemplateComponent,
    ConfirmationComponent,
    ConvertLinksDirective,
    CountriesDirective,
    DecisionComponent,
    DecisionConfirmationComponent,
    DropdownButtonComponent,
    DropdownButtonItemComponent,
    EmissionPointDeleteTemplateComponent,
    EmissionPointDetailsTemplateComponent,
    EmissionSourceDeleteTemplateComponent,
    EmissionSourceDetailsTemplateComponent,
    ErrorPageComponent,
    FileDownloadComponent,
    FileInputComponent,
    FileUploadListComponent,
    GroupedSummaryListDirective,
    IdentityBarComponent,
    IncorporateHeaderComponent,
    InstallationDetailsSummaryComponent,
    MultipleFileInputComponent,
    MultiSelectComponent,
    MultiSelectItemComponent,
    NavigationComponent,
    NavigationLinkDirective,
    NotifyOperatorComponent,
    PageHeadingComponent,
    PaginationComponent,
    PaymentNotCompletedComponent,
    PeerReviewComponent,
    PeerReviewDecisionAnswersComponent,
    PeerReviewDecisionComponent,
    PeerReviewDecisionConfirmationComponent,
    PeerReviewSubmittedComponent,
    PendingButtonDirective,
    PhaseBarComponent,
    PhoneInputComponent,
    RadioOptionComponent,
    RelatedActionsComponent,
    RelatedContentComponent,
    RelatedTasksComponent,
    RouterLinkComponent,
    SelectOtherComponent,
    SkipLinkFocusDirective,
    SourceStreamDeleteTemplateComponent,
    SourceStreamDetailsTemplateComponent,
    SourceStreamHelpTemplateComponent,
    SubmitComponent,
    SummaryDownloadFilesComponent,
    SummaryHeaderComponent,
    TaskHeaderInfoComponent,
    TaskItemComponent,
    TaskItemListComponent,
    TaskListComponent,
    TaskSectionComponent,
    TimelineComponent,
    TimelineItemComponent,
    UserInputComponent,
    WizardStepComponent,
  ],
  exports: [
    AbbreviationsSummaryTemplateComponent,
    AbbreviationsTemplateComponent,
    AddAnotherDirective,
    AdditionalDocumentsSharedComponent,
    AdditionalDocumentsSummaryTemplateComponent,
    AddressInputComponent,
    ApproachesAddTemplateComponent,
    ApproachesDeleteTemplateComponent,
    ApproachesHelpTemplateComponent,
    ApproachesTemplateComponent,
    ArchiveComponent,
    AssignmentComponent,
    AssignmentConfirmationComponent,
    AsyncValidationFieldDirective,
    AttachmentComponent,
    BackLinkComponent,
    BackToTopComponent,
    BooleanRadioGroupComponent,
    BreadcrumbsComponent,
    CommonModule,
    ConfidentialityStatementTemplateComponent,
    ConfirmationComponent,
    ConvertLinksDirective,
    CountriesDirective,
    DecisionComponent,
    DecisionConfirmationComponent,
    DropdownButtonComponent,
    DropdownButtonItemComponent,
    EmissionPointDeleteTemplateComponent,
    EmissionPointDetailsTemplateComponent,
    EmissionSourceDeleteTemplateComponent,
    EmissionSourceDetailsTemplateComponent,
    ErrorPageComponent,
    FileDownloadComponent,
    FileInputComponent,
    GovukComponentsModule,
    GroupedSummaryListDirective,
    IdentityBarComponent,
    IncorporateHeaderComponent,
    InstallationDetailsSummaryComponent,
    MultipleFileInputComponent,
    MultiSelectComponent,
    MultiSelectItemComponent,
    NavigationComponent,
    NavigationLinkDirective,
    NotifyOperatorComponent,
    PageHeadingComponent,
    PaginationComponent,
    PaymentNotCompletedComponent,
    PeerReviewComponent,
    PeerReviewDecisionAnswersComponent,
    PeerReviewDecisionComponent,
    PeerReviewDecisionConfirmationComponent,
    PeerReviewSubmittedComponent,
    PendingButtonDirective,
    PhaseBarComponent,
    PhoneInputComponent,
    PipesModule,
    RadioOptionComponent,
    ReactiveFormsModule,
    RelatedActionsComponent,
    RelatedContentComponent,
    RelatedTasksComponent,
    SelectOtherComponent,
    SkipLinkFocusDirective,
    SourceStreamDeleteTemplateComponent,
    SourceStreamDetailsTemplateComponent,
    SourceStreamHelpTemplateComponent,
    SubmitComponent,
    SummaryDownloadFilesComponent,
    SummaryHeaderComponent,
    TaskHeaderInfoComponent,
    TaskItemComponent,
    TaskItemListComponent,
    TaskListComponent,
    TaskSectionComponent,
    TimelineComponent,
    TimelineItemComponent,
    UserInputComponent,
    WizardStepComponent,
  ],
  providers: [SourceStreamDescriptionPipe],
})
export class SharedModule {}

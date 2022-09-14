import { CommonModule } from '@angular/common';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { InlineSVGModule } from 'ng-inline-svg';

import { AccordionComponent, AccordionItemComponent, AccordionItemSummaryDirective } from './accordion';
import { BackLinkComponent } from './back-link/back-link.component';
import { BreadcrumbsComponent } from './breadcrumbs/breadcrumbs.component';
import { CheckboxComponent, CheckboxesComponent } from './checkboxes';
import { DateInputComponent } from './date-input/date-input.component';
import { DetailsComponent } from './details/details.component';
import {
  ButtonDirective,
  ConditionalContentDirective,
  DebounceClickDirective,
  FormErrorDirective,
  InsetTextDirective,
  LabelDirective,
  LinkDirective,
} from './directives';
import { ErrorMessageComponent } from './error-message/error-message.component';
import { ErrorSummaryComponent } from './error-summary/error-summary.component';
import { FieldsetDirective, FieldsetHintDirective, LegendDirective } from './fieldset';
import { FileUploadComponent } from './file-upload/file-upload.component';
import { FooterComponent, FooterNavListComponent, MetaInfoComponent } from './footer';
import { FormBuilderService } from './form/form-builder.service';
import { HeaderActionsListComponent, HeaderComponent, HeaderNavListComponent } from './header';
import { NotificationBannerComponent } from './notification-banner/notification-banner.component';
import { PanelComponent } from './panel/panel.component';
import { PhaseBannerComponent } from './phase-banner/phase-banner.component';
import { RadioComponent, RadioOptionComponent } from './radio';
import { SelectComponent } from './select';
import { SkipLinkComponent } from './skip-link/skip-link.component';
import {
  SummaryListComponent,
  SummaryListRowActionsDirective,
  SummaryListRowDirective,
  SummaryListRowKeyDirective,
  SummaryListRowValueDirective,
} from './summary-list';
import { TableComponent } from './table';
import { TabDirective, TabLazyDirective, TabsComponent } from './tabs';
import { TagComponent } from './tag/tag.component';
import { TextInputComponent } from './text-input/text-input.component';
import { TextareaComponent } from './textarea/textarea.component';
import { WarningTextComponent } from './warning-text/warning-text.component';

@NgModule({
  declarations: [
    AccordionComponent,
    AccordionItemComponent,
    AccordionItemSummaryDirective,
    BackLinkComponent,
    BreadcrumbsComponent,
    ButtonDirective,
    CheckboxComponent,
    CheckboxesComponent,
    ConditionalContentDirective,
    DateInputComponent,
    DebounceClickDirective,
    DetailsComponent,
    ErrorMessageComponent,
    ErrorSummaryComponent,
    FieldsetDirective,
    FieldsetHintDirective,
    FileUploadComponent,
    FooterComponent,
    FooterNavListComponent,
    FormErrorDirective,
    HeaderActionsListComponent,
    HeaderComponent,
    HeaderNavListComponent,
    InsetTextDirective,
    LabelDirective,
    LegendDirective,
    LinkDirective,
    MetaInfoComponent,
    NotificationBannerComponent,
    PanelComponent,
    PhaseBannerComponent,
    RadioComponent,
    RadioOptionComponent,
    SelectComponent,
    SkipLinkComponent,
    SummaryListComponent,
    SummaryListRowActionsDirective,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    TabDirective,
    TabLazyDirective,
    TableComponent,
    TabsComponent,
    TagComponent,
    TextareaComponent,
    TextInputComponent,
    WarningTextComponent,
  ],
  imports: [CommonModule, HttpClientModule, InlineSVGModule.forRoot(), ReactiveFormsModule, RouterModule],
  exports: [
    AccordionComponent,
    AccordionItemComponent,
    AccordionItemSummaryDirective,
    BackLinkComponent,
    BreadcrumbsComponent,
    ButtonDirective,
    CheckboxComponent,
    CheckboxesComponent,
    ConditionalContentDirective,
    DateInputComponent,
    DebounceClickDirective,
    DetailsComponent,
    ErrorMessageComponent,
    ErrorSummaryComponent,
    FieldsetDirective,
    FieldsetHintDirective,
    FileUploadComponent,
    FooterComponent,
    FooterNavListComponent,
    FormErrorDirective,
    HeaderActionsListComponent,
    HeaderComponent,
    HeaderNavListComponent,
    InsetTextDirective,
    LabelDirective,
    LegendDirective,
    LinkDirective,
    MetaInfoComponent,
    NotificationBannerComponent,
    PanelComponent,
    PhaseBannerComponent,
    RadioComponent,
    RadioOptionComponent,
    SelectComponent,
    SkipLinkComponent,
    SummaryListComponent,
    SummaryListRowActionsDirective,
    SummaryListRowDirective,
    SummaryListRowKeyDirective,
    SummaryListRowValueDirective,
    TabDirective,
    TabLazyDirective,
    TableComponent,
    TabsComponent,
    TagComponent,
    TextareaComponent,
    TextInputComponent,
    WarningTextComponent,
  ],
  providers: [{ provide: FormBuilder, useClass: FormBuilderService }],
})
export class GovukComponentsModule {}

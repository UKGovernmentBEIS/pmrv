import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { SummaryItem } from 'govuk-components';

@Component({
  selector: 'app-abbreviations-summary-template',
  template: `
    <dl
      *ngFor="let abbreviation of abbreviations"
      [details]="abbreviation"
      [hasBottomBorder]="hasBottomBorder"
      appGroupedSummaryList
      govuk-summary-list
    ></dl>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AbbreviationsSummaryTemplateComponent {
  @Input() hasBottomBorder = true;
  @Input() abbreviations: SummaryItem[][];
}

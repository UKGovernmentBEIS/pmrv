import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-description-summary-template',
  template: `
    <dl
      *ngIf="'installationDescription' | task | async as task"
      appGroupedSummaryList
      govuk-summary-list
      [class.summary-list--edge-border]="hasBottomBorder"
    >
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>Activities at the installation</dt>
        <dd class="pre-wrap" govukSummaryListRowValue>{{ task.mainActivitiesDesc }}</dd>
      </div>
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>Description of the site</dt>
        <dd class="pre-wrap" govukSummaryListRowValue>{{ task.siteDescription }}</dd>
      </div>
    </dl>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DescriptionSummaryTemplateComponent {
  @Input() hasBottomBorder = true;
}

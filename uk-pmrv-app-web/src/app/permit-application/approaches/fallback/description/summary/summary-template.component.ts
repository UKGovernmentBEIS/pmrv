import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-fallback-description-summary-template',
  template: `
    <ng-container *ngIf="'FALLBACK' | monitoringApproachTask | async as task">
      <dl
        *ngIf="task?.approachDescription || task?.justification"
        govuk-summary-list
        [hasBorders]="false"
        [class.summary-list--edge-border]="hasBottomBorder"
      >
        <div govukSummaryListRow>
          <dt govukSummaryListRowKey>Approach description</dt>
          <dd class="pre-wrap" govukSummaryListRowValue>{{ task?.approachDescription }}</dd>
        </div>
        <div govukSummaryListRow>
          <dt govukSummaryListRowKey>Justification</dt>
          <dd class="pre-wrap" govukSummaryListRowValue>{{ task?.justification }}</dd>
        </div>
      </dl>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryTemplateComponent {
  @Input() hasBottomBorder = true;
}

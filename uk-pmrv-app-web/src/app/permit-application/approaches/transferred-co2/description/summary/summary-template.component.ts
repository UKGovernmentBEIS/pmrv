import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-transferred-co2-description-summary-template',
  template: `
    <ng-container *ngIf="('TRANSFERRED_CO2' | monitoringApproachTask | async).approachDescription">
      <dl govuk-summary-list [hasBorders]="false" [class.summary-list--edge-border]="hasBottomBorder">
        <div govukSummaryListRow>
          <dt govukSummaryListRowKey>Approach description</dt>
          <dd class="pre-wrap" govukSummaryListRowValue>
            {{ ('TRANSFERRED_CO2' | monitoringApproachTask | async).approachDescription }}
          </dd>
        </div>
      </dl>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryTemplateComponent {
  @Input() hasBottomBorder = true;
}

import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-inherent-co2-description-summary-template',
  template: `
    <ng-container *ngIf="('INHERENT_CO2' | monitoringApproachTask | async)?.approachDescription as approachDescription">
      <dl govuk-summary-list [hasBorders]="false" [class.summary-list--edge-border]="hasBottomBorder">
        <div govukSummaryListRow>
          <dt govukSummaryListRowKey>Approach description</dt>
          <dd class="pre-wrap" govukSummaryListRowValue>
            {{ approachDescription }}
          </dd>
        </div>
      </dl>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InherentCO2DescriptionSummaryTemplateComponent {
  @Input() hasBottomBorder = true;
}

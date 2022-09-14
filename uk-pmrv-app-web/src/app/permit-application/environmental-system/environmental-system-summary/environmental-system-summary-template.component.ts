import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-environmental-system-summary-template',
  template: `
    <dl
      *ngIf="'environmentalManagementSystem' | task | async as environmentalSystem"
      govuk-summary-list
      [hasBorders]="false"
      [class.summary-list--edge-border]="hasBottomBorder"
    >
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>Documented environmental management system</dt>
        <dd govukSummaryListRowValue>{{ environmentalSystem.exist ? 'Yes' : 'No' }}</dd>
      </div>
      <div *ngIf="environmentalSystem.exist" govukSummaryListRow>
        <dt govukSummaryListRowKey>Externally certified</dt>
        <dd govukSummaryListRowValue>{{ environmentalSystem.certified ? 'Yes' : 'No' }}</dd>
      </div>
      <div *ngIf="environmentalSystem.exist && environmentalSystem.certified" govukSummaryListRow>
        <dt govukSummaryListRowKey>Certification standard</dt>
        <dd govukSummaryListRowValue>{{ environmentalSystem.certificationStandard }}</dd>
      </div>
    </dl>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EnvironmentalSystemSummaryTemplateComponent {
  @Input() hasBottomBorder = true;
}

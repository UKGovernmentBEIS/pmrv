import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-management-procedures-exist-summary-template',
  template: `
    <ng-container *ngIf="('managementProceduresExist' | task | async) !== undefined">
      <dl govuk-summary-list [hasBorders]="false" [class.summary-list--edge-border]="hasBottomBorder">
        <div govukSummaryListRow>
          <dt govukSummaryListRowKey>Type</dt>
          <dd govukSummaryListRowValue>{{ ('managementProceduresExist' | task | async) ? 'Yes' : 'No' }}</dd>
        </div>
      </dl>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ManagementProceduresExistTemplateComponent {
  @Input() hasBottomBorder = true;
}

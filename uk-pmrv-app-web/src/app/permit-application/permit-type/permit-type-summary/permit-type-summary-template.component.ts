import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-permit-type-summary-template',
  template: `
    <dl
      *ngIf="this.store.permitType as permitType"
      govuk-summary-list
      [hasBorders]="false"
      [class.summary-list--edge-border]="hasBottomBorder"
    >
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>Type</dt>
        <dd govukSummaryListRowValue>{{ permitType }}</dd>
      </div>
    </dl>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PermitTypeSummaryTemplateComponent {
  @Input() hasBottomBorder = true;

  constructor(readonly store: PermitApplicationStore) {}
}

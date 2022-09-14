import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { map, Observable } from 'rxjs';

import { CellAndAnodeType, PFCMonitoringApproach } from 'pmrv-api';

import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Component({
  selector: 'app-pfc-approach-types-summary-template',
  template: `
    <dl
      *ngFor="let cellAnodeType of cellAnodeTypes$ | async"
      govuk-summary-list
      [hasBorders]="false"
      [class.summary-list--edge-border]="hasBottomBorder"
    >
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>Cell type</dt>
        <dd class="pre-wrap" govukSummaryListRowValue>{{ cellAnodeType.cellType }}</dd>
      </div>
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>Anode type</dt>
        <dd class="pre-wrap" govukSummaryListRowValue>{{ cellAnodeType.anodeType }}</dd>
      </div>
    </dl>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryTemplateComponent {
  @Input() hasBottomBorder = true;

  cellAnodeTypes$: Observable<CellAndAnodeType[]> = this.store
    .getTask('monitoringApproaches')
    .pipe(map((task) => (task.PFC as PFCMonitoringApproach).cellAndAnodeTypes));

  constructor(readonly store: PermitApplicationStore) {}
}

import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { GovukTableColumn } from 'govuk-components';

@Component({
  selector: 'app-regulated-activities-summary-template',
  template: `
    <govuk-table
      [columns]="columns"
      [data]="'regulatedActivities' | task | async"
      [class.no-bottom-border]="!hasBottomBorder"
    >
      <ng-template let-column="column" let-row="row">
        <ng-container [ngSwitch]="column.field">
          <ng-container *ngSwitchCase="'activity'">{{ row.type | regulatedActivityType }}</ng-container>
          <ng-container *ngSwitchCase="'capacityAndUnit'"
            >{{ row.capacity | number }} {{ row.capacityUnit | capacityUnit }}</ng-container
          >
          <ng-container *ngSwitchCase="'type'">{{ row.type | gas }}</ng-container>
        </ng-container>
      </ng-template>
    </govuk-table>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegulatedActivitiesSummaryTemplateComponent {
  @Input() hasBottomBorder = true;

  columns: GovukTableColumn<any>[] = [
    { field: 'activity', header: 'Regulated activity', widthClass: 'govuk-!-width-one-third' },
    { field: 'capacityAndUnit', header: 'Capacity', widthClass: 'govuk-!-width-one-third' },
    { field: 'type', header: 'Greenhouse gas', widthClass: 'govuk-!-width-one-third' },
  ];
}

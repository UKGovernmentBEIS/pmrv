import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { filter, map, Observable } from 'rxjs';

import { SummaryItem } from 'govuk-components';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-permits-summary-template',
  template: `
    <dl
      *ngFor="let detail of details$ | async"
      [details]="detail"
      appGroupedSummaryList
      govuk-summary-list
      [hasBottomBorder]="hasBottomBorder"
    ></dl>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PermitsSummaryTemplateComponent {
  @Input() hasBottomBorder = true;

  details$: Observable<SummaryItem[][]> = this.store.getTask('environmentalPermitsAndLicences').pipe(
    filter((task) => !!task),
    map((task) =>
      task.exist
        ? task.envPermitOrLicences.map((permit) => [
            { key: 'Type', value: permit.type },
            { key: 'Number', value: permit.num },
            { key: 'Issuing authority', value: permit.issuingAuthority },
            { key: 'Permit holder', value: permit.permitHolder },
          ])
        : [[{ key: 'Any other environmental permits or licences', value: 'No' }]],
    ),
  );

  constructor(readonly store: PermitApplicationStore) {}
}

import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { filter, map, Observable } from 'rxjs';

import { SummaryItem } from 'govuk-components';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-monitoring-roles-summary-template',
  templateUrl: './monitoring-roles-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MonitoringRolesSummaryTemplateComponent {
  @Input() hasBottomBorder = true;

  task$ = this.store.getTask('monitoringReporting');
  roles$: Observable<SummaryItem[][]> = this.task$.pipe(
    filter((task) => !!task),
    map((task) =>
      task.monitoringRoles.map((permit) => [
        { key: 'Job title', value: permit.jobTitle },
        { key: 'Main duties', value: permit.mainDuties },
      ]),
    ),
  );

  constructor(readonly store: PermitApplicationStore) {}
}

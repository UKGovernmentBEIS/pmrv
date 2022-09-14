import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { map } from 'rxjs';

import { monitoringApproachTypeOptions } from '@shared/components/approaches/approaches-options';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-approaches-summary-template',
  template: `
    <dl govuk-summary-list [class.dl--no-bottom-border]="!hasBottomBorder">
      <div govukSummaryListRow *ngFor="let monitoringApproach of monitoringApproaches$ | async">
        <dt govukSummaryListRowKey>{{ monitoringApproach | monitoringApproachDescription }} approach</dt>
      </div>
    </dl>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApproachesSummaryTemplateComponent {
  @Input() hasBottomBorder = true;

  monitoringApproaches$ = this.store
    .getTask('monitoringApproaches')
    .pipe(
      map((monitoringApproaches) =>
        monitoringApproachTypeOptions.filter((option) => Object.keys(monitoringApproaches ?? {}).includes(option)),
      ),
    );
  constructor(private readonly store: PermitApplicationStore) {}
}

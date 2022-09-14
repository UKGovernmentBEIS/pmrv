import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { CalculationMonitoringApproach } from 'pmrv-api';

import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Component({
  selector: 'app-calculation-plan-summary-details',
  templateUrl: './summary-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryDetailsComponent {
  @Input() changePerStage: boolean;
  @Input() hasBottomBorder = true;

  private samplingPlan$ = this.store
    .getTask('monitoringApproaches')
    .pipe(map((task) => (task.CALCULATION as CalculationMonitoringApproach).samplingPlan));

  constructor(
    readonly store: PermitApplicationStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  planAttachments$ = this.samplingPlan$.pipe(
    map((plan) =>
      plan.exist && plan.details.procedurePlan?.procedurePlanIds ? plan.details.procedurePlan.procedurePlanIds : [],
    ),
  );

  changeClick(wizardStep?: string): void {
    this.router.navigate(['../' + wizardStep], { relativeTo: this.route, state: { changing: true } });
  }
}

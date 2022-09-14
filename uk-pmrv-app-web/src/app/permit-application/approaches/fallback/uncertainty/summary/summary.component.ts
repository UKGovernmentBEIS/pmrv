import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { FallbackMonitoringApproach, ProcedureForm } from 'pmrv-api';

import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  annualUncertaintyAnalysis$: Observable<ProcedureForm> = this.store
    .getTask('monitoringApproaches')
    .pipe(map((task) => (task.FALLBACK as FallbackMonitoringApproach).annualUncertaintyAnalysis));

  constructor(readonly store: PermitApplicationStore, private readonly router: Router) {}
}

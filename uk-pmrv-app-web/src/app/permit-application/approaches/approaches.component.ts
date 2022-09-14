import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { monitoringApproachTypeOptions } from '@shared/components/approaches/approaches-options';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { PendingRequest } from '../../core/interfaces/pending-request.interface';
import { PermitApplicationStore } from '../store/permit-application.store';

@Component({
  selector: 'app-approaches',
  templateUrl: './approaches.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApproachesComponent implements PendingRequest {
  monitoringApproaches$ = this.store
    .getTask('monitoringApproaches')
    .pipe(
      map((monitoringApproaches) =>
        monitoringApproaches !== undefined
          ? monitoringApproachTypeOptions.filter((option) => Object.keys(monitoringApproaches).includes(option))
          : [],
      ),
    );

  isEveryMonitoringApproachDefined$ = this.monitoringApproaches$.pipe(
    map((monitoringApproaches) => monitoringApproachTypeOptions.every((value) => monitoringApproaches.includes(value))),
  );

  constructor(
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
  ) {}

  onSubmit(): void {
    this.store
      .postStatus('monitoringApproaches', true)
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => this.store.navigate(this.route, 'summary', 'monitoring-approaches'));
  }
}

import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap, withLatestFrom } from 'rxjs';

import { ReceivingTransferringInstallation, TransferredCO2MonitoringApproach } from 'pmrv-api';

import { PermitApplicationStore } from '../../../../store/permit-application.store';

@Component({
  selector: 'app-delete',
  templateUrl: './delete.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DeleteComponent {
  installation$ = this.route.paramMap.pipe(
    first(),
    map((paramMap) => Number(paramMap.get('index'))),
    withLatestFrom(this.store.getTask('monitoringApproaches')),
    map(
      ([index, monitoringApproaches]) =>
        (monitoringApproaches.TRANSFERRED_CO2 as TransferredCO2MonitoringApproach).receivingTransferringInstallations[
          index
        ],
    ),
  );

  constructor(
    private readonly store: PermitApplicationStore,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  delete(): void {
    combineLatest([this.route.data, this.route.paramMap])
      .pipe(
        switchMap(([data, paramMap]) =>
          this.store.findTask<ReceivingTransferringInstallation[]>(data.taskKey).pipe(
            first(),
            map((installations) => installations.filter((_, i) => i !== Number(paramMap.get('index')))),
            switchMap((installations) => this.store.postTask(data.taskKey, installations, false, data.statusKey)),
          ),
        ),
      )
      .subscribe(() => this.router.navigate(['../..'], { relativeTo: this.route }));
  }
}
